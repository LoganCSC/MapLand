package com.barrybecker4.mapland.screens.support;

import com.barrybecker4.mapland.backend.mapLandApi.model.RegionAndUserBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.UserBean;
import com.barrybecker4.mapland.game.GameState;
import com.barrybecker4.mapland.server.IResponseHandler;
import com.google.api.client.json.GenericJson;

/**
 * @author Barry Becker
 */
public class RegionTransferredHandler implements IResponseHandler {

    private LandMap map;
    private GameState state;

    public RegionTransferredHandler(LandMap map, GameState state) {
        this.map = map;
        this.state = state;
    }

    /** Called when the the region has been transferred to the new owner */
    @Override
    public void jsonRetrieved(GenericJson result) {
        RegionAndUserBean regionAndUser = (RegionAndUserBean)result;
        state.setCurrentUser(regionAndUser.getUser());
        state.setCurrentRegion(regionAndUser.getRegion());

        map.drawRegions();
    }
}
