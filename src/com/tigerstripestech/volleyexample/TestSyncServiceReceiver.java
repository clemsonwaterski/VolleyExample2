package com.tigerstripestech.volleyexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TestSyncServiceReceiver extends BroadcastReceiver {
	private static final String TAG = "testSyncServiceReceiver";
	public TestSyncServiceReceiver(){
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onReceive called");
		Intent myIntent = new Intent(context, TestSyncService.class);
		context.startService(myIntent);
	}
	
	

}
