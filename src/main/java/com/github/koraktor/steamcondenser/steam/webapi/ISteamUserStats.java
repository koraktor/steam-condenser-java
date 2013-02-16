package com.github.koraktor.steamcondenser.steam.webapi;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.koraktor.steamcondenser.exceptions.WebApiException;
import com.github.koraktor.steamcondenser.steam.community.WebApi;
import com.github.koraktor.steamcondenser.steam.community.userstats.GameStatsSchema;
import com.github.koraktor.steamcondenser.steam.community.userstats.GlobalAchievements;
import com.github.koraktor.steamcondenser.steam.community.userstats.PlayerAchievements;
import com.github.koraktor.steamcondenser.steam.community.userstats.UserStats;

//XXX: needs documenting
public class ISteamUserStats {
	private static final String APPID = "appid";

	private static final String I_STEAM_USER_STATS = "ISteamUserStats";

	private static Map<Integer, GameStatsSchema> gameStatsSchemas = new HashMap<Integer, GameStatsSchema>();
	private static Map<Integer, GlobalAchievements> globalAchievementPercentagesForAppCache = new HashMap<Integer, GlobalAchievements>();
	
	/**
	 * Clears the game stats schema cache
	 */
	public static void clearGameStatsSchemaCache() {
		gameStatsSchemas.clear();
	}

	/**
	 * Clears the game stats schema cache
	 */
	public static void clearGlobalAchievementPercentagesForAppCache() {
		globalAchievementPercentagesForAppCache.clear();
	}

	public static GlobalAchievements getGlobalAchievementPercentagesForApp(int appId) throws WebApiException, JSONException {
		if(globalAchievementPercentagesForAppCache.containsKey(appId)) {
			return globalAchievementPercentagesForAppCache.get(appId);
		}else{
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("gameId", Integer.toString(appId));
			JSONObject data = WebApi.getJSONObject(I_STEAM_USER_STATS, "GetGlobalAchievementPercentagesForApp", 2, params);
			GlobalAchievements globalAchievements = new GlobalAchievements(appId, data);
			globalAchievementPercentagesForAppCache.put(appId, globalAchievements);
			return globalAchievements;
		}
	}

	public static int getNumberOfCurrentPlayers(int appId) throws WebApiException, JSONException {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(APPID, Integer.toString(appId));
		JSONObject data = WebApi.getJSONObject(I_STEAM_USER_STATS, "GetNumberOfCurrentPlayers", 1, params);
		return data.getJSONObject("response").getInt("player_count");
	}

	public static PlayerAchievements getPlayerAchievements(long steamId, int appId) throws WebApiException, JSONException {
		return getPlayerAchievements(steamId, appId, null);
	}

	public static PlayerAchievements getPlayerAchievements(long steamId, int appId, String language) throws WebApiException, JSONException {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("steamid", Long.toString(steamId));
		params.put(APPID, Integer.toString(appId));
		if (language != null && language.length() > 0) {
			params.put("l", language);
		}
		JSONObject data = WebApi.getJSONObject(I_STEAM_USER_STATS, "GetPlayerAchievements", 1, params);
		return new PlayerAchievements(steamId, appId, language, data);
	}

	public static GameStatsSchema getSchemaForGame(int appId) throws WebApiException, JSONException {
		return getSchemaForGame(appId, null);
	}

	public static GameStatsSchema getSchemaForGame(int appId, String language) throws WebApiException, JSONException {
		if (gameStatsSchemas.containsKey(appId)) {
			return gameStatsSchemas.get(appId);
		} else {
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(APPID, Integer.toString(appId));
			if (language != null && language.length() > 0) {
				params.put("l", language);
			}
			JSONObject data = WebApi.getJSONObject(I_STEAM_USER_STATS, "GetSchemaForGame", 2, params);
			GameStatsSchema gameStatsSchema = new GameStatsSchema(appId, language, data);
			gameStatsSchemas.put(appId, gameStatsSchema);
			return gameStatsSchema;
		}
	}

	public static UserStats getUserStatsForGame(long steamId, int appId) throws WebApiException, JSONException {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("steamid", Long.toString(steamId));
		params.put(APPID, Integer.toString(appId));
		JSONObject data = WebApi.getJSONObject(I_STEAM_USER_STATS, "GetUserStatsForGame", 2, params);
		return new UserStats(steamId, appId, data);
	}
}
