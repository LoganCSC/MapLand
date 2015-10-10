package com.barrybecker4.mapland.backend.datamodel;

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

    private double nwLattitudeCoord;
    private double nwLongitudeCoord;
    private double seLattitudeCoord;
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


    public double getNwLattitudeCoord() {
        return nwLattitudeCoord;
    }

    public void setNwLattitudeCoord(double nwLattitudeCoord) {
        this.nwLattitudeCoord = nwLattitudeCoord;
    }

    public double getNwLongitudeCoord() {
        return nwLongitudeCoord;
    }

    public void setNwLongitudeCoord(double nwLongitudeCoord) {
        this.nwLongitudeCoord = nwLongitudeCoord;
    }

    public double getSeLattitudeCoord() {
        return seLattitudeCoord;
    }

    public void setSeLattitudeCoord(double seLattitudeCoord) {
        this.seLattitudeCoord = seLattitudeCoord;
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
