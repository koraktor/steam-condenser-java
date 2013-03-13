/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.webapi.exceptions;

import com.github.koraktor.steamcondenser.exceptions.WebApiException;

/**
 * This exception is raised when the data provided in a Steam Web API response cannot be parsed.
 * The most common cause is expected but missing data within the response.
 * 
 * This exception is designed to provide a more specific {@link WebApiException} so that users
 * of the API will know programmatically what kind of exception has been thrown.
 *
 * @author Lewis Keen
 * @author Sebastian Staudt
 */
public class ParseException extends WebApiException {
	private static final long serialVersionUID = 1L;

    /**
     * Creates a new <code>ParseException</code> instance
     *
     * @param message The message to attach to the exception
     */
	public ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
