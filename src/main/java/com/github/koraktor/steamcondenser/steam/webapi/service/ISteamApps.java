/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.webapi.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.koraktor.steamcondenser.exceptions.WebApiException;
import com.github.koraktor.steamcondenser.steam.community.WebApi;
import com.github.koraktor.steamcondenser.steam.community.apps.ServerAtAddress;
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
	
	/**
	 * Get the list of all apps from Steam.
	 * 
	 * @return A list of all apps from Steam.
	 * @throws WebApiException if there is a general service error (e.g. no key supplied)
	 * @throws JSONException if the JSON returned from the service is malformed.
	 */
	public Map<Long,String> getAppList() throws WebApiException, JSONException {
		JSONObject data = WebApi.getJSONResponse(I_STEAM_APPS, "GetAppList", 2, null);

		return appsBuilder.buildAppList(data);
	}

	/**
	 * Check that an app is up to date.
	 * 
	 * @param appId The unique Steam Application ID of the game (e.g.
     *        <code>440</code> for Team Fortress 2). See
     *        http://developer.valvesoftware.com/wiki/Steam_Application_IDs for
     *        all application IDs
	 * @param versionNumberToCheck The version number to check for the provided app.
	 * @return An object representation of the version check for the provided app.
	 * @throws WebApiException if there is a general service error (e.g. no key supplied)
	 * @throws JSONException if the JSON returned from the service is malformed.
	 */
	public UpToDateCheck upToDateCheck(int appId, int versionNumberToCheck) throws WebApiException, JSONException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("appid", Integer.toString(appId));
		params.put("version", Integer.toString(versionNumberToCheck));
		JSONObject data = WebApi.getJSONResponse(I_STEAM_APPS, "UpToDateCheck", 1, params);
			
		return appsBuilder.buildUpToDateCheck(appId, versionNumberToCheck, data);
	}
	
	/**
	 * Get the list of servers hosted in a particular IP address.
	 * 
	 * @param ip The IP address to query.
	 * @return A list of servers at a specified IP address.
	 * @throws WebApiException if there is a general service error (e.g. no key supplied)
	 * @throws JSONException if the JSON returned from the service is malformed.
	 */
	public List<ServerAtAddress> getServersAtAddress(String ip) throws WebApiException, JSONException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("addr", ip);
		JSONObject data = WebApi.getJSONResponse(I_STEAM_APPS, "GetServersAtAddress", 1, params);
		
		return appsBuilder.buildServersAtAddress(ip, data);
	}
}
