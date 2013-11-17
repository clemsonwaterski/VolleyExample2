package com.tigerstripestech.volleyexample;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class VolleyDbHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "Volley.db";
	private static final int DATABASE_VERSION = 1;
	public static final String KEY_ID = "_id";

	public static final String DB_PEOPLE = "People";
	public static final String KEY_PEOPLE_NAME = "name";
	
	public static final String LOG_TAG = "Volley DB Mesg";
	String createTable = "CREATE TABLE IF NOT EXISTS "
			+ DB_PEOPLE + " (" + KEY_ID + " INTEGER PRIMARY KEY, "
			+ KEY_PEOPLE_NAME + " TEXT )";
	
	
	VolleyDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createTable);
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + DB_PEOPLE);
		onCreate(db);
	}

	// returns a list of names from the people table
	public List<String> getAllPeople() {
		List<String> people = new ArrayList<String>();

		// Select All Query
		String selectQuery = "SELECT  * FROM " + DB_PEOPLE;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				// cursor has values in the format = _id, pid, name, updated
				people.add(cursor.getString(cursor
						.getColumnIndex(KEY_PEOPLE_NAME)));
			} while (cursor.moveToNext());
		}

		// closing connection
		cursor.close();

		// returning people
		return people;

	}


	// returns the pid of the given name from the people table
	public int getIdPeople(String name) {
		int id = 0;

		// Select Query
		String selectQuery = "SELECT  * FROM " + DB_PEOPLE
				+ " WHERE TRIM(name) = '" + name.trim() + "'";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// move to first is used as there should be no duplicate entries for
		// names
		// will ignore all duplicates if they are present
		if (cursor.moveToFirst()) {
			id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
		}
		return id;
	}

}
