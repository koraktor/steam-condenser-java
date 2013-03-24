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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.github.koraktor.steamcondenser.steam.community.apps.ServerAtAddress;
import com.github.koraktor.steamcondenser.steam.community.apps.UpToDateCheck;
import com.github.koraktor.steamcondenser.steam.webapi.exceptions.DataException;
import com.github.koraktor.steamcondenser.steam.webapi.exceptions.ParseException;

/**
 * @author Lewis Keen
 * @author Sebastian Staudt
 */
public class AppsBuilderTest {
	private AppsBuilder appsBuilder;

	@Before
	public void setup() {
		appsBuilder = new AppsBuilder();
	}

	private String loadFileAsString(String path) throws IOException {
		URL resource = this.getClass().getResource(path);
		File resourceFile = new File(resource.getFile());
		return FileUtils.readFileToString(resourceFile, "UTF-8");
	}

	@Test
	public void testBuildAppList() throws JSONException, IOException, ParseException {
		JSONObject appListDocument = new JSONObject(loadFileAsString("ISteamApps/getAppList.v2.json"));

		Map<Long, String> appList = appsBuilder.buildAppList(appListDocument);

		assertEquals(2746, appList.size());
		assertEquals("Team Fortress 2", appList.get(440L));
	}
	
	@Test
	public void testBuildAppListInvalidJSON() throws JSONException {
		JSONObject appListDocument = new JSONObject("{ }");

		try {
			 appsBuilder.buildAppList(appListDocument);
			fail("Exception should be thrown when calling build app list with invalid JSON.");
		} catch (Exception e) {
			assertEquals("Could not parse JSON data.", e.getMessage());
		}
	}

	@Test
	public void testUpToDateCheckFalse() throws JSONException, IOException, ParseException {
		JSONObject upToDateCheckDocument = new JSONObject(loadFileAsString("ISteamApps/UpToDateCheck.v1.json"));

		UpToDateCheck upToDateCheck = appsBuilder.buildUpToDateCheck(440, 23, upToDateCheckDocument);

		assertEquals(440, upToDateCheck.getAppId());
		assertFalse(upToDateCheck.isUpToDate());
		assertFalse(upToDateCheck.isVersionIsListable());
		assertEquals(new Integer(1258), upToDateCheck.getRequiredVersion());
		assertEquals("Your server is out of date, please upgrade", upToDateCheck.getMessage());
	}
	
	@Test
	public void testUpToDateCheckTrue() throws JSONException, IOException, ParseException {
		JSONObject upToDateCheckDocument = new JSONObject("{ \"response\": { \"success\": true, \"up_to_date\": true,	\"version_is_listable\": true } }");

		UpToDateCheck upToDateCheck = appsBuilder.buildUpToDateCheck(440, 23, upToDateCheckDocument);

		assertTrue(upToDateCheck.isUpToDate());
		assertTrue(upToDateCheck.isVersionIsListable());
		assertNull(upToDateCheck.getRequiredVersion());
		assertNull(upToDateCheck.getMessage());
	}

	@Test
	public void testUpToDateCheckInvalidJSON() throws JSONException {
		JSONObject upToDateCheckDocument = new JSONObject("{ }");

		try {
			appsBuilder.buildUpToDateCheck(440, 23, upToDateCheckDocument);
			fail("Exception should be thrown when calling up to date check builder with invalid JSON.");
		} catch (Exception e) {
			assertEquals("Could not parse JSON data.", e.getMessage());
		}
	}
	
	@Test
	public void testBuildServersAtAddress() throws JSONException, IOException, DataException, ParseException {
		JSONObject serversAtAddressDocument = new JSONObject(loadFileAsString("ISteamApps/GetServersAtAddress.v1.json"));

		List<ServerAtAddress> serversAtAddress = appsBuilder.buildServersAtAddress("85.236.100.104", serversAtAddressDocument);
		
		assertEquals(7, serversAtAddress.size());
		ServerAtAddress server = serversAtAddress.get(3);
		assertEquals("85.236.100.104:27915", server.getAddress());
		assertEquals(65534, server.getGmsIndex());
		assertEquals(440, server.getAppId());
		assertEquals("tf", server.getGameDir());
		assertEquals(3, server.getRegion());
		assertTrue(server.isSecure());
		assertFalse(server.isLan());
		assertEquals(27915, server.getGamePort());
		assertEquals(0, server.getSpecPort());
	}
	
	@Test
	public void testBuildServersAtAddressInvalidIp() throws JSONException, IOException, ParseException {
		JSONObject serversAtAddressDocument = new JSONObject("{	\"response\": {	\"success\": false,	\"message\": true } }");

		try {
			appsBuilder.buildServersAtAddress("invalidIp", serversAtAddressDocument);
			fail("Exception should be thrown when calling build servers at address with an invalid IP.");
		} catch (DataException e) {
			assertEquals("Invalid IP address: invalidIp", e.getMessage());
		}
	}

	@Test
	public void testBuildServersAtAddressInvalidJSON() throws JSONException, DataException {
		JSONObject serversAtAddressDocument = new JSONObject("{ }");

		try {
			appsBuilder.buildServersAtAddress("85.236.100.104", serversAtAddressDocument);
			fail("Exception should be thrown when calling build servers at address with invalid JSON.");
		} catch (ParseException e) {
			assertEquals("Could not parse JSON data.", e.getMessage());
		}
	}
}
