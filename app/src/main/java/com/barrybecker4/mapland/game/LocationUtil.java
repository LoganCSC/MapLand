package com.barrybecker4.mapland.game;

import com.barrybecker4.mapland.backend.mapLandApi.model.LocationBean;
import com.google.android.gms.maps.model.LatLng;

/**
 * The bean class is just for the data so location related methods go here.
 */
public class LocationUtil {

    private static final int PRECISION = 3;
    private static final double SCALE = Math.pow(10, PRECISION);

    public static boolean contains(LatLng point, LocationBean location) {
        return (point.latitude >= location.getNwLatitudeCoord()
                && point.latitude < location.getSeLatitudeCoord()
                && point.longitude >= location.getNwLongitudeCoord()
                && point.longitude < location.getSeLongitudeCoord());
    }

    public static LocationBean createLocationAtPosition(String owner, LatLng here) {
        LocationBean loc = new LocationBean();
        loc.setOwnerId(owner);
        loc.setNwLatitudeCoord(roundDown(here.latitude));
        loc.setNwLongitudeCoord(roundDown(here.longitude));
        loc.setSeLatitudeCoord(roundUp(here.latitude));
        loc.setSeLongitudeCoord(roundUp(here.longitude));
        return loc;
    }

    private static double roundDown(double coord) {
        return Math.floor(coord * SCALE) / SCALE;
    }

    private static double roundUp(double coord) {
        return Math.ceil(coord * SCALE) / SCALE;
    }
}
