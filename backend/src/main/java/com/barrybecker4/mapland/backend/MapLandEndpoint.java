/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.barrybecker4.mapland.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
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
public class MapLandEndpoint {


    private Map<String, UserBean> userInfoMap = new HashMap<>();

    private static final UserBean GUEST_INFO = new UserBean();
    static {
        GUEST_INFO.setUserId("guest");
        GUEST_INFO.setCredits(10);
    }

    private static final Random RAND = new Random();

    /**
     * A simple endpoint method that takes a userId and returns persisted information about that user.
     */
    @ApiMethod(name = "getUserInfo")
    public UserBean getUserInfo(@Named("userId") String userId) {
        UserBean response = new UserBean();

        if (userInfoMap.containsKey(userId)) {
            response = userInfoMap.get(userId);
        }
        else if (userId.equals("guest")) {
            response = GUEST_INFO;
        }
        else {
            // Someone new. Create some random info for them
            long randomCredits = (long) (RAND.nextInt(100) * RAND.nextInt(100) + RAND.nextInt(100));
            response.setCredits(randomCredits);
            int numLocations = RAND.nextInt(10);
            List<Long> locations = new ArrayList<>(numLocations);
            for (int i=0; i< numLocations; i++) {
                locations.add(RAND.nextLong());
            }

            response.setLocations(new ArrayList<>(locations));
            userInfoMap.put(userId, response);
        }

        return response;
    }

}
