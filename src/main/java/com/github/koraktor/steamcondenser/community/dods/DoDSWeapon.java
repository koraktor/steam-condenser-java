/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2009-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community.dods;

import com.github.koraktor.steamcondenser.community.GameWeapon;
import com.github.koraktor.steamcondenser.community.XMLData;

/**
 * Represents the stats for a Day of Defeat: Source weapon for a specific user
 *
 * @author Sebastian Staudt
 */
public class DoDSWeapon extends GameWeapon {

    private int headshots;

    private int hits;

    private String name;

    /**
     * Creates a new instance of a Day of Defeat: Source weapon based on the
     * given XML data
     *
     * @param weaponData The XML data of the class
     */
    public DoDSWeapon(XMLData weaponData) {
        super(weaponData);

        this.headshots = weaponData.getInteger("headshots");
        this.id = weaponData.getAttribute("key");
        this.name = weaponData.getString("name");
        this.shots = weaponData.getInteger("shotsfired");
        this.hits = weaponData.getInteger("shotshit");
    }

    /**
     * Returns the average number of hits needed for a kill with this weapon
     *
     * @return The average number of hits needed for a kill
     */
    public float getAvgHitsPerKill() {
        return this.hits / this.kills;
    }

    /**
     * Returns the percentage of headshots relative to the shots hit with this
     * weapon
     *
     * @return The percentage of headshots
     */
    public float getHeadshotPercentage() {
        return this.headshots / this.hits;
    }

    /**
     * Returns the number of headshots achieved with this weapon
     *
     * @return The number of headshots achieved
     */
    public int getHeadshots() {
        return this.headshots;
    }

    /**
     * Returns the percentage of hits relative to the shots fired with this
     * weapon
     *
     * @return The percentage of hits
     */
    public float getHitPercentage() {
        return this.hits / this.shots;
    }

    /**
     * Returns the number of hits achieved with this weapon
     *
     * @return The number of hits achieved
     */
    public int getHits() {
        return this.hits;
    }

    /**
     * Returns the name of this weapon
     *
     * @return The name of this weapon
     */
    public String getName() {
        return this.name;
    }

}
