package com.barrybecker4.mapland.server.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.barrybecker4.mapland.backend.mapLandApi.model.RegionBeanCollection;
import com.barrybecker4.mapland.server.IResponseHandler;
import com.barrybecker4.mapland.server.MapLandApiService;
import com.barrybecker4.mapland.server.ViewPort;

import java.io.IOException;

/**
 * Used to communicate with the backend endpoints (REST service running
 * in the cloud on Google App Engine) to get the currently visible locations.
 */
public class RegionRetriever extends AsyncTask<Pair<Context, ViewPort>, Void, RegionBeanCollection> {

    private Context context;
    private IResponseHandler callback;

    /**
     * Asynchronously retrieve the user (or add if not there)
     */
    public static void getRegions(ViewPort viewport, Context context, IResponseHandler callback) {

        // call the backend server
        AsyncTask<Pair<Context, ViewPort>, Void, RegionBeanCollection> task =
                new RegionRetriever(callback);

        task.execute(new Pair<>(context, viewport));

        Log.i("TASK", "locations retriever status = " + task.getStatus());
    }

    /**
     * Constructor
     * @param callback called when the user entity has been retrieved
     */
    private RegionRetriever(IResponseHandler callback) {
        this.callback = callback;
    }

    @Override
    protected RegionBeanCollection doInBackground(Pair<Context, ViewPort>... params) {

        context = params[0].first;
        ViewPort view = params[0].second;
        System.out.println("Now requesting all locations bounded by " + view);

        try {
            return MapLandApiService.getInstance()
                    .getRegionsInViewPort(view.getNwLat(), view.getNwLong(), view.getSeLat(), view.getSeLong())
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(RegionBeanCollection result) {
        if (callback != null) {
            callback.jsonRetrieved(result);
        }
    }

}