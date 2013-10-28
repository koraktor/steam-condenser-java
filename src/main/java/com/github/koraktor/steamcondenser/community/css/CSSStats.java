/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community.css;

import java.util.HashMap;
import java.util.Map;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.community.GameStats;
import com.github.koraktor.steamcondenser.community.XMLData;

/**
 * The is class represents the game statistics for a single user in
 * Counter-Strike: Source
 *
 * @author Sebastian Staudt
 */
public class CSSStats extends GameStats {

    /**
     * The names of the maps in Counter-Strike: Source
     */
    private static final String[] MAPS = { "cs_assault", "cs_compound",
           "cs_havana", "cs_italy", "cs_militia", "cs_office", "de_aztec",
           "de_cbble", "de_chateau", "de_dust", "de_dust2", "de_inferno",
           "de_nuke", "de_piranesi", "de_port", "de_prodigy", "de_tides",
           "de_train" } ;

    /**
     * The names of the weapons in Counter-Strike: Source
     */
    private static final String[] WEAPONS = { "deagle", "usp", "glock", "p228",
            "elite", "fiveseven", "awp", "ak47", "m4a1", "aug", "sg552",
            "sg550", "galil", "famas", "scout", "g3sg1", "p90", "mp5navy",
            "tmp", "mac10", "ump45", "m3", "xm1014", "m249", "knife",
            "grenade" };

    private Map<String, Object> lastMatchStats;

    private Map<String, CSSMap> mapStats;

    private Map<String, Object> totalStats;

    private Map<String, CSSWeapon> weaponStats;

    /**
     * Creates a <code>CSSStats</code> instance by calling the super
     * constructor with the game name <code>"cs:s"</code>
     *
     * @param steamId The custom URL or 64bit Steam ID of the user
     * @throws SteamCondenserException if an error occurs while fetching the
     *         stats data
     */
    public CSSStats(Object steamId) throws SteamCondenserException {
        super(steamId, "cs:s");

        if(this.isPublic()); {
            XMLData lastMatchStats = this.xmlData.getElement("stats", "lastmatch");
            XMLData lifetimeStats = this.xmlData.getElement("stats", "lifetime");
            XMLData summaryStats = this.xmlData.getElement("stats", "summary");

            this.lastMatchStats = new HashMap<String, Object>();
            this.totalStats     = new HashMap<String, Object>();

            this.lastMatchStats.put("costPerKill", lastMatchStats.getFloat("costkill"));
            this.lastMatchStats.put("ctWins", lastMatchStats.getInteger("ct_wins"));
            this.lastMatchStats.put("damage", lastMatchStats.getInteger("dmg"));
            this.lastMatchStats.put("deaths", lastMatchStats.getInteger("deaths"));
            this.lastMatchStats.put("dominations", lastMatchStats.getInteger("dominations"));
            this.lastMatchStats.put("favoriteWeaponId", lastMatchStats.getInteger("favwpnid"));
            this.lastMatchStats.put("kills", lastMatchStats.getInteger("kills"));
            this.lastMatchStats.put("maxPlayers", lastMatchStats.getInteger("max_players"));
            this.lastMatchStats.put("money", lastMatchStats.getInteger("money"));
            this.lastMatchStats.put("revenges", lastMatchStats.getInteger("revenges"));
            this.lastMatchStats.put("stars", lastMatchStats.getInteger("stars"));
            this.lastMatchStats.put("tWins", lastMatchStats.getInteger("t_wins"));
            this.lastMatchStats.put("wins", lastMatchStats.getInteger("wins"));
            this.totalStats.put("blindKills", lifetimeStats.getInteger("blindkills"));
            this.totalStats.put("bombsDefused", lifetimeStats.getInteger("bombsdefused"));
            this.totalStats.put("bombsPlanted", lifetimeStats.getInteger("bombsplanted"));
            this.totalStats.put("damage", lifetimeStats.getInteger("dmg"));
            this.totalStats.put("deaths", summaryStats.getInteger("deaths"));
            this.totalStats.put("dominationOverkills", lifetimeStats.getInteger("dominationoverkills"));
            this.totalStats.put("dominations", lifetimeStats.getInteger("dominations"));
            this.totalStats.put("earnedMoney", lifetimeStats.getInteger("money"));
            this.totalStats.put("enemyWeaponKills", lifetimeStats.getInteger("enemywpnkills"));
            this.totalStats.put("headshots", lifetimeStats.getInteger("headshots"));
            this.totalStats.put("hits", summaryStats.getInteger("shotshit"));
            this.totalStats.put("hostagesRescued", lifetimeStats.getInteger("hostagesrescued"));
            this.totalStats.put("kills", summaryStats.getInteger("kills"));
            this.totalStats.put("knifeKills", lifetimeStats.getInteger("knifekills"));
            this.totalStats.put("logosSprayed", lifetimeStats.getInteger("decals"));
            this.totalStats.put("nightvisionDamage", lifetimeStats.getInteger("nvgdmg"));
            this.totalStats.put("pistolRoundsWon", lifetimeStats.getInteger("pistolrounds"));
            this.totalStats.put("revenges", lifetimeStats.getInteger("revenges"));
            this.totalStats.put("roundsPlayed", summaryStats.getInteger("rounds"));
            this.totalStats.put("roundsWon", summaryStats.getInteger("wins"));
            this.totalStats.put("secondsPlayed", summaryStats.getInteger("timeplayed"));
            this.totalStats.put("shots", summaryStats.getInteger("shots"));
            this.totalStats.put("stars", summaryStats.getInteger("stars"));
            this.totalStats.put("weaponsDonated", lifetimeStats.getInteger("wpndonated"));
            this.totalStats.put("windowsBroken", lifetimeStats.getInteger("winbroken"));
            this.totalStats.put("zoomedSniperKills", lifetimeStats.getInteger("zsniperkills"));

            if((Integer) this.lastMatchStats.get("deaths") > 0) {
                this.lastMatchStats.put("kdratio", ((Integer) this.lastMatchStats.get("kills")).floatValue() / (Integer) this.lastMatchStats.get("deaths"));
            } else {
                this.lastMatchStats.put("kdratio", 0);
            }
            if((Integer) this.totalStats.get("shots") > 0) {
                this.totalStats.put("accuracy", ((Integer) this.totalStats.get("hits")).floatValue() / (Integer) this.totalStats.get("shots"));
            } else {
                this.totalStats.put("accuracy", 0);
            }
            if((Integer) this.totalStats.get("deaths") > 0) {
                this.totalStats.put("kdratio", ((Integer) this.totalStats.get("kills")).floatValue() / (Integer) this.totalStats.get("deaths"));
            } else {
                this.totalStats.put("kdratio", 0);
            }
            this.totalStats.put("roundsLost", ((Integer) this.totalStats.get("roundsPlayed")).floatValue() - (Integer) this.totalStats.get("roundsWon"));
        }
    }

