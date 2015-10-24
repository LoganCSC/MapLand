package com.barrybecker4.mapland.server;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.barrybecker4.mapland.backend.mapLandApi.model.LocationBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.UserBean;

import java.io.IOException;

/**
 * Used to communicate with the backend endpoints (REST service running
 * in the cloud on Google App Engine) to add or update users.
 */
public class UserUpdater extends AsyncTask<Pair<Context, UserBean>, Void, UserBean> {

    private Context context;
    private IResponseHandler callback;

    /**
     * Asynchronously retrieve the user (or add if not there)
     */
    public static void updateUser(UserBean user, Context context, IResponseHandler callback) {

        // call the backend server
        AsyncTask<Pair<Context, UserBean>, Void, UserBean> task =
                new UserUpdater(callback);

        task.execute(new Pair<>(context, user));

        Log.i("TASK", "update user status = " + task.getStatus());
    }

    /**
     * Constructor
     * @param callback called when the user entity has been retrieved
     */
    private UserUpdater(IResponseHandler callback) {
        this.callback = callback;
    }

    @Override
    protected UserBean doInBackground(Pair<Context, UserBean>... params) {

        context = params[0].first;
        UserBean user = params[0].second;

        try {
            return MapLandApiService.getInstance()
                    .updateUserInfo(user)
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