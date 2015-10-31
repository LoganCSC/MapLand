package com.barrybecker4.mapland.screens.support;

import android.content.Context;
import android.widget.Toast;

import com.barrybecker4.mapland.backend.mapLandApi.model.UserBean;
import com.barrybecker4.mapland.game.GameState;
import com.barrybecker4.mapland.server.IResponseHandler;
import com.google.api.client.json.GenericJson;

/**
 * @author Barry Becker
 */
public class UserRetrievalHandler implements IResponseHandler {

    private Context context;
    private GameState state;

    public UserRetrievalHandler(Context context, GameState state) {

        this.context = context;
        this.state = state;
    }

    /** Show a popup with the user info */
    @Override
    public void jsonRetrieved(GenericJson result) {
        UserBean user = (UserBean) result;

        if (user == null) {
            Toast.makeText(context, "user unexpectedly null", Toast.LENGTH_SHORT).show();
            return;
        }
        state.setCurrentUser(user);

        String regions = "null regions!";
        if (user.getRegions() != null) {
            regions = user.getRegions().toString();
        }
        System.out.println("User = " + user.toString());
        String message = user.getUserId() + " owns " + user.getCredits() + " credits, and these locations: " + regions;
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
