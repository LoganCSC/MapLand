package com.barrybecker4.mapland.backend.datamodel;

import com.google.api.services.datastore.DatastoreV1;
import com.google.api.services.datastore.client.DatastoreHelper;
import com.google.api.services.datastore.DatastoreV1.Entity;
//import com.google.appengine.api.datastore.GeoPt;

import java.util.Map;

/**
 * Used to transfer information about a specific map region
 * The rectangular region is defined by north-west (nw) and south-east (se) geo coordinates.
 *
 * Regions are square grid cells on a map where the coordinates of the corner vertices
 * are simply the lat/long degree coordinates rounded to 3 decimal places.
 * Since 1 degree of latitude ~= 69 miles,
 * this means the edge length of a grid cell is about 364 feet in the horizontal direction.
 * Since 1 degree of longitude ~= 54.6 miles,
 * this means the edge length of a grid cell is about 288 feet in the vertical direction.
 *
 * @author Barry Becker
 */
public class RegionBean {

    private Long regionId;

    /** Id of the user who owns this region */
    private String ownerId;
    /* switch to using GeoPt when its ready for prime time
    private GeoPt northWestCorner;
    private GeoPt southEastCorner; */


    private double nwLatitudeCoord;
    private double nwLongitudeCoord;
    private double seLatitudeCoord;
    private double seLongitudeCoord;

    public RegionBean() {}

    public RegionBean(Entity regionEntity, Long id) {
        this(regionEntity);
        this.setRegionId(id);
        System.out.println("created Region: " + this);
    }

    public RegionBean(Entity regionEntity) {

        Map<String, DatastoreV1.Value> propertyMap = DatastoreHelper.getPropertyMap(regionEntity);
        //System.out.println("region propertyMap = " + propertyMap);

        DatastoreV1.Value idVal = propertyMap.get("regionId");
        Long id = regionEntity.getKey().getPathElement(0).getId();
        //Long regionId = id; //idVal == null ?  null : idVal.getIntegerValue();
        String ownerId = propertyMap.get("ownerId").getStringValue();
        Long cost = propertyMap.get("cost").getIntegerValue();
        Integer income = (int) propertyMap.get("income").getIntegerValue();
        Double nwLat = propertyMap.get("nwLatitude").getDoubleValue();
        Double nwLong = propertyMap.get("nwLongitude").getDoubleValue();
        Double seLat = propertyMap.get("seLatitude").getDoubleValue();
        Double seLong = propertyMap.get("seLongitude").getDoubleValue();
        /*
        List<Long> regions = new ArrayList<>();
        for (Value value : propertyMap.get("regions").getListValueList()) {
            System.out.println(value.getIntegerValue());
            regions.add(value.getIntegerValue());
        }*/

        this.setRegionId(id);
        this.setOwnerId(ownerId);
        this.setCost(cost);
        this.setIncome(income);
        this.setNwLatitudeCoord(nwLat);
        this.setNwLongitudeCoord(nwLong);
        this.setSeLatitudeCoord(seLat);
        this.setSeLongitudeCoord(seLong);
        /** GeoPt not yet ready for prime time unfortunately
         region.setNorthWestCorner(new GeoPt(nwLat.floatValue(), nwLong.floatValue()));
         region.setSouthEastCorner(new GeoPt(seLat.floatValue(), seLong.floatValue()));
         */
    }


    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long id) {
        this.regionId = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /** the number of credits needed to buy this region */
    private long cost;

    /** the amount of daily income that this property provides the owner */
    private int income;

    /** additional info about the region. Probably added by ownwer(s) */
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
        return "{regionId: " + this.regionId + " owner: " + this.ownerId + " cost: " + this.cost
                + " nw["+this.nwLatitudeCoord +", " + this.nwLongitudeCoord+"] sw["
                + this.seLatitudeCoord +", " + this.seLongitudeCoord+"] notes: "+ notes + "}";
    }
}
