package com.lenovo.launcher2.commoninterface;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lenovo.launcher.R;

public class ProfileReloaderSelectionActivity extends Activity {

	ListView selectionList;
	Button bnOK, bnCancel;

	SelectionListAdapter selectionAdapter;

	public ProfileReloaderSelectionActivity INSTANCE;

//	private int counter = 30;
//
//	private static final int MSG_COUNT_SET = 1;
//	private static final int MSG_COUNT_DOWN = MSG_COUNT_SET + 1;
//	private Handler handler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			switch (msg.what) {
//			case MSG_COUNT_SET:
//				if (bnOK != null) {
//					String str = getResources().getString(
//							R.string.profile_selection_confirmed);
//					bnOK.setText(str + " (" + counter + ")");
//					bnOK.invalidate();
//					sendEmptyMessage(MSG_COUNT_DOWN);
//				}
//				break;
//			case MSG_COUNT_DOWN:
//				counter--;
//
//				if (counter < 0) {
//					bnOK.performClick();
//					return;
//				}
//				sendEmptyMessageDelayed(MSG_COUNT_SET, 1000L);
//				break;
//			}
//		}
//	};
	private LinearLayout mButtonbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		INSTANCE = this;
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.profile_restore_selection_face);

		selectionAdapter = new SelectionListAdapter(this);

		selectionList = (ListView) findViewById(R.id.cl_list_view);
		selectionList.setAdapter(selectionAdapter);

		mButtonbar = (LinearLayout) findViewById(R.id.msg_btnbar);
		bnOK = (Button) mButtonbar.findViewById(R.id.addfinish);
		bnOK.setText(R.string.profile_selection_confirmed);
		bnCancel = (Button) mButtonbar.findViewById(R.id.canceladd);
		bnCancel.setText(R.string.add_profile_cancel);
//        ((Button) mButtonbar.findViewById(R.id.canceladd)).setVisibility(View.GONE);
	//	bnOK = (Button) findViewById(R.id.cl_left_button);

		initButtonListener();

		//setTitle(R.string.profile_selection_title);
		TextView title = (TextView) findViewById(R.id.dialog_title);
	    title.setText(R.string.profile_selection_title);
	    title.setGravity(Gravity.CENTER_HORIZONTAL);
	    TextView restext = (TextView) findViewById(R.id.restore_text);
	    restext.setText(R.string.profile_to_apply);
//		counter = 30;

//		handler.sendEmptyMessageDelayed(MSG_COUNT_SET, 1L);
	}

	private void initButtonListener() {
		bnOK.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setResultAndFinish(false);
			}
		});
		bnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				setResultAndFinish(true);
			}
		});
	}

	private void setResultAndFinish(boolean cancel) {

		Intent data = new Intent();

		if (cancel) {
			data.putExtra("canceled", true);
		} else {
			data.putExtra(Constants.ALLAPP_PRIORITY,
					getState(Constants.ALLAPP_PRIORITY));
			data.putExtra(Constants.APPLICATIONS,
					getState(Constants.APPLICATIONS));
			data.putExtra(Constants.FOLDERS, getState(Constants.FOLDERS));
			data.putExtra(Constants.SETTINGS, getState(Constants.SETTINGS));
			data.putExtra(Constants.SHORTCUTS, getState(Constants.SHORTCUTS));
			data.putExtra(Constants.WALLPAPER, getState(Constants.WALLPAPER));
			data.putExtra(Constants.WIDGETS, getState(Constants.WIDGETS));
		}

		setResult(RESULT_OK, data);
		INSTANCE.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			setResultAndFinish(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean getState(String name) {
		if (INSTANCE != null) {
			return selectionAdapter.getCheckerState(name);
		}

		return false;
	}

	public static final class Constants {
		public static final String APPLICATIONS = "applications";
		public static final String SHORTCUTS = "shortcuts";
		public static final String FOLDERS = "folders";
		public static final String WIDGETS = "widgets";
		public static final String SETTINGS = "settings";
		public static final String WALLPAPER = "wallpaper";
		public static final String ALLAPP_PRIORITY = "priority";
	}

	class SelectionListAdapter extends BaseAdapter {

		private Context mContext = null;
		private Map<Integer, Item> items = new HashMap<Integer, Item>();

		public SelectionListAdapter(Context context) {
			super();
			mContext = context;

			// R2
			fillData();
		}

		public void addItem(int position, Item item) {
			items.put(position, item);
			notifyDataSetInvalidated();
		}

		private void fillData() {
			items.put(0, new Item(mContext, Constants.APPLICATIONS,
					R.string.profile_selection_application));
			items.put(1, new Item(mContext, Constants.FOLDERS,
					R.string.profile_selection_folders));
			items.put(2, new Item(mContext, Constants.WIDGETS,
					R.string.profile_selection_widgets));
			items.put(3, new Item(mContext, Constants.SETTINGS,
					R.string.profile_selection_settings));
			items.put(4, new Item(mContext, Constants.WALLPAPER,
					R.string.profile_selection_wallpaper));
			items.put(5, new Item(mContext, Constants.ALLAPP_PRIORITY,
					R.string.profile_selection_priorities));
		}

		public Map<String, Boolean> getAllCheckerState() {
			Map<String, Boolean> hashMap = new HashMap<String, Boolean>();

			for (int i = 0; i < items.size(); i++) {
				hashMap.put(items.get(i).itemName,
						items.get(i).checker.isChecked());
			}

			return hashMap;
		}

		public boolean getCheckerState(String name) {
			for (int i = 0; i < items.size(); i++) {
				if (items.get(i).itemName.equals(name)) {
					return items.get(i).checker.isChecked();
				}
			}

			return true;
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = items.get(position).parentView;
			}
			return convertView;
		}
	}

	class Item {

		public CheckBox checker;
		public ProgressBar progress;
		public View parentView;
		private Context mContext;

		private String itemName = "";

		public Item(Context context, String name, int id) {
			mContext = context;
			parentView = View.inflate(mContext,
					R.layout.profile_restore_selection_item, null);

			checker = (CheckBox) parentView.findViewById(R.id.cl_checkbox);
			checker.setText(getResources().getString(id));
			checker.setChecked(true);
			progress = (ProgressBar) parentView.findViewById(R.id.cl_progress);

			itemName = name;

			setProgresserVisible(false);
		}

		public String getName() {
			return itemName;
		}

		private void setProgresserVisible(boolean visible) {
			if (progress != null) {
				progress.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
			}
		}
	}

}
