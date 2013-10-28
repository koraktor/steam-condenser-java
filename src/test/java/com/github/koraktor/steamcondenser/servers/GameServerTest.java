/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2012-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.servers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.servers.packets.A2S_INFO_Packet;
import com.github.koraktor.steamcondenser.servers.packets.A2S_PLAYER_Packet;
import com.github.koraktor.steamcondenser.servers.packets.A2S_RULES_Packet;
import com.github.koraktor.steamcondenser.servers.packets.S2A_INFO2_Packet;
import com.github.koraktor.steamcondenser.servers.packets.S2A_PLAYER_Packet;
import com.github.koraktor.steamcondenser.servers.packets.S2A_RULES_Packet;
import com.github.koraktor.steamcondenser.servers.packets.S2C_CHALLENGE_Packet;
import com.github.koraktor.steamcondenser.servers.packets.SteamPacket;
import com.github.koraktor.steamcondenser.servers.sockets.QuerySocket;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

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
        this.server = spy(new GenericGameServer());
        this.socket = mock(QuerySocket.class);
        this.server.socket = this.socket;
    }

    @Test
    public void testSendRequest() throws Exception {
        SteamPacket packet = mock(SteamPacket.class);

        this.server.sendRequest(packet);

        verify(this.socket).send(packet);
    }

    @Test
    public void testGetReply() throws Exception {
        SteamPacket packet = mock(SteamPacket.class);
        when(this.socket.getReply()).thenReturn(packet);

        assertEquals(packet, this.server.getReply());
    }

    @Test
    public void testUpdatePing() throws Exception {
        doAnswer(new Answer<SteamPacket>() {
            public SteamPacket answer(InvocationOnMock invocationOnMock) throws Throwable {
                Thread.sleep(50);
                return null;
            }
        }).when(this.server).getReply();

        this.server.updatePing();

        verify(this.socket).send(any(A2S_INFO_Packet.class));
        assertThat(this.server.getPing(), is(greaterThanOrEqualTo(50)));
    }

    @Test
    public void testUpdateChallengeNumber() throws Exception {
        doNothing().when(this.server).handleResponseForRequest(GameServer.REQUEST_CHALLENGE);

        this.server.updateChallengeNumber();

        verify(this.server).handleResponseForRequest(GameServer.REQUEST_CHALLENGE);
    }

    @Test
    public void testUpdateServerInfo() throws Exception {
        doNothing().when(this.server).handleResponseForRequest(GameServer.REQUEST_INFO);

        this.server.updateServerInfo();

        verify(this.server).handleResponseForRequest(GameServer.REQUEST_INFO);
    }

    @Test
    public void testUpdateRules() throws Exception {
        doNothing().when(this.server).handleResponseForRequest(GameServer.REQUEST_RULES);

        this.server.updateRules();

        verify(this.server).handleResponseForRequest(GameServer.REQUEST_RULES);
    }

    @Test
    public void testUpdatePlayers() throws Exception {
        doNothing().when(this.server).handleResponseForRequest(GameServer.REQUEST_PLAYER);

        this.server.updatePlayers();

        verify(this.server).handleResponseForRequest(GameServer.REQUEST_PLAYER);
    }

    @Test
    public void testInitialize() throws Exception {
        doNothing().when(this.server).updatePing();
        doNothing().when(this.server).updateServerInfo();
        doNothing().when(this.server).updateChallengeNumber();

        this.server.initialize();

        verify(this.server).updatePing();
        verify(this.server).updateServerInfo();
        verify(this.server).updateChallengeNumber();
    }

    @Test
    public void testIsRconAuthenticated() {
        assertEquals(this.server.rconAuthenticated, this.server.isRconAuthenticated());
    }

    @Test
    public void testCachePing() throws Exception {
        doAnswer(new Answer<Object>() {
            public SteamPacket answer(InvocationOnMock invocationOnMock) throws Throwable {
                ((GameServer) invocationOnMock.getMock()).ping = 1;
                return null;
            }
        }).when(this.server).updatePing();

        this.server.getPing();
        this.server.getPing();

        verify(this.server, times(1)).updatePing();
    }

    @Test
    public void testCachePlayers() throws Exception {
        doAnswer(new Answer<Object>() {
            public SteamPacket answer(InvocationOnMock invocationOnMock) throws Throwable {
                ((GameServer) invocationOnMock.getMock()).playerHash = new HashMap<String, SteamPlayer>();
                return null;
            }
        }).when(this.server).updatePlayers(null);

        this.server.getPlayers();
        this.server.getPlayers();

        verify(this.server, times(1)).updatePlayers(null);
    }

    @Test
    public void testCacheRules() throws Exception {
        doAnswer(new Answer<Object>() {
            public SteamPacket answer(InvocationOnMock invocationOnMock) throws Throwable {
                ((GameServer) invocationOnMock.getMock()).rulesHash = new HashMap<String, String>();
                return null;
            }
        }).when(this.server).updateRules();

        this.server.getRules();
        this.server.getRules();

        verify(this.server, times(1)).updateRules();
    }

    @Test
    public void testCacheServerInfo() throws Exception {
        doAnswer(new Answer<Object>() {
            public SteamPacket answer(InvocationOnMock invocationOnMock) throws Throwable {
                ((GameServer) invocationOnMock.getMock()).serverInfo = new HashMap<String, Object>();
                return null;
            }
        }).when(this.server).updateServerInfo();

        this.server.getServerInfo();
        this.server.getServerInfo();

        verify(this.server, times(1)).updateServerInfo();
    }

    @Test
    public void testPlayerInfoSourceWithPassword() throws Exception {
        String status = this.readFixture("status_source");

        SteamPlayer someone = mock(SteamPlayer.class);
        SteamPlayer somebody = mock(SteamPlayer.class);
        HashMap<String, SteamPlayer> playerMap = new HashMap<String, SteamPlayer>();
        playerMap.put("someone", someone);
        playerMap.put("somebody", somebody);
        this.server.playerHash = playerMap;

        doNothing().when(this.server).handleResponseForRequest(GameServer.REQUEST_PLAYER);
        when(this.server.rconExec("status")).thenReturn(status);

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

        mockStatic(GameServer.class);
        ArrayList<String> attributes = new ArrayList<String>();
        when(GameServer.getPlayerStatusAttributes("userid name           uniqueid            score connected ping loss state")).thenReturn(attributes);
        when(GameServer.splitPlayerStatus(attributes, "1 \"someone\"      STEAM_0:0:123456    10    3:52      12   0    active")).thenReturn(someoneData);
        when(GameServer.splitPlayerStatus(attributes, "2 \"somebody\"     STEAM_0:0:123457    3     2:42      34   0    active")).thenReturn(somebodyData);

        this.server.updatePlayers("password");

        verify(this.server).rconAuth("password");
        verify(someone).addInformation(someoneData);
        verify(somebody).addInformation(somebodyData);
    }

    @Test
    public void testPlayerInfoSourceAuthenticated() throws Exception {
        String status = this.readFixture("status_source");

        SteamPlayer someone = mock(SteamPlayer.class);
        SteamPlayer somebody = mock(SteamPlayer.class);
        HashMap<String, SteamPlayer> playerMap = new HashMap<String, SteamPlayer>();
        playerMap.put("someone", someone);
        playerMap.put("somebody", somebody);
        this.server.playerHash = playerMap;
        this.server.rconAuthenticated = true;

        doNothing().when(this.server).handleResponseForRequest(GameServer.REQUEST_PLAYER);
        when(this.server.rconExec("status")).thenReturn(status);

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

        mockStatic(GameServer.class);
        ArrayList<String> attributes = new ArrayList<String>();
        when(GameServer.getPlayerStatusAttributes("userid name           uniqueid            score connected ping loss state")).thenReturn(attributes);
        when(GameServer.splitPlayerStatus(attributes, "1 \"someone\"      STEAM_0:0:123456    10    3:52      12   0    active")).thenReturn(someoneData);
        when(GameServer.splitPlayerStatus(attributes, "2 \"somebody\"     STEAM_0:0:123457    3     2:42      34   0    active")).thenReturn(somebodyData);

        this.server.updatePlayers();

        verify(someone).addInformation(someoneData);
        verify(somebody).addInformation(somebodyData);
    }

    @Test
    public void testPlayerInfoGoldSrcWithPassword() throws Exception {
        String status = this.readFixture("status_goldsrc");

        SteamPlayer someone = mock(SteamPlayer.class);
        SteamPlayer somebody = mock(SteamPlayer.class);
        HashMap<String, SteamPlayer> playerMap = new HashMap<String, SteamPlayer>();
        playerMap.put("someone", someone);
        playerMap.put("somebody", somebody);
        this.server.playerHash = playerMap;

        doNothing().when(this.server).handleResponseForRequest(GameServer.REQUEST_PLAYER);
        when(this.server.rconExec("status")).thenReturn(status);

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

        mockStatic(GameServer.class);
        ArrayList<String> attributes = new ArrayList<String>();
        when(GameServer.getPlayerStatusAttributes("name userid uniqueid frag time ping loss adr")).thenReturn(attributes);
        when(GameServer.splitPlayerStatus(attributes, "1   \"someone\" 1 STEAM_0:0:123456 10 3:52 12 0 0")).thenReturn(someoneData);
        when(GameServer.splitPlayerStatus(attributes, "2   \"somebody\" 2 STEAM_0:0:123457 3 2:42 34 0 0")).thenReturn(somebodyData);

        this.server.updatePlayers("password");

        verify(this.server).rconAuth("password");
        verify(someone).addInformation(someoneData);
        verify(somebody).addInformation(somebodyData);
    }

    @Test
    public void testHandleChallengeRequests() throws Exception {
        S2C_CHALLENGE_Packet packet = mock(S2C_CHALLENGE_Packet.class);
        when(packet.getChallengeNumber()).thenReturn(1234);
        when(server.getReply()).thenReturn(packet);

        this.server.handleResponseForRequest(GameServer.REQUEST_CHALLENGE);

        assertEquals(1234, this.server.challengeNumber);
        verify(this.server).sendRequest(any(A2S_PLAYER_Packet.class));
    }

    @Test
    public void testHandleInfoRequests() throws Exception {
        S2A_INFO2_Packet packet = mock(S2A_INFO2_Packet.class);
        HashMap<String, Object> infoMap = new HashMap<String, Object>();
        infoMap.put("test", "test");
        when(packet.getInfo()).thenReturn(infoMap);
        when(server.getReply()).thenReturn(packet);

        this.server.handleResponseForRequest(GameServer.REQUEST_INFO);

        assertEquals("test", this.server.serverInfo.get("test"));
        verify(this.server).sendRequest(any(A2S_INFO_Packet.class));
    }

    @Test
    public void testHandleRulesRequests() throws Exception {
        S2A_RULES_Packet packet = mock(S2A_RULES_Packet.class);
        HashMap<String, String> rulesMap = new HashMap<String, String>();
        rulesMap.put("test", "test");
        when(packet.getRulesHash()).thenReturn(rulesMap);
        when(server.getReply()).thenReturn(packet);

        this.server.handleResponseForRequest(GameServer.REQUEST_RULES);

        assertEquals("test", this.server.rulesHash.get("test"));
        verify(this.server).sendRequest(any(A2S_RULES_Packet.class));
    }

    @Test
    public void testHandlePlayerRequests() throws Exception {
        SteamPlayer player = mock(SteamPlayer.class);
        S2A_PLAYER_Packet packet = mock(S2A_PLAYER_Packet.class);
        HashMap<String, SteamPlayer> playerMap = new HashMap<String, SteamPlayer>();
        playerMap.put("test", player);
        when(packet.getPlayerHash()).thenReturn(playerMap);
        when(server.getReply()).thenReturn(packet);

        this.server.handleResponseForRequest(GameServer.REQUEST_PLAYER);

        assertEquals(player, this.server.playerHash.get("test"));
        verify(this.server).sendRequest(any(A2S_PLAYER_Packet.class));
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

        this.server.handleResponseForRequest(GameServer.REQUEST_PLAYER);

        assertEquals("test", this.server.serverInfo.get("test"));
        assertEquals(player, this.server.playerHash.get("test"));
        verify(this.server, times(2)).sendRequest(any(A2S_PLAYER_Packet.class));
    }

    private String readFixture(String fixture) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(fixture)));
        String result = "";
        while(reader.ready()) {
            result += reader.readLine() + "\n";
        }

        return result;
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
