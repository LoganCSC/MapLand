package com.barrybecker4.mapland.screens.games;

/**
 * A simple POJO that holds the details about the Game that are used by the List Adapter.
 * These details will originate for the GameBean.
 */
public class GameDetails {

    /** The name of the game.*/
    public final String name;

    /** The required number of players before it can start. */
    public final int numPlayers;

    /** The game duration in hours. It could end before this limit */
    public final int durationHours;


    public GameDetails(
            String name, int numPlayers, int durationsHours) {
        this.name = name;
        this.numPlayers = numPlayers;
        this.durationHours = durationsHours;
        //this.activityClass = activityClass;
    }

    public String getDescription() {
        return this.numPlayers + " required players will play for " + this.durationHours + " hours";
    }
}
