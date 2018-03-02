/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lenovo.lejingpin.share.download;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;

import com.lenovo.lejingpin.settings.LejingpingSettingsValues;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Xml;
import android.webkit.MimeTypeMap;

/**
 * Some helper functions for the download manager
 */
public class Helpers {

	public static Random sRandom = new Random(SystemClock.uptimeMillis());
	private static String mWlanOnly;

	/** Regex used to parse content-disposition headers */
	private static final Pattern CONTENT_DISPOSITION_PATTERN = Pattern
			.compile("attachment;\\s*filename\\s*=\\s*\"([^\"]*)\"");

	private Helpers() {
	}

	/*
	 * Parse the Content-Disposition HTTP Header. The format of the header is
	 * defined here: http://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html This
	 * header provides a filename for content that is going to be downloaded to
	 * the file system. We only support the attachment type.
	 */
	private static String parseContentDisposition(String contentDisposition) {
		try {
			Matcher m = CONTENT_DISPOSITION_PATTERN.matcher(contentDisposition);
			if (m.find()) {
				return m.group(1);
			}
		} catch (IllegalStateException ex) {
			// This function is defined as returning null when it can't parse
			// the header
		}
		return null;
	}

	public static DownloadFileInfo generateSaveFile(Context context,
			String url, String hint, String contentDisposition,
			String contentLocation, String mimeType, int destination,
			int contentLength,
			// zdx modify
			int category) throws FileNotFoundException {
		return generateSaveFile(context, url, hint, contentDisposition,
				contentLocation, mimeType, destination, contentLength, null,
				// zdx modify
				category);
	}

	/**
	 * Creates a filename (where the file should be saved) from a uri.
	 */
	public static DownloadFileInfo generateSaveFile(Context context,
			String url, String hint, String contentDisposition,
			String contentLocation, String mimeType, int destination,
			int contentLength, String fullPathFileName,
			// zdx modify
			int category) throws FileNotFoundException {

		Log.i(TAG, "Helpers.generateSaveFile, fullPathFileName:"
				+ fullPathFileName + ", contentLength:" + contentLength
				+ ", mimeType:" + mimeType + " , url:" + url);

		// Don't download files that we won't be able to handle
		if (mimeType == null) {
			Log.e(TAG, "external download with no mime type not allowed");
			return new DownloadFileInfo(null, null,
					Downloads.Impl.STATUS_NOT_ACCEPTABLE);
		}

		if (fullPathFileName == null) {
			return new DownloadFileInfo(null, null, Downloads.STATUS_FILE_ERROR);
		}

		String filename = null;
		String extension = null;
		filename = chooseFilename(url, hint, contentDisposition,
				contentLocation, destination, fullPathFileName);
		Log.i(TAG, "Helpers.generateSaveFile in, filename:" + filename);
		int lastDotIndex = filename.lastIndexOf('.');
		if (lastDotIndex < 0) {
			extension = chooseExtensionFromMimeType(mimeType, true);
			Log.i(TAG, "Helpers.generateSaveFile in, extension:" + extension
					+ ", filename:" + filename);
		} else {
			extension = chooseExtensionFromFilename(mimeType, destination,
					filename);
			if (extension == null) {
				extension = filename.substring(lastDotIndex);
				filename = filename.substring(0, lastDotIndex);
			}
			Log.i(TAG, "Helpers.generateSaveFile in, else extension:"
					+ extension + ", filename:" + filename);
		}
		String path = fullPathFileName.substring(0,
				fullPathFileName.lastIndexOf("/"));
		File fPath = new File(path);
		if (!fPath.isDirectory() && !fPath.mkdir()) {
			Log.i(TAG,
					"Helpers, download aborted - can't create base directory "
							+ fPath.getPath());
			return new DownloadFileInfo(null, null, Downloads.STATUS_FILE_ERROR);
		}
		StatFs stat = new StatFs(path);
		/*
		 * Check whether there's enough space on the target filesystem to save
		 * the file. Put a bit of margin (in case creating the file grows the
		 * system by a few blocks).
		 */
		if (stat.getBlockSize() * ((long) stat.getAvailableBlocks() - 4) < contentLength) {
			Log.i(TAG, "Helpers, download aborted - not enough free space");
			return new DownloadFileInfo(null, null,
					Downloads.Impl.STATUS_INSUFFICIENT_SPACE_ERROR);
		}
		boolean recoveryDir = Constants.RECOVERY_DIRECTORY
				.equalsIgnoreCase(filename + extension);
		filename = path + File.separator + filename;
		String fullFilename = chooseUniqueFilename(destination, filename,
				extension, recoveryDir);
		if (fullFilename != null) {
			return new DownloadFileInfo(fullFilename, new FileOutputStream(
					fullFilename), 0);
		} else {
			return new DownloadFileInfo(null, null, Downloads.STATUS_FILE_ERROR);
		}
	}

