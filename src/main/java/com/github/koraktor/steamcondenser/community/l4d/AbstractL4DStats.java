/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community.l4d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.community.GameStats;
import com.github.koraktor.steamcondenser.community.GameWeapon;
import com.github.koraktor.steamcondenser.community.XMLData;

/**
 * This abstract class is a base class for statistics for Left4Dead and
 * Left4Dead 2. As both games have more or less the same statistics available
 * in the Steam Community the code for both is pretty much the same.
 *
 * @author Sebastian Staudt
 */
public abstract class AbstractL4DStats extends GameStats {

    protected HashMap<String, Object> favorites;

    protected HashMap<String, Object> lifetimeStats;

    protected HashMap<String, Object> mostRecentGame;

    protected HashMap<String, Object> survivalStats;

    protected HashMap<String, Object> teamplayStats;

    protected HashMap<String, Object> versusStats;

    protected HashMap<String, GameWeapon> weaponStats;

    /**
     * Creates a new instance of statistics for both, Left4Dead and Left4Dead 2
     * parsing basic common data
     *
     * @param steamId The custom URL or 64bit Steam ID of the user
     * @param gameName The name of the game
     * @throws SteamCondenserException if an error occurs while fetching the
     *         stats data
     */
    public AbstractL4DStats(Object steamId, String gameName)
            throws SteamCondenserException {
        super(steamId, gameName);

        if(this.isPublic()) {
            XMLData mostRecentGameNode = this.xmlData.getElement("stats", "mostRecentGame");
            this.mostRecentGame = new HashMap<String, Object>();
            if(mostRecentGameNode != null) {
                this.mostRecentGame.put("difficulty", mostRecentGameNode.getString("difficulty"));
                this.mostRecentGame.put("escaped", mostRecentGameNode.getString("bEscaped").equals("1"));
                this.mostRecentGame.put("movie", mostRecentGameNode.getString("movie"));
                this.mostRecentGame.put("timePlayed", mostRecentGameNode.getString("time"));
            }
        }
    }

    /**
     * Returns a map of favorites for this user like weapons and character
     * <p>
     * If the favorites haven't been parsed already, parsing is done now.
     *
     * @return The favorites of this user
     */
    public Map<String, Object> getFavorites() {
        if(!this.isPublic()) {
            return null;
        }

        if(this.favorites == null) {
            XMLData favoritesNode = this.xmlData.getElement("stats", "favorites");
            this.favorites = new HashMap<String, Object>();
            this.favorites.put("campaign", favoritesNode.getString("campaign"));
            this.favorites.put("campaignPercentage", favoritesNode.getInteger("campaignpct"));
            this.favorites.put("character", favoritesNode.getString("character"));
            this.favorites.put("characterPercentage", favoritesNode.getInteger("characterpct"));
            this.favorites.put("level1Weapon", favoritesNode.getString("weapon1"));
            this.favorites.put("level1WeaponPercentage", favoritesNode.getInteger("weapon1pct"));
            this.favorites.put("level2Weapon", favoritesNode.getString("weapon2"));
            this.favorites.put("level2WeaponPercentage", favoritesNode.getInteger("weapon2pct"));
        }

        return this.favorites;
    }

    /**
     * Returns a map of lifetime statistics for this user like the time played
     * <p>
     * If the lifetime statistics haven't been parsed already, parsing is done
     * now.
     *
     * @return The lifetime statistics for this user
     */
    public Map<String, Object> getLifetimeStats() {
        if(!this.isPublic()) {
            return null;
        }

        if(this.lifetimeStats == null) {
            XMLData lifetimeStatsElement = this.xmlData.getElement("stats", "lifetime");
            this.lifetimeStats = new HashMap<String, Object>();
            this.lifetimeStats.put("finalesSurvived", lifetimeStatsElement.getInteger("finales"));
            this.lifetimeStats.put("gamesPlayed", lifetimeStatsElement.getInteger("gamesplayed"));
            this.lifetimeStats.put("finalesSurvivedPercentage", ((Integer) this.lifetimeStats.get("finalesSurvived")).floatValue() / (Integer) this.lifetimeStats.get("gamesPlayed"));
            this.lifetimeStats.put("infectedKilled", lifetimeStatsElement.getInteger("infectedkilled"));
            this.lifetimeStats.put("killsPerHour", lifetimeStatsElement.getFloat("killsperhour"));
            this.lifetimeStats.put("avgKitsShared", lifetimeStatsElement.getFloat("kitsshared"));
            this.lifetimeStats.put("avgKitsUsed", lifetimeStatsElement.getFloat("kitsused"));
            this.lifetimeStats.put("avgPillsShared", lifetimeStatsElement.getFloat("pillsshared"));
            this.lifetimeStats.put("avgPillsUsed", lifetimeStatsElement.getFloat("pillsused"));
            this.lifetimeStats.put("timePlayed", lifetimeStatsElement.getString("timeplayed"));
        }

        return this.lifetimeStats;
    }

