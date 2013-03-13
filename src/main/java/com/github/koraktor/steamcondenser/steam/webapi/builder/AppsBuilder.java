/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.webapi.builder;

import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.koraktor.steamcondenser.steam.community.apps.UpToDateCheck;
import com.github.koraktor.steamcondenser.steam.webapi.exceptions.ParseException;

/**
 * Builder class to create object representations of the ISteamApps Web API responses.
 * 
 * @author Lewis Keen
 * @author Sebastian Staudt
 */
public class AppsBuilder {
	private static final String JSON_ITEM_NAME = "name";

	private static final String ERR_COULD_NOT_PARSE_JSON_DATA = "Could not parse JSON data.";

	public Map<Long, String> buildAppList(JSONObject data) throws ParseException {
        try {
    		Map<Long, String> appList = new TreeMap<Long, String>();
        	JSONArray appsData = data.getJSONObject("applist").getJSONArray("apps");
            for(int i = 0; i < appsData.length(); i ++) {
                JSONObject achievementData = appsData.getJSONObject(i);
                appList.put(achievementData.getLong("appid"), achievementData.getString(JSON_ITEM_NAME));
            }
    		return appList;
        } catch(JSONException e) {
            throw new ParseException(ERR_COULD_NOT_PARSE_JSON_DATA, e);
        }
	}
	
	public UpToDateCheck buildUpToDateCheck(int appId, int versionNumberToCheck, JSONObject data) throws ParseException {
        try {
        	boolean upToDate, versionIsListable;
        	Integer requiredVersion = null;
        	String message = null;
        	
        	JSONObject response = data.getJSONObject("response");
        	upToDate = response.getBoolean("up_to_date");
        	versionIsListable = response.getBoolean("version_is_listable");
        	if(response.has("required_version")) {
    			requiredVersion = response.getInt("required_version");
        	}
        	if(response.has("required_version")) {
        		message = response.getString("message");
        	}
        	return new UpToDateCheck(upToDate, versionIsListable, requiredVersion, message);
        } catch(JSONException e) {
            throw new ParseException(ERR_COULD_NOT_PARSE_JSON_DATA, e);
        }
		
	}
}
