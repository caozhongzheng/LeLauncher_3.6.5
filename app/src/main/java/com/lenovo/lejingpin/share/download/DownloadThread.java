package com.lenovo.lejingpin.share.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SyncFailedException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import com.lenovo.launcher.R;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.PowerManager;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

/**
 * Runs an actual download
 */
public class DownloadThread extends Thread {

	private Context mContext;
	private BeanDownload mInfo;

	private final static String TAG = "DownloadThread";

	public DownloadThread(Context context, BeanDownload info) {
		// Log.i(TAG," DownloadThread.DownloadThread()");
		mContext = context;
		mInfo = info;
	}

	/**
	 * Returns the user agent provided by the initiating app, or use the default
	 * one
	 */
	private String userAgent() {
		String userAgent = mInfo.mUserAgent;
		if (userAgent == null) {
			userAgent = Constants.DEFAULT_USER_AGENT;
		}
		return userAgent;
	}

	/**
	 * Executes the download in a separate thread
	 */
	public void run() {
		if (mInfo == null)
			return;
		Log.i(TAG, "         DownloadThread.run(), id:" + mInfo.mId
				+ ", name:" + mInfo.mTitle);
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

		int finalStatus = Downloads.Impl.STATUS_UNKNOWN_ERROR;
		boolean countRetry = false;
		int retryAfter = 0;
		int redirectCount = mInfo.mRedirectCount;
		String newUri = null;
		boolean gotData = false;
		String filename = null;
		String mimeType = sanitizeMimeType(mInfo.mMimeType);
		FileOutputStream stream = null;
		AndroidHttpClient client = null;
		PowerManager.WakeLock wakeLock = null;
		Uri contentUri = Uri
				.parse(Downloads.Impl.CONTENT_URI + "/" + mInfo.mId);
		boolean netError = false;
		try {
			boolean continuingDownload = false;
			String headerAcceptRanges = null;
			String headerContentDisposition = null;
			String headerContentLength = null;
			String headerContentLocation = null;
			String headerETag = null;
			String headerTransferEncoding = null;

			byte data[] = new byte[Constants.BUFFER_SIZE];

			int bytesSoFar = 0;

			PowerManager pm = (PowerManager) mContext
					.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					Constants.TAG);
			wakeLock.acquire();

			filename = mInfo.mFileName;
			if (filename != null) {
				if (!Helpers.isFilenameValid(filename)
						&& mInfo.mDestination != Downloads.DESTINATION_USERCHOOSED) {
					finalStatus = Downloads.Impl.STATUS_FILE_ERROR;
					notifyDownloadProcess(finalStatus, false, 0, 0, false,
							filename, null, mInfo.mMimeType);
					return;
				}
				// We're resuming a download that got interrupted
				File f = new File(filename);
				if (f.exists()) {
					long fileLength = f.length();
					if (fileLength == 0) {
						// The download hadn't actually started, we can restart
						// from scratch
						f.delete();
						// filename = null;
					} else if (mInfo.mETag == null && !mInfo.mNoIntegrity) {
						// Tough luck, that's not a resumable download
						Log.d(TAG,
								"DownloadThread.run(), can't resume interrupted non-resumable download");
					} else {
						// All right, we'll be able to resume this download
						stream = new FileOutputStream(filename, true);
						bytesSoFar = (int) fileLength;
						if (mInfo.mTotalBytes != -1) {
							headerContentLength = Integer
									.toString(mInfo.mTotalBytes);
						}
						headerETag = mInfo.mETag;
						continuingDownload = true;
					}
				}
			}
			// zdx modify
			else {
				finalStatus = Downloads.Impl.STATUS_FILE_ERROR;
				notifyDownloadProcess(finalStatus, false, 0, 0, false, null,
						null, mInfo.mMimeType);
				return;
			}

			int bytesNotified = bytesSoFar;
			// starting with MIN_VALUE means that the first write will commit
			// progress to the database
			long timeLastNotification = 0;

			client = AndroidHttpClient.newInstance(userAgent(), mContext);
			if (stream != null
					&& (mInfo.mDestination == Downloads.DESTINATION_USERCHOOSED)
					&& !Constants.MIMETYPE_DRM_MESSAGE
							.equalsIgnoreCase(mimeType)) {
				try {
					stream.close();
					stream = null;
				} catch (IOException ex) {
					Log.v(TAG,
							"DownloadThread.run(), exception when closing the file before download ");
					// nothing can really be done if the file can't be closed
				}
			}
			/*
			 * This loop is run once for every individual HTTP request that gets
			 * sent. The very first HTTP request is a "virgin" request, while
			 * every subsequent request is done with the original ETag and a
			 * byte-range.
			 */
			http_request_loop: while (true) {
				
				// Prepares the request and fires it.
				HttpGet request = new HttpGet(mInfo.mUri);

				if (mInfo.mCookies != null) {
					request.addHeader("Cookie", mInfo.mCookies);
				}
				if (mInfo.mReferer != null) {
					request.addHeader("Referer", mInfo.mReferer);
				}
				if (continuingDownload) {
					if (headerETag != null) {
						request.addHeader("If-Match", headerETag);
					}
					request.addHeader("Range", "bytes=" + bytesSoFar + "-");
				}

				HttpResponse response;
				try {
					response = client.execute(request);
				} catch (IllegalStateException ex) {
					if (mInfo.mNumFailed < Constants.MAX_RETRIES) {
						finalStatus = Downloads.Impl.STATUS_RUNNING_PAUSED;
						countRetry = true;
					}else{
						finalStatus = Downloads.Impl.STATUS_BAD_REQUEST;
					}
					Log.d(TAG,
							"DownloadThread.run()******error******HttpResponse IllegalStateException, status:"
									+ finalStatus);
					request.abort();
					break http_request_loop;
				} catch (IllegalArgumentException ex) {
					finalStatus = Downloads.Impl.STATUS_BAD_REQUEST;
					Log.d(TAG,
							"DownloadThread.run()******error******HttpResponse IllegalArgumentException, Downloads.Impl.STATUS_BAD_REQUEST");
					request.abort();
					break http_request_loop;
				} catch (IOException ex) {
					if (!Helpers.isNetworkAvailable(mContext)) {
						finalStatus = Downloads.Impl.STATUS_RUNNING_PAUSED;
						netError = true;
					} else if (mInfo.mNumFailed < Constants.MAX_RETRIES) {
						finalStatus = Downloads.Impl.STATUS_RUNNING_PAUSED;
						countRetry = true;
					} else {
						finalStatus = Downloads.Impl.STATUS_HTTP_DATA_ERROR;
					}
					Log.d(TAG,
							"DownloadThread.run()******error******HttpResponse IOException, status:"
									+ finalStatus);
					request.abort();
					break http_request_loop;
				}
				int statusCode = response.getStatusLine().getStatusCode();
				Log.v(TAG, "DownloadThread.run(), got HTTP response code :"
						+ statusCode);
				if (statusCode == 503
						&& mInfo.mNumFailed < Constants.MAX_RETRIES) {
					finalStatus = Downloads.Impl.STATUS_RUNNING_PAUSED;
					countRetry = true;
					Header header = response.getFirstHeader("Retry-After");
					if (header != null) {
						try {
							retryAfter = Integer.parseInt(header.getValue());
							if (retryAfter < 0) {
								retryAfter = 0;
							} else {
								if (retryAfter < Constants.MIN_RETRY_AFTER) {
									retryAfter = Constants.MIN_RETRY_AFTER;
								} else if (retryAfter > Constants.MAX_RETRY_AFTER) {
									retryAfter = Constants.MAX_RETRY_AFTER;
								}
								retryAfter += Helpers.sRandom
										.nextInt(Constants.MIN_RETRY_AFTER + 1);
								retryAfter *= 1000;
							}
						} catch (NumberFormatException ex) {
							// ignored - retryAfter stays 0 in this case.
						}
					}
					Log.d(TAG,
							"DownloadThread.run()******error****** retry after, status:"
									+ finalStatus);
					request.abort();
					break http_request_loop;
				}
				if (statusCode == 301 || statusCode == 302 || statusCode == 303
						|| statusCode == 307) {
					if (redirectCount >= Constants.MAX_REDIRECTS) {
						finalStatus = Downloads.Impl.STATUS_TOO_MANY_REDIRECTS;
						Log.d(TAG,
								"DownloadThread.run()******error******too many redires , Downloads.Impl.STATUS_TOO_MANY_REDIRECTS");
						request.abort();
						break http_request_loop;
					}
					Header header = response.getFirstHeader("Location");
					if (header != null) {
						try {
							newUri = new URI(mInfo.mUri).resolve(
									new URI(header.getValue())).toString();
						} catch (URISyntaxException ex) {
							// Log.d(TAG,"DownloadThread.run(), URISyntaxException redirect uri: "
							// + header.getValue() + " , uri:" + mInfo.mUri);
							finalStatus = Downloads.Impl.STATUS_BAD_REQUEST;
							Log.d(TAG,
									"DownloadThread.run()******error******URISyntaxException ,Downloads.Impl.STATUS_BAD_REQUEST");
							request.abort();
							break http_request_loop;
						}
						++redirectCount;
						finalStatus = Downloads.Impl.STATUS_RUNNING_PAUSED;
						Log.d(TAG,
								"DownloadThread.run()************ redirect , Downloads.Impl.STATUS_RUNNING_PAUSED");
						request.abort();
						break http_request_loop;
					}
				}
				if ((!continuingDownload && statusCode != Downloads.Impl.STATUS_SUCCESS)
						|| (continuingDownload && statusCode != 206)) {
					if (Downloads.Impl.isStatusError(statusCode)) {
						finalStatus = statusCode;
					} else if (statusCode >= 300 && statusCode < 400) {
						finalStatus = Downloads.Impl.STATUS_UNHANDLED_REDIRECT;
					} else if (continuingDownload
							&& statusCode == Downloads.Impl.STATUS_SUCCESS) {
						finalStatus = Downloads.Impl.STATUS_PRECONDITION_FAILED;
					} else {
						finalStatus = Downloads.Impl.STATUS_UNHANDLED_HTTP_CODE;
					}
					Log.d(TAG,
							"DownloadThread.run()******error******continuingDownload , status:"
									+ finalStatus);
					request.abort();
					break http_request_loop;
				} else {
					// Handles the response, saves the file
					if (!continuingDownload) {
						Header header = response
								.getFirstHeader("Accept-Ranges");
						if (header != null) {
							headerAcceptRanges = header.getValue();
						}
						header = response.getFirstHeader("Content-Disposition");
						if (header != null) {
							headerContentDisposition = header.getValue();
						}
						header = response.getFirstHeader("Content-Location");
						if (header != null) {
							headerContentLocation = header.getValue();
						}
						if (mimeType == null) {
							header = response.getFirstHeader("Content-Type");
							if (header != null) {
								mimeType = sanitizeMimeType(header.getValue());
								Log.i(TAG,
										"**********************Get mimeType from  Http Content-Type, mimeType :"
												+ mimeType);
							}
						}
						header = response.getFirstHeader("ETag");
						if (header != null) {
							headerETag = header.getValue();
						}
						header = response.getFirstHeader("Transfer-Encoding");
						if (header != null) {
							headerTransferEncoding = header.getValue();
						}
						if (headerTransferEncoding == null) {
							header = response.getFirstHeader("Content-Length");
							if (header != null) {
								headerContentLength = header.getValue();
							}
						}

						if (!mInfo.mNoIntegrity
								&& headerContentLength == null
								&& (headerTransferEncoding == null || !headerTransferEncoding
										.equalsIgnoreCase("chunked"))) {

							finalStatus = Downloads.Impl.STATUS_LENGTH_REQUIRED;
							Log.d(TAG,
									"DownloadThread.run()******error****** , Downloads.Impl.STATUS_LENGTH_REQUIRED");
							request.abort();
							break http_request_loop;
						}

						DownloadFileInfo fileInfo = Helpers.generateSaveFile(
								mContext,
								mInfo.mUri,
								mInfo.mHint,
								headerContentDisposition,
								headerContentLocation,
								mimeType,
								mInfo.mDestination,
								(headerContentLength != null) ? Integer
										.parseInt(headerContentLength) : 0,
								mInfo.mFileName,
								// zdx modify
								mInfo.mCategory);

						ContentValues values = new ContentValues();

						if (!filename.equals(fileInfo.mFileName))
							values.put(Downloads._DATA, fileInfo.mFileName);
						if(fileInfo.mFileName != null)
							filename = fileInfo.mFileName;
						stream = fileInfo.mStream;

						// Log.i("zdx","    DownloadThread start download, filename:"+
						// filename);

						/*
						 * ContentValues values = new ContentValues();
						 * values.put(Downloads.Impl._DATA, filename);
						 */
						if (headerETag != null) {
							values.put(Constants.ETAG, headerETag);
						}
						if (mimeType != null) {
							values.put(Downloads.Impl.COLUMN_MIME_TYPE,
									mimeType);
						}
						int contentLength = -1;
						if (headerContentLength != null) {
							contentLength = Integer
									.parseInt(headerContentLength);
						}
						values.put(Downloads.Impl.COLUMN_TOTAL_BYTES,
								contentLength);
						mInfo.mTotalBytes = contentLength;
//						 Log.i(TAG,"***************DownloadThread.run, mInfo.mTotalBytes:"+
//						 mInfo.mTotalBytes);
						mContext.getContentResolver().update(contentUri,
								values, null, null);
//						 DownloadInfoContainer.updateDBAndBufferById(mContext, contentUri, values, null);
					}

					InputStream entityStream;
					try {
						entityStream = response.getEntity().getContent();
						
					} catch (IOException ex) {

						if (!Helpers.isNetworkAvailable(mContext)) {
							finalStatus = Downloads.Impl.STATUS_RUNNING_PAUSED;
							netError = true;
						} else if (mInfo.mNumFailed < Constants.MAX_RETRIES) {
							finalStatus = Downloads.Impl.STATUS_RUNNING_PAUSED;
							countRetry = true;
						} else {
							finalStatus = Downloads.Impl.STATUS_HTTP_DATA_ERROR;
						}
						Log.d(TAG,
								"DownloadThread.run()******error****** Response.getEntity IOException, status:"
										+ finalStatus);
						request.abort();
						break http_request_loop;
					}

					for (;;) {
						int bytesRead;
						try {
							// -----------------------read from http stream to
							// data---------
							bytesRead = entityStream.read(data);
							// Log.i(TAG,"********************************************************read data, bytesRead:"+
							// bytesRead);
						} catch (IOException ex) {
							ContentValues values = new ContentValues();
							values.put(Downloads.Impl.COLUMN_CURRENT_BYTES,
									bytesSoFar);
							mInfo.mCurrentBytes = bytesSoFar;
							// Log.i("zdx","***************DownloadThread.run, mInfo.mCurrentBytes:"+
							// mInfo.mCurrentBytes);
							mContext.getContentResolver().update(contentUri,
									values, null, null);
//							DownloadInfoContainer.updateDBAndBufferById(mContext, contentUri, values, null);
							
							if (!mInfo.mNoIntegrity && headerETag == null) {
								finalStatus = Downloads.Impl.STATUS_PRECONDITION_FAILED;
							} else if (!Helpers.isNetworkAvailable(mContext)) {
								finalStatus = Downloads.Impl.STATUS_RUNNING_PAUSED;
								netError = true;
							} else if (mInfo.mNumFailed < Constants.MAX_RETRIES) {
								finalStatus = Downloads.Impl.STATUS_RUNNING_PAUSED;
								countRetry = true;
							} else {
								finalStatus = Downloads.Impl.STATUS_HTTP_DATA_ERROR;
							}
							

							Log.d(TAG,
									"DownloadThread.run()******error****** IOException entityStream.read, status:"
											+ finalStatus);
							request.abort();
							break http_request_loop;
						}
						if (bytesRead == -1) { 
							ContentValues values = new ContentValues();
							values.put(Downloads.Impl.COLUMN_CURRENT_BYTES,
									bytesSoFar);
							mInfo.mCurrentBytes = bytesSoFar;
//							Log.i(TAG,"***************DownloadThread.run, 222 mInfo.mCurrentBytes:" + mInfo.mCurrentBytes);
							
							if (headerContentLength == null) {
								values.put(Downloads.Impl.COLUMN_TOTAL_BYTES,
										bytesSoFar);
								mInfo.mTotalBytes = bytesSoFar;
//								Log.i(TAG,"***************DownloadThread.run, 222 mInfo.mTotalBytes:" + mInfo.mTotalBytes);
							}
							mContext.getContentResolver().update(contentUri,
									values, null, null);
//							DownloadInfoContainer.updateDBAndBufferById(mContext, contentUri, values, null);
							if ((headerContentLength != null)
									&& (bytesSoFar != Integer
											.parseInt(headerContentLength))) {
								if (!mInfo.mNoIntegrity && headerETag == null) {
									finalStatus = Downloads.Impl.STATUS_LENGTH_REQUIRED;
								} else if (!Helpers
										.isNetworkAvailable(mContext)) {
									finalStatus = Downloads.Impl.STATUS_RUNNING_PAUSED;
									netError = true;
								} else if (mInfo.mNumFailed < Constants.MAX_RETRIES) {
									finalStatus = Downloads.Impl.STATUS_RUNNING_PAUSED;
									countRetry = true;
								} else {
									finalStatus = Downloads.Impl.STATUS_HTTP_DATA_ERROR;
								}
								Log.d(TAG,
										"DownloadThread.run()******error****** bytesSoFar!=headerContentLength, status:"
												+ finalStatus);
								// zdx modify
								request.abort();
								break http_request_loop;
							}
							break;
						}

						gotData = true;
						// zdx modify
						// for (;;) {
						try {
							if (stream == null) {
								stream = new FileOutputStream(filename, true);
							}
							// -----------------------write data to
							// file---------
							stream.write(data, 0, bytesRead);
							// Log.i(TAG,"********************************************************write data, bytesRead:"+
							// bytesRead);
							if ((mInfo.mDestination == Downloads.DESTINATION_USERCHOOSED)
									&& !Constants.MIMETYPE_DRM_MESSAGE
											.equalsIgnoreCase(mimeType)) {
								try {
									stream.close();
									stream = null;
								} catch (IOException ex) {
									Log.v(TAG,
											"DownloadThread.run(), exception when closing the file .");
									// nothing can really be done if the
									// file can't be closed
								}
							}
							if (!Helpers.isNetworkAvailable(mContext)) {
								finalStatus = Downloads.Impl.STATUS_RUNNING_PAUSED;
								Log.d(TAG,"-----------network unvailable");
								netError = true;
								request.abort();
								break http_request_loop;
							}
							
						} catch (FileNotFoundException ex) {
							Log.d(TAG,
									"DownloadThread.run()******error****** FileNotFoundException, Downloads.Impl.STATUS_FILE_ERROR");
							finalStatus = Downloads.Impl.STATUS_FILE_ERROR;
							request.abort();
							break http_request_loop;
							
							// falls through to the code that reports an error
						} catch (IOException ex) {
							finalStatus = Downloads.Impl.STATUS_FILE_ERROR;
							Log.d(TAG,
									"DownloadThread.run()******error****** FileOutputStream write file, Downloads.Impl.STATUS_FILE_ERROR");
							// zdx modify
							request.abort();
							break http_request_loop;
						}
						// }
						bytesSoFar += bytesRead;
						long now = System.currentTimeMillis();
						if (bytesSoFar - bytesNotified > Constants.MIN_PROGRESS_STEP
								&& now - timeLastNotification > Constants.MIN_PROGRESS_TIME) {
							ContentValues values = new ContentValues();
							values.put(Downloads.Impl.COLUMN_CURRENT_BYTES,
									bytesSoFar);
							mInfo.mCurrentBytes = bytesSoFar;
//							Log.i(TAG,"***************DownloadThread.run, 333 mInfo.mCurrentBytes:"+mInfo.mCurrentBytes);
							mContext.getContentResolver().update(contentUri,
									values, null, null);
//							DownloadInfoContainer.updateDBAndBufferById(mContext, contentUri, values, null);
							bytesNotified = bytesSoFar;
							timeLastNotification = now;
						}
						synchronized (mInfo) {
							if (mInfo.mControl == Downloads.Impl.CONTROL_PAUSED) {
								finalStatus = Downloads.Impl.STATUS_RUNNING_PAUSED;
								Log.d(TAG,
										"DownloadThread.run()****** Downloads.Impl.STATUS_RUNNING_PAUSED");
								request.abort();
								break http_request_loop;
							}
						}
						
						if (mInfo.mStatus == Downloads.Impl.STATUS_CANCELED) {
							finalStatus = Downloads.Impl.STATUS_CANCELED;
							Log.d(TAG,
									"DownloadThread.run()****** Downloads.Impl.STATUS_CANCELED");
							// zdx modify
							request.abort();
							break http_request_loop;
						}
						if (mInfo.mStatus == Downloads.Impl.STATUS_RESTART) {
							mInfo.mStatus = Downloads.Impl.STATUS_RUNNING;
							bytesSoFar = 0;
							bytesNotified = 0;
							Log.d(TAG,
									"DownloadThread.run()****** Downloads.Impl.STATUS_RESTART");
							request.abort();
							continue http_request_loop;
//							break http_request_loop;
						}
					}
					finalStatus = Downloads.Impl.STATUS_SUCCESS;
				}
				break;
			}
		} catch (FileNotFoundException ex) {
			finalStatus = Downloads.Impl.STATUS_FILE_ERROR;
			Log.d(TAG,
					"DownloadThread.run()******error****** FileNotFoundException, Downloads.Impl.STATUS_FILE_ERROR");
			// falls through to the code that reports an error
		} catch (RuntimeException ex) { // sometimes the socket code throws
										// unchecked exceptions
			finalStatus = Downloads.Impl.STATUS_UNKNOWN_ERROR;
			Log.d(TAG,"DownloadThread.run()******error****** RuntimeException, Downloads.Impl.STATUS_UNKNOWN_ERROR");
			// falls through to the code that reports an error
		} 
		finally {
//			mInfo.mHasActiveThread = false;
			if (wakeLock != null) {
				wakeLock.release();
				wakeLock = null;
			}
			if (client != null) {
				client.close();
				client = null;
			}
			try {
				// close the file
				if (stream != null) {
					stream.close();
					// zdx modify
					stream = null;
				}
			} catch (IOException ex) {
				Log.v(TAG,
						"DownloadThread.run()******error****** IOException stream.close");
				// nothing can really be done if the file can't be closed
			}
			if (filename != null) {
				// if the download wasn't successful, delete the file
				if (Downloads.Impl.isStatusError(finalStatus)) {
					new File(filename).delete();
					filename = null;
				} else if (Downloads.Impl.isStatusSuccess(finalStatus)
				// && DrmRawContent.DRM_MIMETYPE_MESSAGE_STRING
						&& Constants.MIMETYPE_DRM_MESSAGE
								.equalsIgnoreCase(mimeType)) {
					// transfer the file to the DRM content provider
					File file = new File(filename);
					file.delete();
				} else if (Downloads.Impl.isStatusSuccess(finalStatus)) {
					// Sync to storage after completion
					// Log.i(TAG,"    DownloadThread.run()****** Succeed!");
					try {
						new FileOutputStream(filename, true).getFD().sync();
					} catch (FileNotFoundException ex) {
						Log.w(TAG,
								"DownloadThread.run()****** , FileNotFoundException file: "
										+ filename);
					} catch (SyncFailedException ex) {
						Log.w(TAG,
								"DownloadThread.run()****** , SyncFailedException file: "
										+ filename);
					} catch (IOException ex) {
						Log.w(TAG,
								"DownloadThread.run()****** , IOException file: "
										+ filename);
					} catch (RuntimeException ex) {
						Log.w(TAG,
								"DownloadThread.run()****** , RuntimeException file: "
										+ filename);
					}
				}
			}
//			Log.d(TAG,"------downloadthread run---------finalStatus:" + finalStatus
//					+ ",countRetry:" + countRetry + ",retryAfter:" + retryAfter + ",redirectCount:" + redirectCount
//					+ ",gotData:" + gotData + ",filename:" + filename + ",newUri:" + newUri + ",mimeType:" + mimeType);
			notifyDownloadProcess(finalStatus, countRetry, retryAfter,
					redirectCount, gotData, filename, newUri, mimeType);
			// zdx modify
			DownloadQueueHandler.getInstance().dequeueDownload(mContext,
					mInfo.mId, netError);
			mInfo.mHasActiveThread = false;

		}
	}

	/**
	 * Stores information about the completed download, and notifies the
	 * initiating application.
	 */
	private void notifyDownloadProcess(int status, boolean countRetry,
			int retryAfter, int redirectCount, boolean gotData,
			String filename, String uri, String mimeType) {
		notifyThroughDatabase(status, countRetry, retryAfter, redirectCount,
				gotData, filename, uri, mimeType);
		if (Downloads.Impl.isStatusCompleted(status)) {
			notifyThroughIntent();
		}
	}

	private void notifyThroughDatabase(int status, boolean countRetry,
			int retryAfter, int redirectCount, boolean gotData,
			String filename, String uri, String mimeType) {
		ContentValues values = new ContentValues();
		
//		if( Downloads.Impl.STATUS_RESTART == status){
//			status = Downloads.Impl.STATUS_PENDING;
//			values.put(Downloads.Impl.COLUMN_CONTROL,
//					Downloads.CONTROL_RUN);
//			values.put(Downloads.Impl.COLUMN_CURRENT_BYTES, 0);
//		}
		values.put(Downloads.Impl.COLUMN_STATUS, status);
		mInfo.mStatus = status;
		if (filename != null && !filename.equals(""))
			values.put(Downloads.Impl._DATA, filename);
		if (uri != null) {
			values.put(Downloads.Impl.COLUMN_URI, uri);
		}
		values.put(Downloads.Impl.COLUMN_MIME_TYPE, mimeType);
		values.put(Downloads.Impl.COLUMN_LAST_MODIFICATION,
				System.currentTimeMillis());
		values.put(Constants.RETRY_AFTER_X_REDIRECT_COUNT, retryAfter
				+ (redirectCount << 28));
		if (!countRetry) {
			values.put(Constants.FAILED_CONNECTIONS, 0);
		} else if (gotData) {
			values.put(Constants.FAILED_CONNECTIONS, 1);
		} else {
			values.put(Constants.FAILED_CONNECTIONS, mInfo.mNumFailed + 1);
		}
		Log.d(TAG,"------notifyThroughDatabase------------,uri:" + uri +",status:"+ status);
		mContext.getContentResolver().update(
				ContentUris.withAppendedId(Downloads.Impl.CONTENT_URI,
						mInfo.mId), values, null, null);
//		DownloadInfoContainer.updateDBAndBufferById(mContext, 
//				ContentUris.withAppendedId(Downloads.Impl.CONTENT_URI,mInfo.mId), values, null);

	}

	/**
	 * Notifies the initiating app if it requested it. That way, it can know
	 * that the download completed even if it's not actively watching the
	 * cursor.
	 */
	private void notifyThroughIntent() {
		Uri uri = Uri.parse(Downloads.Impl.CONTENT_URI + "/" + mInfo.mId);
		mInfo.sendIntentIfRequested(uri, mContext);
	}

	/**
	 * Clean up a mimeType string so it can be used to dispatch an intent to
	 * view a downloaded asset.
	 * 
	 * @param mimeType
	 *            either null or one or more mime types (semi colon separated).
	 * @return null if mimeType was null. Otherwise a string which represents a
	 *         single mimetype in lowercase and with surrounding whitespaces
	 *         trimmed.
	 */
	private String sanitizeMimeType(String mimeType) {
		try {
			mimeType = mimeType.trim().toLowerCase(Locale.ENGLISH);

			final int semicolonIndex = mimeType.indexOf(';');
			if (semicolonIndex != -1) {
				mimeType = mimeType.substring(0, semicolonIndex);
			}
			return mimeType;
		} catch (NullPointerException npe) {
			return null;
		}
	}
}
