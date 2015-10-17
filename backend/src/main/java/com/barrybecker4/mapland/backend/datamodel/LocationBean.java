package com.barrybecker4.mapland.backend.datamodel;

import com.google.api.services.datastore.DatastoreV1;
import com.google.api.services.datastore.client.DatastoreHelper;
import com.google.api.services.datastore.DatastoreV1.Entity;
import com.google.appengine.api.datastore.GeoPt;

import java.util.Map;

/**
 * Used to transfer information about a specific map location
 * The rectangular region is defined by north-west (nw) and south-east (se) geo coordinates.
 *
 * Locations are square grid cells on a map where the coordinates of the corner vertices
 * are simply the lat/long degree coordinates rounded to 3 decimal places.
 * Since 1 degree of latitude ~= 69 miles,
 * this means the edge length of a grid cell is about 364 feet in the horizontal direction.
 * Since 1 degree of longitude ~= 54.6 miles,
 * this means the edge length of a grid cell is about 288 feet in the vertical direction.
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

    public LocationBean() {}

    public LocationBean(Entity locationEntity) {

        Map<String, DatastoreV1.Value> propertyMap = DatastoreHelper.getPropertyMap(locationEntity);
        //System.out.println("location propertyMap = "+ propertyMap);

        Long locationId = propertyMap.get("id").getIntegerValue();
        String ownerId = propertyMap.get("ownerId").getStringValue();
        Long cost = propertyMap.get("cost").getIntegerValue();
        Integer income = (int) propertyMap.get("income").getIntegerValue();
        Double nwLat = propertyMap.get("nwLatitude").getDoubleValue();
        Double nwLong = propertyMap.get("nwLongitude").getDoubleValue();
        Double seLat = propertyMap.get("seLatitude").getDoubleValue();
        Double seLong = propertyMap.get("seLongitude").getDoubleValue();
        /*
        List<Long> locations = new ArrayList<>();
        for (Value value : propertyMap.get("locations").getListValueList()) {
            System.out.println(value.getIntegerValue());
            locations.add(value.getIntegerValue());
        }*/

        System.out.println("created Location: " + this);

        this.setId(locationId);
        this.setOwnerId(ownerId);
        this.setCost(cost);
        this.setIncome(income);
        this.setNwLatitudeCoord(nwLat);
        this.setNwLongitudeCoord(nwLong);
        this.setSeLatitudeCoord(seLat);
        this.setSeLongitudeCoord(seLong);
        /** GeoPt not yet ready for prime time unfortunately
         location.setNorthWestCorner(new GeoPt(nwLat.floatValue(), nwLong.floatValue()));
         location.setSouthEastCorner(new GeoPt(seLat.floatValue(), seLong.floatValue()));
         */
    }


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

    public String toString() {
        return "{locationId: " + this.id + " owner: " + this.ownerId + " cost: " + this.cost
                + " nw["+this.nwLatitudeCoord +", " + this.nwLongitudeCoord+"] sw["
                + this.seLatitudeCoord +", " + this.seLongitudeCoord+"]}";
    }
}
