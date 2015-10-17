package com.barrybecker4.mapland.screens.support;

import android.content.Context;
import android.widget.Toast;

import com.barrybecker4.mapland.backend.mapLandApi.model.LocationBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.LocationBeanCollection;
import com.barrybecker4.mapland.backend.mapLandApi.model.UserBean;
import com.barrybecker4.mapland.game.GameState;
import com.barrybecker4.mapland.server.IRetrievalHandler;
import com.google.api.client.json.GenericJson;

import java.util.List;

/**
 * @author Barry Becker
 */
public class LocationsRetrievalHandler implements IRetrievalHandler {

    private Context context;
    private GameState state;

    public LocationsRetrievalHandler(Context context, GameState state) {

        this.context = context;
        this.state = state;
    }

    /** Show a popup with the user info */
    @Override
    public void jsonRetrieved(GenericJson result) {
        List<LocationBean> locations = ((LocationBeanCollection)result).getItems();
        state.setVisibleLocations(locations);

        String message = "Locations retrieved to client = " + locations;
        System.out.println(message);
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
