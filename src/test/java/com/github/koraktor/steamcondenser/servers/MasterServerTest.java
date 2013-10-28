/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2012-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.servers;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.github.koraktor.steamcondenser.servers.packets.A2M_GET_SERVERS_BATCH2_Paket;
import com.github.koraktor.steamcondenser.servers.packets.M2A_SERVER_BATCH_Paket;
import com.github.koraktor.steamcondenser.servers.sockets.MasterServerSocket;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Sebastian Staudt
 */
public class MasterServerTest {

    private MasterServer server;

    @Before
    public void setup() throws Exception {
        this.server = spy(new MasterServer(InetAddress.getByAddress(new byte[] { 0x7f, 0x0, 0x0, 0x1 }), 27015));
        this.server.socket = mock(MasterServerSocket.class);

        doReturn(true).when(this.server).rotateIp();
    }

    @Test
    public void testGetServers() throws Exception {
        M2A_SERVER_BATCH_Paket packet1 = mock(M2A_SERVER_BATCH_Paket.class);
        Vector<String> servers1 = new Vector<String>();
        servers1.add("127.0.0.1:27015");
        servers1.add("127.0.0.2:27015");
        servers1.add("127.0.0.3:27015");
        when(packet1.getServers()).thenReturn(servers1);
        M2A_SERVER_BATCH_Paket packet2 = mock(M2A_SERVER_BATCH_Paket.class);
        Vector<String> servers2 = new Vector<String>();
        servers2.add("127.0.0.4:27015");
        servers2.add("0.0.0.0:0");
        when(packet2.getServers()).thenReturn(servers2);
        when(this.server.socket.getReply()).thenReturn(packet1).thenReturn(packet2);

        Vector<InetSocketAddress> servers = new Vector<InetSocketAddress>();
        servers.add(new InetSocketAddress("127.0.0.1", 27015));
        servers.add(new InetSocketAddress("127.0.0.2", 27015));
        servers.add(new InetSocketAddress("127.0.0.3", 27015));
        servers.add(new InetSocketAddress("127.0.0.4", 27015));

        assertThat(this.server.getServers(MasterServer.REGION_EUROPE, "filter"), is(equalTo(servers)));

        verify(this.server.socket).send(argThat(new BaseMatcher<A2M_GET_SERVERS_BATCH2_Paket>() {
            public boolean matches(Object o) {
                if(!(o instanceof A2M_GET_SERVERS_BATCH2_Paket)) {
                    return false;
                }
                A2M_GET_SERVERS_BATCH2_Paket packet = (A2M_GET_SERVERS_BATCH2_Paket) o;
                return Arrays.equals(packet.getBytes(), "\u0031\u00030.0.0.0:0\0filter\0".getBytes());
            }

            public void describeTo(Description description) {}
        }));
        verify(this.server.socket).send(argThat(new BaseMatcher<A2M_GET_SERVERS_BATCH2_Paket>() {
            public boolean matches(Object o) {
                if(!(o instanceof A2M_GET_SERVERS_BATCH2_Paket)) {
                    return false;
                }
                A2M_GET_SERVERS_BATCH2_Paket packet = (A2M_GET_SERVERS_BATCH2_Paket) o;
                return Arrays.equals(packet.getBytes(), "\u0031\u0003127.0.0.3:27015\0filter\0".getBytes());
            }

            public void describeTo(Description description) {}
        }));
    }

    @Test
    public void testGetServersForced() throws Exception {
        MasterServer.setRetries(1);

        M2A_SERVER_BATCH_Paket packet1 = mock(M2A_SERVER_BATCH_Paket.class);
        Vector<String> servers1 = new Vector<String>();
        servers1.add("127.0.0.1:27015");
        servers1.add("127.0.0.2:27015");
        servers1.add("127.0.0.3:27015");
        when(packet1.getServers()).thenReturn(servers1);
        when(this.server.socket.getReply()).thenReturn(packet1).thenThrow(new TimeoutException());

        Vector<InetSocketAddress> servers = new Vector<InetSocketAddress>();
        servers.add(new InetSocketAddress("127.0.0.1", 27015));
        servers.add(new InetSocketAddress("127.0.0.2", 27015));
        servers.add(new InetSocketAddress("127.0.0.3", 27015));

        assertThat(this.server.getServers(MasterServer.REGION_EUROPE, "filter", true), is(equalTo(servers)));

        verify(this.server.socket).send(argThat(new BaseMatcher<A2M_GET_SERVERS_BATCH2_Paket>() {
            public boolean matches(Object o) {
                if(!(o instanceof A2M_GET_SERVERS_BATCH2_Paket)) {
                    return false;
                }
                A2M_GET_SERVERS_BATCH2_Paket packet = (A2M_GET_SERVERS_BATCH2_Paket) o;
                return Arrays.equals(packet.getBytes(), "\u0031\u00030.0.0.0:0\0filter\0".getBytes());
            }

            public void describeTo(Description description) {}
        }));
        verify(this.server.socket).send(argThat(new BaseMatcher<A2M_GET_SERVERS_BATCH2_Paket>() {
            public boolean matches(Object o) {
                if(!(o instanceof A2M_GET_SERVERS_BATCH2_Paket)) {
                    return false;
                }
                A2M_GET_SERVERS_BATCH2_Paket packet = (A2M_GET_SERVERS_BATCH2_Paket) o;
                return Arrays.equals(packet.getBytes(), "\u0031\u0003127.0.0.3:27015\0filter\0".getBytes());
            }

            public void describeTo(Description description) {}
        }));
    }

