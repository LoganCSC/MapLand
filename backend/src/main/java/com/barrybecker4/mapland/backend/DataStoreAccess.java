package com.barrybecker4.mapland.backend;


import com.google.api.services.datastore.DatastoreV1.BeginTransactionResponse;
import com.google.api.services.datastore.DatastoreV1.BeginTransactionRequest;
import com.google.api.services.datastore.DatastoreV1.Entity;
import com.google.api.services.datastore.DatastoreV1.LookupResponse;
import com.google.api.services.datastore.DatastoreV1.LookupRequest;
import com.google.api.services.datastore.DatastoreV1.Key;
import com.google.api.services.datastore.DatastoreV1.Value;
import com.google.api.services.datastore.DatastoreV1.Property;
import com.google.api.services.datastore.DatastoreV1.CommitRequest;
import com.google.api.services.datastore.client.Datastore;
import com.google.api.services.datastore.client.DatastoreException;

import com.google.api.services.datastore.client.DatastoreHelper;
import com.google.protobuf.ByteString;


import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Based on the introductory code at https://cloud.google.com/datastore/docs/getstarted/start_java/
 */
public class DataStoreAccess {

    private Datastore datastore = new DataStorage().getInstance();

    /**
     * Get the specified user if they are in the database.
     * If they are not in the database, add a record for them.
     * @param userId user id
     */
    public UserBean getUserById(String userId) {
        UserBean user = new UserBean();

        try {
            Entity entity = getUserEntity("User", userId);

            // Get `name` property value.
            //String name = entity.getProperty(0).getValue().getStringValue();
            // Get `credits` property value.
            //Long credits = entity.getProperty(0).getValue().getIntegerValue();
            //Value locsValue = entity.getProperty(1).getValue();

            Map<String, Value> propertyMap = DatastoreHelper.getPropertyMap(entity);
            System.out.println("propertyMap = "+ propertyMap);

            Long credits = propertyMap.get("credits").getIntegerValue();
            List<Long> locations = new ArrayList<>();
            for (Value value : propertyMap.get("locations").getListValueList()) {
                System.out.println(value.getIntegerValue());
                locations.add(value.getIntegerValue());
            }

            /*
            int numLocations = locsValue.getListValueCount();
            List<Long> locations = new ArrayList<>(numLocations);
            for (int i = 0; i < numLocations; i++) {
               locations.add(locsValue.getListValue(i).getIntegerValue());
            }*/

            System.out.println("Username = " + userId);
            System.out.println("Credits = " + credits);
            System.out.println("Locations = " + locations);

            user.setUserId(userId);
            user.setCredits(credits);
            user.setLocations(locations);

        } catch (DatastoreException exception) {
            // Catch all Datastore rpc errors.
            System.err.println("Error while doing datastore operation");
            // Log the exception, the name of the method called and the error code.
            System.err.println(String.format("DatastoreException(%s): %s %s",
                    exception.getMessage(),
                    exception.getMethodName(),
                    exception.getCode()));
            System.exit(1);
        }


        /*
        if (userInfoMap.containsKey(userId)) {
            user = userInfoMap.get(userId);
        }
        else if (userId.equals(GUEST)) {
            user = GUEST_INFO;
        }
        else {
            // Someone new. Create some random info for them
            long randomCredits = (long) (RAND.nextInt(100) * RAND.nextInt(100) + RAND.nextInt(100));
            user.setCredits(randomCredits);
            int numLocations = RAND.nextInt(10);
            List<Long> locations = new ArrayList<>(numLocations);
            for (int i = 0; i < numLocations; i++) {
                locations.add(RAND.nextLong());
            }

            user.setLocations(new ArrayList<>(locations));
            userInfoMap.put(userId, user);
        }*/
        return user;
    }

