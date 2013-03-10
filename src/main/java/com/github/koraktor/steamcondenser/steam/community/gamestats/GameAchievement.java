/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.community.gamestats;

/**
 * Represents a specific achievement for a game.
 *
 * @author Lewis Keen
 * @author Sebastian Staudt
 */
public class GameAchievement extends GameStat {
	private final String description;
	
    private final String iconOpenUrl;
    private final String iconClosedUrl;

    private final boolean hidden;
	
	/**
	 * Creates an immutable object representing an achievement for a game.
	 * 
	 * @param key The unique identifier for the achievement in this game
	 * @param defaultValue The default value of the achievement
	 * @param displayName The user-friendly name of the achievement
	 * @param description The description of the achievement
	 * @param iconOpenUrl The URL of the icon to be displayed if the achievement is not achieved.
	 * @param iconClosedUrl The URL of the icon to be displayed if the achievement is not achieved.
	 * @param hidden Indicates whether the achievement is hidden or not. 
	 */
	public GameAchievement(String key, int defaultValue, String displayName, String description, String iconOpenUrl, String iconClosedUrl, boolean hidden) {
		super(key, defaultValue, displayName);
		
		this.description = description;
		this.iconOpenUrl = iconOpenUrl;
		this.iconClosedUrl = iconClosedUrl;
		this.hidden = hidden;
	}

	/**
	 * Returns the description of the achievement
	 * 
	 * @return The description of the achievement
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the URL of the icon to be displayed if the achievement is not achieved.
	 * 
	 * @return The URL of the icon to be displayed if the achievement is not achieved.
	 */
	public String getIconOpenUrl() {
		return iconOpenUrl;
	}

	/**
	 * Returns the URL of the icon to be displayed if the achievement is not achieved.
	 * 
	 * @return The URL of the icon to be displayed if the achievement is not achieved.
	 */
	public String getIconClosedUrl() {
		return iconClosedUrl;
	}

	/**
	 * Indicates whether the achievement is hidden or not.
	 *  
	 * @return whether the achievement is hidden or not.
	 */
	public boolean isHidden() {
		return hidden;
	}
}
