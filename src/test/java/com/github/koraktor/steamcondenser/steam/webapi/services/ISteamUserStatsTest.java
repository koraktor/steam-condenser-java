package com.github.koraktor.steamcondenser.steam.webapi.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.koraktor.steamcondenser.exceptions.WebApiException;
import com.github.koraktor.steamcondenser.steam.community.WebApi;
import com.github.koraktor.steamcondenser.steam.community.userstats.GameAchievement;
import com.github.koraktor.steamcondenser.steam.community.userstats.GameStat;
import com.github.koraktor.steamcondenser.steam.community.userstats.GameStatsSchema;
import com.github.koraktor.steamcondenser.steam.community.userstats.GlobalAchievements;
import com.github.koraktor.steamcondenser.steam.community.userstats.PlayerAchievements;
import com.github.koraktor.steamcondenser.steam.community.userstats.UserAchievement;
import com.github.koraktor.steamcondenser.steam.community.userstats.UserStat;
import com.github.koraktor.steamcondenser.steam.community.userstats.UserStats;
import com.github.koraktor.steamcondenser.steam.webapi.ISteamUserStats;

@PrepareForTest(WebApi.class)
@RunWith(PowerMockRunner.class)
public class ISteamUserStatsTest {
	@Before
	public void setup() {
        ISteamUserStats.clearGameStatsSchemaCache();
        ISteamUserStats.clearGlobalAchievementPercentagesForAppCache();
        mockStatic(WebApi.class);
	}

	private String loadFileAsString(String path) throws IOException {
		URL resource = this.getClass().getResource(path);
		File resourceFile = new File(resource.getFile());
		return FileUtils.readFileToString(resourceFile, "UTF-8");
	}

	/* Tests for GetGlobalAchievementPercentagesForApp */
    @Test
    public void testGlobalAchievementPercentagesForAppCache() throws Exception {
		JSONObject globalPercentagesDocument = new JSONObject(loadFileAsString("/com/github/koraktor/steamcondenser/steam/webapi/services/ISteamUserStats/getGlobalAchievementPercentagesForApp.v2.json"));
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("gameId", "440");

		when(WebApi.getJSONObject("ISteamUserStats", "GetGlobalAchievementPercentagesForApp", 2, params)).thenReturn(globalPercentagesDocument);

		assertTrue(ISteamUserStats.getGlobalAchievementPercentagesForApp(440) == ISteamUserStats.getGlobalAchievementPercentagesForApp(440));
    }

    @Test
	public void testGetGlobalAchievementPercentagesForApp() throws WebApiException, JSONException, IOException {
		JSONObject globalPercentagesDocument = new JSONObject(loadFileAsString("/com/github/koraktor/steamcondenser/steam/webapi/services/ISteamUserStats/getGlobalAchievementPercentagesForApp.v2.json"));
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("gameId", "440");

		when(WebApi.getJSONObject("ISteamUserStats", "GetGlobalAchievementPercentagesForApp", 2, params)).thenReturn(globalPercentagesDocument);

		GlobalAchievements globalAchievementPercentagesForApp = ISteamUserStats.getGlobalAchievementPercentagesForApp(440);
		assertEquals(440, globalAchievementPercentagesForApp.getAppId());
		assertEquals(450, globalAchievementPercentagesForApp.getPercentages().size());
		assertEquals(12.263055801391602, globalAchievementPercentagesForApp.getPercentageForAchievement("TF_PYRO_ACHIEVE_PROGRESS3"), 0.000000000000001);
	}
	
	@Test
	public void testGetGlobalAchievementPercentagesForAppNoAchivements() throws WebApiException, JSONException, IOException {
		JSONObject globalPercentagesDocument = new JSONObject(loadFileAsString("/com/github/koraktor/steamcondenser/steam/webapi/services/ISteamUserStats/getGlobalAchievementPercentagesForApp.NoAchievements.v2.json"));
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("gameId", "48240");

		when(WebApi.getJSONObject("ISteamUserStats", "GetGlobalAchievementPercentagesForApp", 2, params)).thenReturn(globalPercentagesDocument);

		GlobalAchievements globalAchievementPercentagesForApp = ISteamUserStats.getGlobalAchievementPercentagesForApp(48240);
		assertEquals(0, globalAchievementPercentagesForApp.getPercentages().size());
	}

