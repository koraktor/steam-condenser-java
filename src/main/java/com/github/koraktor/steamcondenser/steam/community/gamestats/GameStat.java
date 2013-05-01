/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.community.gamestats;

/**
 * Represents a specific stat for a game.
 *
 * @author Lewis Keen
 * @author Sebastian Staudt
 */
public class GameStat {
	private final String key;
	
	private final int defaultValue;
	
	private final String displayName;
	
	/**
	 * Creates an immutable object which contains the details of a game stat
	 * 
	 * @param key The unique identifier for the stat in this game
	 * @param defaultValue The default value of the stat
	 * @param displayName The user-friendly name of the stat
	 */
	public GameStat(String key, int defaultValue, String displayName) {
		this.key = key;
		this.defaultValue = defaultValue;
		this.displayName = displayName;
	}

	/**
	 * Returns the unique identifier for the stat in this game
	 * 
	 * @return The unique identifier for the stat in this game
	 */
	public String getKey() {
		return key;
	}


	/**
	 * Returns the default value of the stat
	 * 
	 * @return The default value of the stat
	 */
	public int getDefaultValue() {
		return defaultValue;
	}


	/**
	 * Returns the user-friendly name of the stat
	 * 
	 * @return  The user-friendly name of the stat
	 */
	public String getDisplayName() {
		return displayName;
	}
}
