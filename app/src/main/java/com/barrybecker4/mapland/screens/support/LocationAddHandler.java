package com.barrybecker4.mapland.screens.support;

import android.content.Context;
import android.widget.Toast;

import com.barrybecker4.mapland.backend.mapLandApi.model.LocationBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.LocationBeanCollection;
import com.barrybecker4.mapland.game.GameState;
import com.barrybecker4.mapland.server.IResponseHandler;
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

    /** Show a popup with the user info */
    @Override
    public void jsonRetrieved(GenericJson result) {
        LocationBean location = (LocationBean)result;
        state.setCurrentLocation(location);

        String message = "Added Location = " + location;
        System.out.println(message);
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
