/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2008-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.packets;

/**
 * This class represents a S2A_INFO_DETAILED response packet sent by a Source
 * or GoldSrc server
 * <p>
 * Out-of-date (before 10/24/2008) GoldSrc servers use an older format (see
 * {@link S2A_INFO_DETAILED_Packet}).
 *
 * @author Sebastian Staudt
 * @see com.github.koraktor.steamcondenser.steam.servers.GameServer#updateServerInfo
 */
public class S2A_INFO2_Packet extends S2A_INFO_BasePacket {

    private static byte EDF_GAME_ID     = (byte) 0x01;
    private static byte EDF_GAME_PORT   = (byte) 0x80;
    private static byte EDF_SERVER_ID   = (byte) 0x10;
    private static byte EDF_SERVER_TAGS = (byte) 0x20;
    private static byte EDF_SOURCE_TV   = (byte) 0x40;

    /**
     * Creates a new S2A_INFO2 response object based on the given data
     *
     * @param dataBytes The raw packet data replied from the server
     */
    public S2A_INFO2_Packet(byte[] dataBytes) {
        super(SteamPacket.S2A_INFO2_HEADER, dataBytes);

        this.getInfo().put("networkVersion", this.contentData.getByte());
        this.getInfo().put("serverName", this.contentData.getString());
        this.getInfo().put("mapName", this.contentData.getString());
        this.getInfo().put("gameDir", this.contentData.getString());
        this.getInfo().put("gameDescription", this.contentData.getString());
        this.getInfo().put("appId", Short.reverseBytes(this.contentData.getShort()));
        this.getInfo().put("numberOfPlayers", this.contentData.getByte());
        this.getInfo().put("maxPlayers", this.contentData.getByte());
        this.getInfo().put("numberOfBots", this.contentData.getByte());
        this.getInfo().put("dedicated", this.contentData.getByte());
        this.getInfo().put("operatingSystem", this.contentData.getByte());
        this.getInfo().put("passwordProtected", this.contentData.getByte() == 1);
        this.getInfo().put("secure", this.contentData.getByte() == 1);
        this.getInfo().put("gameVersion", this.contentData.getString());

        if(this.contentData.remaining() > 0) {
            byte extraDataFlag = this.contentData.getByte();

            if ((extraDataFlag & EDF_GAME_PORT) != 0) {
                this.getInfo().put("serverPort", Short.reverseBytes(this.contentData.getShort()));
            }

            if ((extraDataFlag & EDF_SERVER_ID) != 0) {
                this.getInfo().put("serverId", Long.reverseBytes((this.contentData.getInt() << 32) | this.contentData.getInt()));
            }

            if((extraDataFlag & EDF_SOURCE_TV) != 0) {
                this.getInfo().put("tvPort", Short.reverseBytes(this.contentData.getShort()));
                this.getInfo().put("tvName", this.contentData.getString());
            }

            if((extraDataFlag & EDF_SERVER_TAGS) != 0) {
                this.getInfo().put("serverTags", this.contentData.getString());
            }

            if ((extraDataFlag & EDF_GAME_ID) != 0) {
                this.getInfo().put("gameId", Long.reverseBytes((this.contentData.getInt() << 32) | this.contentData.getInt()));
            }
        }
    }

}
