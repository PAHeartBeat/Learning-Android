package me.paheratbeat.broadcastreceiverexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
	private final String TAG = "BRE:MyReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "Broadcast Recevied");
		final String received = intent.getAction();
		String toastText = "";
		String msg;
		String sender;
		Boolean isShow;
		switch (received) {
			case "me.paheratbeat.broadcastreceiverexample.FIRST_TEST":
				toastText = "First Boardcast";
				break;
			case "me.paheratbeat.broadcastreceiverexample.SECOND_TEST":
				toastText = "Second Boardcast";
				break;
			case "me.paheratbeat.broadcastreceiverexample.THIRD_TEST":
				toastText = "Second Boardcast";
				break;
		}
		isShow = intent.getExtras().getBoolean("isShow");
		msg = intent.getExtras().getString("msg");
		sender = intent.getExtras().getString("sender");

		if (isShow) {
			Toast.makeText(context, toastText + " Received\n" + msg + "\nfrom: " + sender,
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "Received hidden broadcast\nfrom: " + sender,
					Toast.LENGTH_SHORT).show();
		}
	}
}
