/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.webapi.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.koraktor.steamcondenser.steam.community.gamestats.GameAchievement;
import com.github.koraktor.steamcondenser.steam.community.gamestats.GameStat;
import com.github.koraktor.steamcondenser.steam.community.gamestats.GameStatsSchema;
import com.github.koraktor.steamcondenser.steam.community.gamestats.GlobalAchievements;
import com.github.koraktor.steamcondenser.steam.community.userstats.PlayerAchievements;
import com.github.koraktor.steamcondenser.steam.community.userstats.UserStats;
import com.github.koraktor.steamcondenser.steam.webapi.exceptions.DataException;
import com.github.koraktor.steamcondenser.steam.webapi.exceptions.ParseException;

/**
 * Builder class to create object representations of the ISteamUserStats Web API responses.
 *
 * @author Lewis Keen
 * @author Sebastian Staudt
 */
public class UserStatsBuilder {
	private static final String JSON_ITEM_STATS = "stats";
	private static final String JSON_ITEM_GAME_NAME = "gameName";
	private static final String JSON_ITEM_NAME = "name";
	private static final String JSON_ITEM_ACHIEVEMENTS = "achievements";

	private static final String ERR_COULD_NOT_PARSE_JSON_DATA = "Could not parse JSON data.";

	
	/**
	 * Builds an object representation of the Global Achievements of a particular app.
	 * 
	 * @param appId The unique Steam Application ID of the game (e.g.
     *        <code>440</code> for Team Fortress 2). See
     *        http://developer.valvesoftware.com/wiki/Steam_Application_IDs for
     *        all application IDs
	 * @param data The response from the GetGlobalAchievements request, in JSON form.
	 * @return An object representation of the Global Achievements of a particular app.
	 * @throws ParseException if the JSON cannot be parsed as expected.
	 */
	public GlobalAchievements buildGlobalAchievements(int appId, JSONObject data) throws ParseException {
        try {
        	Map<String, Double> percentages = new TreeMap<String, Double>();
        	JSONArray achievementsData = data.getJSONObject("achievementpercentages").getJSONArray(JSON_ITEM_ACHIEVEMENTS);
            for(int i = 0; i < achievementsData.length(); i ++) {
                JSONObject achievementData = achievementsData.getJSONObject(i);
                percentages.put(achievementData.getString(JSON_ITEM_NAME), achievementData.getDouble("percent"));
            }
    		return new GlobalAchievements(appId, percentages);
        } catch(JSONException e) {
            throw new ParseException(ERR_COULD_NOT_PARSE_JSON_DATA, e);
        }
	}

	/**
	 * Builds an object representation of the Player Achievements of a particular user and app.
	 * 
	 * @param steamId The 64bit SteamID of the player
	 * @param appId The unique Steam Application ID of the game (e.g.
     *        <code>440</code> for Team Fortress 2). See
     *        http://developer.valvesoftware.com/wiki/Steam_Application_IDs for
     *        all application IDs
	 * @param language The ISO639-1 language code for the language all tokenized strings should be returned in, or English if not provided.
	 * @param data The response from the GetPlayerAchievements request, in JSON form.
	 * @return An object representation of the Player Achievements of a particular user and app.
	 * @throws ParseException if the JSON cannot be parsed as expected.
	 * @throws DataException if an error has been returned in the response. For example, a requested app may have no stats.
	 */
	public PlayerAchievements buildPlayerAchievements(long steamId, int appId, String language, JSONObject data) throws ParseException, DataException {
		try {
			JSONObject playerstatsObject = data.getJSONObject("playerstats");
			if(!playerstatsObject.getBoolean("success")) {
				throw new DataException(playerstatsObject.getString("error"));
			}else{
				String gameName = playerstatsObject.getString(JSON_ITEM_GAME_NAME);
			
				List<String> openAchievements = new ArrayList<String>();
				List<String> closedAchievements = new ArrayList<String>();

				JSONArray achievementsJSON = playerstatsObject.getJSONArray(JSON_ITEM_ACHIEVEMENTS);
				for (int i = 0; i < achievementsJSON.length(); i++) {
					JSONObject achievementJSON = achievementsJSON.getJSONObject(i);
					
					String name = achievementJSON.getString("apiname");
					boolean isAchieved = achievementJSON.getInt("achieved") == 1 ? true : false;
					
					if(isAchieved) {
						closedAchievements.add(name);
					}else{
						openAchievements.add(name);
					}
				}

				return new PlayerAchievements(steamId, appId, language, gameName, openAchievements, closedAchievements);
			}
		} catch (JSONException e) {
			throw new ParseException(ERR_COULD_NOT_PARSE_JSON_DATA, e);
		}

	}

