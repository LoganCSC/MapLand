package com.barrybecker4.mapland.backend.datamodel;

/**
 * Combine location and owner together.
 * Endpoints are only allowed to pass maximum of one entity.
 */
public class LocationAndUserBean {

    private LocationBean location;
    private UserBean user;

    public LocationBean getLocation() {
        return location;
    }

    public void setLocation(LocationBean location) {
        this.location = location;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }
}
