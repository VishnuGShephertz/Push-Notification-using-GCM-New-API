package com.example.app42sample;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.app42sample.App42GCMController.App42GCMListener;
import com.shephertz.app42.paas.sdk.android.App42API;

public class MainActivity extends Activity implements App42GCMListener {
	public static final String EXTRA_MESSAGE = "message";
	private static final String GoogleProjectNo = "1043599038916";
	private TextView responseTv;
	private EditText edUserName, edMessage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		responseTv = ((TextView) findViewById(R.id.response_msg));
		edUserName = ((EditText) findViewById(R.id.uname));
		edMessage = ((EditText) findViewById(R.id.message));
		 App42API.initialize(
		 this,
		 "e86da38e4f4363bcbce74c431ca10173bf452e47285893e8d2044bb3913b7153",
		 "f8fb6584ee2f9cc5b0d46d22cf696e32bd3988e064ae7760b0219e36f46357f3");
		 App42API.setLoggedInUser("gcmTest") ;
	}

	public void onStart() {
		super.onStart();
		if (App42GCMController.isPlayServiceAvailable(this)) {
			App42GCMController.getRegistrationId(MainActivity.this,
					GoogleProjectNo, this);
		} else {
			Log.i("App42PushNotification",
					"No valid Google Play Services APK found.");
		}
	}

	/*
	 * * This method is called when a Activty is stop disable all the events if
	 * occuring (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	public void onStop() {
		super.onStop();

	}

	/*
	 * This method is called when a Activty is finished or user press the back
	 * button (non-Javadoc)
	 * 
	 * @override method of superclass
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	public void onDestroy() {
		super.onDestroy();

	}

	/*
	 * called when this activity is restart again
	 * 
	 * @override method of superclass
	 */
	public void onReStart() {
		super.onRestart();

	}

	/*
	 * called when activity is paused
	 * 
	 * @override method of superclass (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	public void onPause() {
		super.onPause();
		unregisterReceiver(mBroadcastReceiver);
	}

	/*
	 * called when activity is resume
	 * 
	 * @override method of superclass (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	public void onResume() {
		super.onResume();
		String message = getIntent().getStringExtra(
				App42GCMService.EXTRA_MESSAGE);
		if (message != null)
			Log.d("MainActivity-onResume", "Message Recieved :" + message);
		IntentFilter filter = new IntentFilter(
				App42GCMService.DISPLAY_MESSAGE_ACTION);
		filter.setPriority(2);
		registerReceiver(mBroadcastReceiver, filter);
	}

	final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String message = intent
					.getStringExtra(App42GCMService.EXTRA_MESSAGE);
			Log.i("MainActivity-BroadcastReceiver", "Message Recieved " + " : "
					+ message);
			responseTv.setText(message);

		}
	};

	public void onSendPushClicked(View view) {
		responseTv.setText("Sending Push to User ");
		App42GCMController.sendPushToUser(edUserName.getText().toString(),
				edMessage.getText().toString(), this);

	}

	@Override
	public void onError(String errorMsg) {
		// TODO Auto-generated method stub
		responseTv.setText("Error -" + errorMsg);
	}

	@Override
	public void onGCMRegistrationId(String gcmRegId) {
		// TODO Auto-generated method stub
		responseTv.setText("Registration Id on GCM--" + gcmRegId);
		App42GCMController.storeRegistrationId(this, gcmRegId);
		if(!App42GCMController.isApp42Registerd(MainActivity.this))
		App42GCMController.registerOnApp42(App42API.getLoggedInUser(), gcmRegId, this);
	}

	@Override
	public void onApp42Response(final String responseMessage) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				responseTv.setText(responseMessage);
			}
		});
	}

	@Override
	public void onRegisterApp42(final String responseMessage) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				responseTv.setText(responseMessage);
				App42GCMController.storeApp42Success(MainActivity.this);
			}
		});
	}
}
