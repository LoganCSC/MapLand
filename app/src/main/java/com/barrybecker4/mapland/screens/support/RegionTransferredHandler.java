package com.barrybecker4.mapland.screens.support;

import com.barrybecker4.mapland.backend.mapLandApi.model.RegionAndUserBean;
import com.barrybecker4.mapland.server.IResponseHandler;
import com.google.api.client.json.GenericJson;

/**
 * @author Barry Becker
 */
public class RegionTransferredHandler implements IResponseHandler {

    private LandMap map;

    public RegionTransferredHandler(LandMap map) {
        this.map = map;
    }

    /** Called when the the region has been transferred to the new owner */
    @Override
    public void jsonRetrieved(GenericJson result) {
        RegionAndUserBean region = (RegionAndUserBean)result;
        map.drawRegions();
    }
}
