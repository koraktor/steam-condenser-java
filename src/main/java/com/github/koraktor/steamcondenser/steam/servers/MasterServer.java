/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2008-2012, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.servers;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Vector;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.packets.A2M_GET_SERVERS_BATCH2_Paket;
import com.github.koraktor.steamcondenser.steam.packets.M2A_SERVER_BATCH_Paket;
import com.github.koraktor.steamcondenser.steam.sockets.MasterServerSocket;

/**
 * This class represents a Steam master server and can be used to get game
 * servers which are publicly available
 * <p/>
 * An intance of this class can be used much like Steam's server browser to get
 * a list of available game servers, including filters to narrow down the
 * search results.
 *
 * @author Sebastian Staudt
 */
public class MasterServer extends Server {

    protected static final Logger LOG = LoggerFactory.getLogger("com.github.koraktor.steamcondenser");

    /**
     * The master server address to query for GoldSrc game servers
     */
    public static final String GOLDSRC_MASTER_SERVER = "hl1master.steampowered.com:27010";

    /**
     * The master server address to query for GoldSrc game servers
     */
    public static final String SOURCE_MASTER_SERVER = "hl2master.steampowered.com:27011";

    /**
     * The region code for the US east coast
     */
    public static final byte REGION_US_EAST_COAST = 0x00;

    /**
     * The region code for the US west coast
     */
    public static final byte REGION_US_WEST_COAST = 0x01;

    /**
     * The region code for South America
     */
    public static final byte REGION_SOUTH_AMERICA = 0x02;

    /**
     * The region code for Europe
     */
    public static final byte REGION_EUROPE = 0x03;

    /**
     * The region code for Asia
     */
    public static final byte REGION_ASIA = 0x04;

    /**
     * The region code for Australia
     */
    public static final byte REGION_AUSTRALIA = 0x05;

    /**
     * The region code for the Middle East
     */
    public static final byte REGION_MIDDLE_EAST = 0x06;

    /**
     * The region code for Africa
     */
    public static final byte REGION_AFRICA = 0x07;

    /**
     * The region code for the whole world
     */
    public static final byte REGION_ALL = (byte)0xFF;

    public static int retries = 3;

    protected MasterServerSocket socket;

    /**
     * Sets the number of consecutive requests that may fail, before getting
     * the server list is cancelled (default: 3)
     *
     * @param newRetries The number of allowed retries
     */
    public static void setRetries(int newRetries) {
        retries = newRetries;
    }

    /**
     * Creates a new instance of a master server object
     *
     * @param address Either an IP address, a DNS name or one of them combined
     *        with the port number. If a port number is given, e.g.
     *        'server.example.com:27016' it will override the second argument.
     * @throws SteamCondenserException if initializing the socket fails
     */
    public MasterServer(String address) throws SteamCondenserException {
        super(address, null);
    }

    /**
     * Creates a new instance of a master server object
     *
     * @param address Either an IP address, a DNS name or one of them combined
     *        with the port number. If a port number is given, e.g.
     *        'server.example.com:27016' it will override the second argument.
     * @param port The port the server is listening on
     * @throws SteamCondenserException if initializing the socket fails
     */
    public MasterServer(String address, Integer port)
            throws SteamCondenserException {
        super(address, port);
    }

    /**
     * Creates a new instance of a GoldSrc server object
     *
     * @param address Either an IP address, a DNS name or one of them combined
     *        with the port number. If a port number is given, e.g.
     *        'server.example.com:27016' it will override the second argument.
     * @throws SteamCondenserException if initializing the socket fails
     */
    public MasterServer(InetAddress address) throws SteamCondenserException {
        super(address, null);
    }

    /**
     * Creates a new instance of a master server object
     *
     * @param address Either an IP address, a DNS name or one of them combined
     *        with the port number. If a port number is given, e.g.
     *        'server.example.com:27016' it will override the second argument.
     * @param port The port the server is listening on
     * @throws SteamCondenserException if initializing the socket fails
     */
    public MasterServer(InetAddress address, Integer port)
            throws SteamCondenserException {
        super(address, port);
    }

    /**
     * Returns a list of all available game servers
     * <p/>
     * <strong>Note:</strong> Receiving all servers from the master server is
     * taking quite some time.
     *
     * @return A list of game servers matching the given
     *         region and filters
     * @see A2M_GET_SERVERS_BATCH2_Paket
     * @throws SteamCondenserException if the request fails
     * @throws TimeoutException if too many timeouts occur while querying the
     *         master server
     */
    public Vector<InetSocketAddress> getServers()
            throws SteamCondenserException, TimeoutException {
        return this.getServers(MasterServer.REGION_ALL, "", false);
    }

