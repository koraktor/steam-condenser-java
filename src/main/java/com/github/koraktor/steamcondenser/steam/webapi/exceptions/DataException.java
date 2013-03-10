/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.webapi.exceptions;

import com.github.koraktor.steamcondenser.exceptions.WebApiException;

/**
 * This exception is raised when there is no positive data in a Steam Web API response.
 * Reasons why this exception can be thrown include:
 * <li>
 * 	<ul>A data error in the call to GetPlayerAchievements e.g. requested app has no stats.</ul>
 *  <ul>No schema for app in call to GetSchemaForGame</ul>
 * </li>
 * 
 * This exception is designed to provide a more specific {@link WebApiException} so that users
 * of the API will know programmatically what kind of exception has been thrown.
 *
 * @author Lewis Keen
 * @author Sebastian Staudt
 */
public class DataException extends WebApiException {
	private static final long serialVersionUID = 1L;

    /**
     * Creates a new <code>DataException</code> instance
     *
     * @param message The message to attach to the exception
     */
	public DataException(String message) {
		super(message);
	}
}
