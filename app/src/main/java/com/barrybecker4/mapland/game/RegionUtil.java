package com.barrybecker4.mapland.game;

import com.barrybecker4.mapland.backend.mapLandApi.model.RegionBean;
import com.google.android.gms.maps.model.LatLng;

/**
 * The bean class is just for the data so location related methods go here.
 */
public class RegionUtil {

    private static final int PRECISION = 3;
    private static final double SCALE = Math.pow(10, PRECISION);
    private static final double TOLERANCE = Math.pow(10, -(PRECISION + 1));
    private static final double EPS = Math.pow(10, -(PRECISION + 2));

    public static boolean contains(LatLng point, RegionBean region) {
        return (point.latitude < region.getNwLatitudeCoord()
                && point.latitude >= region.getSeLatitudeCoord()
                && point.longitude >= region.getNwLongitudeCoord()
                && point.longitude < region.getSeLongitudeCoord());
    }

    public static RegionBean createRegionAtPosition(String owner, LatLng here) {
        RegionBean loc = new RegionBean();
        loc.setOwnerId(owner);
        loc.setNwLatitudeCoord(roundUp(here.latitude + EPS));
        loc.setNwLongitudeCoord(roundDown(here.longitude));
        loc.setSeLatitudeCoord(roundDown(here.latitude));
        loc.setSeLongitudeCoord(roundUp(here.longitude + EPS));
        return loc;
    }

    /**
     * @return true if old location is not null and newRegion is different from oldRegion within PRECISION + 1.
     *   Using the precision avoids frequent updates
     */
    public static boolean positionChanged(LatLng newPosition, LatLng oldPosition) {
        return oldPosition == null || distance(newPosition, oldPosition) > TOLERANCE;
    }

    /**
     * @return the distance between the two positions (in degrees).
     */
    public static double distance(LatLng newPosition, LatLng oldPosition) {
        double deltaLat =  newPosition.latitude - oldPosition.latitude;
        double deltaLong = newPosition.longitude - oldPosition.longitude;
        return deltaLat * deltaLat + deltaLong * deltaLong;
    }

    private static double roundDown(double coord) {
        return Math.floor(coord * SCALE) / SCALE;
    }

    private static double roundUp(double coord) {
        return Math.ceil(coord * SCALE) / SCALE;
    }
}
