/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2012-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.servers.sockets;

import java.net.InetAddress;
import java.nio.ByteBuffer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.koraktor.steamcondenser.exceptions.PacketFormatException;
import com.github.koraktor.steamcondenser.servers.packets.SteamPacket;
import com.github.koraktor.steamcondenser.servers.packets.SteamPacketFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Sebastian Staudt
 */
@RunWith(PowerMockRunner.class)
public class MasterServerSocketTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private MasterServerSocket socket;

    @Before
    public void setup() throws Exception {
        this.socket = new MasterServerSocket(InetAddress.getLocalHost(), 27015);
    }

    @Test
    public void testIncorrectPacket() throws Exception {
        this.exception.expect(PacketFormatException.class);
        this.exception.expectMessage("Master query response has wrong packet header.");

        MasterServerSocket socket = spy(this.socket);
        doReturn(1).when(socket).receivePacket(1500);
        socket.buffer = mock(ByteBuffer.class);
        when(socket.buffer.getInt()).thenReturn(1);

        socket.getReply();
    }

    @Test
    @PrepareForTest(SteamPacketFactory.class)
    public void testCorrectPacket() throws Exception {
        SteamPacket packet = mock(SteamPacket.class);
        mockStatic(SteamPacketFactory.class);
        when(SteamPacketFactory.getPacketFromData("test".getBytes())).thenReturn(packet);

        MasterServerSocket socket = spy(this.socket);
        doAnswer(new Answer<Integer>() {
            public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
                MasterServerSocket socket = (MasterServerSocket) invocationOnMock.getMock();
                socket.buffer = ByteBuffer.wrap(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 't', 'e', 's', 't' });
                return 8;
            }
        }).when(socket).receivePacket(1500);

        assertEquals(packet, socket.getReply());
    }

}
