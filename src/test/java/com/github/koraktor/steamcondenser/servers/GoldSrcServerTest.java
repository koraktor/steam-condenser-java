/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2012-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.servers;

import java.net.InetAddress;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.koraktor.steamcondenser.servers.sockets.GoldSrcSocket;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 *
 *
 * @author Sebastian Staudt
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(GoldSrcServer.class)
public class GoldSrcServerTest {

    private static InetAddress LOCALHOST;

    private GoldSrcServer server;

    private GoldSrcSocket socket;

    @Before
    public void setup() throws Exception {
        LOCALHOST = InetAddress.getByAddress(new byte[] { 0x7f, 0x0, 0x0, 0x1 });
        this.server = spy(new GoldSrcServer(LOCALHOST, 27015));
        this.socket = mock(GoldSrcSocket.class);
        this.server.socket = this.socket;
    }

    @Test
    public void testGetMaster() throws Exception {
        MasterServer master = mock(MasterServer.class);
        whenNew(MasterServer.class).withArguments(MasterServer.GOLDSRC_MASTER_SERVER).thenReturn(master);

        assertThat(GoldSrcServer.getMaster(), is(equalTo(master)));
    }

    @Test
    public void testInitSocket() throws Exception {
        GoldSrcSocket socket = mock(GoldSrcSocket.class);
        whenNew(GoldSrcSocket.class).withArguments(LOCALHOST, 27015, false).thenReturn(socket);

        this.server.initSocket();

        assertThat((GoldSrcSocket) this.server.socket, is(equalTo(socket)));
    }

    @Test
    public void testRconAuth() {
        assertTrue(this.server.rconAuth("password"));
        assertThat(this.server.rconPassword, is(equalTo("password")));
    }

    @Test
    public void testRconExec() throws Exception {
        when(this.socket.rconExec("password", "command")).thenReturn("test");

        this.server.rconPassword = "password";

        assertThat(this.server.rconExec("command"), is(equalTo("test")));
    }

}
