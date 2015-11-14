/**
 * Main game screen.
 */
package com.barrybecker4.mapland.screens;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.barrybecker4.mapland.R;
import com.barrybecker4.mapland.backend.mapLandApi.model.RegionBean;
import com.barrybecker4.mapland.backend.mapLandApi.model.UserBean;
import com.barrybecker4.mapland.game.GameState;
import com.barrybecker4.mapland.game.GameStateChangededListener;
import com.barrybecker4.mapland.game.RegionUtil;
import com.barrybecker4.mapland.screens.dialogs.BuyRegionDialogFragment;
import com.barrybecker4.mapland.screens.dialogs.OnRegionBoughtHandler;
import com.barrybecker4.mapland.screens.support.LandMap;
import com.barrybecker4.mapland.screens.support.RegionAddHandler;
import com.barrybecker4.mapland.screens.support.RegionsRetrievalHandler;
import com.barrybecker4.mapland.screens.support.UserAccounts;
import com.barrybecker4.mapland.screens.support.UserRetrievalHandler;
import com.barrybecker4.mapland.server.tasks.RegionAdder;
import com.barrybecker4.mapland.server.tasks.RegionRetriever;
import com.barrybecker4.mapland.server.tasks.RegionTransferer;
import com.barrybecker4.mapland.server.tasks.UserRetriever;
import com.barrybecker4.mapland.server.ViewPort;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;

import java.io.Serializable;
import java.util.List;

/**
 * Lang Grab is a game where you try to acquire as much land as you can in a certain time interval.
 */
