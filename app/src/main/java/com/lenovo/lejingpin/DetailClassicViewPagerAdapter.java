package com.lenovo.lejingpin;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.SoftReference;
import android.content.SharedPreferences;


import com.lenovo.launcher2.customizer.Utilities;

import java.util.HashMap;

import android.net.Uri;
import com.lenovo.lejingpin.AsyncImageLoader;
import com.lenovo.lejingpin.LEJPConstant;
import com.lenovo.launcher.R;
import com.lenovo.lejingpin.AsyncImageLoader.ImageCallback;
import com.lenovo.lejingpin.AsyncImageLoader.ImagePathCallback;
import com.lenovo.lejingpin.network.AmsApplication;
import com.lenovo.lejingpin.network.WallpaperResponse.ApplicationData;

public class DetailClassicViewPagerAdapter extends PagerAdapter {
	private Context mcontext;
	ViewPagerLayout mpreviews[];
	private int mPosition;
	private int mtypeindex = 0;
	AsyncImageLoader asyncImageLoader;
	LEJPConstant mLeConstant;
	private HashMap<Integer, SoftReference<Drawable>> mThumbnailList = new HashMap<Integer, SoftReference<Drawable>>();
	private final int SET_IMAGE_DRAWABLE = 1;
	private final int LOAD_BIG_PREVIEW_IMG = 2;
	private final int SET_BIG_IMAGE_DRAWABLE = 3;

	private class Holder {
		public ImageView mimg;
		public Drawable mdb;
		public int mpos;

		public Holder(ImageView img, Drawable db, int pos) {
			mimg = img;
			mdb = db;
			mpos = pos;
		}
	}

	public DetailClassicViewPagerAdapter(Context context,
			AsyncImageLoader asyncimageloader, LEJPConstant leconstant, int type) {
		mcontext = context;
		asyncImageLoader = asyncimageloader;
		mLeConstant = leconstant;
		mtypeindex = type;
        mThumbnailList.clear();
	}

	public void setPreviewInfo(ViewPagerLayout previewinfos[]) {
		mpreviews = previewinfos;
	}

	public void setPosition(int Position) {
		mPosition = Position;
	}

