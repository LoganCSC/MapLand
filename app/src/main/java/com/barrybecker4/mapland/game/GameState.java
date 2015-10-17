package com.barrybecker4.mapland.game;

import com.barrybecker4.mapland.backend.mapLandApi.model.LocationBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.UserBean;

import java.util.List;

/**
 * Maintains the current MapLand game state
 */
public class GameState {

    private UserBean currentUser;
    private LocationBean currentLocation;
    private List<LocationBean> visibleLocations;


    public UserBean getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserBean currentUser) {
        this.currentUser = currentUser;
    }

    public LocationBean getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LocationBean currentLocation) {
        this.currentLocation = currentLocation;
    }

    public List<LocationBean> getVisibleLocations() {
        return visibleLocations;
    }

    public void setVisibleLocations(List<LocationBean> visibleLocations) {
        this.visibleLocations = visibleLocations;
    }


}
