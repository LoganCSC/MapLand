package com.barrybecker4.mapland.screens.support;

import android.content.Context;
import android.widget.Toast;

import com.barrybecker4.mapland.backend.mapLandApi.model.RegionBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.RegionBeanCollection;
import com.barrybecker4.mapland.game.GameState;
import com.barrybecker4.mapland.server.IResponseHandler;
import com.google.api.client.json.GenericJson;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Barry Becker
 */
public class RegionsRetrievalHandler implements IResponseHandler {

    private Context context;
    private GameState state;

    public RegionsRetrievalHandler(Context context, GameState state) {
        this.context = context;
        this.state = state;
    }

    /** Show a popup with the user info */
    @Override
    public void jsonRetrieved(GenericJson result) {
        List<RegionBean> regions = ((RegionBeanCollection)result).getItems();
        state.setVisibleRegions(regions);

        String message = "Regions retrieved to client = " + listIds(regions);
        System.out.println(message);
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
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