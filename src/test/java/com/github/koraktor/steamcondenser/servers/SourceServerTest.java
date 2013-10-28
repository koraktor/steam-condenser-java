/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2012-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.servers;

import java.net.InetAddress;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.koraktor.steamcondenser.exceptions.RCONNoAuthException;
import com.github.koraktor.steamcondenser.servers.packets.rcon.RCONAuthRequestPacket;
import com.github.koraktor.steamcondenser.servers.packets.rcon.RCONAuthResponse;
import com.github.koraktor.steamcondenser.servers.packets.rcon.RCONExecRequestPacket;
import com.github.koraktor.steamcondenser.servers.packets.rcon.RCONExecResponsePacket;
import com.github.koraktor.steamcondenser.servers.packets.rcon.RCONPacket;
import com.github.koraktor.steamcondenser.servers.packets.rcon.RCONTerminator;
import com.github.koraktor.steamcondenser.servers.sockets.RCONSocket;
import com.github.koraktor.steamcondenser.servers.sockets.SourceSocket;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 *
 *
 * @author Sebastian Staudt
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SourceServer.class)
public class SourceServerTest {

    @Rule
    private ExpectedException exception = ExpectedException.none();

    private static InetAddress LOCALHOST;

    private RCONSocket rconSocket;

    private SourceServer server;

    private SourceSocket socket;

    private Matcher<RCONPacket> requestMatcher = new BaseMatcher<RCONPacket>() {
        public boolean matches(Object o) {
            if(!(o instanceof RCONExecRequestPacket)) {
                return false;
            }
            RCONExecRequestPacket packet = (RCONExecRequestPacket) o;
            return packet.getRequestId() == 1234 &&
                   new String(packet.getBytes(), 12, 7).equals("command");
        }

        public void describeTo(Description description) {}
    };

    private Matcher<RCONPacket> terminatorMatcher = new BaseMatcher<RCONPacket>() {
        public boolean matches(Object o) {
            if(!(o instanceof RCONTerminator)) {
                return false;
            }
            RCONTerminator packet = (RCONTerminator) o;
            return packet.getRequestId() == 1234;
        }

        public void describeTo(Description description) {}
    };

    @Before
    public void setup() throws Exception {
        LOCALHOST = InetAddress.getByAddress(new byte[] { 0x7f, 0x0, 0x0, 0x1 });
        this.server = spy(new SourceServer(LOCALHOST, 27015));
        this.socket = mock(SourceSocket.class);
        this.server.socket = this.socket;
        this.rconSocket = mock(RCONSocket.class);
        this.server.rconSocket = this.rconSocket;
    }

    @Test
    public void testDisconnect() throws Exception {
        this.server.disconnect();

        verify(rconSocket).close();
    }

    @Test
    public void testGetMaster() throws Exception {
        MasterServer master = mock(MasterServer.class);
        whenNew(MasterServer.class).withArguments(MasterServer.SOURCE_MASTER_SERVER).thenReturn(master);

        assertThat(SourceServer.getMaster(), is(equalTo(master)));

        verifyNew(MasterServer.class).withArguments(MasterServer.SOURCE_MASTER_SERVER);
    }

    @Test
    public void testInitSocket() throws Exception {
        SourceSocket socket = mock(SourceSocket.class);
        whenNew(SourceSocket.class).withArguments(LOCALHOST, 27015).thenReturn(socket);
        RCONSocket rconSocket = mock(RCONSocket.class);
        whenNew(RCONSocket.class).withArguments(LOCALHOST, 27015).thenReturn(rconSocket);

        this.server.initSocket();

        assertThat((SourceSocket) this.server.socket, is(equalTo(socket)));
        assertThat(this.server.rconSocket, is(equalTo(rconSocket)));
    }

    @Test
    public void testRconAuthSuccess() throws Exception {
        final SourceServer server = this.server;
        doReturn(mock(RCONAuthResponse.class)).
            doAnswer(new Answer<RCONAuthResponse>() {
            public RCONAuthResponse answer(InvocationOnMock invocationOnMock) throws Throwable {
                RCONAuthResponse reply = mock(RCONAuthResponse.class);
                when(reply.getRequestId()).thenReturn(server.rconRequestId);
                return reply;
            }
        }).when(this.rconSocket).getReply();

        assertTrue(this.server.rconAuth("password"));
        assertTrue(this.server.rconAuthenticated);

        verify(this.rconSocket).send(any(RCONAuthRequestPacket.class));
    }

    @Test
    public void testRconAuthFailure() throws Exception {
        RCONAuthResponse reply = mock(RCONAuthResponse.class);
        when(reply.getRequestId()).thenReturn(-1);
        when(this.rconSocket.getReply()).thenReturn(reply).thenReturn(reply);

        assertFalse(this.server.rconAuth("password"));
        assertFalse(this.server.rconAuthenticated);

        verify(this.rconSocket).send(any(RCONAuthRequestPacket.class));
    }

    @Test
    public void testRconExecNoAuth() throws Exception {
        this.exception.expect(RCONNoAuthException.class);

        this.server.rconExec("command");
    }

    @Test
    public void testResetRCONConnection() throws Exception {
        this.server.rconAuthenticated = true;
        this.server.rconRequestId = 1234;

        RCONAuthResponse reply = mock(RCONAuthResponse.class);
        when(this.rconSocket.getReply()).thenReturn(reply);

        try {
            this.server.rconExec("command");
            fail();
        } catch(RCONNoAuthException e) {}

        assertFalse(this.server.rconAuthenticated);

        verify(this.rconSocket).send(argThat(this.requestMatcher));
    }

    @Test
    public void testRCONExecEmpty() throws Exception {
        this.server.rconAuthenticated = true;
        this.server.rconRequestId = 1234;

        RCONExecResponsePacket reply = mock(RCONExecResponsePacket.class);
        when(reply.getResponse()).thenReturn("");
        when(this.rconSocket.getReply()).thenReturn(reply);

        assertThat(this.server.rconExec("command"), is(equalTo("")));

        verify(this.rconSocket, times(1)).send(argThat(this.requestMatcher));
    }

    @Test
    public void testRCONExec() throws Exception {
        this.server.rconAuthenticated = true;
        this.server.rconRequestId = 1234;

        RCONExecResponsePacket reply1 = mock(RCONExecResponsePacket.class);
        when(reply1.getResponse()).thenReturn("test");
        RCONExecResponsePacket reply2 = mock(RCONExecResponsePacket.class);
        when(reply2.getResponse()).thenReturn("test");
        RCONExecResponsePacket reply3 = mock(RCONExecResponsePacket.class);
        when(reply3.getResponse()).thenReturn("");
        when(this.rconSocket.getReply()).thenReturn(reply1, reply2, reply3, reply3);

        assertThat(this.server.rconExec("command"), is(equalTo("testtest")));

        verify(this.rconSocket).send(argThat(this.requestMatcher));
        verify(this.rconSocket).send(argThat(this.terminatorMatcher));
    }

}
