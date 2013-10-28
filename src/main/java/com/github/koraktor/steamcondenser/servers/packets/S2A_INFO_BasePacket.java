/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2008-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.servers.packets;

import java.util.HashMap;

/**
 * This module implements methods to generate and access server information
 * from S2A_INFO_DETAILED and S2A_INFO2 response packets
 *
 * @author Sebastian Staudt
 * @see S2A_INFO_DETAILED_Packet
 * @see S2A_INFO2_Packet
 */
public abstract class S2A_INFO_BasePacket extends SteamPacket {

    protected HashMap<String, Object> info;

    S2A_INFO_BasePacket(byte headerByte, byte[] dataBytes) {
        super(headerByte, dataBytes);

        this.info = new HashMap<String, Object>();
    }

    /**
     * Returns a generated array of server properties from the instance
     * variables of the packet object
     *
     * @return The information provided by the server
     */
    public HashMap<String, Object> getInfo() {
        return this.info;
    }

}
