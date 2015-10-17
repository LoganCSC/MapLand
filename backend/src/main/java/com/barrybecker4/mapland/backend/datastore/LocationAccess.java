package com.barrybecker4.mapland.backend.datastore;

import com.google.api.services.datastore.DatastoreV1;
import com.google.api.services.datastore.DatastoreV1.Entity;
import com.google.api.services.datastore.DatastoreV1.Key;
import com.google.api.services.datastore.DatastoreV1.Property;
import com.google.api.services.datastore.DatastoreV1.Value;
import com.google.api.services.datastore.client.DatastoreException;
import com.google.api.services.datastore.client.DatastoreHelper;
import com.barrybecker4.mapland.backend.datamodel.LocationBean;
//import com.google.appengine.api.datastore.GeoPt;
//import com.google.appengine.api.datastore.Entity;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.PreparedQuery;
//import com.google.appengine.api.datastore.Query;
import com.google.api.services.datastore.DatastoreV1.Query;
//import com.google.appengine.api.datastore.Entity;
import com.google.apphosting.datastore.DatastoreV4;
import com.google.protobuf.InvalidProtocolBufferException;
import static com.google.api.services.datastore.client.DatastoreHelper.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Based on the introductory code at https://cloud.google.com/datastore/docs/getstarted/start_java/
 * TODO: add a method that gets location for a specified lat/long
 */
public class LocationAccess extends DataStoreAccess {

    public static final String KIND = "Location";

    /**
     * Get the specified location if it is in the database.
     * If they are not in the database, throw an error.
     * @param locationId user id
     */
    public LocationBean getLocationById(Long locationId) {
        LocationBean location = null;

        try {
            Entity entity = getEntity(KIND, locationId);
            location = new LocationBean(entity);

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
     * than one property at a time. To workaround this, the query uses only a latitude
     * filter, then does further filtering on the results.
     * When GeoPt is supported bette, we can switch to using that.
     * @return a list of all locations within the bounds specified.
     */
    public List<LocationBean> getAllLocationsInRegion(
    Double nwLat, Double nwLong, Double seLat, Double seLong) {

        // first query by latitude
        Query.Builder query = Query.newBuilder();

        DatastoreV1.Filter nwLatFilter =
                makeFilter("nwLatitude", DatastoreV1.PropertyFilter.Operator.GREATER_THAN_OR_EQUAL, makeValue(nwLat))
                .build();
        DatastoreV1.Filter seLatFilter =
                makeFilter("seLatitude", DatastoreV1.PropertyFilter.Operator.LESS_THAN_OR_EQUAL, makeValue(seLat))
                .build();

        query.setFilter(makeFilter(nwLatFilter, seLatFilter));
        query.addKindBuilder().setName(KIND);

        DatastoreV1.RunQueryRequest request = DatastoreV1.RunQueryRequest.newBuilder().setQuery(query).build();
        DatastoreV1.RunQueryResponse response;
        List<LocationBean> list = new LinkedList<>();

        try {
            response = datastore.runQuery(request);

            for (DatastoreV1.EntityResult result : response.getBatch().getEntityResultList()) {
                Entity locationEntity = result.getEntity();
                LocationBean locationBean = new LocationBean(locationEntity);
                System.out.println("retrieved " + locationBean);
                // only add it to the list if it also meets the longitude filter.
                // This must be done manually be because of bigTable query limitations.
                if (locationBean.getNwLongitudeCoord() >= nwLong && locationBean.getSeLongitudeCoord() <= seLong) {
                    list.add(locationBean);
                }
            }
        }
        catch (  DatastoreException e) {
            e.printStackTrace();
        }
        System.out.println("returning " + list.size() + " locations");

        return list;
    }

    /**
     * Specify upper left and lower right corners of a bounding region.
     * Because of a limitation of GAE datastore, we cannot make inequality queries on more
     * than one property at a time. To workaround this, the query uses only a latitude
     * filter, then does further filtering on the results.
     * When GeoPt is supported bette, we can switch to using that.
     * @return a list of all locations within the bounds specified.
     *
    public List<LocationBean> getAllLocationsInRegion(
            Double nwLat, Double nwLong, Double seLat, Double seLong) {

        // first query by latitude
        Query.FilterPredicate nwLatFilter =
                new Query.FilterPredicate("nwLatitude",
                        Query.FilterOperator.GREATER_THAN_OR_EQUAL,
                        nwLat);

        Query.FilterPredicate seLatFilter =
                new Query.FilterPredicate("seLatitude",
                        Query.FilterOperator.LESS_THAN_OR_EQUAL,
                        seLat);

        // Use CompositeFilter to combine multiple filters
        Query.CompositeFilter latRangeFilter =
                Query.CompositeFilterOperator.and(nwLatFilter, seLatFilter);


        // Use class Query to assemble a query
        Query query = new Query("Location").setFilter(latRangeFilter);

        // Use PreparedQuery interface to retrieve results
        DatastoreService datastoreSvc = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery pq = datastoreSvc.prepare(query);

        List<LocationBean> list = new ArrayList<>();

        for (Entity result : pq.asIterable()) { //pq.asIterable()) {
            LocationBean loc = new LocationBean(result);
            //String firstName = (String) result.getProperty("firstName");
            //String lastName = (String) result.getProperty("lastName");
            //Long height = (Long) result.getProperty("height");

            System.out.println(loc);
            list.add(loc);
        }

        // then manually filter by longitude (this is really only going to work as
        // long as there are less than 1000 items in the result
        return list;
    }*/

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
