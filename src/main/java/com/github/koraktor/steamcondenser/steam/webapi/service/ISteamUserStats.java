/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.webapi.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.exceptions.WebApiException;
import com.github.koraktor.steamcondenser.steam.community.WebApi;
import com.github.koraktor.steamcondenser.steam.community.gamestats.GameStatsSchema;
import com.github.koraktor.steamcondenser.steam.community.gamestats.GlobalAchievements;
import com.github.koraktor.steamcondenser.steam.community.userstats.PlayerAchievements;
import com.github.koraktor.steamcondenser.steam.community.userstats.UserStats;
import com.github.koraktor.steamcondenser.steam.webapi.builder.UserStatsBuilder;

/**
 * A service class which calls the operations supplied in the ISteamUserStats Web API service.
 *
 * @author Lewis Keen
 * @author Sebastian Staudt
 */
public class ISteamUserStats {
	private static final String APPID = "appid";
	private static final String I_STEAM_USER_STATS = "ISteamUserStats";

	private final UserStatsBuilder userStatsBuilder;
	private Map<Integer, GameStatsSchema> gameStatsSchemasCache = new HashMap<Integer, GameStatsSchema>();
	private Map<Integer, GlobalAchievements> globalAchievementPercentagesForAppCache = new HashMap<Integer, GlobalAchievements>();
	
	/**
	 * Creates a new instance of the ISteamUserStats Web API service object
	 * 
	 * @param userStatsBuilder the user stats builder used to create object representations of the operation responses
	 */
	public ISteamUserStats(UserStatsBuilder userStatsBuilder) {
		this.userStatsBuilder = userStatsBuilder;
	}
	
	/**
	 * Get the global percentages for each achievement for a particular app
	 * 
	 * @param appId The unique Steam Application ID of the game (e.g.
     *        <code>440</code> for Team Fortress 2). See
     *        http://developer.valvesoftware.com/wiki/Steam_Application_IDs for
     *        all application IDs
	 * @return An object representation of the Global Achievements of a particular app.
	 * @throws WebApiException if there is a general service error (e.g. no key supplied)
	 * @throws JSONException if the JSON returned from the service is malformed.
	 */
	public GlobalAchievements getGlobalAchievementPercentagesForApp(int appId) throws WebApiException, JSONException {
		if(globalAchievementPercentagesForAppCache.containsKey(appId)) {
			return globalAchievementPercentagesForAppCache.get(appId);
		}else{
			Map<String, Object> params = new HashMap<String,Object>();
			params.put("gameId", Integer.toString(appId));
			JSONObject data = WebApi.getJSONResponse(I_STEAM_USER_STATS, "GetGlobalAchievementPercentagesForApp", 2, params);
			GlobalAchievements globalAchievements = userStatsBuilder.buildGlobalAchievements(appId, data);
			globalAchievementPercentagesForAppCache.put(appId, globalAchievements);
			return globalAchievements;
		}
	}

	/**
	 * Get the number of players currently playing a particular app
	 * 
	 * @param appId The unique Steam Application ID of the game (e.g.
     *        <code>440</code> for Team Fortress 2). See
     *        http://developer.valvesoftware.com/wiki/Steam_Application_IDs for
     *        all application IDs
	 * @return the number of players currently playing the supplied app.
	 * @throws WebApiException if there is a general service error (e.g. no key supplied)
	 * @throws JSONException if the JSON returned from the service is malformed.
	 */
	public int getNumberOfCurrentPlayers(int appId) throws WebApiException, JSONException {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(APPID, Integer.toString(appId));
		JSONObject data = WebApi.getJSONResponse(I_STEAM_USER_STATS, "GetNumberOfCurrentPlayers", 1, params);
		return data.getJSONObject("response").getInt("player_count");
	}

	/**
	 * Get all player achievements for a particular app, both open and closed.
	 * 
	 * @param steamId The 64bit SteamID of the player
	 * @param appId The unique Steam Application ID of the game (e.g.
     *        <code>440</code> for Team Fortress 2). See
     *        http://developer.valvesoftware.com/wiki/Steam_Application_IDs for
     *        all application IDs
	 * @return an object which holds the list of open and closed achievements
	 * @throws WebApiException if there is a general service error (e.g. no key supplied)
	 * @throws JSONException if the JSON returned from the service is malformed.
	 */
	public PlayerAchievements getPlayerAchievements(long steamId, int appId) throws WebApiException, JSONException {
		return getPlayerAchievements(steamId, appId, null);
	}

