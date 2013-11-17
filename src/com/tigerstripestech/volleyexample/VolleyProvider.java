package com.tigerstripestech.volleyexample;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class VolleyProvider extends ContentProvider {


	// authority is the symbolic name of your provider
	private static final String AUTHORITY = "com.tigerstripestech.volleyprovider";

	private static final int ALL_PEOPLE = 1;
	private static final int SINGLE_PERSON = 2;
	
	public static final String URI_END_PEOPLE = "people";
	public static final Uri URI_PEOPLE = Uri.parse("content://" + AUTHORITY + "/" + URI_END_PEOPLE); // uri deals people table

	// a content URI pattern matches content URIs using wildcard characters:
	// *: Matches a string of any valid characters of any length.
	// #: Matches a string of numeric characters of any length.
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		uriMatcher.addURI(AUTHORITY, URI_END_PEOPLE, ALL_PEOPLE);
		uriMatcher.addURI(AUTHORITY, URI_END_PEOPLE + "/#", SINGLE_PERSON);
	}

	@Override
	public boolean onCreate() {
		VolleyDbHelper dbHelper = App.getDbHelper();
		return false;
	}

	@Override
	public String getType(Uri uri) {
		String base_dir = "vnd.android.cursor.dir/vnd.com." + AUTHORITY + ".";
		String base_item = "vnd.android.cursor.item/vnd.com." + AUTHORITY + ".";
		switch (uriMatcher.match(uri)) {
		case ALL_PEOPLE:
			return base_dir + URI_END_PEOPLE;
		case SINGLE_PERSON:
			return base_item + URI_END_PEOPLE;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {;
		VolleyDbHelper dbHelper = App.getDbHelper();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		String tables, id;
		Cursor cursor;
		switch (uriMatcher.match(uri)) {

		case SINGLE_PERSON:
			id = uri.getPathSegments().get(1);
			queryBuilder.appendWhere(VolleyDbHelper.DB_PEOPLE + "." + VolleyDbHelper.KEY_ID + "=" + id);
		case ALL_PEOPLE:
			tables = VolleyDbHelper.DB_PEOPLE;
			queryBuilder.setTables(tables);
			break;

		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		VolleyDbHelper dbHelper = App.getDbHelper();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Uri newUri = uri;
		long id;
		switch (uriMatcher.match(uri)) {

		case ALL_PEOPLE:
			id = db.insert(VolleyDbHelper.DB_PEOPLE, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			newUri = Uri.parse(URI_PEOPLE + "/" + id);
			break;

		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		return newUri;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		VolleyDbHelper dbHelper = App.getDbHelper();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String table, id;
		switch (uriMatcher.match(uri)) {

		case SINGLE_PERSON:
			id = uri.getPathSegments().get(1);
			selection = VolleyDbHelper.KEY_ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
		case ALL_PEOPLE:
			table = VolleyDbHelper.DB_PEOPLE;
			break;

		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		int updateCount = db.update(table, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return updateCount;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		VolleyDbHelper dbHelper = App.getDbHelper();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String table, id;
		switch (uriMatcher.match(uri)) {

		case SINGLE_PERSON:
			id = uri.getPathSegments().get(1);
			selection = VolleyDbHelper.KEY_ID + "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
		case ALL_PEOPLE:
			table = VolleyDbHelper.DB_PEOPLE;
			break;

		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		int deleteCount = db.delete(table, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return deleteCount;
	}
}
