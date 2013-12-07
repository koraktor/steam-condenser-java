/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.exceptions.WebApiException;

/**
 * This class represents a game available on Steam
 *
 * @author Sebastian Staudt
 */
public class SteamGame {

    private static Map<Integer, SteamGame> games = new HashMap<Integer, SteamGame>();

    private int appId;

    private String iconUrl;

    private String logoHash;

    private String name;

    private String shortName;

    /**
     * Clears the game cache
     */
    public static void clearCache() {
        games.clear();
    }

    /**
     * Checks if a game is up-to-date by reading information from a
     * <code>steam.inf</code> file and comparing it using the Web API
     *
     * @param path The file system path of the `steam.inf` file
     * @return <code>true</code> if the game is up-to-date
     * @throws IOException if the steam.inf cannot be read
     * @throws JSONException if the JSON data is malformed
     * @throws SteamCondenserException if the given steam.inf is invalid or
     *         the Web API request fails
     */
    public static boolean checkSteamInf(String path)
            throws IOException, JSONException, SteamCondenserException {
        BufferedReader steamInf = new BufferedReader(new FileReader(path));
        String steamInfContents = "";

        while(steamInf.ready()) {
            steamInfContents += steamInf.readLine() + "\n";
        }
        steamInf.close();

        Pattern appIdPattern = Pattern.compile("^\\s*appID=(\\d+)\\s*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher appIdMatcher = appIdPattern.matcher(steamInfContents);
        Pattern versionPattern = Pattern.compile("^\\s*PatchVersion=([\\d\\.]+)\\s*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher versionMatcher = versionPattern.matcher(steamInfContents);

        if(!(appIdMatcher.find() && versionMatcher.find())) {
            throw new SteamCondenserException("The steam.inf file at \"" + path + "\" is invalid.");
        }

        int appId = Integer.parseInt(appIdMatcher.group(1));
        int version = Integer.parseInt(versionMatcher.group(1).replace(".", ""));

        return isUpToDate(appId, version);
    }

    /**
     * Creates a new or cached instance of the game specified by the given XML
     * data
     *
     * @param appId The application ID of the game
     * @param gameData The XML data of the game
     * @return The game instance for the given data
     * @see SteamGame#SteamGame
     */
    public static SteamGame create(int appId, XMLData gameData) {
        if(games.containsKey(appId)) {
            return games.get(appId);
        } else {
            return new SteamGame(appId, gameData);
        }
    }

    /**
     * Returns whether the given version of the game with the given application
     * ID is up-to-date
     *
     * @param appId The application ID of the game to check
     * @param version The version to check against the Web API
     * @return <code>true</code> if the given version is up-to-date
     * @throws JSONException if the JSON data is malformed
     * @throws SteamCondenserException if the Web API request fails
     */
    public static boolean isUpToDate(int appId, int version)
            throws JSONException, SteamCondenserException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appid", appId);
        params.put("version", version);
        String json = WebApi.getJSON("ISteamApps", "UpToDateCheck", 1, params);
        JSONObject result = new JSONObject(json).getJSONObject("response");
        if(!result.getBoolean("success")) {
            throw new SteamCondenserException(result.getString("error"));
        }
        return result.getBoolean("up_to_date");
    }

    /**
     * Creates a new instance of a game with the given data and caches it
     *
     * @param appId The application ID of the game
     * @param gameData The XML data of the game
     */
    private SteamGame(int appId, XMLData gameData) {
        this.appId = appId;

        String logoUrl;
        if(gameData.hasElement("name")) {
            logoUrl = gameData.getString("logo");
            this.name  = gameData.getString("name");

            if(gameData.hasElement("globalStatsLink")) {
                Pattern regex = Pattern.compile("http://steamcommunity.com/stats/([^?/]+)/achievements/");
                Matcher matcher = regex.matcher(gameData.getString("globalStatsLink"));
                matcher.find();
                shortName = matcher.group(1).toLowerCase();
                this.shortName = matcher.group(1).toLowerCase();
            } else {
                this.shortName = null;
            }
        } else {
            this.iconUrl = gameData.getString("gameIcon");
            logoUrl = gameData.getString("gameLogo");
            this.name  = gameData.getString("gameName");
            this.shortName = gameData.getString("gameFriendlyName").toLowerCase();
        }

        Pattern regex = Pattern.compile("/" + appId + "/([0-9a-f]+).jpg");
        Matcher matcher = regex.matcher(logoUrl);
        if (matcher.find()) {
            this.logoHash = matcher.group(1).toLowerCase();
        }

        games.put(appId, this);
    }

    /**
     * Returns the Steam application ID of this game
     *
     * @return The Steam application ID of this game
     */
    public int getAppId() {
        return this.appId;
    }

    /**
     * Returns the URL for the icon image of this game
     *
     * @return The URL for the game icon
     */
    public String getIconUrl() {
        return this.iconUrl;
    }

    /**
     * Returns a unique identifier for this game
     *
     * This is either the numeric application ID or the unique short name
     *
     * @return The application ID or short name of the game
     */
    public Object getId() {
        if(String.valueOf(this.appId).equals(this.shortName)) {
            return this.appId;
        } else {
            return this.shortName;
        }
    }

    /**
     * Returns the full name of this game
     *
     * @return The full name of this game
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the leaderboard for this game and the given leaderboard ID
     *
     * @param id The ID of the leaderboard to return
     * @return The matching leaderboard if available
     */
    public GameLeaderboard getLeaderboard(int id)
            throws SteamCondenserException {
        return GameLeaderboard.getLeaderboard(this.shortName, id);
    }

    /**
     * Returns the leaderboard for this game and the given leaderboard name
     *
     * @param name The name of the leaderboard to return
     * @return The matching leaderboard if available
     */
    public GameLeaderboard getLeaderboard(String name)
            throws SteamCondenserException {
        return GameLeaderboard.getLeaderboard(this.shortName, name);
    }

    /**
     * Returns an array containing all of this game's leaderboards
     *
     * @return The leaderboards for this game
     */
    public Map<Integer, GameLeaderboard> getLeaderboards()
            throws SteamCondenserException {
        return GameLeaderboard.getLeaderboards(this.shortName);
    }

    /**
     * Returns the URL for the logo image of this game
     *
     * @return The URL for the game logo
     */
    public String getLogoUrl() {
        if (this.logoHash == null) {
            return null;
        } else {
            return "http://media.steampowered.com/steamcommunity/public/images/apps/" + this.appId + "/" + this.logoHash + ".jpg";
        }
    }

    /**
     * Returns the URL for the logo thumbnail image of this game
     *
     * @return  The URL for the game logo thumbnail
     */
    public String getLogoThumbnailUrl() {
        if (this.logoHash == null) {
            return null;
        } else {
            return "http://media.steampowered.com/steamcommunity/public/images/apps/" + this.appId + "/" + this.logoHash + "_thumb.jpg";
        }
    }

    /**
     * Returns the overall number of players currently playing this game
     *
     * @return The number of players playing this game
     * @throws JSONException In case of misformatted JSON data
     * @throws WebApiException on Web API errors
     */
    public int getPlayerCount() throws JSONException, WebApiException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appid", this.appId);
        String jsonString = WebApi.getJSON("ISteamUserStats", "GetNumberOfCurrentPlayers", 1, params);
        JSONObject result = new JSONObject(jsonString).getJSONObject("response");

        return result.getInt("player_count");
    }

