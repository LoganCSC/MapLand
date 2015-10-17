package com.barrybecker4.mapland.server;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.barrybecker4.mapland.backend.mapLandApi.model.LocationBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.LocationBeanCollection;

import java.io.IOException;
import java.util.List;

/**
 * Used to communicate with the backend endpoints (REST service running
 * in the cloud on Google App Engine) to get the currently visible locations.
 */
public class LocationRetriever extends AsyncTask<Pair<Context, ViewPort>, Void, LocationBeanCollection> {

    /** if IS_LOCAL is false then the app tries to connect to the backed running on appengine in the cloud */
    private static final boolean IS_LOCAL = true;

    private Context context;
    private IRetrievalHandler callback;

    /**
     * Asynchronously retrieve the user (or add if not there)
     */
    public static void getLocations(ViewPort viewport, Context context, IRetrievalHandler callback) {

        // call the backend server
        AsyncTask<Pair<Context, ViewPort>, Void, LocationBeanCollection> task =
                new LocationRetriever(callback);

        task.execute(new Pair<>(context, viewport));

        Log.i("TASK", "status = " + task.getStatus());
    }

    /**
     * Constructor
     * @param callback called when the user entity has been retrieved
     */
    public LocationRetriever(IRetrievalHandler callback) {
        this.callback = callback;
    }

    @Override
    protected LocationBeanCollection doInBackground(Pair<Context, ViewPort>... params) {

        context = params[0].first;
        ViewPort view = params[0].second;

        try {
            return MapLandApiService.getInstance()
                    .getLocationsInViewPort(view.getNwLat(), view.getNwLong(), view.getSeLat(), view.getSeLong())
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(LocationBeanCollection result) {
        if (callback != null) {
            callback.jsonRetrieved(result);
        }
        /*
        String locs = "null locations!";
        if (result.getLocations() != null) {
            locs = result.getLocations().toString();
        }
        String message = result.getUserId() + " owns " + result.getCredits() + " credits, and these locations: " + locs;
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();*/
    }

}