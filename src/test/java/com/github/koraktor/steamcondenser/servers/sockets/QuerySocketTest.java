/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2012-2013, Sebastian Staudt
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

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.servers.packets.SteamPacket;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
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

        Matcher<ByteBuffer> bufferMatcher = new BaseMatcher<ByteBuffer>() {
            public boolean matches(Object o) {
                if(!(o instanceof ByteBuffer)) {
                    return false;
                }
                return Arrays.equals(((ByteBuffer) o).array(), new byte[] { 0x1, 0x2, 0x3, 0x4 });
            }

            public void describeTo(Description description) {}
        };

        Matcher<InetSocketAddress> socketMatcher = new BaseMatcher<InetSocketAddress>() {
            public boolean matches(Object o) {
                if(!(o instanceof InetSocketAddress)) {
                    return false;
                }
                return ((InetSocketAddress) o).getAddress().isLoopbackAddress() &&
                       ((InetSocketAddress) o).getPort() == 27015;
            }

            public void describeTo(Description description) {}
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
