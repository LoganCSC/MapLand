package com.barrybecker4.mapland.backend;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.datastore.DatastoreV1;
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
import com.google.api.services.datastore.client.DatastoreFactory;
import com.google.api.services.datastore.client.DatastoreHelper;
import com.google.api.services.datastore.client.DatastoreOptions;
import com.google.protobuf.ByteString;


import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;

/**
 * Based on the introductory code at https://cloud.google.com/datastore/docs/getstarted/start_java/
 */
public class DataStoreAccess {

    /** same as appEngine app name. See https://console.developers.google.com/project/maplandbackend */
    private static final String DATASET_ID = "maplandbackend";

    public void dataStoreTest() {

        try {
            Entity entity = getEntity("Trivia", "hgtg");

            // Get `question` property value.
            String question = entity.getProperty(0).getValue().getStringValue();
            // Get `answer` property value.
            Long answer = entity.getProperty(1).getValue().getIntegerValue();

            System.out.println("Question = " + question);
            System.out.println("Answer = " + answer);

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
    }

    /** get the question entity, and if its not there add one */
    private Entity getEntity(String kind, String name) throws DatastoreException {
        Datastore datastore = getDatastore();

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
    }


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
    }

    /**
     * The environment variables DATASTORE_SERVICE_ACCOUNT and DATASTORE_PRIVATE_KEY_FILE must be set.
     * For example:
     *  DATASTORE_SERVICE_ACCOUNT =
     *  699545660653-e8s7820hnt688l84uu7aq93geplj6s3l@developer.gserviceaccount.com
     *  DATASTORE_PRIVATE_KEY_FILE = C:/Users/becker/backend/MapLandBackend-ecbfa6fe5549.p12
     * @return the data store instance
     */
    private Datastore getDatastore() {
        Datastore datastore = null;

        // Setup the connection to Google Cloud Datastore and infer credentials
        // from the environment.
        try {
            Credential credential = getCredential();
            System.err.println("Got the credential:" + credential.toString());

            DatastoreOptions.Builder options = DatastoreHelper.getOptionsFromEnv().credential(credential);
            //DatastoreOptions.Builder options = DatastoreHelper.getOptionsfromEnv();
            datastore = DatastoreFactory.get().create(options.dataset(DATASET_ID).build());
        } catch (GeneralSecurityException exception) {
            throw new IllegalArgumentException(
                    "Security error connecting to the datastore: " + exception.getMessage(), exception);
        } catch (IOException exception) {
            throw new IllegalArgumentException(
                    "I/O error connecting to the datastore: " + exception.getMessage(), exception);
        }

        System.out.println("successfully connected to datastore.");
        return datastore;
    }


    private Credential getCredential() throws GeneralSecurityException, IOException {
        final String serviceAccount = "699545660653-e8s7820hnt688l84uu7aq93geplj6s3l@developer.gserviceaccount.com";
        final String FILE_NAME = "/MapLandBackend-ecbfa6fe5549.p12";

        //URL url = getClass().getResource(FILE_NAME);
        //URL url = servletContext.getResource(FILE_NAME);
        URL url = getClass().getResource(FILE_NAME);
        if ( url == null ){
            throw new RuntimeException( "Cannot find resource: '" + FILE_NAME + "'" );
        }
        String filename = url.getFile();
        System.out.println("filename = " + filename);

        //String filename = "E:/projects/java_projects/android/MapLand/backend/MapLandBackend-ecbfa6fe5549.p12";
        return DatastoreHelper.getServiceAccountCredential(serviceAccount, filename);
    }
}
