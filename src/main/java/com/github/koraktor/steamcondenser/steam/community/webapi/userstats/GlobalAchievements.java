package com.github.koraktor.steamcondenser.steam.community.webapi.userstats;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.koraktor.steamcondenser.exceptions.WebApiException;

public class GlobalAchievements {
	private int appId;
	
	private HashMap<String, Double> percentages = new HashMap<String, Double>();
	
	public GlobalAchievements(int appId, JSONObject data) throws WebApiException {
		this.appId = appId;
        try {
            JSONArray achievementsData = data.getJSONObject("achievementpercentages").getJSONArray("achievements");
            for(int i = 0; i < achievementsData.length(); i ++) {
                JSONObject achievementData = achievementsData.getJSONObject(i);
                percentages.put(achievementData.getString("name"), achievementData.getDouble("percent"));
            }
        } catch(JSONException e) {
            throw new WebApiException("Could not parse JSON data.", e);
        }
	}

	public int getAppId() {
		return appId;
	}

	public HashMap<String, Double> getPercentages() {
		return percentages;
	}

	public Double getPercentageForAchievement(String achievementName) {
		return percentages.get(achievementName);
	}
}
