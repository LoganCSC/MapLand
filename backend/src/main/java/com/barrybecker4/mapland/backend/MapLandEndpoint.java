/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.barrybecker4.mapland.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.util.HashSet;
import java.util.Set;

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

    private Set<String> previousUsers = new HashSet<>();
    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "sayHi")
    public MyBean sayHi(@Named("name") String name) {
        MyBean response = new MyBean();

        if (previousUsers.contains(name)) {
            response.setData("Welcome back, " + name);
        }
        else if (name.equals("guest")) {
            response.setData("No one logged in, guest used.");
        }
        else {
            response.setData("Hello " + name);
            previousUsers.add(name);
        }

        return response;
    }

}
