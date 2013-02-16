/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.exceptions;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Sebastian Staudt
 */
public class WebApiExceptionTest {

    @Test
    public void testInvalidKey() {
        WebApiException exception = new WebApiException(WebApiException.Cause.INVALID_KEY);
        assertThat(exception.getMessage(), is(equalTo("This is not a valid Steam Web API key.")));
    }

    @Test
    public void testSimple() {
        WebApiException exception = new WebApiException("message");
        assertThat(exception.getMessage(), is(equalTo("message")));
    }

}
