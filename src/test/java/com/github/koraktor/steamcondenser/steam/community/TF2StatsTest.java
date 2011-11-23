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

import com.github.koraktor.steamcondenser.steam.community.tf2.TF2Stats;

/**
 * @author Guto Maia
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ DocumentBuilderFactory.class, DocumentBuilder.class })
public class TF2StatsTest {
	
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
				parser.parse("http://steamcommunity.com/id/gutomaia/stats/tf2?xml=all"))
				.thenReturn(loadXml("gutomaia-tf2.xml"));
		SteamId steamId = SteamId.create("gutomaia", true, false);
		tf2Stats = (TF2Stats) steamId.getGameStats("tf2");

	}
	
	TF2Stats tf2Stats;

	@Test
	public void getTF2Stats() throws Exception {
		assertEquals("Team Fortress 2", tf2Stats.getGameName());
		assertEquals("TF2", tf2Stats.getGameFriendlyName());
		assertEquals(440, tf2Stats.getAppId());
	}

	@Test
	public void achievements() throws Exception {
		assertEquals(314, tf2Stats.getAchievementsDone());
	}

}