    /**
     * Returns a map of Survival statistics for this user like revived
     * teammates
     * <p>
     * If the Survival statistics haven't been parsed already, parsing is done
     * now.
     *
     * @return The Survival statistics for this user
     */
    public Map<String, Object> getSurvivalStats()
            throws SteamCondenserException {
        if(!this.isPublic()) {
            return null;
        }

        if(this.survivalStats == null) {
            XMLData survivalStatsElement = this.xmlData.getElement("stats", "survival");
            this.survivalStats = new HashMap<String, Object>();
            this.survivalStats.put("goldMedals", survivalStatsElement.getInteger("goldmedals"));
            this.survivalStats.put("silverMedals", survivalStatsElement.getInteger("silvermedals"));
            this.survivalStats.put("bronzeMedals", survivalStatsElement.getInteger("bronzemedals"));
            this.survivalStats.put("roundsPlayed", survivalStatsElement.getInteger("roundsplayed"));
            this.survivalStats.put("bestTime", survivalStatsElement.getFloat("besttime"));

            HashMap<String, L4DMap> mapsHash = new HashMap<String, L4DMap>();
            for(XMLData mapData : survivalStatsElement.getElements("maps")) {
                mapsHash.put(mapData.getName(), new L4DMap(mapData));
            }
            this.survivalStats.put("maps", mapsHash);
        }

        return this.survivalStats;
    }

    /**
     * Returns a map of teamplay statistics for this user like revived
     * teammates
     * <p>
     * If the teamplay statistics haven't been parsed already, parsing is done
     * now.
     *
     * @return The teamplay statistics for this
     */
    public Map<String, Object> getTeamplayStats() {
        if(!this.isPublic()) {
            return null;
        }

        if(this.teamplayStats == null) {
            XMLData teamplayStatsElement = this.xmlData.getElement("stats", "teamplay");
            this.teamplayStats = new HashMap<String, Object>();
            this.teamplayStats.put("revived", teamplayStatsElement.getInteger("revived"));
            this.teamplayStats.put("mostRevivedDifficulty", teamplayStatsElement.getString("reviveddiff"));
            this.teamplayStats.put("avgRevived", teamplayStatsElement.getFloat("revivedavg"));
            this.teamplayStats.put("avgWasRevived", teamplayStatsElement.getFloat("wasrevivedavg"));
            this.teamplayStats.put("protected", teamplayStatsElement.getInteger("protected"));
            this.teamplayStats.put("mostProtectedDifficulty", teamplayStatsElement.getString("protecteddiff"));
            this.teamplayStats.put("avgProtected", teamplayStatsElement.getFloat("protectedavg"));
            this.teamplayStats.put("avgWasProtected", teamplayStatsElement.getFloat("wasprotectedavg"));
            this.teamplayStats.put("friendlyFireDamage", teamplayStatsElement.getInteger("ffdamage"));
            this.teamplayStats.put("mostFriendlyFireDamageDifficulty", teamplayStatsElement.getString("ffdamagediff"));
            this.teamplayStats.put("avgFriendlyFireDamage", teamplayStatsElement.getFloat("ffdamageavg"));
        }

        return this.teamplayStats;
    }

    /**
     * Returns a map of Versus statistics for this user like percentage of
     * rounds won
     * <p>
     * If the Versus statistics haven't been parsed already, parsing is done
     * now.
     *
     * @return The Versus statistics for this user
     */
    public Map<String, Object> getVersusStats() {
        if(!this.isPublic()) {
            return null;
        }

        if(this.versusStats == null) {
            XMLData versusStatsElement = this.xmlData.getElement("stats", "versus");
            this.versusStats = new HashMap<String, Object>();
            this.versusStats.put("gamesPlayed", versusStatsElement.getInteger("gamesplayed"));
            this.versusStats.put("gamesCompleted", versusStatsElement.getInteger("gamescompleted"));
            this.versusStats.put("finalesSurvived", versusStatsElement.getInteger("finales"));
            this.versusStats.put("finalesSurvivedPercentage", ((Integer) this.versusStats.get("finalesSurvived")).floatValue() / (Integer) this.versusStats.get("gamesPlayed"));
            this.versusStats.put("points", versusStatsElement.getInteger("points"));
            this.versusStats.put("mostPointsInfected", versusStatsElement.getString("pointsas"));
            this.versusStats.put("gamesWon", versusStatsElement.getInteger("gameswon"));
            this.versusStats.put("gamesLost", versusStatsElement.getInteger("gameslost"));
            this.versusStats.put("highestSurvivorScore", versusStatsElement.getInteger("survivorscore"));

            ArrayList<String> infectedArray = new ArrayList<String>();
            infectedArray.add("boomer");
            infectedArray.add("hunter");
            infectedArray.add("smoker");
            infectedArray.add("tank");
            for(String infected : infectedArray) {
                HashMap<String, Number> infectedStats = new HashMap<String, Number>();
                infectedStats.put("special", versusStatsElement.getInteger(infected + "special"));
                infectedStats.put("mostDamage", versusStatsElement.getInteger(infected + "dmg"));
                infectedStats.put("avgLifespan", versusStatsElement.getInteger(infected + "lifespan"));
                this.versusStats.put(infected, infectedStats);
            }
        }

        return this.versusStats;
    }
}
