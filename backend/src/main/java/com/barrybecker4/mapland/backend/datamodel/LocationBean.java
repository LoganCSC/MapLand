package com.barrybecker4.mapland.backend.datamodel;

import com.google.appengine.api.datastore.GeoPt;

/**
 * Used to transfer information about a specific map location
 * The rectangular region is defined by north-west (nw) and south-east (se) geo coordinates.
 *
 * @author Barry Becker
 */
public class LocationBean {

    private Long id;

    /** Id of the user who owns this location */
    private String ownerId;
    /* switch to using GeoPt when its ready for prime time
    private GeoPt northWestCorner;
    private GeoPt southEastCorner; */


    private double nwLatitudeCoord;
    private double nwLongitudeCoord;
    private double seLatitudeCoord;
    private double seLongitudeCoord;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /** the number of credits needed to buy this location */
    private long cost;

    /** the amount of daily income that this property provides the owner */
    private int income;

    /** additional info about the location. Probably added by ownwer(s) */
    private String notes;

    /*
    public GeoPt getNorthWestCorner() {
        return northWestCorner;
    }

    public void setNorthWestCorner(GeoPt pt) {
        this.northWestCorner = pt;
    }

    public void setSouthEastCorner(GeoPt pt) {
        this.southEastCorner = pt;
    }

    public GeoPt getSouthEastCorner() {
        return southEastCorner;
    }*/

    public double getNwLatitudeCoord() {
        return nwLatitudeCoord;
    }

    public void setNwLatitudeCoord(double nwLatitudeCoord) {
        this.nwLatitudeCoord = nwLatitudeCoord;
    }

    public double getNwLongitudeCoord() {
        return nwLongitudeCoord;
    }

    public void setNwLongitudeCoord(double nwLongitudeCoord) {
        this.nwLongitudeCoord = nwLongitudeCoord;
    }

    public double getSeLatitudeCoord() {
        return seLatitudeCoord;
    }

    public void setSeLatitudeCoord(double seLatitudeCoord) {
        this.seLatitudeCoord = seLatitudeCoord;
    }

    public double getSeLongitudeCoord() {
        return seLongitudeCoord;
    }

    public void setSeLongitudeCoord(double seLongitudeCoord) {
        this.seLongitudeCoord = seLongitudeCoord;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
