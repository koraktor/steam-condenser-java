package com.github.koraktor.steamcondenser.steam.community.userstats;

import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.koraktor.steamcondenser.exceptions.WebApiException;

public class GameStatsSchema {

	private int appId;

	private String language;

	private String gameName;

	private int gameVersion;

	private Map<String, GameStat> stats;
	private Map<String, GameAchievement> achievements;

	private boolean emptySchema;

	public GameStatsSchema(int appId, String language, JSONObject data) throws WebApiException {
		this.appId = appId;
		this.language = language;
		try {
			JSONObject gameObject = data.getJSONObject("game");
			if (!gameObject.has("gameName") && !gameObject.has("gameVersion")) {
				emptySchema = true;
			} else {
				emptySchema = false;

				gameName = gameObject.getString("gameName");
				gameVersion = gameObject.getInt("gameVersion");

				//assumption is made that if the object has a name and version, then it has stats.
				JSONObject availableGameStats = gameObject.getJSONObject("availableGameStats");

				getGameStats(availableGameStats);
				getGameAchievements(availableGameStats);
			}
		} catch (JSONException e) {
			throw new WebApiException("Could not parse JSON data.", e);
		}
	}

	private void getGameStats(JSONObject availableGameStats) throws JSONException {
		if (availableGameStats.has("stats")) {
			this.stats = new TreeMap<String, GameStat>();
			JSONArray statsJSON = availableGameStats.getJSONArray("stats");
			for (int i = 0; i < statsJSON.length(); i++) {
				JSONObject statJSON = statsJSON.getJSONObject(i);
				GameStat stat = new GameStat(statJSON);

				stats.put(stat.getName(), stat);
			}
		}
	}
	
	private void getGameAchievements(JSONObject availableGameStats) throws JSONException {
		if (availableGameStats.has("achievements")) {
			this.achievements = new TreeMap<String, GameAchievement>();
			JSONArray achievementsJSON = availableGameStats.getJSONArray("achievements");
			for (int i = 0; i < achievementsJSON.length(); i++) {
				JSONObject achievementJSON = achievementsJSON.getJSONObject(i);
				GameAchievement achievement = new GameAchievement(achievementJSON);

				achievements.put(achievement.getName(), achievement);
			}
		}
	}

	public int getAppId() {
		return appId;
	}

	public String getGameName() {
		return gameName;
	}

	public int getGameVersion() {
		return gameVersion;
	}

	public boolean hasStats() {
		return stats != null;
	}

	public boolean hasAchievements() {
		return achievements != null;
	}

	public Map<String, GameStat> getStats() {
		return stats;
	}

	public Map<String, GameAchievement> getAchievements() {
		return achievements;
	}

	public GameStat getStat(String name) {
		return stats.get(name);
	}

	public GameAchievement getAchievement(String name) {
		return achievements.get(name);
	}
	
	public boolean isEmptySchema() {
		return emptySchema;
	}

	public String getLanguage() {
		return language;
	}
}
