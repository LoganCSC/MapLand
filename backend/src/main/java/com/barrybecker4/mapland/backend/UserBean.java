package com.barrybecker4.mapland.backend;

/**
 * Represents all information that we need about a user.
 * The object model for the data we are sending through endpoints.
 * @author Barry Becker
 */
public class UserBean {


    /** probably the users email address */
    private String userId;
    private long credits;
    private long[] locations;


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

    public long[] getLocations() {
        return locations;
    }

    public void setLocations(long[] locations) {
        this.locations = locations;
    }

}