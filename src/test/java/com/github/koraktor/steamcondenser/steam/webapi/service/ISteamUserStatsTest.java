/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2012, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.webapi.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.exceptions.WebApiException;
import com.github.koraktor.steamcondenser.steam.community.WebApi;
import com.github.koraktor.steamcondenser.steam.community.gamestats.GameStatsSchema;
import com.github.koraktor.steamcondenser.steam.community.gamestats.GlobalAchievements;
import com.github.koraktor.steamcondenser.steam.community.userstats.PlayerAchievements;
import com.github.koraktor.steamcondenser.steam.community.userstats.UserStats;
import com.github.koraktor.steamcondenser.steam.webapi.builder.UserStatsBuilder;
import com.github.koraktor.steamcondenser.steam.webapi.exceptions.DataException;
import com.github.koraktor.steamcondenser.steam.webapi.exceptions.ParseException;

/**
 * @author Lewis Keen
 * @author Sebastian Staudt
 */
@PrepareForTest(WebApi.class)
@RunWith(PowerMockRunner.class)
public class ISteamUserStatsTest {
	private static final int STEAM_ID = 12345;
	private static final String LANG_EN = "en";
	private static final int APPID_TF2 = 440;
	private ISteamUserStats iSteamUserStats;
	private UserStatsBuilder userStatsBuilder;
	
	@Before
	public void setup() {
        userStatsBuilder = mock(UserStatsBuilder.class);
		iSteamUserStats = new ISteamUserStats(userStatsBuilder);
        mockStatic(WebApi.class);
	}

	private String loadFileAsString(String path) throws IOException {
		URL resource = this.getClass().getResource(path);
		File resourceFile = new File(resource.getFile());
		return FileUtils.readFileToString(resourceFile, "UTF-8");
	}

	/* Tests for GetGlobalAchievementPercentagesForApp */
    @Test
    public void testGlobalAchievementPercentagesForAppCache() throws JSONException, WebApiException, ParseException  {
		JSONObject globalPercentagesDocument = new JSONObject("{ \"object\" : \"mockJSONObject\"}");
		Map<String, Object> params = Collections.<String,Object>singletonMap("gameId", Integer.toString(APPID_TF2));

		when(WebApi.getJSONResponse("ISteamUserStats", "GetGlobalAchievementPercentagesForApp", 2, params)).thenReturn(globalPercentagesDocument);

		GlobalAchievements globalAchievements = mock(GlobalAchievements.class);
		when(userStatsBuilder.buildGlobalAchievements(APPID_TF2, globalPercentagesDocument)).thenReturn(globalAchievements);
		
		GlobalAchievements globalAchievementPercentagesForApp = iSteamUserStats.getGlobalAchievementPercentagesForApp(APPID_TF2);
		GlobalAchievements globalAchievementPercentagesForApp2 = iSteamUserStats.getGlobalAchievementPercentagesForApp(APPID_TF2);
		assertTrue(globalAchievementPercentagesForApp == globalAchievementPercentagesForApp2);
		
		verify(userStatsBuilder).buildGlobalAchievements(APPID_TF2, globalPercentagesDocument);
    }


	/* Tests for GetNumberOfCurrentPlayers */
	@Test
	public void testGetNumberOfCurrentPlayers() throws WebApiException, JSONException, IOException {
		JSONObject numberOfPlayersDocument = new JSONObject(loadFileAsString("ISteamUserStats/getCurrentNumberOfPlayers.v1.json"));
		Map<String, Object> params = Collections.<String,Object>singletonMap("appid", Integer.toString(APPID_TF2));

		when(WebApi.getJSONResponse("ISteamUserStats", "GetNumberOfCurrentPlayers", 1, params)).thenReturn(numberOfPlayersDocument);

		int numberOfPlayers = iSteamUserStats.getNumberOfCurrentPlayers(440);
		assertEquals(68532, numberOfPlayers);
	}
	
