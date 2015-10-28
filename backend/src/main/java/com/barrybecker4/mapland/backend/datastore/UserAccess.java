package com.barrybecker4.mapland.backend.datastore;

import com.google.api.services.datastore.DatastoreV1.BeginTransactionRequest;
import com.google.api.services.datastore.DatastoreV1.BeginTransactionResponse;
import com.google.api.services.datastore.DatastoreV1.CommitRequest;
import com.google.api.services.datastore.DatastoreV1.Entity;
import com.google.api.services.datastore.DatastoreV1.Key;
import com.google.api.services.datastore.DatastoreV1.LookupRequest;
import com.google.api.services.datastore.DatastoreV1.LookupResponse;
import com.google.api.services.datastore.DatastoreV1.Property;
import com.google.api.services.datastore.DatastoreV1.Value;
import com.google.api.services.datastore.client.Datastore;
import com.google.api.services.datastore.client.DatastoreException;
import com.google.api.services.datastore.client.DatastoreHelper;
import com.google.protobuf.ByteString;
import com.barrybecker4.mapland.backend.datamodel.UserBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Based on the introductory code at https://cloud.google.com/datastore/docs/getstarted/start_java/
 */
public class UserAccess extends DataStoreAccess {

    public static final String KIND = "User";
    private static final Long INITAIL_CREDITS = 100L;

    /**
     * Get the specified user if they are in the database.
     * If they are not in the database, add a new record for them.
     * @param userId user id
     */
    public UserBean getUserById(String userId) {
        UserBean user = null;

        try {
            System.out.println("About to get info for " + userId);
            Entity entity = getUserEntity("User", userId);
            user = new UserBean(entity);

        } catch (DatastoreException exception) {
            // Catch all Datastore rpc errors.
            System.err.println("Error while doing user datastore operation");
            // Log the exception, the name of the method called and the error code.
            System.err.println(String.format("DatastoreException(%s): %s %s",
                    exception.getMessage(),
                    exception.getMethodName(),
                    exception.getCode()));
            System.exit(1);
        }

        return user;
    }

    public boolean updateUser(UserBean user) throws DatastoreException {

        // Set the transaction, so we get a consistent snapshot of the entity at the time the txn started.
        ByteString tx = createTransaction();

        // Create an RPC request to commit the transaction.
        CommitRequest.Builder creq = CommitRequest.newBuilder();
        // Set the transaction to commit.
        creq.setTransaction(tx);

        Entity entity = createUserEntity(user);
        // Insert the entity in the commit request mutation.
        creq.getMutationBuilder().addUpdate(entity);

        // Execute the Commit RPC synchronously and ignore the response.
        // Apply the insert mutation if the entity was not found and close
        // the transaction.
        datastore.commit(creq.build());
        return true;
    }

    /** get the user entity, and if its not there create one */
    private Entity getUserEntity(String kind, String name) throws DatastoreException {

        // Create an RPC request to get entities by key.
        LookupRequest.Builder lreq = LookupRequest.newBuilder();
        // Set the entity key with only one `path_element`: no parent.
        Key.Builder key = Key.newBuilder().addPathElement(
                Key.PathElement.newBuilder().setKind(kind).setName(name));
        lreq.addKey(key); // Add one key to the lookup request.
        //key.getPathElement(0).getName()

        // Set the transaction, so we get a consistent snapshot of the entity at the time the txn started.
        ByteString tx = createTransaction();
        lreq.getReadOptionsBuilder().setTransaction(tx);
        // Execute the RPC and get the response.
        LookupResponse lresp = datastore.lookup(lreq.build());

        // Create an RPC request to commit the transaction.
        CommitRequest.Builder creq = CommitRequest.newBuilder();
        // Set the transaction to commit.
        creq.setTransaction(tx);

        Entity entity;
        if (lresp.getFoundCount() > 0) {
            System.out.println("Found a user entity with id = " + name);
            entity = lresp.getFound(0).getEntity();
        } else {
            System.out.println("No user entity found for name = " + name + ". Adding one.");
            // If no entity was found, create a new one.

            List<Long> regions = new LinkedList<>();
            entity = createUserEntity(key, name, INITAIL_CREDITS, regions);
            // Insert the entity in the commit request mutation.
            creq.getMutationBuilder().addInsert(entity);
        }

        // Execute the Commit RPC synchronously and ignore the response.
        // Apply the insert mutation if the entity was not found and close
        // the transaction.
        datastore.commit(creq.build());
        return entity;
    }

    private Entity createUserEntity(UserBean user) {

        // Set the entity key with only one `path_element`: no parent.
        Key.Builder key = Key.newBuilder().addPathElement(
                Key.PathElement.newBuilder().setKind(KIND).setName(user.getUserId()));

        return createUserEntity(key, user.getUserId(), user.getCredits(), user.getRegions());
    }

    /** @return new User entity with specified info */
    private Entity createUserEntity(
            Key.Builder key, String userId, Long credits, List<Long> regions) {
        Entity entity;
        Entity.Builder entityBuilder = Entity.newBuilder();
        // Set the entity key.
        entityBuilder.setKey(key);
        // - a utf-8 string: `user name`
        entityBuilder.addProperty(Property.newBuilder()
                .setName("name")
                .setValue(Value.newBuilder().setStringValue(userId)));

        // - a 64bit integer: `credits`
        entityBuilder.addProperty(Property.newBuilder()
                .setName("credits")
                .setValue(Value.newBuilder().setIntegerValue(credits)));

        // - a list of 64bit integers: `regions`
        // See http://stackoverflow.com/questions/23858208/how-to-add-array-property-value-in-google-cloud-datastore

        Value.Builder[] valueArray = new Value.Builder[regions.size()];
        int i = 0;
        for (Long loc : regions) {
            valueArray[i++] = DatastoreHelper.makeValue(loc);
        }
        entityBuilder.addProperty(
                DatastoreHelper.makeProperty("regions", DatastoreHelper.makeValue(valueArray)));

        // Build the entity.
        entity = entityBuilder.build();
        return entity;
    }
}
