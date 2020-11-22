/*
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2020, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.packets;

import org.apache.commons.lang3.ArrayUtils;

/**
 * This is used as a wrapper to create padding of request packets to a minimum
 * size of 1200 bytes. This was introduced in November 2020 as a
 * counter-measure to DoS attacks on game servers.
 *
 * @author Sebastian Staudt
 */
abstract class QueryPacket extends SteamPacket {

    // The minimum package size as defined by Valve
    private static final int STEAM_GAMESERVER_MIN_CONNECTIONLESS_PACKET_SIZE = 1200;

    static byte[] addPadding(byte[] data) {
        if (data.length < STEAM_GAMESERVER_MIN_CONNECTIONLESS_PACKET_SIZE - 5) {
            byte[] padding = new byte[STEAM_GAMESERVER_MIN_CONNECTIONLESS_PACKET_SIZE - 5 - data.length];
            return ArrayUtils.addAll(data, padding);
        } else {
            return data;
        }
    }

    /**
     * Creates a new query packet including data padding
     *
     * @param data The data of the original query
     */
    QueryPacket(byte header, byte[] data) {
        super(header, addPadding(data));
    }
}
