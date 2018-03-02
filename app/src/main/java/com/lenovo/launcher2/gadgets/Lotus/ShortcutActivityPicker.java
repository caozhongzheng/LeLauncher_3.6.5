/*
 * Copyright (C) 2009 The Android Open Source Project
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

package com.lenovo.launcher2.gadgets.Lotus;

import android.app.Activity;
import android.graphics.ColorFilter;
import android.util.DisplayMetrics;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.ImageView;

import android.app.ListActivity;
import android.util.Log;

import android.widget.AdapterView;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.lenovo.launcher.R;
import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher2.commoninterface.ApplicationInfo;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Debug.R2;
import com.lenovo.launcher2.customizer.Utilities;
import com.lenovo.launcher2.gadgets.Lotus.ShortcutActivityPicker.PickAdapter.Item;


/**
 * Displays a list of all activities matching the incoming
 * {@link Intent#EXTRA_INTENT} query, along with any injected items.
 */
public class ShortcutActivityPicker extends ListActivity implements AdapterView.OnItemClickListener {

	private static String TAG = "ShortcutActivityPicker";
	private static final int MENU_RESET_ITEMID = 1;
	private static final int MENU_RESET_SIGNALITEMID = MENU_RESET_ITEMID + 1;

	private static final String LOTUSINFO = "lotuspage";
	private static final String PAGENUM = "PAGENUM";
	int mPageNum;
	public static final String ACTION_LOTUS_CHANGE = "com.android.launcher2.gadgets.LOTUSCHANGE";
	public static final String ACTION_LOTUS_ALL_RESET = "com.android.launcher2.gadgets.LOTUSALLRESET";
	public static final String ACTION_LOTUS_PAGENUM_RESET = "com.android.launcher2.gadgets.LOTUSPAGENUMALLRESET";
	/**
	 * Adapter of items that are displayed in this dialog.
	 */
	private PickAdapter mAdapter;

	/**
	 * Base {@link Intent} used when building list.
	 */
	private Intent mBaseIntent;

