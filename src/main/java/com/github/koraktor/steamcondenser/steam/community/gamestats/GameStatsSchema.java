/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.community.gamestats;

import java.util.HashMap;
import java.util.Map;

/**
 * A object which contains all game stats and achievements for a particular game.
 * This data has been retrieved from the GetGameStatsSchema operation in the ISteamUserStats Web API service.
 *
 * @author Lewis Keen
 * @author Sebastian Staudt
 */
public class GameStatsSchema {

	private final int appId;

	private final String language;

	private final String gameName;

	private final int gameVersion;

	private final Map<String, GameStat> stats;
	private final Map<String, GameAchievement> achievements;
	
	private final int visibleAchievements;
	private final int hiddenAchievements;

	/**
	 * Create an immutable object containing all game stats and achievement information for a particular app.
	 * 
	 * @param appId The unique Steam Application ID of the game (e.g.
     *        <code>440</code> for Team Fortress 2). See
     *        http://developer.valvesoftware.com/wiki/Steam_Application_IDs for
     *        all application IDs
	 * @param language The ISO639-1 language code for the language all tokenized strings should be returned in, or English if not provided.
	 * @param gameName The full name of this app
	 * @param gameVersion The version of this app
	 * @param stats A map of stats for this app.
	 * @param achievements A map of user achievements for this app.
	 */
	public GameStatsSchema(int appId, String language, String gameName, int gameVersion, Map<String, GameStat> stats, Map<String, GameAchievement> achievements) {
		this.appId = appId;
		this.language = language;
		this.gameName = gameName;
		this.gameVersion = gameVersion;
		this.stats = new HashMap<String, GameStat>(stats);
		this.achievements = new HashMap<String, GameAchievement>(achievements);
		
		int visibleAchievements = 0, hiddenAchievements = 0;
		for (GameAchievement achievement : this.achievements.values()) {
			if(!achievement.isHidden()) {
				visibleAchievements++;
			}else{
				hiddenAchievements++;
			}
		}
		this.visibleAchievements = visibleAchievements;
		this.hiddenAchievements = hiddenAchievements;
	}

    /**
     * Returns the Steam Application ID of the game this game stats schema belong to
     *
     * @return The application ID of the game these achievements belong to
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
     * Returns the version of this game
     *
     * @return The version of this game
     */
	public int getGameVersion() {
		return gameVersion;
	}

    /**
     * Determines whether the game has any stats
     *
     * @return a boolean that indicates if the game has any stats
     */
	public boolean hasStats() {
		return stats.size() > 0;
	}

    /**
     * Determines whether this game has any achievements
     *
     * @return a boolean that indicates if this game has any achievements
     */
	public boolean hasAchievements() {
		return stats.size() > 0;
	}

	/**
	 * Returns a map of this game's stats.
	 * 
	 * @return a map of this game's stats.
	 */
	public Map<String, GameStat> getStats() {
		return stats;
	}

	/**
	 * Returns a map of this game's achievement.
	 * 
	 * @return a map of this game's achievement.
	 */
	public Map<String, GameAchievement> getAchievements() {
		return achievements;
	}

	/**
	 * Get a particular stat details for the game.
	 * 
	 * @param name The name of the stat to retrieve.
	 * @return An object that represents the requested stat information. null if the stat is not found.
	 */
	public GameStat getStat(String name) {
		return stats.get(name);
	}

	/**
	 * Get a particular achievement details for the game.
	 * 
	 * @param name The name of the achievement to retrieve.
	 * @return An object that represents the requested achievement. null if the achievement is not found.
	 */
	public GameAchievement getAchievement(String name) {
		return achievements.get(name);
	}
	
	/**
	 * Returns the ISO639-1 language for the achievements.
	 * 
	 * @return the ISO639-1 language for the achievements.
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Returns the number of visible achievements
	 * 
	 * @return the number of visible achievements
	 */
	public int getVisibleAchievements() {
		return visibleAchievements;
	}

	/**
	 * Returns the number of hidden achievements
	 * 
	 * @return the number of hidden achievements
	 */
	public int getHiddenAchievements() {
		return hiddenAchievements;
	}
}
