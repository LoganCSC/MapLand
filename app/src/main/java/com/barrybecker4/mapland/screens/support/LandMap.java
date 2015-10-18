package com.barrybecker4.mapland.screens.support;

import android.location.Location;

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

    //private static final LatLng INITIAL_CENTER = new LatLng(37.65478, -122.07035);
    private static final LatLng DEFAULT_POSITION = new LatLng(37.6545, -122.0701);
    private static final int INITIAL_ZOOM_LEVEL = 11;

    private static final Map<String, Integer> MAP_TYPE_MAP = new HashMap<>();
    static {
        MAP_TYPE_MAP.put("Normal", GoogleMap.MAP_TYPE_NORMAL);
        MAP_TYPE_MAP.put("Terrain", GoogleMap.MAP_TYPE_TERRAIN);
        MAP_TYPE_MAP.put("Hybrid", GoogleMap.MAP_TYPE_HYBRID);
        MAP_TYPE_MAP.put("Satellite", GoogleMap.MAP_TYPE_SATELLITE);
    }

    private GoogleMap theMap;

    public LandMap(GoogleMap map) {
        theMap = map;
        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        configureMapSettings(map);;

        LatLng pos = getCurrentLocation();
        LatLng center = pos;


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, INITIAL_ZOOM_LEVEL));
        map.addMarker(new MarkerOptions().position(pos).alpha(0.5f).title("Start"));
    }

    public LatLng getCurrentLocation() {
        Location loc = theMap.getMyLocation();
        LatLng pos = DEFAULT_POSITION;;
        if (loc != null) {
            pos =  new LatLng(loc.getLatitude(), loc.getLongitude());
        }
        return pos;
    }

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