	// 销毁arg1位置的界面
	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		if (arg1 < mpreviews.length){
			((ViewPager) arg0).removeView((View)mpreviews[arg1]);
		}
	}

	@Override
	public void finishUpdate(View arg0) {
		// TODO Auto-generated method stub
	}

	// 获得当前界面数
	@Override
	public int getCount() {
		if (mpreviews != null)
			return mpreviews.length;
		return 0;
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
                        case 0:
                           Holder aholder = (Holder) msg.obj;
                           aholder.mimg.setImageDrawable(aholder.mdb);
                        break;
			case SET_IMAGE_DRAWABLE:
				Log.i("XXXX","=== SET_IMAGE_DRAWABLE");
				handler.removeMessages(LOAD_BIG_PREVIEW_IMG);
				Holder holder = (Holder) msg.obj;
				if(holder.mdb == null) Log.i("XXXX","=== SET_IMAGE_DRAWABLE, db is null");
				holder.mimg.setImageDrawable(holder.mdb);
				handler.sendMessageDelayed(handler.obtainMessage(LOAD_BIG_PREVIEW_IMG, holder.mpos), 100);
				break;
			case SET_BIG_IMAGE_DRAWABLE:
				Log.i("XXXX","=== SET_BIG_IMAGE_DRAWABLE");
				Holder big_holder = (Holder) msg.obj;
				if(big_holder.mdb == null) Log.i("XXXX","=== SET_BIG_IMAGE_DRAWABLE, db is null");
				big_holder.mimg.setImageDrawable(big_holder.mdb);
				break;
			case LOAD_BIG_PREVIEW_IMG:
				Log.i("XXXX","=== LOAD_BIG_PREVIEW_IMG");
				//load big pic for preview later
				final ApplicationData appdata = mLeConstant.mServiceWallPaperDataList.get(mPosition);
				final String bigPicUrl = appdata.getIconUrl();
				final int tpos = msg.arg2;
				asyncImageLoader.loadDrawable(mpreviews[tpos].mimage,
						bigPicUrl, 0, tpos, new ImageCallback() {
							public void imageLoaded(final View img,
									final Drawable imageDrawable, int position,
									int j) {
								Log.i("XXXX","33333 imageloaded, big pic url = "+bigPicUrl);
								try {
									ApplicationData data = mLeConstant.mServiceWallPaperDataList.get(mPosition);
									SoftReference<Drawable> drawable = new SoftReference<Drawable>(imageDrawable);
									if (data != null) {
										data.SetPriviewDrawable(drawable);
										String bigPicPath = DetailClassicActivity.getLocationToSave(bigPicUrl, 0);
										appdata.setIconUrl(bigPicPath);
										DetailClassicActivity.saveDownloadInfo(mcontext,
														DetailClassicActivity.SP_WALLPAPER_ICON_URL,
														appdata.getPackage_name(),
														appdata.getIconUrl());
										copySharedPreferencesToSDCard(mcontext,
												DetailClassicActivity.SP_WALLPAPER_ICON_URL,
												DetailClassicActivity.SP_WALLPAPER_ICON_URL);
									}
									if (img instanceof ImageView) {
										Message message = handler.obtainMessage(SET_BIG_IMAGE_DRAWABLE,
												new Holder((ImageView)img,drawable.get(), tpos));
										handler.sendMessage(message);
									}
								} catch (Exception e) {
									Log.d("DetailClassicViewPagerAdapter","error=" + e);
								}
							}
						});
				break;
			}
		}
	};
	
	// 初始化arg1位置的界面
	@Override
	public Object instantiateItem(View arg0, int arg1) {
		final int tpos = arg1;
		if(arg1 >= getCount()){
			return null;
		}
		switch(mtypeindex){
		
		//yangmao add for lock
		
		case 12: // local lock
			if (mLeConstant.mServiceLocalLockAmsDataList == null)
				return null;
			if (mLeConstant.mServiceLocalLockAmsDataList.size() - 1 >= mPosition) {
				final AmsApplication data = mLeConstant.mServiceLocalLockAmsDataList
						.get(mPosition);
				if (!data.getIsNative()) {
					final String[] PicUrl = data.thumbpaths;
					File icon = new File(PicUrl[tpos]);
					mpreviews[arg1].mimage.setImageURI(Uri.fromFile(icon));
					break;
				} 
				Log.e("yumina", "arg1==========================" + arg1);
				if (arg1 < (data.getpreviewResId()).size()) {
					if (data.getpreviewResId().size() == 1) {
						if (arg1 == 0) {
							mpreviews[arg1].mimage.setImageDrawable((data
									.getpreviewResId()).get(arg1));
						}
					} else {
						mpreviews[arg1].mimage.setImageDrawable((data
								.getpreviewResId()).get(arg1));
					}
				}
			}
			break;
		
		case 11: // local theme
			if (mLeConstant.mServiceLocalThemeAmsDataList == null)
				return null;
			if (mLeConstant.mServiceLocalThemeAmsDataList.size() - 1 >= mPosition) {
				final AmsApplication data = mLeConstant.mServiceLocalThemeAmsDataList
						.get(mPosition);
				if (!data.getIsNative()) {
					Log.e("yumina", "data is not Native: arg1========" + arg1); 
					final String[] PicUrl = data.thumbpaths;
					File icon = new File(PicUrl[tpos]);
					mpreviews[arg1].mimage.setImageURI(Uri.fromFile(icon));
					break;
				} 
				Log.e("yumina", "arg1========" + arg1 + ",mposition = " + mPosition
						+", previewsize = "+ data.getpreviewResId().size());
				if (arg1 < (data.getpreviewResId()).size()) {
					if (data.getpreviewResId().size() == 1) {
						if (arg1 == 0) {
							mpreviews[arg1].mimage.setImageDrawable((data
									.getpreviewResId()).get(arg1));
						}
						boolean flag = false;
						if (mThumbnailList.containsKey(1)) {
							Log.e("yumina", "mThumbnailList containsKey 1");
							Drawable tmppic = mThumbnailList.get(1).get();
							if (tmppic != null) {
								mpreviews[1].mimage.setImageDrawable(tmppic);
							} else {
								flag = true;
							}
						}
						if (flag || !mThumbnailList.containsKey(1)) {
							Log.e("yumina","getDrawableFromAPK: data.getPackage_name()");
							Drawable imageDrawable = getDrawableFromAPK(
									data.getPackage_name(), 1);

							mThumbnailList.put(1, new SoftReference<Drawable>(
									imageDrawable));
							mpreviews[1].mimage.setImageDrawable(imageDrawable);
						}
					} else {
						mpreviews[arg1].mimage.setImageDrawable((data
								.getpreviewResId()).get(arg1));
					}
				}
			}
			break;
		case 10: // local wallpaper
			if (mLeConstant.mServiceLocalWallPaperDataList.size() - 1 >= mPosition) {
				final ApplicationData appdata = mLeConstant.mServiceLocalWallPaperDataList
						.get(mPosition);
				if (!appdata.getIsNative()) {
					Log.e("yumina", "drawable 33333333333333333333333333="
							+ appdata.isDynamic);
					if (appdata.isDynamic == 1
							&& (appdata.getPreviewAddr() == null || appdata
									.getPreviewAddr().equals(""))) {
						SoftReference<Drawable> thumb = appdata
								.getthumbdrawable();
						if (thumb == null || thumb.get() == null) {
							String mPkgName = appdata.getPackage_name();
							Drawable tmpdraw = getThumbnailFromApk(mPkgName);
							appdata.setthumbdrawable(new SoftReference<Drawable>(
									tmpdraw));
							mpreviews[arg1].mimage.setImageDrawable(tmpdraw);
						} else {
							mpreviews[arg1].mimage.setImageDrawable(appdata
									.getthumbdrawable().get());
						}
					} else {
						if (appdata.getIsDelete()) {// system/etc/localwallpaper
							SoftReference<Drawable> thumb = appdata
									.getthumbdrawable();
							if (thumb == null || thumb.get() == null) {

								final String icUrl = appdata.getUrl();
								Drawable d = Drawable.createFromPath(icUrl);
								appdata.setthumbdrawable(new SoftReference<Drawable>(d));
								mpreviews[arg1].mimage.setImageDrawable(d);
							} else {
								mpreviews[arg1].mimage.setImageDrawable(appdata
										.getthumbdrawable().get());
							}
						} else {
							SoftReference<Drawable> thumb = appdata
									.getthumbdrawable();
							if (thumb == null || thumb.get() == null) {
								final String PicUrl = appdata.getIconUrl();
								if(PicUrl != null && !PicUrl.equals("") &&
										PicUrl.startsWith(LEJPConstant.getDownloadPath())){
									Drawable d = Drawable.createFromPath(PicUrl);
									SoftReference<Drawable> drawable = new SoftReference<Drawable>(d);
									appdata.setthumbdrawable(drawable);
									mpreviews[arg1].mimage.setImageDrawable(drawable.get());
								}else{
									Drawable d = Drawable.createFromPath(appdata.getPreviewAddr());
									SoftReference<Drawable> drawable = new SoftReference<Drawable>(d);
									appdata.setthumbdrawable(drawable);
									mpreviews[arg1].mimage.setImageDrawable(drawable.get());
								}
							} else {
								mpreviews[arg1].mimage.setImageDrawable(appdata
										.getthumbdrawable().get());
							}
						}
					}
				} else {
					mpreviews[arg1].mimage.setImageResource(appdata
							.getpreviewdrawableresid());
				}
			}
			break;
		case 0: //online wallpaper
			if(mLeConstant.mServiceWallPaperDataList.size()-1 >= mPosition){
			final ApplicationData appdata = mLeConstant.mServiceWallPaperDataList.get(mPosition);
			final SoftReference<Drawable> drawable = appdata.getPriviewDrawable();
			final String PicUrl = appdata.getPreviewAddr();
			final String bigPicUrl = appdata.getIconUrl();
			if(drawable != null && drawable.get() != null){
				Log.i("XXXX","setimage from drawable");
				mpreviews[arg1].mimage.setImageDrawable(drawable.get());
			}else{
				//check big pic first
				if(isUrlLocalAddr(bigPicUrl)){ //local big pic exists
					Log.i("XXXX","bigPicUrl is local");
					asyncImageLoader.loadDrawable(mpreviews[arg1].mimage, bigPicUrl,
							0, tpos, new ImageCallback() {
								public void imageLoaded(final View img, final Drawable imageDrawable, int position, int j) {
									Log.i("XXXX","1111 imageloaded");
									try{
										ApplicationData data = mLeConstant.mServiceWallPaperDataList.get(mPosition);
										SoftReference<Drawable> drawable = new SoftReference<Drawable>(imageDrawable);
										if(data != null){
											data.SetPriviewDrawable(drawable);
										}
										if(img instanceof ImageView){
											Message message = handler.obtainMessage(SET_BIG_IMAGE_DRAWABLE, new Holder((ImageView) img, drawable.get(), tpos));
											handler.sendMessage(message);
										}
									}catch (Exception e) {
										Log.d("DetailClassicViewPagerAdapter", "error=" + e);
									}
								}
							});
				}else{
					asyncImageLoader.loadDrawable(mpreviews[arg1].mimage, PicUrl,
							0, tpos, new ImageCallback() {
								public void imageLoaded(final View img, final Drawable imageDrawable, int position, int j) {
									Log.i("XXXX","2222 imageloaded, pic url = "+PicUrl);
									try{
										ApplicationData data = mLeConstant.mServiceWallPaperDataList.get(mPosition);
										SoftReference<Drawable> drawable = new SoftReference<Drawable>(imageDrawable);
										if(data != null){
											data.SetPriviewDrawable(drawable);
										}
										if(img instanceof ImageView){
											Log.i("XXXX","2222 imageloaded, send msg SET_IMAGE_DRAWABLE");
//											Message message = handler.obtainMessage(SET_IMAGE_DRAWABLE, new Holder((ImageView) img, imageDrawable, tpos));
											Message message = handler.obtainMessage(SET_IMAGE_DRAWABLE, new Holder((ImageView) img, drawable.get(), tpos));
											handler.sendMessage(message);
										}
									}catch (Exception e) {
										Log.d("DetailClassicViewPagerAdapter", "error=" + e);
									}
								}
							});
				}
			}
        }
		break;
		case 1:
            if(mLeConstant.mServiceThemeAmsDataList.size()-1 >= mPosition){
			final AmsApplication data = mLeConstant.mServiceThemeAmsDataList
					.get(mPosition);
			if (data.getIsPath()) {
				SoftReference<Drawable>[] drawables = data.thumbdrawables;
				final String[] PicUrl = data.thumbpaths;
				final int len = PicUrl.length;
				if (drawables == null) {
					drawables = mLeConstant.mServiceThemeAmsDataList
							.get(mPosition).thumbdrawables = new SoftReference[len];
					for (int i = 0; i < PicUrl.length; i++) {
						mLeConstant.mServiceThemeAmsDataList.get(mPosition).thumbdrawables[i] = null;
						drawables[i] = null;
					}
					mLeConstant.mServiceThemeAmsDataList.get(mPosition).thumbdrawables[tpos] = null;
					for (int jj = 0; jj < PicUrl.length; jj++) {
						asyncImageLoader.loadDrawable(mpreviews[tpos].mimage,
								PicUrl[jj], tpos, jj, new ImageCallback() {
									public void imageLoaded(final View image, Drawable imageDrawable,
											int position, int j) {
										try{
											AmsApplication data = mLeConstant.mServiceThemeAmsDataList.get(mPosition);
											if(data != null){
												//mLeConstant.mServiceThemeAmsDataList.get(mPosition).thumbdrawables[j] = new SoftReference<Drawable>(
												//imageDrawable);
											}
											if (j == position && image instanceof ImageView) {
												Message message = handler.obtainMessage(0,new Holder((ImageView) image,imageDrawable, tpos));
												handler.sendMessage(message);
											}
										}catch (Exception e) {
											Log.d("DetailClassicViewPagerAdapter", "error=" + e);
										}
									}
								});
					}
				} else {
					if (drawables[tpos] == null) {
						// mpreviews[arg1].mimage.setImageResource(R.drawable.lemagicdownload_push_app_icon_def);
						asyncImageLoader.loadDrawable(mpreviews[arg1].mimage,
								PicUrl[tpos], tpos, 0, new ImageCallback() {
									public void imageLoaded(View img,
											Drawable imageDrawable,
											int position, int j) {
										try {
											mLeConstant.mServiceThemeAmsDataList
													.get(mPosition).thumbdrawables[j] = new SoftReference<Drawable>(
													imageDrawable);
										} catch (Exception e) {
											Log.d("DetailClassicViewPagerAdapter",
													"postion error=" + e);
										}
										if(img instanceof ImageView){
										Message message = handler.obtainMessage(0, new Holder((ImageView) img,imageDrawable, tpos));
										handler.sendMessage(message);
										}
									}
								});

					} else {
						Drawable db = drawables[tpos].get();
						if (db != null) {
							mpreviews[arg1].mimage.setImageDrawable(db);
						} else {
							// mpreviews[arg1].mimage.setImageResource(R.drawable.lemagicdownload_push_app_icon_def);
							asyncImageLoader.loadDrawable(
									mpreviews[arg1].mimage, PicUrl[tpos], tpos,
									0, new ImageCallback() {
										public void imageLoaded(View img,Drawable imageDrawable, int position, int j) {
											try{
												mLeConstant.mServiceThemeAmsDataList.get(mPosition).thumbdrawables[j] = new SoftReference<Drawable>(
														imageDrawable);
												if(img instanceof ImageView){
												Message message = handler.obtainMessage(0, new Holder((ImageView) img,imageDrawable, tpos));
												handler.sendMessage(message);
												}
											}catch (Exception e) {
												Log.d("DetailClassicViewPagerAdapter",
														"postion error=" + e);
											}
										}
									});
						}
					}
				}
			} else {
				// mpreviews[arg1].mimage.setImageResource(R.drawable.lemagicdownload_push_app_icon_def);
				asyncImageLoader.requestAppInfo(mpreviews[arg1].mimage,
						mcontext, mPosition, data.getPackage_name(),
						data.getApp_versioncode(), new ImagePathCallback() {
							public void ImagePathLoaded(String[] imageUrls,
									int position, final ImageView imag) {
								if (imageUrls != null) {
									mLeConstant.mServiceThemeAmsDataList.get(position).thumbpaths = imageUrls;
									mLeConstant.mServiceThemeAmsDataList.get(position).setIsPath(true);
									mLeConstant.mServiceThemeAmsDataList.get(position).thumbdrawables = new SoftReference[imageUrls.length];
									asyncImageLoader.loadDrawable(imag, imageUrls[tpos], tpos, mPosition, new ImageCallback() {
										public void imageLoaded(View image,Drawable imageDrawable,int postion, int j) {
											Log.d("d", "postion=" + postion);
											try{
												mLeConstant.mServiceThemeAmsDataList.get(postion).thumbdrawables[j] = new SoftReference<Drawable>(
															imageDrawable);
												if(image instanceof ImageView){
													Message message = handler.obtainMessage(0,new Holder((ImageView) image,imageDrawable, tpos));
													handler.sendMessage(message);
												}
												}catch (Exception e) {
													Log.d("DetailClassicViewPagerAdapter", "postion error=" + e);
												}
											}
										});
								}
							}
						});
			}
          }
			break;
			
		//yangmao add for lock	
		case 2:	

            if(mLeConstant.mServiceLockAmsDataList.size()-1 >= mPosition){
			final AmsApplication data = mLeConstant.mServiceLockAmsDataList
					.get(mPosition);
			if (data.getIsPath()) {
				SoftReference<Drawable>[] drawables = data.thumbdrawables;
				final String[] PicUrl = data.thumbpaths;
				final int len = PicUrl.length;
				if (drawables == null) {
					// mpreviews[arg1].mimage.setImageResource(R.drawable.lemagicdownload_push_app_icon_def);
					drawables = mLeConstant.mServiceLockAmsDataList
							.get(mPosition).thumbdrawables = new SoftReference[len];
					for (int i = 0; i < PicUrl.length; i++) {
						mLeConstant.mServiceLockAmsDataList.get(mPosition).thumbdrawables[i] = null;
						drawables[i] = null;
					}
					mLeConstant.mServiceLockAmsDataList.get(mPosition).thumbdrawables[tpos] = null;
					for (int jj = 0; jj < PicUrl.length; jj++) {
						asyncImageLoader.loadDrawable(mpreviews[tpos].mimage,
								PicUrl[jj], tpos, jj, new ImageCallback() {
									public void imageLoaded(final View image, Drawable imageDrawable,
											int position, int j) {
										try{
											AmsApplication data = mLeConstant.mServiceLockAmsDataList.get(mPosition);
											if(data != null){
												//mLeConstant.mServiceThemeAmsDataList.get(mPosition).thumbdrawables[j] = new SoftReference<Drawable>(
												//imageDrawable);
											}
											if (j == position && image instanceof ImageView) {
												Message message = handler.obtainMessage(0,new Holder((ImageView) image,imageDrawable, tpos));
												handler.sendMessage(message);
											}
										}catch (Exception e) {
											Log.d("DetailClassicViewPagerAdapter", "error=" + e);
										}
									}
								});
					}
				} else {
					if (drawables[tpos] == null) {
						// mpreviews[arg1].mimage.setImageResource(R.drawable.lemagicdownload_push_app_icon_def);
						asyncImageLoader.loadDrawable(mpreviews[arg1].mimage,
								PicUrl[tpos], tpos, 0, new ImageCallback() {
									public void imageLoaded(View img,
											Drawable imageDrawable,
											int position, int j) {
										try {
											mLeConstant.mServiceLockAmsDataList
													.get(mPosition).thumbdrawables[j] = new SoftReference<Drawable>(
													imageDrawable);
										} catch (Exception e) {
											Log.d("DetailClassicViewPagerAdapter",
													"postion error=" + e);
										}
										if(img instanceof ImageView){
										Message message = handler.obtainMessage(0, new Holder((ImageView) img,imageDrawable, tpos));
										handler.sendMessage(message);
										}
									}
								});

					} else {
						Drawable db = drawables[tpos].get();
						if (db != null) {
							mpreviews[arg1].mimage.setImageDrawable(db);
						} else {
							// mpreviews[arg1].mimage.setImageResource(R.drawable.lemagicdownload_push_app_icon_def);
							asyncImageLoader.loadDrawable(
									mpreviews[arg1].mimage, PicUrl[tpos], tpos,
									0, new ImageCallback() {
										public void imageLoaded(View img,Drawable imageDrawable, int position, int j) {
											try{
												mLeConstant.mServiceLockAmsDataList.get(mPosition).thumbdrawables[j] = new SoftReference<Drawable>(
														imageDrawable);
												if(img instanceof ImageView){
												Message message = handler.obtainMessage(0, new Holder((ImageView) img,imageDrawable, tpos));
												handler.sendMessage(message);
												}
											}catch (Exception e) {
												Log.d("DetailClassicViewPagerAdapter",
														"postion error=" + e);
											}
										}
									});
						}
					}
				}
			} else {
				// mpreviews[arg1].mimage.setImageResource(R.drawable.lemagicdownload_push_app_icon_def);
				asyncImageLoader.requestAppInfo(mpreviews[arg1].mimage,
						mcontext, mPosition, data.getPackage_name(),
						data.getApp_versioncode(), new ImagePathCallback() {
							public void ImagePathLoaded(String[] imageUrls,
									int position, final ImageView imag) {
								if (imageUrls != null) {
									mLeConstant.mServiceLockAmsDataList.get(position).thumbpaths = imageUrls;
									mLeConstant.mServiceLockAmsDataList.get(position).setIsPath(true);
									mLeConstant.mServiceLockAmsDataList.get(position).thumbdrawables = new SoftReference[imageUrls.length];
									asyncImageLoader.loadDrawable(imag, imageUrls[tpos], tpos, mPosition, new ImageCallback() {
										public void imageLoaded(View image,Drawable imageDrawable,int postion, int j) {
											Log.d("d", "postion=" + postion);
											try{
												mLeConstant.mServiceLockAmsDataList.get(postion).thumbdrawables[j] = new SoftReference<Drawable>(
															imageDrawable);
												if(image instanceof ImageView){
													Message message = handler.obtainMessage(0,new Holder((ImageView) image,imageDrawable, tpos));
													handler.sendMessage(message);
												}
												}catch (Exception e) {
													Log.d("DetailClassicViewPagerAdapter", "postion error=" + e);
												}
											}
										});
								}
							}
						});
			}
          }
			
			break;
			
			
		default:
			break;
		}
		
		((ViewPager) arg0).addView((View)mpreviews[arg1], 0);
		return mpreviews[arg1];
	}

	// 判断是否由对象生成界面
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		//modified by mohl because sonar reported this error 
//		return (arg0 == arg1);
		return (arg0.equals(arg1));
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public Parcelable saveState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
		// TODO Auto-generated method stub

	}
    public void clearHashmap(){
        mThumbnailList.clear();
    }
    
    private static boolean isUrlLocalAddr(String url){
    	return url.startsWith(LEJPConstant.getDownloadPath());
    }
    private static boolean copySharedPreferencesToSDCard(Context context, String spName, String filename){
   	 LocalDataManager mgr = LocalDataManager.getInstance(context);
   	 boolean res = mgr.writeDataToSDCard(spName, filename);
   	 return res;
    }
    
    private Drawable getDrawableFromAPK(String pkgName ,int index){
        Drawable thumbnail = mcontext.getResources().getDrawable(R.drawable.lemagicdownload_push_app_icon_def);

        SharedPreferences sp = mcontext.getSharedPreferences("local_themepreview _url", 0);
        boolean bReLoadPreview = false;
        if(sp.contains(pkgName)){
            String preview_files = sp.getString(pkgName, "");
            Log.d("yumina","DetailClassicPageAdapter=========== getLocalTheme prview files: " + preview_files);
            String[] files = preview_files.split(",");
            int ai=0;
            for(String f : files){
                File file = new File(f);
                if(file.exists()){
                    if(ai==index){
                        thumbnail =  Drawable.createFromPath(f);
                        return thumbnail;
                    }
                    ai++;
                }else{
                    bReLoadPreview = true;
                }
            }
        }
        if((sp.contains(pkgName) && bReLoadPreview) || !sp.contains(pkgName)){
            String[] previewImages = null;
            String previewNameString = "config_theme_previews";
            Context  mFriendContext = null;
            try{
                 mFriendContext = mcontext.createPackageContext(pkgName, Context.CONTEXT_IGNORE_SECURITY);
            } catch (Exception e) {
                return thumbnail;
            }

            previewImages = Utilities.findStringArrayByResourceName(previewNameString, mFriendContext);
            if (previewImages == null) {
                String previewName = "themepreview";
                Log.d("mohl","getLocalTheme: previewImages is null! ");
                Drawable brawInfo = Utilities.findDrawableByResourceName("themepreview", mFriendContext);
                if (brawInfo == null) {
                    brawInfo = mcontext.getResources().getDrawable(R.drawable.ic_launcher_theme_shortcut);
                }
                thumbnail = brawInfo;
            } else {
                Log.d("mohl","getLocalTheme: previewImages is not null, length =  " + previewImages.length);
                String previewName = previewImages[index];
                Log.d("mohl","getLocalTheme: previewImages j = " + index + ", previewName = " + previewName);
                Drawable arawInfo = Utilities.findDrawableByResourceName(previewName, mFriendContext);
                if (arawInfo == null) {
                   arawInfo = mcontext.getResources().getDrawable(R.drawable.ic_launcher_theme_shortcut);
                }
                thumbnail = arawInfo;
            }
        }
        return thumbnail;
    }
      private Drawable getThumbnailFromApk(String mPkgName){
        Log.e("Thumbnail","getInstalledLiveWallpaper the pkgname ="+mPkgName);
        Drawable thumbnail=mcontext.getResources().getDrawable(R.drawable.le_wallpaper_magicdownload_push_app_icon_def);
        String previewNameString = "thumbnail";
        Context mFriendContext = null;
        try {
            mFriendContext = mcontext.createPackageContext(mPkgName, Context.CONTEXT_IGNORE_SECURITY);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Thumbnail","222222getInstalledLiveWallpaper the pkgname ="+mPkgName);
            return thumbnail;
        }

        if(mFriendContext != null ){
            thumbnail = Utilities.findDrawableByResourceName(previewNameString, mFriendContext);
            Log.e("Thumbnail","555555555555tInstalledLiveWallpaper the pkgname ="+thumbnail);
        }
        return thumbnail;
    }

}
