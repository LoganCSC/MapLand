package com.barrybecker4.mapland.server;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.barrybecker4.mapland.backend.mapLandApi.model.LocationBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.LocationBeanCollection;

import java.io.IOException;

/**
 * Used to communicate with the backend endpoints (REST service running
 * in the cloud on Google App Engine) to ad new locations.
 */
public class LocationAdder extends AsyncTask<Pair<Context, LocationBean>, Void, LocationBean> {

    private Context context;
    private IResponseHandler callback;

    /**
     * Asynchronously retrieve the user (or add if not there)
     */
    public static void addLocation(LocationBean loc, Context context, IResponseHandler callback) {

        // call the backend server
        AsyncTask<Pair<Context, LocationBean>, Void, LocationBean> task =
                new LocationAdder(callback);

        task.execute(new Pair<>(context, loc));

        Log.i("TASK", "add location status = " + task.getStatus());
    }

    /**
     * Constructor
     * @param callback called when the user entity has been retrieved
     */
    private LocationAdder(IResponseHandler callback) {
        this.callback = callback;
    }

    @Override
    protected LocationBean doInBackground(Pair<Context, LocationBean>... params) {

        context = params[0].first;
        LocationBean loc = params[0].second;

        try {
            return MapLandApiService.getInstance()
                    .addLocationInfo(loc.getOwnerId(),
                            loc.getNwLatitudeCoord(), loc.getNwLongitudeCoord(), loc.getSeLatitudeCoord(), loc.getSeLatitudeCoord())
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(LocationBean result) {
        if (callback != null) {
            callback.jsonRetrieved(result);
        }
    }

}