/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2012, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.webapi.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.exceptions.WebApiException;
import com.github.koraktor.steamcondenser.steam.community.gamestats.GameAchievement;
import com.github.koraktor.steamcondenser.steam.community.gamestats.GameStat;
import com.github.koraktor.steamcondenser.steam.community.gamestats.GameStatsSchema;
import com.github.koraktor.steamcondenser.steam.community.gamestats.GlobalAchievements;
import com.github.koraktor.steamcondenser.steam.community.userstats.PlayerAchievements;
import com.github.koraktor.steamcondenser.steam.community.userstats.UserStats;
import com.github.koraktor.steamcondenser.steam.webapi.exceptions.DataException;
import com.github.koraktor.steamcondenser.steam.webapi.exceptions.ParseException;

/**
 * @author Lewis Keen
 * @author Sebastian Staudt
 */
public class UserStatsBuilderTest {
	private UserStatsBuilder userStatsBuilder;

	@Before
	public void setup() {
		userStatsBuilder = new UserStatsBuilder();
	}

	private String loadFileAsString(String path) throws IOException {
		URL resource = this.getClass().getResource(path);
		File resourceFile = new File(resource.getFile());
		return FileUtils.readFileToString(resourceFile, "UTF-8");
	}

	@Test
	public void testBuildGlobalAchievements() throws JSONException, IOException, ParseException {
		JSONObject globalPercentagesDocument = new JSONObject(
				loadFileAsString("ISteamUserStats/getGlobalAchievementPercentagesForApp.v2.json"));

		GlobalAchievements globalAchievements = userStatsBuilder.buildGlobalAchievements(440, globalPercentagesDocument);

		assertEquals(440, globalAchievements.getAppId());
		assertEquals(450, globalAchievements.getPercentages().size());
		assertEquals(new Double(19.12983512878418), globalAchievements.getPercentageForAchievement("TF_GET_MULTIPLEKILLS"));
	}

	@Test
	public void testBuildGlobalAchievementsNoAchivements() throws JSONException, IOException, ParseException {
		JSONObject globalPercentagesDocument = new JSONObject(
				loadFileAsString("ISteamUserStats/getGlobalAchievementPercentagesForApp.NoAchievements.v2.json"));

		GlobalAchievements globalAchievements = userStatsBuilder.buildGlobalAchievements(440, globalPercentagesDocument);

		assertEquals(440, globalAchievements.getAppId());
		assertEquals(0, globalAchievements.getPercentages().size());
	}

	@Test
	public void testGetGlobalAchievementPercentagesForAppInvalidJSON() throws JSONException {
		JSONObject globalPercentagesDocument = new JSONObject("{ }");

		try {
			userStatsBuilder.buildGlobalAchievements(440, globalPercentagesDocument);
			fail("Exception should be thrown when calling user Stats Builder with invalid JSON.");
		} catch (Exception e) {
			assertEquals("Could not parse JSON data.", e.getMessage());
		}
	}

	/* Tests for getPlayerAchievements */
	@Test
	public void testBuildPlayerAchievements() throws JSONException, IOException, ParseException, DataException {
		JSONObject playerAchievementsDocument = new JSONObject(loadFileAsString("ISteamUserStats/getPlayerAchievements.v1.json"));

		PlayerAchievements playerAchievements = userStatsBuilder.buildPlayerAchievements(12345, 440, "en", playerAchievementsDocument);

		assertEquals(12345L, playerAchievements.getSteamId());
		assertEquals(440, playerAchievements.getAppId());
		assertEquals("en", playerAchievements.getLanguage());
		assertEquals("Left 4 Dead 2", playerAchievements.getGameName());
		assertEquals(64, playerAchievements.getClosedAchievements().size());
		assertEquals(5, playerAchievements.getOpenAchievements().size());

		String achievedAchievement = playerAchievements.getClosedAchievements().get(19);
		assertEquals("ACH_KILL_WITH_EVERY_MELEE", achievedAchievement);

		String notAchievedAchievement = playerAchievements.getOpenAchievements().get(0);
		assertEquals("ACH_VS_LEAVE_SAFE_ROOM_DEFIB_TEAMMATE", notAchievedAchievement);
	}

