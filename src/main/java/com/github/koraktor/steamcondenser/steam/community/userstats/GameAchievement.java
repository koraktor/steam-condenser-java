package com.github.koraktor.steamcondenser.steam.community.userstats;

import org.json.JSONException;
import org.json.JSONObject;

public class GameAchievement extends GameStat {
	private String description;
	
    private String iconClosedUrl;

    private String iconOpenUrl;

    private boolean hidden;
	
	public GameAchievement(JSONObject statJSONObject) throws JSONException {
		super(statJSONObject);
		
		this.description = statJSONObject.has("description") ? statJSONObject.getString("description") : "";
		this.iconClosedUrl = statJSONObject.getString("icon");
		this.iconOpenUrl = statJSONObject.getString("icongray");
		this.hidden = statJSONObject.getInt("hidden") == 1 ? true : false;
	}

	public String getDescription() {
		return description;
	}

	public String getIconClosedUrl() {
		return iconClosedUrl;
	}

	public String getIconOpenUrl() {
		return iconOpenUrl;
	}

	public boolean isHidden() {
		return hidden;
	}
}
