package com.barrybecker4.mapland.screens.support;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.barrybecker4.mapland.backend.mapLandApi.model.RegionBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.RegionBeanCollection;
import com.barrybecker4.mapland.game.GameState;
import com.barrybecker4.mapland.server.IResponseHandler;
import com.google.api.client.json.GenericJson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Barry Becker
 */
public class RegionsRetrievalHandler implements IResponseHandler {

    private Context context;
    private GameState state;
    private LandMap map;

    public RegionsRetrievalHandler(Context context, GameState state, LandMap map) {
        this.context = context;
        this.state = state;
        this.map = map;
    }

    /** Show a popup with the user info */
    @Override
    public void jsonRetrieved(GenericJson result) {
        //if (result == null) {
        //    Toast.makeText(context, "unexpected null set of regions", Toast.LENGTH_SHORT).show();
        //}
        List<RegionBean> regions = ((RegionBeanCollection)result).getItems();
        // we should not need this if setting DATASTORE_EMPTY_LIST_SUPPORT would work
        if (regions == null) {
            regions = new ArrayList<>();
        }
        state.setVisibleRegions(regions);
        map.showRegions(regions, state.getCurrentUser().getUserId());

        String message = "Regions retrieved to client = " + listIds(regions);
        Log.i("REGION RETRIEVAL", message);
    }

    private List<Long> listIds(List<RegionBean> regions) {
        List<Long> idList = new LinkedList<>();
        if (regions != null) {
            for (RegionBean region : regions) {
                idList.add(region.getRegionId());
            }
        }
        return idList;
    }
}