	private static String chooseFilename(String url, String hint,
			String contentDisposition, String contentLocation, int destination,
			String fullPathFileName) {
		String filename = null;

		// First, try to use the hint from the application, if there's one
		if (hint != null && !hint.endsWith("/")) {
			int index = hint.lastIndexOf('/') + 1;
			if (index > 0) {
				filename = hint.substring(index);
			} else {
				filename = hint;
			}
		}

		// If we couldn't do anything with the hint, move toward the content
		// disposition
		if (filename == null && contentDisposition != null) {
			filename = parseContentDisposition(contentDisposition);
			if (filename != null) {
				int index = filename.lastIndexOf('/') + 1;
				if (index > 0) {
					filename = filename.substring(index);
				}
			}
		}

		// If we still have nothing at this point, try the content location
		if (filename == null && contentLocation != null) {
			String decodedContentLocation = Uri.decode(contentLocation);
			if (decodedContentLocation != null
					&& !decodedContentLocation.endsWith("/")
					&& decodedContentLocation.indexOf('?') < 0) {
				int index = decodedContentLocation.lastIndexOf('/') + 1;
				if (index > 0) {
					filename = decodedContentLocation.substring(index);
				} else {
					filename = decodedContentLocation;
				}
			}
		}

		// If all the other http-related approaches failed, use the plain uri
		if (filename == null) {
			String decodedUrl = Uri.decode(url);
			if (decodedUrl != null && !decodedUrl.endsWith("/")
					&& decodedUrl.indexOf('?') < 0) {
				int index = decodedUrl.lastIndexOf('/') + 1;
				if (index > 0) {
					filename = decodedUrl.substring(index);
				}
			}
		}

		// Finally, if couldn't get filename from URI, get a generic filename
		if (filename == null) {
			if (fullPathFileName != null && !fullPathFileName.isEmpty())
				filename = fullPathFileName.substring(fullPathFileName
						.lastIndexOf("/") + 1);
			else
				filename = Constants.DEFAULT_DL_FILENAME;
		}
		filename = filename
				.replaceAll(
						"[^a-zA-Z0-9\\.\\-_\\u4E00-\\u9FA5\\uFF00-\\uFFEF\\u2E80-\\u2EFF\\u3000-\\u303F\\u31C0-\\u31EF\\u0028\\u0029\\u005E\\u002B\\u007B\\u007D\\u0040\\u0023-\\u0026\\u002D\\u2014\\u201C\\u201D\\u0021\\u003B\\u002C\\u0020]+",
						"_");

		return filename;
	}

	private static String chooseExtensionFromMimeType(String mimeType,
			boolean useDefaults) {
		String extension = null;
		if (mimeType != null) {
			extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(
					mimeType);
			if (extension != null) {
				extension = "." + extension;
			}
		}
		if (extension == null) {
			if (mimeType != null && mimeType.toLowerCase().startsWith("text/")) {
				if (mimeType.equalsIgnoreCase("text/html")) {
					extension = Constants.DEFAULT_DL_HTML_EXTENSION;
				} else if (useDefaults) {
					extension = Constants.DEFAULT_DL_TEXT_EXTENSION;
				}
			} else if (useDefaults) {
				extension = Constants.DEFAULT_DL_BINARY_EXTENSION;
			}
		}
		return extension;
	}

	private static String chooseExtensionFromFilename(String mimeType,
			int destination, String filename) {
		String extension = null;
		int lastDotIndex = filename.lastIndexOf('.');
		if (mimeType != null) {
			// Compare the last segment of the extension against the mime type.
			// If there's a mismatch, discard the entire extension.
			String typeFromExt = MimeTypeMap.getSingleton()
					.getMimeTypeFromExtension(
							filename.substring(lastDotIndex + 1));
			if (typeFromExt == null || !typeFromExt.equalsIgnoreCase(mimeType)) {
				extension = chooseExtensionFromMimeType(mimeType, false);
			}
		}
		return extension;
	}

