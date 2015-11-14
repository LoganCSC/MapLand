package com.barrybecker4.mapland.backend.datastore;

//import com.google.appengine.api.datastore.Entity;
import com.google.api.services.datastore.DatastoreV1;
import com.google.api.services.datastore.DatastoreV1.BeginTransactionResponse;
import com.google.api.services.datastore.DatastoreV1.BeginTransactionRequest;
import com.google.api.services.datastore.DatastoreV1.Entity;
import com.google.api.services.datastore.DatastoreV1.LookupResponse;
import com.google.api.services.datastore.DatastoreV1.LookupRequest;
import com.google.api.services.datastore.DatastoreV1.Key;
import com.google.api.services.datastore.DatastoreV1.CommitRequest;
import com.google.api.services.datastore.client.Datastore;
import com.google.api.services.datastore.client.DatastoreException;

import com.google.protobuf.ByteString;

import java.util.LinkedList;
import java.util.List;


/**
 * Based on the introductory code at https://cloud.google.com/datastore/docs/getstarted/start_java/
 */
public class DataStoreAccess {

    protected Datastore datastore = DataStorage.getInstance();


    /**
     * Get the specified entity. If its not there, throw an exception.
     * @param kind the kind of entity to get
     * @param id unique id - either name string or long id.
     * @throws DatastoreException if problem accessing the datastore
     * @throws IllegalStateException if entity with specified id not found
     */
    protected Entity getEntity(String kind, Object id) throws DatastoreException {

        // Create an RPC request to get entities by key.
        LookupRequest.Builder lreq = LookupRequest.newBuilder();

        Key.Builder key = createKey(kind, id);
        lreq.addKey(key); // Add one key to the lookup request.

        // Set the transaction, so we get a consistent snapshot of the entity at the time the txn started.
        lreq.getReadOptionsBuilder().setTransaction(createTransaction());
        // Execute the RPC and get the response.
        LookupResponse lresp = datastore.lookup(lreq.build());


        Entity entity;
        if (lresp.getFoundCount() > 0) {
            System.out.println("Found an entity with id = " + id);
            entity = lresp.getFound(0).getEntity();
        } else {
            throw new IllegalStateException("No " + kind + " entity found with id " + id);
        }

        return entity;
    }

    /**
     * Get the specified entities. If its not there, throw an exception.
     * @param kind the kind of entity to get
     * @param ids unique id - either name string or long id.
     * @throws DatastoreException if problem accessing the datastore
     * @throws IllegalStateException if entity with specified id not found
     */
    protected List<Entity> getEntities(String kind, List<?> ids) throws DatastoreException {

        // Create an RPC request to get entities by key.
        LookupRequest.Builder lreq = LookupRequest.newBuilder();

        for (Object id : ids) {
            Key.Builder key = createKey(kind, id);
            lreq.addKey(key); // Add one key to the lookup request.
        }

        // Set the transaction, so we get a consistent snapshot of the entity at the time the txn started.
        lreq.getReadOptionsBuilder().setTransaction(createTransaction());
        // Execute the RPC and get the response.
        LookupResponse lresp = datastore.lookup(lreq.build());

        List<Entity> entities = new LinkedList<>();
        if (lresp.getFoundCount() > 0) {
            System.out.println("Found entities with ids = " + ids);
            for (DatastoreV1.EntityResult e : lresp.getFoundList()) {
                entities.add(e.getEntity());
            }
        } else {
            throw new IllegalStateException("No " + kind + " entities found for " + ids);
        }

        return entities;
    }


    /**
     * @param entity the entity to add
     * @return the region of the added entity that was added (will now have its ID)
     * DatastoreException if problem accessing the datastore
     */
    protected Long insertEntity(Entity entity) throws DatastoreException {

        // Create an RPC request to commit the transaction.
        CommitRequest.Builder creq = CommitRequest.newBuilder();
        // Set the transaction to commit.
        creq.setTransaction(createTransaction());

        // Insert the entity in the commit request mutation.
        creq.getMutationBuilder().addInsertAutoId(entity); // addInsert(entity)

        // Execute the Commit RPC synchronously and ignore the response.
        // Apply the insert mutation if the entity was not found and close
        // the transaction.
        DatastoreV1.CommitResponse resp = datastore.commit(creq.build());
        Key key = resp.getMutationResult().getInsertAutoIdKey(0);
        Long id = key.getPathElement(0).getId();
        System.out.println("id generated for region just added : " + id);
        return id;
    }

    /**
     * @return new transaction
     * @throws DatastoreException if problem accessing the datastore
     */
    protected ByteString createTransaction() throws DatastoreException {
        // Create an RPC request to begin a new transaction.
        BeginTransactionRequest.Builder treq = BeginTransactionRequest.newBuilder();
        // Execute the RPC synchronously.
        BeginTransactionResponse tres = datastore.beginTransaction(treq.build());
        // Get the transaction handle from the response.
        return tres.getTransaction();
    }

    protected void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
