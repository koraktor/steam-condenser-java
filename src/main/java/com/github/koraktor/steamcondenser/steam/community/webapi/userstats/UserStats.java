package com.github.koraktor.steamcondenser.steam.community.webapi.userstats;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.koraktor.steamcondenser.exceptions.WebApiException;

public class UserStats {
	private String gameName;
	
    private int appId;
    private long steamId;

	private List<UserStat> stats;
	private List<UserAchievement> achievements;
	
	public UserStats(long steamId, int appId, JSONObject data) throws WebApiException {
		this.steamId = steamId;
		this.appId = appId;
		try {
			JSONObject playerstatsObject = data.getJSONObject("playerstats");
			gameName = playerstatsObject.getString("gameName");
		
			if(playerstatsObject.has("stats")) {
				this.stats = new ArrayList<UserStat>();
				JSONArray statsJSON = playerstatsObject.getJSONArray("stats");
				for (int i = 0; i < statsJSON.length(); i++) {
					JSONObject statJSON = statsJSON.getJSONObject(i);
					UserStat stat = new UserStat(statJSON);
					
					stats.add(stat);
				}
			}
		
			if(playerstatsObject.has("achievements")) {
				this.achievements = new ArrayList<UserAchievement>();
				JSONArray achievementsJSON = playerstatsObject.getJSONArray("achievements");
				for (int i = 0; i < achievementsJSON.length(); i++) {
					JSONObject achievementJSON = achievementsJSON.getJSONObject(i);
					UserAchievement achievement = new UserAchievement(achievementJSON);
					
					achievements.add(achievement);
				}
			}
		} catch (JSONException e) {
			throw new WebApiException("Could not parse JSON data.", e);
		}
	}

	public long getSteamId() {
		return steamId;
	}

	public int getAppId() {
		return appId;
	}

	public String getGameName() {
		return gameName;
	}
	
	public boolean hasStats() {
		return stats != null;
	}

	public boolean hasAchievements() {
		return achievements != null;
	}

	public List<UserStat> getStats() {
		return stats;
	}

	public List<UserAchievement> getAchievements() {
		return achievements;
	}
}
