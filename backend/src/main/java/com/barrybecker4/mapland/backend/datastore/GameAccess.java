package com.barrybecker4.mapland.backend.datastore;

import com.barrybecker4.mapland.backend.datamodel.GameBean;
import com.google.api.services.datastore.DatastoreV1.CommitRequest;
import com.google.api.services.datastore.DatastoreV1.Entity;
import com.google.api.services.datastore.DatastoreV1.Key;
import com.google.api.services.datastore.DatastoreV1.LookupRequest;
import com.google.api.services.datastore.DatastoreV1.LookupResponse;
import com.google.api.services.datastore.DatastoreV1.Property;
import com.google.api.services.datastore.DatastoreV1.Value;
import com.google.api.services.datastore.client.DatastoreException;
import com.google.protobuf.ByteString;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Provide access to games entities in datastore.
 */
public class GameAccess extends DataStoreAccess {

    public static final String KIND = "Game";
    private static final String DEFAULT_GAME_NAME = "New Game";
    private static final int DEFAULT_NUM_PLAYERS = 2;
    private static final int DEFAULT_DURATION = 72;

    // define default bounding region
    // // 37.6021,-122.0758 - house
    private static final double NW_LAT = 37.7;
    private static final double NW_LNG = -122.1;
    private static final double SE_LAT = 37.5;
    private static final double SE_LNG = -121.9;


    /**
     * Get the specified game if it is in the database.
     * If not in the database, add a new record for it based on defaults.
     * @param gameId game identifier
     */
    public GameBean getGameById(Long gameId) {
        GameBean game = null;

        try {
            System.out.println("About to get game for " + gameId);
            Entity entity = getGameEntity(KIND, gameId);
            game = new GameBean(entity);
        }
        catch (DatastoreException exception) {
            fatalError(exception);
        }

        return game;
    }

    /** update the game in the datastore */
    public boolean updateGame(GameBean game) throws DatastoreException {

        return updateEntity(createGameEntity(game));
    }

    /**
     * Get the game entity, and if its not there create one.
     * If the game is there, we need to update it.
     */
    private Entity getGameEntity(String kind, Long id) throws DatastoreException {

        // Create an RPC request to get entities by key.
        LookupRequest.Builder lreq = LookupRequest.newBuilder();
        // Set the entity key with only one `path_element`: no parent.
        Key.Builder key = Key.newBuilder().addPathElement(
                Key.PathElement.newBuilder().setKind(kind).setId(id));
        lreq.addKey(key); // Add one key to the lookup request.

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
            System.out.println("Found a game entity with id = " + id);
            entity = lresp.getFound(0).getEntity();
        } else {
            System.out.println("No game entity found for id = " + id + ". Adding one.");
            // If no entity was found, create a new one.

            List<Long> regions = new LinkedList<>();
            entity = createGameEntity(key, DEFAULT_GAME_NAME, DEFAULT_NUM_PLAYERS, DEFAULT_DURATION,
                    NW_LAT, NW_LNG, SE_LAT, SE_LNG);
            // Insert the entity in the commit request mutation.
            creq.getMutationBuilder().addInsert(entity);
        }

        // Execute the Commit RPC synchronously and ignore the response.
        // Apply the insert mutation if the entity was not found and close
        // the transaction.
        datastore.commit(creq.build());
        return entity;
    }

    private Entity createGameEntity(GameBean game) {

        // Set the entity key with only one `path_element`: no parent.
        Key.Builder key = Key.newBuilder().addPathElement(
                Key.PathElement.newBuilder().setKind(KIND).setId(game.getGameId()));

        return createGameEntity(key, game.getGameName(), game.getNumPlayers(), game.getDuration(),
                game.getNwLatitudeCoord(), game.getNwLongitudeCoord(),
                game.getSeLatitudeCoord(), game.getSeLongitudeCoord());
    }

    /** @return new User entity with specified info */
    private Entity createGameEntity(
            Key.Builder key, String gameName, int numPlayers, int durationHrs,
            double nwLat, double nwLng, double seLat, double seLng) {
        Entity entity;
        Entity.Builder entityBuilder = Entity.newBuilder();
        // Set the entity key.
        entityBuilder.setKey(key);
        // - a utf-8 string: `user name`
        entityBuilder.addProperty(Property.newBuilder()
                .setName("gameName")
                .setValue(Value.newBuilder().setStringValue(gameName)));

        entityBuilder.addProperty(Property.newBuilder()
                .setName("numPlayers")
                .setValue(Value.newBuilder().setIntegerValue(numPlayers)));

        entityBuilder.addProperty(Property.newBuilder()
                .setName("duration")
                .setValue(Value.newBuilder().setIntegerValue(durationHrs)));


        //entityBuilder.addProperty(Property.newBuilder()
        //        .setName("region")
        //        .setValue(Value.newBuilder().setEntityValue(RegionAccess.createRegionEntity(region))));

        entityBuilder.addProperty(Property.newBuilder()
                .setName("nwLatitudeCoord")
                .setValue(Value.newBuilder().setDoubleValue(nwLat)));
        entityBuilder.addProperty(Property.newBuilder()
                .setName("nwLongitudeCoord")
                .setValue(Value.newBuilder().setDoubleValue(nwLng)));
        entityBuilder.addProperty(Property.newBuilder()
                .setName("seLatitudeCoord")
                .setValue(Value.newBuilder().setDoubleValue(seLat)));
        entityBuilder.addProperty(Property.newBuilder()
                .setName("seLongitudeCoord")
                .setValue(Value.newBuilder().setDoubleValue(seLng)));

        // Build the entity.
        entity = entityBuilder.build();
        return entity;
    }
}
