package com.barrybecker4.mapland.screens.support;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.barrybecker4.mapland.backend.mapLandApi.model.RegionBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.UserBean;
import com.barrybecker4.mapland.game.GameState;
import com.barrybecker4.mapland.server.IResponseHandler;
import com.barrybecker4.mapland.server.tasks.UserUpdater;
import com.google.api.client.json.GenericJson;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Barry Becker
 */
public class RegionAddHandler implements IResponseHandler {

    private GameState state;

    public RegionAddHandler(GameState state) {
        this.state = state;
    }

    /** Called when the new location has ben added the datastore */
    @Override
    public void jsonRetrieved(GenericJson result) {
        RegionBean region = (RegionBean)result;
        state.setCurrentRegion(region);

        String message = "Added new region = " + region;
        Log.i("REGION ADD", message);
        //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        // Update user with new region
        UserBean user = state.getCurrentUser();

        if (user.getRegions() == null) {
            List<Long> locs = new LinkedList<>();
            locs.add(region.getRegionId());
            user.setRegions(locs);
        } else {
            user.getRegions().add(region.getRegionId());
        }

        // This should not be necessary. They should already have this new region.
        // UserUpdater.updateUser(user, context, null);
    }
}
