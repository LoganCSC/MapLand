package com.barrybecker4.mapland.server.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.barrybecker4.mapland.backend.mapLandApi.model.RegionBean;
import com.barrybecker4.mapland.server.IResponseHandler;
import com.barrybecker4.mapland.server.MapLandApiService;


import java.io.IOException;

/**
 * Used to communicate with the backend endpoints (REST service running
 * in the cloud on Google App Engine) to add a new location with a certain user as owner.
 * Note: The user also needs to have the id for this location added to their list of owned locations.
 */
public class RegionAdder extends AsyncTask<Pair<Context, RegionBean>, Void, RegionBean> {

    private Context context;
    private IResponseHandler callback;

    /**
     * Asynchronously retrieve the user (or add if not there)
     */
    public static void addRegionForUser(RegionBean loc, Context context, IResponseHandler callback) {

        // call the backend server
        AsyncTask<Pair<Context, RegionBean>, Void, RegionBean> task =
                new RegionAdder(callback);

        task.execute(new Pair<>(context, loc));

        Log.i("TASK", "add location status = " + task.getStatus());
    }

    /**
     * Constructor
     * @param callback called when the user entity has been retrieved
     */
    private RegionAdder(IResponseHandler callback) {
        this.callback = callback;
    }

    @Override
    protected RegionBean doInBackground(Pair<Context, RegionBean>... params) {

        context = params[0].first;
        RegionBean loc = params[0].second;

        try {
            return MapLandApiService.getInstance()
                    .addRegionInfo(loc.getOwnerId(),
                            loc.getNwLatitudeCoord(), loc.getNwLongitudeCoord(),
                            loc.getSeLatitudeCoord(), loc.getSeLongitudeCoord())
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(RegionBean result) {
        if (callback != null) {
            callback.jsonRetrieved(result);
        }
    }

}