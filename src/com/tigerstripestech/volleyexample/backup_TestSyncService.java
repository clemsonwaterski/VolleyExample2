package com.tigerstripestech.volleyexample;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class backup_TestSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static TestSyncAdapter sSyncAdapter = null;
    
    private RequestQueue queue;
    private JsonArrayRequest jsObjRequest;
	private String hostname = "http://192.168.1.119:8080";
	private String url = hostname + "/com.tigerstripestech.jersey.db/rest/people";
 
    @Override
    public void onCreate() {
    	Log.d("TestSyncService", "TestSyncService - onCreate");
    	
    	queue = Volley.newRequestQueue(this);
        /* Old Implementation
    	synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new TestSyncAdapter(getApplicationContext(), true);
        }
        */
    }
 
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
    
    @Override
    public void onStart(Intent intent, int startId){
    	final Handler h = new Handler();
    	final int delay = 5000;//milli seconds
    	// final int delay = 300000 // 5 minute delay (commented out for testing purposes)

    	h.postDelayed(new Runnable(){

    	    public void run(){
    	        //do something
    	    	Log.d("TestSyncService", "TestSyncService - onStart");

    	    	MainActivity ma = new MainActivity();  // WRONG!!!
    	    	jsObjRequest = ma.getJSONData(url);
				queue.add(jsObjRequest);
    	        h.postDelayed(this,delay);
    	    }},
    	delay);
    	
    }

}
