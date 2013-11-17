package com.tigerstripestech.volleyexample;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

public class App extends Application {

	private static VolleyDbHelper dbHelper;

	public static VolleyDbHelper getDbHelper() {
		return dbHelper;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		dbHelper = new VolleyDbHelper(getApplicationContext());
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.close();
	}


}
