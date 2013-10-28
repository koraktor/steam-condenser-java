/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2008-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.servers.packets;

/**
 * This class represents a S2A_INFO_DETAILED response packet sent by a Source
 * or GoldSrc server
 * <p>
 * Out-of-date (before 10/24/2008) GoldSrc servers use an older format (see
 * {@link S2A_INFO_DETAILED_Packet}).
 *
 * @author Sebastian Staudt
 * @see com.github.koraktor.steamcondenser.servers.GameServer#updateServerInfo
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

        this.info.put("networkVersion", this.contentData.getByte());
        this.info.put("serverName", this.contentData.getString());
        this.info.put("mapName", this.contentData.getString());
        this.info.put("gameDir", this.contentData.getString());
        this.info.put("gameDescription", this.contentData.getString());
        this.info.put("appId", Short.reverseBytes(this.contentData.getShort()));
        this.info.put("numberOfPlayers", this.contentData.getByte());
        this.info.put("maxPlayers", this.contentData.getByte());
        this.info.put("numberOfBots", this.contentData.getByte());
        this.info.put("dedicated", this.contentData.getByte());
        this.info.put("operatingSystem", this.contentData.getByte());
        this.info.put("passwordProtected", this.contentData.getByte() == 1);
        this.info.put("secure", this.contentData.getByte() == 1);
        this.info.put("gameVersion", this.contentData.getString());

        if(this.contentData.remaining() > 0) {
            byte extraDataFlag = this.contentData.getByte();

            if ((extraDataFlag & EDF_GAME_PORT) != 0) {
                this.info.put("serverPort", Short.reverseBytes(this.contentData.getShort()));
            }

            if ((extraDataFlag & EDF_SERVER_ID) != 0) {
                this.info.put("serverId", Long.reverseBytes((this.contentData.getInt() << 32) | this.contentData.getInt()));
            }

            if ((extraDataFlag & EDF_SOURCE_TV) != 0) {
                this.info.put("tvPort", Short.reverseBytes(this.contentData.getShort()));
                this.info.put("tvName", this.contentData.getString());
            }

            if ((extraDataFlag & EDF_SERVER_TAGS) != 0) {
                this.info.put("serverTags", this.contentData.getString());
            }

            if ((extraDataFlag & EDF_GAME_ID) != 0) {
                this.info.put("gameId", Long.reverseBytes((this.contentData.getInt() << 32) | this.contentData.getInt()));
            }
        }
    }

}
