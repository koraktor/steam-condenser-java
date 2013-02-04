package com.github.koraktor.steamcondenser.steam.community.webapi.userstats;

import org.json.JSONException;
import org.json.JSONObject;

public class UserAchievement {
	private String name;
	
	private boolean achieved;
	
	public UserAchievement(JSONObject statJSONObject) throws JSONException {
		if(statJSONObject.has("name")) {
			this.name = statJSONObject.getString("name");
		}else if(statJSONObject.has("apiname")) {
			this.name = statJSONObject.getString("apiname");
		}else{
			throw new JSONException("apiname or name not found for user achievement");
		}
		this.achieved = statJSONObject.getInt("achieved") == 1 ? true : false;
	}

	public String getName() {
		return name;
	}

	public boolean isAchieved() {
		return achieved;
	}
}