	public ArrayList<String> addedAppClassNameList = null;
	public ArrayList<String> hideAppPackageNameList = null;
	/*** RK_ID:RK_REDSUQARE_1745 AUT:zhanglz1@lenovo.com. ***/
	private SharedPreferences preferences = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setup_pererence_layout);
		TextView title = (TextView)findViewById(R.id.dialog_title);
		title.setText(R.string.menu_addapp);
		ImageView icon = (ImageView)findViewById(R.id.dialog_icon);
		icon.setImageResource(R.drawable.dialog_title_back_arrow);
		title.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		icon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		final Intent intent = getIntent();

		// Read base intent from extras, otherwise assume default

		Parcelable parcel = intent.getParcelableExtra(Intent.EXTRA_INTENT);
		if (parcel instanceof Intent) {
			mBaseIntent = (Intent) parcel;
		} else {
			mBaseIntent = new Intent(Intent.ACTION_MAIN, null);
			mBaseIntent.addCategory(Intent.CATEGORY_DEFAULT);
		}
		// Create dialog parameters
		addedAppClassNameList = intent.getStringArrayListExtra("APPCLASSNAME");
		hideAppPackageNameList = intent.getStringArrayListExtra("HIDEAPPLIST");
		mPageNum = intent.getIntExtra(PAGENUM, -1);
		/*** RK_ID:RK_REDSUQARE_1745 AUT:zhanglz1@lenovo.com. ***/
		preferences = getSharedPreferences(LOTUSINFO, 0);

		// Build list adapter of pickable items
		List<PickAdapter.Item> items = getItems();
		/*** RK_ID:RK_PICKLIST_HIDE_NAVI_1923 AUT:zhanglz1@lenovo.com. S***/

		String intent_str4 = preferences.getString("leaf_left_top_solid_first", "");
		String intent_str5 = preferences.getString("leaf_right_top_solid_first", "");
		String intent_str6 = preferences.getString("leaf_left_bottom_solid_first", "");
		String intent_str7 = preferences.getString("leaf_right_bottom_solid_first", "");
		String str4 =null;
		String str5 =null;
		String str6 =null;
		String str7 =null;
		if (intent_str4 != null && intent_str4.length()!=0){
			str4 = intent_str4.replace("/", "");
		}
		if (intent_str5 != null && intent_str5.length()!=0){
			str5 = intent_str5.replace("/", "");
        }
		if (intent_str6 != null && intent_str6.length()!=0){
			str6 = intent_str6.replace("/", "");
		}
		if (intent_str7 != null && intent_str7.length() != 0) {
			str7 = intent_str7.replace("/", "");
		}
		String str = str4 + str5 + str6 + str7;
		/*RK_ID: RK_LEFAMILY_NEW_NEED . AUT: zhanglz1 . DATE: 2012-10-20 . S*/
		/* 增加乐家族假图标*/
		if (str.contains(LotusUtilites.FAKE_LE_FAMILY)) {
			/*** RK_ID:RK_LENAVI_WITHOUT_APK_1791 AUT:zhanglz1@lenovo.com. S ***/
			BitmapDrawable customLefamily = setCustomIcon(R.drawable.lefamily_default);
			String leFamiltTitle = this.getResources().getString(
					R.string.title_lefamily);
			boolean add = false;
			if (addedAppClassNameList.toString().contains(LotusUtilites.FAKE_LE_FAMILY)
					) {
				add = true;
			} else {
				add = false;
			}
			PickAdapter.Item fakeLefamily = new PickAdapter.Item(this, leFamiltTitle,
					customLefamily, add);
			fakeLefamily.className = LotusUtilites.FAKE_LE_FAMILY;
			fakeLefamily.packageName = LotusUtilites.FAKE_LE_FAMILY + "fakefolder";
			items.add(fakeLefamily);
			/*** RK_ID:RK_PICKLIST_RESORT_1795 AUT:zhanglz1@lenovo.com. ***/
			/*RK_ID: RK_LEFAMILY_NEW_NEED . AUT: zhanglz1 . DATE: 2012-10-20 . E*/

		}
		/*** RK_ID:RK_PICKLIST_HIDE_NAVI_1923 AUT:zhanglz1@lenovo.com. E***/

		Collections.sort(items, APP_NAME_COMPARATOR);
		/*** RK_ID:RK_LENAVI_WITHOUT_APK_1791 AUT:zhanglz1@lenovo.com. E ***/
		mAdapter = new PickAdapter(this, items);
		getListView().setAdapter(mAdapter);
		getListView().setOnItemClickListener(this);
		Log.e(TAG, "setOnItemClickListener");
		if(!SettingsValue.isRotationEnabled(this)){
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	/*** RK_ID:RK_PICKLIST_RESORT_1795 AUT:zhanglz1@lenovo.com. S ***/
/*	@SuppressWarnings("unchecked")
	private void resort(List<PickAdapter.Item> items) {
		Collections.sort(items, new Comparator() {
			@Override
			public int compare(final Object arg0, final Object arg1) {
				// TODO Auto-generated method stub
				final PickAdapter.Item other0 = (PickAdapter.Item) arg0;
				final PickAdapter.Item other1 = (PickAdapter.Item) arg1;
				final String str0 = other0.label.toString();
				final String str1 = other1.label.toString();
				return str0.compareTo(str1);
			}
		});
	}*/
    private static final Collator sCollator = Collator.getInstance();

	private static final Comparator<PickAdapter.Item> APP_NAME_COMPARATOR = new Comparator<PickAdapter.Item>() {
		@Override
		public int compare(PickAdapter.Item a, PickAdapter.Item b) {
			int result = sCollator.compare(a.label.toString(),b.label.toString());
			if (result == 0) {
				result = a.packageName.compareTo(b.packageName);
			}
			return result;
		}
	
	};

	/*** RK_ID:RK_PICKLIST_RESORT_1795 AUT:zhanglz1@lenovo.com. E ***/
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.e(TAG, "onItemClick");

		/*if (((PickAdapter.Item) mAdapter.getItem(position)).misadded) {
			return;
		}*/

		final Intent intent = getIntentForPosition(position);

		Intent it = new Intent();
		it.setAction(ACTION_LOTUS_CHANGE);
		it.putExtra("packageName", intent.getComponent().getPackageName());
		it.putExtra("className", intent.getComponent().getClassName());
		R2.echo("Touch 1" + it.toUri(0));
		sendBroadcast(it);
		
		setLotusPageInfo(mPageNum, intent);
		R2.echo("Touch 2");
		finish();
	}

	/**
	 * Handle canceled dialog by passing back {@link Activity#RESULT_CANCELED}.
	 */
	public void onCancel(DialogInterface dialog) {
		setResult(Activity.RESULT_CANCELED);
		finish();
	}

	/**
	 * Build the specific {@link Intent} for a given list position. Convenience
	 * method that calls through to {@link PickAdapter.Item#getIntent(Intent)}.
	 */
	protected Intent getIntentForPosition(int position) {
		PickAdapter.Item item = (PickAdapter.Item) mAdapter.getItem(position);
		return item.getIntent(mBaseIntent);
	}

	/**
	 * Build and return list of items to be shown in dialog. Default
	 * implementation mixes activities matching {@link #mBaseIntent} from
	 * {@link #putIntentItems(Intent, List)} with any injected items from
	 * {@link Intent#EXTRA_SHORTCUT_NAME}. Override this method in subclasses to
	 * change the items shown.
	 */
	protected List<PickAdapter.Item> getItems() {
		PackageManager packageManager = getPackageManager();
		List<PickAdapter.Item> items = new ArrayList<PickAdapter.Item>();

		final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		final List<ResolveInfo> infolist = packageManager.queryIntentActivities(mainIntent, 0);
		/*** RK_ID:RK_PICKLIST_RESORT_1795 AUT:zhanglz1@lenovo.com. ***/
		// Collections.sort(infolist, new
		// ResolveInfo.DisplayNameComparator(packageManager));
		for (int j = 0; j < infolist.size(); j++) {
			ResolveInfo info = infolist.get(j);
			/*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-10-26 . PUR: for bug 171904 . S*/
			if (info.activityInfo.packageName.startsWith(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF)
				|| info.activityInfo.packageName.contains(SettingsValue.THEME_PACKAGE_NAME_PREF)
			    || info.activityInfo.packageName.contains(SettingsValue.THEME_PACKAGE_QIGAMELOCKSCREEN_PREF)) {
            	continue;
            }
			/*RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2012-10-26 . PUR: for bug 171904 . E*/
			
			String packageName = info.activityInfo.applicationInfo.packageName;
			if (hideAppPackageNameList != null) {
				if (hideAppPackageNameList.contains(packageName)) {
					continue;
				}
			}
            //bugfix 12633
            Item item = new PickAdapter.Item(this, packageManager, info, false);

			if (addedAppClassNameList != null) {
				String className = info.activityInfo.name;
				String packagename = info.activityInfo.packageName;
				String packageAndClassName = packagename+className;
                for(int i = 0 ; i <addedAppClassNameList.size();i++){
                	if (addedAppClassNameList.get(i).equals(packageAndClassName)) {
                		item.setAdded(true);
    				} 
                }
				items.add(item);

			} else {
				items.add(item);
			}
		}

		return items;
	}

	/**
	 * Fill the given list with any activities matching the base {@link Intent}.
	 */
	protected void putIntentItems(Intent baseIntent, List<PickAdapter.Item> items) {
		PackageManager packageManager = getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(baseIntent, 0 /*
																					 * no
																					 * flags
																					 */);
		/*** RK_ID:RK_PICKLIST_RESORT_1795 AUT:zhanglz1@lenovo.com. ***/
		// Collections.sort(list, new
		// ResolveInfo.DisplayNameComparator(packageManager));
		items.clear();

		final int listSize = list.size();
		for (int i = 0; i < listSize; i++) {
			ResolveInfo resolveInfo = list.get(i);
			if (addedAppClassNameList != null) {
				String className = resolveInfo.activityInfo.name;
				if (addedAppClassNameList.contains(className)) {
					items.add(new PickAdapter.Item(this, packageManager, resolveInfo, true));
				} else {
					items.add(new PickAdapter.Item(this, packageManager, resolveInfo, false));
				}
			} else {
				items.add(new PickAdapter.Item(this, packageManager, resolveInfo, false));
			}
		}
	}

	/**
	 * Adapter which shows the set of activities that can be performed for a
	 * given {@link Intent}.
	 */
	protected static  class PickAdapter extends BaseAdapter {

		/**
		 * Item that appears in a {@link PickAdapter} list.
		 */
		public static  class Item {
		/*	protected static IconResizer sResizer;

			protected IconResizer getResizer(Context context) {
				if (sResizer == null) {
					final Resources resources = context.getResources();
					int size = (int) resources.getDimension(android.R.dimen.app_icon_size);
					sResizer = new IconResizer(size, size, resources.getDisplayMetrics());
				}
				return sResizer;
			}*/

			CharSequence label;
			Drawable icon;
			String packageName;
			String className;
			boolean misadded;
			Bundle extras;

			/**
			 * Create a list item from given label and icon.
			 */
			Item(Context context, CharSequence label, Drawable icon, boolean added) {
				this.label = label;
				/*** RK_ID:RK_PICKLIST_UPDATE_1744 AUT:zhanglz1@lenovo.com. ***/
				/*** RK_ID:RK_LENAVI_WITHOUT_APK_1791 AUT:zhanglz1@lenovo.com. ***/
				this.icon = icon;
				this.misadded = added;
			}

			/**
			 * Create a list item and fill it with details from the given
			 * {@link ResolveInfo} object.
			 */
			Item(Context context, PackageManager pm, ResolveInfo resolveInfo, boolean added) {
				label = resolveInfo.loadLabel(pm);
				if (label == null && resolveInfo.activityInfo != null) {
					label = resolveInfo.activityInfo.name;
				}
				/*** RK_ID:RK_PICKLIST_UPDATE_1744 AUT:zhanglz1@lenovo.com. S ***/
				LauncherApplication app = (LauncherApplication) context.getApplicationContext();
				ApplicationInfo appInfo = new ApplicationInfo(context.getPackageManager(),
						resolveInfo, app.getIconCache(), null);

				icon = null;
				// icon =
				// getResizer(context).createIconThumbnail(resolveInfo.loadIcon(pm));
				BitmapDrawable bitmapDrawable = new BitmapDrawable(appInfo.iconBitmap);
				icon = bitmapDrawable;
				/*** RK_ID:RK_PICKLIST_UPDATE_1744 AUT:zhanglz1@lenovo.com. E ***/
				packageName = resolveInfo.activityInfo.applicationInfo.packageName;
				className = resolveInfo.activityInfo.name;
				misadded = added;
			}

			/**
			 * Build the {@link Intent} described by this item. If this item
			 * can't create a valid {@link android.content.ComponentName}, it
			 * will return {@link Intent#ACTION_CREATE_SHORTCUT} filled with the
			 * item label.
			 */
			Intent getIntent(Intent baseIntent) {
				Intent intent = new Intent(baseIntent);
				if (packageName != null && className != null) {
					// Valid package and class, so fill details as normal intent
					intent.setClassName(packageName, className);
					if (extras != null) {
						intent.putExtras(extras);
					}
				} else {
					// No valid package or class, so treat as shortcut with
					// label
					intent.setAction(Intent.ACTION_CREATE_SHORTCUT);
					intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, label);
				}
				return intent;
			}
			void setAdded(boolean b){
				this.misadded = b;
			}
		}

		private final LayoutInflater mInflater;
		private final List<Item> mItems;

		/**
		 * Create an adapter for the given items.
		 */
		public PickAdapter(Context context, List<Item> items) {
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mItems = items;
		}

		/**
		 * {@inheritDoc}
		 */
		public int getCount() {
			return mItems.size();
		}

		/**
		 * {@inheritDoc}
		 */
		public Object getItem(int position) {
			return mItems.get(position);
		}

		/**
		 * {@inheritDoc}
		 */
		public long getItemId(int position) {
			return position;
		}

		/**
		 * {@inheritDoc}
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.pick_item, parent, false);
			}

			Item item = (Item) getItem(position);
			TextView textView = (TextView) convertView.findViewById(R.id.app_textview);
			if (textView != null) {
				textView.setText(item.label);
				/*** RK_ID:RK_PICKLIST_UPDATE_1744 AUT:zhanglz1@lenovo.com. S ***/
				// textView.setCompoundDrawablesWithIntrinsicBounds(item.icon,
				// null, null, null);
			}
			ImageView image = (ImageView) convertView.findViewById(R.id.add_icon);
			if (item.misadded) {
				image.setVisibility(View.VISIBLE);
                image.setSelected(true);
			} else {
				image.setVisibility(View.GONE);
			}
			ImageView imageadd = (ImageView) convertView.findViewById(R.id.app_icon);
			if (imageadd != null) {
				imageadd.setImageDrawable(item.icon);
			}
			/*** RK_ID:RK_PICKLIST_UPDATE_1744 AUT:zhanglz1@lenovo.com. E ***/
			return convertView;
		}
	}

	/**
	 * Utility class to resize icons to match default icon size. Code is mostly
	 * borrowed from Launcher.
	 */
	/*** RK_ID:RK_LENAVI_WITHOUT_APK_1791 AUT:zhanglz1@lenovo.com. S ***/
	private BitmapDrawable setCustomIcon(int iconRes) {
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int style = SettingsValue.getIconStyleIndex(this);
		Drawable d = this.getResources().getDrawable(iconRes);
		/*RK_ID: RK_LEFAMILY_NEW_NEED . AUT: zhanglz1 . DATE: 2012-10-20 . S*/
		BitmapDrawable customLefamily = new BitmapDrawable(this.getResources(), Utilities
				.createIconBitmap(d, style, this));
		return customLefamily;
		/*RK_ID: RK_LEFAMILY_NEW_NEED . AUT: zhanglz1 . DATE: 2012-10-20 . E*/

	}

	/*** RK_ID:RK_LENAVI_WITHOUT_APK_1791 AUT:zhanglz1@lenovo.com. E ***/
	private static class IconResizer {
		private final int mIconWidth;
		private final int mIconHeight;

		private final DisplayMetrics mMetrics;
		private final Rect mOldBounds = new Rect();
		private final Canvas mCanvas = new Canvas();

		public IconResizer(int width, int height, DisplayMetrics metrics) {
			mCanvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG,
					Paint.FILTER_BITMAP_FLAG));

			mMetrics = metrics;
			mIconWidth = width;
			mIconHeight = height;
		}

		/**
		 * Returns a Drawable representing the thumbnail of the specified
		 * Drawable. The size of the thumbnail is defined by the dimension
		 * android.R.dimen.launcher_application_icon_size.
		 * 
		 * This method is not thread-safe and should be invoked on the UI thread
		 * only.
		 * 
		 * @param icon
		 *            The icon to get a thumbnail of.
		 * 
		 * @return A thumbnail for the specified icon or the icon itself if the
		 *         thumbnail could not be created.
		 */
