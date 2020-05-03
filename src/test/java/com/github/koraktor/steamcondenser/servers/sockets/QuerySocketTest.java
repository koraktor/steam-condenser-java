/*
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2012-2020, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.servers.sockets;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import org.mockito.ArgumentMatcher;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.servers.packets.SteamPacket;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Sebastian Staudt
 */
public class QuerySocketTest {

    private DatagramChannel channel;

    private QuerySocket socket;

    @Before
    public void setup() throws Exception {
        this.socket = new GenericQuerySocket();

        this.channel = mock(DatagramChannel.class);
        this.socket.channel = this.channel;
    }

    @Test
    public void testPacketIsSplit() {
        this.socket.buffer = mock(ByteBuffer.class);
        when(this.socket.buffer.getInt()).thenReturn(0xFFFFFFFF).thenReturn(0xFEFFFFFF);

        assertEquals(false, this.socket.packetIsSplit());
        assertEquals(true, this.socket.packetIsSplit());
    }

    @Test
    public void testReceivePacket() throws Exception {
        QuerySocket socket = spy(this.socket);
        doReturn(0).when(socket).receivePacket(0);

        socket.receivePacket();

        verify(socket).receivePacket(0);
    }

    @Test
    public void testSend() throws Exception {
        SteamPacket packet = mock(SteamPacket.class);
        when(packet.getBytes()).thenReturn(new byte[] { 0x1, 0x2, 0x3, 0x4 } );

        this.socket.send(packet);

        ArgumentMatcher<ByteBuffer> bufferMatcher = new ArgumentMatcher<ByteBuffer>() {
            public boolean matches(ByteBuffer buffer) {
                return Arrays.equals(buffer.array(), new byte[] { 0x1, 0x2, 0x3, 0x4 });
            }
        };

        ArgumentMatcher<InetSocketAddress> socketMatcher = new ArgumentMatcher<InetSocketAddress>() {
            public boolean matches(InetSocketAddress socketAddress) {
                return socketAddress.getAddress().isLoopbackAddress() &&
                       socketAddress.getPort() == 27015;
            }
        };

        verify(this.channel).send(argThat(bufferMatcher), argThat(socketMatcher));
    }

    class GenericQuerySocket extends QuerySocket {

        public GenericQuerySocket() throws SteamCondenserException, UnknownHostException {
            super(InetAddress.getByName("127.0.0.1"), 27015);
        }

        public SteamPacket getReply() {
            return null;
        }

    }

}
