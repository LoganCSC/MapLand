package com.barrybecker4.mapland.backend;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.datastore.DatastoreV1;
import com.google.api.services.datastore.DatastoreV1.BeginTransactionResponse;
import com.google.api.services.datastore.client.Datastore;
import com.google.api.services.datastore.client.DatastoreException;
import com.google.api.services.datastore.client.DatastoreFactory;
import com.google.api.services.datastore.client.DatastoreHelper;
import com.google.api.services.datastore.client.DatastoreOptions;
import com.google.protobuf.ByteString;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;

import javax.servlet.ServletContext;

/**
 * Based on the introductory code at https://cloud.google.com/datastore/docs/getstarted/start_java/
 */
public class DataStoreAccess {

    /** same as appEngine app name. See https://console.developers.google.com/project/maplandbackend */
    private static final String DATASET_ID = "maplandbackend";

    public void dataStoreTest() {

        Datastore datastore = getDatastore();

        try {
            // Create an RPC request to begin a new transaction.
            DatastoreV1.BeginTransactionRequest.Builder treq = DatastoreV1.BeginTransactionRequest.newBuilder();
            // Execute the RPC synchronously.
            BeginTransactionResponse tres = datastore.beginTransaction(treq.build());
            // Get the transaction handle from the response.
            ByteString tx = tres.getTransaction();

            // Create an RPC request to get entities by key.
            DatastoreV1.LookupRequest.Builder lreq = DatastoreV1.LookupRequest.newBuilder();
            // Set the entity key with only one `path_element`: no parent.
            DatastoreV1.Key.Builder key = DatastoreV1.Key.newBuilder().addPathElement(
                    DatastoreV1.Key.PathElement.newBuilder()
                            .setKind("Trivia")
                            .setName("hgtg"));
            // Add one key to the lookup request.
            lreq.addKey(key);

            // Set the transaction, so we get a consistent snapshot of the
            // entity at the time the transaction started.
            lreq.getReadOptionsBuilder().setTransaction(tx);
            // Execute the RPC and get the response.
            DatastoreV1.LookupResponse lresp = datastore.lookup(lreq.build());
            // Create an RPC request to commit the transaction.
            DatastoreV1.CommitRequest.Builder creq = DatastoreV1.CommitRequest.newBuilder();
            // Set the transaction to commit.
            creq.setTransaction(tx);
            DatastoreV1.Entity entity;
            if (lresp.getFoundCount() > 0) {
                System.out.println("Found an entity");
                entity = lresp.getFound(0).getEntity();
            } else {
                System.out.println("No entity found. Adding one.");
                // If no entity was found, create a new one.
                DatastoreV1.Entity.Builder entityBuilder = DatastoreV1.Entity.newBuilder();
                // Set the entity key.
                entityBuilder.setKey(key);
                // Add two entity properties:
                // - a utf-8 string: `question`
                entityBuilder.addProperty(DatastoreV1.Property.newBuilder()
                        .setName("question")
                        .setValue(DatastoreV1.Value.newBuilder()
                                .setStringValue("What is the meaning of Life?")));
                // - a 64bit integer: `answer`
                entityBuilder.addProperty(DatastoreV1.Property.newBuilder()
                        .setName("answer")
                        .setValue(DatastoreV1.Value.newBuilder().setIntegerValue(42)));
                // Build the entity.
                entity = entityBuilder.build();
                // Insert the entity in the commit request mutation.
                creq.getMutationBuilder().addInsert(entity);
            }
            // Execute the Commit RPC synchronously and ignore the response.
            // Apply the insert mutation if the entity was not found and close
            // the transaction.
            datastore.commit(creq.build());


            // Get `question` property value.
            String question = entity.getProperty(0).getValue().getStringValue();
            // Get `answer` property value.
            Long answer = entity.getProperty(1).getValue().getIntegerValue();
            System.out.println(question);

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