	private static String chooseUniqueFilename(int destination,
			String filename, String extension, boolean recoveryDir) {
		String fullFilename = filename + extension;
		if (!new File(fullFilename).exists() && (!recoveryDir)) {
			return fullFilename;
		}
		filename = filename + Constants.FILENAME_SEQUENCE_SEPARATOR;
		/*
		 * This number is used to generate partially randomized filenames to
		 * avoid collisions. It starts at 1. The next 9 iterations increment it
		 * by 1 at a time (up to 10). The next 9 iterations increment it by 1 to
		 * 10 (random) at a time. The next 9 iterations increment it by 1 to 100
		 * (random) at a time. ... Up to the point where it increases by
		 * 100000000 at a time. (the maximum value that can be reached is
		 * 1000000000) As soon as a number is reached that generates a filename
		 * that doesn't exist, that filename is used. If the filename coming in
		 * is [base].[ext], the generated filenames are [base]-[sequence].[ext].
		 */
		int sequence = 1;
		for (int magnitude = 1; magnitude < 1000000000; magnitude *= 10) {
			for (int iteration = 0; iteration < 9; ++iteration) {
				fullFilename = filename + sequence + extension;
				if (!new File(fullFilename).exists()) {
					return fullFilename;
				}
				sequence += sRandom.nextInt(magnitude) + 1;
			}
		}
		return null;
	}
	
	public static void setwlanDownloadValue(boolean flag){
		mWlanOnly = String.valueOf(flag);
		Log.d(TAG,"-----setwlanDownloadValue----mWlanOnly:" + mWlanOnly);
	}
	
    public static synchronized boolean getwlanDownloadValue(Context context) {
    	boolean re;
    	if(null == mWlanOnly){
	        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		//yangmao modify this , the default value is false 0522
	        boolean isWlanDownload = sp.getBoolean(LejingpingSettingsValues.KEY_WLAN_DOWNLOAD, false);
	        mWlanOnly = String.valueOf(isWlanDownload);
    	}
    	Log.d(TAG,"-----getwlanDownloadValue----mWlanOnly:" + mWlanOnly);
    	if(mWlanOnly.equals("true")){
    		re = true;
    	}
    	else{
    		re = false;
    	}
    	Log.d(TAG,"-----getwlanDownloadValue----iswlanonly:" + re);
		return re;
    }

	/**
	 * Returns whether the network is available
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						if(info[i].getType() != ConnectivityManager.TYPE_WIFI 
								&& getwlanDownloadValue(context)
								)
							return false;
						else
							return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns whether the network is roaming
	 */
	/*
	 * public static boolean isNetworkRoaming(Context context) {
	 * ConnectivityManager connectivity = (ConnectivityManager) context
	 * .getSystemService(Context.CONNECTIVITY_SERVICE); TelephonyManager
	 * telephonyManager = (TelephonyManager)
	 * context.getSystemService(Context.TELEPHONY_SERVICE); if (connectivity !=
	 * null) { NetworkInfo info = connectivity.getActiveNetworkInfo(); if (info
	 * != null && info.getType() == ConnectivityManager.TYPE_MOBILE) { if
	 * (telephonyManager.isNetworkRoaming()) { return true; } } } return false;
	 * }
	 */

	/**
	 * Checks whether the filename looks legitimate
	 */
	public static boolean isFilenameValid(String filename) {
		File dir = new File(filename).getParentFile();
		return dir.equals(Environment.getDownloadCacheDirectory())
				|| dir.equals(new File(Environment
						.getExternalStorageDirectory()
						+ Constants.DEFAULT_DL_SUBDIR));
	}

	/**
	 * Checks whether this looks like a legitimate selection parameter
	 */
	public static void validateSelection(String selection,
			Set<String> allowedColumns) {
		try {
			if (selection == null) {
				return;
			}
			Lexer lexer = new Lexer(selection, allowedColumns);
			parseExpression(lexer);
			if (lexer.currentToken() != Lexer.TOKEN_END) {
				throw new IllegalArgumentException("syntax error");
			}
		} catch (RuntimeException ex) {
			Log.d(TAG, "Helpers, invalid selection [" + selection
					+ "] triggered " + ex);
			throw ex;
		}

	}

