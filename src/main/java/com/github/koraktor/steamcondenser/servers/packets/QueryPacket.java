package com.github.koraktor.steamcondenser.servers.packets;

import java.util.Arrays;

/**
 * This is used as a wrapper to create padding of request packets to a minimum
 * size of 1200 bytes. This was introduced in November 2020 as a
 * counter-measure to DoS attacks on game servers.
 *
 * @author Sebastian Staudt
 */
class QueryPacket extends SteamPacket {

    // The minimum package size as defined by Valve
    private static final int STEAM_GAMESERVER_MIN_CONNECTIONLESS_PACKET_SIZE = 1200;

    /**
     * Creates a new query packet including data padding
     *
     * @param data The data of the original query
     */
    QueryPacket(byte header, byte[] data) {
        super(header, Arrays.copyOf(data, STEAM_GAMESERVER_MIN_CONNECTIONLESS_PACKET_SIZE - 5));
    }
}
