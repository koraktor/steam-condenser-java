package com.github.koraktor.steamcondenser.steam.community.webapi.userstats;

import org.json.JSONException;
import org.json.JSONObject;

public class UserStat {
	private String name;
	
	private int value;
	
	public UserStat(JSONObject statJSONObject) throws JSONException {
		this.name = statJSONObject.getString("name");
		this.value = statJSONObject.getInt("value");
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}
}
