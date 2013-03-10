/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.community.userstats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A object which contains all user stats and closed achievements for a particular game.
 * This data has been retrieved from the GetUserStatsForGame operation in the ISteamUserStats Web API service.
 *
 * @author Lewis Keen
 * @author Sebastian Staudt
 */
public class UserStats {
	private final String gameName;
	
	private final long steamId;
    private final int appId;
    
    private final Map<String, Integer> stats;

	/* 
	 * Only closed achievements are provided in the GetUserStatsForGame operation. For a complete list,
	 * see GetPlayerAchievements operation.
	 */
	private final List<String> closedAchievements;

	/**
	 * Create an immutable object containing all user stats and closed achievements for a particular app.
	 * 
	 * @param steamId The 64bit SteamID of the player
	 * @param appId The unique Steam Application ID of the game (e.g.
     *        <code>440</code> for Team Fortress 2). See
     *        http://developer.valvesoftware.com/wiki/Steam_Application_IDs for
     *        all application IDs
	 * @param gameName the full name of this game
	 * @param stats a map of user stat's for this app.
	 * @param closedAchievements a list of achievement keys that the user has achieved for this app.
	 */
	public UserStats(long steamId, int appId, String gameName, Map<String, Integer> stats, List<String> closedAchievements) {
		this.steamId = steamId;
		this.appId = appId;
		this.gameName = gameName;
		this.stats = new TreeMap<String, Integer>(stats);
		this.closedAchievements = new ArrayList<String>(closedAchievements);
	}

    /**
     * Determines whether the user have any stats for this game
     *
     * @return a boolean that indicates if the user has any stats for this game
     */
	public boolean hasStats() {
		return stats.size() > 0;
	}

    /**
     * Determines whether the user have any closed achievements for this game
     *
     * @return a boolean that indicates if the user has any closed achievements for this game
     */
	public boolean hasAchievements() {
		return closedAchievements.size() > 0;
	}

	/**
	 * Returns a map of game stats for this user.
	 * 
	 * @return a map of game stats for this user.
	 */
	public Map<String, Integer> getStats() {
		return stats;
	}
	
    /**
     * Returns the Steam Application ID of the game these user stats belong to
     *
     * @return The application ID of the game these user stats belong to
     */
	public int getAppId() {
		return appId;
	}

    /**
     * Returns the full name of this game
     *
     * @return The full name of this game
     */
	public String getGameName() {
		return gameName;
	}
	
	/**
	 * Returns the list of achievement keys that the user has achieved for this game.
	 * 
	 * @return the list of achievement keys that the user has achieved for this game.
	 */
	public List<String> getClosedAchievements() {
		return closedAchievements;
	}
	
    /**
     * Returns the 64bit steam ID of the user these achievements belong to
     *
     * @return The 64bit steam ID of the user these achievements belong to
     */
	public long getSteamId() {
		return steamId;
	}
}
