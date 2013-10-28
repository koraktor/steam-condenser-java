/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2008-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community.tf2;

import com.github.koraktor.steamcondenser.community.XMLData;

/**
 * Represents the stats for the Team Fortress 2 Engineer class for a specific
 * user
 *
 * @author Sebastian Staudt
 */
public class TF2Engineer extends TF2Class {

    private int maxBuildingsBuilt;

    private int maxSentryKills;

    private int maxTeleports;

    /**
     * Creates a new instance of the Engineer class based on the given XML data
     *
     * @param classData The XML data for this Engineer
     */
    public TF2Engineer(XMLData classData) {
        super(classData);

        this.maxBuildingsBuilt = classData.getInteger("ibuildingsbuilt");
        this.maxSentryKills    = classData.getInteger("isentrykills");
        this.maxTeleports      = classData.getInteger("inumteleports");
    }

    /**
     * Returns the maximum number of buildings built by the player in a single
     * life as an Engineer
     *
     * @return Maximum number of buildings built
     */
    public int getMaxBuildingsBuilt() {
        return this.maxBuildingsBuilt;
    }

    /**
     * Returns the maximum number of enemies killed by sentry guns built by the
     * player in a single life as an Engineer
     *
     * @return Maximum number of sentry kills
     */
    public int getMaxSentryKills() {
        return this.maxSentryKills;
    }

    /**
     * Returns the maximum number of teammates teleported by teleporters built
     * by the player in a single life as an Engineer
     *
     * @return Maximum number of teleports
     */
    public int getMaxTeleports() {
        return this.maxTeleports;
    }

}