/*		public Drawable createIconThumbnail(Drawable icon) {
			int width = mIconWidth;
			int height = mIconHeight;

			if (icon == null) {
				return new EmptyDrawable(width, height);
			}

			try {
				if (icon instanceof PaintDrawable) {
					PaintDrawable painter = (PaintDrawable) icon;
					painter.setIntrinsicWidth(width);
					painter.setIntrinsicHeight(height);
				} else if (icon instanceof BitmapDrawable) {
					// Ensure the bitmap has a density.
					BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
					Bitmap bitmap = bitmapDrawable.getBitmap();
					if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
						bitmapDrawable.setTargetDensity(mMetrics);

					}
					bitmapDrawable = null;
					bitmap = null;
				}
				int iconWidth = icon.getIntrinsicWidth();
				int iconHeight = icon.getIntrinsicHeight();

				if (iconWidth > 0 && iconHeight > 0) {
					if (width < iconWidth || height < iconHeight) {
						final float ratio = (float) iconWidth / iconHeight;

						if (iconWidth > iconHeight) {
							height = (int) (width / ratio);
						} else if (iconHeight > iconWidth) {
							width = (int) (height * ratio);
						}

						final Bitmap.Config c = icon.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565;
						Bitmap thumb = Bitmap.createBitmap(mIconWidth, mIconHeight, c);
						final Canvas canvas = mCanvas;
						canvas.setBitmap(thumb);
						// Copy the old bounds to restore them later
						// If we were to do oldBounds = icon.getBounds(),
						// the call to setBounds() that follows would
						// change the same instance and we would lose the
						// old bounds
						mOldBounds.set(icon.getBounds());
						final int x = (mIconWidth - width) / 2;
						final int y = (mIconHeight - height) / 2;
						icon.setBounds(x, y, x + width, y + height);
						icon.draw(canvas);
						icon.setBounds(mOldBounds);
						// noinspection deprecation
						icon = new BitmapDrawable(thumb);
						((BitmapDrawable) icon).setTargetDensity(mMetrics);
						thumb = null;
					} else if (iconWidth < width && iconHeight < height) {
						final Bitmap.Config c = Bitmap.Config.ARGB_8888;
						Bitmap thumb = Bitmap.createBitmap(mIconWidth, mIconHeight, c);
						final Canvas canvas = mCanvas;
						canvas.setBitmap(thumb);
						mOldBounds.set(icon.getBounds());
						final int x = (width - iconWidth) / 2;
						final int y = (height - iconHeight) / 2;
						icon.setBounds(x, y, x + iconWidth, y + iconHeight);
						icon.draw(canvas);
						icon.setBounds(mOldBounds);
						// noinspection deprecation
						icon = new BitmapDrawable(thumb);
						((BitmapDrawable) icon).setTargetDensity(mMetrics);
						thumb = null;
					}
				}

			} catch (Exception t) {
				icon = new EmptyDrawable(width, height);
			}

			return icon;
		}*/
	}

	/*@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add(0, MENU_RESET_ITEMID, 0, R.string.menu_reset_all_lotus).setIcon(
				android.R.drawable.ic_menu_recent_history);
		menu.add(0, MENU_RESET_SIGNALITEMID, 0, R.string.menu_reset_signal_lotus).setIcon(
				android.R.drawable.ic_menu_revert);
		return true;
	}
*/
	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * super.onCreateOptionsMenu(menu); menu.add(0, MENU_RESET_ITEMID,
	 * 0,R.string.menu_reset_all_lotus)
	 * .setIcon(android.R.drawable.ic_menu_recent_history); menu.add(0,
	 * MENU_RESET_SIGNALITEMID, 0,R.string.menu_reset_signal_lotus)
	 * .setIcon(android.R.drawable.ic_menu_revert); return true; }
	 */
	/*重设叶片*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case MENU_RESET_ITEMID:

			Intent it = new Intent(ACTION_LOTUS_ALL_RESET);
			sendBroadcast(it);
			finish();
			break;
		case MENU_RESET_SIGNALITEMID:
			Intent it1 = new Intent(ACTION_LOTUS_PAGENUM_RESET);
			sendBroadcast(it1);
			finish();
			break;
		 default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private static class EmptyDrawable extends Drawable {
		private final int mWidth;
		private final int mHeight;

		EmptyDrawable(int width, int height) {
			mWidth = width;
			mHeight = height;
		}

		@Override
		public int getIntrinsicWidth() {
			return mWidth;
		}

		@Override
		public int getIntrinsicHeight() {
			return mHeight;
		}

		@Override
		public int getMinimumWidth() {
			return mWidth;
		}

		@Override
		public int getMinimumHeight() {
			return mHeight;
		}

		@Override
		public void draw(Canvas canvas) {
		}

		@Override
		public void setAlpha(int alpha) {
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
		}

		@Override
		public int getOpacity() {
			return PixelFormat.TRANSLUCENT;
		}
	}
	private void setLotusPageInfo(int pagenum, Intent data) {
		if(preferences==null)
		preferences = getSharedPreferences(LOTUSINFO, 0);

		final SharedPreferences.Editor editor = preferences.edit();
		if (data == null) {
			R2.echo("Name -is1 :  " + LotusUtilites.sfPrefKeyMapTarget[pagenum]);
			editor.putString(LotusUtilites.sfPrefKeyMapTarget[pagenum], null);
		} else {
			String name = data.getComponent().getPackageName() + File.separator
					+ data.getComponent().getClassName();
			R2.echo("Name -is :  " + name + "  , pagenum : " + LotusUtilites.sfPrefKeyMapTarget[pagenum]
					+ " , ");
			editor.putString(LotusUtilites.sfPrefKeyMapTarget[pagenum], name);
		}
		editor.apply();
	}
}
