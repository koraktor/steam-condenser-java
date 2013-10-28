/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community.l4d;

import java.util.HashMap;
import java.util.Map;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.community.GameWeapon;
import com.github.koraktor.steamcondenser.community.XMLData;

/**
 * This class represents the game statistics for a single user in Left4Dead 2
 *
 * @author Sebastian Staudt
 */
public class L4D2Stats extends AbstractL4DStats {

    private HashMap<String, Object> scavengeStats;
    private HashMap<String, Float> damagePercentages;

    /**
     * Creates a <code>L4D2Stats</code> object by calling the super constructor
     * with the game name <code>"l4d2"</code>
     *
     * @param steamId The custom URL or 64bit Steam ID of the user
     * @throws SteamCondenserException if an error occurs while fetching the
     *         stats data
     */
    public L4D2Stats(Object steamId) throws SteamCondenserException {
        super(steamId, "l4d2");

        XMLData weaponsData = this.xmlData.getElement("stats", "weapons");
        this.damagePercentages = new HashMap<String, Float>();
        this.damagePercentages.put("melee", weaponsData.getFloat("meleePctDmg"));
        this.damagePercentages.put("pistols", weaponsData.getFloat("pistolsPctDmg"));
        this.damagePercentages.put("rifles", weaponsData.getFloat("bulletsPctDmg"));
        this.damagePercentages.put("shotguns", weaponsData.getFloat("shellsPctDmg"));
    }

    /**
     * Returns the percentage of damage done by this player with each weapon
     * type
     *
     * Available weapon types are <var>"melee"</var>, <var>"pistols"</var>,
     * <var>"rifles"</var> and <var>"shotguns"</var>.
     *
     * @return float The percentages of damage done with each weapon type
     */
    public Map<String, Float> getDamagePercentages() {
        return this.damagePercentages;
    }

    /**
     * Returns a map of lifetime statistics for this user like the time played
     * <p>
     * If the lifetime statistics haven't been parsed already, parsing is done
     * now.
     * <p>
     * There are only a few additional lifetime statistics for Left4Dead 2
     * which are not generated for Left4Dead, so this calls
     * <code>AbstractL4DStats#getLifetimeStats()</code> first and adds some
     * additional stats.
     *
     * @return The lifetime statistics of the player in Left4Dead 2
     */
    public Map<String, Object> getLifetimeStats() {
        if(!this.isPublic()) {
            return null;
        }

        if(this.lifetimeStats == null) {
            super.getLifetimeStats();
            XMLData lifetimeStatsElement = this.xmlData.getElement("stats", "lifetime");
            this.lifetimeStats.put("avgAdrenalineShared", lifetimeStatsElement.getFloat("adrenalineshared"));
            this.lifetimeStats.put("avgAdrenalineUsed", lifetimeStatsElement.getFloat("adrenalineused"));
            this.lifetimeStats.put("avgDefibrillatorsUsed", lifetimeStatsElement.getFloat("defibrillatorsused"));
        }

        return this.lifetimeStats;
    }

    /**
     * Returns a map of Scavenge statistics for this user like the number of
     * Scavenge rounds played
     * <p>
     * If the Scavenge statistics haven't been parsed already, parsing is done
     * now.
     *
     * @return The Scavenge statistics of the player
     */
    public Map<String, Object> getScavengeStats() {
        if(!this.isPublic()) {
            return null;
        }

        if(this.scavengeStats == null) {
            XMLData scavengeStatsElement = this.xmlData.getElement("stats", "scavenge");
            this.scavengeStats = new HashMap<String, Object>();
            this.scavengeStats.put("avgCansPerRound", scavengeStatsElement.getFloat("avgcansperround"));
            this.scavengeStats.put("perfectRounds", scavengeStatsElement.getInteger("perfect16canrounds"));
            this.scavengeStats.put("roundsLost", scavengeStatsElement.getInteger("roundslost"));
            this.scavengeStats.put("roundsPlayed", scavengeStatsElement.getInteger("roundsplayed"));
            this.scavengeStats.put("roundsWon", scavengeStatsElement.getInteger("roundswon"));
            this.scavengeStats.put("totalCans", scavengeStatsElement.getInteger("totalcans"));

            HashMap<String, HashMap<String, Object>> mapsHash = new HashMap<String, HashMap<String, Object>>();
            for(XMLData mapData : scavengeStatsElement.getChildren("mapstats")) {
                String mapId = mapData.getString("name");
                HashMap<String, Object> mapHash = new HashMap<String, Object>();
                mapHash.put("avgRoundScore", mapData.getInteger("avgscoreperround"));
                mapHash.put("highestGameScore", mapData.getInteger("highgamescore"));
                mapHash.put("highestRoundScore", mapData.getInteger("highroundscore"));
                mapHash.put("name", mapData.getString("fullname"));
                mapHash.put("roundsPlayed", mapData.getInteger("roundsplayed"));
                mapHash.put("roundsWon", mapData.getInteger("roundswon"));
                mapsHash.put(mapId, mapHash);
            }
            this.scavengeStats.put("maps", mapsHash);
        }

        return this.scavengeStats;
    }

    /**
     * Returns a map of Survival statistics for this user like revived
     * teammates
     * <p>
     * If the Survival statistics haven't been parsed already, parsing is done
     * now.
     *
     * The XML layout for the Survival statistics for Left4Dead 2 differs a bit
     * from Left4Dead's Survival statistics. So we have to use a different way
     * of parsing for the maps and we use a different map class
     * (<code>L4D2Map</code>) which holds the additional information provided
     * in Left4Dead 2's statistics.
     *
     * @return The Survival statistics of the player
     */
    public Map<String, Object> getSurvivalStats()
            throws SteamCondenserException {
        if(!this.isPublic()) {
            return null;
        }

        if(this.survivalStats == null) {
            super.getSurvivalStats();
            HashMap<String, L4D2Map> mapsHash = new HashMap<String, L4D2Map>();
            for(XMLData mapData : this.xmlData.getChildren("stats", "survival", "maps")) {
                mapsHash.put(mapData.getName(), new L4D2Map(mapData));
            }
            this.survivalStats.put("maps", mapsHash);
        }

        return this.survivalStats;
    }

    /**
     * Returns a map of <code>L4D2Weapon</code> for this user containing all
     * Left4Dead 2 weapons
     * <p>
     * If the weapons haven't been parsed already, parsing is done now.
     *
     * @return The weapon statistics for this player
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
                if(!weaponName.equals("bilejars") && !weaponName.equals("molotov") && !weaponName.equals("pipes")) {
                    weapon = new L4D2Weapon(weaponData);
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
