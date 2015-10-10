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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.barrybecker4.mapland.R;
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

    private CompoundButton mTrafficCheckbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_demo);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // map type droplist
        dropList = (Spinner) findViewById(R.id.map_type_select);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, mapTypeValues);
        dropList.setAdapter(adapter);
        dropList.setOnItemSelectedListener(this);

        mTrafficCheckbox = (CompoundButton) findViewById(R.id.traffic_toggle);

        // look this up in the cloud database and retrieve relevant info
        String username = getAccountName();

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

    /**
     * @return the account name. For gmail users, this is the email address
     */
    public String getAccountName() {
        // get the users already signed in account
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        Log.i("ACCT", "User Accounts");
        for (Account acct : list) {
            Log.i("ACCT", "Account = " + acct.toString());
        }

        String accountName;
        if (list.length == 0) {
            Log.i("ACCT", "No accounts found. Using guest user ");
            accountName = "guest";
        }
        else {
            Account account = list[0];
            accountName = account.name;
            Log.i("ACCT", "Account Name = " + accountName);
        }

        return accountName;
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
        if (theMap != null) {
            theMap.setMapType(MAP_TYPE_MAP.get(dropList.getSelectedItem()));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
