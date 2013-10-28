/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community.l4d;

import com.github.koraktor.steamcondenser.community.XMLData;

/**
 * This class represents the statistics of a single weapon for a user in
 * Left4Dead 2
 *
 * @author Sebastian Staudt
 */
public class L4D2Weapon extends AbtractL4DWeapon {

    private int damage;

    private String weaponGroup;

    /**
     * Creates a new instance of a weapon based on the given XML data
     *
     * @param weaponData The XML data of this weapon
     */
    public L4D2Weapon(XMLData weaponData) {
        super(weaponData);

        this.damage = weaponData.getInteger("damage");
        this.killPercentage = Float.parseFloat(weaponData
                .getString("pctkills").replace("%", "")) * 0.01f;
        this.weaponGroup = weaponData.getAttribute("group");
    }

    /**
     * Returns the amount of damage done by the player with this weapon
     *
     * @return The damage done by this weapon
     */
    public int getDamage() {
        return this.damage;
    }

    /**
     * Returns the weapon group this weapon belongs to
     *
     * @return The group this weapon belongs to
     */
    public String getWeaponGroup() {
        return this.weaponGroup;
    }
}
