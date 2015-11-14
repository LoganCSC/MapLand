package com.barrybecker4.mapland.backend.datastore;

import com.barrybecker4.mapland.backend.datamodel.RegionAndUserBean;
import com.barrybecker4.mapland.backend.datamodel.UserBean;
import com.google.api.services.datastore.DatastoreV1;
import com.google.api.services.datastore.DatastoreV1.Entity;
import com.google.api.services.datastore.DatastoreV1.Key;
import com.google.api.services.datastore.DatastoreV1.Property;
import com.google.api.services.datastore.DatastoreV1.Value;
import com.google.api.services.datastore.client.DatastoreException;
import com.barrybecker4.mapland.backend.datamodel.RegionBean;
//import com.google.appengine.api.datastore.GeoPt;
//import com.google.appengine.api.datastore.Entity;

//import com.google.appengine.api.datastore.Query;
import com.google.api.services.datastore.DatastoreV1.Query;
import com.google.protobuf.ByteString;
//import com.google.appengine.api.datastore.Entity;
import static com.google.api.services.datastore.client.DatastoreHelper.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Based on the introductory code at https://cloud.google.com/datastore/docs/getstarted/start_java/
 * TODO: add a method that gets region for a specified lat/long
 */
public class RegionAccess extends DataStoreAccess {

    public static final String KIND = "Region";
    private static final Random RND = new Random();

    private static final double MIN_INITIAL_COST = 100L;
    private static final double MAX_INITIAL_COST = 200L;
    private static final double MIN_INITIAL_INCOME = 0;
    private static final double MAX_INITIAL_INCOME = 2.0;

    private static final Logger LOG = Logger.getLogger(RegionAccess.class.getName());

    /** Used to avoid two different users adding the same region at the same time */
    private static final Set<String> lockedRegions = Collections.synchronizedSet(new HashSet<String>());


    /**
     * Get the specified region if it is in the database.
     * If they are not in the database, throw an error.
     * @param regionId region id
     */
    public RegionBean getRegionById(Long regionId) {
        RegionBean region = null;

        try {
            Entity entity = getEntity(KIND, regionId);
            region = new RegionBean(entity);
        }
        catch (DatastoreException exception) {
            fatalError(exception);
        }

        return region;
    }

    /**
     * Get the specified regions if they are in the database.
     * If they are not in the database, throw an error.
     * @param regionIds region ids
     */
    public List<RegionBean> getRegionsByIds(List<Long> regionIds) {
        List<RegionBean> regions = new LinkedList<>();

        if (regionIds.size() > 0) {
            try {
                List<Entity> entities = getEntities(KIND, regionIds);
                for (Entity e : entities) {
                    regions.add(new RegionBean(e));
                }
            }
            catch (DatastoreException exception) {
                fatalError(exception);
            }
        }

        return regions;
    }

    private void fatalError(DatastoreException exception) {
        // Catch all Datastore rpc errors.
        System.err.println("Error while doing region datastore operation");
        // Log the exception, the name of the method called and the error code.
        System.err.println(String.format("DatastoreException(%s): %s %s",
                exception.getMessage(),
                exception.getMethodName(),
                exception.getCode()));
        System.exit(1);
    }

    /**
     * Add a new region with the specified bounds and owning user.
     * Other info is generated as need.
     * The user also needs to be updated as part of the transaction to add the new region.
     * @return the new region bean or null if it could not be added. Null could be returned if someone else is
     *    adding a region in this particular area at the same time.
     */
    public RegionBean addNewRegion(String owner, Double nwLat, Double nwLong, Double seLat, Double seLong)
            throws DatastoreException {

        String hashKey =  "" + nwLat + nwLong;
        if (lockedRegions.contains(hashKey)) {
            // Wait until its not locked, then retrieve that new region and return it.
            while (lockedRegions.contains(hashKey)) {
                sleep(500);
            }
            return getExistingRegionAtPosition(nwLat, nwLong, seLat, seLong);
        }
        else {
            lockedRegions.add(hashKey);
        }

        // The ID will be generated by the datastore
        Key.Builder key = Key.newBuilder().addPathElement(
                Key.PathElement.newBuilder().setKind(KIND)); //.setId(id));

        // Random cost and income assigned
        Double cost = MIN_INITIAL_COST
                + RND.nextInt((int)(MAX_INITIAL_COST - MIN_INITIAL_COST));
        Double income = MIN_INITIAL_INCOME
                + RND.nextDouble() * RND.nextDouble() * (MAX_INITIAL_INCOME - MIN_INITIAL_INCOME);

        Entity regionEntity = createRegionEntity(key, owner, cost, income,
                nwLat, nwLong, seLat, seLong);

        Long newId = addRegionWithOwner(regionEntity, owner);

        lockedRegions.remove(hashKey);
        return new RegionBean(regionEntity, newId);
    }

