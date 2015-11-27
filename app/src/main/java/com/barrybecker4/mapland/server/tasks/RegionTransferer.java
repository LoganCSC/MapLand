package com.barrybecker4.mapland.server.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.barrybecker4.mapland.backend.mapLandApi.model.RegionBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.RegionBeanCollection;
import com.barrybecker4.mapland.backend.mapLandApi.model.UserBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.RegionAndUserBean;
import com.barrybecker4.mapland.server.IResponseHandler;
import com.barrybecker4.mapland.server.MapLandApiService;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Used to communicate with the backend endpoints (REST service running
 * in the cloud on Google App Engine) to transfer locations to new owner.
 */
public class RegionTransferer
        extends AsyncTask<Pair<Context, RegionAndUserBean>, Void, RegionAndUserBean> {

    private Context context;
    private IResponseHandler callback;

    /**
     * Transfer a region from one owner to another
     */
    public static void transferRegionOwnership(
            RegionBean region, UserBean newOwner, Context context, IResponseHandler callback) {

        // call the backend server
        AsyncTask<Pair<Context, RegionAndUserBean>, Void, RegionAndUserBean> task =
                new RegionTransferer(callback);

        // update on client as well as server
        // this should not be needed
        if (newOwner.getRegions() == null) {
            newOwner.setRegions(new ArrayList<Long>());
        }
        newOwner.getRegions().add(region.getRegionId());
        region.setOwnerId(newOwner.getUserId());


        RegionAndUserBean regionAndNewOwner = new RegionAndUserBean();
        regionAndNewOwner.setRegion(region.clone());
        regionAndNewOwner.setUser(newOwner.clone());

        task.execute(new Pair<>(context, regionAndNewOwner));

        Log.i("TASK", "transferring ownership status: = " + task.getStatus());
    }

    /**
     * Constructor
     */
    private RegionTransferer(IResponseHandler callback) {
        this.callback = callback;
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

    @Override
    protected void onPostExecute(RegionAndUserBean result) {
        if (callback != null) {
            callback.jsonRetrieved(result);
        }
    }
}