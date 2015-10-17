package com.barrybecker4.mapland.server;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.barrybecker4.mapland.backend.mapLandApi.model.UserBean;
import java.io.IOException;

/**
 * Used to communicate with the backend endpoints (REST service running
 * in the cloud on Google App Engine) to get info on the current user.
 */
public class UserRetriever extends AsyncTask<Pair<Context, String>, Void, UserBean> {

    private Context context;
    private IResponseHandler callback;

    /**
     * Asynchronously retrieve the user (or add if not there)
     */
    public static void getUser(String username, Context context, IResponseHandler callback) {

        // call the backend server
        AsyncTask<Pair<Context, String>, Void, UserBean> task = new UserRetriever(callback);
        task.execute(new Pair<>(context, username));

        Log.i("TASK", "user retriever status = " + task.getStatus());
        /*
        try {
            Log.i("TASK", "value = " + task.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Constructor
     * @param callback called when the user entity has been retrieved
     */
    private UserRetriever(IResponseHandler callback) {
        this.callback = callback;
    }

    @Override
    protected UserBean doInBackground(Pair<Context, String>... params) {

        context = params[0].first;
        String userId = params[0].second;

        try {
            return MapLandApiService.getInstance()
                    .getUserInfo(userId)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(UserBean result) {
        if (callback != null) {
            callback.jsonRetrieved(result);
        }
    }
}