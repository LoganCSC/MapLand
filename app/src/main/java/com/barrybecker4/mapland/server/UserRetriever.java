package com.barrybecker4.mapland.server;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.barrybecker4.mapland.backend.mapLandApi.model.UserBean;
import com.barrybecker4.mapland.backend.mapLandApi.MapLandApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

/**
 * Used to communicate with the backend endpoints (REST service) running
 * in the cloud on Google App Engine.
 */
public class UserRetriever extends AsyncTask<Pair<Context, String>, Void, UserBean> {

    /** if IS_LOCAL is false then the app tries to connect to the backed running on appengine in the cloud */
    private static final boolean IS_LOCAL = true;

    private static MapLandApi mapLandApiService = null;
    private Context context;
    private IRetrievalHandler callback;

    /**
     * Asynchronously retrieve the user (or add if not there)
     */
    public static void getUser(String username, Context context, IRetrievalHandler callback) {

        // call the backend server
        AsyncTask<Pair<Context, String>, Void, UserBean> task = new UserRetriever(callback);
        task.execute(new Pair<>(context, username));

        Log.i("TASK", "status = " + task.getStatus());
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
    public UserRetriever(IRetrievalHandler callback) {
        this.callback = callback;
    }

    @Override
    protected UserBean doInBackground(Pair<Context, String>... params) {
        if (mapLandApiService == null) {  // Only do this once
            MapLandApi.Builder builder = createBuilder();
            mapLandApiService = builder.build();
        }

        context = params[0].first;
        String userId = params[0].second;

        try {
            return mapLandApiService.getUserInfo(userId).execute();   // call getUserInfo
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * The shorter version is used if the backend has been deployed to google app ending in the cloud.
     * @return builder with provides cloud api service access
     */
    private MapLandApi.Builder createBuilder() {

        MapLandApi.Builder builder;

        if (IS_LOCAL) {
            builder = new MapLandApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                // options for running against local devappserver
                // - 10.0.2.2 is localhost's IP address in Android emulator
                // - turn off compression when running against local devappserver
                .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });
        }
        else {
            // Assuming that the server has been deployed to the appEngine in the cloud
            builder = new MapLandApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://maplandbackend.appspot.com/_ah/api/");
        }

        return builder;
    }

    @Override
    protected void onPostExecute(UserBean result) {
        if (callback != null) {
            callback.entityRetrieved(result);
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