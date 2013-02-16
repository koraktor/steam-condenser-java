package com.github.koraktor.steamcondenser.steam.community.userstats;

import org.json.JSONException;
import org.json.JSONObject;

public class GameStat {
	private String name;
	
	private int defaultValue;
	
	private String displayName;
	
	public GameStat(JSONObject statJSONObject) throws JSONException {
		this.name = statJSONObject.getString("name");
		this.defaultValue = statJSONObject.getInt("defaultvalue");
		this.displayName = statJSONObject.getString("displayName");
	}

	public String getName() {
		return name;
	}


	public int getDefaultValue() {
		return defaultValue;
	}


	public String getDisplayName() {
		return displayName;
	}
}
