package com.barrybecker4.mapland.screens.support;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

/**
 * Provide support for accesing user accounts on the device
 * @author Barry Becker
 */
public class UserAccounts {

    /**
     * @return the account name. For gmail users, this is the email address
     */
    public static String getDefaultAccountName(Context context) {

        Account[] list = getAccounts(context);

        String accountName;
        if (list.length == 0) {
            Log.i("ACCT", "No accounts found. Using guest user ");
            accountName = "guest";
        }
        else {
            Account account = list[0];
            accountName = account.name;
            Log.i("ACCT", "Account Name = " + accountName);
        }

        return accountName;
    }

    /**
     * @return the account name. For gmail users, this is the email address
     */
    public static Account[] getAccounts(Context context) {
        // get the users already signed in account
        AccountManager manager = (AccountManager) context.getSystemService(context.ACCOUNT_SERVICE);
        Account[] accounts = manager.getAccounts();

        Log.i("ACCT", "User Accounts");
        for (Account acct : accounts) {
            Log.i("ACCT", "Account = " + acct.toString());
        }

        return accounts;
    }
}
