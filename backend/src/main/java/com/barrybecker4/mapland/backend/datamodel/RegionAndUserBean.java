package com.barrybecker4.mapland.backend.datamodel;

/**
 * Combine region and owner together.
 * Endpoints are only allowed to pass maximum of one entity.
 */
public class RegionAndUserBean {

    private RegionBean region;
    private UserBean user;

    public RegionBean getRegion() {
        return region;
    }

    public void setRegion(RegionBean region) {
        this.region = region;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }
}
