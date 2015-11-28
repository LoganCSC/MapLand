package com.barrybecker4.mapland.screens.dialogs;

import com.barrybecker4.mapland.backend.mapLandApi.model.GameBean;

/**
 * @author Barry Becker
 */
public interface OnNewGameCreatedHandler {

    void createNewGame(GameBean newGame);
}
