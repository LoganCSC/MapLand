package com.barrybecker4.mapland.server;

import com.barrybecker4.mapland.backend.mapLandApi.MapLandApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

/**
 * Provides singleton access to the backend MapLand api running on google appengine in the cloud.
 */
public class MapLandApiService {

    /** if IS_LOCAL is false then the app tries to connect to the backed running on appengine in the cloud */
    private static final boolean IS_LOCAL = false;

    private static MapLandApi mapLandApiService = null;

    /**
     * @return the singleton instance to the backend API
     */
    public static MapLandApi getInstance() {
        if (mapLandApiService == null) {  // Only do this once
            MapLandApi.Builder builder = createBuilder();
            mapLandApiService = builder.build();
        }
        return mapLandApiService;
    }

    /**
     * The shorter version is used if the backend has been deployed to google app ending in the cloud.
     * @return builder with provides cloud api service access
     */
    private static MapLandApi.Builder createBuilder() {

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

}
