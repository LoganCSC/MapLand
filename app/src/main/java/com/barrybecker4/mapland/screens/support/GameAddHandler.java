package com.barrybecker4.mapland.screens.support;

import android.util.Log;

import com.barrybecker4.mapland.backend.mapLandApi.model.GameBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.RegionBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.UserBean;
import com.barrybecker4.mapland.game.GameState;
import com.barrybecker4.mapland.server.IResponseHandler;
import com.google.api.client.json.GenericJson;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Barry Becker
 */
public class GameAddHandler implements IResponseHandler {

    //private GameState state;

    public GameAddHandler() {
        //this.state = state;
    }

    /** Called when the new location has been added the datastore */
    @Override
    public void jsonRetrieved(GenericJson result) {
        GameBean game = (GameBean)result;
        //state.setCurrentGame(game);

        String message = "New game = " + game;
        Log.i("GAME ADD", message);

        // Update user with new region
        //UserBean user = state.getCurrentUser();

        // It is possible that a user in the same region added it first (rare)
        // If that happens do not do this update
        /*
        if (user.getUserId().equals(game.getOwnerId())) {
            if (user.getRegions() == null) {
                List<Long> locs = new LinkedList<>();
                locs.add(game.getRegionId());
                user.setRegions(locs);
            } else {
                user.getRegions().add(game.getRegionId());
            }
        }
        else {
            Log.w("REGION ADD", game.getOwnerId() + " added this region first!");
        }
        */
    }
}
