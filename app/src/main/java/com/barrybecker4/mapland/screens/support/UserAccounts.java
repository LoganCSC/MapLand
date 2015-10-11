package com.barrybecker4.mapland.screens.support;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Provide support for accesing user accounts on the device
 * @author Barry Becker
 */
public class UserAccounts {

    /**
     * If no accounts found, guest assumed.
     * @return the account name. For gmail users, this is the email address
     */
    public static String getDefaultAccountName(Context context) {

        List<String> list = getAccountNames(context);
        return list.get(0);
    }

    public static List<String> getAccountNames(Context context) {
        Account[] accounts = getAccounts(context);

        List<String> accountNames = new LinkedList<>();
        for (Account acc : accounts) {
            accountNames.add(acc.name);
        }
        if (accountNames.isEmpty()) {
            Log.i("ACCT", "No accounts found. Using guest user");
            accountNames.add("guest");
        }
        return accountNames;
    }

    /**
     * @return the account name. For gmail users, this is the email address
     */
    public static Account[] getAccounts(Context context) {
        // get the users already signed in account
        AccountManager manager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] accounts = manager.getAccounts();

        Log.i("ACCT", "User Accounts");
        for (Account acct : accounts) {
            Log.i("ACCT", "Account = " + acct.toString());
        }

        return accounts;
    }
}