public class LandGrabMapActivity extends FragmentActivity
        implements OnMapReadyCallback, OnRegionBoughtHandler,
            AdapterView.OnItemSelectedListener,
            GameStateChangededListener {

    // these spinners/droplists are AdapterViews and passed to onItemSelected.
    private Spinner userDropList;
    private Spinner mapTypeDropList;
    private CompoundButton mTrafficCheckbox;
    private TextView mLocationTextView;

    private LandMap theMap;
    private GameState state;

    /** location tolerance in meters. More that this will not be accepted */
    private static final float GPS_TOL = 12;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.land_grab_screen);
        state = new GameState(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // user droplist (for when there are more than one user on the device)
        userDropList = (Spinner) findViewById(R.id.user_select);
        ArrayAdapter userSelectAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, UserAccounts.getAccountNames(this));
        userDropList.setAdapter(userSelectAdapter);
        userDropList.setOnItemSelectedListener(this);

        // map type droplist (for selecting terrain, satellite, normal, etc)
        mapTypeDropList = (Spinner) findViewById(R.id.map_type_select);
        ArrayAdapter mapTypeAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, LandMap.MAP_TYPE_VALUES);
        mapTypeDropList.setAdapter(mapTypeAdapter);
        mapTypeDropList.setOnItemSelectedListener(this);

        mTrafficCheckbox = (CompoundButton) findViewById(R.id.traffic_toggle);
        mLocationTextView = (TextView) findViewById(R.id.location_text);
        Button mMoveButton = (Button) findViewById(R.id.move_position);
        mMoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng cpos = RegionUtil.jitter(theMap.getCurrentPosition());
                Location loc = new Location(RegionUtil.createLocation(cpos));
                locationChanged(loc);
            }
        });
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

    @Override
    public void onMapReady(GoogleMap map) {
        theMap = new LandMap(map);

        theMap.setCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.v("MAP", "Camera positionChanged.");
                retrieveVisibleRegions();
            }
        });

        theMap.setLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location position) {
                locationChanged(position);
            }
        });
    }

    private void locationChanged(Location loc) {
        Log.i("MAP", "User's positionChanged: " + loc + " accuracy =" + loc.getAccuracy());
        if (loc.getAccuracy() <= GPS_TOL) {
            mLocationTextView.setText(RegionUtil.formatLocation(loc));
            state.setCurrentPosition(new LatLng(loc.getLatitude(), loc.getLongitude()));
        }
    }

    /**
     * Retrieve the user (asynchronously) specified in the droplist (if more than one on device).
     */
    private void retrieveActiveUser() {
        String username = (String)userDropList.getSelectedItem();
        UserRetriever.getUser(username, this, new UserRetrievalHandler(this, state));
        if ("guest".equals(username)) {
            //when running in the emulator (as guest), there is no current location, so fake it
            state.setCurrentPosition(theMap.getCurrentPosition());
        }
    }

    /**
     * Retrieve the regions that are in the current map viewport.
     */
    private void retrieveVisibleRegions() {
        VisibleRegion region = theMap.getVisibleRegion();
        System.out.println("The visible region is " + region.toString());
        Log.i("MAP", "The visible region is " + region.toString());
        ViewPort viewport = new ViewPort(region);
        RegionRetriever.getRegions(viewport, this, new RegionsRetrievalHandler(state, theMap));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    /**
     * Called when the game state is initialized, or when the user is positionChanged, or when the user changes their region.
     * Regions are only added as needed. Initially there are no regions in a game
     * When a user occupies a region for the first time, that rectangular region is created and
     * the user immediately gets ownership of it.
     * If a user moves into a region owned by someone else, then they get ownership.
     * That transfer of ownership is immediate right now, but really they should be prompted to buy it.
     */
    @Override
    public void stateChanged(GameState state) {

        System.out.println("Game state changed. User position = " + state.getCurrentPosition());
        //Toast.makeText(this, "Game state changed. User position = " + state.getCurrentPosition(), Toast.LENGTH_SHORT).show();
        UserBean user = state.getCurrentUser();

        // if the user owns the current region, then set it as current
        List<RegionBean> regions = state.getVisibleRegions();
        for (RegionBean region : regions) {
            if (RegionUtil.contains(state.getCurrentPosition(), region)) {
                System.out.println("The current position " + state.getCurrentPosition() + " is within " + region);
                state.setCurrentRegion(region);
                if (!region.getOwnerId().equals(user.getUserId())) {
                    showBuyRegionDlg();
                }
            }
            else {
                System.out.println("The current position " + state.getCurrentPosition() + " is not within " + region);
                Log.i("STATE_CHANGE", "The current position " + state.getCurrentPosition() + " is not within " + region);
            }
        }
        if (state.getCurrentRegion() == null
                || !RegionUtil.contains(state.getCurrentPosition(), state.getCurrentRegion())) {
            // otherwise, add this new region to the datastore, and to the users list of owned regions
            // Both must be done at the same time as part of a single atomic transaction
            Toast.makeText(this, "no region at this position. Creating.", Toast.LENGTH_SHORT).show();
            RegionBean region = RegionUtil.createRegionAtPosition(user.getUserId(), state.getCurrentPosition());
            RegionAdder.addRegionForUser(region, this, new RegionAddHandler(state));
            state.setCurrentRegion(region);
            retrieveVisibleRegions();
        }

        theMap.setVisibleRegions(state.getVisibleRegions(), user.getUserId()); // updates too often?
    }

    /**
     * If the user says yes when asked, do the transfer in regionBoughtByCurrentUser
     */
    private void showBuyRegionDlg() {
        BuyRegionDialogFragment dialog = new BuyRegionDialogFragment();
        Bundle args = new Bundle();
        UserBean user = state.getCurrentUser();
        RegionBean region = state.getCurrentRegion();
        args.putString("oldOwner", region.getOwnerId());
        args.putDouble("cost", region.getCost());
        args.putDouble("balance", user.getCredits());
        dialog.setArguments(args);
        dialog.show(getFragmentManager(), "buy-region-dialog");
    }

    /**
     * Transfer ownership of the region from the old owner to the current user
     */
    @Override
    public void regionBoughtByCurrentUser() {
        UserBean user = state.getCurrentUser();
        RegionBean region = state.getCurrentRegion();

        Log.i("STATE_CHANGE", "The region you are in is owned by " + region.getOwnerId()
                + " Transferring ownership...");
        String oldOwner = region.getOwnerId();

        // This does 3 things: User has this region added, region has its owner set to user,
        // and the old owner has this region removed from its list.
        RegionTransferer.transferRegionOwnership(region, user, this);
        Toast.makeText(this, "Transferring ownership of "
                + region.getRegionId() + " from " + oldOwner
                + " to " + user.getUserId(), Toast.LENGTH_LONG).show();
    }
}