	@Test
	public void testGetGlobalAchievementPercentagesForAppInvalidJSON() throws WebApiException, JSONException, IOException {
		JSONObject globalPercentagesDocument = new JSONObject("{ }");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("gameId", "123456");

		when(WebApi.getJSONObject("ISteamUserStats", "GetGlobalAchievementPercentagesForApp", 2, params)).thenReturn(globalPercentagesDocument);

		try {
			ISteamUserStats.getGlobalAchievementPercentagesForApp(123456);
			fail("Exception should be thrown when calling getGlobalAchievementPercentagesForApp with invalid JSON.");
		} catch (Exception e) {
			assertEquals("Could not parse JSON data.", e.getMessage());
		}
	}

	/* Tests for getNumberOfCurrentPlayers */
	@Test
	public void testGetNumberOfCurrentPlayers() throws WebApiException, JSONException, IOException {
		JSONObject numberOfPlayersDocument = new JSONObject(loadFileAsString("/com/github/koraktor/steamcondenser/steam/webapi/services/ISteamUserStats/getCurrentNumberOfPlayers.v1.json"));
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("appid", "440");

		when(WebApi.getJSONObject("ISteamUserStats", "GetNumberOfCurrentPlayers", 1, params)).thenReturn(numberOfPlayersDocument);

		int numberOfPlayers = ISteamUserStats.getNumberOfCurrentPlayers(440);
		assertEquals(68532, numberOfPlayers);
	}
	
	@Test
	public void testGetNumberOfCurrentPlayersInvalidAppId() throws WebApiException, JSONException, IOException {
		JSONObject numberOfPlayersDocument = new JSONObject(loadFileAsString("/com/github/koraktor/steamcondenser/steam/webapi/services/ISteamUserStats/getCurrentNumberOfPlayers.InvalidAppId.v1.json"));
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("appid", "123456");

		when(WebApi.getJSONObject("ISteamUserStats", "GetNumberOfCurrentPlayers", 1, params)).thenReturn(numberOfPlayersDocument);

		int numberOfPlayers = ISteamUserStats.getNumberOfCurrentPlayers(123456);
		assertEquals(0, numberOfPlayers);
	}
	
	/* Tests for getPlayerAchievements */
	@Test
	public void testGetPlayerAchievements() throws WebApiException, JSONException, IOException {
		JSONObject playerAchievementsDocument = new JSONObject(loadFileAsString("/com/github/koraktor/steamcondenser/steam/webapi/services/ISteamUserStats/getPlayerAchievements.v1.json"));
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("steamid", "12345");
		params.put("appid", "440");
		params.put("l", "en");

		when(WebApi.getJSONObject("ISteamUserStats", "GetPlayerAchievements", 1, params)).thenReturn(playerAchievementsDocument);

		PlayerAchievements playerAchievements = ISteamUserStats.getPlayerAchievements(12345, 440, "en");
		
		assertEquals(12345L, playerAchievements.getSteamId());
		assertEquals(440, playerAchievements.getAppId());
		assertEquals("en", playerAchievements.getLanguage());
		assertEquals("Left 4 Dead 2", playerAchievements.getGameName());
		assertEquals(64, playerAchievements.getClosedAchievements().size());
		assertEquals(5, playerAchievements.getOpenAchievements().size());

		UserAchievement achievedAchievement = playerAchievements.getClosedAchievements().get(19);
		assertEquals("ACH_KILL_WITH_EVERY_MELEE", achievedAchievement.getName());
		assertTrue(achievedAchievement.isAchieved());

		UserAchievement notAchievedAchievement = playerAchievements.getOpenAchievements().get(0);
		assertEquals("ACH_VS_LEAVE_SAFE_ROOM_DEFIB_TEAMMATE", notAchievedAchievement.getName());
		assertFalse(notAchievedAchievement.isAchieved());
	}

	@Test
	public void testGetPlayerAchievementsNoLanguage() throws WebApiException, JSONException, IOException {
		JSONObject playerAchievementsDocument = new JSONObject(loadFileAsString("/com/github/koraktor/steamcondenser/steam/webapi/services/ISteamUserStats/getPlayerAchievements.v1.json"));
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("steamid", "12345");
		params.put("appid", "440");

		when(WebApi.getJSONObject("ISteamUserStats", "GetPlayerAchievements", 1, params)).thenReturn(playerAchievementsDocument);

		PlayerAchievements playerAchievements = ISteamUserStats.getPlayerAchievements(12345, 440);
		assertNull(playerAchievements.getLanguage());

		playerAchievements = ISteamUserStats.getPlayerAchievements(12345, 440, "");
		assertEquals("", playerAchievements.getLanguage());
	}