	/**
	 * Builds an object representation of the Schema for a particular game.
	 * 
	 * @param appId The unique Steam Application ID of the game (e.g.
     *        <code>440</code> for Team Fortress 2). See
     *        http://developer.valvesoftware.com/wiki/Steam_Application_IDs for
     *        all application IDs
	 * @param language The ISO639-1 language code for the language all tokenized strings should be returned in, or English if not provided.
	 * @param data The response from the GetSchemaForGame request, in JSON form.
	 * @return An object representation of the Schema for a particular game.
	 * @throws ParseException if the JSON cannot be parsed as expected.
	 * @throws DataException if no schema has been supplied in the response. This indicates that the game has no stats / achievements.
	 */
	public GameStatsSchema buildSchemaForGame(int appId, String language, JSONObject data) throws ParseException, DataException {
		try {
			JSONObject gameObject = data.getJSONObject("game");

			if (!gameObject.has(JSON_ITEM_GAME_NAME) && !gameObject.has("gameVersion")) {
				throw new DataException(String.format("No schema for app ID %s", appId));
			}

			String gameName = gameObject.getString(JSON_ITEM_GAME_NAME);
			int gameVersion = gameObject.getInt("gameVersion");

			// assumption is made that if the object has a name and version,
			// then it has stats.
			JSONObject availableGameStats = gameObject.getJSONObject("availableGameStats");

			Map<String, GameStat> gameStats = buildGameStats(availableGameStats);
			Map<String, GameAchievement> gameAchievements = buildGameAchievements(availableGameStats);

			return new GameStatsSchema(appId, language, gameName, gameVersion, gameStats, gameAchievements);
		} catch (JSONException e) {
			throw new ParseException(ERR_COULD_NOT_PARSE_JSON_DATA, e);
		}
	}

	/**
	 * Builds a collection of Game Stat objects, based on the schema returned from the GetGameForSchema operation.
	 * 
	 * @param availableGameStats the outer wrapper of the JSON collection of game stats. The "stats" collection
	 * may not exist if the game has no stats.
	 * @return A collection of Game Stat objects.
	 * @throws JSONException if the JSON cannot be parsed as expected.
	 */
	private Map<String, GameStat> buildGameStats(JSONObject availableGameStats) throws JSONException {
		Map<String, GameStat> stats = new HashMap<String, GameStat>();
		if (availableGameStats.has(JSON_ITEM_STATS)) {
			stats = new TreeMap<String, GameStat>();
			JSONArray statsJSON = availableGameStats.getJSONArray(JSON_ITEM_STATS);
			for (int i = 0; i < statsJSON.length(); i++) {
				JSONObject statJSON = statsJSON.getJSONObject(i);
				
				String name = statJSON.getString(JSON_ITEM_NAME);
				int defaultValue = statJSON.getInt("defaultvalue");
				String displayName = statJSON.getString("displayName");

				GameStat stat = new GameStat(name, defaultValue, displayName);

				stats.put(stat.getKey(), stat);
			}
		}
		return stats;
	}

