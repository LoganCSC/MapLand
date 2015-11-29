package com.barrybecker4.mapland.server.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.barrybecker4.mapland.backend.mapLandApi.model.GameBeanCollection;
import com.barrybecker4.mapland.server.IResponseHandler;
import com.barrybecker4.mapland.server.MapLandApiService;
import com.barrybecker4.mapland.server.ViewPort;

import java.io.IOException;

/**
 * Used to communicate with the backend endpoints (REST service running
 * in the cloud on Google App Engine) to get the currently open games.
 */
public class GameRetriever extends AsyncTask<Void, Void, GameBeanCollection> {

    private Context context;
    private IResponseHandler callback;

    /**
     * Asynchronously retrieve the user (or add if not there)
     */
    public static void getRegions(IResponseHandler callback) {

        // call the backend server
        AsyncTask<Void, Void, GameBeanCollection> task =
                new GameRetriever(callback);

        task.execute();

        Log.i("TASK", "Regions retriever status = " + task.getStatus());
    }

    /**
     * Constructor
     * @param callback called when the user entity has been retrieved
     */
    private GameRetriever(IResponseHandler callback) {
        this.callback = callback;
    }

    @Override
    protected GameBeanCollection doInBackground(Void... params) {


        System.out.println("Now requesting all open games.");

        try {
            return MapLandApiService.getInstance()
                    .getOpenGames()
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(GameBeanCollection result) {
        if (callback != null) {
            callback.jsonRetrieved(result);
        }
    }

}