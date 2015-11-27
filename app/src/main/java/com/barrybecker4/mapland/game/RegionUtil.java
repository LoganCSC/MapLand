package com.barrybecker4.mapland.game;

import android.graphics.Region;
import android.location.Location;

import com.barrybecker4.mapland.backend.mapLandApi.model.RegionBean;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Random;

/**
 * The bean class is just for the data so region related methods go here.
 */
public class RegionUtil {

    private static final int PRECISION = 3;
    private static final double REGION_SIZE = Math.pow(10, -PRECISION);
    private static final double REGION_SCALE = Math.pow(10, PRECISION);
    private static final double TOLERANCE = Math.pow(10, -(PRECISION + 1));
    private static final double EPS = Math.pow(10, -(PRECISION + 2));
    private static final double EPS_SCALE = Math.pow(10, PRECISION + 2);
    private static final Random RND = new Random();

    public static boolean contains(LatLng point, RegionBean region) {
        return (point.latitude < region.getNwLatitudeCoord()
                && point.latitude >= region.getSeLatitudeCoord()
                && point.longitude >= region.getNwLongitudeCoord()
                && point.longitude < region.getSeLongitudeCoord());
    }

    public static RegionBean createRegionAtPosition(String owner, LatLng here) {
        RegionBean region = new RegionBean();
        region.setOwnerId(owner);
        region.setNwLatitudeCoord(roundUp(here.latitude + EPS));
        region.setNwLongitudeCoord(roundDown(here.longitude));
        region.setSeLatitudeCoord(roundDown(here.latitude));
        region.setSeLongitudeCoord(roundUp(here.longitude + EPS));
        return region;
    }

    /**
     * @return true if old region is not null and newRegion is different from oldRegion within PRECISION + 1.
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
        return Math.sqrt(deltaLat * deltaLat + deltaLong * deltaLong);
    }

    /**
     * Move the specified position randomly in one of the compass directions by one region edge unit.
     * @param position the position to jitter
     * @return changed position
     */
    public static LatLng jitter(LatLng position) {
        // move it a random amount.
        int rnd = RND.nextInt(4);
        LatLng newPosition;
        switch (rnd) {
            case 0: newPosition = new LatLng(position.latitude + REGION_SIZE, position.longitude); break;
            case 1: newPosition = new LatLng(position.latitude - REGION_SIZE, position.longitude); break;
            case 2: newPosition = new LatLng(position.latitude, position.longitude + REGION_SIZE); break;
            case 3: newPosition = new LatLng(position.latitude, position.longitude - REGION_SIZE); break;
            default: throw new IllegalStateException("Unexpected rnd:"+ rnd);
        }
        return newPosition;
    }

    public static Location createLocation(LatLng pos) {
        Location location = new Location("Test");
        location.setLatitude(pos.latitude);
        location.setLongitude(pos.longitude);
        location.setTime(new Date().getTime());
        return location;
    }

    public static String formatLocation(Location loc) {
        float lat = RegionUtil.roundToEPS(loc.getLatitude());
        float lng = RegionUtil.roundToEPS(loc.getLongitude());
        return "[" + lat + ", " + lng + "]("+loc.getAccuracy()+")";
    }

    /**
     * Find a region that contains a specified point among a list of regions.
     * There are potentially more efficient ways to find the region 9like a k-d tree etc)
     * @return the found region or null if not found
     */
    public static RegionBean findRegion(LatLng latLng, List<RegionBean> regions) {
        for (RegionBean region : regions) {
            if (latLng.latitude >= region.getSeLatitudeCoord()
                    && latLng.longitude >= region.getNwLongitudeCoord()
                    && latLng.latitude < region.getNwLatitudeCoord()
                    && latLng.longitude < region.getSeLongitudeCoord()) {
                return region;
            }
        }
        return null;
    }

    private static float roundToEPS(double coord) {
        return (float) (Math.round(coord * EPS_SCALE) / EPS_SCALE);
    }

    private static double roundDown(double coord) {
        return Math.floor(coord * REGION_SCALE) / REGION_SCALE;
    }

    private static double roundUp(double coord) {
        return Math.ceil(coord * REGION_SCALE) / REGION_SCALE;
    }
}