	// expression <- ( expression ) | statement [AND_OR ( expression ) |
	// statement] *
	// | statement [AND_OR expression]*
	private static void parseExpression(Lexer lexer) {
		for (;;) {
			// ( expression )
			if (lexer.currentToken() == Lexer.TOKEN_OPEN_PAREN) {
				lexer.advance();
				parseExpression(lexer);
				if (lexer.currentToken() != Lexer.TOKEN_CLOSE_PAREN) {
					throw new IllegalArgumentException(
							"syntax error, unmatched parenthese");
				}
				lexer.advance();
			} else {
				// statement
				parseStatement(lexer);
			}
			if (lexer.currentToken() != Lexer.TOKEN_AND_OR) {
				break;
			}
			lexer.advance();
		}
	}

	// statement <- COLUMN COMPARE VALUE
	// | COLUMN IS NULL
	private static void parseStatement(Lexer lexer) {
		// both possibilities start with COLUMN
		if (lexer.currentToken() != Lexer.TOKEN_COLUMN) {
			throw new IllegalArgumentException(
					"syntax error, expected column name");
		}
		lexer.advance();

		// statement <- COLUMN COMPARE VALUE
		if (lexer.currentToken() == Lexer.TOKEN_COMPARE) {
			lexer.advance();
			if (lexer.currentToken() != Lexer.TOKEN_VALUE) {
				throw new IllegalArgumentException(
						"syntax error, expected quoted string");
			}
			lexer.advance();
			return;
		}

		// statement <- COLUMN IS NULL
		if (lexer.currentToken() == Lexer.TOKEN_IS) {
			lexer.advance();
			if (lexer.currentToken() != Lexer.TOKEN_NULL) {
				throw new IllegalArgumentException(
						"syntax error, expected NULL");
			}
			lexer.advance();
			return;
		}

		// didn't get anything good after COLUMN
		throw new IllegalArgumentException("syntax error after column name");
	}

	/**
	 * A simple lexer that recognizes the words of our restricted subset of SQL
	 * where clauses
	 */
	private static class Lexer {
		public static final int TOKEN_START = 0;
		public static final int TOKEN_OPEN_PAREN = 1;
		public static final int TOKEN_CLOSE_PAREN = 2;
		public static final int TOKEN_AND_OR = 3;
		public static final int TOKEN_COLUMN = 4;
		public static final int TOKEN_COMPARE = 5;
		public static final int TOKEN_VALUE = 6;
		public static final int TOKEN_IS = 7;
		public static final int TOKEN_NULL = 8;
		public static final int TOKEN_END = 9;

		private final String mSelection;
		private final Set<String> mAllowedColumns;
		private int mOffset = 0;
		private int mCurrentToken = TOKEN_START;
		private final char[] mChars;

		public Lexer(String selection, Set<String> allowedColumns) {
			mSelection = selection;
			mAllowedColumns = allowedColumns;
			mChars = new char[mSelection.length()];
			mSelection.getChars(0, mChars.length, mChars, 0);
			advance();
		}

		public int currentToken() {
			return mCurrentToken;
		}

