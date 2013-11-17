package com.tigerstripestech.volleyexample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tigerstripestech.volleyexample.SyncService.ServiceBinder;
import com.tigerstripestech.volleyexample.SyncService.ServiceClient;

public class MainActivity extends Activity implements ServiceClient {

	public static final String AUTHORITY = "com.example.android.datasync.provider";
	public static final String ACCOUNT_TYPE = "example.com";
	public static final String ACCOUNT = "dummyaccount";

	public Spinner spinPeople;
	List<String> people;
	private EditText editPeople;
	private Button btnSave;
	private RequestQueue queue;
	private JsonArrayRequest jsObjRequest;
	private String hostname = "http://drc-spawar.rhcloud.com";
	private String url = hostname + "/rest/people";
	private String urlSave = hostname + "/rest/people/save";
	
	private SyncServiceConnection sServiceConnection = new SyncServiceConnection();
	private SyncService mService = null;
			

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		bindService(new Intent(this, SyncService.class), sServiceConnection, BIND_AUTO_CREATE);

		spinPeople = (Spinner) findViewById(R.id.spinPeople);
		btnSave = (Button) findViewById(R.id.buttonSave);
		editPeople = (EditText) findViewById(R.id.editPeople);

		queue = Volley.newRequestQueue(this);

		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String newPerson = editPeople.getText().toString();
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("name", newPerson);
				JSONObject jobj = new JSONObject(params);
				

				JsonObjectRequest req = new JsonObjectRequest(urlSave,
						jobj,
						new Response.Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {
								try {
									VolleyLog.v("Response:%n %s",
											response.toString(4));
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}, new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								VolleyLog.e("Error: ", error.getMessage());
							}
						}){
					@Override
					public Map<String,String> getHeaders() throws AuthFailureError {
						HashMap<String,String> headers = new HashMap<String,String>();
						headers.put("Accept", "application/json");
						return headers;
					}};
					
				queue.add(req);

				ContentValues values = new ContentValues();
				values.put(VolleyDbHelper.KEY_PEOPLE_NAME, newPerson);
				getContentResolver().insert(VolleyProvider.URI_PEOPLE, values);
				
				jsObjRequest = getJSONData(url);
				queue.add(jsObjRequest);
			}
		});
		
		jsObjRequest = getJSONData(url);
		findViewById(R.id.progressBar1).setVisibility(View.GONE);
		queue.add(jsObjRequest);

	}
	
	class SyncServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = ((ServiceBinder)service).getService();
			
			mService.setServiceClient(MainActivity.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService.setServiceClient(null);
			mService = null;
		}
		
	}
	
	@Override
	public JsonArrayRequest getJSONData(String url){
		spinPeople = null;
		spinPeople = (Spinner) findViewById(R.id.spinPeople);
		JsonArrayRequest jsObjRequest = new JsonArrayRequest(url,
				new Response.Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						people = new ArrayList<String>();
						for (int i = 0; i < response.length(); i++) {
							String name = "";

							try {
								JSONObject obj = response.getJSONObject(i);
								name = obj.getString("name");
							} catch (JSONException e) {
								e.printStackTrace();
							}

							if (name != null)
								people.add(name);

						}
						ArrayAdapter<String> peopleAdapter = new ArrayAdapter<String>(
								getApplicationContext(),
								R.layout.spinner_item,
								people);
						spinPeople.setAdapter(peopleAdapter);

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						String tmp = error.toString();
						Log.d("Response", tmp);
					}
				});
		
		return jsObjRequest;
	}
	
	public void performSync(View v){
		Intent myIntent = new Intent(this, SyncService.class);
		startService(myIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
