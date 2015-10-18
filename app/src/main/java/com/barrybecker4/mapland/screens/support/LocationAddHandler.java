package com.barrybecker4.mapland.screens.support;

import android.content.Context;
import android.widget.Toast;

import com.barrybecker4.mapland.backend.mapLandApi.model.LocationBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.LocationBeanCollection;
import com.barrybecker4.mapland.backend.mapLandApi.model.UserBean;
import com.barrybecker4.mapland.game.GameState;
import com.barrybecker4.mapland.server.IResponseHandler;
import com.barrybecker4.mapland.server.UserUpdater;
import com.google.api.client.json.GenericJson;

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

        // add this new location to the list of locations owned by the currnt user
        UserBean user = state.getCurrentUser();
        user.getLocations().add(location.getId());
        UserUpdater.updateUser(user, context, null);

    }
}
