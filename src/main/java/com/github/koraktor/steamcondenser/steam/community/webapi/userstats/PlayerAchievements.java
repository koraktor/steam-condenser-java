package com.github.koraktor.steamcondenser.steam.community.webapi.userstats;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.koraktor.steamcondenser.exceptions.WebApiException;

public class PlayerAchievements {
	private String gameName;
	
	private long steamId;
    private int appId;
    
    private String language;

	private List<UserAchievement> closedAchievements = new ArrayList<UserAchievement>();
	private List<UserAchievement> openAchievements = new ArrayList<UserAchievement>();
	
	public PlayerAchievements(long steamId, int appId, String language, JSONObject data) throws WebApiException {
		this.steamId = steamId;
		this.appId = appId;
		this.language = language;
		try {
			JSONObject playerstatsObject = data.getJSONObject("playerstats");
			if(!playerstatsObject.getBoolean("success")) {
				throw new WebApiException(playerstatsObject.getString("error"));
			}else{
				gameName = playerstatsObject.getString("gameName");
			
				JSONArray achievementsJSON = playerstatsObject.getJSONArray("achievements");
				for (int i = 0; i < achievementsJSON.length(); i++) {
					JSONObject achievementJSON = achievementsJSON.getJSONObject(i);
					UserAchievement achievement = new UserAchievement(achievementJSON);
					
					if(achievement.isAchieved()) {
						closedAchievements.add(achievement);
					}else{
						openAchievements.add(achievement);
					}
				}
			}
		} catch (JSONException e) {
			throw new WebApiException("Could not parse JSON data.", e);
		}
	}

	public int getAppId() {
		return appId;
	}

	public String getGameName() {
		return gameName;
	}
	
	public List<UserAchievement> getOpenAchievements() {
		return openAchievements;
	}

	public List<UserAchievement> getClosedAchievements() {
		return closedAchievements;
	}
	
	public long getSteamId() {
		return steamId;
	}

	public String getLanguage() {
		return language;
	}
}
