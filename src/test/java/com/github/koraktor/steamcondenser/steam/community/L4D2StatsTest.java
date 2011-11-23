/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2008-2011, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.community;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xerces.parsers.DOMParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Document;

import com.github.koraktor.steamcondenser.steam.community.l4d.L4D2Stats;

/**
 * @author Guto Maia
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ DocumentBuilderFactory.class, DocumentBuilder.class })
public class L4D2StatsTest {

	public Document loadXml(String file) throws Exception {
		try {
			DOMParser parser = new DOMParser();
			parser.parse("src/test/resources/" + file);
			return parser.getDocument();
		} catch (Exception e) {
			throw e;
		}
	}

	DocumentBuilder parser = mock(DocumentBuilder.class);
	DocumentBuilderFactory factory = mock(DocumentBuilderFactory.class);

	@Before
	public void init() throws Exception {
		mockStatic(DocumentBuilderFactory.class);
		when(DocumentBuilderFactory.newInstance()).thenReturn(factory);
		when(factory.newDocumentBuilder()).thenReturn(parser);
		when(parser.parse("http://steamcommunity.com/id/gutomaia?xml=1"))
				.thenReturn(loadXml("gutomaia.xml"));
		when(parser.parse("http://steamcommunity.com/id/gutomaia/games?xml=1"))
				.thenReturn(loadXml("gutomaia-games.xml"));
		when(
				parser.parse("http://steamcommunity.com/id/gutomaia/stats/l4d2?xml=all"))
				.thenReturn(loadXml("gutomaia-l4d2.xml"));
		SteamId steamId = SteamId.create("gutomaia", true, false);
		l4d2Stats = (L4D2Stats) steamId.getGameStats("l4d2");

	}

	L4D2Stats l4d2Stats;

	@Test
	public void getL4D2Stats() throws Exception {

		assertEquals("Left 4 Dead 2", l4d2Stats.getGameName());
		assertEquals("L4D2", l4d2Stats.getGameFriendlyName());
		assertEquals(550, l4d2Stats.getAppId());
		assertEquals("0s", l4d2Stats.getHoursPlayed());//TODO: strange behavior
		assertEquals(76561197985077150l, l4d2Stats.getSteamId64());
	}

	@Test
	public void achievements() throws Exception {
		assertEquals(7, l4d2Stats.getAchievementsDone());
	}

	@Test
	public void getDamagePercentages() throws Exception {
		assertEquals("9.9", l4d2Stats.getDamagePercentages().get("melee")
				.toString());
		assertEquals("28.8", l4d2Stats.getDamagePercentages().get("pistols")
				.toString());
		assertEquals("43.8", l4d2Stats.getDamagePercentages().get("rifles")
				.toString());
		assertEquals("17.0", l4d2Stats.getDamagePercentages().get("shotguns")
				.toString());
	}

	@Test
	public void getLifetimeStats() {
		assertEquals("0.0",
				l4d2Stats.getLifetimeStats().get("avgAdrenalineShared")
						.toString());
		assertEquals("0.263158",
				l4d2Stats.getLifetimeStats().get("avgAdrenalineUsed")
						.toString());
		assertEquals("0.157895",
				l4d2Stats.getLifetimeStats().get("avgDefibrillatorsUsed")
						.toString());
	}

}
