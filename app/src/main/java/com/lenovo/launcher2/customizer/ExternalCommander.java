package com.lenovo.launcher2.customizer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.lenovo.launcher2.customizer.Debug.R2;

public class ExternalCommander extends BroadcastReceiver {

	public static final String EXTERNAL_COMMAND_SHOW_APPLIST = "com.lenovo.launcher.SHOW_ALLAPPS";
	public static final String EXTERNAL_COMMAND_ = "com.lenovo.launcher.SHOW_ALLAPPS";

	public static final String COMMAND_KEY_SHOW_ALLAPPLIST = "EXTERNAL_COMMAND_SHOW_ALLAPPS";

	public static boolean sbShowAllApplist = false;

	@Override
	public void onReceive(final Context context, Intent intent) {

		if (Debug.MAIN_DEBUG_SWITCH)
			R2.echo("Performing EXTERNAL LAUNCHER COMMAND : " + intent);
		if (EXTERNAL_COMMAND_SHOW_APPLIST.equals(intent.getAction())) {
			setSbShowAllApplist(true);
			perform(commandShowAllApps(context));
			return;
		}
	}

	private void setSbShowAllApplist(boolean b) {
		sbShowAllApplist = b;
	}

	private void perform(final Runnable r) {
		new Thread(r).start();
	}

	private Runnable commandShowAllApps(final Context context) {

		Runnable r = new Runnable() {

			@Override
			public void run() {

				if (context == null)
					return;

				ComponentName cname = new ComponentName(SettingsValue.LAUNCHER_PACKAGE_NAME_PREF,
						"com.lenovo.launcher2.Launcher");
				Intent launcher = new Intent();
				launcher.setComponent(cname);
				launcher.addCategory(Intent.CATEGORY_LAUNCHER);
				launcher.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				launcher.putExtra(COMMAND_KEY_SHOW_ALLAPPLIST, true);
				context.startActivity(launcher);

			}
		};

		return r;
	}

}
