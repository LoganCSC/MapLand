/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.barrybecker4.mapland.screens;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.barrybecker4.mapland.R;
import com.barrybecker4.mapland.backend.mapLandApi.model.LocationBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.UserBean;
import com.barrybecker4.mapland.game.GameState;
import com.barrybecker4.mapland.game.GameStateInitializedListener;
import com.barrybecker4.mapland.game.LocationUtil;
import com.barrybecker4.mapland.screens.support.LandMap;
import com.barrybecker4.mapland.screens.support.LocationAddHandler;
import com.barrybecker4.mapland.screens.support.LocationsRetrievalHandler;
import com.barrybecker4.mapland.screens.support.UserAccounts;
import com.barrybecker4.mapland.screens.support.UserRetrievalHandler;
import com.barrybecker4.mapland.server.LocationAdder;
import com.barrybecker4.mapland.server.LocationRetriever;
import com.barrybecker4.mapland.server.UserRetriever;
import com.barrybecker4.mapland.server.UserUpdater;
import com.barrybecker4.mapland.server.ViewPort;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lang Grab is a game where you try to acquire as much land as you can in a certain time interval.
 */
public class LandGrabMapActivity extends FragmentActivity
        implements OnMapReadyCallback, AdapterView.OnItemSelectedListener, GameStateInitializedListener {

    // these spinners/droplists are AdapterViews and passed to onItemSelected.
    private Spinner userDropList;
    private Spinner mapTypeDropList;
    private CompoundButton mTrafficCheckbox;

    private LandMap theMap;
    private GameState state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.land_grab_screen);
        state = new GameState(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // user droplist  (for when there are more than one user on the device)
        userDropList = (Spinner) findViewById(R.id.user_select);
        ArrayAdapter userSelectAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, UserAccounts.getAccountNames(this));
        userDropList.setAdapter(userSelectAdapter);
        userDropList.setOnItemSelectedListener(this);

        // map type droplist
        mapTypeDropList = (Spinner) findViewById(R.id.map_type_select);
        ArrayAdapter mapTypeAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, LandMap.MAP_TYPE_VALUES);
        mapTypeDropList.setAdapter(mapTypeAdapter);
        mapTypeDropList.setOnItemSelectedListener(this);

        mTrafficCheckbox = (CompoundButton) findViewById(R.id.traffic_toggle);

        retrieveActiveUser();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        theMap = new LandMap(map);
        state.setCurrentPosition(theMap.getCurrentLocation());
        retrieveVisibleLocations();
    }

    /**
     * Called when the traffic checkbox is toggled
     */
    public void onToggleTraffic(View view) {
        theMap.showTraffic(mTrafficCheckbox.isChecked());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == userDropList) {
            state.reset();
            retrieveActiveUser();
        }
        else if (parent == mapTypeDropList) {
            if (theMap != null) {
                theMap.setMapType((String)mapTypeDropList.getSelectedItem());
            }
        }
        else {
            Log.i("WARNING", "No droplist found for " + parent.toString() + " item=" + parent.getSelectedItem());
        }
    }

    /**
     * Retrieve the user (asynchronously) specified in the droplist.
     */
    private void retrieveActiveUser() {
        String username = (String)userDropList.getSelectedItem();
        UserRetriever.getUser(username, this, new UserRetrievalHandler(this, state));
    }

    /**
     * Retrieve the locations that are in the current map viewport.
     */
    private void retrieveVisibleLocations() {
        VisibleRegion region = theMap.getVisibleRegion();
        ViewPort viewport = new ViewPort(region);
        LocationRetriever.getLocations(viewport, this, new LocationsRetrievalHandler(this, state));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Called when the game state is initialized
     * Locations are only added as needed. Initially there are no locations in a game
     * When a user occupies a spot for the first time, a location is created and the
     * user gets ownership of it.
     * If a user moves into a location owned by someone else, then they get ownership.
     */
    @Override
    public void initialized(GameState state) {

        //LocationBean currentLocation = null;
        UserBean user = state.getCurrentUser();

        // if the user owns the current location, then set it as current
        List<LocationBean> locations = state.getVisibleLocations();
        for (LocationBean loc : locations) {
            if (LocationUtil.contains(state.getCurrentPosition(), loc)) {
                state.setCurrentLocation(loc);
                if (!loc.getOwnerId().equals(user.getUserId())) {
                    // then need to change ownership on this location to the current user!
                    System.out.println("The location you are in is owned by " + loc.getOwnerId());

                    user.getLocations().add(loc.getId());
                    UserUpdater.updateUser(user, this, null);
                }
            }
        }
        if (state.getCurrentLocation() == null) {
            // otherwise, add this new location to the datastore, and to the users list of owned locations
            LocationBean loc = LocationUtil.createLocationAtPosition(user.getUserId(), state.getCurrentPosition());
            LocationAdder.addLocation(loc, this, new LocationAddHandler(this, state));
        }
    }
}
