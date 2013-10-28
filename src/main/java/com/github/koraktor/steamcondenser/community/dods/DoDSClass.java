/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2009-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community.dods;

import com.github.koraktor.steamcondenser.community.GameClass;
import com.github.koraktor.steamcondenser.community.XMLData;

/**
 * Represents the stats for a Day of Defeat: Source class for a specific user
 *
 * @author Sebastian Staudt
 */
public class DoDSClass extends GameClass {

    private int blocks;

    private int bombsDefused;

    private int bombsPlanted;

    private int captures;

    private int deaths;

    private int dominations;

    private String key;

    private int kills;

    private int roundsLost;

    private int roundsWon;

    private int revenges;

    /**
     * Creates a new instance of a Day of Defeat: Source class based on the
     * given XML data
     *
     * @param classData The XML data of the class
     */
    public DoDSClass(XMLData classData) {
        this.blocks = classData.getInteger("blocks");
        this.bombsDefused = classData.getInteger("bombsdefused");
        this.bombsPlanted = classData.getInteger("bombsplanted");
        this.captures = classData.getInteger("captures");
        this.deaths = classData.getInteger("deaths");
        this.dominations = classData.getInteger("dominations");
        this.key = classData.getAttribute("key");
        this.kills = classData.getInteger("kills");
        this.name = classData.getString("name");
        this.playTime = classData.getInteger("playtime");
        this.roundsLost = classData.getInteger("roundslost");
        this.roundsWon = classData.getInteger("roundswon");
        this.revenges = classData.getInteger("revenges");
    }

    /**
     * Returns the blocks achieved by the player with this class
     *
     * @return The blocks achieved by the player
     */
    public int getBlocks() {
        return this.blocks;
    }

    /**
     * Returns the bombs defused by the player with this class
     *
     * @return The bombs defused by the player
     */
    public int getBombsDefuse() {
        return this.bombsDefused;
    }

    /**
     * Returns the bombs planted by the player with this class
     *
     * @return the bombs planted by the player
     */
    public int getBombsPlanted() {
        return this.bombsPlanted;
    }

    /**
     * Returns the number of points captured by the player with this class
     *
     * @return The number of points captured by the player
     */
    public int getCaptures() {
        return this.captures;
    }

    /**
     * Returns the number of times the player died with this class
     *
     * @return The number of deaths by the player
     */
    public int getDeaths() {
        return this.deaths;
    }

    /**
     * Returns the dominations achieved by the player with this class
     *
     * @return The dominations achieved by the player
     */
    public int getDominations() {
        return this.dominations;
    }

    /**
     * Returns the ID of this class
     *
     * @return The ID of this class
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Returns the number of enemies killed by the player with this class
     *
     * @return The number of enemies killed by the player
     */
    public int getKills() {
        return this.kills;
    }

    /**
     * Returns the revenges achieved by the player with this class
     *
     * @return The revenges achieved by the player
     */
    public int getRevenges() {
        return this.revenges;
    }

    /**
     * Returns the number of rounds lost with this class
     *
     * @return The number of rounds lost with this class
     */
    public int getRoundsLost() {
        return this.roundsLost;
    }

    /**
     * Returns the number of rounds won with this class
     *
     * @return The number of rounds won with this class
     */
    public int getRoundsWon() {
        return this.roundsWon;
    }
}
