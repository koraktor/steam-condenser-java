/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011-2013, Sebastian Staudt
 */
package com.github.koraktor.steamcondenser.servers.sockets;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.koraktor.steamcondenser.servers.packets.SteamPacket;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * @author Sebastian Staudt
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SteamSocket.class)
public class SteamSocketTest {

    @Rule
    private ExpectedException exception = ExpectedException.none();

    private DatagramChannel channel;

    private SteamSocket socket;

    @Before
    public void setup() throws Exception {
        this.socket = new GenericSteamSocket();

        this.channel = mock(DatagramChannel.class);
        this.socket.channel = this.channel;
    }

    @Test
    public void testClose() throws IOException {
        when(this.channel.isOpen()).thenReturn(true);

        this.socket.close();

        verify(this.channel).close();
    }

    @Test
    public void testReceiveIntoNewBuffer() throws Exception {
        Selector selector = mock(Selector.class);
        when(selector.select(SteamSocket.timeout)).thenReturn(1);
        mockStatic(Selector.class);
        when(Selector.open()).thenReturn(selector);
        when(this.channel.register(selector, SelectionKey.OP_READ)).thenReturn(null);

        final SteamSocket socket = this.socket;
        when(this.channel.read(any(ByteBuffer.class))).thenAnswer(new Answer<Integer>() {
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                socket.buffer.put("test".getBytes());
                return 4;
            }
        });

        assertEquals(4, this.socket.receivePacket(4));

        ByteBuffer buffer = this.socket.buffer;
        assertEquals(0, buffer.position());
        assertEquals(4, buffer.capacity());
        assertEquals("test", new String(buffer.array()));
    }

    @Test
    public void testReceiveIntoExistingBuffer() throws Exception {
        Selector selector = mock(Selector.class);
        when(selector.select(SteamSocket.timeout)).thenReturn(1);
        mockStatic(Selector.class);
        when(Selector.open()).thenReturn(selector);
        when(this.channel.register(selector, SelectionKey.OP_READ)).thenReturn(null);

        this.socket.buffer = ByteBuffer.allocate(10);

        final SteamSocket socket = this.socket;
        when(this.channel.read(any(ByteBuffer.class))).thenAnswer(new Answer<Integer>() {
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                socket.buffer.put("test".getBytes());
                return 4;
            }
        });

        assertEquals(4, this.socket.receivePacket(4));

        ByteBuffer buffer = this.socket.buffer;
        assertEquals(0, buffer.position());
        assertEquals(4, buffer.capacity());
        assertEquals("test", new String(buffer.array()));
    }

    @Test
    public void testSetTimeout() {
        SteamSocket.setTimeout(2000);

        assertEquals(2000, SteamSocket.timeout);
    }

    @Test
    public void testTimeout() throws Exception {
        this.exception.expect(TimeoutException.class);

        Selector selector = mock(Selector.class);
        when(selector.select(SteamSocket.timeout)).thenReturn(0);
        mockStatic(Selector.class);
        when(Selector.open()).thenReturn(selector);
        when(this.channel.register(selector, SelectionKey.OP_READ)).thenReturn(null);

        this.socket.receivePacket(4);
    }

    class GenericSteamSocket extends SteamSocket {

        public GenericSteamSocket() throws UnknownHostException {
            super(InetAddress.getLocalHost(), 27015);
        }

        public SteamPacket getReply() {
            return null;
        }

    }

}