	/* Tests for GetPlayerAchievements */
	@Test
	public void testGetPlayerAchievements() throws WebApiException, JSONException, ParseException, DataException {
		JSONObject playerAchievementsDocument = new JSONObject("{ \"object\" : \"mockJSONObject\"}");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("steamid", Long.toString(STEAM_ID));
		params.put("appid", Integer.toString(APPID_TF2));
		params.put("l", LANG_EN);

		when(WebApi.getJSONResponse("ISteamUserStats", "GetPlayerAchievements", 1, params)).thenReturn(playerAchievementsDocument);

		PlayerAchievements playerAchievements = mock(PlayerAchievements.class);
		when(userStatsBuilder.buildPlayerAchievements(STEAM_ID, APPID_TF2, LANG_EN, playerAchievementsDocument)).thenReturn(playerAchievements);

		iSteamUserStats.getPlayerAchievements(STEAM_ID, APPID_TF2, LANG_EN);
		
		verify(userStatsBuilder).buildPlayerAchievements(STEAM_ID, APPID_TF2, LANG_EN, playerAchievementsDocument);
	}

	@Test
	public void testGetPlayerAchievementsNullLanguage() throws WebApiException, JSONException, ParseException, DataException {
		JSONObject playerAchievementsDocument = new JSONObject("{ \"object\" : \"mockJSONObject\"}");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("steamid", Long.toString(STEAM_ID));
		params.put("appid", Integer.toString(APPID_TF2));

		when(WebApi.getJSONResponse("ISteamUserStats", "GetPlayerAchievements", 1, params)).thenReturn(playerAchievementsDocument);

		PlayerAchievements playerAchievements = mock(PlayerAchievements.class);
		when(userStatsBuilder.buildPlayerAchievements(STEAM_ID, APPID_TF2, null, playerAchievementsDocument)).thenReturn(playerAchievements);

		iSteamUserStats.getPlayerAchievements(STEAM_ID, APPID_TF2);
		
		verify(userStatsBuilder).buildPlayerAchievements(STEAM_ID, APPID_TF2, null, playerAchievementsDocument);
	}

	@Test
	public void testGetPlayerAchievementsEmptyLanguage() throws WebApiException, JSONException, ParseException, DataException {
		JSONObject playerAchievementsDocument = new JSONObject("{ \"object\" : \"mockJSONObject\"}");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("steamid", Long.toString(STEAM_ID));
		params.put("appid", Integer.toString(APPID_TF2));

		when(WebApi.getJSONResponse("ISteamUserStats", "GetPlayerAchievements", 1, params)).thenReturn(playerAchievementsDocument);

		PlayerAchievements playerAchievements = mock(PlayerAchievements.class);
		when(userStatsBuilder.buildPlayerAchievements(STEAM_ID, APPID_TF2, "", playerAchievementsDocument)).thenReturn(playerAchievements);

		iSteamUserStats.getPlayerAchievements(STEAM_ID, APPID_TF2, "");
		
		verify(userStatsBuilder).buildPlayerAchievements(STEAM_ID, APPID_TF2, "", playerAchievementsDocument);
	}

	/* Tests for GetStatsSchemaForGame */
    @Test
    public void testGameSchemaCache() throws JSONException, SteamCondenserException  {
		JSONObject schemaForGameDocument = new JSONObject("{ \"object\" : \"mockJSONObject\"}");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("appid", Integer.toString(APPID_TF2));
		params.put("l", LANG_EN);

		when(WebApi.getJSONResponse("ISteamUserStats", "GetSchemaForGame", 2, params)).thenReturn(schemaForGameDocument);

		GameStatsSchema gameStatsSchema = mock(GameStatsSchema.class);
		when(userStatsBuilder.buildSchemaForGame(APPID_TF2, LANG_EN, schemaForGameDocument)).thenReturn(gameStatsSchema);
		
		assertTrue(iSteamUserStats.getSchemaForGame(APPID_TF2, LANG_EN) == iSteamUserStats.getSchemaForGame(APPID_TF2, LANG_EN));

		verify(userStatsBuilder).buildSchemaForGame(APPID_TF2, LANG_EN, schemaForGameDocument);
    }

	@Test
	public void testGetSchemaForGameNoLanguage() throws JSONException, SteamCondenserException {
		JSONObject schemaForGameDocument = new JSONObject("{ \"object\" : \"mockJSONObject\"}");
		Map<String, Object> params = Collections.<String,Object>singletonMap("appid", Integer.toString(APPID_TF2));

		when(WebApi.getJSONResponse("ISteamUserStats", "GetSchemaForGame", 2, params)).thenReturn(schemaForGameDocument);

		GameStatsSchema gameStatsSchema = mock(GameStatsSchema.class);
		when(userStatsBuilder.buildSchemaForGame(APPID_TF2, null, schemaForGameDocument)).thenReturn(gameStatsSchema);

		iSteamUserStats.getSchemaForGame(APPID_TF2);

		verify(userStatsBuilder).buildSchemaForGame(APPID_TF2, null, schemaForGameDocument);
	}