    /**
     * Returns the short name of this game (also known as "friendly name")
     *
     * @return The short name of this game
     */
    public String getShortName() {
        return this.shortName;
    }

    /**
     * Returns the URL of this game's page in the Steam Store
     *
     * @return This game's store page
     */
    public String getStoreUrl() {
        return "http://store.steampowered.com/app/" + this.appId;
    }

    /**
     * Creates a stats object for the given user and this game
     *
     * @param steamId The custom URL or the 64bit Steam ID of the user
     * @return The stats of this game for the given user
     */
    public GameStats getUserStats(Object steamId)
            throws SteamCondenserException {
        if(!this.hasStats()) {
            return null;
        }

        return GameStats.createGameStats(steamId, this.shortName);
    }

    /**
     * Returns whether this game has statistics available
     *
     * @return <code>true</code> if this game has stats
     */
    public boolean hasStats() {
        return this.shortName != null;
    }

    /**
     * Returns whether the given version of this game is up-to-date
     *
     * @param version The version to check against the Web API
     * @return <code>true</code> if the given version is up-to-date
     * @throws JSONException if the JSON data is malformed
     * @throws SteamCondenserException if the Web API request fails
     */
    public boolean isUpToDate(int version)
            throws JSONException, SteamCondenserException {
        return SteamGame.isUpToDate(this.appId, version);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("appId", this.appId)
            .append("name", this.name)
            .append("shortName", this.shortName)
            .toString();
    }

}
