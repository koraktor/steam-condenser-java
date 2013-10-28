/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2008-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community.tf2;

import com.github.koraktor.steamcondenser.community.GameClass;
import com.github.koraktor.steamcondenser.community.XMLData;

/**
 * Represents the stats for a Team Fortress 2 class for a specific user
 *
 * @author Sebastian Staudt
 */
public class TF2Class extends GameClass {

    protected int maxBuildingsDestroyed;
    protected int maxCaptures;
    protected int maxDamage;
    protected int maxDefenses;
    protected int maxDominations;
    protected int maxKillAssists;
    protected int maxKills;
    protected int maxRevenges;
    protected int maxScore;
    protected int maxTimeAlive;

    /**
     * Creates a new TF2 class instance based on the assigned XML data
     *
     * @param classData The XML data for this class
     */
    public TF2Class(XMLData classData) {
        this.name                  = classData.getString("className");
        this.maxBuildingsDestroyed = classData.getInteger("ibuildingsdestroyed");
        this.maxCaptures           = classData.getInteger("ipointcaptures");
        this.maxDamage             = classData.getInteger("idamagedealt");
        this.maxDefenses           = classData.getInteger("ipointdefenses");
        this.maxDominations        = classData.getInteger("idominations");
        this.maxKillAssists        = classData.getInteger("ikillassists");
        this.maxKills              = classData.getInteger("inumberofkills");
        this.maxRevenges           = classData.getInteger("irevenge");
        this.maxScore              = classData.getInteger("ipointsscored");
        this.maxTimeAlive          = classData.getInteger("iplaytime");
        this.playTime              = classData.getInteger("playtimeSeconds");
    }

    /**
     * Returns the maximum number of buildings the player has destroyed in a
     * single life with this class
     *
     * @return Maximum number of buildings destroyed
     */
    public int getMaxBuildingsDestroyed() {
        return this.maxBuildingsDestroyed;
    }

    /**
     * Returns the maximum number of points captured by the player in a single
     * life with this class
     *
     * @return Maximum number of points captured
     */
    public int getMaxCaptures() {
        return this.maxCaptures;
    }

    /**
     * Returns the maximum damage dealt by the player in a single life with
     * this class
     *
     * @return Maximum damage dealt
     */
    public int getMaxDamage() {
        return this.maxDamage;
    }

    /**
     * Returns the maximum number of defenses by the player in a single life
     * with this class
     *
     * @return Maximum number of defenses
     */
    public int getMaxDefenses() {
        return this.maxDefenses;
    }

    /**
     * Returns the maximum number of dominations by the player in a single life
     * with this class
     *
     * @return Maximum number of dominations
     */
    public int getMaxDominations() {
        return this.maxDominations;
    }

    /**
     * Returns the maximum number of times the the player assisted a teammate
     * with killing an enemy in a single life with this class
     *
     * @return Maximum number of kill assists
     */
    public int getMaxKillAssists() {
        return this.maxKillAssists;
    }

    /**
     * Returns the maximum number of enemies killed by the player in a single
     * life with this class
     *
     * @return Maximum number of kills
     */
    public int getMaxKills() {
        return this.maxKills;
    }

    /**
     * Returns the maximum number of revenges by the player in a single life
     * with this class
     *
     * @return Maximum number of revenges
     */
    public int getMaxRevenges() {
        return this.maxRevenges;
    }

    /**
     * Returns the maximum number score achieved by the player in a single life
     * with this class
     *
     * @return Maximum score
     */
    public int getMaxScore() {
        return this.maxScore;
    }

    /**
     * Returns the maximum lifetime by the player in a single life with this
     * class
     *
     * @return Maximum lifetime
     */
    public int getMaxTimeAlive() {
        return this.maxTimeAlive;
    }
}