	@Test
	public void testGetSchemaForGameEmptyLanguage() throws JSONException, IOException, SteamCondenserException {
		JSONObject schemaForGameDocument = new JSONObject("{ \"object\" : \"mockJSONObject\"}");
		Map<String, Object> params = Collections.<String,Object>singletonMap("appid", Integer.toString(APPID_TF2));

		when(WebApi.getJSONResponse("ISteamUserStats", "GetSchemaForGame", 2, params)).thenReturn(schemaForGameDocument);

		GameStatsSchema gameStatsSchema = mock(GameStatsSchema.class);
		when(userStatsBuilder.buildSchemaForGame(APPID_TF2, "", schemaForGameDocument)).thenReturn(gameStatsSchema);

		iSteamUserStats.getSchemaForGame(APPID_TF2, "");

		verify(userStatsBuilder).buildSchemaForGame(APPID_TF2, "", schemaForGameDocument);
	}
	
	/* Tests for GetUserStatsForGame */
	@Test
	public void testGetUserStatsForGame() throws WebApiException, JSONException, ParseException {
		JSONObject userStatsForGameDocument = new JSONObject("{ \"object\" : \"mockJSONObject\"}");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("steamid", Long.toString(STEAM_ID));
		params.put("appid", Integer.toString(APPID_TF2));

		when(WebApi.getJSONResponse("ISteamUserStats", "GetUserStatsForGame", 2, params)).thenReturn(userStatsForGameDocument);

		UserStats userStats = mock(UserStats.class);
		when(userStatsBuilder.buildUserStatsForGame(STEAM_ID, APPID_TF2, userStatsForGameDocument)).thenReturn(userStats);
		
		iSteamUserStats.getUserStatsForGame(STEAM_ID, APPID_TF2);

		verify(userStatsBuilder).buildUserStatsForGame(STEAM_ID, APPID_TF2, userStatsForGameDocument);
	}
	
	@Test
	public void testClearGameStatsSchemaCache() throws JSONException, SteamCondenserException {
		JSONObject schemaForGameDocument = new JSONObject("{ \"object\" : \"mockJSONObject\"}");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("appid", Integer.toString(APPID_TF2));
		params.put("l", LANG_EN);

		when(WebApi.getJSONResponse("ISteamUserStats", "GetSchemaForGame", 2, params)).thenReturn(schemaForGameDocument);

		GameStatsSchema gameStatsSchema = mock(GameStatsSchema.class);
		when(userStatsBuilder.buildSchemaForGame(APPID_TF2, LANG_EN, schemaForGameDocument)).thenReturn(gameStatsSchema);
		
		iSteamUserStats.getSchemaForGame(APPID_TF2, LANG_EN);
		
		iSteamUserStats.clearGameStatsSchemaCache();
		
		iSteamUserStats.getSchemaForGame(APPID_TF2, LANG_EN);

		verify(userStatsBuilder, times(2)).buildSchemaForGame(APPID_TF2, LANG_EN, schemaForGameDocument);		
	}

    @Test
    public void testClearGlobalAchievementPercentagesForAppCache() throws JSONException, WebApiException, ParseException  {
		JSONObject globalPercentagesDocument = new JSONObject("{ \"object\" : \"mockJSONObject\"}");
		Map<String, Object> params = Collections.<String,Object>singletonMap("gameId", Integer.toString(APPID_TF2));

		when(WebApi.getJSONResponse("ISteamUserStats", "GetGlobalAchievementPercentagesForApp", 2, params)).thenReturn(globalPercentagesDocument);

		GlobalAchievements globalAchievements = mock(GlobalAchievements.class);
		when(userStatsBuilder.buildGlobalAchievements(APPID_TF2, globalPercentagesDocument)).thenReturn(globalAchievements);
		
		iSteamUserStats.getGlobalAchievementPercentagesForApp(APPID_TF2);
		
		iSteamUserStats.clearGlobalAchievementPercentagesForAppCache();
		
		iSteamUserStats.getGlobalAchievementPercentagesForApp(APPID_TF2);
		
		verify(userStatsBuilder, times(2)).buildGlobalAchievements(APPID_TF2, globalPercentagesDocument);
    }

}
