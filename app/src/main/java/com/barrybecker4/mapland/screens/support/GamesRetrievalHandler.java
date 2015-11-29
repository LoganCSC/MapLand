package com.barrybecker4.mapland.screens.support;

import android.util.Log;

import com.barrybecker4.mapland.backend.mapLandApi.model.GameBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.GameBeanCollection;
import com.barrybecker4.mapland.backend.mapLandApi.model.RegionBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.RegionBeanCollection;
import com.barrybecker4.mapland.game.GameState;
import com.barrybecker4.mapland.screens.GameManagementActivity;
import com.barrybecker4.mapland.server.IResponseHandler;
import com.google.api.client.json.GenericJson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Barry Becker
 */
public class GamesRetrievalHandler implements IResponseHandler {

    GameManagementActivity management;

    public GamesRetrievalHandler(GameManagementActivity management) {
        this.management = management;
    }

    /** Show a popup with the user info */
    @Override
    public void jsonRetrieved(GenericJson result) {
        GameBeanCollection gameCollection = ((GameBeanCollection)result);
        if (gameCollection == null) {
            Log.e("REGION RETRIEVAL", "game collection null");
            return;
        }
        List<GameBean> games = gameCollection.getItems();
        // we should not need this if setting DATASTORE_EMPTY_LIST_SUPPORT would work
        if (games == null) {
            games = new ArrayList<>();
        }

        management.gamesRetrieved(games);

        String message = "Games retrieved to client = " + listIds(games);
        Log.i("GAME RETRIEVAL", message);
    }

    private List<Long> listIds(List<GameBean> games) {
        List<Long> idList = new LinkedList<>();
        if (games != null) {
            for (GameBean game : games) {
                idList.add(game.getGameId());
            }
        }
        return idList;
    }
}
