/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2008-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.packets;

import java.util.HashMap;

/**
 * This class represents a S2A_INFO_DETAILED response packet sent by a GoldSrc
 * server
 *
 * @author Sebastian Staudt
 * @deprecated Only outdated GoldSrc servers (before 10/24/2008) use this
 *             format. Newer ones use the same format as Source servers now
 *             (see {@link S2A_INFO2_Packet}).
 * @see com.github.koraktor.steamcondenser.steam.servers.GameServer#updateServerInfo
 */
public class S2A_INFO_DETAILED_Packet extends S2A_INFO_BasePacket {

    /**
     * Creates a new S2A_INFO_DETAILED response object based on the given data
     *
     * @param dataBytes The raw packet data replied from the server
     */
    public S2A_INFO_DETAILED_Packet(byte[] dataBytes) {
        super(SteamPacket.S2A_INFO_DETAILED_HEADER, dataBytes);

        this.getInfo().put("serverIp", this.contentData.getString());
        this.getInfo().put("serverName", this.contentData.getString());
        this.getInfo().put("mapName", this.contentData.getString());
        this.getInfo().put("gameDir", this.contentData.getString());
        this.getInfo().put("gameDescription", this.contentData.getString());
        this.getInfo().put("numberOfPlayers", this.contentData.getByte());
        this.getInfo().put("maxPlayers", this.contentData.getByte());
        this.getInfo().put("networkVersion", this.contentData.getByte());
        this.getInfo().put("dedicated", this.contentData.getByte());
        this.getInfo().put("operatingSystem", this.contentData.getByte());
        this.getInfo().put("passwordProtected", this.contentData.getByte() == 1);
        boolean isMod = this.contentData.getByte() == 1;
        this.getInfo().put("isMod", isMod);

        if(isMod) {
            HashMap<String, Object> modInfo = new HashMap<String, Object>(6);
            modInfo.put("urlInfo", this.contentData.getString());
            modInfo.put("urlDl", this.contentData.getString());
            this.contentData.getByte();
            if(this.contentData.remaining() == 12) {
                modInfo.put("modVersion", Integer.reverseBytes(this.contentData.getInt()));
                modInfo.put("modSize", Integer.reverseBytes(this.contentData.getInt()));
                modInfo.put("svOnly", this.contentData.getByte() == 1);
                modInfo.put("clDll", this.contentData.getByte() == 1);
                this.getInfo().put("secure", this.contentData.getByte() == 1);
                this.getInfo().put("numberOfBots", this.contentData.getByte());
            }
            this.getInfo().put("modInfo", modInfo);
        } else {
            this.getInfo().put("secure", this.contentData.getByte() == 1);
            this.getInfo().put("numberOfBots", this.contentData.getByte());
        }
    }

}
