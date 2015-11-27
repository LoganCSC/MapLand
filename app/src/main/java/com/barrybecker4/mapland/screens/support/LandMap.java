package com.barrybecker4.mapland.screens.support;

import android.content.Context;
import android.location.Location;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.barrybecker4.mapland.R;
import com.barrybecker4.mapland.backend.mapLandApi.model.RegionBean;
import com.barrybecker4.mapland.game.FormatUtil;
import com.barrybecker4.mapland.game.RegionUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
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

    // 37.6021,-122.0758 - house
    // 37.6131828,-122.0764492
    //private static final LatLng INITIAL_CENTER = new LatLng(37.65478, -122.07035);
    /** This is used if the position cannot be retrieved from the map - such as when the emulator is used */
    //private static final LatLng DEFAULT_POSITION = new LatLng(37.607768, -122.07912);
    private static final LatLng DEFAULT_POSITION = new LatLng(37.602768, -122.0752);
    private static final int INITIAL_ZOOM_LEVEL = 12;

    private static final Map<String, Integer> MAP_TYPE_MAP = new HashMap<>();
    static {
        MAP_TYPE_MAP.put("Normal", GoogleMap.MAP_TYPE_NORMAL);
        MAP_TYPE_MAP.put("Terrain", GoogleMap.MAP_TYPE_TERRAIN);
        MAP_TYPE_MAP.put("Hybrid", GoogleMap.MAP_TYPE_HYBRID);
        MAP_TYPE_MAP.put("Satellite", GoogleMap.MAP_TYPE_SATELLITE);
    }

    private static final int CURRENT_USER_COLOR = 0x880055EE;
    private static final int OTHER_USER_COLOR = 0x66BB4400;
    private static final int BORDER_COLOR = 0xAA000055;


    private GoogleMap theMap;
    private Marker currentMarker;
    private String currentUserId;
    private List<RegionBean> currentRegions;

    /**
     * @param map the google map used internally
     */
    public LandMap(GoogleMap map, final Context context) {
        theMap = map;

        map.setMyLocationEnabled(true);
        configureMapSettings(map);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng center = getCurrentPosition();

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, INITIAL_ZOOM_LEVEL));

        //map.setOnMapLoadedCallback(callback); // GoogleMap.OnMapLoadedCallback callback

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (currentMarker != null) {
                    currentMarker.remove();
                }
                final RegionBean clickedRegion = RegionUtil.findRegion(latLng, currentRegions);
                if (clickedRegion != null) {
                    theMap.setInfoWindowAdapter(new RegionInfoWindowAdapter(clickedRegion, context));

                    MarkerOptions opts = new MarkerOptions()
                            .alpha(0.1f).position(latLng)
                            .title(clickedRegion.getOwnerId());
                    currentMarker = theMap.addMarker(opts);
                    currentMarker.showInfoWindow();
                }
            }
        });
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
     * @param regions the list of currently visible regions to show
     * @param userId the current user's id
     */
    public void setVisibleRegions(List<RegionBean> regions, String userId) {
        this.currentRegions = regions;
        this.currentUserId = userId;
        drawRegions();
    }

    public void drawRegions() {
        if (currentUserId == null) {
            throw new IllegalStateException("Must set user and regions first.");
        }
        theMap.clear();
        if (currentRegions != null) {
            for (RegionBean region : currentRegions) {
                // for now just put a marker at the center of each region.
                double latitude = (region.getNwLatitudeCoord() + region.getSeLatitudeCoord()) / 2.0;
                double longitude = (region.getNwLongitudeCoord() + region.getSeLongitudeCoord()) / 2.0;
                LatLng center = new LatLng(latitude, longitude);
                System.out.println("Adding marker at " + center + " current = " + this.getCurrentPosition());
                int hue = region.getOwnerId().equals(currentUserId) ? CURRENT_USER_COLOR : OTHER_USER_COLOR;

                PolygonOptions options = new PolygonOptions();
                options.add(new LatLng(region.getNwLatitudeCoord(), region.getNwLongitudeCoord()),
                        new LatLng(region.getSeLatitudeCoord(), region.getNwLongitudeCoord()),
                        new LatLng(region.getSeLatitudeCoord(), region.getSeLongitudeCoord()),
                        new LatLng(region.getNwLatitudeCoord(), region.getSeLongitudeCoord()))
                        .strokeColor(BORDER_COLOR).strokeWidth(1f)
                        .fillColor(hue);
                theMap.addPolygon(options);
            }
        }
    }

    public LatLng getCurrentPosition() {
        Location loc = theMap.getMyLocation();
        LatLng pos = DEFAULT_POSITION;
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
