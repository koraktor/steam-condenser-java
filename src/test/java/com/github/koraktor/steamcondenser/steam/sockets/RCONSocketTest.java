/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2012, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.sockets;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.koraktor.steamcondenser.exceptions.RCONBanException;
import com.github.koraktor.steamcondenser.steam.packets.rcon.RCONPacket;
import com.github.koraktor.steamcondenser.steam.packets.rcon.RCONPacketFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Sebastian Staudt
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(RCONPacketFactory.class)
public class RCONSocketTest {

    @Rule
    private ExpectedException exception = ExpectedException.none();

    private RCONSocket socket;

    @Before
    public void setup() throws Exception {
        this.socket = spy(new RCONSocket(InetAddress.getLocalHost(), 27015));
    }

    @Test
    public void testNewSocket() throws Exception {
        assertEquals(InetAddress.getLocalHost(), this.socket.remoteSocket.getAddress());
        assertEquals(27015, this.socket.remoteSocket.getPort());
        assertFalse(((SocketChannel) this.socket.channel).isConnected());
    }

    @Test
    public void testReceiveReply() throws Exception {
        RCONPacket packet = mock(RCONPacket.class);
        mockStatic(RCONPacketFactory.class);
        when(RCONPacketFactory.getPacketFromData("test test".getBytes())).thenReturn(packet);

        doAnswer(new Answer<Integer>() {
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                RCONSocket socket = (RCONSocket) invocationOnMock.getMock();
                socket.buffer = ByteBuffer.wrap(new byte[]{ (byte) 0xd2, 0x4, 0x0, 0x0 });
                return 4;
            }
        }).when(this.socket).receivePacket(4);
        doAnswer(new Answer<Integer>() {
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                RCONSocket socket = (RCONSocket) invocationOnMock.getMock();
                socket.buffer = ByteBuffer.wrap("test ".getBytes());
                return 1000;
            }
        }).when(this.socket).receivePacket(1234);
        doAnswer(new Answer<Integer>() {
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                RCONSocket socket = (RCONSocket) invocationOnMock.getMock();
                socket.buffer = ByteBuffer.wrap("test".getBytes());
                return 234;
            }
        }).when(this.socket).receivePacket(234);

        assertEquals(packet, this.socket.getReply());
    }

    @Test
    public void testBan() throws Exception {
        this.exception.expect(RCONBanException.class);

        doReturn(0).when(this.socket).receivePacket(4);

        this.socket.getReply();
    }

}
