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
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.koraktor.steamcondenser.steam.community.l4d.L4D2Stats;

/**
 * @author Guto Maia
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ DocumentBuilderFactory.class, DocumentBuilder.class })
public class L4D2StatsTest extends StatsTestCase<L4D2Stats> {

	public L4D2StatsTest() {
		super("gutomaia", "l4d2");
	}

	@Test
	public void getL4D2Stats() throws Exception {

		assertEquals("Left 4 Dead 2", stats.getGameName());
		assertEquals("L4D2", stats.getGameFriendlyName());
		assertEquals(550, stats.getAppId());
		assertEquals("0s", stats.getHoursPlayed());// TODO: strange behavior
		assertEquals(76561197985077150l, stats.getSteamId64());
	}

	@Test
	public void achievements() throws Exception {
		assertEquals(7, stats.getAchievementsDone());
	}

	@Test
	public void getDamagePercentages() throws Exception {
		assertEquals("9.9", stats.getDamagePercentages().get("melee")
				.toString());
		assertEquals("28.8", stats.getDamagePercentages().get("pistols")
				.toString());
		assertEquals("43.8", stats.getDamagePercentages().get("rifles")
				.toString());
		assertEquals("17.0", stats.getDamagePercentages().get("shotguns")
				.toString());
	}

	@Test
	public void getLifetimeStats() {
		assertEquals("0.0", stats.getLifetimeStats().get("avgAdrenalineShared")
				.toString());
		assertEquals("0.263158",
				stats.getLifetimeStats().get("avgAdrenalineUsed").toString());
		assertEquals("0.157895",
				stats.getLifetimeStats().get("avgDefibrillatorsUsed")
						.toString());
	}

}
