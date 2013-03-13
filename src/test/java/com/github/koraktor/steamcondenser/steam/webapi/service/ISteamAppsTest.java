/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2012, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.webapi.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.koraktor.steamcondenser.exceptions.WebApiException;
import com.github.koraktor.steamcondenser.steam.community.WebApi;
import com.github.koraktor.steamcondenser.steam.community.apps.UpToDateCheck;
import com.github.koraktor.steamcondenser.steam.webapi.builder.AppsBuilder;
import com.github.koraktor.steamcondenser.steam.webapi.exceptions.ParseException;

/**
 * @author Lewis Keen
 * @author Sebastian Staudt
 */
@PrepareForTest(WebApi.class)
@RunWith(PowerMockRunner.class)
public class ISteamAppsTest {
	private ISteamApps iSteamApps;
	private AppsBuilder appsBuilder;
	
	@Before
	public void setup() {
		appsBuilder = mock(AppsBuilder.class);
        iSteamApps = new ISteamApps(appsBuilder);
        mockStatic(WebApi.class);
	}

    @Test
    public void testGetAppList() throws JSONException, WebApiException, ParseException  {
		JSONObject appListDocument = new JSONObject("{ \"object\" : \"mockJSONObject\"}");

		when(WebApi.getJSONResponse("ISteamApps", "GetAppList", 2, null)).thenReturn(appListDocument);

		Map<Long, String> appList = new TreeMap<Long, String>();
		when(appsBuilder.buildAppList(appListDocument)).thenReturn(appList);
		
		iSteamApps.getAppList();
		
		verify(appsBuilder).buildAppList(appListDocument);
    }

    @Test
    public void testUpToDateCheck() throws JSONException, WebApiException, ParseException  {
		JSONObject upToDateCheckDocument = new JSONObject("{ \"object\" : \"mockJSONObject\"}");

		Map<String, Object> params = new HashMap<String, Object>(); 
		params.put("appid", Integer.toString(440));
		params.put("version", Integer.toString(1253));
		when(WebApi.getJSONResponse("ISteamApps", "UpToDateCheck", 1, params)).thenReturn(upToDateCheckDocument);

		UpToDateCheck upToDateCheck = mock(UpToDateCheck.class);
		when(appsBuilder.buildUpToDateCheck(440, 1253, upToDateCheckDocument)).thenReturn(upToDateCheck);
		
		iSteamApps.upToDateCheck(440, 1253);
		
		verify(appsBuilder).buildUpToDateCheck(440, 1253, upToDateCheckDocument);
    }
}
