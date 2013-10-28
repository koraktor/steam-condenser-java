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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.koraktor.steamcondenser.servers.packets.SteamPacket;
import com.github.koraktor.steamcondenser.servers.packets.SteamPacketFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Sebastian Staudt
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SteamPacketFactory.class)
public class SourceSocketTest {

    private SteamPacket packet;

    private SourceSocket socket;

    @Before
    public void setup() throws Exception {
        this.packet = mock(SteamPacket.class);
        this.socket = spy(new SourceSocket(InetAddress.getLocalHost(), 27015));

        mockStatic(SteamPacketFactory.class);
    }

    @Test
    public void testSinglePacketReply() throws Exception {
        when(SteamPacketFactory.getPacketFromData("test".getBytes())).thenReturn(this.packet);

        doAnswer(new Answer<Integer>() {
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                SourceSocket socket = (SourceSocket) invocationOnMock.getMock();
                socket.buffer = ByteBuffer.wrap(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 't', 'e', 's', 't' });
                return 8;
            }
        }).when(this.socket).receivePacket(1400);

        assertEquals(this.packet, this.socket.getReply());
    }

    @Test
    public void testSplitPacketReply() throws Exception {
        ArrayList<byte[]> packets = new ArrayList<byte[]>();
        packets.add("test".getBytes());
        packets.add("test".getBytes());
        when(SteamPacketFactory.reassemblePacket(refEq(packets))).thenReturn(this.packet);

        doAnswer(new Answer<Integer>() {
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                SourceSocket socket = (SourceSocket) invocationOnMock.getMock();
                socket.buffer = ByteBuffer.wrap(new byte[] { (byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xD2, 0x4, 0x0, 0x0, 0x2, 0x0, 0x4, 0x0, 't', 'e', 's', 't' });
                return 1400;
            }
        }).when(this.socket).receivePacket(1400);
        doAnswer(new Answer<Integer>() {
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                SourceSocket socket = (SourceSocket) invocationOnMock.getMock();
                socket.buffer = ByteBuffer.wrap(new byte[]{(byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xD2, 0x4, 0x0, 0x0, 0x2, 0x1, 0x4, 0x0, 't', 'e', 's', 't'});
                return 1400;
            }
        }).when(this.socket).receivePacket();

        assertEquals(this.packet, this.socket.getReply());
    }

    @Test
    public void testCompressedReply() throws Exception {
        ArrayList<byte[]> packets = new ArrayList<byte[]>();
        packets.add("test".getBytes());
        packets.add("test".getBytes());
        when(SteamPacketFactory.reassemblePacket(refEq(packets), eq(true), eq(8), eq(1337))).thenReturn(this.packet);

        doAnswer(new Answer<Integer>() {
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                SourceSocket socket = (SourceSocket) invocationOnMock.getMock();
                socket.buffer = ByteBuffer.wrap(new byte[] { (byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xD2, 0x4, 0x0, (byte) 0x80, 0x2, 0x0, 0x8, 0x0, 0x0, 0x0, 0x39, 0x5, 0x0, 0x0, 't', 'e', 's', 't' });
                return 1400;
            }
        }).when(this.socket).receivePacket(1400);
        doAnswer(new Answer<Integer>() {
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                SourceSocket socket = (SourceSocket) invocationOnMock.getMock();
                socket.buffer = ByteBuffer.wrap(new byte[]{(byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xD2, 0x4, 0x0, (byte) 0x80, 0x2, 0x1, 0x8, 0x0, 0x0, 0x0, 0x39, 0x5, 0x0, 0x0, 't', 'e', 's', 't'});
                return 1400;
            }
        }).when(this.socket).receivePacket();

        assertEquals(this.packet, this.socket.getReply());
    }

}