	/**
	 * Get all player achievements for a particular app, both open and closed. Achievement strings are returned in the supplied language.
	 * 
	 * @param steamId The 64bit SteamID of the player
	 * @param appId The unique Steam Application ID of the game (e.g.
     *        <code>440</code> for Team Fortress 2). See
     *        http://developer.valvesoftware.com/wiki/Steam_Application_IDs for
     *        all application IDs
	 * @param language The ISO639-1 language code for the language all tokenized strings should be returned in, or English if not provided.
	 * @return an object which holds the list of open and closed achievements
	 * @throws WebApiException if there is a general service error (e.g. no key supplied)
	 * @throws JSONException if the JSON returned from the service is malformed.
	 */
	public PlayerAchievements getPlayerAchievements(long steamId, int appId, String language) throws WebApiException, JSONException {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("steamid", Long.toString(steamId));
		params.put(APPID, Integer.toString(appId));
		if (language != null && language.length() > 0) {
			params.put("l", language);
		}
		JSONObject data = WebApi.getJSONResponse(I_STEAM_USER_STATS, "GetPlayerAchievements", 1, params);
		return userStatsBuilder.buildPlayerAchievements(steamId, appId, language, data);
	}

	/**
	 * Retrieves details of all stats and achievements for a particular game.
	 * 
	 * @param appId The unique Steam Application ID of the game (e.g.
     *        <code>440</code> for Team Fortress 2). See
     *        http://developer.valvesoftware.com/wiki/Steam_Application_IDs for
     *        all application IDs
	 * @return An object which contains all stats and achievements for a particular game.
	 * @throws JSONException if the JSON returned from the service is malformed.
	 * @throws SteamCondenserException if there is a general service error (e.g. no key supplied), or if 
	 * 		   there is no stats or achievements for this game.
	 */
	public GameStatsSchema getSchemaForGame(int appId) throws JSONException, SteamCondenserException {
		return getSchemaForGame(appId, null);
	}

	/**
	 * Retrieves details of all stats and achievements for a particular game. Stats and achievement strings are returned in the supplied language.
	 * 
	 * @param appId The unique Steam Application ID of the game (e.g.
     *        <code>440</code> for Team Fortress 2). See
     *        http://developer.valvesoftware.com/wiki/Steam_Application_IDs for
     *        all application IDs
	 * @param language The ISO639-1 language code for the language all tokenized strings should be returned in, or English if not provided.
	 * @return An object which contains all stats and achievements for a particular game.
	 * @throws JSONException if the JSON returned from the service is malformed.
	 * @throws SteamCondenserException if there is a general service error (e.g. no key supplied), or if 
	 * 		   there is no stats or achievements for this game.
	 */
	public GameStatsSchema getSchemaForGame(int appId, String language) throws JSONException, SteamCondenserException {
		if (gameStatsSchemasCache.containsKey(appId)) {
			return gameStatsSchemasCache.get(appId);
		} else {
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(APPID, Integer.toString(appId));
			if (language != null && language.length() > 0) {
				params.put("l", language);
			}
			JSONObject data = WebApi.getJSONResponse(I_STEAM_USER_STATS, "GetSchemaForGame", 2, params);
			GameStatsSchema gameStatsSchema = userStatsBuilder.buildSchemaForGame(appId, language, data);
			gameStatsSchemasCache.put(appId, gameStatsSchema);
			return gameStatsSchema;
		}
	}

	/**
	 * Get all user stats and closed achievements for a particular game. 
	 * 
	 * @param steamId The 64bit SteamID of the player
	 * @param appId The unique Steam Application ID of the game (e.g.
     *        <code>440</code> for Team Fortress 2). See
     *        http://developer.valvesoftware.com/wiki/Steam_Application_IDs for
     *        all application IDs
	 * @return an object which contain all user stats and closed achievements.
	 * @throws WebApiException if there is a general service error (e.g. no key supplied)
	 * @throws JSONException if the JSON returned from the service is malformed.
	 */
	public UserStats getUserStatsForGame(long steamId, int appId) throws WebApiException, JSONException {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("steamid", Long.toString(steamId));
		params.put(APPID, Integer.toString(appId));
		JSONObject data = WebApi.getJSONResponse(I_STEAM_USER_STATS, "GetUserStatsForGame", 2, params);
		return userStatsBuilder.buildUserStatsForGame(steamId, appId, data);
	}
	
	/**
	 * Clears the game stats schema cache.
	 */
	public void clearGameStatsSchemaCache() {
		gameStatsSchemasCache.clear();
	}

	/**
	 * Clear the global achievement perecentages cache.
	 */
	public void clearGlobalAchievementPercentagesForAppCache() {
		globalAchievementPercentagesForAppCache.clear();
	}
}
