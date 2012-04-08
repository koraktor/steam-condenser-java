/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011, Guto Maia
 * Copyright (c) 2011, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.community;

import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;

import org.w3c.dom.Document;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Sebastian Staudt
 * @author Guto Maia
 */
public class SteamIdTest {

    private DocumentBuilder parser;

    @Before
    public void setUp() throws Exception {
        this.parser = mock(DocumentBuilder.class);

        XMLData.setDocumentBuilder(this.parser);
    }

    @Test
    public void testCache() throws Exception {
        Document steamIdDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this.getClass().getResourceAsStream("gutomaia-steamid.xml"));
        when(parser.parse("http://steamcommunity.com/id/gutomaia?xml=1")).thenReturn(steamIdDocument);

        SteamId.create("gutomaia", true);
        SteamId.create("gutomaia", true);

        verify(parser, times(1)).parse("http://steamcommunity.com/id/gutomaia?xml=1");
    }

    @Test
    public void testFetch() throws Exception {
        Document steamIdDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this.getClass().getResourceAsStream("gutomaia-steamid.xml"));
        when(parser.parse("http://steamcommunity.com/id/gutomaia?xml=1")).thenReturn(steamIdDocument);

        SteamId steamId = SteamId.create("gutomaia", true, true);

        assertEquals(76561197985077150L, steamId.getSteamId64());
        assertEquals("gutomaia", steamId.getNickname());
        assertEquals("gutomaia", steamId.getCustomUrl());
        assertEquals("Salvador, Bahia, Brazil", steamId.getLocation());
        assertEquals("http://media.steampowered.com/steamcommunity/public/images/avatars/56/566f5c7e9126864777b7d9d3cfe9f8e62e27f706_full.jpg", steamId.getAvatarFullUrl());
        assertEquals("http://media.steampowered.com/steamcommunity/public/images/avatars/56/566f5c7e9126864777b7d9d3cfe9f8e62e27f706.jpg", steamId.getAvatarIconUrl());
        assertEquals("http://media.steampowered.com/steamcommunity/public/images/avatars/56/566f5c7e9126864777b7d9d3cfe9f8e62e27f706_medium.jpg", steamId.getAvatarMediumUrl());

        assertEquals(false, steamId.isBanned());
        assertEquals(false, steamId.isInGame());
        assertEquals(false, steamId.isOnline());

        verify(parser).parse("http://steamcommunity.com/id/gutomaia?xml=1");
    }

    @Test
    public void getGames() throws Exception {
        Document gamesDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this.getClass().getResourceAsStream("gutomaia-games.xml"));
        when(parser.parse("http://steamcommunity.com/id/gutomaia/games?xml=1")).thenReturn(gamesDocument);

        SteamId steamId = SteamId.create("gutomaia", false);
        HashMap<Integer, SteamGame> games = steamId.getGames();

        verify(parser).parse("http://steamcommunity.com/id/gutomaia/games?xml=1");

        assertEquals(285, games.size());
    }

    @Test
    public void getFriends() throws Exception {
        Document friendsDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this.getClass().getResourceAsStream("gutomaia-friends.xml"));
        when(parser.parse("http://steamcommunity.com/id/gutomaia/friends?xml=1")).thenReturn(friendsDocument);

        SteamId steamId = SteamId.create("gutomaia", false);
        SteamId friends[] = steamId.getFriends();

        verify(parser).parse("http://steamcommunity.com/id/gutomaia/friends?xml=1");

        assertEquals(30, friends.length);
    }

}
