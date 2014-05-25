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

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * @author Mukesh Y
 */
public class Helper extends Application {

	public Helper() {
		// TODO Auto-generated constructor stub
	}

	private static Context context;

	public void onCreate() {
		super.onCreate();
		Helper.context = getApplicationContext();
	}

	public static void showMessage(Context ctx, String msg) {
		Toast toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 580);
		toast.show();

	}

	public static BroadcastReceiver locationBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("======Service is called", "true");
		}
	};

	public static boolean CheckIfServiceIsRunning(Context ctx) {
		// If the service is running in background
		ActivityManager manager = (ActivityManager) ctx
				.getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.android.services.MyService".equals(service.service
					.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public static void stop(Context ctx) {
		try {
			if (CheckIfServiceIsRunning(ctx)) {
				ctx.stopService(new Intent(ctx, MyService.class));
				// unregisterReceiver(locationBroadcastReceiver);
				LocalBroadcastManager.getInstance(ctx).unregisterReceiver(
						Helper.locationBroadcastReceiver);
			}

		} catch (Exception ex) {

		}
	}
}
