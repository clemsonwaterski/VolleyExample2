package com.tigerstripestech.volleyexample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tigerstripestech.volleyexample.TestSyncService.ServiceClient;

public class backup_MainActivity extends Activity implements ServiceClient {

	public static final String AUTHORITY = "com.example.android.datasync.provider";
	public static final String ACCOUNT_TYPE = "example.com";
	public static final String ACCOUNT = "dummyaccount";

	public static Spinner spinPeople;
	List<String> people;
	private EditText editPeople;
	private Button btnSave, btnSync;
	private RequestQueue queue;
	private JsonArrayRequest jsObjRequest;
	private String hostname = "http://192.168.1.119:8080";
	private String url = hostname + "/com.tigerstripestech.jersey.db/rest/people";
	private String urlSave = hostname + "/com.tigerstripestech.jersey.db/rest/people/save";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//VolleyDbHelper dbHelper2 = new VolleyDbHelper(getApplicationContext());		
		//VolleyDbHelper dbHelper= App.getDbHelper();
		//SQLiteDatabase db2 = dbHelper2.getWritableDatabase();
		//SQLiteDatabase db = dbHelper.getWritableDatabase();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		spinPeople = (Spinner) findViewById(R.id.spinPeople);
		btnSave = (Button) findViewById(R.id.buttonSave);
		btnSync = (Button) findViewById(R.id.buttonSync);
		editPeople = (EditText) findViewById(R.id.editPeople);

		queue = Volley.newRequestQueue(this);

		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String newPerson = editPeople.getText().toString();
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("name", newPerson);

				JsonObjectRequest req = new JsonObjectRequest(urlSave,
						new JSONObject(params),
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
						});
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
	
	@Override
	public JsonArrayRequest getJSONData(String url){
		//spinPeople = null;
		//spinPeople = (Spinner) findViewById(R.id.spinPeople);
		JsonArrayRequest jsObjRequest = new JsonArrayRequest(url,
				new Response.Listener<JSONArray>() {
					@Override
					public void onResponse(JSONArray response) {
						people = new ArrayList<String>();
						//findViewById(R.id.progressBar1)
						//		.setVisibility(View.GONE);
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
						//spinPeople.setBackgroundColor(android.R.color.black);
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
		Intent myIntent = new Intent(this, TestSyncService.class);
		startService(myIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Create a new dummy account for the sync adapter
	 * 
	 * @param context
	 *            The application context
	 */
	public static Account CreateSyncAccount(Context context) {
		// Create the account type and default account
		Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
		// Get an instance of the Android account manager
		AccountManager accountManager = (AccountManager) context
				.getSystemService(ACCOUNT_SERVICE);
		/*
		 * Add the account and account type, no password or user data If
		 * successful, return the Account object, otherwise report an error.
		 */
		if (accountManager.addAccountExplicitly(newAccount, null, null)) {
			/*
			 * If you don't set android:syncable="true" in in your <provider>
			 * element in the manifest, then call context.setIsSyncable(account,
			 * AUTHORITY, 1) here.
			 */
		} else {
			/*
			 * The account exists or some other error occurred. Log this, report
			 * it, or handle it internally.
			 */
		}
		return newAccount;
	}

}
