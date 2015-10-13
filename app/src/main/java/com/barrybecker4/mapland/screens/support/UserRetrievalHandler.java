package com.barrybecker4.mapland.screens.support;

import android.content.Context;
import android.widget.Toast;

import com.barrybecker4.mapland.backend.mapLandApi.model.UserBean;
import com.barrybecker4.mapland.server.IRetrievalHandler;
import com.google.api.client.json.GenericJson;

/**
 * @author Barry Becker
 */
public class UserRetrievalHandler implements IRetrievalHandler {

    private Context context;

    public UserRetrievalHandler(Context context) {
        this.context = context;
    }

    /** Show a popup with the user info */
    @Override
    public void entityRetrieved(GenericJson entity) {
        UserBean result = (UserBean) entity;
        String locs = "null locations!";
        if (result.getLocations() != null) {
            locs = result.getLocations().toString();
        }
        System.out.println("User = " + result.toString());
        String message = result.getUserId() + " owns " + result.getCredits() + " credits, and these locations: " + locs;
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
