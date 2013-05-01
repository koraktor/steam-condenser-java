/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.community.userstats;

import java.util.ArrayList;
import java.util.List;

/**
 * A object which contains all player achievements for a particular game.
 * This data has been retrieved from the GetPlayerAchievements operation in the ISteamUserStats Web API service.
 *
 * @author Lewis Keen
 * @author Sebastian Staudt
 */
public class PlayerAchievements {
	private final String gameName;
	
	private final long steamId;
    private final int appId;
    
    private final String language;

	private final List<String> openAchievements;
	private final List<String> closedAchievements;
	
	/**
	 * Create an immutable object containing all player achievements for a particular game.
	 * 
	 * @param steamId The 64bit SteamID of the player
	 * @param appId The unique Steam Application ID of the game (e.g.
     *        <code>440</code> for Team Fortress 2). See
     *        http://developer.valvesoftware.com/wiki/Steam_Application_IDs for
     *        all application IDs
	 * @param language The ISO639-1 language code for the language all tokenized strings should be returned in, or English if not provided.
	 * @param gameName the full name of this game
	 * @param openAchievements a list of achievement keys that the user has not achieved for this game.
	 * @param closedAchievements a list of achievement keys that the user has achieved for this game.
	 */
	public PlayerAchievements(long steamId, int appId, String language, String gameName, List<String> openAchievements, List<String> closedAchievements) {
		this.steamId = steamId;
		this.appId = appId;
		this.gameName = gameName;
		this.language = language;
		this.openAchievements = new ArrayList<String>(openAchievements);
		this.closedAchievements = new ArrayList<String>(closedAchievements);
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
     * Returns the full name of this game
     *
     * @return The full name of this game
     */
	public String getGameName() {
		return gameName;
	}
	
	/**
	 * Returns the list of achievement keys that the user has not achieved for this game.
	 * 
	 * @return the list of achievement keys that the user has not achieved for this game.
	 */
	public List<String> getOpenAchievements() {
		return openAchievements;
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

	/**
	 * Returns the ISO639-1 language for the achievements.
	 * 
	 * @return the ISO639-1 language for the achievements.
	 */
	public String getLanguage() {
		return language;
	}
}
