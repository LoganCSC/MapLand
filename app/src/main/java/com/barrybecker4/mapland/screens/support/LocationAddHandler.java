package com.barrybecker4.mapland.screens.support;

import android.content.Context;
import android.widget.Toast;

import com.barrybecker4.mapland.backend.mapLandApi.model.LocationBean;
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
public class LocationAddHandler implements IResponseHandler {

    private Context context;
    private GameState state;

    public LocationAddHandler(Context context, GameState state) {
        this.context = context;
        this.state = state;
    }

    /** Called when the new location has ben added the datastore */
    @Override
    public void jsonRetrieved(GenericJson result) {
        LocationBean location = (LocationBean)result;
        state.setCurrentLocation(location);

        String message = "Added new location = " + location;
        System.out.println(message);
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        // Update user with new location
        UserBean user = state.getCurrentUser();

        if (user.getLocations() == null) {
            List<Long> locs = new LinkedList<>();
            locs.add(location.getLocationId());
            user.setLocations(locs);
        } else {
            user.getLocations().add(location.getLocationId());
        }

        // This should not be necessary. They should already have this new location.
        // UserUpdater.updateUser(user, context, null);
    }
}
