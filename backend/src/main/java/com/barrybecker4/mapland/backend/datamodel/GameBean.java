package com.barrybecker4.mapland.backend.datamodel;

import com.google.api.services.datastore.DatastoreV1;
import com.google.api.services.datastore.DatastoreV1.Entity;
import com.google.api.services.datastore.client.DatastoreHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Used to transfer information about a multi-player game instance.
 * See https://github.com/LoganCSC/MapLand/issues/12
 *
 * @author Barry Becker
 */
public class GameBean {

    private Long gameId;

    /** name of the game. Does not have to be unique */
    private String gameName;

    /**
     * Required number of players.
     * It may be longer than the length of the player list before the game starts
     */
    private Integer numPlayers;

    private List<Long> players;

    /** The maximum game duration in hours */
    private Integer duration;

    /** timestamp when game started */
    private Date startTime;

    private double nwLatitudeCoord;
    private double nwLongitudeCoord;
    private double seLatitudeCoord;
    private double seLongitudeCoord;


    public GameBean() {}

    public GameBean(Entity gameEntity, Long id) {
        this(gameEntity);
        this.setGameId(id);
        System.out.println("created Game: " + this);
    }

    public GameBean(Entity gameEntity) {

        Map<String, DatastoreV1.Value> propertyMap = DatastoreHelper.getPropertyMap(gameEntity);

        DatastoreV1.Value idVal = propertyMap.get("regionId");
        Long id = gameEntity.getKey().getPathElement(0).getId();
        //Long regionId = id; //idVal == null ?  null : idVal.getIntegerValue();
        //RegionBean region = new RegionBean(propertyMap.get("region").getEntityValue());
        double nwLat = propertyMap.get("nwLatitudeCoord").getDoubleValue();
        double nwLng = propertyMap.get("nwLongitudeCoord").getDoubleValue();
        double seLat = propertyMap.get("seLatitudeCoord").getDoubleValue();
        double seLng = propertyMap.get("seLongitudeCoord").getDoubleValue();

        String gameName = propertyMap.get("gameName").getStringValue();
        Integer numPlayers = (int) propertyMap.get("numPlayers").getIntegerValue();
        Integer duration = (int) propertyMap.get("duration").getIntegerValue();
        Long micros = propertyMap.get("startTime").getTimestampMicrosecondsValue();
        Date startTime = new Date(micros / 1000);

        List<Long> players = new ArrayList<>();
        DatastoreV1.Value playerList = propertyMap.get("players");
        if (playerList != null) {
            for (DatastoreV1.Value value : playerList.getListValueList()) {
                System.out.println("game player: " + value.getIntegerValue());
                players.add(value.getIntegerValue());
            }
        }

        this.setGameId(id);
        this.setGameName(gameName);
        this.setNumPlayers(numPlayers);
        //this.setRegion(region);
        this.setNwLatitudeCoord(nwLat);
        this.setNwLongitudeCoord(nwLng);
        this.setSeLatitudeCoord(seLat);
        this.setSeLongitudeCoord(seLng);
        this.setPlayers(players);
        this.setDuration(duration);
        this.setStartTime(startTime);
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Integer getNumPlayers() {
        return numPlayers;
    }

    public void setNumPlayers(Integer numPlayers) {
        this.numPlayers = numPlayers;
    }
    /*
    public RegionBean getRegion() {
        return region;
    }

    public void setRegion(RegionBean region) {
        this.region = region;
    }*/

    public List<Long> getPlayers() {
        return players;
    }

    public void setPlayers(List<Long> players) {
        this.players = players;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public double getNwLatitudeCoord() {
        return nwLatitudeCoord;
    }

    public void setNwLatitudeCoord(double nwLatitudeCoord) {
        this.nwLatitudeCoord = nwLatitudeCoord;
    }

    public double getNwLongitudeCoord() {
        return nwLongitudeCoord;
    }

    public void setNwLongitudeCoord(double nwLongitudeCoord) {
        this.nwLongitudeCoord = nwLongitudeCoord;
    }

    public double getSeLatitudeCoord() {
        return seLatitudeCoord;
    }

    public void setSeLatitudeCoord(double seLatitudeCoord) {
        this.seLatitudeCoord = seLatitudeCoord;
    }

    public double getSeLongitudeCoord() {
        return seLongitudeCoord;
    }

    public void setSeLongitudeCoord(double seLongitudeCoord) {
        this.seLongitudeCoord = seLongitudeCoord;
    }


    public String toString() {
        return "{gameId: " + this.gameId + " name: " + this.gameName
                + " region: [" + this.nwLatitudeCoord + ", " + this.nwLongitudeCoord + "] ["
                + this.seLatitudeCoord + ", " + this.seLongitudeCoord + "] "
                + " duration: "+this.duration +" players: " + this.players;
    }
}
