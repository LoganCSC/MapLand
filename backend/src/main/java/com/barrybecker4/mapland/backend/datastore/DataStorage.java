package com.barrybecker4.mapland.backend.datastore;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.datastore.client.Datastore;
import com.google.api.services.datastore.client.DatastoreFactory;
import com.google.api.services.datastore.client.DatastoreHelper;
import com.google.api.services.datastore.client.DatastoreOptions;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;


/**
 * Encapsulates access to the google datastore
 */
public class DataStorage {

    /** same as appEngine app name. See https://console.developers.google.com/project/maplandbackend */
    private static final String DATASET_ID = "maplandbackend";

    private Datastore instance;

    public DataStorage() {
        instance = getDatastore();
    }

    public Datastore getInstance() {
        return instance;
    }

    /**
     * The environment variables DATASTORE_SERVICE_ACCOUNT and DATASTORE_PRIVATE_KEY_FILE must be set.
     * For example:
     *  DATASTORE_SERVICE_ACCOUNT =
     *  699545660653-e8s7820hnt688l84uu7aq93geplj6s3l@developer.gserviceaccount.com
     *  DATASTORE_PRIVATE_KEY_FILE = C:/Users/becker/backend/MapLandBackend-ecbfa6fe5549.p12
     *  See http://stackoverflow.com/questions/14209175/java-security-accesscontrolexception-access-denied-java-io-filepermission/32960102#32960102
     * @return the data store instance
     */
    private Datastore getDatastore() {
        Datastore datastore = null;

        // Setup the connection to Google Cloud Datastore and infer credentials
        try {
            Credential credential = getCredential();
            System.err.println("Got the credential:" + credential.toString());

            DatastoreOptions.Builder options = DatastoreHelper.getOptionsFromEnv().credential(credential);
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

        URL url = getClass().getResource(FILE_NAME);
        if ( url == null ){
            throw new RuntimeException( "Cannot find resource: '" + FILE_NAME + "'" );
        }
        String filename = url.getFile();

        return DatastoreHelper.getServiceAccountCredential(serviceAccount, filename);
    }
}
