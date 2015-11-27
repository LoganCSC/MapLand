/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.barrybecker4.mapland.backend;

import com.barrybecker4.mapland.backend.datamodel.RegionAndUserBean;
import com.barrybecker4.mapland.backend.datamodel.RegionBean;
import com.barrybecker4.mapland.backend.datamodel.UserBean;
import com.barrybecker4.mapland.backend.datastore.RegionAccess;
import com.barrybecker4.mapland.backend.datastore.UserAccess;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.services.datastore.client.DatastoreException;
import com.google.appengine.api.datastore.DatastoreServiceConfig;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

/**
 * Define the mapLand API endpoints
 */
@Api(
    name = "mapLandApi",
    version = "v1",
    namespace = @ApiNamespace(
            ownerDomain = "backend.mapland.barrybecker4.com",
            ownerName = "backend.mapland.barrybecker4.com",
            packagePath = ""
    )
)

/**
 * When deployed to google appengine view here:
 *  https://console.developers.google.com/project/maplandbackend
 */
public class MapLandEndpoint {
    private static final Logger LOG = Logger.getLogger(MapLandEndpoint.class.getName());

    static {
        System.setProperty(DatastoreServiceConfig.DATASTORE_EMPTY_LIST_SUPPORT, Boolean.TRUE.toString());
    }

    /**
     * endpoint method that takes a userId and returns persisted information about that user.
     */
    @ApiMethod(name = "getUserInfo")
    public UserBean getUserInfo(@Named("userId") String userId) {

        UserAccess access = new UserAccess();
        UserBean user = access.getUserById(userId);
        LOG.info("ENDPOINT: returning user = " + user);
        return user;
    }

    /**
     * endpoint method that takes a userId and returns persisted information about that user.
     */
    @ApiMethod(name = "updateUserInfo")
    public UserBean updateUserInfo(UserBean user) throws DatastoreException {

        UserAccess access = new UserAccess();
        access.updateUser(user);
        System.out.println("updating user = " + user);
        return user;
    }

    /**
     * endpoint method that takes a regionId and returns persisted information about that region.
     */
    @ApiMethod(name = "getRegionsInViewPort")
    public List<RegionBean> getRegionsInViewPort(
            @Named("nwLat") Double nwLat, @Named("nwLong") Double nwLong,
            @Named("seLat") Double seLat, @Named("seLong") Double seLong) {

        RegionAccess access = new RegionAccess();
        return access.getAllRegionsInViewPort(nwLat, nwLong, seLat, seLong);
    }

    /**
     * endpoint method that takes a regionId and returns persisted information about that region.
     */
    @ApiMethod(name = "getRegionInfo")
    public RegionBean getRegionInfo(@Named("regionId") Long regionId) {
        RegionAccess access = new RegionAccess();
        return access.getRegionById(regionId);
    }

    /**
     * Endpoint method that adds a new region with specified information and owner.
     * The owner must also be updated with this new region as part of an atomic transaction.
     */
    @ApiMethod(name = "addRegionInfo")
    public RegionBean addRegionInfo(@Named("owner") String owner,
            @Named("nwLat") Double nwLat, @Named("nwLong") Double nwLong,
            @Named("seLat") Double seLat, @Named("seLong") Double seLong) throws DatastoreException {

        RegionAccess access = new RegionAccess();
        return access.addNewRegion(owner, nwLat, nwLong, seLat, seLong);
    }

    /**
     * Endpoint method that adds a new region with specified information and owner.
     * The owner must also be updatd with this new region as part of an atomic transaction.
     */
    @ApiMethod(name = "transferRegionOwnership")
    public RegionAndUserBean transferRegionOwnership(
            RegionAndUserBean regionAndUser) throws DatastoreException {
        RegionAccess access = new RegionAccess();
        return access.transferRegionOwnership(regionAndUser);
    }
}
