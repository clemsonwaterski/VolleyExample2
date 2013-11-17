package com.tigerstripestech.volleyexample;

import java.lang.ref.WeakReference;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class TestSyncService extends Service {
    
    private RequestQueue queue;
    private JsonArrayRequest jsObjRequest;
	private String hostname = "http://drc-spawar.rhcloud.com";
	private String url = hostname + "/rest/people";
	
	public interface ServiceClient {
		JsonArrayRequest getJSONData(String url);
	}
	
	private WeakReference<ServiceClient> mClient;
	
	public void setServiceClient(ServiceClient client){
		if(client == null){
			mClient = null;
			return;
		}
		
		mClient = new WeakReference<ServiceClient>(client);
	}
	
	public class ServiceBinder extends Binder {
		TestSyncService getService() {
			return TestSyncService.this;
		}
	}
	
	private IBinder mBinder = new ServiceBinder();
 
    @Override
    public void onCreate() {
    	Log.d("TestSyncService", "TestSyncService - onCreate");
    	
    	queue = Volley.newRequestQueue(this);
    }
 
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    @Override
    public void onStart(Intent intent, int startId){
    	final Handler h = new Handler();
    	final int delay = 5000;//milli seconds
    	// final int delay = 300000 // 5 minute delay (commented out for testing purposes)

    	h.postDelayed(new Runnable(){

    	    public void run(){
    	    	Log.d("TestSyncService", "TestSyncService - onStart");
    	    	
    	    	jsObjRequest = mClient.get().getJSONData(url);
    	    	//Log.d("JOSH", "TOSTRING RETURNED: " + mClient.get().getClass().toString());
				queue.add(jsObjRequest);
    	        h.postDelayed(this,delay);
    	    }},
    	delay);
    	
    }

}