	@Test
	public void testGetPlayerAchievementsInvalidSteamId() throws WebApiException, JSONException, IOException {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("steamid", "123456578");
		params.put("appid", "440");

		WebApiException webApiException = new WebApiException(WebApiException.Cause.HTTP_ERROR, 400, "Bad Request");
		
		when(WebApi.getJSONObject("ISteamUserStats", "GetPlayerAchievements", 1, params)).thenThrow(webApiException);

		try {
			ISteamUserStats.getPlayerAchievements(123456578, 440);
			fail("Exception should be thrown when calling getPlayerAchievements with an invalid steamId.");
		} catch (Exception e) {
			assertEquals("The Web API request has failed due to an HTTP error: Bad Request (status code: 400).", e.getMessage());
		}
	}

	@Test
	public void testGetPlayerAchievementsInvalidAppId() throws WebApiException, JSONException, IOException {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("steamid", "12345");
		params.put("appid", "12345");

		WebApiException webApiException = new WebApiException(WebApiException.Cause.HTTP_ERROR, 400, "Bad Request");
		
		when(WebApi.getJSONObject("ISteamUserStats", "GetPlayerAchievements", 1, params)).thenThrow(webApiException);

		try {
			ISteamUserStats.getPlayerAchievements(12345, 12345);
			fail("Exception should be thrown when calling getPlayerAchievements with an invalid appId.");
		} catch (Exception e) {
			assertEquals("The Web API request has failed due to an HTTP error: Bad Request (status code: 400).", e.getMessage());
		}
	}

	@Test
	public void testGetPlayerAchievementsNoAchievements() throws WebApiException, JSONException, IOException {
		JSONObject playerAchievementsDocument = new JSONObject("{ \"playerstats\": { \"error\": \"Requested app has no stats\", \"success\": false } }");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("steamid", "12345");
		params.put("appid", "48240");

		when(WebApi.getJSONObject("ISteamUserStats", "GetPlayerAchievements", 1, params)).thenReturn(playerAchievementsDocument);

		try {
			ISteamUserStats.getPlayerAchievements(12345, 48240); //Anno 2070
			fail("Exception should be thrown when calling getPlayerAchievements with an invalid appId.");
		} catch (Exception e) {
			assertEquals("Requested app has no stats", e.getMessage());
		}
	}

	@Test
	public void testGetPlayerAchievementsInvalidResponse() throws WebApiException, JSONException, IOException {
		JSONObject playerAchievementsDocument = new JSONObject("{ \"playerstats\": { \"success\": true } }");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("steamid", "12345");
		params.put("appid", "48240");

		when(WebApi.getJSONObject("ISteamUserStats", "GetPlayerAchievements", 1, params)).thenReturn(playerAchievementsDocument);

		try {
			ISteamUserStats.getPlayerAchievements(12345, 48240);
			fail("Exception should be thrown when calling getPlayerAchievements with invalid JSON.");
		} catch (Exception e) {
			assertEquals("Could not parse JSON data.", e.getMessage());
		}
	}
	