    /**
     * @return the region at the specified position. Fails if none is found there.
     */
    private RegionBean getExistingRegionAtPosition(Double nwLat, Double nwLong, Double seLat, Double seLong) {

        List<RegionBean> regions = getAllRegionsInViewPort(nwLat, nwLong, seLat, seLong);
        // there should be exactly one
        if (regions.size() != 1) {
            throw new IllegalStateException(
                    "There should have been exactly one region at position " + nwLat + " " + nwLong);
        }
        return regions.get(0);
    }

    /**
     * Transferring ownership of a region involves 3 things:
     * 1) The user needs to have this region added to her list of regions.
     * 2) The region needs to have its owner property set to user.
     * 3) The old owner needs to have this region removed from her list.
     * All these things need to happen as part of a single atomic transaction, and right now they are not.
     * In order to work as a single transaction, we may need to change the datamodel to have
     * users parents of regions. In that case, changing ownership will probably involve
     * deleting the region owned by the previous owner before recreating it for the new owner.
     * @param regionAndUser contains the region to update, and the new owner
     * @return the updated region (with new owner) and user (with new region).
     *   The old owner is updated too, but not returned.
     */
    public RegionAndUserBean transferRegionOwnership(
            RegionAndUserBean regionAndUser) throws DatastoreException {

        UserAccess userAccess = new UserAccess();
        RegionBean region = regionAndUser.getRegion();
        UserBean oldOwner = userAccess.getUserById(region.getOwnerId());
        UserBean newOwner = regionAndUser.getUser();
        long time = System.currentTimeMillis();
        LOG.warning("TRANSFER: oldOwner:" + oldOwner.getUserId() + "newOwner:" + newOwner.getUserId()
                + " region:"+ region.getRegionId());

        if (newOwner.getRegions().contains(region.getRegionId())) {
            LOG.severe(newOwner.getUserId() + " already owns region " + region.getRegionId());
            //throw new IllegalStateException(newOwner.getUserId() + " already owns region " + region.getRegionId());
        }
        else {
            newOwner.getRegions().add(region.getRegionId());
        }
        region.setOwnerId(newOwner.getUserId());
        boolean removed = oldOwner.getRegions().remove(region.getRegionId());
        LOG.warning("TRANSFER: " + oldOwner + " after removing " + region.getRegionId());
        if (!removed) {
            String msg = "Was not able to remove region " + region.getRegionId() + " from " + oldOwner.getUserId()
                    + "with these regions: "+ oldOwner.getRegions();
            LOG.severe(msg);
            //throw new IllegalStateException(msg);
        }

        // this should happen as part of a single transaction (but its not right now)
        userAccess.updateUser(oldOwner);
        userAccess.updateUser(newOwner);
        this.updateRegion(region);

        long duration = System.currentTimeMillis() - time;
        String msg = "time to transfer ownership = " + duration + "ms.";
        System.out.println(msg);
        LOG.warning(msg);

        return regionAndUser;
    }

    /**
     * @param region region to update
     * @return true if successfully updated
     * @throws DatastoreException
     */
    public boolean updateRegion(RegionBean region) throws DatastoreException {

        // Set the transaction, so we get a consistent snapshot of the entity at the time the txn started.
        ByteString tx = createTransaction();

        // Create an RPC request to commit the transaction.
        DatastoreV1.CommitRequest.Builder creq = DatastoreV1.CommitRequest.newBuilder();
        // Set the transaction to commit.
        creq.setTransaction(tx);

        Entity entity = createRegionEntity(region);
        // Insert the entity in the commit request mutation.
        creq.getMutationBuilder().addUpdate(entity);

        // Execute the Commit RPC synchronously and ignore the response.
        // Apply the insert mutation if the entity was not found and close the transaction.
        datastore.commit(creq.build());
        return true;
    }

    /**
     * These steps should all be part of one atomic transaction. The way it is now, there is
     * a microscopic chance that some other user could try to take ownership of the new region
     * before it gets added to the owner passed to this method.
     * In order to do that, we may have to switch the datamodel so that users are parents of regions.
     * One thing to note with that approach is that you cannot just change the ownership of a region.
     * It would need to be cloned, deleted, then added to the new user owner.
     * @param regionEntity new region to add
     * @param owner user that will initially own this new region.
     * @throws DatastoreException if error accessing the datastore
     */
    private Long addRegionWithOwner(Entity regionEntity, String owner) throws DatastoreException {
        UserAccess userAcces = new UserAccess();
        UserBean ownerBean = userAcces.getUserById(owner);

        Long newId = insertEntity(regionEntity);
        List<Long> regionList = ownerBean.getRegions();
        // should not be needed if DATASTORE_EMPTY_LIST_SUPPORT would work
        if (regionList == null) {
            regionList = new LinkedList<>();
        }

        regionList.add(newId);
        ownerBean.setRegions(regionList);

        boolean success = userAcces.updateUser(ownerBean);
        if (success) {
            LOG.info(owner + " just had region " + newId + " added.");
            System.out.println();
        }
        else {
            LOG.severe("Failed to add " + newId + " to " + owner);
        }
        return newId;
    }

