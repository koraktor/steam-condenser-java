/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.webapi.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.koraktor.steamcondenser.exceptions.WebApiException;
import com.github.koraktor.steamcondenser.steam.community.WebApi;
import com.github.koraktor.steamcondenser.steam.community.apps.UpToDateCheck;
import com.github.koraktor.steamcondenser.steam.webapi.builder.AppsBuilder;

/**
 * A service class which calls the operations supplied in the ISteamApps Web API service.
 *
 * @author Lewis Keen
 * @author Sebastian Staudt
 */
public class ISteamApps {
	private static final String I_STEAM_APPS = "ISteamApps";

	private final AppsBuilder appsBuilder;
	
	/**
	 * Creates a new instance of the ISteamApps Web API service object
	 * 
	 * @param appsBuilder the apps builder used to create object representations of the operation responses
	 */
	public ISteamApps(AppsBuilder appsBuilder) {
		this.appsBuilder = appsBuilder;
	}
	
	public Map<Long,String> getAppList() throws WebApiException, JSONException {
		JSONObject data = WebApi.getJSONResponse(I_STEAM_APPS, "GetAppList", 2, null);

		return appsBuilder.buildAppList(data);
	}

	public UpToDateCheck upToDateCheck(int appId, int versionNumberToCheck) throws WebApiException, JSONException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("appid", Integer.toString(appId));
		params.put("version", Integer.toString(versionNumberToCheck));
		JSONObject data = WebApi.getJSONResponse(I_STEAM_APPS, "UpToDateCheck", 1, params);
			
		return appsBuilder.buildUpToDateCheck(appId, versionNumberToCheck, data);
	}
}
