/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community.l4d;

import com.github.koraktor.steamcondenser.community.GameWeapon;
import com.github.koraktor.steamcondenser.community.XMLData;

/**
 * This abstract class is a base class for weapons in Left4Dead and Left4Dead 2
 * as the weapon stats for both games are very similar
 *
 * @author Sebastian Staudt
 */
public abstract class AbtractL4DWeapon extends GameWeapon {

    protected float accuracy;

    protected float headshotPercentage;

    protected float killPercentage;

    protected String name;

    protected int shots;

    /**
     * Creates a new instance of weapon from the given XML data and parses
     * common data for both, <code>L4DWeapon</code> and <code>L4D2Weapon</code>
     *
     * @param weaponData The XML data for this weapon
     */
    public AbtractL4DWeapon(XMLData weaponData) {
        super(weaponData);

        this.accuracy = Float.parseFloat(weaponData
            .getString("accuracy").replace("%", "")) * 0.01f;
        this.headshotPercentage = Float.parseFloat(weaponData
            .getString("headshots").replace("%", "")) * 0.01f;
        this.name = weaponData.getName();
        this.shots = weaponData.getInteger("shots");
    }

    /**
     * Returns the overall accuracy of the player with this weapon
     *
     * @return The accuracy of the player with this weapon
     */
    public float getAccuracy() {
        return this.accuracy;
    }

    /**
     * Returns the percentage of kills with this weapon that have been
     * headshots
     *
     * @return The percentage of headshots with this weapon
     */
    public float getHeadshotPercentage() {
        return this.headshotPercentage;
    }

    /**
     * Returns the percentage of overall kills of the player that have been
     * achieved with this weapon
     *
     * @return The percentage of kills with this weapon
     */
    public float getKillPercentage() {
        return this.killPercentage;
    }

    /**
     * Returns the name of the weapon
     *
     * @return The name of the weapon
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the number of shots the player has fired with this weapon
     *
     * @return The number of shots with this weapon
     */
    public int getShots() {
        return this.shots;
    }
}