    /**
     * Specify upper left and lower right corners of a bounding region.
     * Because of a limitation of GAE datastore, we cannot make inequality queries on more
     * than one property at a time. To workaround this, the query uses only a latitude
     * filter, then does further filtering on the results.
     * latitudes are like a y value where the origin is at the equator. Higher values are further north.
     * logitudes are like x values where the origin passes through Greenwich England. CA is in a negative longitude
     * When GeoPt is supported bette, we can switch to using that.
     * @return a list of all regions within the bounds specified.
     */
    public List<RegionBean> getAllRegionsInViewPort(
            Double nwLat, Double nwLong, Double seLat, Double seLong) {

        // first query by latitude
        Query.Builder query = Query.newBuilder();
        double regionHeight = nwLat - seLat;

        // One degree of latitude = about 69 miles, so 0.01 degrees is less than a mile.
        DatastoreV1.Filter nwLatFilter =
                makeFilter("nwLatitude",
                        DatastoreV1.PropertyFilter.Operator.LESS_THAN, makeValue(nwLat + regionHeight))
                .build();
        // I really want this to be seLattitude, but cannot query on more than one attribute
        // Switch to using GeoPt when feasable.
        DatastoreV1.Filter seLatFilter =
                makeFilter("nwLatitude",
                        DatastoreV1.PropertyFilter.Operator.GREATER_THAN_OR_EQUAL, makeValue(seLat))
                .build();

        query.setFilter(makeFilter(nwLatFilter, seLatFilter));
        query.addKindBuilder().setName(KIND);

        DatastoreV1.RunQueryRequest request = DatastoreV1.RunQueryRequest.newBuilder().setQuery(query).build();
        DatastoreV1.RunQueryResponse response;
        List<RegionBean> list = new LinkedList<>();

        try {
            response = datastore.runQuery(request);

            List<DatastoreV1.EntityResult> results =
                    response.getBatch().getEntityResultList();
            System.out.println("There were " + results.size()
                    + " regions retrieved for viewport: ["+ nwLat +", " + nwLong +"] [" + seLat + ", " + seLong + "]");

            for (DatastoreV1.EntityResult result : results) {
                Entity regionEntity = result.getEntity();
                RegionBean regionBean = new RegionBean(regionEntity);
                // only add it to the list if it also meets the longitude filter.
                // This must be done manually be because of bigTable query limitations.
                if (regionBean.getSeLongitudeCoord() >= nwLong &&
                        regionBean.getNwLongitudeCoord() <= seLong) {
                    list.add(regionBean);
                }
            }
        }
        catch (  DatastoreException e) {
            e.printStackTrace();
        }
        LOG.info("returning " + list.size() + " regions in viewport");
        return list;
    }

    /**
     * Specify upper left and lower right corners of a bounding region.
     * Because of a limitation of GAE datastore, we cannot make inequality queries on more
     * than one property at a time. To workaround this, the query uses only a latitude
     * filter, then does further filtering on the results.
     * When GeoPt is supported bette, we can switch to using that.
     * @return a list of all regions within the bounds specified.
     *
    public List<RegionBean> getAllRegionsInViewPort(
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
        Query query = new Query("Region").setFilter(latRangeFilter);

        // Use PreparedQuery interface to retrieve results
        DatastoreService datastoreSvc = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery pq = datastoreSvc.prepare(query);

        List<RegionBean> list = new ArrayList<>();

        for (Entity result : pq.asIterable()) { //pq.asIterable()) {
            RegionBean loc = new RegionBean(result);
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

    private Entity createRegionEntity(RegionBean region) {

        // Set the entity key with only one `path_element`: no parent.
        Key.Builder key = Key.newBuilder().addPathElement(
                Key.PathElement.newBuilder().setKind(KIND).setId(region.getRegionId()));

        return createRegionEntity(key, region.getOwnerId(), region.getCost(), region.getIncome(),
                region.getNwLatitudeCoord(), region.getNwLongitudeCoord(),
                region.getSeLatitudeCoord(), region.getSeLongitudeCoord());
    }

    /** @return new Region entity with specified info */
    private Entity createRegionEntity(
            Key.Builder key, String ownerId, double cost, double income,
            Double nwLat, Double nwLong, Double seLat, Double seLong) {
        Entity entity;
        Entity.Builder entityBuilder = Entity.newBuilder();
        // Set the entity key.
        entityBuilder.setKey(key);
        // - a 64 bit integer: `region id`
        entityBuilder.addProperty(Property.newBuilder()
                .setName("regionId")
                .setValue(Value.newBuilder())); //.setIntegerValue(regionId)));

        // - a utf-8 string: `user name`
        entityBuilder.addProperty(Property.newBuilder()
                .setName("ownerId")
                .setValue(Value.newBuilder().setStringValue(ownerId)));

        // - a 64bit integer: `cost`
        entityBuilder.addProperty(Property.newBuilder()
                .setName("cost")
                .setValue(Value.newBuilder().setDoubleValue(cost)));

        // - a 64bit integer: `income`
        entityBuilder.addProperty(Property.newBuilder()
                .setName("income")
                .setValue(Value.newBuilder().setDoubleValue(income)));


        // region region bounds
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
