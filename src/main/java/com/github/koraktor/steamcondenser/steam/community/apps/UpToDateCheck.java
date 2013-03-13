/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.community.apps;


/**
 * A object which contains all details about whether a game is up to date.
 * This data has been retrieved from the UpToDateCehck operation in the ISteamApps Web API service.
 *
 * @author Lewis Keen
 * @author Sebastian Staudt
 */
public class UpToDateCheck {
	private final boolean upToDate, versionIsListable;
	private final Integer requiredVersion;
	private final String message;
	
	public UpToDateCheck(boolean upToDate, boolean versionIsListable, Integer requiredVersion, String message) {
		this.upToDate = upToDate;
		this.versionIsListable = versionIsListable;
		this.requiredVersion = requiredVersion;
		this.message = message;
	}

	public boolean isUpToDate() {
		return upToDate;
	}

	public boolean isVersionIsListable() {
		return versionIsListable;
	}

	public int getRequiredVersion() {
		return requiredVersion;
	}

	public String getMessage() {
		return message;
	}
}