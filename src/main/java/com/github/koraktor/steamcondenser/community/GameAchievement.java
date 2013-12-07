/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2008-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.koraktor.steamcondenser.exceptions.WebApiException;

/**
 * The GameAchievement class represents a specific achievement for a single
 * game and for a single user
 * <p>
 * It also provides the ability to load the global unlock percentages of all
 * achievements of a specific game.
 *
 * @author Sebastian Staudt
 */
public class GameAchievement {

    private String apiName;

    private String description;

    private SteamGame game;

    private String iconClosedUrl;

    private String iconOpenUrl;

    private String name;

    private Date timestamp;

    private boolean unlocked;

    private SteamId user;

    /**
     * Loads the global unlock percentages of all achievements for the given
     * game
     *
     * @param appId The unique Steam Application ID of the game (e.g.
     *        <code>440</code> for Team Fortress 2). See
     *        http://developer.valvesoftware.com/wiki/Steam_Application_IDs for
     *        all application IDs
     * @return The symbolic achievement names with the corresponding global
     *         unlock percentages
     * @throws WebApiException if a request to Steam's Web API fails
     */
    public static Map<String, Double> getGlobalPercentages(int appId)
            throws WebApiException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("gameid", appId);

        try {
            JSONObject data = new JSONObject(WebApi.getJSON("ISteamUserStats", "GetGlobalAchievementPercentagesForApp", 2, params));

            HashMap<String, Double> percentages = new HashMap<String, Double>();
            JSONArray achievementsData = data.getJSONObject("achievementpercentages").getJSONArray("achievements");
            for(int i = 0; i < achievementsData.length(); i ++) {
                JSONObject achievementData = achievementsData.getJSONObject(i);
                percentages.put(achievementData.getString("name"), achievementData.getDouble("percent"));
            }

            return percentages;
        } catch(JSONException e) {
            throw new WebApiException("Could not parse JSON data.", e);
        }
    }

    /**
     * Creates the achievement with the given name for the given user and game
     * and achievement data
     *
     * @param user The Steam ID of the player this achievement belongs to
     * @param game The game this achievement belongs to
     * @param achievementData The achievement data extracted from XML
     */
    public GameAchievement(SteamId user, SteamGame game, XMLData achievementData) {
        this.apiName       = achievementData.getString("apiname");
        this.description   = achievementData.getString("description");
        this.game          = game;
        this.iconClosedUrl = achievementData.getString("iconClosed");
        this.iconOpenUrl   = achievementData.getString("iconOpen");
        this.name          = achievementData.getString("name");
        this.unlocked      = achievementData.getAttribute("closed").equals("1");
        this.user          = user;

        if(this.unlocked && achievementData.hasElement("unlockTimestamp")) {
            this.timestamp = new Date(achievementData.getLong("unlockTimestamp") * 1000);
        }
    }

    /**
     * Returns the symbolic API name of this achievement
     *
     * @return The API name of this achievement
     */
    public String getApiName() {
        return this.apiName;
    }

    /**
     * Return the description of this achievement
     *
     * @return The description of this achievement
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the game this achievement belongs to
     *
     * @return The game this achievement belongs to
     */
    public SteamGame getGame() {
        return this.game;
    }

    /**
     * Return the url for the closed icon of this achievement
     *
     * @return The url for the closed icon of this achievement
     */
    public String getIconClosedURL() {
        return this.iconClosedUrl;
    }

    /**
     * Return the url for the open icon of this achievement
     *
     * @return The url for the open icon of this achievement
     */
    public String getIconOpenURL() {
        return this.iconOpenUrl;
    }

    /**
     * Returns the name of this achievement
     *
     * @return The name of this achievement
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the time this achievement has been unlocked by its owner
     *
     * @return The time this achievement has been unlocked
     */
    public Date getTimestamp() {
        return this.timestamp;
    }

    /**
     * Returns the SteamID of the user who owns this achievement
     *
     * @return The SteamID of this achievement's owner
     */
    public SteamId getUser() {
        return this.user;
    }

    /**
     * Returns whether this achievement has been unlocked by its owner
     *
     * @return <code>true</code> if the achievement has been unlocked by
     *         the user
     */
    public boolean isUnlocked() {
        return this.unlocked;
    }

    @Override
    public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("apiName", this.apiName)
        .append("name", this.name)
        .append("unlocked", this.unlocked)
        .append("timestamp", this.timestamp)
        .append("user", this.user)
        .append("game", this.game)
        .toString();
    }

}
