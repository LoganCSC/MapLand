package com.barrybecker4.mapland.game;

/**
 * Call when all the components of the came state have been initialized.
 * This is needed since the different parts are set asynchronously.
 */
public interface GameStateInitializedListener {

    void initialized(GameState state);
}
