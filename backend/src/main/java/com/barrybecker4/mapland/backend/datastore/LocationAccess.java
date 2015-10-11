package com.barrybecker4.mapland.backend.datastore;

import com.google.api.services.datastore.DatastoreV1.Entity;
import com.google.api.services.datastore.DatastoreV1.Key;
import com.google.api.services.datastore.DatastoreV1.Property;
import com.google.api.services.datastore.DatastoreV1.Value;
import com.google.api.services.datastore.client.DatastoreException;
import com.google.api.services.datastore.client.DatastoreHelper;
import com.barrybecker4.mapland.backend.datamodel.LocationBean;
import com.google.appengine.api.datastore.GeoPt;
//import com.google.appengine.api.datastore.Entity;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.List;
import java.util.Map;

/**
 * Based on the introductory code at https://cloud.google.com/datastore/docs/getstarted/start_java/
 * TODO: add a method that gets location for a specified lat/long
 */
public class LocationAccess extends DataStoreAccess {

    /**
     * Get the specified location if it is in the database.
     * If they are not in the database, throw an error.
     * @param locationId user id
     */
    public LocationBean getLocationById(Long locationId) {
        LocationBean location = new LocationBean();

        try {
            Entity entity = getEntity("Location", locationId);

            Map<String, Value> propertyMap = DatastoreHelper.getPropertyMap(entity);
            System.out.println("location propertyMap = "+ propertyMap);

            String ownerId = propertyMap.get("ownerId").getStringValue();
            Long cost = propertyMap.get("cost").getIntegerValue();
            Integer income = (int) propertyMap.get("income").getIntegerValue();
            Double nwLat = propertyMap.get("nwLatitude").getDoubleValue();
            Double nwLong = propertyMap.get("nwLongitude").getDoubleValue();
            Double seLat = propertyMap.get("seLatitude").getDoubleValue();
            Double seLong = propertyMap.get("seLongitude").getDoubleValue();
            /*
            List<Long> locations = new ArrayList<>();
            for (Value value : propertyMap.get("locations").getListValueList()) {
                System.out.println(value.getIntegerValue());
                locations.add(value.getIntegerValue());
            }*/

            System.out.println("LocationId = " + locationId);
            System.out.println("OwnerId = " + ownerId);
            System.out.println("Cost = " + cost);
            System.out.println("Income = " + income);
            System.out.println("nwLat = " + nwLat);
            System.out.println("nwLong = " + nwLong);
            System.out.println("seLat = " + seLat);
            System.out.println("seLong = " + seLong);

            location.setId(locationId);
            location.setOwnerId(ownerId);
            location.setCost(cost);
            location.setIncome(income);
            location.setNwLatitudeCoord(nwLat);
            location.setNwLongitudeCoord(nwLong);
            location.setSeLatitudeCoord(seLat);
            location.setSeLongitudeCoord(seLong);
            /** GeoPt not yet ready for prime time unfortunately
            location.setNorthWestCorner(new GeoPt(nwLat.floatValue(), nwLong.floatValue()));
            location.setSouthEastCorner(new GeoPt(seLat.floatValue(), seLong.floatValue()));
             */

        } catch (DatastoreException exception) {
            // Catch all Datastore rpc errors.
            System.err.println("Error while doing location datastore operation");
            // Log the exception, the name of the method called and the error code.
            System.err.println(String.format("DatastoreException(%s): %s %s",
                    exception.getMessage(),
                    exception.getMethodName(),
                    exception.getCode()));
            System.exit(1);
        }

        return location;
    }

    /**
     * Specify upper left and lower right corners of a bounding region.
     * Because of a limitation of GAE datastore, we cannot make inequality queries on more
     * than one property at a time. To workaround this, the query uses only a lattitude
     * filter, then does further filtering on the results.
     * @return a list of all locations within the bounds specified.
     */
    public List<LocationBean> getAllLocationsInRegion(
            Double nwLat, Double nwLong, Double seLat, Double seLong) {

        // @@TODO
        return null;
    }

    /** @return new Location entity with specified info */
    private Entity createLocationEntity(
            Key.Builder key, Long locationId, String ownerId, Long cost, Integer income,
            Double nwLat, Double nwLong, Double seLat, Double seLong) {
        Entity entity;
        Entity.Builder entityBuilder = Entity.newBuilder();
        // Set the entity key.
        entityBuilder.setKey(key);
        // - a 64 bit integer: `location id`
        entityBuilder.addProperty(Property.newBuilder()
                .setName("locationId")
                .setValue(Value.newBuilder().setIntegerValue(locationId)));

        // - a utf-8 string: `user name`
        entityBuilder.addProperty(Property.newBuilder()
                .setName("ownerId")
                .setValue(Value.newBuilder().setStringValue(ownerId)));

        // - a 64bit integer: `cost`
        entityBuilder.addProperty(Property.newBuilder()
                .setName("cost")
                .setValue(Value.newBuilder().setIntegerValue(cost)));

        // - a 64bit integer: `income`
        entityBuilder.addProperty(Property.newBuilder()
                .setName("income")
                .setValue(Value.newBuilder().setIntegerValue(income)));


        // location region bounds
        entityBuilder.addProperty(Property.newBuilder()
                .setName("nwLatitude")
                .setValue(Value.newBuilder().setDoubleValue(nwLat)));
        entityBuilder.addProperty(Property.newBuilder()
                .setName("nwLongitude")
                .setValue(Value.newBuilder().setDoubleValue(nwLong)));
        entityBuilder.addProperty(Property.newBuilder()
                .setName("seLatitude")
                .setValue(Value.newBuilder().setDoubleValue(seLat)));
        entityBuilder.addProperty(Property.newBuilder()
                .setName("seLongitude")
                .setValue(Value.newBuilder().setDoubleValue(seLong)));

        // Build the entity.
        entity = entityBuilder.build();
        return entity;
    }
}
