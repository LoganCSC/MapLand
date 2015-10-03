package com.barrybecker4.mapland.backend;

/**
 * Used to transfer information about a specific map location
 * The rectangular region is defined by north-west (nw) and south-east (se) geo coordinates.
 *
 * @author Barry Becker
 */
public class LocationBean {

    private double nwLattitudeCoord;
    private double nwLongitudeCoord;
    private double seLattitudeCoord;
    private double seLongitudeCoord;

    /** Id of the user who owns this location */
    private String ownerId;

    /** the number of credits needed to buy this location */
    private long cost;

    /** the amount of daily income that this property provides the owner */
    private int income;

    /** additional info about the location. Probably added by ownwer(s) */
    private String notes;

}