	/**
	 * Builds a collection of Game Achievement objects, based on the schema returned from the GetGameForSchema operation.
	 * 
	 * @param availableGameStats the outer wrapper of the JSON collection of game stats. The "achievements" collection
	 * may not exist if the game has no achievements.
	 * @return A collection of Game Achievement objects.
	 * @throws JSONException if the JSON cannot be parsed as expected.
	 */
	private Map<String, GameAchievement> buildGameAchievements(JSONObject availableGameStats) throws JSONException {
		Map<String, GameAchievement> achievements = new HashMap<String, GameAchievement>();
		if (availableGameStats.has(JSON_ITEM_ACHIEVEMENTS)) {
			achievements = new TreeMap<String, GameAchievement>();
			JSONArray achievementsJSON = availableGameStats.getJSONArray(JSON_ITEM_ACHIEVEMENTS);
			for (int i = 0; i < achievementsJSON.length(); i++) {
				JSONObject achievementJSON = achievementsJSON.getJSONObject(i);
				
				String name = achievementJSON.getString(JSON_ITEM_NAME);
				int defaultValue = achievementJSON.getInt("defaultvalue");
				String displayName = achievementJSON.getString("displayName");
				String description = achievementJSON.has("description") ? achievementJSON.getString("description") : "";
				String iconOpenUrl = achievementJSON.getString("icongray");
				String iconClosedUrl = achievementJSON.getString("icon");
				boolean hidden = achievementJSON.getInt("hidden") == 1 ? true : false;
				
				GameAchievement achievement = new GameAchievement(name, defaultValue, displayName, description, iconOpenUrl, iconClosedUrl, hidden);

				achievements.put(achievement.getKey(), achievement);
			}
		}
		return achievements;
	}
	
	/**
	 * Builds an object representation of a user's stats for a particular game.
	 * 
	 * @param steamId
	 * @param appId The unique Steam Application ID of the game (e.g.
     *        <code>440</code> for Team Fortress 2). See
     *        http://developer.valvesoftware.com/wiki/Steam_Application_IDs for
     *        all application IDs
	 * @param data The response from the GetUserStatsForGame request, in JSON form.
	 * @return An object representation of a user's stats for a particular game.
	 * @throws ParseException if the JSON cannot be parsed as expected.
	 */
	public UserStats buildUserStatsForGame(long steamId, int appId, JSONObject data) throws ParseException {
		try {
			JSONObject playerstatsObject = data.getJSONObject("playerstats");
			String gameName = playerstatsObject.getString(JSON_ITEM_GAME_NAME);
		
			Map<String, Integer> userStatList = buildUserStats(playerstatsObject);
			List<String> closedAchievements = new ArrayList<String>();
			
			if(playerstatsObject.has(JSON_ITEM_ACHIEVEMENTS)) {
				JSONArray achievementsJSON = playerstatsObject.getJSONArray(JSON_ITEM_ACHIEVEMENTS);
				for (int i = 0; i < achievementsJSON.length(); i++) {
					JSONObject achievementJSON = achievementsJSON.getJSONObject(i);
					
					String name = achievementJSON.getString(JSON_ITEM_NAME);

					//Assumption made that only closed achievements are provided
					closedAchievements.add(name);
				}
			}

			return new UserStats(steamId, appId, gameName, userStatList, closedAchievements);
		} catch (JSONException e) {
			throw new ParseException(ERR_COULD_NOT_PARSE_JSON_DATA, e);
		}
	}

	/**
	 * Builds a collection of user stats, based on the data returned from the GetUserStatsFromGame operation.
	 * 
	 * @param playerstatsObject the outer wrapper of the JSON collection of the user's game stats. The "stats" collection
	 * may not exist if the game has no stats.
	 * @return A collection of user stats details.
	 * @throws JSONException if the JSON cannot be parsed as expected.
	 */
	private Map<String, Integer> buildUserStats(JSONObject playerstatsObject) throws JSONException {
		Map<String, Integer> stats = new TreeMap<String, Integer>();
		if(playerstatsObject.has(JSON_ITEM_STATS)) {
			JSONArray statsJSON = playerstatsObject.getJSONArray(JSON_ITEM_STATS);
			for (int i = 0; i < statsJSON.length(); i++) {
				JSONObject statJSON = statsJSON.getJSONObject(i);
				
				String name = statJSON.getString(JSON_ITEM_NAME);
				int value = statJSON.getInt("value");
				
				stats.put(name, value);
			}
		}
		return stats;
	}
}
