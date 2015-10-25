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

    /** best guess as to the user's current position on the map */
    private LatLng currentPosition;

    /** location containing the current position. Perhaps rename location to region to reduce confusion. */
    private LocationBean currentLocation;

    /** All visible locations. Changes with view port navigation. */
    private List<LocationBean> visibleLocations;
    private List<LocationBean> previousVisibleLocations;

    private GameStateChangededListener listener;

    private boolean changed;

    public GameState(GameStateChangededListener listener) {
        this.listener = listener;
        this.changed = false;
    }

    /** call this before changing the current user */
    public void reset() {
        currentUser = null;
        changed = false;
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
        this.previousVisibleLocations = this.visibleLocations;
        this.visibleLocations = visibleLocations;
        if (this.previousVisibleLocations == null) {
            // After the first update, we do not want to call statChanged when the visible locations change.
            checkIfInitialized();
        }
    }

    /**
     * Called when first initialized, and whenever the current user or her position changes.
     */
    private void checkIfInitialized() {
        if (changed) return;
        changed = (currentUser != null
                && currentPosition != null
                && visibleLocations != null);

        if (changed && listener != null) {
            listener.stateChanged(this);
            changed = false;
        }
    }

}
