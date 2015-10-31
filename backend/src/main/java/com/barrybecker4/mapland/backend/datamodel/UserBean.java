package com.barrybecker4.mapland.backend.datamodel;

import com.google.api.services.datastore.DatastoreV1;
import com.google.api.services.datastore.client.DatastoreHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Represents all information that we need about a user.
 * The object model for the data we are sending through endpoints.
 * @author Barry Becker
 */
public class UserBean {

    /** probably the users email address */
    private String userId;
    private long credits = 0;
    private List<Long> regions = new ArrayList<>();

    public UserBean() {}

    public UserBean(DatastoreV1.Entity userEntity) {

        // Get `name` property value.
        //String name = entity.getProperty(0).getValue().getStringValue();
        // Get `credits` property value.
        //Long credits = entity.getProperty(0).getValue().getIntegerValue();
        //Value locsValue = entity.getProperty(1).getValue();

        Map<String, DatastoreV1.Value> propertyMap = DatastoreHelper.getPropertyMap(userEntity);
        //System.out.println("user propertyMap = "+ propertyMap);

        String username = propertyMap.get("name").getStringValue();
        Long credits = propertyMap.get("credits").getIntegerValue();
        List<Long> regions = new ArrayList<>();
        DatastoreV1.Value regionList = propertyMap.get("regions");
        if (regionList != null) {
            for (DatastoreV1.Value value : regionList.getListValueList()) {
                System.out.println("region: " + value.getIntegerValue());
                regions.add(value.getIntegerValue());
            }
        }
        /*
        int numRegions = locsValue.getListValueCount();
        List<Long> regions = new ArrayList<>(numRegions);
        for (int i = 0; i < numRegions; i++) {
           regions.add(locsValue.getListValue(i).getIntegerValue());
        }*/

        this.setUserId(username);
        this.setCredits(credits);
        this.setRegions(regions);
        System.out.println("created User:"  + this);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return this.userId;
    }
    public long getCredits() {
        return credits;
    }

    public void setCredits(long credits) {
        this.credits = credits;
    }

    public List<Long> getRegions() {
        // for some reason the datastore makes empty lists null.
        if (regions == null) {
            regions = new LinkedList<>();
        }

        return regions;
    }

    public void setRegions(List<Long> regions) {
        this.regions = regions;
    }

    public String toString() {
        return "{userId: " + this.userId + " credits: " + this.credits + " region: " + this.regions + "}";
    }
}