    /** get the user entity, and if its not there add one */
    private Entity getUserEntity(String kind, String name) throws DatastoreException {

        // Create an RPC request to begin a new transaction.
        BeginTransactionRequest.Builder treq = BeginTransactionRequest.newBuilder();
        // Execute the RPC synchronously.
        BeginTransactionResponse tres = datastore.beginTransaction(treq.build());
        // Get the transaction handle from the response.
        ByteString tx = tres.getTransaction();

        // Create an RPC request to get entities by key.
        LookupRequest.Builder lreq = LookupRequest.newBuilder();
        // Set the entity key with only one `path_element`: no parent.
        Key.Builder key = Key.newBuilder().addPathElement(
                Key.PathElement.newBuilder().setKind(kind).setName(name));
        lreq.addKey(key); // Add one key to the lookup request.

        // Set the transaction, so we get a consistent snapshot of the entity at the time the transaction started.
        lreq.getReadOptionsBuilder().setTransaction(tx);
        // Execute the RPC and get the response.
        LookupResponse lresp = datastore.lookup(lreq.build());

        // Create an RPC request to commit the transaction.
        CommitRequest.Builder creq = CommitRequest.newBuilder();
        // Set the transaction to commit.
        creq.setTransaction(tx);

        Entity entity;
        if (lresp.getFoundCount() > 0) {
            System.out.println("Found a user entity");
            entity = lresp.getFound(0).getEntity();
        } else {
            System.out.println("No user entity found. Adding one.");
            // If no entity was found, create a new one.
            Long credits = (long) (100 * Math.random());
            List<Long> locations = Arrays.asList(34L, 45L, 67L);
            entity = createUserEntity(key, name, credits, locations);
            // Insert the entity in the commit request mutation.
            creq.getMutationBuilder().addInsert(entity);
        }

        // Execute the Commit RPC synchronously and ignore the response.
        // Apply the insert mutation if the entity was not found and close
        // the transaction.
        datastore.commit(creq.build());
        return entity;
    }


    private Entity createUserEntity(Key.Builder key, String userId, Long credits, List<Long> locations) {
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

        // - a list of 64bit integers: `locations`
        // See http://stackoverflow.com/questions/23858208/how-to-add-array-property-value-in-google-cloud-datastore

        Value.Builder[] valueArray = new Value.Builder[locations.size()];
        int i = 0;
        for (Long loc : locations) {
            valueArray[i++] = DatastoreHelper.makeValue(loc);
        }
        entityBuilder.addProperty(
                DatastoreHelper.makeProperty("locations", DatastoreHelper.makeValue(valueArray)));

        // Build the entity.
        entity = entityBuilder.build();
        return entity;
    }

    /** get the question entity, and if its not there add one *
    private Entity getEntity(String kind, String name) throws DatastoreException {

        // Create an RPC request to begin a new transaction.
        BeginTransactionRequest.Builder treq = BeginTransactionRequest.newBuilder();
        // Execute the RPC synchronously.
        BeginTransactionResponse tres = datastore.beginTransaction(treq.build());
        // Get the transaction handle from the response.
        ByteString tx = tres.getTransaction();

        // Create an RPC request to get entities by key.
        LookupRequest.Builder lreq = LookupRequest.newBuilder();
        // Set the entity key with only one `path_element`: no parent.
        Key.Builder key = Key.newBuilder().addPathElement(
                Key.PathElement.newBuilder().setKind(kind).setName(name));
        lreq.addKey(key); // Add one key to the lookup request.

        // Set the transaction, so we get a consistent snapshot of the entity at the time the transaction started.
        lreq.getReadOptionsBuilder().setTransaction(tx);
        // Execute the RPC and get the response.
        LookupResponse lresp = datastore.lookup(lreq.build());

        // Create an RPC request to commit the transaction.
        CommitRequest.Builder creq = CommitRequest.newBuilder();
        // Set the transaction to commit.
        creq.setTransaction(tx);

        Entity entity;
        if (lresp.getFoundCount() > 0) {
            System.out.println("Found an entity");
            entity = lresp.getFound(0).getEntity();
        } else {
            System.out.println("No entity found. Adding one.");
            // If no entity was found, create a new one.
            entity = createQuestionEntity(key, "What is the meaning of Life?", 42);
            // Insert the entity in the commit request mutation.
            creq.getMutationBuilder().addInsert(entity);
        }
        // Execute the Commit RPC synchronously and ignore the response.
        // Apply the insert mutation if the entity was not found and close
        // the transaction.
        datastore.commit(creq.build());
        return entity;
    }*/


    /*
    private Entity createQuestionEntity(Key.Builder key, String question, Integer answer) {
        Entity entity;
        Entity.Builder entityBuilder = Entity.newBuilder();
        // Set the entity key.
        entityBuilder.setKey(key);
        // Add two entity properties:
        // - a utf-8 string: `question`
        entityBuilder.addProperty(Property.newBuilder()
                .setName("question")
                .setValue(Value.newBuilder()
                        .setStringValue(question)));
        // - a 64bit integer: `answer`
        entityBuilder.addProperty(Property.newBuilder()
                .setName("answer")
                .setValue(Value.newBuilder().setIntegerValue(answer)));
        // Build the entity.
        entity = entityBuilder.build();
        return entity;
    }*/
}
