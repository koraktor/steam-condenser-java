package com.github.koraktor.steamcondenser.steam.community.apps;

public class ServerAtAddress {
	private final String address, gameDir;
	private final int gmsIndex, appId, region, gamePort, specPort;
	private final boolean secure, lan;

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
	
	public String getAddress() {
		return address;
	}
	public String getGameDir() {
		return gameDir;
	}
	public int getGmsIndex() {
		return gmsIndex;
	}
	public int getAppId() {
		return appId;
	}
	public int getRegion() {
		return region;
	}
	public int getGamePort() {
		return gamePort;
	}
	public int getSpecPort() {
		return specPort;
	}
	public boolean isSecure() {
		return secure;
	}
	public boolean isLan() {
		return lan;
	}
}