    @Test
    public void testGetServersSwapIp() throws Exception {
        M2A_SERVER_BATCH_Paket packet1 = mock(M2A_SERVER_BATCH_Paket.class);
        Vector<String> servers1 = new Vector<String>();
        servers1.add("127.0.0.1:27015");
        servers1.add("127.0.0.2:27015");
        servers1.add("127.0.0.3:27015");
        when(packet1.getServers()).thenReturn(servers1);
        M2A_SERVER_BATCH_Paket packet2 = mock(M2A_SERVER_BATCH_Paket.class);
        Vector<String> servers2 = new Vector<String>();
        servers2.add("127.0.0.4:27015");
        servers2.add("0.0.0.0:0");
        when(packet2.getServers()).thenReturn(servers2);
        when(this.server.socket.getReply())
            .thenReturn(packet1)
            .thenThrow(new TimeoutException())
            .thenThrow(new TimeoutException())
            .thenThrow(new TimeoutException())
            .thenReturn(packet2);

        when(this.server.rotateIp()).thenReturn(false);

        Vector<InetSocketAddress> servers = new Vector<InetSocketAddress>();
        servers.add(new InetSocketAddress("127.0.0.1", 27015));
        servers.add(new InetSocketAddress("127.0.0.2", 27015));
        servers.add(new InetSocketAddress("127.0.0.3", 27015));
        servers.add(new InetSocketAddress("127.0.0.4", 27015));

        assertThat(this.server.getServers(MasterServer.REGION_EUROPE, "filter"), is(equalTo(servers)));

        verify(this.server, times(3)).rotateIp();
        verify(this.server.socket).send(argThat(new BaseMatcher<A2M_GET_SERVERS_BATCH2_Paket>() {
            public boolean matches(Object o) {
                if(!(o instanceof A2M_GET_SERVERS_BATCH2_Paket)) {
                    return false;
                }
                A2M_GET_SERVERS_BATCH2_Paket packet = (A2M_GET_SERVERS_BATCH2_Paket) o;
                return Arrays.equals(packet.getBytes(), "\u0031\u00030.0.0.0:0\0filter\0".getBytes());
            }

            public void describeTo(Description description) {}
        }));
        verify(this.server.socket, times(4)).send(argThat(new BaseMatcher<A2M_GET_SERVERS_BATCH2_Paket>() {
            public boolean matches(Object o) {
                if(!(o instanceof A2M_GET_SERVERS_BATCH2_Paket)) {
                    return false;
                }
                A2M_GET_SERVERS_BATCH2_Paket packet = (A2M_GET_SERVERS_BATCH2_Paket) o;
                return Arrays.equals(packet.getBytes(), "\u0031\u0003127.0.0.3:27015\0filter\0".getBytes());
            }

            public void describeTo(Description description) {}
        }));
    }

    @Test
    public void testGetServersTimeout() throws Exception {
        int retries = new Random().nextInt(4) + 1;
        MasterServer.setRetries(retries);

        when(this.server.socket.getReply()).thenThrow(new TimeoutException());

        try {
            this.server.getServers();
            fail();
        } catch(TimeoutException e) {}

        verify(this.server.socket, times(retries)).send(argThat(new BaseMatcher<A2M_GET_SERVERS_BATCH2_Paket>() {
            public boolean matches(Object o) {
                if(!(o instanceof A2M_GET_SERVERS_BATCH2_Paket)) {
                    return false;
                }
                A2M_GET_SERVERS_BATCH2_Paket packet = (A2M_GET_SERVERS_BATCH2_Paket) o;
                return Arrays.equals(packet.getBytes(), new byte[] { 0x31, (byte) 0xFF, 0x30, 0x2E, 0x30, 0x2E, 0x30, 0x2E, 0x30, 0x3a, 0x30, 0x00, 0x00 });
            }

            public void describeTo(Description description) {}
        }));
    }

}
