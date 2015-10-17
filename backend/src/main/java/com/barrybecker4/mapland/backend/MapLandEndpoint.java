/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.barrybecker4.mapland.backend;

import com.barrybecker4.mapland.backend.datamodel.UserBean;
import com.barrybecker4.mapland.backend.datastore.LocationAccess;
import com.barrybecker4.mapland.backend.datastore.UserAccess;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.barrybecker4.mapland.backend.datamodel.LocationBean;
import com.google.api.services.datastore.client.DatastoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Named;

/**
 * Define the endpoint class we are exposing
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

    //private Map<Long, LocationBean> locationInfoMap = new HashMap<>();

    private static final String GUEST = "guest";
    private static final UserBean GUEST_INFO = new UserBean();
    static {
        GUEST_INFO.setUserId(GUEST);
        GUEST_INFO.setCredits(10);
        List<Long> locations = new ArrayList<>();
        locations.add(123L);
        GUEST_INFO.setLocations(locations);
    }
    /*
    private static final LocationBean LOCATION_123_INFO = new LocationBean();
    static {
        LOCATION_123_INFO.setId(123L);
        LOCATION_123_INFO.setOwnerId(GUEST);
        LOCATION_123_INFO.setCost(1234);
        LOCATION_123_INFO.setIncome(3);
        LOCATION_123_INFO.setNwLatitudeCoord(123.45);
        LOCATION_123_INFO.setNwLongitudeCoord(124.45);
        LOCATION_123_INFO.setSeLatitudeCoord(125.45);
        LOCATION_123_INFO.setSeLongitudeCoord(126.45);
    }*/

    private static final Random RAND = new Random();

    /**
     * endpoint method that takes a userId and returns persisted information about that user.
     */
    @ApiMethod(name = "getUserInfo")
    public UserBean getUserInfo(@Named("userId") String userId) {

        UserAccess access = new UserAccess();
        UserBean user = access.getUserById(userId);
        System.out.println("returning user = " + user);
        return user;
    }

    /**
     * endpoint method that takes a locationId and returns persisted information about that location.
     */
    @ApiMethod(name = "getLocationsInViewPort")
    public List<LocationBean> getLocationsInViewPort(
            @Named("nwLat") Double nwLat, @Named("nwLong") Double nwLong,
            @Named("seLat") Double seLat, @Named("seLong") Double seLong) {

        LocationAccess access = new LocationAccess();
        return access.getAllLocationsInViewPort(nwLat, nwLong, seLat, seLong);
    }

    /**
     * endpoint method that takes a locationId and returns persisted information about that location.
     */
    @ApiMethod(name = "getLocationInfo")
    public LocationBean getLocationInfo(@Named("locationId") Long locationId) {
        LocationAccess access = new LocationAccess();
        return access.getLocationById(locationId);
    }

    /**
     * endpoint method that adds a new location with specified information.
     */
    @ApiMethod(name = "addLocationInfo")
    public LocationBean addLocationInfo(@Named("owner") String owner,
            @Named("nwLat") Double nwLat, @Named("nwLong") Double nwLong,
            @Named("seLat") Double seLat, @Named("seLong") Double seLong) throws DatastoreException {

        LocationAccess access = new LocationAccess();
        return access.addNewLocation(owner, nwLat, nwLong, seLat, seLong);
    }
}
