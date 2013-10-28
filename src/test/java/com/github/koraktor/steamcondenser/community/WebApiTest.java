/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2012-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.json.JSONObject;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.koraktor.steamcondenser.exceptions.WebApiException;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 *
 *
 * @author Sebastian Staudt
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ DefaultHttpClient.class, WebApi.class })
public class WebApiTest {

    @Rule
    private ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() throws Exception {
        WebApi.apiKey = "0123456789ABCDEF0123456789ABCDEF";
        WebApi.setSecure(true);
    }

    @Test
    public void testGetApiKey() {
        assertThat(WebApi.getApiKey(), is(equalTo("0123456789ABCDEF0123456789ABCDEF")));
    }

    @Test
    public void testSetApiKey() throws Exception {
        WebApi.setApiKey("FEDCBA9876543210FEDCBA9876543210");
        assertThat(WebApi.getApiKey(), is(equalTo("FEDCBA9876543210FEDCBA9876543210")));
    }

    @Test
    public void testInvalidApiKey() throws Exception {
        this.exception.expect(WebApiException.class);
        this.exception.expectMessage("This is not a valid Steam Web API key.");

        WebApi.setApiKey("test");
    }

    @Test
    public void testGetJSON() throws Exception {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("test", "param");

        spy(WebApi.class);
        doReturn("test").when(WebApi.class, "load", "json", "interface", "method", 2, params);

        assertThat(WebApi.getJSON("interface", "method", 2, params), is(equalTo("test")));
    }

    @Test
    public void testGetJSONData() throws Exception {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("test", "param");

        String data = mock(String.class);
        spy(WebApi.class);
        doReturn(data).when(WebApi.class, "getJSON", "interface", "method", 2, params);
        JSONObject json = mock(JSONObject.class);
        JSONObject result = mock(JSONObject.class);
        when(json.getJSONObject("result")).thenReturn(result);
        when(result.getInt("status")).thenReturn(1);
        whenNew(JSONObject.class).withParameterTypes(String.class).withArguments(data).thenReturn(json);

        assertThat(WebApi.getJSONData("interface", "method", 2, params), is(result));
    }

    @Test
    public void testGetJSONDataFailed() throws Exception {
        String data = mock(String.class);
        spy(WebApi.class);
        doReturn(data).when(WebApi.class, "getJSON", "interface", "method", 2, null);
        JSONObject json = mock(JSONObject.class);
        JSONObject result = mock(JSONObject.class);
        when(json.getJSONObject("result")).thenReturn(result);
        when(result.getInt("status")).thenReturn(0);
        when(result.getString("statusDetail")).thenReturn("Error");
        whenNew(JSONObject.class).withParameterTypes(String.class).withArguments(data).thenReturn(json);

        this.exception.expect(WebApiException.class);
        this.exception.expectMessage("The Web API request failed with the following error: Error (status code: 0).");

        WebApi.getJSONData("interface", "method", 2);
    }

    @Test
    public void testLoad() throws Exception {
        DefaultHttpClient httpClient = mock(DefaultHttpClient.class);
        when(httpClient.getParams()).thenReturn(new BasicHttpParams());
        whenNew(DefaultHttpClient.class).withNoArguments().thenReturn(httpClient);

        this.prepareRequest("https://api.steampowered.com/interface/method/v0002/?test=param&format=json&key=0123456789ABCDEF0123456789ABCDEF", 200, null, "test");

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("test", "param");

        assertThat(WebApi.load("json", "interface", "method", 2, params), is(equalTo("test")));
    }

    @Test
    public void testLoadInsecure() throws Exception {
        WebApi.setSecure(false);

        DefaultHttpClient httpClient = mock(DefaultHttpClient.class);
        when(httpClient.getParams()).thenReturn(new BasicHttpParams());
        whenNew(DefaultHttpClient.class).withNoArguments().thenReturn(httpClient);

        this.prepareRequest("http://api.steampowered.com/interface/method/v0002/?test=param&format=json&key=0123456789ABCDEF0123456789ABCDEF", 200, null, "test");

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("test", "param");

        assertThat(WebApi.load("json", "interface", "method", 2, params), is(equalTo("test")));
    }

    @Test
    public void testLoadUnauthorized() throws Exception {
        this.exception.expect(WebApiException.class);
        this.exception.expectMessage("Your Web API request has been rejected. You most likely did not specify a valid Web API key.");

        this.prepareRequest("https://api.steampowered.com/interface/method/v0002/?format=json&key=0123456789ABCDEF0123456789ABCDEF", 401, null, null);

        WebApi.load("json", "interface", "method", 2);
    }

    @Test
    public void testLoadWithoutKey() throws Exception {
        WebApi.setApiKey(null);

        DefaultHttpClient httpClient = mock(DefaultHttpClient.class);
        when(httpClient.getParams()).thenReturn(new BasicHttpParams());
        whenNew(DefaultHttpClient.class).withNoArguments().thenReturn(httpClient);

        this.prepareRequest("https://api.steampowered.com/interface/method/v0002/?test=param&format=json", 200, null, "test");

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("test", "param");

        assertThat(WebApi.load("json", "interface", "method", 2, params), is(equalTo("test")));
    }

    @Test
    public void testLoadError() throws Exception {
        this.exception.expect(WebApiException.class);
        this.exception.expectMessage("The Web API request has failed due to an HTTP error: Not found (status code: 404).");

        this.prepareRequest("https://api.steampowered.com/interface/method/v0002/?format=json&key=0123456789ABCDEF0123456789ABCDEF", 404, "Not found", null);

        WebApi.load("json", "interface", "method", 2);
    }

    private void prepareRequest(String url, int statusCode, String reason, String content) throws Exception {
        DefaultHttpClient httpClient = mock(DefaultHttpClient.class);
        when(httpClient.getParams()).thenReturn(new BasicHttpParams());
        whenNew(DefaultHttpClient.class).withNoArguments().thenReturn(httpClient);

        HttpGet request = mock(HttpGet.class);
        whenNew(HttpGet.class).withArguments(url).thenReturn(request);

        HttpResponse response = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        when(statusLine.getReasonPhrase()).thenReturn(reason);
        when(statusLine.getStatusCode()).thenReturn(statusCode);
        when(response.getStatusLine()).thenReturn(statusLine);

        if (content != null) {
            HttpEntity entity = mock(HttpEntity.class);
            when(entity.getContent()).thenReturn(new ByteArrayInputStream(content.getBytes()));
            when(response.getEntity()).thenReturn(entity);
        }

        doReturn(response).when(httpClient).execute(request);
        when(httpClient.execute(request)).thenReturn(response);
    }

}
