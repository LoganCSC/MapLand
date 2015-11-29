package com.barrybecker4.mapland.screens.games;


import com.barrybecker4.mapland.backend.mapLandApi.model.GameBean;

import java.util.List;

/**
 * A list of all the games that are available to join.
 */
public final class GameDetailsList {

    /** This class should not be instantiated. */
    private GameDetailsList() {}

    // this is hardcoded for now, but will eventually come from the datastore
    public static final GameDetails[] GAMES = {
        new GameDetails("First test game", 3, 30),
        new GameDetails("Second game", 4, 20),
        new GameDetails("3rd game", 7, 2)
    };

    public static GameDetails[] createGameDetailsArray(List<GameBean> games) {
        GameDetails[] detailsList = new GameDetails[games.size()];

        int ct = 0;
        for (GameBean game : games) {
            detailsList[ct++] = new GameDetails(game.getGameName(), game.getNumPlayers(), game.getDuration());
        }
        return detailsList;
    }
}
