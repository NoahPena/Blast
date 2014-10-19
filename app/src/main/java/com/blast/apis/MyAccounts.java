package com.blast.apis;

import android.accounts.Account;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import com.blast.R;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by khanguy on 10/18/14.
 * handles all the accounts for the app
 */
public class MyAccounts {
    public static final String MY_NAME = "com.blast";

    /**
     * getst he facebook account contact info
     * screw fb
     * @param a
     * @return
     */
    public static Account getFacebookAccount(Activity a) {
        Account acc = null;
        AccountManager am = AccountManager.get(a);
        Account[] accounts = am.getAccountsByType("com.facebook.auth.login");
        if (accounts.length > 0) {
            Account account = accounts[0];
            if (account.type != null && account.type.equals("com.facebook.auth.login")) {
                    Log.e(MY_NAME, "FACEBOOK-TYPE FOUND");
                    acc = account;
                }
        }

        return acc;
    }

    /**
     * currenlty does only gmail.
     * @param
     * @return
     */
    public ArrayList<Account> getMailAccounts(Activity a){
        ArrayList<Account> out = new ArrayList<Account>();
        AccountManager am = AccountManager.get(a);
        Account[] accounts = am.getAccountsByType(null);
        if(accounts.length > 0){
            Account account = accounts[0];
            if (account.type != null && account.type.equals("com.google")){
                Log.e(MY_NAME, "GMAIL-TYPE FOUND");
                out.add(account);
            }
        }
        return out;
    }


}
