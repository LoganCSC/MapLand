package com.barrybecker4.mapland.screens.games;



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
}