		public void advance() {
			char[] chars = mChars;

			// consume whitespace
			while (mOffset < chars.length && chars[mOffset] == ' ') {
				++mOffset;
			}

			// end of input
			if (mOffset == chars.length) {
				mCurrentToken = TOKEN_END;
				return;
			}

			// "("
			if (chars[mOffset] == '(') {
				++mOffset;
				mCurrentToken = TOKEN_OPEN_PAREN;
				return;
			}

			// ")"
			if (chars[mOffset] == ')') {
				++mOffset;
				mCurrentToken = TOKEN_CLOSE_PAREN;
				return;
			}

			// "?"
			if (chars[mOffset] == '?') {
				++mOffset;
				mCurrentToken = TOKEN_VALUE;
				return;
			}

			// "=" and "=="
			if (chars[mOffset] == '=') {
				++mOffset;
				mCurrentToken = TOKEN_COMPARE;
				if (mOffset < chars.length && chars[mOffset] == '=') {
					++mOffset;
				}
				return;
			}

			// ">" and ">="
			if (chars[mOffset] == '>') {
				++mOffset;
				mCurrentToken = TOKEN_COMPARE;
				if (mOffset < chars.length && chars[mOffset] == '=') {
					++mOffset;
				}
				return;
			}

			// "<", "<=" and "<>"
			if (chars[mOffset] == '<') {
				++mOffset;
				mCurrentToken = TOKEN_COMPARE;
				if (mOffset < chars.length
						&& (chars[mOffset] == '=' || chars[mOffset] == '>')) {
					++mOffset;
				}
				return;
			}

			// "!="
			if (chars[mOffset] == '!') {
				++mOffset;
				mCurrentToken = TOKEN_COMPARE;
				if (mOffset < chars.length && chars[mOffset] == '=') {
					++mOffset;
					return;
				}
				throw new IllegalArgumentException(
						"Unexpected character after !");
			}

			// columns and keywords
			// first look for anything that looks like an identifier or a
			// keyword
			// and then recognize the individual words.
			// no attempt is made at discarding sequences of underscores with no
			// alphanumeric
			// characters, even though it's not clear that they'd be legal
			// column names.
			if (isIdentifierStart(chars[mOffset])) {
				int startOffset = mOffset;
				++mOffset;
				while (mOffset < chars.length
						&& isIdentifierChar(chars[mOffset])) {
					++mOffset;
				}
				String word = mSelection.substring(startOffset, mOffset);
				if (mOffset - startOffset <= 4) {
					if (word.equals("IS")) {
						mCurrentToken = TOKEN_IS;
						return;
					}
					if (word.equals("OR") || word.equals("AND")) {
						mCurrentToken = TOKEN_AND_OR;
						return;
					}
					if (word.equals("NULL")) {
						mCurrentToken = TOKEN_NULL;
						return;
					}
				}
				if (mAllowedColumns.contains(word)) {
					mCurrentToken = TOKEN_COLUMN;
					return;
				}
				throw new IllegalArgumentException(
						"unrecognized column or keyword");
			}

			// quoted strings
			if (chars[mOffset] == '\'') {
				++mOffset;
				while (mOffset < chars.length) {
					if (chars[mOffset] == '\'') {
						if (mOffset + 1 < chars.length
								&& chars[mOffset + 1] == '\'') {
							++mOffset;
						} else {
							break;
						}
					}
					++mOffset;
				}
				if (mOffset == chars.length) {
					throw new IllegalArgumentException("unterminated string");
				}
				++mOffset;
				mCurrentToken = TOKEN_VALUE;
				return;
			}

			// anything we don't recognize
			throw new IllegalArgumentException("illegal character");
		}

		private static final boolean isIdentifierStart(char c) {
			return c == '_' || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
		}

		private static final boolean isIdentifierChar(char c) {
			return c == '_' || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
					|| (c >= '0' && c <= '9');
		}
	}

	private static final String TAG = "Helpers";

	/**
	 *  
	 */
//	public static int checkErrorCode(int status) {
//		if (Downloads.STATUS_PENDING == status
//				|| Downloads.STATUS_PENDING_PAUSED == status) {
//			status = Downloads.STATUS_PENDING;
//		}
//		status = getErrorNetwork(status);
//		if (-1 == status) {
//			status = -1;
//		}
//		status = getErrorSDcard(status);
//		if (-2 == status) {
//			status = -2;
//		}
//		return status;
//	}

//	public static int getErrorNetwork(int status) {
//		int s = getErrorSDcard(status);
//		if (status != 190 && status != 192 && status != 193 && status != 200
//				&& s != -2) {
//			return -1;
//		}
//		return status;
//	}
	
	public synchronized static int getStatus(int status){
		if (status == 492 || status == 498 || status == 499) {
			// file error
			status = -2;
		}else if(status > 399 && status < 500){
			//connect error
			status = -1;
		}
		
		return status;
	}

//	public static int getErrorSDcard(int status) {
//		if (status == 492 || status == 498 || status == 499) {
//			return -2;
//		}
//		return status;
//	}

