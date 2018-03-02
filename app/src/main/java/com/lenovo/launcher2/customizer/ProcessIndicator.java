package com.lenovo.launcher2.customizer;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;


import com.lenovo.launcher.components.XAllAppFace.XLauncher;
import com.lenovo.launcher.components.XAllAppFace.slimengine.XContext;
import com.lenovo.launcher2.weather.widget.utils.Debug.R2;

public class ProcessIndicator {

	private static Toast message = null;

	private Context mContext = null;

	public static ProcessIndicator getInstance(Context context) {
		return new ProcessIndicator(context);
	}

	private ProcessIndicator(Context context) {
		mContext = context;
	}

	public void setState(final int id) {
		try {
			this.setState(mContext.getString(id));
		} catch (Exception e) {
		}
	}

	public void setState(final String msg) {

		try {

			((XLauncher) mContext).getXLauncherView().post(new Runnable() {
				public void run() {
					if (message != null) {
						message.setText(msg);
					} else {
						message = Toast.makeText(mContext, msg,
								Toast.LENGTH_LONG);
					}
					for (int i = 0; i < 5; i++) {
						if (message != null)
							message.show();
					}
				}
			});
		} catch (Exception e) {
			try {
				if (Looper.myLooper() == null)
					Looper.prepare();
				setMsg(Toast.makeText(mContext.getApplicationContext(), msg,
						Toast.LENGTH_LONG));
				message.show();
			} catch (Exception e1) {
			}
		}

	}

	private void setMsg(Toast makeText) {
		// TODO Auto-generated method stub
		message = makeText;
	}

	public static void clean() {
		message = null;
	}

}
