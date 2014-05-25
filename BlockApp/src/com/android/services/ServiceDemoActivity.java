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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Mukesh Y
 */
public class ServiceDemoActivity extends Activity {

	TextView notification;
	Button stop;
	Context ctx = ServiceDemoActivity.this;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		notification = (TextView) findViewById(R.id.notification);
		if (Helper.CheckIfServiceIsRunning(ctx)) {
			notification.setText("Continuation of last started service");
		} else {
			notification.setText("Service started now only");
			startService(new Intent(this, MyService.class));
		}

		stop = (Button) findViewById(R.id.stop);

		stop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Helper.CheckIfServiceIsRunning(ctx)) {
					ctx.stopService(new Intent(ctx, MyService.class));
					notification
							.setText("Service stopped - to start the service go out and come inside the application");
					LocalBroadcastManager.getInstance(ctx).unregisterReceiver(
							broadcastReceiver);
				}
			}
		});
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// blockApp();
			System.out.print("-------testing----------");
			Toast.makeText(getApplicationContext(), "Installing any app",
					Toast.LENGTH_LONG).show();
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(this)
				.registerReceiver(broadcastReceiver,
						new IntentFilter(MyService.BROADCAST_ACTION));
	}

	@Override
	public void onPause() {
		super.onPause();
		// unregisterReceiver(broadcastReceiver);
	}

}