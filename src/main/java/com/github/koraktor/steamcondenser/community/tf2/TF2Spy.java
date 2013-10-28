/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2008-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community.tf2;

import com.github.koraktor.steamcondenser.community.XMLData;

/**
 * Represents the stats for the Team Fortress 2 Spy class for a specific user
 *
 * @author Sebastian Staudt
 */
public class TF2Spy extends TF2Class {

    private int maxBackstabs;

    private int maxHeadShots;

    private int maxHealthLeeched;

    /**
     * Creates a new instance of the Spy class based on the given XML data
     *
     * @param classData The XML data for this Spy
     */
    public TF2Spy(XMLData classData) {
        super(classData);

        this.maxBackstabs      = classData.getInteger("ibackstabs");
        this.maxHeadShots      = classData.getInteger("iheadshots");
        this.maxHealthLeeched  = classData.getInteger("ihealthpointsleached");
    }

    /**
     * Returns the maximum health leeched from enemies by the player in a single
     * life as a Spy
     *
     * @return Maximum health leeched
     */
    public int getMaxBackstabs() {
        return this.maxBackstabs;
    }

    /**
     * Returns the head shots by the player in a single life as a Spy
     *
     * @return Maximum number of head shots
     */
    public int getMaxHeadShots() {
        return this.maxHeadShots;
    }

    /**
     * Returns the maximum health leeched from enemies by the player in a single
     * life as a Spy
     *
     * @return Maximum health leeched
     */
    public int getMaxHealthLeeched() {
        return this.maxHealthLeeched;
    }
}
