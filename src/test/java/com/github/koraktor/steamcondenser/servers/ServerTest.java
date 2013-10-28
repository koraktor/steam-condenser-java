/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2012-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.servers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Sebastian Staudt
 */
public class ServerTest {

    private Server server;

    @Before
    public void setup() throws Exception {
        this.server = spy(new GenericServer());
    }

    @Test
    public void testRotateIp() throws Exception {
        this.server.ipAddresses.add(InetAddress.getByAddress(new byte[] { 127, 0, 0, 2 }));

        assertFalse(this.server.rotateIp());
        assertEquals(this.server.ipAddresses.get(1), this.server.ipAddress);
        assertTrue(this.server.rotateIp());
        assertEquals(this.server.ipAddresses.get(0), this.server.ipAddress);

        verify(this.server, times(2)).initSocket();
    }

    class GenericServer extends Server {

        public GenericServer() throws SteamCondenserException, UnknownHostException {
            super(InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }), 27015);
        }

        public void initSocket() {}

    }

}
