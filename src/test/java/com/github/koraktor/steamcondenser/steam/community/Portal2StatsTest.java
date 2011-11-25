/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2008-2011, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.community;

import static com.github.koraktor.steamcondenser.steam.community.XMLUtil.loadXml;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.koraktor.steamcondenser.steam.community.portal2.Portal2Stats;


/**
 * @author Guto Maia
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ DocumentBuilderFactory.class, DocumentBuilder.class })
public class Portal2StatsTest extends StatsTestCase<Portal2Stats>{

	public Portal2StatsTest() {
		super("gutomaia", "portal2");
	}

	@Test
	public void getPortal2Stats() throws Exception {
		assertEquals("Portal 2", stats.getGameName());
		assertEquals("Portal2", stats.getGameFriendlyName());
		assertEquals(620, stats.getAppId());
		assertEquals("0", stats.getHoursPlayed());
		assertEquals(76561197985077150l, stats.getSteamId64());
	}
	
	@Test
	public void achievements() throws Exception {
		assertEquals(17, stats.getAchievementsDone());
	}
}