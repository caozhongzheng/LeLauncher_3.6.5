package com.lenovo.launcher2.addon.share;

import java.util.ArrayList;
import java.util.HashMap;

import com.lenovo.launcher.R;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.lenovo.launcher.components.XAllAppFace.LauncherApplication;
import com.lenovo.launcher2.customizer.ThemeSimpleAdapter;
import com.lenovo.launcher2.commonui.MenuGridView;
import com.lenovo.launcher2.customizer.Reaper;
import com.lenovo.launcher2.customizer.SettingsValue;
import com.lenovo.launcher2.customizer.Utilities;

public class LeShareActivity extends Activity {

	private final String TAG = "LeShareActivity";

	private Button mReceive;
	private Typeface tf;

	private static final int ITEM_APPLICATION = 0;
	private static final int ITEM_WALLPAPER = ITEM_APPLICATION + 1;
	private static final int ITEM_THEME = ITEM_WALLPAPER + 1;

	private int[] sendname_array = { R.string.desktop_share_app_text,
			R.string.desktop_share_wallpaper_text,
			R.string.desktop_share_theme_text };
	private int[] sendimage_array = { R.drawable.ic_launcher_app,
			R.drawable.ic_launcher_wallpaper, R.drawable.ic_launcher_theme };

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		initLayout();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	private void initLayout() {
		this.setContentView(R.layout.menu_share_item);
		Window window = this.getWindow();
		Utilities.setDialogWidth(window,this.getResources());
		 WindowManager.LayoutParams lp = window.getAttributes();
         lp.dimAmount = 0.0f;
         window.setAttributes(lp);
         window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
         window.setBackgroundDrawableResource(R.drawable.menu_dialog_bg);
         window.setWindowAnimations(R.style.dialogWindowAnim);
         // set layout param
         window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
         window.setGravity(Gravity.CENTER );
		View shareDialog_bg = this.findViewById(R.id.share_dialog_bg);
		shareDialog_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO
				finish();
			}
		});
		View shareDialog = this.findViewById(R.id.share_dialog);
		shareDialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});
		mReceive = (Button) this.findViewById(R.id.btn_receive);
		mReceive.setText(R.string.desktop_share_receive_text);
		Log.d(TAG, "height : "+mReceive.getLayoutParams().height+" ; width : "+mReceive.getLayoutParams().width);
		if (tf == null) {
			tf = SettingsValue.getFontStyle(this);
		}
		TextView title = (TextView) this.findViewById(R.id.dialog_title);
		title.setText(R.string.desktop_share_title);
		if (tf != null && tf != title.getTypeface()) {
			title.setTypeface(tf);
			mReceive.setTypeface(tf);
		}
		mReceive.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (LeShareUtils.isInstalledRightQiezi(LeShareActivity.this)) {
					try {
						Intent shareIntent = new Intent(
								"com.lenovo.anyshare.intent.RECEIVE");
						shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(shareIntent);
					} catch (Exception e) {
						Toast.makeText(LeShareActivity.this,
								R.string.activity_not_found, Toast.LENGTH_SHORT)
								.show();
					}
					// TODO
					finish();
				} else if (!LeShareUtils.isInstalledQiezi(LeShareActivity.this)) {
					LeShareUtils.showInstallDialog(LeShareActivity.this, false);
					// TODO
					finish();

				} else {
					LeShareUtils.showInstallDialog(LeShareActivity.this, true);
					// TODO
					finish();
				}

			}

		});

		MenuGridView menuGrid = (MenuGridView) this
				.findViewById(R.id.grid_item);
		menuGrid.setFocusable(false);
		menuGrid.setAdapter(getMenuAdapter(sendname_array, sendimage_array,
				false));
		menuGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				switch (position) {

				case ITEM_APPLICATION: {
					Intent intent = new Intent(LeShareActivity.this,
							LelauncherAppsShare.class);
					startActivitySafely(intent, "");
					Reaper.processReaper(LeShareActivity.this, "Share",
							"ToAppQiezi", Reaper.REAPER_NO_LABEL_VALUE,
							Reaper.REAPER_NO_INT_VALUE);
					// TODO
					finish();
					break;
				}
				case ITEM_WALLPAPER: {
					Intent intent = new Intent(LeShareActivity.this,
							LeShareWallpaperChooserActivity.class);
					startActivitySafely(intent, "");
					Reaper.processReaper(LeShareActivity.this, "Share",
							"ToPaperQiezi", Reaper.REAPER_NO_LABEL_VALUE,
							Reaper.REAPER_NO_INT_VALUE);
					// TODO
					finish();
					break;
				}
				case ITEM_THEME: {
					Intent intent = new Intent(LeShareActivity.this,
							LeShareThemeChooserActivity.class);

					startActivitySafely(intent, "");
					Reaper.processReaper(LeShareActivity.this, "Share",
							"ToThemeQiezi", Reaper.REAPER_NO_LABEL_VALUE,
							Reaper.REAPER_NO_INT_VALUE);

					// TODO
					finish();
					break;
				}
				}
			}
		});
	}

	public ThemeSimpleAdapter getMenuAdapter(int[] str, int[] pic,
			boolean fromTheme) {
		ArrayList<HashMap<String, Object>> menulist = new ArrayList<HashMap<String, Object>>();
		final String image_key = "itemImage";
		final String text_key = "itemText";

		/* RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2011-12-26 . S */
		LauncherApplication app = (LauncherApplication) getApplicationContext();
		// Resources res = getResources();//cancel by xingqx for sonar
		/* RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2011-12-26 . E */

		for (int i = 0; i < str.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			/* RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2011-12-26 . S */
			// Drawable icon = Utilities.findDrawableById(res, pic[i],
			// Launcher.this);
			// Drawable icon = app.mLauncherContext.getDrawable(pic[i], false);
			Drawable icon = app.getResources().getDrawable(pic[i]);

			if (icon != null) {
				map.put(image_key, icon);
			} else {
				map.put(image_key, pic[i]);
			}
			/* RK_ID: RK_THEME . AUT: zhanggx1 . DATE: 2011-12-26 . E */

			map.put(text_key, getString(str[i]));
			menulist.add(map);
		}
		int griditem = R.layout.menu_griditem;
		if (!fromTheme) {
			griditem = R.layout.menu_add_griditem;
		}
		/*** RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 S ***/
		Typeface tf_z = SettingsValue.getFontStyle(this);
		ThemeSimpleAdapter simple = new ThemeSimpleAdapter(this, menulist,
				griditem, new String[] { image_key, text_key }, new int[] {
						R.id.menuitem_text, R.id.menuitem_text },
				R.color.menu_text_color, R.color.def__menu_text_color, tf_z,
				fromTheme);
		/*** RK_ID:RK_FONT AUT:zhanglz1@lenovo.com. DATE: 2013-02-21 E ***/
		return simple;
	}

	public boolean startActivitySafely(Intent intent, Object tag) {

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		/* RK_ID: RK_SHORTCUT. AUT: liuli1 . DATE: 2012-10-19 . START */
		// fix bug 171724
		if (SettingsValue.ACTION_LETHEME_LAUNCH.equals(intent.getAction())) {
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		}
		/* RK_ID: RK_SHORTCUT. AUT: liuli1 . DATE: 2012-10-19 . END */
		try {
			startActivity(intent);
			return true;
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, R.string.activity_not_found,
					Toast.LENGTH_SHORT).show();
			Log.e(TAG, "Unable to launch. tag=" + tag + " intent=" + intent, e);
		} catch (SecurityException e) {
			Toast.makeText(this, R.string.activity_not_found,
					Toast.LENGTH_SHORT).show();
			Log.e(TAG,
					"Launcher does not have the permission to launch "
							+ intent
							+ ". Make sure to create a MAIN intent-filter for the corresponding activity "
							+ "or use the exported attribute for this activity. "
							+ "tag=" + tag + " intent=" + intent, e);
		}
		return false;
	}
}