	@Test
	public void testGetPlayerAchievementsInvalidNameAttribute() throws WebApiException, JSONException, IOException {
		JSONObject playerAchievementsDocument = new JSONObject("{	\"playerstats\": { \"steamID\": \"12345\", \"gameName\": \"Left 4 Dead 2\", \"achievements\": [ { \"thisShouldBe_apiname_or_name\": \"ACH_HONK_A_CLOWNS_NOSE\", \"achieved\": 1	} ], \"success\": true } }");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("steamid", "12345");
		params.put("appid", "48240");

		when(WebApi.getJSONObject("ISteamUserStats", "GetPlayerAchievements", 1, params)).thenReturn(playerAchievementsDocument);

		try {
			ISteamUserStats.getPlayerAchievements(12345, 48240);
			fail("Exception should be thrown when calling getPlayerAchievements with invalid JSON.");
		} catch (Exception e) {
			assertEquals("apiname or name not found for user achievement", e.getCause().getMessage());
		}
	}

	
	/* Tests for getStatsSchemaForGame */
    @Test
    public void testGameSchemaCache() throws Exception {
		JSONObject schemaForGameDocument = new JSONObject(loadFileAsString("/com/github/koraktor/steamcondenser/steam/webapi/services/ISteamUserStats/getSchemaForGame.v2.json"));
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("appid", "440");

		when(WebApi.getJSONObject("ISteamUserStats", "GetSchemaForGame", 2, params)).thenReturn(schemaForGameDocument);

		assertTrue(ISteamUserStats.getSchemaForGame(440) == ISteamUserStats.getSchemaForGame(440));
    }

	
	@Test
	public void testGetSchemaForGame() throws WebApiException, JSONException, IOException {
		JSONObject schemaForGameDocument = new JSONObject(loadFileAsString("/com/github/koraktor/steamcondenser/steam/webapi/services/ISteamUserStats/getSchemaForGame.v2.json"));
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("appid", "440");
		params.put("l", "en");

		when(WebApi.getJSONObject("ISteamUserStats", "GetSchemaForGame", 2, params)).thenReturn(schemaForGameDocument);

		GameStatsSchema gameStatsSchema = ISteamUserStats.getSchemaForGame(440, "en");

		assertEquals(440, gameStatsSchema.getAppId());
		assertEquals("en", gameStatsSchema.getLanguage());
		assertEquals("Team Fortress 2", gameStatsSchema.getGameName());
		assertEquals(323, gameStatsSchema.getGameVersion());
		assertTrue(gameStatsSchema.hasAchievements());
		assertTrue(gameStatsSchema.hasStats());
		assertEquals(130, gameStatsSchema.getAchievements().size());
		assertEquals(101, gameStatsSchema.getStats().size());

		GameAchievement gameAchievement = gameStatsSchema.getAchievement("TF_PYRO_BURN_SNIPERS_ZOOMED");
		assertEquals("TF_PYRO_BURN_SNIPERS_ZOOMED", gameAchievement.getName());
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
	public void testGetSchemaForGameNoLanguage() throws WebApiException, JSONException, IOException {
		JSONObject schemaForGameDocument = new JSONObject(loadFileAsString("/com/github/koraktor/steamcondenser/steam/webapi/services/ISteamUserStats/getSchemaForGame.v2.json"));
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("appid", "440");

		when(WebApi.getJSONObject("ISteamUserStats", "GetSchemaForGame", 2, params)).thenReturn(schemaForGameDocument);

		GameStatsSchema gameStatsSchema = ISteamUserStats.getSchemaForGame(440);
		assertNull(gameStatsSchema.getLanguage());

		ISteamUserStats.clearGameStatsSchemaCache();

		gameStatsSchema = ISteamUserStats.getSchemaForGame(440, "");
		assertEquals("", gameStatsSchema.getLanguage());
	}

	@Test
	public void testGetSchemaForGameInvalidAppId() throws WebApiException, JSONException, IOException {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("appid", "12345");

		WebApiException webApiException = new WebApiException(WebApiException.Cause.HTTP_ERROR, 400, "Bad Request");
		
		when(WebApi.getJSONObject("ISteamUserStats", "GetSchemaForGame", 2, params)).thenThrow(webApiException);

		try {
			ISteamUserStats.getSchemaForGame(12345);
			fail("Exception should be thrown when calling getSchemaForGame with an invalid appId.");
		} catch (Exception e) {
			assertEquals("The Web API request has failed due to an HTTP error: Bad Request (status code: 400).", e.getMessage());
		}
	}

	@Test
	public void testGetSchemaForGameEmptySchema() throws WebApiException, JSONException, IOException {
		JSONObject schemaForGameDocument = new JSONObject("{	\"game\": {	} }");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("appid", "440");

		when(WebApi.getJSONObject("ISteamUserStats", "GetSchemaForGame", 2, params)).thenReturn(schemaForGameDocument);

		GameStatsSchema gameStatsSchema = ISteamUserStats.getSchemaForGame(440);

		assertTrue(gameStatsSchema.isEmptySchema());
	}

	@Test
	public void testGetSchemaForGameNoStats() throws WebApiException, JSONException, IOException {
		JSONObject schemaForGameDocument = new JSONObject("{	\"game\": {	\"gameName\": \"testGameName\", \"gameVersion\": 1, \"availableGameStats\": { } } }");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("appid", "440");

		when(WebApi.getJSONObject("ISteamUserStats", "GetSchemaForGame", 2, params)).thenReturn(schemaForGameDocument);

		GameStatsSchema gameStatsSchema = ISteamUserStats.getSchemaForGame(440);

		assertFalse(gameStatsSchema.hasAchievements());
		assertFalse(gameStatsSchema.hasStats());
	}
	
	
	@Test
	public void testGetSchemaForGameNoGameVersion() throws WebApiException, JSONException, IOException {
		JSONObject schemaForGameDocument = new JSONObject("{	\"game\": {	\"gameName\": \"myGameNameWithoutVersion\" } }");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("appid", "440");

		when(WebApi.getJSONObject("ISteamUserStats", "GetSchemaForGame", 2, params)).thenReturn(schemaForGameDocument);

		try {
			ISteamUserStats.getSchemaForGame(440);
			fail("Exception should be thrown when calling getSchemaForGame with invalid JSON.");
		} catch (Exception e) {
			assertEquals("Could not parse JSON data.", e.getMessage());
		}
	}

	@Test
	public void testGetSchemaForGameNoGameName() throws WebApiException, JSONException, IOException {
		JSONObject schemaForGameDocument = new JSONObject("{	\"game\": {	\"gameVersion\": 12345 } }");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("appid", "440");

		when(WebApi.getJSONObject("ISteamUserStats", "GetSchemaForGame", 2, params)).thenReturn(schemaForGameDocument);

		try {
			ISteamUserStats.getSchemaForGame(440);
			fail("Exception should be thrown when calling getSchemaForGame with invalid JSON.");
		} catch (Exception e) {
			assertEquals("Could not parse JSON data.", e.getMessage());
		}
	}

	/* Tests for getUserStatsForGame */
	@Test
	public void testGetUserStatsForGame() throws WebApiException, JSONException, IOException {
		JSONObject userStatsForGameDocument = new JSONObject(loadFileAsString("/com/github/koraktor/steamcondenser/steam/webapi/services/ISteamUserStats/getUserStatsForGame.v2.json"));
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("steamid", "12345");
		params.put("appid", "440");

		when(WebApi.getJSONObject("ISteamUserStats", "GetUserStatsForGame", 2, params)).thenReturn(userStatsForGameDocument);

		UserStats userStats = ISteamUserStats.getUserStatsForGame(12345, 440);

		assertEquals(12345, userStats.getSteamId());
		assertEquals(440, userStats.getAppId());
		assertEquals("Team Fortress 2", userStats.getGameName());
		assertTrue(userStats.hasAchievements());
		assertTrue(userStats.hasStats());
		assertEquals(76, userStats.getAchievements().size());
		assertEquals(42, userStats.getStats().size());

		UserAchievement userAchievement = userStats.getAchievements().get(68);
		assertEquals("TF_PYRO_BURN_SNIPERS_ZOOMED", userAchievement.getName());
		assertTrue(userAchievement.isAchieved());

		UserStat userStat = userStats.getStats().get(5);
		assertEquals("Engineer.accum.iPlayTime", userStat.getName());
		assertEquals(93938, userStat.getValue());
	}

	@Test
	public void testGetUserStatsForGameInvalidSteamIdOrAppId() throws WebApiException, JSONException, IOException {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("steamid", "12345");
		params.put("appid", "440739");

		WebApiException webApiException = new WebApiException(WebApiException.Cause.HTTP_ERROR, 400, "Bad Request");
		
		when(WebApi.getJSONObject("ISteamUserStats", "GetUserStatsForGame", 2, params)).thenThrow(webApiException);

		try {
			ISteamUserStats.getUserStatsForGame(12345, 440739);
			fail("Exception should be thrown when calling getUserStatsForGame with an invalid appId or steamId.");
		} catch (Exception e) {
			assertEquals("The Web API request has failed due to an HTTP error: Bad Request (status code: 400).", e.getMessage());
		}

	}

	@Test
	public void testGetUserStatsForGameNoStatsOrAchievements() throws WebApiException, JSONException, IOException {
		JSONObject userStatsForGameDocument = new JSONObject("{ \"playerstats\": {	\"steamID\": \"12345\",	\"gameName\": \"AaaaaAAaaaAAAaaAAAAaAAAAA!!!\"	}}");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("steamid", "12345");
		params.put("appid", "440739");

		when(WebApi.getJSONObject("ISteamUserStats", "GetUserStatsForGame", 2, params)).thenReturn(userStatsForGameDocument);

		UserStats userStats = ISteamUserStats.getUserStatsForGame(12345, 440739);
		assertEquals("AaaaaAAaaaAAAaaAAAAaAAAAA!!!", userStats.getGameName());
		assertFalse(userStats.hasAchievements());
		assertFalse(userStats.hasStats());
	}

	@Test
	public void testGetUserStatsForGameInvalidResponse() throws WebApiException, JSONException, IOException {
		JSONObject userStatsForGameDocument = new JSONObject("{ \"playerstats\": {	\"steamID\": \"12345\" }}");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("steamid", "12345");
		params.put("appid", "440739");

		when(WebApi.getJSONObject("ISteamUserStats", "GetUserStatsForGame", 2, params)).thenReturn(userStatsForGameDocument);

		try {
			ISteamUserStats.getUserStatsForGame(12345, 440739);
			fail("Exception should be thrown when calling getUserStatsForGame with invalid JSON.");
		} catch (Exception e) {
			assertEquals("Could not parse JSON data.", e.getMessage());
		}
	}
}
