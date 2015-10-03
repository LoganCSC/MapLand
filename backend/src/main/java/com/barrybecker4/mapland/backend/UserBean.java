package com.barrybecker4.mapland.backend;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents all information that we need about a user.
 * The object model for the data we are sending through endpoints.
 * @author Barry Becker
 */
public class UserBean {


    /** probably the users email address */
    private String userId;
    private long credits = 0;
    private List<Long> locations = new ArrayList<>();


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getCredits() {
        return credits;
    }

    public void setCredits(long credits) {
        this.credits = credits;
    }

    public List<Long> getLocations() {
        return locations;
    }

    public void setLocations(List<Long> locations) {
        this.locations = locations;
    }

}