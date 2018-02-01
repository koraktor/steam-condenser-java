/*
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2012-2018, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.servers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.SteamPlayer;
import com.github.koraktor.steamcondenser.steam.packets.A2S_INFO_Packet;
import com.github.koraktor.steamcondenser.steam.packets.A2S_PLAYER_Packet;
import com.github.koraktor.steamcondenser.steam.packets.A2S_RULES_Packet;
import com.github.koraktor.steamcondenser.steam.packets.S2A_INFO2_Packet;
import com.github.koraktor.steamcondenser.steam.packets.S2A_PLAYER_Packet;
import com.github.koraktor.steamcondenser.steam.packets.S2A_RULES_Packet;
import com.github.koraktor.steamcondenser.steam.packets.S2C_CHALLENGE_Packet;
import com.github.koraktor.steamcondenser.steam.packets.SteamPacket;
import com.github.koraktor.steamcondenser.steam.sockets.QuerySocket;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Sebastian Staudt
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(GameServer.class)
public class GameServerTest {

    private GameServer server;

    private QuerySocket socket;

    @Before
    public void setup() throws Exception {
        server = spy(new GenericGameServer());
        socket = mock(QuerySocket.class);
        server.socket = socket;
    }

    @Test
    public void testSendRequest() throws Exception {
        SteamPacket packet = mock(SteamPacket.class);

        server.sendRequest(packet);

        verify(socket).send(packet);
    }

    @Test
    public void testGetReply() throws Exception {
        SteamPacket packet = mock(SteamPacket.class);
        when(socket.getReply()).thenReturn(packet);

        assertEquals(packet, server.getReply());
    }

    @Test
    public void testUpdatePing() throws Exception {
        doAnswer(new Answer<SteamPacket>() {
            public SteamPacket answer(InvocationOnMock invocationOnMock) throws Throwable {
                Thread.sleep(50);
                return null;
            }
        }).when(server).getReply();

        server.updatePing();

        verify(socket).send(any(A2S_INFO_Packet.class));
        assertThat(server.getPing(), is(greaterThanOrEqualTo(50)));
    }

    @Test
    public void testUpdateChallengeNumber() throws Exception {
        doNothing().when(server).handleResponseForRequest(GameServer.REQUEST_CHALLENGE);

        server.updateChallengeNumber();

        verify(server).handleResponseForRequest(GameServer.REQUEST_CHALLENGE);
    }

    @Test
    public void testUpdateServerInfo() throws Exception {
        doNothing().when(server).handleResponseForRequest(GameServer.REQUEST_INFO);

        server.updateServerInfo();

        verify(server).handleResponseForRequest(GameServer.REQUEST_INFO);
    }

    @Test
    public void testUpdateRules() throws Exception {
        doNothing().when(server).handleResponseForRequest(GameServer.REQUEST_RULES);

        server.updateRules();

        verify(server).handleResponseForRequest(GameServer.REQUEST_RULES);
    }

    @Test
    public void testUpdatePlayers() throws Exception {
        doNothing().when(server).handleResponseForRequest(GameServer.REQUEST_PLAYER);

        server.updatePlayers();

        verify(server).handleResponseForRequest(GameServer.REQUEST_PLAYER);
    }

    @Test
    public void testInitialize() throws Exception {
        doNothing().when(server).updatePing();
        doNothing().when(server).updateServerInfo();
        doNothing().when(server).updateChallengeNumber();

        server.initialize();

        verify(server).updatePing();
        verify(server).updateServerInfo();
        verify(server).updateChallengeNumber();
    }

    @Test
    public void testIsRconAuthenticated() {
        assertEquals(server.rconAuthenticated, server.isRconAuthenticated());
    }

    @Test
    public void testCachePing() throws Exception {
        doAnswer(new Answer<Object>() {
            public SteamPacket answer(InvocationOnMock invocationOnMock) {
                ((GameServer) invocationOnMock.getMock()).ping = 1;
                return null;
            }
        }).when(server).updatePing();

        server.getPing();
        server.getPing();

        verify(server, times(1)).updatePing();
    }

    @Test
    public void testCachePlayers() throws Exception {
        doAnswer(new Answer<Object>() {
            public SteamPacket answer(InvocationOnMock invocationOnMock) {
                ((GameServer) invocationOnMock.getMock()).playerHash = new HashMap<String, SteamPlayer>();
                return null;
            }
        }).when(server).updatePlayers(null);

        server.getPlayers();
        server.getPlayers();

        verify(server, times(1)).updatePlayers(null);
    }

    @Test
    public void testCacheRules() throws Exception {
        doAnswer(new Answer<Object>() {
            public SteamPacket answer(InvocationOnMock invocationOnMock) {
                ((GameServer) invocationOnMock.getMock()).rulesHash = new HashMap<String, String>();
                return null;
            }
        }).when(server).updateRules();

        server.getRules();
        server.getRules();

        verify(server, times(1)).updateRules();
    }

    @Test
    public void testCacheServerInfo() throws Exception {
        doAnswer(new Answer<Object>() {
            public SteamPacket answer(InvocationOnMock invocationOnMock) {
                ((GameServer) invocationOnMock.getMock()).serverInfo = new HashMap<String, Object>();
                return null;
            }
        }).when(server).updateServerInfo();

        server.getServerInfo();
        server.getServerInfo();

        verify(server, times(1)).updateServerInfo();
    }

    @Test
    public void testPlayerInfoSourceWithPassword() throws Exception {
        String status = readFixture("status_source");

        SteamPlayer someone = mock(SteamPlayer.class);
        SteamPlayer somebody = mock(SteamPlayer.class);
        HashMap<String, SteamPlayer> playerMap = new HashMap<String, SteamPlayer>();
        playerMap.put("someone", someone);
        playerMap.put("somebody", somebody);
        server.playerHash = playerMap;

        doNothing().when(server).handleResponseForRequest(GameServer.REQUEST_PLAYER);
        when(server.rconExec("status")).thenReturn(status);

        HashMap<String, String> someoneData = new HashMap<String, String>();
        someoneData.put("name", "someone");
        someoneData.put("userid", "1");
        someoneData.put("uniqueid", "STEAM_0:0:123456");
        someoneData.put("score", "10");
        someoneData.put("time", "3:52");
        someoneData.put("ping", "12");
        someoneData.put("loss", "0");
        someoneData.put("state", "active");
        HashMap<String, String> somebodyData = new HashMap<String, String>();
        somebodyData.put("name", "somebody");
        somebodyData.put("userid", "2");
        somebodyData.put("uniqueid", "STEAM_0:0:123457");
        somebodyData.put("score", "3");
        somebodyData.put("time", "2:42");
        somebodyData.put("ping", "34");
        somebodyData.put("loss", "0");
        somebodyData.put("state", "active");

        server.updatePlayers("password");

        verify(server).rconAuth("password");
        verify(someone).addInformation(someoneData);
        verify(somebody).addInformation(somebodyData);
    }

    @Test
    public void testPlayerInfoSourceAuthenticated() throws Exception {
        String status = readFixture("status_source");

        SteamPlayer someone = mock(SteamPlayer.class);
        SteamPlayer somebody = mock(SteamPlayer.class);
        HashMap<String, SteamPlayer> playerMap = new HashMap<String, SteamPlayer>();
        playerMap.put("someone", someone);
        playerMap.put("somebody", somebody);
        server.playerHash = playerMap;
        server.rconAuthenticated = true;

        doNothing().when(server).handleResponseForRequest(GameServer.REQUEST_PLAYER);
        when(server.rconExec("status")).thenReturn(status);

        HashMap<String, String> someoneData = new HashMap<String, String>();
        someoneData.put("name", "someone");
        someoneData.put("userid", "1");
        someoneData.put("uniqueid", "STEAM_0:0:123456");
        someoneData.put("score", "10");
        someoneData.put("time", "3:52");
        someoneData.put("ping", "12");
        someoneData.put("loss", "0");
        someoneData.put("state", "active");
        HashMap<String, String> somebodyData = new HashMap<String, String>();
        somebodyData.put("name", "somebody");
        somebodyData.put("userid", "2");
        somebodyData.put("uniqueid", "STEAM_0:0:123457");
        somebodyData.put("score", "3");
        somebodyData.put("time", "2:42");
        somebodyData.put("ping", "34");
        somebodyData.put("loss", "0");
        somebodyData.put("state", "active");

        server.updatePlayers();

        verify(someone).addInformation(someoneData);
        verify(somebody).addInformation(somebodyData);
    }

    @Test
    public void testPlayerInfoGoldSrcWithPassword() throws Exception {
        String status = readFixture("status_goldsrc");

        SteamPlayer someone = mock(SteamPlayer.class);
        SteamPlayer somebody = mock(SteamPlayer.class);
        HashMap<String, SteamPlayer> playerMap = new HashMap<String, SteamPlayer>();
        playerMap.put("someone", someone);
        playerMap.put("somebody", somebody);
        server.playerHash = playerMap;

        doNothing().when(server).handleResponseForRequest(GameServer.REQUEST_PLAYER);
        when(server.rconExec("status")).thenReturn(status);

        HashMap<String, String> someoneData = new HashMap<String, String>();
        someoneData.put("name", "someone");
        someoneData.put("userid", "1");
        someoneData.put("uniqueid", "STEAM_0:0:123456");
        someoneData.put("score", "10");
        someoneData.put("time", "3:52");
        someoneData.put("ping", "12");
        someoneData.put("loss", "0");
        someoneData.put("adr", "0");
        HashMap<String, String> somebodyData = new HashMap<String, String>();
        somebodyData.put("name", "somebody");
        somebodyData.put("userid", "2");
        somebodyData.put("uniqueid", "STEAM_0:0:123457");
        somebodyData.put("score", "3");
        somebodyData.put("time", "2:42");
        somebodyData.put("ping", "34");
        somebodyData.put("loss", "0");
        somebodyData.put("adr", "0");

        server.updatePlayers("password");

        verify(server).rconAuth("password");
        verify(someone).addInformation(someoneData);
        verify(somebody).addInformation(somebodyData);
    }

    @Test
    public void testHandleChallengeRequests() throws Exception {
        S2C_CHALLENGE_Packet packet = mock(S2C_CHALLENGE_Packet.class);
        when(packet.getChallengeNumber()).thenReturn(1234);
        when(server.getReply()).thenReturn(packet);

        server.handleResponseForRequest(GameServer.REQUEST_CHALLENGE);

        assertEquals(1234, server.challengeNumber);
        verify(server).sendRequest(any(A2S_PLAYER_Packet.class));
    }

    @Test
    public void testHandleInfoRequests() throws Exception {
        S2A_INFO2_Packet packet = mock(S2A_INFO2_Packet.class);
        HashMap<String, Object> infoMap = new HashMap<String, Object>();
        infoMap.put("test", "test");
        when(packet.getInfo()).thenReturn(infoMap);
        when(server.getReply()).thenReturn(packet);

        server.handleResponseForRequest(GameServer.REQUEST_INFO);

        assertEquals("test", server.serverInfo.get("test"));
        verify(server).sendRequest(any(A2S_INFO_Packet.class));
    }

    @Test
    public void testHandleRulesRequests() throws Exception {
        S2A_RULES_Packet packet = mock(S2A_RULES_Packet.class);
        HashMap<String, String> rulesMap = new HashMap<String, String>();
        rulesMap.put("test", "test");
        when(packet.getRulesHash()).thenReturn(rulesMap);
        when(server.getReply()).thenReturn(packet);

        server.handleResponseForRequest(GameServer.REQUEST_RULES);

        assertEquals("test", server.rulesHash.get("test"));
        verify(server).sendRequest(any(A2S_RULES_Packet.class));
    }

    @Test
    public void testHandlePlayerRequests() throws Exception {
        SteamPlayer player = mock(SteamPlayer.class);
        S2A_PLAYER_Packet packet = mock(S2A_PLAYER_Packet.class);
        HashMap<String, SteamPlayer> playerMap = new HashMap<String, SteamPlayer>();
        playerMap.put("test", player);
        when(packet.getPlayerHash()).thenReturn(playerMap);
        when(server.getReply()).thenReturn(packet);

        server.handleResponseForRequest(GameServer.REQUEST_PLAYER);

        assertEquals(player, server.playerHash.get("test"));
        verify(server).sendRequest(any(A2S_PLAYER_Packet.class));
    }

    @Test
    public void testHandleUnexpectedResponses() throws Exception {
        S2A_INFO2_Packet packet1 = mock(S2A_INFO2_Packet.class);
        HashMap<String, Object> infoMap = new HashMap<String, Object>();
        infoMap.put("test", "test");
        when(packet1.getInfo()).thenReturn(infoMap);
        SteamPlayer player = mock(SteamPlayer.class);
        S2A_PLAYER_Packet packet2 = mock(S2A_PLAYER_Packet.class);
        HashMap<String, SteamPlayer> playerMap = new HashMap<String, SteamPlayer>();
        playerMap.put("test", player);
        when(packet2.getPlayerHash()).thenReturn(playerMap);
        when(server.getReply()).thenReturn(packet1).thenReturn(packet2);

        server.handleResponseForRequest(GameServer.REQUEST_PLAYER);

        assertEquals("test", server.serverInfo.get("test"));
        assertEquals(player, server.playerHash.get("test"));
        verify(server, times(2)).sendRequest(any(A2S_PLAYER_Packet.class));
    }

    private String readFixture(String fixture) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fixture)));
        StringBuilder result = new StringBuilder();
        while(reader.ready()) {
            result.append(reader.readLine()).append("\n");
        }

        return result.toString();
    }

    class GenericGameServer extends GameServer {

        public GenericGameServer() throws SteamCondenserException, UnknownHostException {
            super(InetAddress.getByAddress(new byte[]{127, 0, 0, 1}), 27015);
        }

        public void initSocket() {}

        public boolean rconAuth(String password) {
            return true;
        }

        public String rconExec(String command) {
            return "";
        }

    }

}
