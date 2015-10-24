package com.barrybecker4.mapland.server.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.barrybecker4.mapland.backend.mapLandApi.model.LocationBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.UserBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.LocationAndUserBean;
import com.barrybecker4.mapland.server.MapLandApiService;

import java.io.IOException;

/**
 * Used to communicate with the backend endpoints (REST service running
 * in the cloud on Google App Engine) to transfer locations to new owner.
 */
public class LocationTransferer
        extends AsyncTask<Pair<Context, LocationAndUserBean>, Void, LocationAndUserBean> {

    private Context context;

    /**
     * Asynchronously retrieve the user (or add if not there)
     */
    public static void transferLocationOwnership(LocationBean location, UserBean newOwner, Context context) {

        // call the backend server
        AsyncTask<Pair<Context, LocationAndUserBean>, Void, LocationAndUserBean> task =
                new LocationTransferer();

        LocationAndUserBean locationAndNewOwner = new LocationAndUserBean();
        locationAndNewOwner.setLocation(location);
        locationAndNewOwner.setUser(newOwner);

        task.execute(new Pair<>(context, locationAndNewOwner));

        Log.i("TASK", "transferring ownership status: = " + task.getStatus());
    }

    /**
     * Constructor
     */
    private LocationTransferer() {
    }

    @Override
    protected LocationAndUserBean doInBackground(Pair<Context, LocationAndUserBean>... params) {

        context = params[0].first;
        LocationAndUserBean locAndOwner = params[0].second;

        try {
            return MapLandApiService.getInstance()
                    .transferLocationOwnership(locAndOwner)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}