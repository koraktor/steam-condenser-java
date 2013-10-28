/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community.alien_swarm;

import java.util.HashMap;
import java.util.Map;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.community.GameStats;
import com.github.koraktor.steamcondenser.community.XMLData;

/**
 * This class represents the game statistics for a single user in Alien Swarm
 *
 * @author Sebastian Staudt
 */
public class AlienSwarmStats extends GameStats {

    /**
     * The base URL for all images referenced in the stats
     */
    public static final String BASE_URL = "http://steamcommunity.com/public/images/gamestats/swarm/";

    /**
     * The names of all weapons in Alien Swarm
     */
    private static final String[] WEAPONS = { "Autogun", "Cannon_Sentry", "Chainsaw",
        "Flamer", "Grenade_Launcher", "Hand_Grenades", "Hornet_Barrage",
        "Incendiary_Sentry", "Laser_Mines", "Marskman_Rifle", "Minigun",
        "Mining_Laser", "PDW", "Pistol", "Prototype_Rifle", "Rail_Rifle",
        "Rifle", "Rifle_Grenade", "Sentry_Gun", "Shotgun", "Tesla_Cannon",
        "Vindicator", "Vindicator_Grenade" };

    private Map<String, Object> favorites;

    private Map<String, Object> itemStats;

    private Map<String, Object> lifetimeStats;

    private Map<String, Object> missionStats;

    private Map<String, Object> weaponStats;

    /**
     * Creates a new <code>AlienSwarmStats</code> instance by calling the super
     * constructor with the game name <code>"alienswarm"</code>
     *
     * @param steamId The custom URL or the 64bit Steam ID of the user
     * @throws SteamCondenserException if an error occurs while fetching the
     *         stats data
     */
    public AlienSwarmStats(Object steamId) throws SteamCondenserException {
        super(steamId, "alienswarm");

        if(this.isPublic()) {
            XMLData lifetimeStats = this.xmlData.getElement("stats", "lifetime");
            this.hoursPlayed = lifetimeStats.getString("timeplayed");

            this.lifetimeStats = new HashMap<String, Object>();
            this.lifetimeStats.put("accuracy", lifetimeStats.getFloat("accuracy"));
            this.lifetimeStats.put("aliensBurned", lifetimeStats.getInteger("aliensburned"));
            this.lifetimeStats.put("aliensKilled", lifetimeStats.getInteger("alienskilled"));
            this.lifetimeStats.put("campaigns", lifetimeStats.getInteger("campaigns"));
            this.lifetimeStats.put("damageTaken", lifetimeStats.getInteger("damagetaken"));
            this.lifetimeStats.put("experience", lifetimeStats.getInteger("experience"));
            this.lifetimeStats.put("experienceRequired", lifetimeStats.getInteger("xprequired"));
            this.lifetimeStats.put("fastHacks", lifetimeStats.getInteger("fasthacks"));
            this.lifetimeStats.put("friendlyFire", lifetimeStats.getInteger("friendlyfire"));
            this.lifetimeStats.put("gamesSuccessful", lifetimeStats.getInteger("gamessuccess"));
            this.lifetimeStats.put("healing", lifetimeStats.getInteger("healing"));
            this.lifetimeStats.put("killsPerHour", lifetimeStats.getFloat("killsperhour"));
            this.lifetimeStats.put("level", lifetimeStats.getInteger("level"));
            this.lifetimeStats.put("promotion", lifetimeStats.getInteger("promotion"));
            this.lifetimeStats.put("nextUnlock", lifetimeStats.getString("nextunlock"));
            this.lifetimeStats.put("nextUnlockImg", BASE_URL + lifetimeStats.getString("nextunlockimg"));
            this.lifetimeStats.put("shotsFired", lifetimeStats.getInteger("shotsfired"));
            this.lifetimeStats.put("totalGames", lifetimeStats.getInteger("totalgames"));

            if((Integer) this.lifetimeStats.get("promotion") > 0) {
               this.lifetimeStats.put("promotionImg", BASE_URL + lifetimeStats.getString("promotionpic"));
            }

            this.lifetimeStats.put("games_successful_percentage", ((Integer) this.lifetimeStats.get("totalGames") > 0) ? ((Integer) this.lifetimeStats.get("gamesSuccessful")).floatValue() / (Integer) this.lifetimeStats.get("totalGames") : 0);
        }
    }

    /**
     * Returns the favorites of this user like weapons and marine
     * <p>
     * If the favorites haven't been parsed already, parsing is done now.
     *
     * @return The favorites of this player
     */
    public Map<String, Object> getFavorites() {
        if(!this.isPublic()) {
            return null;
        }

        if(this.favorites == null) {
            XMLData favoritesData = this.xmlData.getElement("stats", "favorites");

            this.favorites = new HashMap<String, Object>();
            this.favorites.put("class", favoritesData.getString("class"));
            this.favorites.put("classImg", favoritesData.getString("classimg"));
            this.favorites.put("classPercentage", favoritesData.getFloat("classpct"));
            this.favorites.put("difficulty", favoritesData.getString("difficulty"));
            this.favorites.put("difficultyPercentage", favoritesData.getFloat("difficultypct"));
            this.favorites.put("extra", favoritesData.getString("extra"));
            this.favorites.put("extraImg", favoritesData.getString("extraimg"));
            this.favorites.put("extraPercentage", favoritesData.getFloat("extrapct"));
            this.favorites.put("marine", favoritesData.getString("marine"));
            this.favorites.put("marineImg", favoritesData.getString("marineimg"));
            this.favorites.put("marinePercentage", favoritesData.getFloat("marinepct"));
            this.favorites.put("mission", favoritesData.getString("mission"));
            this.favorites.put("missionImg", favoritesData.getString("missionimg"));
            this.favorites.put("missionPercentage", favoritesData.getFloat("missionpct"));
            this.favorites.put("primaryWeapon", favoritesData.getString("primary"));
            this.favorites.put("primaryWeaponImg", favoritesData.getString("primaryimg"));
            this.favorites.put("primaryWeaponPercentage", favoritesData.getFloat("primarypct"));
            this.favorites.put("secondaryWeapon", favoritesData.getString("secondary"));
            this.favorites.put("secondaryWeaponImg", favoritesData.getString("secondaryimg"));
            this.favorites.put("secondaryWeapon_Percentage", favoritesData.getFloat("secondarypct"));
        }

        return this.favorites;
    }

