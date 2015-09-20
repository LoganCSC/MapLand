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

package com.example.mapdemo;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;

/**
 * This shows how to create a simple activity with a map and a marker on the map.
 */
public class BasicMapDemoActivity extends FragmentActivity
        implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {


    private Spinner dropList;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_demo);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //MapFragment mapFragment = (MapFragment) getFragmentManager()
        //        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // combo list
        dropList = (Spinner) findViewById(R.id.map_type_select);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, mapTypeValues);
        dropList.setAdapter(adapter);
        dropList.setOnItemSelectedListener(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        theMap = map;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(37.65478,-122.07035), 11));

        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        // Other supported types include: MAP_TYPE_NORMAL,
        // MAP_TYPE_TERRAIN, MAP_TYPE_HYBRID and MAP_TYPE_NONE
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        map.setMyLocationEnabled(true);
        UiSettings settings = map.getUiSettings();

        settings.setZoomControlsEnabled(true);
        //settings.setCompassEnabled(true);
        //settings.setMyLocationButtonEnabled(true);
        //settings.setScrollGesturesEnabled(isChecked(R.id.scroll_toggle));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (theMap != null) {
            theMap.setMapType(MAP_TYPE_MAP.get(dropList.getSelectedItem()));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
