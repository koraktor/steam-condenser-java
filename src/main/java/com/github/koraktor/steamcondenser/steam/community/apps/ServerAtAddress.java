/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.community.apps;

/**
 * A object which contains all details about a server which is hosted on a particular IP address
 * This data has been retrieved from the GetServersAtAddress operation in the ISteamApps Web API service.
 *
 * @author Lewis Keen
 * @author Sebastian Staudt
 */
public class ServerAtAddress {
	private final String address, gameDir;
	private final int gmsIndex, appId, region, gamePort, specPort;
	private final boolean secure, lan;

	/**
	 * Creates an immutable object which contains the details of the a server on a particular IP address.
	 * 
	 * @param address The full address of the server, both IP and port.
	 * @param gmsIndex The GMS Index of the server.
	 * @param appId The id of the app hosted on this address.
	 * @param gameDir The game directory for the app.
	 * @param region The region the server is hosted in.
	 * @param secure Whether the server is secure or not.
	 * @param lan Whether the server is a LAN server or not.
	 * @param gamePort The port on which the server is hosted.
	 * @param specPort The port on which a spectator can view the main server.
	 */
	public ServerAtAddress(String address, int gmsIndex, int appId, String gameDir, int region, boolean secure, boolean lan, int gamePort, int specPort) {
		this.address = address;
		this.gmsIndex = gmsIndex;
		this.appId = appId;
		this.gameDir = gameDir;
		this.region = region;
		this.secure = secure;
		this.lan = lan;
		this.gamePort = gamePort;
		this.specPort = specPort;
	}
	
	/**
	 * Returns the full address of the server, both IP and port.
	 * 
	 * @return The full address of the server, both IP and port.
	 */
	public String getAddress() {
		return address;
	}
	
	/**
	 * Returns the GMS Index of the server.
	 * 
	 * @return The GMS Index of the server.
	 */
	public int getGmsIndex() {
		return gmsIndex;
	}
	
	/**
	 * Returns the id of the app hosted on this address.
	 * 
	 * @return The id of the app hosted on this address.
	 */
	public int getAppId() {
		return appId;
	}

	/**
	 * Returns the game directory for the app.
	 * 
	 * @return The game directory for the app.
	 */
	public String getGameDir() {
		return gameDir;
	}
	
	
	/**
	 * Returns the region the server is hosted in.
	 * 
	 * @return The region the server is hosted in.
	 */
	public int getRegion() {
		return region;
	}
	
	/**
	 * Returns whether the server is secure or not.
	 * 
	 * @return Whether the server is secure or not.
	 */
	public boolean isSecure() {
		return secure;
	}
	
	/**
	 * Returns whether the server is a LAN server or not.
	 * 
	 * @return Whether the server is a LAN server or not.
	 */
	public boolean isLan() {
		return lan;
	}

	/**
	 * Returns the port on which the server is hosted.
	 * 
	 * @return The port on which the server is hosted.
	 */
	public int getGamePort() {
		return gamePort;
	}
	
	/**
	 * Returns the port on which a spectator can view the main server.
	 * 
	 * @return The port on which a spectator can view the main server.
	 */
	public int getSpecPort() {
		return specPort;
	}
	
}
