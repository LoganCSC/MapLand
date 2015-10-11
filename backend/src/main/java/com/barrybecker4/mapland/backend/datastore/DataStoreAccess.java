package com.barrybecker4.mapland.backend.datastore;

//import com.google.appengine.api.datastore.Entity;
import com.google.api.services.datastore.DatastoreV1.BeginTransactionResponse;
import com.google.api.services.datastore.DatastoreV1.BeginTransactionRequest;
import com.google.api.services.datastore.DatastoreV1.Entity;
import com.google.api.services.datastore.DatastoreV1.LookupResponse;
import com.google.api.services.datastore.DatastoreV1.LookupRequest;
import com.google.api.services.datastore.DatastoreV1.Key;
import com.google.api.services.datastore.DatastoreV1.CommitRequest;
import com.google.api.services.datastore.client.Datastore;
import com.google.api.services.datastore.client.DatastoreException;

import com.google.api.services.datastore.client.DatastoreHelper;
import com.google.protobuf.ByteString;


/**
 * Based on the introductory code at https://cloud.google.com/datastore/docs/getstarted/start_java/
 */
public class DataStoreAccess {

    protected Datastore datastore = DataStorage.getInstance();


    /** get the user entity, and if its not there create one */
    protected Entity getEntity(String kind, Object name) throws DatastoreException {

        // Create an RPC request to begin a new transaction.
        BeginTransactionRequest.Builder treq = BeginTransactionRequest.newBuilder();
        // Execute the RPC synchronously.
        BeginTransactionResponse tres = datastore.beginTransaction(treq.build());
        // Get the transaction handle from the response.
        ByteString tx = tres.getTransaction();

        // Create an RPC request to get entities by key.
        LookupRequest.Builder lreq = LookupRequest.newBuilder();
        // Set the entity key with only one `path_element`: no parent.

        Key.Builder key = createKey(kind, name);

        lreq.addKey(key); // Add one key to the lookup request.

        // Set the transaction, so we get a consistent snapshot of the entity at the time the txn started.
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
            throw new IllegalStateException("No " + kind + " entity found with name " + name);
            /*
            // If no entity was found, create a new one.
            Long credits = (long) (100 * Math.random());
            List<Long> locations = Arrays.asList(34L, 45L, 67L);
            entity = createUserEntity(key, name, credits, locations);
            // Insert the entity in the commit request mutation.
            creq.getMutationBuilder().addInsert(entity);*/
        }

        // Execute the Commit RPC synchronously and ignore the response.
        // Apply the insert mutation if the entity was not found and close
        // the transaction.
        datastore.commit(creq.build());  // need?
        return entity;
    }


    private Key.Builder createKey(String kind, Object name) {
        Key.PathElement.Builder pathBuilder = Key.PathElement.newBuilder().setKind(kind);
        if (name instanceof Long) {
            pathBuilder.setId((Long)name);
        }
        else if (name instanceof String) {
            pathBuilder.setName((String)name);
        }
        else {
            throw new IllegalStateException("Unexpected type for name " + name);
        }
        return Key.newBuilder().addPathElement(pathBuilder);
    }

}