    /**
     * Returns the item stats for this user like ammo deployed and medkits
     * used
     * <p>
     * If the items haven't been parsed already, parsing is done now.
     *
     * @return The item stats of this player
     */
    public Map<String, Object> getItemStats() {
        if(!this.isPublic()) {
            return null;
        }

        if(this.itemStats == null) {
            XMLData itemStatsData = this.xmlData.getElement("stats", "weapons");

            this.itemStats = new HashMap<String, Object>();
            this.itemStats.put("ammoDeployed", itemStatsData.getInteger("ammo_deployed"));
            this.itemStats.put("sentrygunsDeployed", itemStatsData.getInteger("sentryguns_deployed"));
            this.itemStats.put("sentryFlamersDeployed", itemStatsData.getInteger("sentry_flamers_deployed"));
            this.itemStats.put("sentryFreezeDeployed", itemStatsData.getInteger("sentry_freeze_deployed"));
            this.itemStats.put("sentryCannonDeployed", itemStatsData.getInteger("sentry_cannon_deployed"));
            this.itemStats.put("medkitsUsed", itemStatsData.getInteger("medkits_used"));
            this.itemStats.put("flaresUsed", itemStatsData.getInteger("flares_used"));
            this.itemStats.put("adrenalineUsed", itemStatsData.getInteger("adrenaline_used"));
            this.itemStats.put("teslaTrapsDeployed", itemStatsData.getInteger("tesla_traps_deployed"));
            this.itemStats.put("freezeGrenadesThrown", itemStatsData.getInteger("freeze_grenades_thrown"));
            this.itemStats.put("electricArmorUsed", itemStatsData.getInteger("electric_armor_used"));
            this.itemStats.put("healgunHeals", itemStatsData.getInteger("healgun_heals"));
            this.itemStats.put("healgunHealsSelf", itemStatsData.getInteger("healgun_heals_self"));
            this.itemStats.put("healbeaconHeals", itemStatsData.getInteger("healbeacon_heals"));
            this.itemStats.put("healbeaconHealsSelf", itemStatsData.getInteger("healbeacon_heals_self"));
            this.itemStats.put("damageAmpsUsed", itemStatsData.getInteger("damage_amps_used"));
            this.itemStats.put("healbeaconsDeployed", itemStatsData.getInteger("healbeacons_deployed"));
            this.itemStats.put("healbeaconHealsPct", itemStatsData.getFloat("healbeacon_heals_pct"));
            this.itemStats.put("healgunHealsPct", itemStatsData.getFloat("healgun_heals_pct"));
            this.itemStats.put("healbeaconHealsPctSelf", itemStatsData.getFloat("healbeacon_heals_pct_self"));
            this.itemStats.put("healgunHealsPctSelf", itemStatsData.getFloat("healgun_heals_pct_self"));
        }

        return this.itemStats;
    }

    /**
     * Returns general stats for the players
     *
     * @return The stats for the player
     */
    public Map<String, Object> getLifetimeStats() {
        return this.lifetimeStats;
    }

    /**
     * Returns the stats for individual missions for this user containing all
     * Alien Swarm missions
     * <p>
     * If the mission stats haven't been parsed already, parsing is done now.
     *
     * @return The mission stats for this player
     */
    public Map<String, Object> getMissionStats() {
        if(!this.isPublic()) {
            return null;
        }

        if(this.missionStats == null) {
            this.missionStats = new HashMap<String, Object>();
            for(XMLData missionData : this.xmlData.getElements("stats", "missions")) {
                this.missionStats.put(missionData.getName(), new AlienSwarmMission(missionData));
            }
        }

        return this.missionStats;
    }

    /**
     * Returns the stats for individual weapons for this user containing all
     * Alien Swarm weapons
     * <p>
     * If the weapon stats haven't been parsed already, parsing is done now.
     *
     * @return The weapon stats for this player
     */
    public Map<String, Object> getWeaponStats() {
        if(!this.isPublic()) {
            return null;
        }

        if(this.weaponStats == null) {
            this.weaponStats = new HashMap<String, Object>();
            for(String weaponNode : WEAPONS) {
                XMLData weaponData = this.xmlData.getElement("stats", "weapons", weaponNode);
                AlienSwarmWeapon weapon = new AlienSwarmWeapon(weaponData);
                this.weaponStats.put(weapon.getName(), weapon);
            }
        }

        return this.weaponStats;
    }

}
