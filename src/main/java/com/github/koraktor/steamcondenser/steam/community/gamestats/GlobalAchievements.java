/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.community.gamestats;

import java.util.Map;
import java.util.TreeMap;

/**
 * Represents the achievements for a game, and the percentage of people that own the game which have achieved the achievement.
 *
 * @author Lewis Keen
 * @author Sebastian Staudt
 */
public class GlobalAchievements {
	private final int appId;
	
	private final Map<String, Double> percentages;
	
	/**
	 * Creates an immutable object which contains the percentage of complete achievements for a game
	 * 
	 * @param appId The unique Steam Application ID of the game (e.g.
     *        <code>440</code> for Team Fortress 2). See
     *        http://developer.valvesoftware.com/wiki/Steam_Application_IDs for
     *        all application IDs
	 * @param percentages a map containing the percentages for each achievement in this game
	 */
	public GlobalAchievements(int appId, Map<String, Double> percentages) {
		this.appId = appId;
		this.percentages = new TreeMap<String, Double>(percentages);
	}

    /**
     * Returns the Steam Application ID of the game these achievements belong to
     *
     * @return The application ID of the game these achievements belong to
     */
	public int getAppId() {
		return appId;
	}

	/**
	 * Retrieve all percentages for each achievement for this game
	 * 
	 * @return A map containing the percentages for each achievement in this game
	 */
	public Map<String, Double> getPercentages() {
		return percentages;
	}

	/**
	 * Retrieve a percentage for one achievement for this game
	 * 
	 * @param achievementName the unique key used for the achievement required
	 * @return the percentage of people that own the game which have achieved the achievement.
	 */
	public Double getPercentageForAchievement(String achievementName) {
		return percentages.get(achievementName);
	}
}
