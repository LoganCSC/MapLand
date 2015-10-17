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
        this.nwLat = vr.latLngBounds.southwest.longitude;
        this.nwLong = vr.latLngBounds.northeast.latitude;
        this.seLat = vr.latLngBounds.northeast.longitude;
        this.seLong = vr.latLngBounds.southwest.latitude;
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
}