	@Test
	public void testBuildPlayerAchievementsNoAchievements() throws JSONException {
		JSONObject playerAchievementsDocument = new JSONObject("{ \"playerstats\": { \"error\": \"Requested app has no stats\", \"success\": false } }");

		try {
			userStatsBuilder.buildPlayerAchievements(12345, 48240, "en", playerAchievementsDocument);
			fail("Exception should be thrown when calling getPlayerAchievements with an invalid appId.");
		} catch (DataException e) {
			assertEquals("Requested app has no stats", e.getMessage());
		} catch (ParseException e) {
			fail("Exception should be thrown when calling getPlayerAchievements with an invalid appId.");
		}
	}

	@Test
	public void testBuildPlayerAchievementsInvalidResponse() throws WebApiException, JSONException, IOException {
		JSONObject playerAchievementsDocument = new JSONObject("{ \"playerstats\": { \"success\": true } }");

		try {
			userStatsBuilder.buildPlayerAchievements(12345, 48240, "en", playerAchievementsDocument);
			fail("Exception should be thrown when calling getPlayerAchievements with invalid JSON.");
		} catch (Exception e) {
			assertEquals("Could not parse JSON data.", e.getMessage());
		}
	}

	@Test
	public void testBuildSchemaForGame() throws JSONException, IOException, SteamCondenserException {
		JSONObject schemaForGameDocument = new JSONObject(loadFileAsString("ISteamUserStats/getSchemaForGame.v2.json"));

		GameStatsSchema gameStatsSchema = userStatsBuilder.buildSchemaForGame(440, "en", schemaForGameDocument);

		assertEquals(440, gameStatsSchema.getAppId());
		assertEquals("en", gameStatsSchema.getLanguage());
		assertEquals("Team Fortress 2", gameStatsSchema.getGameName());
		assertEquals(323, gameStatsSchema.getGameVersion());
		assertTrue(gameStatsSchema.hasAchievements());
		assertTrue(gameStatsSchema.hasStats());
		assertEquals(126, gameStatsSchema.getVisibleAchievements());
		assertEquals(4, gameStatsSchema.getHiddenAchievements());
		assertEquals(gameStatsSchema.getVisibleAchievements() + gameStatsSchema.getHiddenAchievements(), gameStatsSchema.getAchievements().size());
		assertEquals(101, gameStatsSchema.getStats().size());

		GameAchievement gameAchievement = gameStatsSchema.getAchievement("TF_PYRO_BURN_SNIPERS_ZOOMED");
		assertEquals("TF_PYRO_BURN_SNIPERS_ZOOMED", gameAchievement.getKey());
		assertEquals(0, gameAchievement.getDefaultValue());
		assertEquals("Firewatch", gameAchievement.getDisplayName());
		assertEquals("Ignite 10 Snipers while they are zoomed in.", gameAchievement.getDescription());
		assertEquals("http://media.steampowered.com/steamcommunity/public/images/apps/440/16e90c29a9a53a7723afbdbe63f2accb50c14415.jpg", gameAchievement.getIconClosedUrl());
		assertEquals("http://media.steampowered.com/steamcommunity/public/images/apps/440/212a92733def907031c3969f680638bdd7f0baed.jpg", gameAchievement.getIconOpenUrl());
		assertFalse(gameAchievement.isHidden());

		GameAchievement hiddenAchievement = gameStatsSchema.getAchievement("TF_PYRO_KILL_HEAVIES");
		assertTrue(hiddenAchievement.isHidden());

		GameAchievement noDescriptionAchievement = gameStatsSchema.getAchievement("TF_HEAVY_KILL_DOMINATED");
		assertEquals("", noDescriptionAchievement.getDescription());

		GameStat gameStat = gameStatsSchema.getStat("Spy.accum.iNumInvulnerable");
		assertEquals("Spy.accum.iNumInvulnerable", gameStat.getDisplayName());
		assertEquals(0, gameStat.getDefaultValue());
		assertEquals("Spy.accum.iNumInvulnerable", gameStat.getDisplayName());
	}