    /**
     * Returns a list of game server matching the given region and filters
     * <p/>
     * Filtering:
     * Instead of filtering the results sent by the master server locally, you
     * should at least use the filters listed at {@link
     * MasterServer#getServers(byte, String, boolean)} to narrow down the
     * results sent by the master server.
     * <p/>
     * <strong>Note:</strong> Receiving all servers from the master server is
     * taking quite some time.
     *
     * @param regionCode The region code to specify a location of the game
     *        servers
     * @param filter The filters that game servers should match
     * @return A list of game servers matching the given
     *         region and filters
     * @see A2M_GET_SERVERS_BATCH2_Paket
     * @throws SteamCondenserException if the request fails
     * @throws TimeoutException if too many timeouts occur while querying the
     *         master server
     */
    public Vector<InetSocketAddress> getServers(byte regionCode, String filter)
            throws SteamCondenserException, TimeoutException {
        return this.getServers(regionCode, filter, false);
    }

    /**
     * Returns a list of game server matching the given region and filters
     * <p/>
     * Filtering:
     * Instead of filtering the results sent by the master server locally, you
     * should at least use the following filters to narrow down the results
     * sent by the master server.
     * <p/>
     * <strong>Note:</strong> Receiving all servers from the master server is
     * taking quite some time.
     *
     * Available filters:
     *
     * <ul>
     * <li><code>\type\d</code>: Request only dedicated servers
     * <li><code>\secure\1</code>: Request only secure servers
     * <li><code>\gamedir\[mod]</code>: Request only servers of a specific mod
     * <li><code>\map\[mapname]</code>: Request only servers running a specific
     *     map
     * <li><code>\linux\1</code>: Request only linux servers
     * <li><code>\emtpy\1</code>: Request only **non**-empty servers
     * <li><code>\full\1</code>: Request only servers **not** full
     * <li><code>\proxy\1</code>: Request only spectator proxy servers
     * </ul>
     *
     * @param regionCode The region code to specify a location of the game
     *        servers
     * @param filter The filters that game servers should match
     * @return A list of game servers matching the given
     *         region and filters
     * @see A2M_GET_SERVERS_BATCH2_Paket
     * @throws SteamCondenserException if the request fails
     * @throws TimeoutException if too many timeouts occur while querying the
     *         master server
     */
    public Vector<InetSocketAddress> getServers(byte regionCode, String filter, boolean force)
            throws SteamCondenserException, TimeoutException {
        int failCount    = 0;
        boolean finished = false;
        int portNumber   = 0;
        String hostName  = "0.0.0.0";
        Vector<String> serverStringArray;
        Vector<InetSocketAddress> serverArray = new Vector<InetSocketAddress>();

        while(true) {
            try {
                failCount = 0;
                do {
                    this.socket.send(new A2M_GET_SERVERS_BATCH2_Paket(regionCode, hostName + ":" + portNumber, filter));
                    try {
                        serverStringArray = ((M2A_SERVER_BATCH_Paket) this.socket.getReply()).getServers();

                        for(String serverString : serverStringArray) {
                            hostName = serverString.substring(0, serverString.lastIndexOf(":"));
                            portNumber = Integer.valueOf(serverString.substring(serverString.lastIndexOf(":") + 1));

                            if(!hostName.equals("0.0.0.0") && portNumber != 0) {
                                serverArray.add(new InetSocketAddress(hostName, portNumber));
                            } else {
                                finished = true;
                            }
                        }
                        failCount = 0;
                    } catch(TimeoutException e) {
                        failCount ++;
                        if(failCount == retries) {
                            throw e;
                        }
                        LOG.info("Request to master server " + this.ipAddress + " timed out, retrying...");
                    }
                } while(!finished);
                break;
            } catch(TimeoutException e) {
                if (force) {
                    break;
                } else if(this.rotateIp()) {
                    throw e;
                }
                LOG.info("Request to master server failed, retrying " + this.ipAddress + "...");
            }
        }

        return serverArray;
    }

    /**
     * Initializes the socket to communicate with the master server
     *
     * @see MasterServerSocket
     */
    public void initSocket() throws SteamCondenserException {
        this.socket = new MasterServerSocket(this.ipAddress, this.port);
    }

}
