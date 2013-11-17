package com.tigerstripestech.volleyexample;

import java.util.Date;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;

public class TestSyncAdapter extends AbstractThreadedSyncAdapter {

	// Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    private static final String TAG = "SyncAdapter";
    private final AccountManager mAccountManager;
    private final Context mContext;

    private Date mLastUpdated;
    
	public TestSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mContentResolver = context.getContentResolver();
		
	    mContext = context;
	    mAccountManager = AccountManager.get(context);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		String msg = "Got here";
		
	    String authtoken = null;
	     try {
	         // use the account manager to request the credentials
	         //authtoken = mAccountManager.blockingGetAuthToken(account, Constants.AUTHTOKEN_TYPE, true /* notifyAuthFailure */);
	         
	         // fetch updates from the sample service over the cloud
	         //users = NetworkUtilities.fetchFriendUpdates(account, authtoken,mLastUpdated);
	        // update the last synced date.
	        mLastUpdated = new Date();
	        // update platform contacts.
	        Log.d(TAG, "Calling contactManager's sync contacts");

	    } catch (final ParseException e) {
	        syncResult.stats.numParseExceptions++;
	        Log.e(TAG, "ParseException", e);
	    }
	}
		

}


