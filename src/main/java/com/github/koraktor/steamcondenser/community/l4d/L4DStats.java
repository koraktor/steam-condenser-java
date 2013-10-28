/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2009-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community.l4d;

import java.util.HashMap;
import java.util.Map;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.community.GameWeapon;
import com.github.koraktor.steamcondenser.community.XMLData;

/**
 * This class represents the game statistics for a single user in Left4Dead
 *
 * @author Sebastian Staudt
 */
public class L4DStats extends AbstractL4DStats {

    /**
     * Creates a <code>L4DStats</code> object by calling the super constructor
     * with the game name <code>"l4d"</code>
     *
     * @param steamId The custom URL or 64bit Steam ID of the user
     * @throws SteamCondenserException if an error occurs while fetching the
     *         stats data
     */
    public L4DStats(Object steamId) throws SteamCondenserException {
        super(steamId, "l4d");
    }

    /**
     * Returns a map of Survival statistics for this user like revived
     * teammates
     * <p>
     * If the Survival statistics haven't been parsed already, parsing is done
     * now.
     *
     * @return The stats for the Survival mode
     */
    public Map<String, Object> getSurvivalStats()
            throws SteamCondenserException {
        if(!this.isPublic()) {
            return null;
        }

        if(this.survivalStats == null) {
            super.getSurvivalStats();
            HashMap<String, L4DMap> mapsHash = new HashMap<String, L4DMap>();
            for(XMLData mapData : this.xmlData.getElements("stats", "survival", "maps")) {
                mapsHash.put(mapData.getName(), new L4DMap(mapData));
            }
            this.survivalStats.put("maps", mapsHash);
        }

        return this.survivalStats;
    }

    /**
     * Returns a map of <code>L4DWeapon</code> for this user containing all
     * Left4Dead weapons
     * <p>
     * If the weapons haven't been parsed already, parsing is done now.
     *
     * @return The weapon statistics
     */
    public Map<String, GameWeapon> getWeaponStats() {
        if(!this.isPublic()) {
            return null;
        }

        if(this.weaponStats == null) {
            this.weaponStats = new HashMap<String, GameWeapon>();
            for(XMLData weaponData : this.xmlData.getChildren("stats", "weapons")) {
                String weaponName = weaponData.getName();
                GameWeapon weapon;
                if(!weaponName.equals("molotov") && !weaponName.equals("pipes")) {
                    weapon = new L4DWeapon(weaponData);
                }
                else {
                    weapon = new L4DExplosive(weaponData);
                }
                this.weaponStats.put(weaponName, weapon);
            }
        }

        return this.weaponStats;
    }
}