    /**
     * Returns statistics about the last match the player played
     *
     * @return The stats of the last match
     */
    public Map<String, Object> getLastMatchStats() {
        return this.lastMatchStats;
    }

    /**
     * Returns a map of <code>CSSMap</code> for this user containing all
     * CS:S maps.
     * <p>
     * If the maps haven't been parsed already, parsing is done now.
     *
     * @return The map statistics for this user
    */
    public Map<String, CSSMap> getMapStats() {
        if(!this.isPublic()) {
            return null;
        }

        if(this.mapStats == null) {
            this.mapStats = new HashMap<String, CSSMap>();
            XMLData mapsData = this.xmlData.getElement("stats", "maps");

            for(String mapName : MAPS) {
                this.mapStats.put(mapName, new CSSMap(mapName, mapsData));
            }
        }

        return this.mapStats;
    }

    /**
     * Returns overall statistics of this player
     *
     * @return The overall statistics
     */
    public Map<String, Object> getTotalStats() {
        return this.totalStats;
    }

    /**
     * Returns a map of <code>CSSWeapon</code> for this user containing all
     * CS:S weapons.
     * <p>
     * If the weapons haven't been parsed already, parsing is done now.
     *
     * @return The weapon statistics for this user
    */
    public Map<String, CSSWeapon> getWeaponStats() {
        if(!this.isPublic()) {
            return null;
        }

        if(this.weaponStats == null) {
            this.weaponStats = new HashMap<String, CSSWeapon>();
            XMLData weaponData = this.xmlData.getElement("stats", "weapons");

            for(String weaponName : WEAPONS) {
                this.weaponStats.put(weaponName, new CSSWeapon(weaponName, weaponData));
            }
        }

        return this.weaponStats;
    }

}
