/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011, Guto Maia
 * Copyright (c) 2011-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.w3c.dom.Document;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * @author Sebastian Staudt
 * @author Guto Maia
 */
@PrepareForTest(WebApi.class)
@RunWith(PowerMockRunner.class)
public class SteamIdTest {

    private DocumentBuilder parser;

    @Before
    public void setUp() throws Exception {
        this.parser = mock(DocumentBuilder.class);
        SteamId.clearCache();

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
        InputStream friendStream = this.getClass().getResourceAsStream("koraktor-friends.json");

        mockStatic(WebApi.class);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("relationship", "friend");
        params.put("steamid", 76561197961384956L);
        byte[] jsonData = new byte[friendStream.available()];
        new DataInputStream(friendStream).readFully(jsonData);
        String json = new String(jsonData);
        when(WebApi.getJSON("ISteamUser", "GetFriendList", 1, params)).thenReturn(json);

        SteamId steamId = SteamId.create("koraktor", false);
        steamId.steamId64 = 76561197961384956L;
        List<SteamId> friends = steamId.getFriends();

        assertEquals(76561197960299808L, friends.get(0).getSteamId64());
        assertEquals(76561198037444976L, friends.get(friends.size() - 1).getSteamId64());
        assertEquals(43, friends.size());
    }

    @Test
    public void testGetId() throws Exception {
        SteamId steamId1 = SteamId.create(76561197983311154L, false);
        SteamId steamId2= SteamId.create("Son_of_Thor", false);

        assertThat((Long) steamId1.getId(), is(equalTo(76561197983311154L)));
        assertThat((String) steamId2.getId(), is(equalTo("Son_of_Thor")));
    }

    @Test
    public void testResolveVanityUrlSuccess() throws Exception {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("vanityurl", "koraktor");

        mockStatic(WebApi.class);
        when(WebApi.getJSON("ISteamUser", "ResolveVanityURL", 1, params)).
            thenReturn("{ \"response\": { \"success\": 1, \"steamid\": \"76561197961384956\" } }");

        Long steamID64 = SteamId.resolveVanityUrl("koraktor");
        assertThat(steamID64, is(equalTo(76561197961384956L)));
    }

    @Test
    public void testResolveVanityUrlFailure() throws Exception {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("vanityurl", "unknown");

        mockStatic(WebApi.class);
        when(WebApi.getJSON("ISteamUser", "ResolveVanityURL", 1, params)).
            thenReturn("{ \"response\": { \"success\": 42 } }");

        Long steamID64 = SteamId.resolveVanityUrl("unknown");
        assertThat(steamID64, is(eq(null)));
    }

}
