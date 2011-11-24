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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Sebastian Staudt
 * @author Guto Maia
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ DocumentBuilderFactory.class, DocumentBuilder.class })
public class SteamCommunityTest {

	DocumentBuilder parser = mock(DocumentBuilder.class);
	DocumentBuilderFactory factory = mock(DocumentBuilderFactory.class);

	@Before
	public void setUp() throws Exception {
		mockStatic(DocumentBuilderFactory.class);
		when(DocumentBuilderFactory.newInstance()).thenReturn(factory);
		when(factory.newDocumentBuilder()).thenReturn(parser);

	}
		
	@Test
	public void testCache() throws Exception {
		when(parser.parse("http://steamcommunity.com/id/gutomaia?xml=1"))
		.thenReturn(loadXml("gutomaia.xml"), loadXml("gutomaia.xml"));
		
		SteamId.create("gutomaia", true, true);
		SteamId.create("gutomaia", true, true);
		
		verify(factory, times(2)).newDocumentBuilder();
		verify(parser, times(2)).parse("http://steamcommunity.com/id/gutomaia?xml=1");
	}

	/**
	 * This test tries to aquire information from a online Steam ID. This test
	 * only passes if the parsing of the XML document works
	 */
	@Test
	public void steamIdByCustomUrl() throws Exception {
		when(parser.parse("http://steamcommunity.com/id/gutomaia?xml=1"))
				.thenReturn(loadXml("gutomaia.xml"));

		SteamId steamId = SteamId.create("gutomaia", true, true);

		verify(factory).newDocumentBuilder();
		verify(parser).parse("http://steamcommunity.com/id/gutomaia?xml=1");

		assertEquals(76561197985077150l, steamId.getSteamId64());
		assertEquals("gutomaia", steamId.getNickname());
		assertEquals("gutomaia", steamId.getCustomUrl());
		assertEquals(
				"http://media.steampowered.com/steamcommunity/public/images/avatars/56/566f5c7e9126864777b7d9d3cfe9f8e62e27f706_full.jpg",
				steamId.getAvatarFullUrl());
		assertEquals(
				"http://media.steampowered.com/steamcommunity/public/images/avatars/56/566f5c7e9126864777b7d9d3cfe9f8e62e27f706.jpg",
				steamId.getAvatarIconUrl());
		assertEquals(
				"http://media.steampowered.com/steamcommunity/public/images/avatars/56/566f5c7e9126864777b7d9d3cfe9f8e62e27f706_medium.jpg",
				steamId.getAvatarMediumUrl());

		assertEquals(false, steamId.isBanned());
		assertEquals(false, steamId.isInGame());
		assertEquals(false, steamId.isOnline());

		assertEquals("Salvador, Bahia, Brazil", steamId.getLocation());
	}

	/**
	 * This test tries to aquire information from a online Steam ID. This test
	 * only passes if the parsing of the XML document works
	 */
	@Test
	public void steamIdBySteamId64() throws Exception {

		when(parser.parse("http://steamcommunity.com/profiles/76561197985077150?xml=1"))
				.thenReturn(loadXml("gutomaia.xml"));

		SteamId steamId = SteamId.create(76561197985077150l, true, true);

		verify(factory).newDocumentBuilder();
		verify(parser).parse("http://steamcommunity.com/profiles/76561197985077150?xml=1");

		assertEquals(76561197985077150l, steamId.getSteamId64());
		assertEquals("gutomaia", steamId.getNickname());
		assertEquals("gutomaia", steamId.getCustomUrl());
		assertEquals(
				"http://media.steampowered.com/steamcommunity/public/images/avatars/56/566f5c7e9126864777b7d9d3cfe9f8e62e27f706_full.jpg",
				steamId.getAvatarFullUrl());
		assertEquals(
				"http://media.steampowered.com/steamcommunity/public/images/avatars/56/566f5c7e9126864777b7d9d3cfe9f8e62e27f706.jpg",
				steamId.getAvatarIconUrl());
		assertEquals(
				"http://media.steampowered.com/steamcommunity/public/images/avatars/56/566f5c7e9126864777b7d9d3cfe9f8e62e27f706_medium.jpg",
				steamId.getAvatarMediumUrl());

		assertEquals(false, steamId.isBanned());
		assertEquals(false, steamId.isInGame());
		assertEquals(false, steamId.isOnline());

		assertEquals("Salvador, Bahia, Brazil", steamId.getLocation());
	}

	@Test
	public void getGames() throws Exception {
		when(parser.parse("http://steamcommunity.com/id/gutomaia?xml=1"))
				.thenReturn(loadXml("gutomaia.xml"));
		when(parser.parse("http://steamcommunity.com/id/gutomaia/games?xml=1"))
				.thenReturn(loadXml("gutomaia-games.xml"));

		SteamId steamId = SteamId.create("gutomaia", true, true);
		HashMap<Integer, SteamGame> games = steamId.getGames();
		
		verify(factory, times(2)).newDocumentBuilder();
		verify(parser).parse("http://steamcommunity.com/id/gutomaia?xml=1");
		verify(parser).parse("http://steamcommunity.com/id/gutomaia/games?xml=1");

		assertEquals(285, games.size());
	}

	@Test
	public void getFriends() throws Exception {
		when(parser.parse("http://steamcommunity.com/id/gutomaia?xml=1"))
				.thenReturn(loadXml("gutomaia.xml"));
		when(
				parser.parse("http://steamcommunity.com/id/gutomaia/friends?xml=1"))
				.thenReturn(loadXml("gutomaia-friends.xml"));

		SteamId steamId = SteamId.create("gutomaia", true, true);
		SteamId friends[] = steamId.getFriends();

		verify(parser).parse("http://steamcommunity.com/id/gutomaia?xml=1");
		verify(parser).parse("http://steamcommunity.com/id/gutomaia/friends?xml=1");

		assertEquals(30, friends.length);
	}

	@Test
	public void groupByCustomUrl() throws Exception {
		when(
				parser.parse("http://steamcommunity.com/groups/gutonet/memberslistxml?p=1"))
				.thenReturn(loadXml("gutonet-memberslistxml.xml"));
		when(
				parser.parse("http://steamcommunity.com/profiles/76561197985077150?xml=1"))
				.thenReturn(loadXml("gutomaia.xml"));

		SteamGroup group = SteamGroup.create("gutonet");

		//TODO: cache for groups not working
		//verify(parser).parse("http://steamcommunity.com/groups/gutonet/memberslistxml?p=1");
		//verify(parser).parse("http://steamcommunity.com/profiles/76561197985077150?xml=1");

		assertEquals(1, group.getMemberCount());
	}

	@Test
	public void groupByGroupId64() throws Exception {
		when(
				parser.parse("http://steamcommunity.com/gid/103582791429521412/memberslistxml?p=1"))
				.thenReturn(loadXml("gutonet-memberslistxml.xml"));

		when(
				parser.parse("http://steamcommunity.com/profiles/76561197985077150?xml=1"))
				.thenReturn(loadXml("gutomaia.xml"));

		SteamGroup group = SteamGroup.create(103582791429521412L);

		//TODO: cache for groups not working
		//verify(parser).parse("http://steamcommunity.com/gid/103582791429521412/memberslistxml?p=1");
		//verify(parser).parse("http://steamcommunity.com/profiles/76561197985077150?xml=1");

		assertEquals(1, group.getMemberCount());
	}

}
