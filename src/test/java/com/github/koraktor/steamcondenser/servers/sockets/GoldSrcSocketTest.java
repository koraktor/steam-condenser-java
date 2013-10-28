/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2012-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.servers.sockets;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.koraktor.steamcondenser.exceptions.RCONBanException;
import com.github.koraktor.steamcondenser.servers.packets.SteamPacket;
import com.github.koraktor.steamcondenser.servers.packets.SteamPacketFactory;
import com.github.koraktor.steamcondenser.servers.packets.rcon.RCONGoldSrcRequestPacket;
import com.github.koraktor.steamcondenser.servers.packets.rcon.RCONGoldSrcResponsePacket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * @author Sebastian Staudt
 */
@RunWith(PowerMockRunner.class)
public class GoldSrcSocketTest {

    @Rule
    private ExpectedException exception = ExpectedException.none();

    private GoldSrcSocket socket;

    @Before
    public void setup() throws Exception {
        this.socket = spy(new GoldSrcSocket(InetAddress.getLocalHost(), 27015));
    }

    @Test
    public void testHLTV() throws Exception{
        GoldSrcSocket socket2 = new GoldSrcSocket(InetAddress.getLocalHost(), 27015, true);

        assertFalse(this.socket.isHLTV);
        assertTrue(socket2.isHLTV);
    }

    @Test
    @PrepareForTest(GoldSrcSocket.class)
    public void testRconSend() throws Exception{
        RCONGoldSrcRequestPacket packet = mock(RCONGoldSrcRequestPacket.class);
        whenNew(RCONGoldSrcRequestPacket.class).withArguments("test").thenReturn(packet);
        doNothing().when(this.socket).send(packet);

        this.socket.rconSend("test");

        verify(this.socket).send(packet);
    }

    @Test
    public void testRconChallenge() throws Exception {
        RCONGoldSrcResponsePacket packet = mock(RCONGoldSrcResponsePacket.class);
        when(packet.getResponse()).thenReturn("hallenge rcon 12345678");
        doReturn(packet).when(this.socket).getReply();

        this.socket.rconGetChallenge();

        verify(this.socket).rconSend("challenge rcon");
        assertEquals(12345678, this.socket.rconChallenge);
    }

    @Test
    public void testRconBan() throws Exception {
        this.exception.expect(RCONBanException.class);

        RCONGoldSrcResponsePacket packet = mock(RCONGoldSrcResponsePacket.class);
        when(packet.getResponse()).thenReturn("You have been banned from this server.");
        doReturn(packet).when(this.socket).getReply();

        this.socket.rconGetChallenge();
    }

    @Test
    @PrepareForTest(SteamPacketFactory.class)
    public void testSinglePacketReply() throws Exception {
        SteamPacket packet = mock(SteamPacket.class);
        mockStatic(SteamPacketFactory.class);
        PowerMockito.when(SteamPacketFactory.getPacketFromData("test".getBytes())).thenReturn(packet);

        doAnswer(new Answer<Integer>() {
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                GoldSrcSocket socket = (GoldSrcSocket) invocationOnMock.getMock();
                socket.buffer = ByteBuffer.wrap(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 't', 'e', 's', 't'});
                return 8;
            }
        }).when(this.socket).receivePacket(1400);

        assertEquals(packet, this.socket.getReply());
    }

    @Test
    @PrepareForTest(SteamPacketFactory.class)
    public void testSplitPacketReply() throws Exception {
        SteamPacket packet = mock(SteamPacket.class);
        ArrayList<byte[]> packets = new ArrayList<byte[]>();
        packets.add("test".getBytes());
        packets.add("test".getBytes());
        mockStatic(SteamPacketFactory.class);
        PowerMockito.when(SteamPacketFactory.reassemblePacket(refEq(packets))).thenReturn(packet);

        doAnswer(new Answer<Integer>() {
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                GoldSrcSocket socket = (GoldSrcSocket) invocationOnMock.getMock();
                socket.buffer = ByteBuffer.wrap(new byte[] { (byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xD2, 0x4, 0x0, 0x0, 0x2, 't', 'e', 's', 't' });
                return 1400;
            }
        }).when(this.socket).receivePacket(1400);
        doAnswer(new Answer<Integer>() {
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                GoldSrcSocket socket = (GoldSrcSocket) invocationOnMock.getMock();
                socket.buffer = ByteBuffer.wrap(new byte[]{(byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xD2, 0x4, 0x0, 0x0, 0x12, 't', 'e', 's', 't'});
                return 1400;
            }
        }).when(this.socket).receivePacket();

        assertEquals(packet, this.socket.getReply());
    }

    @Test
    public void testRconGameServer() throws Exception {
        doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ((GoldSrcSocket) invocationOnMock.getMock()).rconChallenge = 1234;
                return Void.TYPE;
            }
        }).when(this.socket).rconGetChallenge();
        doNothing().when(this.socket).rconSend(anyString());

        RCONGoldSrcResponsePacket packet1 = mock(RCONGoldSrcResponsePacket.class);
        when(packet1.getResponse()).thenReturn("test ");
        RCONGoldSrcResponsePacket packet2 = mock(RCONGoldSrcResponsePacket.class);
        when(packet2.getResponse()).thenReturn("test");
        RCONGoldSrcResponsePacket packet3 = mock(RCONGoldSrcResponsePacket.class);
        when(packet3.getResponse()).thenReturn("");
        doReturn(packet1).doReturn(packet2).doReturn(packet3).when(this.socket).getReply();

        assertEquals("test test", this.socket.rconExec("password", "command"));

        verify(this.socket).rconSend("rcon 1234 password command");
        verify(this.socket).rconSend("rcon 1234 password");
    }

    @Test
    public void testRconHLTVServer() throws Exception {
        this.socket.isHLTV = true;

        doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ((GoldSrcSocket) invocationOnMock.getMock()).rconChallenge = 1234;
                return Void.TYPE;
            }
        }).when(this.socket).rconGetChallenge();
        doNothing().when(this.socket).rconSend(anyString());

        RCONGoldSrcResponsePacket packet1 = mock(RCONGoldSrcResponsePacket.class);
        when(packet1.getResponse()).thenReturn("test ");
        RCONGoldSrcResponsePacket packet2 = mock(RCONGoldSrcResponsePacket.class);
        when(packet2.getResponse()).thenReturn("test");
        RCONGoldSrcResponsePacket packet3 = mock(RCONGoldSrcResponsePacket.class);
        when(packet3.getResponse()).thenReturn("");
        doThrow(new TimeoutException()).doReturn(packet1).doReturn(packet2).doReturn(packet3).when(this.socket).getReply();

        assertEquals("test test", this.socket.rconExec("password", "command"));

        verify(this.socket).rconSend("rcon 1234 password command");
        verify(this.socket).rconSend("rcon 1234 password");
    }

}
