/*
 * Copyright (C) 2014 Mukesh Y authors
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
package com.android.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Mukesh Y
 */
public class MyService extends Service {
	public static final String BROADCAST_ACTION = "com.android.service";
	private final Handler handler = new Handler();
	Intent intent;
	int counter = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		intent = new Intent(BROADCAST_ACTION);
	}

	@Override
	public void onDestroy() {
		handler.removeCallbacks(sendUpdatesToUI);
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startid) {
		handler.removeCallbacks(sendUpdatesToUI);
		handler.postDelayed(sendUpdatesToUI, 1 * 1000); // 1 sec
	}

	private Runnable sendUpdatesToUI = new Runnable() {
		public void run() {
			DisplayLoggingInfo();
			handler.postDelayed(this, 1 * 5000); // 5 sec
		}
	};

	private void DisplayLoggingInfo() {
		Log.d("====service started===", "checking current package");
		ArrayList<String> apps = new ArrayList<String>();
		// list all install app
		PackageManager packageManager = getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> appList = packageManager.queryIntentActivities(
				mainIntent, 0);
		Collections.sort(appList, new ResolveInfo.DisplayNameComparator(
				packageManager));
		List<PackageInfo> packs = packageManager.getInstalledPackages(0);
		for (int i = 0; i < packs.size(); i++) {
			PackageInfo p = packs.get(i);
			ApplicationInfo a = p.applicationInfo;
			// skip system apps if they shall not be included
			if ((a.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
				continue;
			}
			apps.add(p.packageName);
		}

		// find current top activity
		ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> RunningTask = mActivityManager
				.getRunningTasks(1);
		ActivityManager.RunningTaskInfo ar = RunningTask.get(0);
		String activityOnTop = ar.topActivity.getClassName();
		Toast.makeText(getApplicationContext(),
				"Service called " + activityOnTop, Toast.LENGTH_SHORT).show();

		/* close app on launch */

		if (activityOnTop.toLowerCase().contains("com.android.services")) {
			Helper.stop(getApplicationContext());
			LayoutInflater li = LayoutInflater.from(getApplicationContext());
			View promptsView = li.inflate(R.layout.prompts, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					getApplicationContext());

			// set prompts.xml to alertdialog builder
			alertDialogBuilder.setView(promptsView);

			final EditText userInput = (EditText) promptsView
					.findViewById(R.id.editTextDialogUserInput);

			// set dialog message
			alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									String adminPin = userInput.getText()
											.toString();
									if (!adminPin.isEmpty()
											&& adminPin
													.equalsIgnoreCase("1234")) {
										// allow user to access that app
										// add that app to ignore list show that next time it does not ask for pin
									} else {
										Toast.makeText(
												getApplicationContext(),
												"Wrong admin pin,  try again!!!",
												Toast.LENGTH_SHORT).show();
										Intent startHomescreen = new Intent(
												Intent.ACTION_MAIN);
										startHomescreen
												.addCategory(Intent.CATEGORY_HOME);
										startHomescreen
												.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										getApplicationContext().startActivity(
												startHomescreen);
										if (!Helper.CheckIfServiceIsRunning(getApplicationContext())) {
											startService(new Intent(getApplicationContext(), MyService.class));			
										} 

									}
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Intent startHomescreen = new Intent(
											Intent.ACTION_MAIN);
									startHomescreen
											.addCategory(Intent.CATEGORY_HOME);
									startHomescreen
											.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									getApplicationContext().startActivity(
											startHomescreen);
									dialog.cancel();
									if (!Helper.CheckIfServiceIsRunning(getApplicationContext())) {
										startService(new Intent(getApplicationContext(), MyService.class));			
									} 

								}
							});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			/*
			 * Window token null exception, when trying to show alert dialog from
			 * service class.use alertDialog.getWindow() for getting window and
			 * add permission in manifest
			 */
			alertDialog.getWindow().setType(
					WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			// show it
			alertDialog.show();
			Toast.makeText(getApplicationContext(),
					"====matched===" + activityOnTop, Toast.LENGTH_SHORT)
					.show();

		}
	}

	public static boolean isRunning() {
		return true;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