	public static Map<String, String> getAmp(String body) throws Exception {
		InputStream inStream;
		inStream = new ByteArrayInputStream(body.getBytes());
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(inStream, "UTF-8");
		int eventType = parser.getEventType();
		Map<String, String> amp = null;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				amp = new HashMap<String, String>();
				break;
			case XmlPullParser.START_TAG:
				String nodeName = parser.getName();
				if (amp != null) {
					if ("Type".equals(nodeName)) {
						amp.put("Type", parser.nextText());
					}
					if ("AppID".equals(nodeName)) {
						amp.put("AppID", parser.nextText());
					}
					if ("AppName".equals(nodeName)) {
						amp.put("AppName", parser.nextText());
					}
					if ("PackageName".equals(nodeName)) {
						amp.put("PackageName", parser.nextText());
					}
					if ("VersionCode".equals(nodeName)) {
						amp.put("VersionCode", parser.nextText());
					}
					if ("App".equals(nodeName)) {
						amp.put("App", parser.nextText());
					}
				}
				break;

			case XmlPullParser.END_TAG:
				break;
			}
			eventType = parser.next();
		}
		inStream.close();
		return amp;
	}

	/**
	 * Push app data parse method
	 */
	public static Map<String, String> Parse(String body) {

		return MyParser.Parse(body, new CommendHandler());
	}

	private abstract static class AppHandler extends DefaultHandler {

		private String mCurrentTAG;
		private Map<String, String> data;
		public static List<String> permissions = new ArrayList<String>();
		public static int launchActivities = 0;

		public AppHandler() {
			data = new HashMap<String, String>();
		}

		public Map<String, String> getParseData() {
			return data;
		}

		@Override
		public void startDocument() throws SAXException {
			launchActivities = 0;
		}

		@Override
		public void startElement(String uri, String localName, String name,
				Attributes attr) throws SAXException {
			mCurrentTAG = localName;
			// if(localName.equals("manifest")){
			// permissions = new ArrayList();
			// }
			if (attr == null) {
				return;
			}
			if (mCurrentTAG.equals("uses-permission")) {
				if (attr.getLength() > 0) {
					permissions.add(attr.getValue(0));
				}

			}
			if (mCurrentTAG.equals("category")) {
				if (attr.getValue(0).equals(Intent.CATEGORY_LAUNCHER)) {
					launchActivities++;
				}
			}
			int len = attr.getLength();
			for (int i = 0; i < len; i++) {
				Log.d(TAG,
						String.format("attr(%s) value(%s)",
								attr.getLocalName(i), attr.getValue(i)));
				data.put(attr.getLocalName(i), attr.getValue(i));
			}
		}

		@Override
		public void endElement(String uri, String localName, String name)
				throws SAXException {
			mCurrentTAG = null;
		}

		@Override
		public void characters(char ch[], int start, int length) {
			String curStr = new String(ch, start, length);
			if (mCurrentTAG == null) {
				return;
			}
			if (data.containsKey(mCurrentTAG)) {
				curStr = (String) data.get(mCurrentTAG) + curStr;
			}
			data.put(mCurrentTAG, curStr.trim());
		}
	}

	private static class MyParser {

		public static Map<String, String> Parse(String text, AppHandler handler) {
			if (text == null || text.trim().length() == 0) {
				Log.e(TAG, "Parse() text is null !");
				return null;
			}
			Map<String, String> data = null;
			try {
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser sp = spf.newSAXParser();
				XMLReader xr = sp.getXMLReader();
				xr.setContentHandler(handler);
				CharArrayReader car = new CharArrayReader(text.toCharArray());
				xr.parse(new InputSource(car));
				data = handler.getParseData();
				car.close();
			} catch (Exception e) {
				Log.e(TAG, "ParsingXMLError:\n", e);
				return null;
			}
			return data;
		}
	}

	public static class CommendHandler extends AppHandler {
		public final static String TAG_APPS = "App";
		public final static String TAG_TYPE = "Type";
		public final static String TAG_Version = "Version";
		public final static String TAG_APPID = "AppID";
		public final static String TAG_APPNAME = "AppName";
	}

}

class DownloadFileInfo {
	String mFileName;
	FileOutputStream mStream;
	int mStatus;

	public DownloadFileInfo(String fileName, FileOutputStream stream, int status) {
		mFileName = fileName;
		mStream = stream;
		mStatus = status;
	}
}

class Constants {

	private Constants(){
		
	}
	/** Tag used for debugging/logging */
	public static final String TAG = "xujing3";

	/** The column that used to be used for the HTTP method of the request */
	public static final String RETRY_AFTER_X_REDIRECT_COUNT = "method";

	/** The column that used to be used for the magic OTA update filename */
	public static final String OTA_UPDATE = "otaupdate";

