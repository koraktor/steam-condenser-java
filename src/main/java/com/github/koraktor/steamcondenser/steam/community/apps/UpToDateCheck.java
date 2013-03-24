/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.community.apps;

/**
 * A object which contains all details about whether a game is up to date.
 * This data has been retrieved from the UpToDateCheck operation in the ISteamApps Web API service.
 *
 * @author Lewis Keen
 * @author Sebastian Staudt
 */
public class UpToDateCheck {
	private final int appId;
	private final boolean upToDate, versionIsListable;
	private final Integer requiredVersion;
	private final String message;
	
	/**
	 * Creates an immutable object which contains the details of an up-to-date check on an app.
	 * 
	 * @param appId The unique Steam Application ID of the game (e.g.
     *        <code>440</code> for Team Fortress 2). See
     *        http://developer.valvesoftware.com/wiki/Steam_Application_IDs for
     *        all application IDs
	 * @param upToDate A boolean that specifies if the app is up to date or not.
	 * @param versionIsListable A boolean that specifies if the app version is listable or not.
	 * @param requiredVersion the version number of the app, if not up-to-date.
	 * @param message The message provided when checking if the application is up to date.
	 */
	public UpToDateCheck(int appId, boolean upToDate, boolean versionIsListable, Integer requiredVersion, String message) {
		this.appId = appId;
		this.upToDate = upToDate;
		this.versionIsListable = versionIsListable;
		this.requiredVersion = requiredVersion;
		this.message = message;
	}

    /**
     * Returns the Steam Application ID of the game this game stats schema belong to
     *
     * @return The application ID of the game these achievements belong to
     */
	public int getAppId() {
		return appId;
	}

	/**
	 * Is the app up to date, given the provided appId and version number.
	 * 
	 * @return A boolean that specifies if the app is up to date or not.
	 */
	public boolean isUpToDate() {
		return upToDate;
	}

	/**
	 * Is the version listable for the provided appId.
	 * 
	 * @return A boolean that specifies if the app version is listable or not.
	 */
	public boolean isVersionIsListable() {
		return versionIsListable;
	}

	/**
	 * Returns the most up-to-date version number of the app.
	 * 
	 * @return the version number of the app, if not up-to-date. Returns null if app is up-to-date.
	 */
	public Integer getRequiredVersion() {
		return requiredVersion;
	}

	/**
	 * Returns the message provided when checking if the application is up to date.
	 * 
	 * @return The message provided when checking if the application is up to date. Returns null if app is up-to-date.
	 */
	public String getMessage() {
		return message;
	}
}