package com.barrybecker4.mapland.server;

import com.google.android.gms.maps.model.VisibleRegion;

/**
 * Defines a rectangular viewport into the map
 */
public class ViewPort {

    private double nwLat;
    private double nwLong;
    private double seLat;
    private double seLong;

    public ViewPort(VisibleRegion vr) {
        this.nwLat = vr.latLngBounds.northeast.latitude;
        this.nwLong = vr.latLngBounds.southwest.longitude;
        this.seLat = vr.latLngBounds.southwest.latitude;
        this.seLong = vr.latLngBounds.northeast.longitude;
    }

    public ViewPort(double nwLat, double nwLong, double seLat, double seLong) {
        this.nwLat = nwLat;
        this.nwLong = nwLong;
        this.seLat = seLat;
        this.seLong = seLong;
    }

    public double getNwLat() {
        return nwLat;
    }

    public double getNwLong() {
        return nwLong;
    }

    public double getSeLat() {
        return seLat;
    }

    public double getSeLong() {
        return seLong;
    }

    public String toString() {
        return "[" + nwLat + ", " + nwLong + "] [" + seLat + ", " + seLong + "]";
    }
}
