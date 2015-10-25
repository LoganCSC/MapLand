package com.barrybecker4.mapland.screens.support;

import android.location.Location;

import com.barrybecker4.mapland.backend.mapLandApi.model.LocationBean;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulate the google map instance used by the game.
 */
public class LandMap {

    public static final List<String> MAP_TYPE_VALUES = Arrays.asList(
            "Normal", "Terrain", "Hybrid", "Satellite");

    // 37.6017602,-122.074174 - house
    // 37.6131828,-122.0764492
    //private static final LatLng INITIAL_CENTER = new LatLng(37.65478, -122.07035);
    /** This is used if the position cannot be retrieved from the map - such as when the emulator is used */
    private static final LatLng DEFAULT_POSITION = new LatLng(37.607768, -122.07912);
    private static final int INITIAL_ZOOM_LEVEL = 11;

    private static final Map<String, Integer> MAP_TYPE_MAP = new HashMap<>();
    static {
        MAP_TYPE_MAP.put("Normal", GoogleMap.MAP_TYPE_NORMAL);
        MAP_TYPE_MAP.put("Terrain", GoogleMap.MAP_TYPE_TERRAIN);
        MAP_TYPE_MAP.put("Hybrid", GoogleMap.MAP_TYPE_HYBRID);
        MAP_TYPE_MAP.put("Satellite", GoogleMap.MAP_TYPE_SATELLITE);
    }

    private GoogleMap theMap;

    /**
     *
     * @param map the google map used internally
     */
    public LandMap(GoogleMap map) {
        theMap = map;
        //this.readyCallback = readyCallback;
        map.setMyLocationEnabled(true);
        configureMapSettings(map);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng center = getCurrentPosition();

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, INITIAL_ZOOM_LEVEL));

        // This adds a transparent marker at the specified position.
        //map.addMarker(new MarkerOptions().position(center).alpha(0.5f).title("Start"));
    }

    /**
     * @param listener Called whenever the camera is done changing. IOW when the user has positionChanged the viewport.
     *    need to use this because it can take some time for the map to initialize
     *                     before its safe to call the getViewport method.
     */
    public void setCameraChangeListener(GoogleMap.OnCameraChangeListener listener) {
        theMap.setOnCameraChangeListener(listener);
    }

    /**
     * @param listener called when the users location changes
     */
    public void setLocationChangeListener(GoogleMap.OnMyLocationChangeListener listener) {
        theMap.setOnMyLocationChangeListener(listener);
    }

    /**
     * Brian, make this show rectangles color-coded by owner instead of the default marker.
     * @param locations the list of currently visible locations to show
     */
    public void showLocations(List<LocationBean> locations) {
        theMap.clear();
        for (LocationBean location : locations) {
            // for now just put a marker at the center of each region.
            double latitude = (location.getNwLatitudeCoord() + location.getSeLatitudeCoord()) / 2.0;
            double longitude = (location.getNwLongitudeCoord() + location.getSeLongitudeCoord()) / 2.0;
            LatLng center = new LatLng(latitude, longitude);
            System.out.println("Adding maker at "+ center +" current = " + this.getCurrentPosition() );
            theMap.addMarker(new MarkerOptions().position(center).alpha(0.5f).title(location.getOwnerId()));
        }
    }

    public LatLng getCurrentPosition() {
        Location loc = theMap.getMyLocation();
        LatLng pos = DEFAULT_POSITION;;
        if (loc != null) {
            pos =  new LatLng(loc.getLatitude(), loc.getLongitude());
        }
        return pos;
    }

    /** @return the current camera viewport */
    public VisibleRegion getVisibleRegion() {
        return theMap.getProjection().getVisibleRegion();
    }

    public void setMapType(String type) {
        theMap.setMapType(MAP_TYPE_MAP.get(type));
    }

    public void showTraffic(boolean enabled) {
        theMap.setTrafficEnabled(enabled);
    }

    private void configureMapSettings(GoogleMap map) {
        UiSettings settings = map.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setCompassEnabled(true);
        settings.setMyLocationButtonEnabled(true);
        settings.setScrollGesturesEnabled(true);
    }
}