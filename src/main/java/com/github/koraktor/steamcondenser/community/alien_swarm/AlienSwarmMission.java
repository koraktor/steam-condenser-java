/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community.alien_swarm;

import java.util.HashMap;
import java.util.Map;

import com.github.koraktor.steamcondenser.community.XMLData;

/**
 * This class holds statistical information about missions played by a player
 * in Alien Swarm
 *
 * @author Sebastian Staudt
 */
public class AlienSwarmMission {

    private float avgDamageTaken;

    private float avgFriendlyFire;

    private float avgKills;

    private String bestDifficulty;

    private int damageTaken;

    private int friendlyFire;

    private int gamesSuccessful;

    private String img;

    private int kills;

    private String mapName;

    private String name;

    private Map<String, String> time;

    private int totalGames;

    private float totalGamesPercentage;

    /**
     * Creates a new mission instance of based on the given XML data
     *
     * @param missionData The data representing this mission
     */
    public AlienSwarmMission(XMLData missionData) {
        this.avgDamageTaken       = missionData.getFloat("damagetakenavg");
        this.avgFriendlyFire      = missionData.getFloat("friendlyfireavg");
        this.avgKills             = missionData.getFloat("killsavg");
        this.bestDifficulty       = missionData.getString("bestdifficulty");
        this.damageTaken          = missionData.getInteger("damagetaken");
        this.friendlyFire         = missionData.getInteger("friendlyfire");
        this.gamesSuccessful      = missionData.getInteger("gamessuccess");
        this.img                  = AlienSwarmStats.BASE_URL + missionData.getString("image");
        this.kills                = missionData.getInteger("kills");
        this.mapName              = missionData.getName();
        this.name                 = missionData.getString("name");
        this.totalGames           = missionData.getInteger("gamestotal");
        this.totalGamesPercentage = missionData.getFloat("gamestotalpct");

        this.time = new HashMap<String, String>();
        this.time.put("average", missionData.getString("avgtime"));
        this.time.put("brutal", missionData.getString("brutaltime"));
        this.time.put("easy", missionData.getString("easytime"));
        this.time.put("hard", missionData.getString("hardtime"));
        this.time.put("insane", missionData.getString("insanetime"));
        this.time.put("normal", missionData.getString("normaltime"));
        this.time.put("total", missionData.getString("totaltime"));
    }

    /**
     * Returns the avarage damage taken by the player while playing a round in
     * this mission
     *
     * @return The average damage taken by the player
     */
    public float getAvgDamageTaken() {
        return this.avgDamageTaken;
    }

    /**
     * Returns the avarage damage dealt by the player to team mates while
     * playing a round in this mission
     *
     * @return The average damage dealt by the player to team mates
     */
    public float getAvgFriendlyFire() {
        return this.avgFriendlyFire;
    }

    /**
     * Returns the avarage number of aliens killed by the player while playing
     * a round in this mission
     *
     * @return The avarage number of aliens killed by the player
     */
    public float getAvgKills() {
        return this.avgKills;
    }

    /**
     * Returns the highest difficulty the player has beat this mission in
     *
     * @return The highest difficulty the player has beat this mission in
     */
    public String getBestDifficulty() {
        return this.bestDifficulty;
    }

    /**
     * Returns the total damage taken by the player in this mission
     *
     * @return The total damage taken by the player
     */
    public int getDamageTaken() {
        return this.damageTaken;
    }

    /**
     * Returns the total damage dealt by the player to team mates in this
     * mission
     *
     * @return The total damage dealt by the player to team mates
     */
    public int getFriendlyFire() {
        return this.friendlyFire;
    }

    /**
     * Returns the number of successful rounds the player played in this
     * mission
     *
     * @return The number of successful rounds of this mission
     */
    public int getGamesSuccessful() {
        return this.gamesSuccessful;
    }

    /**
     * Returns the URL to a image displaying the mission
     *
     * @return The URL of the mission's image
     */
    public String getImg() {
        return this.img;
    }

    /**
     * Returns the total number of aliens killed by the player in this mission
     *
     * @return The total number of aliens killed by the player
     */
    public int getKills() {
        return this.kills;
    }

    /**
     * Returns the file name of the mission's map
     *
     * @return The file name of the mission's map
     */
    public String getMapName() {
        return this.mapName;
    }

    /**
     * Returns the name of the mission
     *
     * @return The name of the mission
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns various statistics about the times needed to accomplish this
     * mission
     *
     * This includes the best times for each difficulty, the average time and
     * the total time spent in this mission.
     *
     * @return Various time statistics about this mission
     */
    public Map<String, String> getTime() {
        return this.time;
    }

    /**
     * Returns the number of games played in this mission
     *
     * @return The number of games played in this mission
     */
    public int getTotalGames() {
        return this.totalGames;
    }

    /**
     * Returns the percentage of successful games played in this mission
     *
     * @return The percentage of successful games played in this mission
     */
    public float getTotalGamesPercentage() {
        return this.totalGamesPercentage;
    }

}
