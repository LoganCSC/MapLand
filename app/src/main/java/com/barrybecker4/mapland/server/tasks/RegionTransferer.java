package com.barrybecker4.mapland.server.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.barrybecker4.mapland.backend.mapLandApi.model.RegionBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.UserBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.RegionAndUserBean;
import com.barrybecker4.mapland.server.MapLandApiService;

import java.io.IOException;

/**
 * Used to communicate with the backend endpoints (REST service running
 * in the cloud on Google App Engine) to transfer locations to new owner.
 */
public class RegionTransferer
        extends AsyncTask<Pair<Context, RegionAndUserBean>, Void, RegionAndUserBean> {

    private Context context;

    /**
     * Asynchronously retrieve the user (or add if not there)
     */
    public static void transferRegionOwnership(RegionBean region, UserBean newOwner, Context context) {

        // call the backend server
        AsyncTask<Pair<Context, RegionAndUserBean>, Void, RegionAndUserBean> task =
                new RegionTransferer();

        RegionAndUserBean regionAndNewOwner = new RegionAndUserBean();
        regionAndNewOwner.setRegion(region);
        regionAndNewOwner.setUser(newOwner);

        task.execute(new Pair<>(context, regionAndNewOwner));

        Log.i("TASK", "transferring ownership status: = " + task.getStatus());
    }

    /**
     * Constructor
     */
    private RegionTransferer() {
    }

    @Override
    protected RegionAndUserBean doInBackground(Pair<Context, RegionAndUserBean>... params) {

        context = params[0].first;
        RegionAndUserBean regAndOwner = params[0].second;

        try {
            return MapLandApiService.getInstance()
                    .transferRegionOwnership(regAndOwner)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}