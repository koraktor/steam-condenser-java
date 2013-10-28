/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2008-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community.tf2;

import java.util.ArrayList;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.exceptions.WebApiException;
import com.github.koraktor.steamcondenser.community.GameInventory;
import com.github.koraktor.steamcondenser.community.GameStats;
import com.github.koraktor.steamcondenser.community.XMLData;

/**
 * This class represents the game statistics for a single user in Team Fortress
 * 2
 *
 * @author Sebastian Staudt
 */
public class TF2Stats extends GameStats {

    private int accumulatedPoints;

    private ArrayList<TF2Class> classStats;

    private GameInventory inventory;

    private int totalPlayTime;

    /**
     * Creates a new <code>TF2Stats</code> instance by calling the super
     * constructor with the game name <code>"tf2"</code>
     *
     * @param steamId The custom URL or 64bit Steam ID of the user
     * @throws SteamCondenserException if an error occurs while fetching the
     *         stats data
     */
    public TF2Stats(Object steamId) throws SteamCondenserException {
        this(steamId, false);
    }

    /**
     * Creates a new <code>TF2Stats</code> instance by calling the super
     * constructor with the game name <code>"tf2"</code> (or <code>"520"</code>
     * for the beta)
     *
     * @param steamId The custom URL or 64bit Steam ID of the user
     * @param beta If <code>true</code>, creates stats for the public TF2 beta
     * @throws SteamCondenserException if an error occurs while fetching the
     *         stats data
     */
    public TF2Stats(Object steamId, boolean beta) throws SteamCondenserException {
        super(steamId, (beta ? "520" : "tf2"));

        if(this.isPublic()) {
            this.accumulatedPoints = this.xmlData.getInteger("stats", "accumulatedPoints");
            this.totalPlayTime = this.xmlData.getInteger("stats", "secondsPlayedAllClassesLifetime");
        }
    }

    /**
     * Returns the total points this player has achieved in his career
     *
     * @return This player's accumulated points
     */
    public int getAccumulatedPoints() {
        return this.accumulatedPoints;
    }

    /**
     * Returns the accumulated number of seconds this player has spent playing as a TF2 class
     *
     * @return total seconds played as a TF2 class
     */
    public int getTotalPlayTime() {
        return this.totalPlayTime;
    }

    /**
     * Returns the statistics for all Team Fortress 2 classes for this user
     * <p>
     * If the classes haven't been parsed already, parsing is done now.
     *
     * @return An array storing individual stats for each Team Fortress 2 class
     */
    public ArrayList<TF2Class> getClassStats() {
        if(!this.isPublic()) {
            return null;
        }

        if(this.classStats == null) {
            this.classStats = new ArrayList<TF2Class>();
            for(XMLData classData : this.xmlData.getElements("stats", "classData")) {
                this.classStats.add(TF2ClassFactory.getTF2Class(classData));
            }
        }

        return this.classStats;
    }

    /**
     * Returns the current Team Fortress 2 inventory (a.k.a. backpack) of this
     * player
     *
     * @return This player's TF2 backpack
     * @throws WebApiException If an error occured while querying Steam's Web
     *         API
     */
    public GameInventory getInventory() throws SteamCondenserException {
        if(!this.isPublic()) {
            return null;
        }

        if(this.inventory == null) {
            this.inventory = GameInventory.create(this.getGame().getAppId(), this.user.getSteamId64());
        }

        return this.inventory;
    }
}