	/** The column that used to be used to reject system filetypes */
	public static final String NO_SYSTEM_FILES = "no_system";

	/** The column that is used for the downloads's ETag */
	public static final String ETAG = "etag";

	/** The column that is used for the initiating app's UID */
	public static final String UID = "uid";

	/**
	 * The column that is used to remember whether the media scanner was invoked
	 */
	public static final String MEDIA_SCANNED = "scanned";

	/** The column that is used to count retries */
	public static final String FAILED_CONNECTIONS = "numfailed";

	/** The intent that gets sent when the service must wake up for a retry */
	public static final String ACTION_RETRY = "com.lenovo.lejingpin.share.download.DOWNLOAD_WAKEUP";

	/** the intent that gets sent when clicking a on_going download */
	public static final String ACTION_OPEN = "android.intent.action.DOWNLOAD_OPEN";

	/** the intent that gets sent when clicking an complete/failed download */
	public static final String ACTION_LIST = "android.intent.action.DOWNLOAD_LIST";

	/**
	 * the intent that gets sent when deleting the notification of a completed
	 * download
	 */
	public static final String ACTION_HIDE = "android.intent.action.DOWNLOAD_HIDE";

	/**
	 * The default base name for downloaded files if we can't get one at the
	 * HTTP level
	 */
	public static final String DEFAULT_DL_FILENAME = "downloadfile";

	/**
	 * The default extension for html files if we can't get one at the HTTP
	 * level
	 */
	public static final String DEFAULT_DL_HTML_EXTENSION = ".html";

	public static final String DEFAULT_DL_LIC_EXTENSION = ".lca";

	/**
	 * The default extension for text files if we can't get one at the HTTP
	 * level
	 */
	public static final String DEFAULT_DL_TEXT_EXTENSION = ".txt";

	/**
	 * The default extension for binary files if we can't get one at the HTTP
	 * level
	 */
	public static final String DEFAULT_DL_BINARY_EXTENSION = ".bin";

	/**
	 * When a number has to be appended to the filename, this string is used to
	 * separate the base filename from the sequence number
	 */
	public static final String FILENAME_SEQUENCE_SEPARATOR = "-";

	/** Where we store downloaded files on the external storage */
	public static final String DEFAULT_DL_SUBDIR = "/download";

	/** A magic filename that is allowed to exist within the system cache */
	public static final String KNOWN_SPURIOUS_FILENAME = "lost+found";

	/** A magic filename that is allowed to exist within the system cache */
	public static final String RECOVERY_DIRECTORY = "recovery";

	/** The default user agent used for downloads */
	public static final String DEFAULT_USER_AGENT = "LDownloadManager";

	/** The MIME type of special DRM files */
	public static final String MIMETYPE_DRM_MESSAGE = "application/vnd.oma.drm.message";

	/** The MIME type of APKs */
	public static final String MIMETYPE_APK = "application/vnd.android.package";

	/** The buffer size used to stream the data */
	public static final int BUFFER_SIZE = 4096;

	/**
	 * The minimum amount of progress that has to be done before the progress
	 * bar gets updated
	 */
	public static final int MIN_PROGRESS_STEP = 4096;

	/**
	 * The minimum amount of time that has to elapse before the progress bar
	 * gets updated, in ms
	 */
	public static final long MIN_PROGRESS_TIME = 1500;

	/** The maximum number of rows in the database (FIFO) */
	public static final int MAX_DOWNLOADS = 1000;

	/**
	 * The number of times that the download manager will retry its network
	 * operations when no progress is happening before it gives up.
	 */
	public static final int MAX_RETRIES = 5;

	/**
	 * The minimum amount of time that the download manager accepts for a
	 * Retry-After response header with a parameter in delta-seconds.
	 */
	public static final int MIN_RETRY_AFTER = 30; // 30s

	/**
	 * The maximum amount of time that the download manager accepts for a
	 * Retry-After response header with a parameter in delta-seconds.
	 */
	public static final int MAX_RETRY_AFTER = 24 * 60 * 60; // 24h

	/**
	 * The maximum number of redirects.
	 */
	public static final int MAX_REDIRECTS = 5; // can't be more than 7.

	/**
	 * The time between a failure and the first retry after an IOException. Each
	 * subsequent retry grows exponentially, doubling each time. The time is in
	 * seconds.
	 */
	public static final int RETRY_FIRST_DELAY = 30;

}
