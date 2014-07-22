/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2008-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.servers.sockets;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.koraktor.steamcondenser.exceptions.ConnectionResetException;
import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.servers.packets.rcon.RCONPacket;
import com.github.koraktor.steamcondenser.servers.packets.rcon.RCONPacketFactory;

/**
 * This class represents a socket used for RCON communication with game servers
 * based on the Source engine (e.g. Team Fortress 2, Counter-Strike: Source)
 * <p>
 * The Source engine uses a stateful TCP connection for RCON communication and
 * uses an additional socket of this type to handle RCON requests.
 *
 * @author Sebastian Staudt
 */
public class RCONSocket extends SteamSocket {

    protected final static Logger LOG = LoggerFactory.getLogger(RCONSocket.class.getName());

    /**
     * Creates a new TCP socket to communicate with the server on the given IP
     * address and port
     *
     * @param ipAddress Either the IP address or the DNS name of the server
     * @param portNumber The port the server is listening on
     */
    public RCONSocket(InetAddress ipAddress, int portNumber) {
        super(ipAddress, portNumber);
    }

    /**
     * Closes the underlying TCP socket if it is connected
     *
     * @see SteamSocket#close
     */
    @Override
    public void close() {
        if (this.channel != null &&
            ((SocketChannel) this.channel).isConnected()) {
            super.close();
        }
    }

    /**
     * Sends the given RCON packet to the server
     *
     * @param dataPacket The RCON packet to send to the server
     * @throws SteamCondenserException if an error occurs while writing to the
     *         socket
     */
    public void send(RCONPacket dataPacket)
            throws SteamCondenserException {
        try {
            if (this.channel == null ||
               !((SocketChannel)this.channel).isConnected()) {
                this.channel = SocketChannel.open();
                ((SocketChannel) this.channel).socket().connect(this.remoteSocket, SteamSocket.timeout);
                this.channel.configureBlocking(false);
            }

            this.buffer = ByteBuffer.wrap(dataPacket.getBytes());
            ((SocketChannel)this.channel).write(this.buffer);
        } catch(IOException e) {
            throw new SteamCondenserException(e.getMessage(), e);
        }
    }

    /**
     * Reads a packet from the socket
     * <p>
     * The Source RCON protocol allows packets of an arbitrary sice transmitted
     * using multiple TCP packets. The data is received in chunks and
     * concatenated into a single response packet.
     *
     * @return The packet replied from the server or <code>null</code> if the
     *         connection has been closed by the server
     * @throws SteamCondenserException if an error occurs while communicating
     *         with the server
     * @throws TimeoutException if the request times out
     */
    public RCONPacket getReply()
            throws SteamCondenserException, TimeoutException {
        try {
            if (this.receivePacket(4) == 0) {
                try {
                    this.channel.close();
                } catch (IOException ignored) {}
                return null;
            }
        } catch (ConnectionResetException e) {
            try {
                this.channel.close();
            } catch (IOException ignored) {}
            return null;
        }

        int packetSize = Integer.reverseBytes(this.buffer.getInt());
        int remainingBytes = packetSize;

        byte[] packetData = new byte[packetSize];
        int receivedBytes;
        do {
            receivedBytes = this.receivePacket(remainingBytes);
            System.arraycopy(this.buffer.array(), 0, packetData, packetSize - remainingBytes, receivedBytes);
            remainingBytes -= receivedBytes;
        } while(remainingBytes > 0);

        RCONPacket packet = RCONPacketFactory.getPacketFromData(packetData);

        LOG.info("Received packet of type \"" + packet.getClass() + "\".");

        return packet;
    }
}
