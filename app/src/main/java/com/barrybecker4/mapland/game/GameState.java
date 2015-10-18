package com.barrybecker4.mapland.game;

import com.barrybecker4.mapland.backend.mapLandApi.model.LocationBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.UserBean;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Maintains the current MapLand game state
 */
public class GameState {

    /** user playing the game on this client device */
    private UserBean currentUser;

    /** best guess as to current position on the map */
    private LatLng currentPosition;

    /** location containing the current position */
    private LocationBean currentLocation;

    /** All visible locations. Changes with view port navigation. */
    private List<LocationBean> visibleLocations;

    private GameStateInitializedListener listener;

    private boolean initialized;

    public GameState(GameStateInitializedListener listener) {
        this.listener = listener;
        this.initialized = false;
    }

    /** call this before changing the current user */
    public void reset() {
        currentUser = null;
        initialized = false;
    }

    public UserBean getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserBean currentUser) {
        this.currentUser = currentUser;
        checkIfInitialized();
    }

    public LatLng getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(LatLng currentPosition) {
        this.currentPosition = currentPosition;
        checkIfInitialized();
    }

    public LocationBean getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LocationBean currentLocation) {
        this.currentLocation = currentLocation;
        checkIfInitialized();
    }

    public List<LocationBean> getVisibleLocations() {
        return visibleLocations;
    }

    public void setVisibleLocations(List<LocationBean> visibleLocations) {
        this.visibleLocations = visibleLocations;
        checkIfInitialized();
    }

    private void checkIfInitialized() {
        if (initialized) return;
        initialized = (currentUser != null && currentPosition != null && visibleLocations != null);

        if (initialized && listener != null) {
            listener.initialized(this);
        }
    }

}