	@Test
	public void testBuildSchemaForGameNoStats() throws JSONException, IOException, SteamCondenserException {
		JSONObject schemaForGameDocument = new JSONObject("{	\"game\": {	\"gameName\": \"testGameName\", \"gameVersion\": 1, \"availableGameStats\": { } } }");

		GameStatsSchema gameStatsSchema = userStatsBuilder.buildSchemaForGame(440, "en", schemaForGameDocument);

		assertFalse(gameStatsSchema.hasAchievements());
		assertFalse(gameStatsSchema.hasStats());
	}

	@Test
	public void testBuildSchemaForGameNoSchema() throws JSONException, IOException, SteamCondenserException {
		JSONObject schemaForGameDocument = new JSONObject("{	\"game\": {	} }");

		try {
			userStatsBuilder.buildSchemaForGame(440, "en", schemaForGameDocument);
		} catch (Exception e) {
			assertEquals("No schema for app ID 440", e.getMessage());
		}
	}

	@Test
	public void testBuildSchemaForGameNoGameVersion() throws WebApiException, JSONException, IOException {
		JSONObject schemaForGameDocument = new JSONObject("{	\"game\": {	\"gameName\": \"myGameNameWithoutVersion\" } }");

		try {
			userStatsBuilder.buildSchemaForGame(440, "en", schemaForGameDocument);
			fail("Exception should be thrown when calling getSchemaForGame with invalid JSON.");
		} catch (Exception e) {
			assertEquals("Could not parse JSON data.", e.getMessage());
		}
	}

	@Test
	public void testBuildSchemaForGameNoGameName() throws WebApiException, JSONException, IOException {
		JSONObject schemaForGameDocument = new JSONObject("{	\"game\": {	\"gameVersion\": 12345 } }");

		try {
			userStatsBuilder.buildSchemaForGame(440, "en", schemaForGameDocument);
			fail("Exception should be thrown when calling getSchemaForGame with invalid JSON.");
		} catch (Exception e) {
			assertEquals("Could not parse JSON data.", e.getMessage());
		}
	}

	@Test
	public void testBuildUserStatsForGame() throws WebApiException, JSONException, IOException, ParseException {
		JSONObject userStatsForGameDocument = new JSONObject(loadFileAsString("ISteamUserStats/getUserStatsForGame.v2.json"));

		UserStats userStats = userStatsBuilder.buildUserStatsForGame(12345, 440, userStatsForGameDocument);

		assertEquals(12345, userStats.getSteamId());
		assertEquals(440, userStats.getAppId());
		assertEquals("Team Fortress 2", userStats.getGameName());
		assertTrue(userStats.hasAchievements());
		assertTrue(userStats.hasStats());
		assertEquals(76, userStats.getClosedAchievements().size());
		assertEquals(42, userStats.getStats().size());

		String closedAchievement = userStats.getClosedAchievements().get(68);
		assertEquals("TF_PYRO_BURN_SNIPERS_ZOOMED", closedAchievement);

		Integer engineerPlayTime = userStats.getStats().get("Engineer.accum.iPlayTime");
		assertEquals(93938, engineerPlayTime.intValue());
	}

	@Test
	public void testGetUserStatsForGameNoStatsOrAchievements() throws WebApiException, JSONException, IOException, ParseException {
		JSONObject userStatsForGameDocument = new JSONObject("{ \"playerstats\": {	\"steamID\": \"12345\",	\"gameName\": \"AaaaaAAaaaAAAaaAAAAaAAAAA!!!\"	}}");

		UserStats userStats = userStatsBuilder.buildUserStatsForGame(12345, 440739, userStatsForGameDocument);
		assertEquals("AaaaaAAaaaAAAaaAAAAaAAAAA!!!", userStats.getGameName());
		assertFalse(userStats.hasAchievements());
		assertFalse(userStats.hasStats());
	}

	@Test
	public void testGetUserStatsForGameInvalidResponse() throws WebApiException, JSONException, IOException {
		JSONObject userStatsForGameDocument = new JSONObject("{ \"playerstats\": {	\"steamID\": \"12345\" }}");

		try {
			userStatsBuilder.buildUserStatsForGame(12345, 440739, userStatsForGameDocument);
			fail("Exception should be thrown when calling getUserStatsForGame with invalid JSON.");
		} catch (Exception e) {
			assertEquals("Could not parse JSON data.", e.getMessage());
		}
	}
}
