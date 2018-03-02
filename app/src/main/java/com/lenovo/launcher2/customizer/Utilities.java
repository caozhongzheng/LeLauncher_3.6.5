package com.lenovo.launcher2.customizer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.TableMaskFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StatFs;
import android.os.SystemProperties;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher.components.XAllAppFace.XLauncher;
import com.lenovo.launcher2.customizer.Debug.R2;

/**
 * Author : ChengLiang
 * */
public class Utilities {

	public InputStream getInputStream(String path) {
		File file = new File(path);
		FileInputStream fis = null;
		try {
			if (!file.exists()) {
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Expected file: " + file.getPath());
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Can not find the given file, just return!");
			}
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
		}
		if (fis != null)
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("File inputstream found.");
		return fis;
	}

	public InputStream getInputStream(final Context context, String name) {
		try {
			return context.openFileInput(name);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	public boolean ensureDir(String dirPath) {
		// if(Debug.MAIN_DEBUG_SWITCH)
		R2.echo("Ensure dir : " + dirPath);
		if (dirPath == null)
			return true;
		File f = new File(dirPath);
		if (f.exists())
			return true;
		return f.mkdir();
	}

	public String getPathNameByProfileName(String profileName, String subDir) {
		StringBuilder fullName = new StringBuilder();
		fullName.append(ConstantAdapter.DIR_PARENT_OF_STORAGE_BACKUP_FILE)
				.append(ConstantAdapter.DIR_TO_STORAGE_BACKUP_FILE)
				.append(File.separator).append(profileName)
				.append(File.separator).append(subDir).append(File.separator);
		fullName.trimToSize();
		return new File(fullName.toString()).getPath();
	}

	public class ZipHelper {

		public void createZipFile(String filePath, String zipFilePath) {

			ensureParentsPaths(zipFilePath, false);

			FileOutputStream fos = null;
			ZipOutputStream zos = null;
			CheckedOutputStream cos = null;
			try {
				fos = new FileOutputStream(zipFilePath);
				cos = new CheckedOutputStream(fos, new Adler32());
				zos = new ZipOutputStream(cos);
				writeZipFile(new File(filePath), zos, null);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (zos != null)
						zos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					if (fos != null)
						fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void writeZipFile(File f, ZipOutputStream zos, String arch) {
			if (f.exists()) {
				if (f.isDirectory()) {
					if (arch != null) {
						arch += f.getName() + File.separator;
					} else {
						arch = "";
					}
					File[] fif = f.listFiles();
					for (int i = 0; i < fif.length; i++) {
						writeZipFile(fif[i], zos, arch);
					}
				} else {
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(f);
						ZipEntry ze = new ZipEntry(arch + f.getName());
						// R2
						if (Debug.MAIN_DEBUG_SWITCH)
							R2.echo("lbk add : " + arch + f.getName());
						// 2R
						zos.putNextEntry(ze);
						byte[] buf = new byte[1024];
						int offset = 0;
						while ((offset = fis.read(buf)) != -1) {
							zos.write(buf, 0, offset);
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							if (fis != null)
								fis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

		}

		public boolean unzipToDir(String zipFileName, String outputDir) {
			try {
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Unzip " + zipFileName + " to " + outputDir);
				BufferedOutputStream bos = null;
				FileInputStream fis = new FileInputStream(zipFileName);
				BufferedInputStream bis = new BufferedInputStream(fis);
				ZipInputStream zis = new ZipInputStream(bis);
				ZipEntry entry;
				while ((entry = zis.getNextEntry()) != null) {
					int count;
					byte data[] = new byte[2048];
					Utilities.newInstance()
							.ensureParentsPaths(
									outputDir + File.separator
											+ entry.getName(), false);
					// 2R
					if (entry.isDirectory()) {
						continue;
					} else {
						bos = new BufferedOutputStream(new FileOutputStream(
								new File(outputDir + File.separator,
										entry.getName())));
						while ((count = zis.read(data)) != -1) {
							bos.write(data, 0, count);
						}
						bos.flush();
						bos.close();
					}
				}
				zis.close();

				if (new File(outputDir).listFiles() == null)
					return false;
				else
					return true;
			} catch (Throwable e) {
				e.printStackTrace();
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("Exception *****************");
				return false;
			}
		}
	}

	public static String getTimeStamp(String format) {
		String date = "";
		SimpleDateFormat myfmt = new SimpleDateFormat(
				format == null ? "yyyy-MM-dd(---)hh:mm:ss" : format);
		date = myfmt.format(new java.util.Date()).toString();

		return date;
	}

	public static String getTimeStamp() {
		return getTimeStamp(false, null);
	}

	public static String getTimeStamp(boolean isSimple, String spilitor) {
		StringBuilder stamp = new StringBuilder();
		String dash = (spilitor == null) ? "-" : spilitor;
		int adjust = 0;
		Calendar c = GregorianCalendar.getInstance();
		stamp.append(c.get(Calendar.YEAR));
		stamp.append(dash);
		adjust = c.get(Calendar.MONTH) + 1;
		stamp = (adjust < 10 ? stamp.append(0).append(adjust) : stamp
				.append(adjust));
		stamp.append(dash);
		adjust = c.get(Calendar.DATE);
		stamp = (adjust < 10 ? stamp.append(0).append(adjust) : stamp
				.append(adjust));
		stamp.append(dash);
		adjust = c.get(Calendar.HOUR);
		stamp = (adjust < 10 ? stamp.append(0).append(adjust) : stamp
				.append(adjust));
		stamp.append(dash);
		adjust = c.get(Calendar.MINUTE);
		stamp = (adjust < 10 ? stamp.append(0).append(adjust) : stamp
				.append(adjust));
		if (!isSimple) {
			stamp.append(dash);
			adjust = c.get(Calendar.SECOND);
			stamp = (adjust < 10 ? stamp.append(0).append(adjust) : stamp
					.append(adjust));
			stamp.trimToSize();
		}
		return stamp.toString();
	}

	private Utilities() {

	}

	// for multithread
	public static Utilities newInstance() {
		return new Utilities();
	}

	public boolean saveBitmapToPng(String targetFileFullPath, Bitmap bitmap) {
		try {
			ParcelFileDescriptor fd = ParcelFileDescriptor.open(new File(
					targetFileFullPath), ParcelFileDescriptor.MODE_CREATE
					| ParcelFileDescriptor.MODE_READ_WRITE);
			if (fd == null) {
				if (Debug.MAIN_DEBUG_SWITCH)
					R2.echo("RETURN FALSE");
				return false;
			}
			FileOutputStream fos = null;
			try {
				fos = new ParcelFileDescriptor.AutoCloseOutputStream(fd);
				bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			} finally {
				if (fos != null) {
					fos.close();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
		    return drawableToBitmap(drawable, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }
	}

    /* RK_ID: RK_MEMORY. AUT: liuli1 . DATE: 2013-04-26 . START */
    public static Bitmap drawable2BitmapNoScale(Drawable drawable) {
        try {
			if (drawable instanceof BitmapDrawable) {
			    return ((BitmapDrawable) drawable).getBitmap();
			} else {
			    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable
			            .getIntrinsicHeight(),
			            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
			                    : Bitmap.Config.RGB_565);

			    Canvas canvas = new Canvas(bitmap);
			    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			    drawable.draw(canvas);

			    return bitmap;
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		}
    }

    /* RK_ID: RK_MEMORY. AUT: liuli1 . DATE: 2013-04-26 . END */

	public static Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
	        
		Bitmap bitmap = null;

		try {
			bitmap = Bitmap
					.createBitmap(
							1,
							1,
							drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
									: Bitmap.Config.RGB_565);
			bitmap = Bitmap.createScaledBitmap(bitmap,
					width,
					height, true);

			Canvas canvas = new Canvas(bitmap);

			drawable.setBounds(0, 0, width,
					height);

			drawable.draw(canvas);
		} catch (OutOfMemoryError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bitmap;
	}

	public byte[] codecBytes(byte[] target, boolean codec) {
		if (!codec)
			return target;
		byte[] mix = target;
		for (int i = 0; i < mix.length; i++) {

			if (mix[i] > 0 && mix[i] != Byte.MAX_VALUE) {
				byte tmp = mix[i];
				mix[i] = (byte) (Byte.MAX_VALUE - mix[i]);

				if (mix[i] == Byte.MAX_VALUE || mix[i] == 0
						|| mix[i] == Byte.MIN_VALUE)
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo("codec Fail 0 ! --> " + tmp);

			} else if (mix[i] < 0 && mix[i] != Byte.MIN_VALUE) {

				byte tmp = mix[i];
				mix[i] = (byte) (Byte.MIN_VALUE - mix[i]);

				if (mix[i] == Byte.MAX_VALUE || mix[i] == 0
						|| mix[i] == Byte.MIN_VALUE)
					if (Debug.MAIN_DEBUG_SWITCH)
						R2.echo(" codec Fail 1! --> " + tmp);

			} else if (mix[i] == Byte.MAX_VALUE) {

				mix[i] = Byte.MIN_VALUE;

			} else if (mix[i] == Byte.MIN_VALUE) {

				mix[i] = Byte.MAX_VALUE;

			}
		}

		return mix;
	}

	public String deSuffix(String origin, String suffix) {
		int len = suffix.length();
		if (origin.contains(suffix))
			return origin.substring(0, origin.length() - len);
		else
			return origin;
	}

	public boolean copyFiles(String src, String des) {
		return copyFiles(src, des, null, null);
	}

	public boolean copyFiles(String src, String des, List<File> reservedFiles) {
		return copyFiles(src, des, null, reservedFiles);
	}

	public boolean copyFiles(String src, String des, FileFilter filter) {
		return copyFiles(src, des, filter, null);
	}

	public boolean checkAppInstallState(PackageManager pm, Intent intent) {

		try {
			if (intent == null) {
				return false;
			}

			ResolveInfo ri = pm.resolveActivity(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
			return ri != null;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean copyFiles(String src, String des, FileFilter filter,
			List<File> reservedFiles) {
		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Copy file : " + new File(src).getPath() + " , to " + des);
		ensureParentsPaths(des, true);

		boolean res = true;
		try {
			File file1 = new File(src);
			File[] fs = file1.listFiles(filter);

			File file2 = new File(des);
			if (!file2.exists()) {
				file2.mkdirs();
			}

			for (File f : fs) {
				if (f.isFile()) {

					if (reservedFiles != null) {
						if (Debug.MAIN_DEBUG_SWITCH)
							R2.echo("Touch ---------------------!3");
						boolean except = false;
						for (File ef : reservedFiles) {
							if (Debug.MAIN_DEBUG_SWITCH)
								R2.echo(ef.getPath() + "  &1 +++ "
										+ f.getPath());
							except = (ef.getPath()).equals(f.getPath());
							if (except)
								break;
						}

						if (except)
							continue;
					}

					res = copyFile(f.getPath(),
							des + File.separator + f.getName());
				} else if (f.isDirectory()) {
					if (reservedFiles != null) {
						if (Debug.MAIN_DEBUG_SWITCH)
							R2.echo("Touch ---------------------!4");
						boolean except = false;
						for (File ef : reservedFiles) {
							if (Debug.MAIN_DEBUG_SWITCH)
								R2.echo(ef.getPath() + "  &1 " + f.getPath());
							except = (ef.getPath()).equals(f.getPath());
							if (except)
								break;
						}

						if (except)
							continue;
					}

					res = copyFiles(f.getPath(),
							des + File.separator + f.getName(), reservedFiles);
				}
			}
			res = true;
		} catch (Exception e) {
			e.printStackTrace();
			res = false;
		}

		return res;
	}

	public boolean copyFile(String src, String des) {
		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Copy file 0: " + src + " , to " + des);
		try {
			return copyFile(new FileInputStream(src), des);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean copyFile(InputStream is, String des) {

		BufferedInputStream bis = null;
		FileOutputStream fos = null;

		boolean res = true;

		try {
			byte[] buf = new byte[2048];
			bis = new BufferedInputStream(is);
			fos = new FileOutputStream(des);
			int len = 0;
			while ((len = (bis.read(buf))) != -1) {
				fos.write(buf, 0, len);
			}

			fos.flush();

		} catch (FileNotFoundException e) {
			res = false;
			e.printStackTrace();
		} catch (IOException e) {
			res = false;
			e.printStackTrace();
		} finally {

			try {
				if (bis != null)
					bis.close();
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				res = false;
				e.printStackTrace();
			}

		}

		return res;
	}

	public void deleteFiles(File fileToDelete, boolean withMe) {
		deleteFiles(fileToDelete, withMe, null);
	}

	public void deleteFiles(File fileToDelete, boolean withMe,
			List<File> filesExcepted) {
		// if(Debug.MAIN_DEBUG_SWITCH)
		R2.echo("Will delete : " + fileToDelete.getPath());
		boolean hasExcept = false;
		if (fileToDelete.exists()) {
			if (fileToDelete.isFile()) {

				if (filesExcepted != null) {
					boolean except = false;
					for (File ef : filesExcepted) {
						except = (ef.getPath()).equals(fileToDelete.getPath());
						// R5.echo("Except file in delete is  : " +
						// fileToDelete.getPath() + "  , " + except);
						if (except) {
							hasExcept = true;
							break;
						}
					}

					if (!except)
						fileToDelete.delete();
				}
			} else if (fileToDelete.isDirectory()) {
				File files[] = fileToDelete.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFiles(files[i], true, filesExcepted);
				}
			}

			if (withMe && !hasExcept)
				fileToDelete.delete();
		}
	}

	public Bitmap getApplicationIconToBitmap(PackageManager pm,
			String packageName) {
		try {
			BitmapDrawable bd = (BitmapDrawable)pm.getApplicationIcon(packageName);
			return bd.getBitmap();
		} catch (Exception e) {
			return null;
		}

	}

	public static boolean isSdcardAvalible() {
		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("sccard state is : "
					+ Environment.getExternalStorageState());
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	public long getAvalibleSpace(File representor) {

		StatFs statFs = new StatFs(representor.getPath());

		long blocSize = statFs.getBlockSize();

		long availaBlock = statFs.getAvailableBlocks();

		long availableSpare = availaBlock * blocSize;

		return availableSpare;
	}

	public boolean isFreeSpaceEnough(File representor, long needSpace) {
		long free = getAvalibleSpace(representor);
		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Free space is : " + free + "  , need : " + needSpace);
		return free > needSpace;
	}

	public boolean ensureParentsPaths(String pathTo, boolean withMe) {
		return ensureParentsPaths(pathTo, withMe, false);
	}

	public boolean ensureParentsPaths(String pathTo, boolean withMe,
			boolean isReadCheck) {
		try {
			if (pathTo == null)
				return false;

			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Ensure parents of : " + pathTo + " , withMe is : "
						+ withMe);

			File f = new File(pathTo);
			List<File> dirs = new ArrayList<File>();

			if (withMe && !f.exists()) {
				dirs.add(f);
			}

			while (!f.exists()) {
				f = f.getParentFile();
				dirs.add(f);
			}

			if (!dirs.isEmpty()) {
				for (int i = dirs.size() - 1; i >= 0; i--) {
					if (!(f = dirs.get(i)).exists()
							|| (f.exists() && f.isFile())) {
						f.delete();

						if (isFreeSpaceEnough(f.getParentFile(),
								ConstantAdapter.SDCARD_FREE_BEFORE_SPACE / 10)) {
							if (Debug.MAIN_DEBUG_SWITCH)
								R2.echo("Now create :  " + f.getPath());
							f.mkdir();
						} else {
							// ProcessIndicator.getInstance(Launcher.getInstance()).setState(
							// R.string.operation_failed_by_no_free_space);
							if (Debug.MAIN_DEBUG_SWITCH)
								R2.echo("Can not create :  " + f.getPath());
							return false;
						}
					}
				}
			} else {

				if (isReadCheck) {
					return true;
				} else {
					boolean res = isFreeSpaceEnough(
							new File(pathTo).getParentFile(),
							ConstantAdapter.SDCARD_FREE_BEFORE_SPACE / 10);
					if (res) {
						return true;
					} else {
						// ProcessIndicator.getInstance(Launcher.getInstance()).setState(
						// R.string.operation_failed_by_no_free_space);
						if (Debug.MAIN_DEBUG_SWITCH)
							R2.echo("Can not write :  " + f.getPath());
						return false;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public byte[] bitmap2ByteArray(Bitmap bmp) {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (baos != null) {
				try {
					baos.flush();
					baos.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public Bitmap bitmapStampForWidgetWithSnap(Bitmap bottom, Bitmap stamp) {
		try {
			if( bottom == null ){
				return stamp;
			}
			// extract gapEY
			int gapEY = 0;
			for (int j = bottom.getWidth() / 2 - 1; j < bottom.getWidth(); j++) {

				int gapY = 0;

				for (int i = 0; i < bottom.getHeight() / 2; i++) {
					int pixel = bottom.getPixel(j, i);
					int red = (pixel >> 16) & 0xFF;
					int green = (pixel >> 8) & 0xFF;
					int blue = pixel & 0xFF;
					int alpha = pixel >>> 24;

					if (alpha < 100 && red < 100 && green < 100 && blue < 100) {
						gapY++;
					} else {
						if (gapEY == 0 && gapY > 0) {
							gapEY = gapY;
						}
						break;
					}
				}

				if (gapEY > gapY) {
					gapEY = gapY;
					gapY = 0;
				}
			}

			// extract gapEX
			int gapEX = 0;
			for (int j = bottom.getHeight() / 2 - 1; j < bottom.getHeight(); j++) {

				int gapX = 0;

				for (int i = bottom.getWidth() - 1; i > bottom.getWidth() / 2; i--) {
					int pixel = bottom.getPixel(i, j);

					int red = (pixel >> 16) & 0xFF;
					int green = (pixel >> 8) & 0xFF;
					int blue = pixel & 0xFF;
					int alpha = pixel >>> 24;

					if (alpha < 100 && red < 100 && green < 100 && blue < 100) {
						gapX++;
					} else {
						if (gapEX == 0 && gapX > 0) {
							gapEX = gapX;
						}
						break;
					}
				}

				if (gapEX > gapX) {
					gapEX = gapX;
					gapX = 0;
				}
			}

			//
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Gap : " + gapEX + " , " + gapEY);
			int sW, sH;
			if(stamp == null){
				sW = 0;
				sH  = 0;
			}else{
				 sW = stamp.getWidth();
				 sH = stamp.getHeight();				
			}
			int bW = bottom.getWidth();
			int bH = bottom.getHeight();
			Canvas canvas = new Canvas(bottom);
			Paint paint = new Paint();
			int seedBH = bH - 2 * gapEY;
			int seedBW = bW - 2 * gapEX;

			float factor = 0;
			if(sW != 0 && sH !=0){
				 factor = (float) sH / (float) sW;
			}
			Log.i("ss","---- factor is " + factor);
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("Factor : " + factor);

			if(factor != 0){
				try {
					if (seedBW < sW && seedBH > sH) {
	
						stamp = Bitmap.createScaledBitmap(stamp, sW = seedBW + 1,
								sH = (int) (seedBW * factor), true);
	
					} else if (seedBW < sW && seedBH < sH) {
	
						stamp = Bitmap.createScaledBitmap(stamp, sW = seedBW + 1,
								sH = (int) (seedBW * factor), true);
	
					} else if (seedBW > sW && seedBH < sH) {
	
						stamp = Bitmap.createScaledBitmap(stamp,
								sW = (int) (seedBH / factor) + 1, sH = seedBH, true);
	
					}
	
					stamp = Bitmap.createScaledBitmap(stamp, sW = stamp.getWidth() + 1,
							sH = stamp.getHeight() + 1, true);
	
					canvas.drawBitmap(stamp, bW - sW - gapEX + 2, gapEY - 2, paint);
	
					stamp.recycle();
					stamp = null;
	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return bottom;
		} catch (OutOfMemoryError e) {
			return null;
		}
	}

	public boolean checkAppInstallState(PackageManager pm, ComponentName cn) {
		Intent intent = new Intent();
		intent.setComponent(cn);
		ResolveInfo ri = pm.resolveActivity(intent,
				PackageManager.MATCH_DEFAULT_ONLY);
		return ri != null;
	}

	public String dumpRawOrAssetsToFile(final Context context, String from,
			String toPathAndName) {
		AssetManager am = context.getResources().getAssets();
		try {

			ensureParentsPaths(toPathAndName, false);

			File f = new File(toPathAndName);
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();

			boolean res = copyFile(am.open(from), toPathAndName);
			if (Debug.MAIN_DEBUG_SWITCH)
				R2.echo("File copy res is &&&&&&&&&&&&&&&&&&&&: " + res);
			return toPathAndName;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return "";
	}

//	    private static final String TAG = "Launcher.Utilities";

//	    private static final boolean TEXT_BURN = false;

	    private static int sIconWidth = -1;
	    private static int sIconHeight = -1;
	    //bugfix 11311
	   // private static int sIconTextureWidth = -1;
	   // private static int sIconTextureHeight = -1;
	    private static int sIconSWidth = -1;
	    private static int sIconSHeight = -1;

	    private static final Paint sBlurPaint = new Paint();
	    private static final Paint sGlowColorPressedPaint = new Paint();
	    private static final Paint sGlowColorFocusedPaint = new Paint();
	    private static final Paint sDisabledPaint = new Paint();
	    private static final Paint sIconPaint = new Paint();
	    private static final Rect sOldBounds = new Rect();
	    private static final Canvas sCanvas = new Canvas();

	    private static final int WITHOUT_ICON_STYLE = -1;
	    public static final boolean FLAG_DRAWABLE_PADDING = true;
	    public static final int ICON_STYLE_COUNT = 7;//31;
		/** AUT: henryyu1986@163.com DATE: 2011-12-20 */
	    // add lenovo background and chita background by liuli1
	    /*** MODIFYBY: zhaoxy . DATE: 2012-04-05 . START***/
	    //the resources of icon style will be found by rule name.
//	    public static int[] sBgRes = { R.drawable.icon_overlay, R.drawable.appbg_preview, R.drawable.appbg_1, R.drawable.appbg_2, R.drawable.appbg_3, R.drawable.appbg_4, R.drawable.appbg_5, R.drawable.appbg_6,
//	            R.drawable.appbg_7, R.drawable.appbg_8, R.drawable.appbg_9, R.drawable.appbg_10, R.drawable.appbg_11, R.drawable.appbg_12, R.drawable.appbg_13,
//	            R.drawable.appbg_14, R.drawable.appbg_15, R.drawable.appbg_16, R.drawable.appbg_17, R.drawable.appbg_18, R.drawable.appbg_19, R.drawable.appbg_20};
//	    public static int[] sFgRes = { -1, -1, -1, R.drawable.appfg_2, -1, -1, -1, -1, R.drawable.appfg_7, R.drawable.appbg_8, -1, -1,
//	    		-1, -1, -1, -1, -1, -1, -1, R.drawable.appfg_18, -1, -1};
//	    private static Bitmap[] sBgBitmap = null;
//	    private static Bitmap[] sFgBitmap = null;
	    /*** MODIFYBY: zhaoxy . DATE: 2012-04-05 . END***/
	    private static int[] sLeBgRes = { R.drawable.appbg_yellow, R.drawable.appbg_blue, R.drawable.appbg_green,
	            R.drawable.appbg_purple };
	    private static Bitmap[] sLeBgBitmap = null;
	    static int sBgIndex = 0;
		/** AUT: henryyu1986@163.com DATE: 2011-12-20 */

	    static {
	        sCanvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG,
	                Paint.FILTER_BITMAP_FLAG));
	    }
	    static int sColors[] = { 0xffff0000, 0xff00ff00, 0xff0000ff };
	    static int sColorIndex = 0;

	    /**
	     * Returns a bitmap suitable for the all apps view. Used to convert pre-ICS
	     * icon bitmaps that are stored in the database (which were 74x74 pixels at hdpi size)
	     * to the proper size (48dp)
	     */
	    public static Bitmap createIconBitmap(Bitmap icon, Context context) {
	        int textureWidth = sIconWidth;
	        int textureHeight = sIconHeight;
	        int sourceWidth = icon.getWidth();
	        int sourceHeight = icon.getHeight();
	        if (sourceWidth > textureWidth && sourceHeight > textureHeight) {
	            // Icon is bigger than it should be; clip it (solves the GB->ICS migration case)
	        	return  lessenBitmap(icon, textureWidth, textureWidth, false);
	        } else if (sourceWidth == textureWidth && sourceHeight == textureHeight) {
	            // Icon is the right size, no need to change it
	            return icon;
	        } else {
	            // Icon is too small, render to a larger bitmap
//	            return createIconBitmap(new BitmapDrawable(icon), context);
	            /* RK_ID: RK_ICONSTYLE. AUT: liuli1 . DATE: 2012-05-24 . START */
	            // fix bug 165051
	            return createIconBitmapWithoutStyle(new BitmapDrawable(icon), context);
	            /* RK_ID: RK_ICONSTYLE. AUT: liuli1 . DATE: 2012-05-24 . END */
	        }
	    }

	    /** AUT: henryyu1986@163.com DATE: 2011-12-27 */
	    /**
	     * Returns a bitmap suitable for the all apps view.
	     */
	    public static Bitmap createIconBitmap(Drawable icon, Context context) {
//	    	SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//	        int index = sharedPreferences.getInt(IconStyleSettings.PREF_ICON_BG_STYLE, 0);
	        int index = SettingsValue.getIconStyleIndex(context);
	    	return createIconBitmap(icon, index, context);
	    }
	    /** AUT: henryyu1986@163.com DATE: 2011-12-27 */

	    /* AUT: liuli liuli0120@use.com.cn DATE: 2011-12-20 START */
	    public static final String DEFAULT_PACKAGE = "default";

	   /* public static Bitmap drawableToBitmap(Drawable drawable) {
	        int w = drawable.getIntrinsicWidth();
	        int h = drawable.getIntrinsicHeight();

	        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
	                : Bitmap.Config.RGB_565;
	        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
	        Canvas canvas = new Canvas(bitmap);
	        drawable.setBounds(0, 0, w, h);
	        drawable.draw(canvas);
	        return bitmap;
	    }*/

	    // add lenovo background and chita background by liuli1
	    private static Random mRandom = new Random();
		/* AUT: kangwei3 DATE: 2012-11-14 START */
	    static public Bitmap createIconBitmap(Drawable icon, Context context, String pkgName) {
//	        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//	        int index = sharedPreferences.getInt(IconStyleSettings.PREF_ICON_BG_STYLE, 0);
	        int index = SettingsValue.getIconStyleIndex(context);
	        if (index == 1) {
	            // this index is owned by lenovo background
	            if (sLeBgBitmap == null) {
	                sLeBgBitmap = new Bitmap[sLeBgRes.length];
	                for (int i = 0; i < sLeBgRes.length; i++) {
	                    sLeBgBitmap[i] = BitmapFactory.decodeResource(context.getResources(), sLeBgRes[i]);
	                }
	            }
	            int length = pkgName.length()%sLeBgRes.length;
	            return createIconBitmap(icon, sLeBgBitmap[length], context);
	        }
	        // other case, normal steps
	        return createIconBitmap(icon, index, context);
	    }

	    public static Bitmap createIconBitmapWithoutStyle(Drawable icon, Context context) {
	        return createIconBitmap(icon, WITHOUT_ICON_STYLE, context);
	    }

	    public static Bitmap createIconBitmap(Drawable icon, int index, Context context) {
	        if (index == 1) {
	            // this index is owned by lenovo background
	            if (sLeBgBitmap == null) {
	                sLeBgBitmap = new Bitmap[sLeBgRes.length];
	                for (int i = 0; i < sLeBgRes.length; i++) {
	                    sLeBgBitmap[i] = BitmapFactory.decodeResource(context.getResources(), sLeBgRes[i]);
	                }
	            }
	            int length = mRandom.nextInt(sLeBgRes.length);//Math.abs(mRandom.nextInt()) % sLeBgRes.length;
	            return createIconBitmap(icon, sLeBgBitmap[length], context);
	        }
	        /*** MODIFYBY: zhaoxy . DATE: 2012-04-05 . START***/
	        /*if (sBgBitmap == null) {
	            sBgBitmap = new Bitmap[sBgRes.length];
	            sFgBitmap = new Bitmap[sFgRes.length];
	            for (int i = 0; i < sBgRes.length; i++) {
	            	if (sBgRes[i] != -1) {
	            		sBgBitmap[i] = BitmapFactory.decodeResource(context.getResources(), sBgRes[i]);
	            		
	            		if (sBgBitmap[i].getWidth() != sIconWidth) {
	                        sBgBitmap[i] = lessenBitmap(sBgBitmap[i], sIconWidth, sIconHeight, true);
	                    }
					} else {
						sBgBitmap[i] = null;
					}
	                
	                if (sFgRes[i] == -1) {
	                    sFgBitmap[i] = null;
	                } else {
	                    sFgBitmap[i] = BitmapFactory.decodeResource(context.getResources(), sFgRes[i]);
	                }
	                if (sFgBitmap[i] != null && sFgBitmap[i].getWidth() != sIconWidth) {
	                    sFgBitmap[i] = lessenBitmap(sFgBitmap[i], sIconWidth, sIconHeight, true);
	                }
	            }
	        }*/
	        
	        

	        if (index == WITHOUT_ICON_STYLE) {
	            return createIconBitmap(icon, null, null, context);
	        } else if (index >= 0 && index < ICON_STYLE_COUNT){
	    	    /***RK_ID:RK_SQURE_ICONSTYLE AUT:zhanglz1@lenovo.com DATE: 2013-04-12 S***/ 
	        	Bitmap a = null;
	        	for(int i = 0;i<SettingsValue.SQUARE_BG.length;i++){
					if(index == SettingsValue.SQUARE_BG[i]) 
	        			a= getIconStyleImageSqure(icon, index, context);
	        		else continue;
	        	}
	        	if(a !=null){
	        		return a;
	        	}else{
	        		return createIconBitmap(index, icon, context);
	        	}
	    	    /***RK_ID:RK_SQURE_ICONSTYLE AUT:zhanglz1@lenovo.com DATE: 2013-04-12 E***/ 
	        } else if (index == SettingsValue.THEME_ICON_BG_INDEX) { 
	        	Bitmap[] iconBg = SettingsValue.getThemeIconBg();
	        	return createIconBitmap(icon, iconBg[0], iconBg[1], iconBg[2], context);
	        } else {
//	            return null;
	            return createIconBitmap(icon, null, null, context);
	        }
	        /*** MODIFYBY: zhaoxy . DATE: 2012-04-05 . END***/
	    }   
	    
	    /***RK_ID:RK_SQURE_ICONSTYLE AUT:zhanglz1@lenovo.com DATE: 2013-04-12 S***/ 
	    static Bitmap getIconStyleImageSqure(Drawable icon, int n,/*Bitmap bg,*/ Context context) {
	        /*synchronized (sCanvas) {
	            if (sIconWidth == -1 || sIconWidth != SettingsValue.getIconSizeValue(context)) {
	                initStatics(context);
	            }
	            ICON_SIZE = SettingsValue.getIconSizeValue(context);
	            sIconWidth = sIconHeight = ICON_SIZE;
	            Bitmap icon1 = null;
	            int width = sIconSWidth;
	            int height = sIconSHeight;
	            if (icon instanceof BitmapDrawable) {
	                BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
	                Bitmap bitmap = bitmapDrawable.getBitmap();
	                if (bitmap !=null && bitmap.getDensity() == Bitmap.DENSITY_NONE) {
	                    bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
	                }
	            }
	            int iconWidth = icon.getIntrinsicWidth();
	            int iconHeight = icon.getIntrinsicHeight();
	            icon.setBounds(0, 0, sIconSWidth, sIconSWidth);
	            DisplayMetrics dm =context.getApplicationContext().getResources().getDisplayMetrics();  
	     		float mDeviceDensity = dm.density;
	     		int temp = Math.round(ICON_SIZE/mDeviceDensity);
	     		String[] keys = SettingsValue.ICON_SIZE_VALUES;
	     		//normal
	     		if (temp == Integer.parseInt(keys[0])) {
	     			
	     		} //small
	     		else if (temp == Integer.parseInt(keys[1])) {
	                //等比例进行比较 此时width已经根据设置的图标大小改变了
	     			float scale = (float)Integer.parseInt(keys[1]) / (float)Integer.parseInt(keys[0]);
	     			iconWidth  = Math.round(iconWidth * scale);
	     			iconHeight = Math.round(iconHeight * scale);
	     		}
	            if (width > 0 && height > 0) {
	                if (width < iconWidth || height < iconHeight) {
	                    final Bitmap.Config c = icon.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
	                            : Bitmap.Config.RGB_565;
	                    final Bitmap thumb = Bitmap.createBitmap(sIconSWidth, sIconSHeight, c);
	                    final Canvas canvas = sCanvas;
	                    canvas.setBitmap(thumb);
	                    icon.draw(canvas);

	                    LenovoIconConvert convert = LenovoIconConvert.getInstance();

	                    if(!convert.isCirle(context, thumb)){
	                    	return createIconBitmapSqure(n, icon,context);
	                    }
	                } else if (iconWidth < width && iconHeight < height) {
	                    Bitmap.Config c = Bitmap.Config.ARGB_8888;
	                    Bitmap thumb = Bitmap.createBitmap(width, height, c);
	                    final Canvas canvas = sCanvas;
	                    canvas.setBitmap(thumb);
	                    canvas.save();

	                    icon.draw(canvas);
	                    canvas.restore();
	                    icon.setBounds(sOldBounds);

	                    LenovoIconConvert convert = LenovoIconConvert.getInstance();
	                    if(!convert.isCirle(context, thumb)){
	                    	return createIconBitmapSqure(n, icon,context);
	                    }
	                } else {
	                    Bitmap.Config c = Bitmap.Config.ARGB_8888;
	                    Bitmap thumb = Bitmap.createBitmap(sIconSWidth, sIconSHeight, c);
	                    final Canvas canvas = sCanvas;
	                    canvas.setBitmap(thumb);

	                    icon.draw(canvas);
	                    icon.setBounds(sOldBounds);

	                    LenovoIconConvert convert = LenovoIconConvert.getInstance();
	                    if(!convert.isCirle(context, thumb)){
	                    	
	                    }
	                }
	            }
	            return null;
	        }*/
	    	return createIconBitmapSqure(n, icon,context);
	    }
	public static Bitmap createIconBitmapSqure(int n, Drawable icon,
			Context context) {
		try {
			synchronized (sCanvas) { // we share the statics :-(
				Bitmap bg = null;
				Bitmap fg = null;
				Bitmap mask = null;
				if (sIconWidth == -1
						/*|| sIconWidth != SettingsValue.getIconSizeValue(context)*/) {
					initStatics(context);
				}

				if (n == 6) {
//					Random mRandom1 = new Random();
//					int n1 = mRandom1.nextInt(5);
//					bg = getBitmap(context, "default_ic_style_" + n + "_" + n1
//							+ "_bg");
					bg = getBitmap(context, "default_ic_style_" + n + "_0" + "_bg");
				} else if (n == 5) {
//				Random mRandom1 = new Random();
//				int n1 = mRandom1.nextInt(8);
//				bg = getBitmap(context, "default_ic_style_" + n + "_" + n1
//						+ "_bg");
					bg = getBitmap(context, "default_ic_style_" + n + "_0" + "_bg");
				}else {
					bg = getBitmap(context, "default_ic_style_" + n + "_bg");
				}
				fg = getBitmap(context, "default_ic_style_" + n + "_fg");
				mask = getBitmap(context, "default_ic_style_" + n + "_mask");
				
			//	mask = null;
			//	fg = null;
				// test 去掉S
				int width = (int) (sIconWidth);
				int height = (int) (sIconHeight);
				if (icon == null) {
					icon = context.getResources().getDrawable(R.drawable.nothing);
				}
				if (icon instanceof PaintDrawable) {
					PaintDrawable painter = (PaintDrawable) icon;
					painter.setIntrinsicWidth(width);
					painter.setIntrinsicHeight(height);
				} else if (icon instanceof BitmapDrawable) {
					// Ensure the bitmap has a density.
					BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
					Bitmap bitmap = bitmapDrawable.getBitmap();
					if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
						bitmapDrawable.setTargetDensity(context.getResources()
								.getDisplayMetrics());
					}
				}
				int sourceWidth = icon.getIntrinsicWidth();
				int sourceHeight = icon.getIntrinsicHeight();
				Bitmap lessBitmap = null;

				if (sourceWidth > 0 && sourceHeight > 0) {
					final float ratio = (float) sourceWidth / sourceHeight;
					if (sourceWidth > sourceHeight) {
						height = (int) (width / ratio);
					} else if (sourceHeight > sourceWidth) {
						width = (int) (height * ratio);
					}
					Resources res = context.getResources();
					if (icon instanceof FastBitmapDrawable) {
						lessBitmap = lessenBitmap(
								((FastBitmapDrawable) icon).getBitmap(), width,
								height, false);
						icon = new BitmapDrawable(res, lessBitmap);
					} else if (icon instanceof BitmapDrawable) {
						lessBitmap = lessenBitmap(
								((BitmapDrawable) icon).getBitmap(), width, height,
								false);
						icon = new BitmapDrawable(res, lessBitmap);
					} else {
						lessBitmap = lessenBitmap(drawableToBitmap(icon), width,
								height, false);
						icon = new BitmapDrawable(res, lessBitmap);
					}
				}

				// no intrinsic size --> use default size
				int textureWidth = //sIconTextureWidth;
			        		sIconWidth;
			    int textureHeight =// sIconTextureHeight;
			    		    sIconHeight;
			    //bugfix zhanglz1
			    /*if (FLAG_DRAWABLE_PADDING) {
			        textureWidth += 2;
			        textureHeight += 2;
			    }*/

				final Bitmap bitmap = Bitmap.createBitmap(textureWidth,
						textureHeight, Bitmap.Config.ARGB_8888);
				final Canvas canvas = sCanvas;
				canvas.setBitmap(bitmap);

				final int left = (textureWidth - width) / 2;
				final int top = (textureHeight - height) / 2;

				sOldBounds.set(icon.getBounds());
				icon.setBounds(left, top, left + width, top + height);
				BitmapDrawable bd = (BitmapDrawable) icon;
				Bitmap bm = bd.getBitmap();
				canvas.drawBitmap(bm, left, top, null);
				if (mask != null) {
					if (mask.getWidth() != textureWidth
							|| mask.getHeight() != textureHeight) {
						mask = Bitmap.createScaledBitmap(mask, textureWidth,
								textureHeight, true);
					}
					sIconPaint.setXfermode(new PorterDuffXfermode(
							PorterDuff.Mode.DST_IN));
					canvas.drawBitmap(mask, 0f, 0f, sIconPaint);
				}
				icon.setBounds(sOldBounds);
				if (bg != null) {
					if (bg.getWidth() != textureWidth
							|| bg.getHeight() != textureHeight) {
						bg = Bitmap.createScaledBitmap(bg, textureWidth,
								textureHeight, true);
					}
					sIconPaint.setXfermode(new PorterDuffXfermode(
							PorterDuff.Mode.DST_OVER));
					canvas.drawBitmap(bg, 0f, 0f, sIconPaint);
				}
				if (fg != null) {
					if (fg.getWidth() != textureWidth
							|| fg.getHeight() != textureHeight) {
						fg = Bitmap.createScaledBitmap(fg, textureWidth,
								textureHeight, true);
					}
					canvas.drawBitmap(fg, 0f, 0f, null);
				}
				canvas.setBitmap(null);

				return bitmap;
			}
		} catch (OutOfMemoryError e) {
			return null;
		}
	}
    /***RK_ID:RK_SQURE_ICONSTYLE AUT:zhanglz1@lenovo.com DATE: 2013-04-12 E***/  

		private static int ICON_SIZE = 72;
	    static Bitmap createIconBitmap(Drawable icon, Bitmap bg, Context context) {
	        try {
				synchronized (sCanvas) {
				    if (sIconWidth == -1 /*|| sIconWidth != SettingsValue.getIconSizeValue(context)*/) {
				        /*** RK_ID: ICON_STYLE.  AUT: zhaoxy . DATE: 2012-07-18 . START***/
				        // sIconWidth = sIconHeight = app_icon_size;
				        /*** RK_ID: ICON_STYLE.  AUT: zhaoxy . DATE: 2012-07-18 . END***/
				        //sIconWidth = sIconHeight = ICON_SIZE;
				        initStatics(context);
				    }
//	            Random mC = new Random();
//	            int iconC = mC.nextInt(6);
				    ICON_SIZE = SettingsValue.getIconSizeValue(context);
				    sIconWidth = sIconHeight = ICON_SIZE;
				    Bitmap icon1 = null;
				    int width = sIconSWidth;
				    int height = sIconSHeight;
				    if (icon instanceof BitmapDrawable) {
				        // Ensure the bitmap has a density.
				        BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
				        Bitmap bitmap = bitmapDrawable.getBitmap();
				        if (bitmap !=null && bitmap.getDensity() == Bitmap.DENSITY_NONE) {
				            bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
				        }
				    }
				    int iconWidth = icon.getIntrinsicWidth();
				    int iconHeight = icon.getIntrinsicHeight();
				    icon.setBounds(0, 0, sIconSWidth, sIconSWidth);
					/*PK_ID: SONAR_VARIABLE_NOT_USE AUTH:GECN1 DATE;2013.1.24 S*/
				    DisplayMetrics dm =context.getApplicationContext().getResources().getDisplayMetrics();  
					/*PK_ID: SONAR_VARIABLE_NOT_USE AUTH:GECN1 DATE;2013.1.24 E*/
					float mDeviceDensity = dm.density;
					int temp = Math.round(ICON_SIZE/mDeviceDensity);
					String[] keys = SettingsValue.RES_ICON_SIZE_VALUES;
					//normal
					if (temp == Integer.parseInt(keys[0])) {
						
					} //small
					else if (temp == Integer.parseInt(keys[1])) {
				        //等比例进行比较 此时width已经根据设置的图标大小改变了
						float scale = (float)Integer.parseInt(keys[1]) / (float)Integer.parseInt(keys[0]);
						iconWidth  = Math.round(iconWidth * scale);
						iconHeight = Math.round(iconHeight * scale);
						
						//iconWidth  = Math.round(iconWidth * Integer.parseInt(keys[1]) / Integer.parseInt(keys[0]));
						//iconHeight = Math.round(iconHeight * Integer.parseInt(keys[1]) / Integer.parseInt(keys[0]));
						
					}
				    if (width > 0 && height > 0) {
				        if (width < iconWidth || height < iconHeight) {
//	                    final float ratio = (float) iconWidth / iconHeight;
//	                    if (iconWidth > iconHeight) {
//	                        height = (int) (width / ratio);
//	                    } else if (iconHeight > iconWidth) {
//	                        width = (int) (height * ratio);
//	                    }

				            final Bitmap.Config c = icon.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				                    : Bitmap.Config.RGB_565;
				            final Bitmap thumb = Bitmap.createBitmap(sIconSWidth, sIconSHeight, c);
				            final Canvas canvas = sCanvas;
				            canvas.setBitmap(thumb);
				            // Copy the old bounds to restore them later
				            // If we were to do oldBounds = icon.getBounds(),
				            // the call to setBounds() that follows would
				            // change the same instance and we would lose the
				            // old bounds
				            icon.draw(canvas);

				            ImageConvert convert = LenovoIconConvert.getInstance();
//	                    Bitmap bitmapTemp = convert.convert(context, thumb, iconC);
				            icon1 = convert.convert(context, thumb, bg);
				            if (icon1 == null) {
				                icon1 = createIconBitmap(icon, null, null, context);
				            }
//	                    Bitmap bitmapThumb = Bitmap.createScaledBitmap(bitmapTemp, ICON_SIZE, ICON_SIZE, true);
//	                    icon1 = bitmapThumb;
//	                    icon1 = Bitmap.createScaledBitmap(bitmapTemp, ICON_SIZE, ICON_SIZE, true);
				        } else if (iconWidth < width && iconHeight < height) {
				            Bitmap.Config c = Bitmap.Config.ARGB_8888;
				            Bitmap thumb = Bitmap.createBitmap(width, height, c);
				            final Canvas canvas = sCanvas;
				            canvas.setBitmap(thumb);
				            canvas.save();

				            icon.draw(canvas);
				            canvas.restore();
				            icon.setBounds(sOldBounds);

				            ImageConvert convert = LenovoIconConvert.getInstance();
//	                    Bitmap bitmapTemp = convert.convert(context, thumb, iconC);
//	                    Bitmap bitmapThumb = Bitmap.createScaledBitmap(bitmapTemp, ICON_SIZE, ICON_SIZE, true);
//	                    icon1 = bitmapThumb;
				            icon1 = convert.convert(context, thumb, bg);
				            if (icon1 == null) {
				                icon1 = createIconBitmap(icon, null, null, context);
				            }
//	                    icon1 = Bitmap.createScaledBitmap(bitmapTemp, ICON_SIZE, ICON_SIZE, true);
				        } else {
				            Bitmap.Config c = Bitmap.Config.ARGB_8888;
				            Bitmap thumb = Bitmap.createBitmap(sIconSWidth, sIconSHeight, c);
				            final Canvas canvas = sCanvas;
				            canvas.setBitmap(thumb);

				            icon.draw(canvas);
				            icon.setBounds(sOldBounds);

				            ImageConvert convert = LenovoIconConvert.getInstance();
//	                    Bitmap bitmapTemp = convert.convert(context, thumb, iconC);
//	                    Bitmap bitmapThumb = Bitmap.createScaledBitmap(bitmapTemp, ICON_SIZE, ICON_SIZE, true);
//	                    icon1 = bitmapThumb;
				            icon1 = convert.convert(context, thumb, bg);
				            if (icon1 == null) {
				                icon1 = createIconBitmap(icon, null, null, context);
				            }

//	                    icon1 = Bitmap.createScaledBitmap(bitmapTemp, ICON_SIZE, ICON_SIZE, true);
				        }
				    }
				    
				    return icon1;
				}
			} catch (OutOfMemoryError e) {
				return null;
			}
	    }
	    /* AUT: liuli liuli0120@use.com.cn DATE: 2011-12-20 END */

	/** AUT: henryyu1986@163.com DATE: 2011-12-20 */
	    public static Bitmap createIconBitmap(Drawable icon, Bitmap bg, Bitmap fg, Context context) {
	        try {
				synchronized (sCanvas) { // we share the statics :-(
				    if (sIconWidth == -1/*|| sIconWidth != SettingsValue.getIconSizeValue(context)*/) {
				        initStatics(context);
				    }

				    int width = sIconSWidth;
				    int height = sIconSHeight;
//	            if(index == -1) {
				    /*** MODIFYBY: zhaoxy . DATE: 2012-04-05 . START***/
				    if(bg == null && fg == null) {
				    	width = sIconWidth;
				        height = sIconHeight;
				    }
				    /*** MODIFYBY: zhaoxy . DATE: 2012-04-05 . END***/

				    if (icon instanceof PaintDrawable) {
				        PaintDrawable painter = (PaintDrawable) icon;
				        painter.setIntrinsicWidth(width);
				        painter.setIntrinsicHeight(height);
				    } else if (icon instanceof BitmapDrawable) {
				        // Ensure the bitmap has a density.
				        BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
				        Bitmap bitmap = bitmapDrawable.getBitmap();
				        Log.d("a","getWidth ="+bitmap.getWidth());
				        Log.d("a","getHeight ="+bitmap.getHeight());
				        if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
				            bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
				        }
				    }
				    
//	            int sourceWidth = icon.getIntrinsicWidth();
//	            int sourceHeight = icon.getIntrinsicHeight();

				    /*** MODIFYBY: zhaoxy . DATE: 2012-04-05 . START***/
				    /*if (sourceWidth > 0 && sourceHeight > 0) {
				        // There are intrinsic sizes.
//	                if (width < sourceWidth || height < sourceHeight) {
				            // It's too big, scale it down.
				            final float ratio = (float) sourceWidth / sourceHeight;
				            if (sourceWidth > sourceHeight) {
				                height = (int) (width / ratio);
				            } else if (sourceHeight > sourceWidth) {
				                width = (int) (height * ratio);
				            }
				            Resources res = context.getResources();
				            if(icon instanceof FastBitmapDrawable) {
				                icon = new BitmapDrawable(res, lessenBitmap(((FastBitmapDrawable) icon).getBitmap(), width, height, false));
				            } else {
				                icon = new BitmapDrawable(res, lessenBitmap(((BitmapDrawable) icon).getBitmap(), width, height, false));
				            }
//	                } else if (sourceWidth < width && sourceHeight < height) {
//	                    // Don't scale up the icon
//	                    width = sourceWidth;
//	                    height = sourceHeight;
//	                }
				    }*/
				    /*** MODIFYBY: zhaoxy . DATE: 2012-04-05 . END***/

				    // no intrinsic size --> use default size
				    int textureWidth = //sIconTextureWidth;
				    		sIconWidth;
				    int textureHeight =// sIconTextureHeight;
				    		sIconHeight;
/*	            if (bg == null && fg == null && FLAG_DRAWABLE_PADDING) {
				        // added by liuli1, for bug 7079.
				        textureWidth += 2;
				        textureHeight += 2;
				    }*/

				    final Bitmap bitmap = Bitmap.createBitmap(textureWidth, textureHeight,
				            Bitmap.Config.ARGB_8888);
				    final Canvas canvas = sCanvas;
				    canvas.setBitmap(bitmap);

				    final int left = (textureWidth-width) / 2;
				    final int top = (textureHeight-height) / 2;

				    /** AUT: henryyu1986@163.com DATE: 2011-12-20 */
//	            if (false) {
//	                // draw a big box for the icon for debugging
////	                canvas.drawColor(sColors[sColorIndex]);
////	                if (++sColorIndex >= sColors.length) sColorIndex = 0;
//	                Paint debugPaint = new Paint();
////	                debugPaint.setColor(0xffcccc00);
//	                debugPaint.setStyle(Paint.Style.STROKE);
//	                debugPaint.setColor(sColors[sColorIndex]);
//	                debugPaint.setStrokeWidth(0.1f);
//	                if (++sColorIndex >= sColors.length) sColorIndex = 0;
////	                canvas.drawRect(left, top, left+width, top+height, debugPaint);
//	                canvas.drawCircle(sIconWidth / 2, sIconWidth / 2, sIconWidth / 2, debugPaint);
//	            }
//
//	            if (true) {
				        // draw a big box for the icon for debugging
//	                canvas.drawColor(sColors[sColorIndex]);
//	                if (++sColorIndex >= sColors.length) sColorIndex = 0;
//	                Paint debugPaint = new Paint();
//	                debugPaint.setColor(0xffcccc00);
//	                canvas.drawRect(left, top, left+width, top+height, debugPaint);

//	            	if(index != -1) {
				        if (bg != null) {
				            canvas.drawBitmap(bg, 0, 0, null);
				        }
//	            }
				    /** AUT: henryyu1986@163.com DATE: 2011-12-20 */

				    sOldBounds.set(icon.getBounds());
				    icon.setBounds(left, top, left+width, top+height);
				    icon.draw(canvas);
				    icon.setBounds(sOldBounds);
				    
//	            if(index != -1 && sFgBitmap[index] != null) {
				    if (fg != null) {
				        canvas.drawBitmap(fg, 0, 0, null);
				    }
				    
				    canvas.setBitmap(null);

				    return bitmap;
				}
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				return null;
			}
	    }

	    public static Bitmap lessenBitmap(Bitmap src, int destWidth, int destHeight, boolean needRecycle) {
	        try {
				if (src == null)
				    return null;
				int w = src.getWidth();
				int h = src.getHeight();

				float scaleWidth = ((float) destWidth) / w;
				float scaleHeight = ((float) destHeight) / h;

				Matrix m = new Matrix();
				m.postScale(scaleWidth, scaleHeight);

				Bitmap resizeBitmap = Bitmap.createBitmap(src, 0, 0, w, h, m, true);

				if (needRecycle && !src.isRecycled()) {
				    src.recycle();
				    src = null;
				}

				return resizeBitmap;
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				return null;
			}
	    }
	/** AUT: henryyu1986@163.com DATE: 2011-12-20 */

	    /*** AUT: zhaoxy . DATE: 2012-03-28 . START***/
	    /**
	     * 
	     * @param n which icon style do u want to use, as a value between 1 (the first) and {@value #ICON_STYLE_COUNT}.
	     * @param icon whic icon do u want to modify.
	     * @param context
	     * @return
	     */
	    public static Bitmap createIconBitmap(int n, Drawable icon, Context context) {
	        try {
				synchronized (sCanvas) { // we share the statics :-(
				    Bitmap bg = null;
				    Bitmap fg = null;
				    Bitmap mask = null;
				    if (sIconWidth == -1 /*|| sIconWidth != SettingsValue.getIconSizeValue(context)*/) {
				        initStatics(context);
				    }
				    
				    if(n == 6){
				    	//Random mRandom1 = new Random();
				    	
//				    	int n1 = mRandom1.nextInt(5);
//				    	bg = getBitmap(context, "default_ic_style_" + n +"_"+n1+ "_bg");
				    	bg = getBitmap(context, "default_ic_style_" + n +"_0"+ "_bg");
				    }else if (n == 5){
//	            	Random mRandom1 = new Random();
//	            	int n1 = mRandom1.nextInt(8);
//	            	bg = getBitmap(context, "default_ic_style_" + n +"_"+n1+ "_bg");
				    	bg = getBitmap(context, "default_ic_style_" + n +"_0"+ "_bg");  //八个随机颜色，浅蓝色序号为1. 注释掉的前三行为产生随机颜色.add by shenchao1
				    }else{
				        bg = getBitmap(context, "default_ic_style_" + n + "_bg");
				        
				    }
				    fg = getBitmap(context, "default_ic_style_" + n + "_fg");
				    mask = getBitmap(context, "default_ic_style_" + n + "_mask");

				    int width = (int) (sIconSWidth);
				    int height = (int) (sIconSHeight);
//	            if(index == -1) {
				    /*if(bg == null) {
				        width = sIconWidth;
				        height = sIconHeight;
				    }*/

				    if (icon == null) {
				        icon = context.getResources().getDrawable(R.drawable.nothing);
				    }
				    if (icon instanceof PaintDrawable) {
				        PaintDrawable painter = (PaintDrawable) icon;
				        painter.setIntrinsicWidth(width);
				        painter.setIntrinsicHeight(height);
				    } else if (icon instanceof BitmapDrawable) {
				        // Ensure the bitmap has a density.
				        BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
				        Bitmap bitmap = bitmapDrawable.getBitmap();
				        if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
				            bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
				        }
				    }
				    int sourceWidth = icon.getIntrinsicWidth();
				    int sourceHeight = icon.getIntrinsicHeight();
				    Bitmap lessBitmap = null;

				    if (sourceWidth > 0 && sourceHeight > 0) {
				        // There are intrinsic sizes.
//	                if (width < sourceWidth || height < sourceHeight) {
				            // It's too big, scale it down.
				            final float ratio = (float) sourceWidth / sourceHeight;
				            if (sourceWidth > sourceHeight) {
				                height = (int) (width / ratio);
				            } else if (sourceHeight > sourceWidth) {
				                width = (int) (height * ratio);
				            }
				            Resources res = context.getResources();
				            if(icon instanceof FastBitmapDrawable) {
				            	lessBitmap = lessenBitmap(((FastBitmapDrawable) icon).getBitmap(), width, height, false);
				                icon = new BitmapDrawable(res, lessBitmap);
				            } else if (icon instanceof BitmapDrawable) {
				            /* RK_ID: RK_LAUNCHERMODEL. AUT: liuli1 . DATE: 2012-05-11 . START */
				            // fix bug 164602
				            	lessBitmap = lessenBitmap(((BitmapDrawable) icon).getBitmap(), width, height, false);
				            	icon = new BitmapDrawable(res, lessBitmap);
				        } else {
				        	 lessBitmap =lessenBitmap(drawableToBitmap(icon), width, height, false);
				        	 icon = new BitmapDrawable(res, lessBitmap);
				        	   /* RK_ID: RK_LAUNCHERMODEL. AUT: liuli1 . DATE: 2012-05-11 . END */
				        }
//	                } else if (sourceWidth < width && sourceHeight < height) {
//	                    // Don't scale up the icon
//	                    width = sourceWidth;
//	                    height = sourceHeight;
//	                }
				    }

				    // no intrinsic size --> use default size
				    int textureWidth = //sIconTextureWidth;
				    		sIconWidth;
				    int textureHeight =// sIconTextureHeight;
				    		sIconHeight;
				    //bugfix zhanglz1
/*	            if (FLAG_DRAWABLE_PADDING) {
				        textureWidth += 2;
				        textureHeight += 2;
				    }*/

				    final Bitmap bitmap = Bitmap.createBitmap(textureWidth, textureHeight,
				            Bitmap.Config.ARGB_8888);
				    final Canvas canvas = sCanvas;
				    canvas.setBitmap(bitmap);

				    final int left = (textureWidth-width) / 2;
				    final int top = (textureHeight-height) / 2;

//	            /** AUT: henryyu1986@163.com DATE: 2011-12-20 */
//	            if (false) {
//	                // draw a big box for the icon for debugging
////	                canvas.drawColor(sColors[sColorIndex]);
////	                if (++sColorIndex >= sColors.length) sColorIndex = 0;
//	                Paint debugPaint = new Paint();
////	                debugPaint.setColor(0xffcccc00);
//	                debugPaint.setStyle(Paint.Style.STROKE);
//	                debugPaint.setColor(sColors[sColorIndex]);
//	                debugPaint.setStrokeWidth(0.1f);
//	                if (++sColorIndex >= sColors.length) sColorIndex = 0;
////	                canvas.drawRect(left, top, left+width, top+height, debugPaint);
//	                canvas.drawCircle(sIconWidth / 2, sIconWidth / 2, sIconWidth / 2, debugPaint);
//	            }
//
//	            if (true) {
//	                // draw a big box for the icon for debugging
////	                canvas.drawColor(sColors[sColorIndex]);
////	                if (++sColorIndex >= sColors.length) sColorIndex = 0;
////	                Paint debugPaint = new Paint();
////	                debugPaint.setColor(0xffcccc00);
////	                canvas.drawRect(left, top, left+width, top+height, debugPaint);
//
////	              if(index != -1) {
////	                if (bg != null) {
////	                    canvas.drawBitmap(bg, 0, 0, null);
////	                }
//	                
//	            }
//	            /** AUT: henryyu1986@163.com DATE: 2011-12-20 */

				    sOldBounds.set(icon.getBounds());
				    icon.setBounds(left, top, left+width, top+height);
				    BitmapDrawable bd = (BitmapDrawable) icon;
				    Bitmap bm = bd.getBitmap();
				    canvas.drawBitmap(bm, left, top, null);
				    if (mask != null) {
				        if (mask.getWidth() != textureWidth || mask.getHeight() != textureHeight) {
				            mask = Bitmap.createScaledBitmap(mask, textureWidth, textureHeight, true);
				        }
				        sIconPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
				        canvas.drawBitmap(mask, 0f, 0f, sIconPaint);
				    }
				    icon.setBounds(sOldBounds);
				    if (bg != null) {
				        if (bg.getWidth() != textureWidth || bg.getHeight() != textureHeight) {
				            bg = Bitmap.createScaledBitmap(bg, textureWidth, textureHeight, true);
				        }
				        sIconPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
				        canvas.drawBitmap(bg, 0f, 0f, sIconPaint);
				    }
				    if (fg != null) {
				        if (fg.getWidth() != textureWidth || fg.getHeight() != textureHeight) {
				            fg = Bitmap.createScaledBitmap(fg, textureWidth, textureHeight, true);
				        }
				        canvas.drawBitmap(fg, 0f, 0f, null);
				    }
				    canvas.setBitmap(null);

				    return bitmap;
				}
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				return null;
			}
	    }
	    
	    /**
	     * <p>
	     * Create an icon that uses the specified resources(Background, Foreground and Mask).
	     * </p>
	     *
	     * @param icon
	     *            Which icon do u want to modify.May be null.
	     * @param bg
	     *            Background in the bottom of the composite image. May be null.
	     * @param fg
	     *            Foreground in the top of the composite image. May be null.
	     * @param mask
	     *            Only the alpha channel information of this image will be used.
	     *            The pixel in icon at the same corresponding location will
	     *            change based on this mask. May be null.
	     * @param context
	     * @return
	     */
	    public static Bitmap createIconBitmap(Drawable icon, Bitmap bg, Bitmap fg, Bitmap mask, Context context) {
	        try {
				synchronized (sCanvas) { // we share the statics :-(
				    if (sIconWidth == -1 /*|| sIconWidth != SettingsValue.getIconSizeValue(context)*/) {
				        initStatics(context);
				    }
				    //48dp 72px
				    LauncherApplication app = (LauncherApplication) context
		    				.getApplicationContext();
		    		int isScale = app.mLauncherContext.getInteger(
		    				R.integer.config_scale_appicon, R.integer.config_scale_appicon);
		    		int width = (int) (sIconWidth);
					int height = (int) (sIconHeight);
		    		if(isScale == 1){
			    		width = (int) (sIconSWidth);
						height = (int) (sIconSHeight);
		    		}
				    if (icon == null) {
				        icon = context.getResources().getDrawable(R.drawable.nothing);
				    }
				    if (icon instanceof PaintDrawable) {
				        PaintDrawable painter = (PaintDrawable) icon;
				        painter.setIntrinsicWidth(width);
				        painter.setIntrinsicHeight(height);
				    } else if (icon instanceof BitmapDrawable) {
				        // Ensure the bitmap has a density.
				        BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
				        Bitmap bitmap = bitmapDrawable.getBitmap();
				        if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
				            bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
				        }
				    }
				    int sourceWidth = icon.getIntrinsicWidth();
				    int sourceHeight = icon.getIntrinsicHeight();
				    if (sourceWidth > 0 && sourceHeight > 0) {
				        final float ratio = (float) sourceWidth / sourceHeight;
				        if (sourceWidth > sourceHeight) {
				            height = (int) (width / ratio);
				        } else if (sourceHeight > sourceWidth) {
				            width = (int) (height * ratio);
				        }
				        Resources res = context.getResources();
				        Bitmap lessBitmap = null;
				        if (icon instanceof FastBitmapDrawable) {
				        	lessBitmap = lessenBitmap(((FastBitmapDrawable) icon).getBitmap(), width, height, false);
				            icon = new BitmapDrawable(res,lessBitmap );
				        } else if (icon instanceof BitmapDrawable) {
				        	lessBitmap = lessenBitmap(((BitmapDrawable) icon).getBitmap(), width, height, false);
				            icon = new BitmapDrawable(res,lessBitmap );
				        } else {
				        	lessBitmap =lessenBitmap(drawableToBitmap(icon), width, height, false);
				            icon = new BitmapDrawable(res, lessBitmap);
				        }
				    }
				    // no intrinsic size --> use default size
				    // 54dp 81px
				    int textureWidth = //sIconTextureWidth;
				    		sIconWidth;
				    int textureHeight =// sIconTextureHeight;
				    		sIconHeight;
				    //bugfix zhanglz1
/*	            if (FLAG_DRAWABLE_PADDING) {
				        textureWidth += 2;
				        textureHeight += 2;
				    }*/

				    final Bitmap bitmap = Bitmap.createBitmap(textureWidth, textureHeight, Bitmap.Config.ARGB_8888);
				    final Canvas canvas = sCanvas;
				    canvas.setBitmap(bitmap);

				    final int left = (textureWidth - width) / 2;
				    final int top = (textureHeight - height) / 2;
				    sOldBounds.set(icon.getBounds());
				    icon.setBounds(left, top, left + width, top + height);
				    BitmapDrawable bd = (BitmapDrawable) icon;
				    Bitmap bm = bd.getBitmap();
				    canvas.drawBitmap(bm, left, top, null);
				    if (mask != null) {
				        if (mask.getWidth() != textureWidth || mask.getHeight() != textureHeight) {
				            mask = Bitmap.createScaledBitmap(mask, textureWidth, textureHeight, true);
				        }
				        sIconPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
				        canvas.drawBitmap(mask, 0f, 0f, sIconPaint);
				    }
				    icon.setBounds(sOldBounds);
				    if (bg != null) {
				        if (bg.getWidth() != textureWidth || bg.getHeight() != textureHeight) {
				            bg = Bitmap.createScaledBitmap(bg, textureWidth, textureHeight, true);
				        }
				        sIconPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
				        canvas.drawBitmap(bg, 0f, 0f, sIconPaint);
				    }
				    if (fg != null) {
				        if (fg.getWidth() != textureWidth || fg.getHeight() != textureHeight) {
				            fg = Bitmap.createScaledBitmap(fg, textureWidth, textureHeight, true);
				        }
				        canvas.drawBitmap(fg, 0f, 0f, null);
				    }
				    canvas.setBitmap(null);

				    return bitmap;
				}
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				return null;
			}
	    }

	    public static Bitmap getBitmap(Context c, String name) {
	        Bitmap localBitmap = null;
	        try{
	        Resources localResources;
	        int i;
	        localResources = c.getResources();
	        i = localResources.getIdentifier(name, "drawable", c.getPackageName());
	        if (i != 0) {
	        	final ActivityManager activityManager =
		                (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
	            localBitmap = ((BitmapDrawable) localResources.getDrawableForDensity(i, activityManager.getLauncherLargeIconDensity())).getBitmap();
	        }
	        }catch(NotFoundException e){
	        	return null;
	        }
	        return localBitmap;
	    }
	    /*** AUT: zhaoxy . DATE: 2012-03-28 . END***/

	    static void drawSelectedAllAppsBitmap(Canvas dest, int destWidth, int destHeight,
	            boolean pressed, Bitmap src) {
	        synchronized (sCanvas) { // we share the statics :-(
	            if (sIconWidth == -1) {
	                // We can't have gotten to here without src being initialized, which
	                // comes from this file already.  So just assert.
	                //initStatics(context);
	                throw new RuntimeException("Assertion failed: Utilities not initialized");
	            }

	            dest.drawColor(0, PorterDuff.Mode.CLEAR);

	            int[] xy = new int[2];
	            Bitmap mask = src.extractAlpha(sBlurPaint, xy);

	            float px = (destWidth - src.getWidth()) / 2.0f;
	            float py = (destHeight - src.getHeight()) / 2.0f;
	            dest.drawBitmap(mask, px + xy[0], py + xy[1],
	                    pressed ? sGlowColorPressedPaint : sGlowColorFocusedPaint);

	            mask.recycle();
	            mask = null;
	        }
	    }

	    /**
	     * Returns a Bitmap representing the thumbnail of the specified Bitmap.
	     * The size of the thumbnail is defined by the dimension
	     * android.R.dimen.launcher_application_icon_size.
	     *
	     * @param bitmap The bitmap to get a thumbnail of.
	     * @param context The application's context.
	     *
	     * @return A thumbnail for the specified bitmap or the bitmap itself if the
	     *         thumbnail could not be created.
	     */
	    public static Bitmap resampleIconBitmap(Bitmap bitmap, Context context) {
	        synchronized (sCanvas) { // we share the statics :-(
	            if (sIconWidth == -1 /*|| sIconWidth != SettingsValue.getIconSizeValue(context)*/) {
	                initStatics(context);
	            }

	            if (bitmap.getWidth() == sIconWidth && bitmap.getHeight() == sIconHeight) {
	                return bitmap;
	            } else {
	                return createIconBitmap(new BitmapDrawable(bitmap), context);
	            }
	        }
	    }

	    static Bitmap drawDisabledBitmap(Bitmap bitmap, Context context) {
	        try {
				synchronized (sCanvas) { // we share the statics :-(
				    if (sIconWidth == -1 /*|| sIconWidth != SettingsValue.getIconSizeValue(context)*/) {
				        initStatics(context);
				    }
				    final Bitmap disabled = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
				            Bitmap.Config.ARGB_8888);
				    final Canvas canvas = sCanvas;
				    canvas.setBitmap(disabled);
				    
				    canvas.drawBitmap(bitmap, 0.0f, 0.0f, sDisabledPaint);

				    canvas.setBitmap(null);

				    return disabled;
				}
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				return null;
			}
	    }
	    
	    public static void initStatics(Context context) {
	        final Resources resources = context.getResources();
	        final DisplayMetrics metrics = resources.getDisplayMetrics();
	        final float density = metrics.density;
	        mapp= (LauncherApplication) context.getApplicationContext();
	      //  sIconWidth = sIconHeight = (int) resources.getDimension(R.dimen.app_icon_size);
	        int iconsize = SettingsValue.getIconSizeValue(context);
	        sIconWidth = sIconHeight = iconsize;
	      //bugfix 11311
	     //   sIconTextureWidth = sIconTextureHeight = sIconWidth;
	      //  sIconSWidth = sIconSHeight = (int) resources.getDimension(R.dimen.app_icon_texture_size);
//	        int defaultIconSHeigt = SettingsValue.RES_ICON_TEXTURE_SIZE;
	        sIconSWidth = sIconSHeight = resources.getDimensionPixelSize(R.dimen.app_icon_texture_size);
//	        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();  
//			float mDeviceDensity = dm.density;
			/*int temp = Math.round(iconsize/density);
			String[] keys = SettingsValue.RES_ICON_SIZE_VALUES;
			//normal
			if (temp == Integer.parseInt(keys[0])) {
		        sIconSWidth = sIconSHeight = defaultIconSHeigt;
			} //small
			else if (temp == Integer.parseInt(keys[1])) {
				int a = Integer.parseInt(keys[1]);
				int b = Integer.parseInt(keys[0]);
				float c = ((float)a / (float)b);
				sIconSWidth = sIconSHeight  = Math.round(defaultIconSHeigt * c);
			}*/
			
	        sBlurPaint.setMaskFilter(new BlurMaskFilter(5 * density, BlurMaskFilter.Blur.NORMAL));
	        sGlowColorPressedPaint.setColor(0xffffc300);
	        sGlowColorPressedPaint.setMaskFilter(TableMaskFilter.CreateClipTable(0, 30));
	        sGlowColorFocusedPaint.setColor(0xffff8e00);
	        sGlowColorFocusedPaint.setMaskFilter(TableMaskFilter.CreateClipTable(0, 30));

	        ColorMatrix cm = new ColorMatrix();
	        cm.setSaturation(0.2f);
	        sDisabledPaint.setColorFilter(new ColorMatrixColorFilter(cm));
	        sDisabledPaint.setAlpha(0x88);

			/** AUT: henryyu1986@163.com DATE: 2011-12-20 */
	        /*** MODIFYBY: zhaoxy . DATE: 2012-04-05 . START***/
//	        sBgBitmap = new Bitmap[sBgRes.length];
//	        sFgBitmap = new Bitmap[sFgRes.length];
	        /*for (int i = 0; i < sBgRes.length; i++) {
	        	if (sBgRes[i] != -1) {
	        		sBgBitmap[i] = BitmapFactory.decodeResource(context.getResources(), sBgRes[i]);
	        		
	        		if (sBgBitmap[i].getWidth() != sIconWidth) {
	                    sBgBitmap[i] = lessenBitmap(sBgBitmap[i], sIconWidth, sIconHeight, true);
	                }
				} else {
					sBgBitmap[i] = null;
				}
	            if(sFgRes[i] == -1) {
	            	sFgBitmap[i] = null;
	            } else {
	            	sFgBitmap[i] = BitmapFactory.decodeResource(context.getResources(), sFgRes[i]);
	            }
	            if (sFgBitmap[i] != null && sFgBitmap[i].getWidth() != sIconWidth) {
	                sFgBitmap[i] = lessenBitmap(sFgBitmap[i], sIconWidth, sIconHeight, true);
	            }
	        }*/
	        /*** MODIFYBY: zhaoxy . DATE: 2012-04-05 . END***/
			/** AUT: henryyu1986@163.com DATE: 2011-12-20 */
	    }

	    /** Only works for positive numbers. */
	    static int roundToPow2(int n) {
	        int orig = n;
	        n >>= 1;
	        int mask = 0x8000000;
	        while (mask != 0 && (n & mask) == 0) {
	            mask >>= 1;
	        }
	        while (mask != 0) {
	            n |= mask;
	            mask >>= 1;
	        }
	        n += 1;
	        if (n != orig) {
	            n <<= 1;
	        }
	        return n;
	    }

	    static int generateRandomId() {
	        return new Random(System.currentTimeMillis()).nextInt(1 << 24);
	    }
	    
	    /*RK_ID: RK_HOTSEAT . AUT: zhanggx1 . DATE: 2011-12-26 . S*/
	    public static Drawable findDrawableById(Resources res, int iconId, Context context) {
	        if (iconId == 0)
	            return null;
	        try {
	            // get icon id name
	            String s = res.getResourceName(iconId);

	            //parse the id name
	            if (s != null) {
	                int index = s.indexOf(File.separator);
	                if (index != -1) {
	                    s = s.substring(index + 1);
	                    return findDrawableByResourceName(s, context);
	                }
	            } // end if s != null
	        } catch (NotFoundException e) {
	        	Debug.printException("Utilities->findDrawableById error", e);
//	            e.printStackTrace();
	        } catch (OutOfMemoryError e) {
	        	Debug.printException("Utilities->findDrawableById error", e);
//	        	e.printStackTrace();
	        }
	        
	        return null;
	    }
	    
	    public static int getResourceId(Resources res, int iconId, String type, Context newContext) {
	    	int ret = 0;
	    	if (res == null || iconId == 0 || type == null || newContext == null) {
	    		return ret;
	    	}
	    	try {
	            // get icon id name
	            String s = res.getResourceName(iconId);

	            //parse the id name
	            if (s != null) {
	                int index = s.indexOf(File.separator);
	                if (index != -1) {
	                    s = s.substring(index + 1);
	                    int resID = newContext.getResources().getIdentifier(s, type, newContext.getPackageName());
	        	        return resID;
	                }
	            } // end if s != null
	        } catch (NotFoundException e) {
	        	Debug.printException("Utilities->findDrawableById error", e);
//	            e.printStackTrace();
	        } 
	    	return ret;
	    }
	    
	    public static Bitmap findBitmapById(Resources res, int iconId, Context context) {
	        if (iconId == 0)
	            return null;
	        try {
	            // get icon id name
	            String s = res.getResourceName(iconId);

	            //parse the id name
	            if (s != null) {
	                int index = s.indexOf(File.separator);
	                if (index != -1) {
	                    s = s.substring(index + 1);
	                    Drawable drawable = findDrawableByResourceName(s, context);
	                    return createBitmap(drawable, 0, 0, context);
	                }
	            } // end if s != null
	        } catch (NotFoundException e) {
	        	Debug.printException("Utilities->findBitmapById error", e);
//	            e.printStackTrace();
	        } catch (OutOfMemoryError e) {
	        	Debug.printException("Utilities->findBitmapById error", e);
//	        	e.printStackTrace();
	        }
	        
	        return null;
	    }
	    

  public static boolean isInstalledRightQiezi(Context context) {
                PackageInfo packageInfo;
                String packagename = "com.lenovo.anyshare";
                try {
                        packageInfo = context.getPackageManager().getPackageInfo(
                                        packagename, 0);
                        Log.d("liuyg1","packageInfo.versionCode"+packageInfo.versionCode);

                        if(packageInfo.versionCode<=4020002){
                                return false;
                        }
                } catch (NameNotFoundException e) {
                        packageInfo = null;
                        e.printStackTrace();
                }
                if(packageInfo ==null){
                        return false;
                }

                return true;
        }
        public static void showInstallDialog(final Context context,boolean needupdate) {
                //Intent intent = new Intent(context,LelauncherDownloadAnyShare.class);
                //context.startActivity(intent);
}
	    /*
	     * retrieve customize bitmap icon by given id name
	     */
        private static int mIconDpi=0;
	    public static Drawable findDrawableByResourceName(String name, Context context) {
	        if (context == null)
	            return null;
	        Resources localResources = context.getResources();
	        try {
		        int resID = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
		        if (resID == 0) {
		        	return null;
		        }
		        final ActivityManager activityManager =
		                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		        if(density == Float.MIN_VALUE){
		        	density = context.getResources().getDisplayMetrics().density;
		        }
		        if(density<=1){
	        		if(mapp.mLauncherContext.getFriendContext()!=null)
	        			mIconDpi = 240;
	        		else
	        			mIconDpi = 160;
	        	}else if(density>1.0&&density<2.0){
	        		mIconDpi = 240;
	        	}else{
	        		mIconDpi = activityManager.getLauncherLargeIconDensity();
	        	}
		        Drawable drawable = context.getResources().getDrawableForDensity(resID, mIconDpi);
		        return drawable;
	        } catch (NotFoundException e) {
	        	Debug.printException("Utilities->findDrawableByResourceName error", e);
//	            e.printStackTrace();
	        } catch (OutOfMemoryError e) {
	        	Debug.printException("Utilities->findDrawableByResourceName error", e);
//	        	e.printStackTrace();
	        }
	        return null;
	    }
	    
	    public static ColorStateList findColorById(Resources res, int iconId, Context context) {
	        if (iconId == 0)
	            return null;

	        try {
	            // get icon id name
	            String s = res.getResourceName(iconId);

	            //parse the id name
	            if (s != null) {
	                int index = s.indexOf(File.separator);
	                if (index != -1) {
	                    s = s.substring(index + 1);
	                    return findColorByResourceName(s, res.getResourceTypeName(iconId), context);
	                }
	            } // end if s != null
	        } catch (NotFoundException e) {
	        	Debug.printException("Utilities->findColorById error", e);
//	            e.printStackTrace();
	        }
	        
	        return null;
	    }
	    
	    /*
	     * retrieve customize bitmap icon by given id name
	     */
	    public static ColorStateList findColorByResourceName(String name, String defType, Context context) {
	        if (context == null)
	            return null;
	        int resID = context.getResources().getIdentifier(name, defType, context.getPackageName());
	        if (resID == 0) {
	        	return null;
	        }        
	        try {
		        ColorStateList colors = context.getResources().getColorStateList(resID);
		        return colors;
	        } catch (NotFoundException e) {
	        	Debug.printException("Utilities->findColorByResourceName error", e);
	        }

	        return null;
	    }
	    
	    public static String[] findStringArrayById(Resources res, int iconId, Context context) {
	    	if (iconId == 0) {
	            return null;
	    	}
	        try {
	            // get icon id name
	            String s = res.getResourceName(iconId);
	            //parse the id name
	            if (s != null) {
	                int index = s.indexOf(File.separator);
	                if (index != -1) {
	                    s = s.substring(index + 1);
	                    return findStringArrayByResourceName(s, context);
	                }
	            } // end if s != null
	        } catch (NotFoundException e) {
	        	Debug.printException("Utilities->findStringArrayById error", e);
//	            e.printStackTrace();
	        }        
	        return null;
	    }
	    
	    public static String[] findStringArrayByResourceName(String name, Context context) {
	    	if (context == null) {
	    		return null;
	    	}
	    	int resID = context.getResources().getIdentifier(name, "array", context.getPackageName());
	    	if (resID == 0) {
	    		return null;
	    	}
	    	try {
	            return context.getResources().getStringArray(resID);
	        } catch (NotFoundException e) {
	        	Debug.printException("Utilities->findStringArrayByResourceName error", e);
	        }
	    	return null;
	    }
	    
	    public static Bitmap createBitmap(Drawable drawable, int width, int height, Context context) {
	        try {
				synchronized (sCanvas) { // we share the statics :-(
					
					if (drawable == null) {
						return null;
					}

				    if (drawable instanceof PaintDrawable) {
				        PaintDrawable painter = (PaintDrawable) drawable;
				        painter.setIntrinsicWidth(width);
				        painter.setIntrinsicHeight(height);
				    } else if (drawable instanceof BitmapDrawable) {
				        // Ensure the bitmap has a density.
				        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
				        Bitmap bitmap = bitmapDrawable.getBitmap();
				        if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
				            bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
				        }
				    }
				    int sourceWidth = drawable.getIntrinsicWidth();
				    int sourceHeight = drawable.getIntrinsicHeight();
				    
				    if (width <= 0) {
				    	width = sourceWidth;
				    }
				    
				    if (height <= 0) {
				    	height = sourceHeight;
				    }

				    /*** MODIFYBY: zhaoxy . DATE: 2012-04-05 . START***/
				    /*if (sourceWidth > 0 && sourceHeight > 0) {
				        // There are intrinsic sizes.
				        if (width < sourceWidth || height < sourceHeight) {
				            // It's too big, scale it down.
				            final float ratio = (float) sourceWidth / sourceHeight;
				            if (sourceWidth > sourceHeight) {
				                height = (int) (width / ratio);
				            } else if (sourceHeight > sourceWidth) {
				                width = (int) (height * ratio);
				            }
				        } else if (sourceWidth < width && sourceHeight < height) {
				            // Don't scale up the icon
				            width = sourceWidth;
				            height = sourceHeight;
				        }
				    }*/
				    /*** MODIFYBY: zhaoxy . DATE: 2012-04-05 . END***/

				    final Bitmap bitmap = Bitmap.createBitmap(width, height,
				            Bitmap.Config.ARGB_8888);
				    final Canvas canvas = sCanvas;
				    canvas.setBitmap(bitmap);

				    final int left = 0;
				    final int top = 0;

				    sOldBounds.set(drawable.getBounds());
				    drawable.setBounds(left, top, left+width, top+height);
				    drawable.draw(canvas);
				    drawable.setBounds(sOldBounds);
				    canvas.setBitmap(null);

				    return bitmap;
				}
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				return null;
			}
	    }
	    
	    public static Bitmap retrieveCustomIconFromFile(android.content.pm.ActivityInfo activityInfo, Context context, Context iconStyleContext) {
	        if (context == null || activityInfo == null) {
	            return null;
	        }

	        int iconId = activityInfo.getIconResource();
	        String pkg = activityInfo.packageName;
	        try {
	            Resources res = context.getPackageManager().getResourcesForApplication(pkg);
	            // liuli1 add lenovo background
	            return findCustomIconById(res, iconId, context, iconStyleContext, pkg);
	        } catch (NameNotFoundException e) {
	        	Debug.printException("Utilities->retrieveCustomIconFromFile error", e);
//	            e.printStackTrace();
	        } catch (OutOfMemoryError e) {
	        	Debug.printException("Utilities->retrieveCustomIconFromFile error", e);
//	        	e.printStackTrace();
	        }

	        return null;
	    }
	    
	    public static Bitmap retrieveCustomIconFromFile(int iconId, String pkg, Context context, Context iconStyleContext) {
	        if (context == null 
	        		|| iconId == 0
	        		|| pkg == null) {
	            return null;
	        }

	        try {
	            Resources res = context.getPackageManager().getResourcesForApplication(pkg);
	            // liuli1 add lenovo background
	            return findCustomIconById(res, iconId, context, iconStyleContext, pkg);
	        } catch (NameNotFoundException e) {
	        	Debug.printException("Utilities->retrieveCustomIconFromFile error", e);
//	            e.printStackTrace();
	        } catch (OutOfMemoryError e) {
	        	Debug.printException("Utilities->retrieveCustomIconFromFile error", e);
//	        	e.printStackTrace();
	        }

	        return null;
	    }
	    
	    public static Bitmap retrieveHotseatIconFromFile(android.content.pm.ActivityInfo activityInfo, Context context, Context iconStyleContext) {
	        if (context == null || activityInfo == null) {
	            return null;
	        }

	        int iconId = activityInfo.getIconResource();
	        String pkg = activityInfo.packageName;
	        try {
	            Resources res = context.getPackageManager().getResourcesForApplication(pkg);
	            return findHotseatIconById(res, iconId, context, iconStyleContext);
	        } catch (NameNotFoundException e) {
	        	Debug.printException("Utilities->retrieveHotseatIconFromFile error", e);
//	            e.printStackTrace();
	        } catch (OutOfMemoryError e) {
	        	Debug.printException("Utilities->retrieveHotseatIconFromFile error", e);
//	        	e.printStackTrace();
	        }

	        return null;
	    }
	    
	    public static Bitmap findHotseatIconById(Resources res, int iconId, Context context, Context iconStyleContext) {
	        if (iconId == 0)
	            return null;

	        try {
	            // get icon id name
	            String s = res.getResourceName(iconId);

	            //parse the id name
	            if (s != null) {
	                int indexColon = s.indexOf(":");
	                int indexSeperator = s.indexOf(File.separator);
	                if (indexColon != -1 && indexSeperator != -1) {
	                	String packageName = s.substring(0, indexColon);                	
	                	packageName = packageName.replace(".", "_");
	                	s = s.substring(indexSeperator + 1);
	                	s = "hotseat_" + packageName + "__" + s;
	                	s = s.toLowerCase();
	                    
	                    return findIconBitmapByIdName(s, context, iconStyleContext, packageName.replace("_", "."));
	                }
	            } // end if s != null
	        } catch (NotFoundException e) {
	        	Debug.printException("Utilities->findHotseatIconById error", e);
//	            e.printStackTrace();
	        } catch (OutOfMemoryError e) {
	        	Debug.printException("Utilities->findHotseatIconById error", e);
//	        	e.printStackTrace();
	        }
	        
	        return null;
	    }
	    	    
	    public static Bitmap findCustomIconById(Resources res, int iconId, Context context, Context iconStyleContext, String pkg) {
	        if (iconId == 0)
	            return null;

	        try {
	            // get icon id name
	            String s = res.getResourceName(iconId);

	            //parse the id name
	            if (s != null) {
	                if(isDefalutApp(s)){
	                     String resName = s.substring(s.indexOf("/")+1);
	                     Log.d("liuyg1",resName);
	                    return findIconBitmapByIdName(resName, context, iconStyleContext, pkg); 
	                }else{
	                    
	                
	                int indexColon = s.indexOf(":");
	                int indexSeperator = s.indexOf(File.separator);
	                if (indexColon != -1 && indexSeperator != -1) {
	                    String packageName = s.substring(0, indexColon);
	                	boolean pkgNotEquals = !packageName.equals(pkg);
	                	packageName = packageName.replace(".", "_");
	                	s = s.substring(indexSeperator + 1);
	                	String iconStylePkg = iconStyleContext == null ? "" : iconStyleContext.getPackageName();
	                	// for shortcuts from the default launcher.apk
	                	String resName = (packageName.equals(iconStylePkg.replace(".", "_")) && iconStylePkg.equals(context.getPackageName()))
	                			? s : (packageName + "__" + s);
	                	resName = resName.toLowerCase();
	                    
	                    Bitmap ret = findIconBitmapByIdName(resName, context, iconStyleContext, pkg);
	                    if (ret == null && pkgNotEquals) {
	                    	packageName = pkg;
	                    	packageName = packageName.replace(".", "_");
	                    	resName = (packageName.equals(iconStylePkg.replace(".", "_")) && iconStylePkg.equals(context.getPackageName()))
	                    			? s : (packageName + "__" + s);
	                    	resName = resName.toLowerCase();
	                    	return findIconBitmapByIdName(resName, context, iconStyleContext, pkg);                    	
	                    } else {
	                    	return ret;
	                    }

	                }
	                }
	            } // end if s != null
	        } catch (NotFoundException e) {
	        	Debug.printException("Utilities->findCustomIconById error", e);
//	            e.printStackTrace();
	        } catch (OutOfMemoryError e) {
	        	Debug.printException("Utilities->findCustomIconById error", e);
//	        	e.printStackTrace();
	        }
	        
	        return null;
	    }
	    private static LauncherApplication mapp ;
	    private static float density=Float.MIN_VALUE;
	    public static Bitmap findIconBitmapByIdName(String name, Context context, Context iconStyleContext, String pkg) {
	        if (context == null)
	            return null;
	        final ActivityManager activityManager =
	                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	        if(density == Float.MIN_VALUE){
	        	density = context.getResources().getDisplayMetrics().density;
	        }
        	if(density<=1){
        		if(mapp.mLauncherContext.getFriendContext()!=null)
        			mIconDpi = 240;
        		else
        			mIconDpi = 160;
        	}else if(density>1.0&&density<2.0){
        		mIconDpi = 240;
        	}else{
        		mIconDpi = activityManager.getLauncherLargeIconDensity();
        	}
	        int resID = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
	        if (isDefalutApp(name) && resID == 0)
	        {
	            resID = getAnotherResId(name, context);
	        }       
	        	        
	        if (resID == 0) {
	        	return null;
	        }
	        Drawable drawable = context.getResources().getDrawableForDensity(resID, mIconDpi); 
	        
            Log.d("a","mIconDpi="+mIconDpi);
	        if (drawable == null) {
	        	return null;
	        }
	        
	        int iconBgIndex = SettingsValue.getIconStyleIndex(context);
	        if (iconStyleContext == null
	        		|| (!context.getPackageName().equals(iconStyleContext.getPackageName()) && iconBgIndex == SettingsValue.THEME_ICON_BG_INDEX)
	        		|| (context.getPackageName().equals(iconStyleContext.getPackageName())
	                    /*RK_THEME_IN_LAUNCHER gechuanning 2012-11-06 S*/
	        				&&( iconBgIndex == SettingsValue.DEFAULT_ICON_BG_INDEX ||iconBgIndex == SettingsValue.THEME_ICON_BG_INDEX ))) {
	                   /*RK_THEME_IN_LAUNCHER gechuanning 2012-11-06 E*/
	        	return Utilities.createIconBitmapWithoutStyle(drawable, context);
	        }
	        
	        return Utilities.createIconBitmap(drawable, iconStyleContext, pkg);
	    }
	    
	    public static View createLayoutById(Resources res, int iconId, Context context, ViewGroup root) {
	    	if (iconId == 0) {
	            return null;
	    	}
	        try {
	            // get icon id name
	            String s = res.getResourceName(iconId);
	            //parse the id name
	            if (s != null) {
	                int index = s.indexOf(File.separator);
	                if (index != -1) {
	                    s = s.substring(index + 1);
	                    return createLayoutByResourceName(s, context, root);
	                }
	            } // end if s != null
	        } catch (NotFoundException e) {
	        	Debug.printException("Utilities->createLayoutById error", e);
//	            e.printStackTrace();
	        }        
	        return null;
	    }
	    
	    public static View createLayoutByResourceName(String name, Context context, ViewGroup root) {
	    	View ret = null;
	    	if (context == null) {
	    		return null;
	    	}
	    	int resID = context.getResources().getIdentifier(name, "layout", context.getPackageName());
	    	if (resID == 0) {
	    		return null;
	    	}
	    	try {
	    		XmlResourceParser parser = context.getResources().getLayout(resID);
	    		if (parser != null) {
	    			LayoutInflater inflater = (LayoutInflater)context.
	    					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    			ret = inflater.inflate(parser, root);
	    		}
	        } catch (NotFoundException e) {
	        	Debug.printException("Utilities->createLayoutByResourceName error", e);
	        }
	    	return ret;
	    }
	    
	    public static int getViewId(String name, Context context) {
	    	if (name == null || context == null) {
	    		return 0;
	    	}
	    	int resID = context.getResources().getIdentifier(name, "id", context.getPackageName());
	    	return resID;
	    }
	    
	    public static int findIntegerById(Resources res, int iconId, Context context) {
	    	if (iconId == 0) {
	            return Integer.MIN_VALUE;
	    	}
	        try {
	            // get icon id name
	            String s = res.getResourceName(iconId);
	            //parse the id name
	            if (s != null) {
	                int index = s.indexOf(File.separator);
	                if (index != -1) {
	                    s = s.substring(index + 1);
	                    return findIntegerByResourceName(s, context);
	                }
	            } // end if s != null
	        } catch (NotFoundException e) {
	        	Debug.printException("Utilities->findIntegerById error", e);
//	            e.printStackTrace();
	        }        
	        return Integer.MIN_VALUE;
	    }
	    
	    public static int findIntegerByResourceName(String name, Context context) {
	    	if (context == null) {
	    		return Integer.MIN_VALUE;
	    	}
	    	int resID = context.getResources().getIdentifier(name, "integer", context.getPackageName());
	    	if (resID == 0) {
	    		return Integer.MIN_VALUE;
	    	}
	    	try {
	            return context.getResources().getInteger(resID);
	        } catch (NotFoundException e) {
	        	Debug.printException("Utilities->findIntegerByResourceName error", e);
	        }
	    	return Integer.MIN_VALUE;
	    }
	    
	    public static float findDimensionPixelOffsetById(Resources res, int iconId, Context context, DimenType dimenType) {
	    	if (iconId == 0) {
	            return Float.MIN_VALUE;
	    	}
	        try {
	            // get icon id name
	            String s = res.getResourceName(iconId);
	            //parse the id name
	            if (s != null) {
	                int index = s.indexOf(File.separator);
	                if (index != -1) {
	                    s = s.substring(index + 1);
	                    return findDimensionPixelOffsetByResourceName(s, context, dimenType);
	                }
	            } // end if s != null
	        } catch (NotFoundException e) {
	        	Debug.printException("Utilities->findDimensionPixelOffsetById error", e);
//	            e.printStackTrace();
	        }        
	        return Float.MIN_VALUE;
	    }
	    
	    public static float findDimensionPixelOffsetByResourceName(String name, Context context, DimenType dimenType) {
	    	float ret = Float.MIN_VALUE;
	    	if (context == null) {
	    		return Float.MIN_VALUE;
	    	}
	    	int resID = context.getResources().getIdentifier(name, "dimen", context.getPackageName());
	    	if (resID == 0) {
	    		return Float.MIN_VALUE;
	    	}
	    	try {    		
	    		switch (dimenType) {
	    		case DIMENSION:
	    			ret = context.getResources().getDimension(resID);
	    			break;
	    		case DIMENSION_OFFSET:
	    			ret = context.getResources().getDimensionPixelOffset(resID);
	    			break;
	    		case DIMENSION_SIZE:
	    			ret = context.getResources().getDimensionPixelSize(resID);
	    			break;
	    		default:
	    			break;
	    		}
	            return ret;
	        } catch (NotFoundException e) {
	        	Debug.printException("Utilities->findDimensionPixelOffsetByResourceName error", e);
	        }
	    	return Float.MIN_VALUE;
	    }
	    
	    public enum DimenType {
	    	DIMENSION  (0),
	    	DIMENSION_OFFSET (1),
	    	DIMENSION_SIZE (2);
	    	
	    	private final int nativeInt;
	    	DimenType(int ni) {
	    		nativeInt = ni;
	    	}
	    }
	    
	    public static Animation findAnimationById(Resources res, int iconId, Context context) {
	    	if (iconId == 0) {
	            return null;
	    	}
	        try {
	            // get icon id name
	            String s = res.getResourceName(iconId);
	            //parse the id name
	            if (s != null) {
	                int index = s.indexOf(File.separator);
	                if (index != -1) {
	                    s = s.substring(index + 1);
	                    return findAnimationResourceName(s, context);
	                }
	            } // end if s != null
	        } catch (NotFoundException e) {
	        	Debug.printException("Utilities->findAnimationById error", e);
//	            e.printStackTrace();
	        }        
	        return null;
	    }
	    
	    
		/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 S*/
		private static String themePostfix = SettingsValue.getInbuildThemePostfix();
		private static String TAG = "00";

		public static int findInbuildThemeIdbyId(Context context,int Id,String type){
			Resources res = context.getResources();
			String s =null;
	        try {
	            // get icon id name
	            s = res.getResourceName(Id);
	            if (s != null) {
	                int index = s.indexOf(File.separator);
	                if (index != -1) {
	                    s = s.substring(index + 1);
	                }
	            } // end if s != null
	        } catch (NotFoundException e) {
	        	Debug.printException("Utilities->findIntegerById error", e);
	        } 
	        if(s == null){
	        	return 0;
	        }
			s = s + themePostfix;
			return res.getIdentifier(s, type, context.getPackageName());
		}
		
		public static int findInbuildThemeIdbyName(Context context,String name,String type){
			Resources res = context.getResources();
	        if(name == null){
	        	return 0;
	        }
	        name = name + themePostfix;
			return res.getIdentifier(name, type, context.getPackageName());
		}
		public static String[] findInbulidThemeCustomIconNameIdbyId(Context context,int iconId,String pkg ,String type,Context iconStyleContext){
			
	        if (context == null 
	        		|| iconId == 0
	        		|| pkg == null) {
	            return null;
	        }
	        Resources res = null ;
	        try {
	            res = context.getPackageManager().getResourcesForApplication(pkg);
	        } catch (NameNotFoundException e) {
	        	Debug.printException("Utilities->retrieveCustomIconFromFile error", e);
	        } catch (OutOfMemoryError e) {
	        	Debug.printException("Utilities->retrieveCustomIconFromFile error", e);
	        }
	        if(res == null){
	        	return null;
	        }

	        String s = null;
	        String[] ret = new String[2];
	        try {
	            // get icon id name
	            s = res.getResourceName(iconId);

	            //parse the id name
	            if (s != null) {
	                int indexColon = s.indexOf(":");
	                int indexSeperator = s.indexOf(File.separator);
	                if (indexColon != -1 && indexSeperator != -1) {
	                	String packageName = s.substring(0, indexColon); 
	                	boolean pkgEquals = packageName.equals(pkg);
	                	
	                	packageName = packageName.replace(".", "_");
	                	s = s.substring(indexSeperator + 1);
	                	
	                	String iconStylePkg = iconStyleContext == null ? "" : iconStyleContext.getPackageName();
	                	// for shortcuts from the default launcher.apk
	                	
	                	ret[0] = (packageName.equals(iconStylePkg.replace(".", "_")) && iconStylePkg.equals(context.getPackageName()))
	                			? s : (packageName + "__" + s);
	                	ret[0] = ret[0].toLowerCase();
	                	
	                	if (pkgEquals) {
	                		ret[1] = ret[0];
	                	} else {
	                		packageName = pkg;
	                		packageName = packageName.replace(".", "_");
	                		ret[1] = (packageName.equals(iconStylePkg.replace(".", "_")) && iconStylePkg.equals(context.getPackageName()))
	                    			? s : (packageName + "__" + s);
	                		ret[1] = ret[1].toLowerCase();
	                	}
	                }
	            } // end if s != null
	        } catch (NotFoundException e) {
	        	Debug.printException("Utilities->findCustomIconById error", e);
//	            e.printStackTrace();
	        } catch (OutOfMemoryError e) {
	        	Debug.printException("Utilities->findCustomIconById error", e);
//	        	e.printStackTrace();
	        }
	        if(ret[0] == null){
	        	return null;
	        }
	        ret[0] += themePostfix;
	        ret[1] += themePostfix;
	        return ret;
		}
		/*PK_ID:THEM IN LAUNCHER AUTH:GECN1 DATE:2012-11-01 E*/
	    
	    public static Animation findAnimationResourceName(String name, Context context) {
	    	XmlResourceParser parser = null;
	    	if (context == null) {
	    		return null;
	    	}
	    	int resID = context.getResources().getIdentifier(name, "anim", context.getPackageName());
	    	if (resID == 0) {
	    		return null;
	    	}
	    	try {
	            parser = context.getResources().getAnimation(resID);
	            return createAnimationFromXml(context, parser);
	        } catch (XmlPullParserException ex) {
	        	Debug.printException("Utilities->findAnimationResourceName error", ex);
//	        	ex.printStackTrace();
	        } catch (IOException ex) {
	        	Debug.printException("Utilities->findAnimationResourceName error", ex);
//	        	ex.printStackTrace();
	        } catch (NotFoundException e) {
	        	Debug.printException("Utilities->findAnimationResourceName error", e);
//	        	e.printStackTrace();
	        } finally {
	            if (parser != null) {
	            	parser.close();
	            }
	        }
	    	return null;
	    }
	    
	    private static Animation createAnimationFromXml(Context c, XmlPullParser parser)
	            throws XmlPullParserException, IOException {
	        return createAnimationFromXml(c, parser, null, Xml.asAttributeSet(parser));
	    }

	    private static Animation createAnimationFromXml(Context c, XmlPullParser parser,
	            AnimationSet parent, AttributeSet attrs) throws XmlPullParserException, IOException {

	        Animation anim = null;

	        // Make sure we are on a start tag.
	        int type;
	        int depth = parser.getDepth();

	        type = parser.next();
	        while ((type != XmlPullParser.END_TAG || parser.getDepth() > depth)
	               && type != XmlPullParser.END_DOCUMENT) {

	            if (type != XmlPullParser.START_TAG) {
	            	type = parser.next();
	                continue;
	            }
	            String  name = parser.getName();

	            if (name.equals("set")) {
	                anim = new AnimationSet(c, attrs);
	                createAnimationFromXml(c, parser, (AnimationSet)anim, attrs);
	            } else if (name.equals("alpha")) {
	                anim = new AlphaAnimation(c, attrs);
	            } else if (name.equals("scale")) {
	                anim = new ScaleAnimation(c, attrs);
	            }  else if (name.equals("rotate")) {
	                anim = new RotateAnimation(c, attrs);
	            }  else if (name.equals("translate")) {
	                anim = new TranslateAnimation(c, attrs);
	            } else {
	                throw new RuntimeException("Unknown animation name: " + parser.getName());
	            }
	            if (parent != null) {
	                parent.addAnimation(anim);
	            }
	            type = parser.next();
	        }
	        return anim;
	    }
	    /*RK_ID: RK_HOTSEAT . AUT: zhanggx1 . DATE: 2011-12-26 . E*/
	    
	    public static void copyFile(File sourceFile, File targetFile) {
	        BufferedInputStream inBuff = null;
	        BufferedOutputStream outBuff = null;
	        try {
	            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

	            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

	            byte[] b = new byte[1024 * 5];
	            int len;
	            while ((len = inBuff.read(b)) != -1) {
	                outBuff.write(b, 0, len);
	            }
	            outBuff.flush();
	            
	            if (inBuff != null) {
	                inBuff.close();
	            }
	            if (outBuff != null) {
	            	outBuff.close();
	            }                
	        } catch (IOException e) {
	        	
	        } finally {
	        	inBuff = null;
	        	outBuff = null;
	        }
	    }
	    
	    public static List<ResolveInfo> findActivitiesForSkin(Context context) {
	    	int defaultIndex = -1;
	        final PackageManager packageManager = context.getPackageManager();

	        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
	        mainIntent.addCategory(SettingsValue.THEME_PACKAGE_CATEGORY);

	        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
	        if (apps == null) {
	        	return null;
	        }
	        
	        String defaultTheme = SettingsValue.getDefaultThemeValue(context);
	        for (int i = 0; i < apps.size(); i++) {
	        	if (defaultTheme.equals(apps.get(i).activityInfo.packageName)) {
	        		defaultIndex = i;
	        		break;
	        	}
	        }
	        if (defaultIndex != -1) {
	        	apps.remove(defaultIndex);
	        }
	        
	        return apps;
	    }
	    
		public static String getRandomTheme(Context context, Random r) {
			String skin;
			int size = 0;
			String defaultTheme = SettingsValue.getDefaultThemeValue(context);
			String currentTheme = SettingsValue.getThemeValue(context);
			List<ResolveInfo> installedSkins = findActivitiesForSkin(context);
			
			if (r == null || installedSkins == null) {
				return defaultTheme;
			}
			size = installedSkins.size();
			if (size == 0) {
				return defaultTheme;
			}
			
			size++;
			
			do {
				int index = Math.abs(r.nextInt(size)) % size;
				
				if (index >= installedSkins.size()) {
					return defaultTheme;
				}
				skin = installedSkins.get(index).activityInfo.packageName;
			} while (skin.equals(currentTheme));
			
			return skin;
		}
		
		/*RK_ID: RK_LIST_APP . AUT: zhanggx1 . DATE: 2012-06-19 . S*/
	    public static boolean canBeRemoved(String pkgName, Context context) {
	    	if (context == null || pkgName == null) {
	    		return false;
	    	}
			boolean ret = false;
			try {
				ApplicationInfo ai = context.getPackageManager().getApplicationInfo(pkgName, 0);
				ret = (ai.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
						|| (ai.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
				return ret;
			} catch (NameNotFoundException e) {
				Debug.printException("ThemeSettings->isSystemApp. Get applicationInfo error", e);
//				e.printStackTrace();
				return false;
			}
		}
	    /*RK_ID: RK_LIST_APP . AUT: zhanggx1 . DATE: 2012-06-19 . E*/
	    
	    public static Rect setBackgroundDrawable(View view, Drawable drawable, boolean setPaddingJustByBg) {
	    	if (view == null || drawable == null) {
	    		return null;
	    	}
	    	
	    	Rect padding = new Rect();
	    	drawable.getPadding(padding);
	    	
	    	view.setBackgroundDrawable(drawable);
	    	
	    	if (setPaddingJustByBg) {
	    		view.setPadding(padding.left, padding.top, padding.right, padding.bottom);
	    	}
	    	return padding;
	    }
	    
	    public static Rect setBackgroundDrawable(View view, Drawable drawable) {
	    	return setBackgroundDrawable(view, drawable, true);
	    }
	    
	    public static void setBackgroundAndPadding(View view, 
	    		Drawable drawable, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
	    	if (view == null || drawable == null) {
	    		return;
	    	}
	    	Rect padding = setBackgroundDrawable(view, drawable, false);
	    	int left = Math.max(paddingLeft, padding.left);
	    	int top = Math.max(paddingTop, padding.top);
	    	int right = Math.max(paddingRight, padding.right);
	    	int bottom = Math.max(paddingBottom, padding.bottom);
	    	view.setPadding(left, top, right, bottom);
	    }
	    
	    public static boolean isLightColor(int color) {
	    	return isLightColor(color, 192f);
	    }
	    public static boolean isLightColor(int color, float threshold) {
	    	int r = Color.red(color);
	    	int g = Color.green(color);
	    	int b = Color.blue(color);
	    	float gray = r * 0.299f + g * 0.587f + b * 0.114f;
//	    	android.util.Log.i("dooba", "+++++++++++++++++>r: " + r + "....g: " + g + "....b: " + b + "....gray: " + gray);
	    	return (gray >= threshold);
	    }

	    public static ComponentName getComponentNameFromResolveInfo(ResolveInfo info) {
	        if (info.activityInfo != null) {
	            return new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
	        } else {
	            return new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name);
	        }
	    }
	    public static boolean isScreenLarge(Context context) {
	    	 final int screenSize = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
	         boolean sIsScreenLarge = screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
	             screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE;
	         return sIsScreenLarge;
	    }
	    public static final int INSTALL_TYPE_NOT_AKEY = 0;
		public static final int INSTALL_TYPE_AKEY_NORMAL = 1;
		public static final String PREF_AKEY_INSTALL_CANCEL = "pref_akey_install_cancel";
		public static final String PREF_MAGICDOWNLOAD = "com.lenovo.launcher.magicdownload_preferences";
		public static final String EXTRA_POPTOAST = "poptoast";
	    public static int canAKeyIntall(Context context){
			if( checkSystemPermission(context) && checkInstallPermission(context)){
	    		return INSTALL_TYPE_AKEY_NORMAL;
	    	}/*else if( checkRootSystem(context)){
	    		return INSTALL_TYPE_AKEY_SHELL;
	    	}*/else{
	    		return INSTALL_TYPE_NOT_AKEY;
	    	}
		}
		private static boolean checkSystemPermission(Context context){
			PackageInfo packageinfo = null;
	    	PackageManager packagemanager = context.getPackageManager();
	    	String packName = context.getPackageName();
			try {
				packageinfo = packagemanager.getPackageInfo(packName, 0);
			} catch (NameNotFoundException e) {
				return false;
			}
			ApplicationInfo appInfo = packageinfo.applicationInfo;
	        if( appInfo != null ){
	            if( ( appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 ){
	            	Log.i(TAG,"checkSystemPermission,  system app!!!");
	                return true;
	            }
	        }
	        Log.i(TAG ,"checkSystemPermission,not system app!!!");
			return false;
		}
		private static boolean checkInstallPermission(Context context){
			if( context.checkCallingOrSelfPermission("android.permission.INSTALL_PACKAGES") == 0 ){
				Log.i(TAG,"checkInstallPermission, have install permission!!!");
				return true;
			}
			Log.i(TAG,"checkInstallPermission, not have install permission!!!");
			return false;
		}
		public static Bitmap getIconStyleImage(int n, Drawable icon, Context context) {
	        try {
				synchronized (sCanvas) { // we share the statics :-(
				    Bitmap bg = null;
				    Bitmap fg = null;
				    Bitmap mask = null;
				    
				    /*if (sIconWidth == -1 || sIconWidth != SettingsValue.getIconSizeValue(context)) {
				        initStatics(context);
				    }*/
				    if( n == 6 ){
//						Random mRandom1 = new Random();
//						int n1 = mRandom1.nextInt(5);
//				    	bg = getBitmap(context, "default_ic_style_" + n + "_" + n1 + "_bg");
				    	bg = getBitmap(context, "default_ic_style_" + n + "_0"  + "_bg");
				    }else if(n == 5){
//	            	Random mRandom1 = new Random();
//					int n1 = mRandom1.nextInt(8);
//					bg = getBitmap(context, "default_ic_style_" + n + "_" + n1 + "_bg");
				    	bg = getBitmap(context, "default_ic_style_" + n + "_0"  + "_bg");
				    }else {
				    	bg = getBitmap(context, "default_ic_style_" + n + "_bg");
				    }
				    fg = getBitmap(context, "default_ic_style_" + n + "_fg");
				    mask = getBitmap(context, "default_ic_style_" + n + "_mask");
				    if (n == 1) {
				        // this index is owned by lenovo background
				        if (sLeBgBitmap == null) {
				            sLeBgBitmap = new Bitmap[sLeBgRes.length];
				            for (int i = 0; i < sLeBgRes.length; i++) {
				                sLeBgBitmap[i] = BitmapFactory.decodeResource(context.getResources(), sLeBgRes[i]);
				            }
				        }
				        int length = mRandom.nextInt(sLeBgRes.length);//Math.abs(mRandom.nextInt()) % sLeBgRes.length;
				        Bitmap temp = createIconBitmap(icon, sLeBgBitmap[length], context);
				        Resources res = context.getResources();
				        int iconSize = context.getResources().getDimensionPixelSize(R.dimen.icon_style_app_icon_size);
				        Bitmap bitmap = lessenBitmap(temp, iconSize, iconSize, false);
				        return bitmap;
				    }
				    
				    int width = (int) (sIconSWidth); //先把所有应用图标都缩小成72*72
				    int height = (int) (sIconSHeight);

				    if (icon == null) {
				        icon = context.getResources().getDrawable(R.drawable.nothing);
				    }
				    if (icon instanceof PaintDrawable) {
				        PaintDrawable painter = (PaintDrawable) icon;
				        painter.setIntrinsicWidth(width);
				        painter.setIntrinsicHeight(height);
				    } else if (icon instanceof BitmapDrawable) {
				        // Ensure the bitmap has a density.
				        BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
				        Bitmap bitmap = bitmapDrawable.getBitmap();
				        if (bitmap!=null && bitmap.getDensity() == Bitmap.DENSITY_NONE) {
				            bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
				        }
				    }
				    int sourceWidth = icon.getIntrinsicWidth();
				    int sourceHeight = icon.getIntrinsicHeight();
				    Bitmap lessBitmap = null;

				    if (sourceWidth > 0 && sourceHeight > 0) {
				            final float ratio = (float) sourceWidth / sourceHeight;
				            if (sourceWidth > sourceHeight) {
				                height = (int) (width / ratio);
				            } else if (sourceHeight > sourceWidth) {
				                width = (int) (height * ratio);
				            }
				            Resources res = context.getResources();
				            if(icon instanceof FastBitmapDrawable) {
				            	lessBitmap = lessenBitmap(((FastBitmapDrawable) icon).getBitmap(), width, height, false);
				                icon = new BitmapDrawable(res, lessBitmap);
				            } else if (icon instanceof BitmapDrawable) {
				            	lessBitmap = lessenBitmap(((BitmapDrawable) icon).getBitmap(), width, height, false);
				            	icon = new BitmapDrawable(res, lessBitmap);
				           } else {
				        	   lessBitmap =lessenBitmap(drawableToBitmap(icon), width, height, false);
				        	   icon = new BitmapDrawable(res, lessBitmap);
				            }
				    }
				    //变成要求的iconsize的大小
				    int textureWidth = context.getResources().getDimensionPixelSize(R.dimen.icon_style_app_icon_size);
				    int textureHeight = textureWidth;
				    final Bitmap bitmap = Bitmap//.createScaledBitmap(lessBitmap, textureWidth, textureHeight, true);
				    		.createBitmap(textureWidth, textureHeight,
				            Bitmap.Config.ARGB_8888);
				    final Canvas canvas = sCanvas;
				    canvas.setBitmap(bitmap);

				    final int left = (textureWidth-width) / 2;
				    final int top = (textureHeight-height) / 2;

				    sOldBounds.set(icon.getBounds());
				    icon.setBounds(left, top, left+width, top+height);
				    BitmapDrawable bd = (BitmapDrawable) icon;
				    Bitmap bm = bd.getBitmap();
				    if(bm!=null){
				        canvas.drawBitmap(bm, left, top, null);
				    }
				    if (mask != null) {
				        if (mask.getWidth() != textureWidth || mask.getHeight() != textureHeight) {
				            mask = Bitmap.createScaledBitmap(mask, textureWidth, textureHeight, true);
				        }
				        sIconPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
				        canvas.drawBitmap(mask, 0f, 0f, sIconPaint);
				    }
				    icon.setBounds(sOldBounds);
				    if (bg != null) {
				        if (bg.getWidth() != textureWidth || bg.getHeight() != textureHeight) {
				            bg = Bitmap.createScaledBitmap(bg, textureWidth, textureHeight, true);
				        }
				        sIconPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
				        canvas.drawBitmap(bg, 0f, 0f, sIconPaint);
				    }
				    if (fg != null) {
				        if (fg.getWidth() != textureWidth || fg.getHeight() != textureHeight) {
				            fg = Bitmap.createScaledBitmap(fg, textureWidth, textureHeight, true);
				        }
				        canvas.drawBitmap(fg, 0f, 0f, null);
				    }
				        canvas.setBitmap(null);

				    return bitmap;
				}
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				return null;
			}
		}
		
		
	private static boolean isDefalutApp(String s){
        if (s.contains("com_android_contacts__ic_launcher_phone")||
            s.contains("com_android_contacts__ic_launcher_contacts")||
            s.contains("com_android_mms__ic_launcher_smsmms")||
            s.contains("com_android_browser__ic_launcher_browser") ||
            s.contains("com_android_camera__ic_launcher_camera"))
        {
            return true;
        }
        
        return false;
    }
	
	public static int getAnotherResId(String s, Context context)
    {
	    int resID = 0;
	    if (s.contains("com_android_contacts__ic_launcher_phone"))
	    {
	        resID = context.getResources().getIdentifier("com_lenovo_ideafriend__ic_launcher_phone", "drawable", context.getPackageName());
	    }
	    else if (s.contains("com_android_contacts__ic_launcher_contacts"))
	    {
	        resID = context.getResources().getIdentifier("com_lenovo_ideafriend__ic_launcher_contacts", "drawable", context.getPackageName());
	    }
        else if (s.contains("com_android_mms__ic_launcher_smsmms"))
        {
            resID = context.getResources().getIdentifier("com_lenovo_mms__ic_launcher_smsmms", "drawable", context.getPackageName());
            if (resID == 0)
            {
                resID = context.getResources().getIdentifier("com_lenovo_ideafriend__ic_launcher_smsmms", "drawable", context.getPackageName());
            }
        }	        
        else if (s.contains("com_android_browser__ic_launcher_browser"))
        {
            resID = context.getResources().getIdentifier("com_android_browser__ic_launcher_browser_evdo", "drawable", context.getPackageName());
        }	    
        else if (s.contains("com_android_camera__ic_launcher_camera"))
        {
            //do nothing
        }
	    
	    return resID;
    }
	public static int getNavigationBarHeight(Context context) {
		Resources res = context.getResources();//launcher.getResources();
        boolean hasNavigationBar = res.getBoolean(com.android.internal.R.bool.config_showNavigationBar);

        // if we cannot retrieve navigation configuration,
        // maybe there is an error about framework.jar. so change a way to try.
//        if (!hasNavigationBar) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$bool");
                Field f = c.getField("config_showNavigationBar");
                int resid = f.getInt(null);

                hasNavigationBar = context.getResources().getBoolean(resid);
                Log.v(TAG, " res = " + hasNavigationBar);
            } catch (Exception e) {
            	Debug.printException("cannot read com.android.internal.R.bool.config_showNavigationBar-->", e);
            }
//        }

        // Allow a system property to override this. Used by the emulator.
        // See also hasNavigationBar().
        String navBarOverride = SystemProperties.get("qemu.hw.mainkeys");
        if (!"".equals(navBarOverride)) {
            if (navBarOverride.equals("1"))
                hasNavigationBar = false;
            else if (navBarOverride.equals("0"))
                hasNavigationBar = true;
        }

        Log.i(TAG, "hasNavigationBar== " + hasNavigationBar);
        int navigationBarHeight = hasNavigationBar ? res
                .getDimensionPixelSize(com.android.internal.R.dimen.navigation_bar_height) : 0;
        return navigationBarHeight;
	}
    //added by yumina for the A3500,A3300 reset crashed
    public static void setNewWallpaper(Context context,String wallpaperPath){
        FileInputStream is = null;
        try {
            is = new FileInputStream(new File(wallpaperPath));
            if (is != null) {
                WallpaperManager wm = (WallpaperManager) context.getSystemService(Context.WALLPAPER_SERVICE);
                Log.e("pad1121","setDefaultWallpaper 11111111111111111111111");
                wm.setStream(is);
                is.close();
                is = null;
                Intent intent = new Intent();
                String wallpaperName = "wallpaper.jpg";

                intent.setAction("com.lenovo.launcher.action.SET_WALLPAPER");
                intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
                intent.putExtra("name", wallpaperName);
                context.sendBroadcast(intent);
            }
        } catch (IOException e) {
//                      Log.d("liuyg1","Launcher->intAtTheBeginning. get Wallpaper failed"+ e);
        } catch (NotFoundException e) {
//                      Log.d("liuyg1","Launcher->intAtTheBeginning, wallpaper not found"+ e);
        }
    }

	public static void setDefaultWallpaper(Context context){
		InputStream is = null;			
		try {
			is = context.getResources().openRawResource(R.drawable.wallpaper_grass);

			if (is != null) {
				WallpaperManager wm = (WallpaperManager) context.getSystemService(Context.WALLPAPER_SERVICE);
				wm.setStream(is);
				is.close();
				is = null;
/* RK_ID:bug 10094 AUT:liuyg1@lenovo.com DATE: 2013-3-28 START */
				Intent intent = new Intent();
				String wallpaperName = "wallpaper_grass";
				intent.setAction("com.lenovo.launcher.action.SET_WALLPAPER");
				intent.setPackage(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF);
				intent.putExtra("name", wallpaperName);
				context.sendBroadcast(intent);
/* RK_ID:bug 10094 AUT:liuyg1@lenovo.com DATE: 2013-3-28 END */
			}
		} catch (IOException e) {
//			Log.d("liuyg1","Launcher->intAtTheBeginning. get Wallpaper failed"+ e);
		} catch (NotFoundException e) {
//			Log.d("liuyg1","Launcher->intAtTheBeginning, wallpaper not found"+ e);
		}
	}
	 /*RK_ID:RK_WIDGET_SETTING AUT:liuyg1@lenovo.com DATE: 2013-04-24 START */
    public static HashMap<String, String>getLeosWidgetSettingMap(){
        HashMap<String,String> settinMap = new HashMap<String,String>();
		settinMap.put(Constants.TASKMANAGERWIDGETVIEWHELPER, Constants.TASKMANAGERETTING);
		settinMap.put(Constants.WEATHERWIDGETVIEWHELPER, Constants.WEATHERSETTING);
		settinMap.put(Constants.WEATHERMAGICWIDGETVIEWHELPER,Constants.WEATHERSETTING);
		settinMap.put(Constants.WEATHERLOTUSWIDGETVIEWHELPER,Constants.WEATHERSETTING);
		settinMap.put(Constants.LOTUSDEFAULTVIEWHELPER, Constants.LOTUSSETTING);
		
    	return settinMap;
    }
    /*RK_ID:RK_WIDGET_SETTING AUT:liuyg1@lenovo.com DATE: 2013-04-24 END */

	public static void setDialogWidth(Window window, Resources resources) {
		// TODO Auto-generated method stub
		window.setGravity(Gravity.CENTER);
		//tesy by dining
		//set the window width 
        WindowManager.LayoutParams params = window.getAttributes();
        
        DisplayMetrics dm = resources.getDisplayMetrics();
        
        int widthPersent = resources.getInteger(R.integer.dialog_width_major);
        int width = 0;
		if(dm.widthPixels <= dm.heightPixels) width  = dm.widthPixels;
        else width = dm.heightPixels;
        params.width = (int) (width*(widthPersent/100.0f));
        window.setAttributes(params);
	}
	
	public static boolean filterFreezingOrientation() {
		String deviceModel = android.os.Build.MODEL;
		return deviceModel.contains("B6000") || deviceModel.contains("B8000")
				|| deviceModel.contains("S5000") || deviceModel.contains("B8080") || deviceModel.contains("A7600")
				|| deviceModel.contains("A3300") || deviceModel.contains("A3500") || deviceModel.contains("A5500");
	}	
	
	public static boolean mapReverseConfiguration() {
		String deviceModel = android.os.Build.MODEL;
		if (deviceModel.contains( "B6000" )
				|| deviceModel.contains("B8000") || deviceModel.contains("B8080") || deviceModel.contains("A7600")
				/*|| deviceModel.contains("A3300")*/ /*|| deviceModel.contains("A3500") *//*|| deviceModel.contains("A5500")*/) {
			return true;
		} else if (deviceModel.contains("S5000")) {
			return false;
		}

		return false;
	}
	
	public static boolean mapOrientationConfiguration()
	{
		String deviceModel = android.os.Build.MODEL;
		if(deviceModel.contains("B8080") || deviceModel.contains("A7600")
				/*|| deviceModel.contains("A3300")*/ /*|| deviceModel.contains("A3500") *//*|| deviceModel.contains("A5500")*/)
		{
			return true;
		}else
		{
			return false;
		}
	}
	
    public static void newfreezingOrientation(Activity ctx) {
	ctx.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
    public static void newfreezingOrientation(Activity ctx, boolean enable) {
     
	int target = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        if(!enable){
	    target = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }else{
            target = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }
        ctx.setRequestedOrientation(target);
    }

	public static void freezingOrientation(Activity ctx, boolean enable) {
		
		if( !filterFreezingOrientation() ){
			return;
		}
		
		if (enable) {
			int orientation = ctx.getResources().getConfiguration().orientation;

			Display d = ctx.getWindowManager().getDefaultDisplay();
			int rotation = d.getRotation();

			int target = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
			if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
				switch (rotation) {
				case Surface.ROTATION_0:
				case Surface.ROTATION_90:
					target = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
					break;
				case Surface.ROTATION_180:
				case Surface.ROTATION_270:
					target = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
					break;
				}

			} else {
				switch (rotation) {
				case Surface.ROTATION_0:
				case Surface.ROTATION_90:
					if(mapOrientationConfiguration())
					{
						target = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
					}else
					{
						target = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
					}
					break;
				case Surface.ROTATION_180:
				case Surface.ROTATION_270:
					if (mapReverseConfiguration()) {
						target = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
					} else {
						target = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
					}
					break;
				}
			}

			ctx.setRequestedOrientation(target);

		} else {
			ctx.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
	}
}





