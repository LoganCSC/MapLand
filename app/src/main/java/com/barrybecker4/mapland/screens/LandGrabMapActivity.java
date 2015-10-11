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

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.barrybecker4.mapland.R;
import com.barrybecker4.mapland.screens.support.UserAccounts;
import com.barrybecker4.mapland.screens.support.UserRetrievalHandler;
import com.barrybecker4.mapland.server.UserRetriever;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This shows how to create a simple activity with a map and a marker on the map.
 */
public class LandGrabMapActivity extends FragmentActivity
        implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    /// these spinners/droplists are AdapterViews and passed to onItemSelected.
    private Spinner userDropList;
    private Spinner mapTypeDropList;

    private List<String> mapTypeValues = Arrays.asList(
            "Normal", "Terrain", "Hybrid", "Satellite");
    private static final Map<String, Integer> MAP_TYPE_MAP = new HashMap<>();
    private GoogleMap theMap;

    static {
        MAP_TYPE_MAP.put("Normal", GoogleMap.MAP_TYPE_NORMAL);
        MAP_TYPE_MAP.put("Terrain", GoogleMap.MAP_TYPE_TERRAIN);
        MAP_TYPE_MAP.put("Hybrid", GoogleMap.MAP_TYPE_HYBRID);
        MAP_TYPE_MAP.put("Satellite", GoogleMap.MAP_TYPE_SATELLITE);
    }

    private CompoundButton mTrafficCheckbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.land_grab_screen);

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
                android.R.layout.simple_spinner_item, mapTypeValues);
        mapTypeDropList.setAdapter(mapTypeAdapter);
        mapTypeDropList.setOnItemSelectedListener(this);

        mTrafficCheckbox = (CompoundButton) findViewById(R.id.traffic_toggle);

        updateWithNewUser();
    }


    @Override
    public void onMapReady(GoogleMap map) {
        theMap = map;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(37.65478, -122.07035), 11));

        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        // Other supported types include: MAP_TYPE_NORMAL,
        // MAP_TYPE_TERRAIN, MAP_TYPE_HYBRID and MAP_TYPE_NONE
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        map.setMyLocationEnabled(true);
        UiSettings settings = map.getUiSettings();

        settings.setZoomControlsEnabled(true);
        settings.setCompassEnabled(true);
        settings.setMyLocationButtonEnabled(true);
        settings.setScrollGesturesEnabled(true);
    }

    /**
     * Called when the traffic checkbox is toggled
     */
    public void onToggleTraffic(View view) {
        theMap.setTrafficEnabled(mTrafficCheckbox.isChecked());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == userDropList) {
            updateWithNewUser();
        }
        else if (parent == mapTypeDropList) {
            if (theMap != null) {
                theMap.setMapType(MAP_TYPE_MAP.get((String)mapTypeDropList.getSelectedItem()));
            }
        }
        else {
            Log.i("WARNING", "No droplist found for " + parent.toString() + " item=" + parent.getSelectedItem());
        }
    }

    private void updateWithNewUser() {
        String username = (String)userDropList.getSelectedItem(); //UserAccounts.getDefaultAccountName(this);

        // call the backend server asynchronously
        UserRetriever.getUser(username, this, new UserRetrievalHandler(this));
        //AsyncTask<Pair<Context, String>, Void, UserBean> task = new UserRetriever();
        //task.execute(new Pair<Context, String>(this, username));
        /*
        Log.i("TASK", "status = " + task.getStatus());
        try {
            Log.i("TASK", "value = " + task.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
