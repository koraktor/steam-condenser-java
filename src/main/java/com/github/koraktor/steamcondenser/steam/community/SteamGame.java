/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.community;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;

/**
 * This class represents a game available on Steam
 *
 * @author Sebastian Staudt
 */
public class SteamGame {

    private static Map<Integer, SteamGame> games = new HashMap<Integer, SteamGame>();

    private int appId;

    private String logoUrl;

    private String name;

    private String shortName;

    private String storeUrl;

    /**
     * Creates a new or cached instance of the game specified by the given XML
     * data
     *
     * @param gameData The XML data of the game
     * @return The game instance for the given data
     * @see SteamGame#SteamGame
     */
    public static SteamGame create(Element gameData) {
        int appId = Integer.parseInt(gameData.getElementsByTagName("appID").item(0).getTextContent());

        if(games.containsKey(appId)) {
            return games.get(appId);
        } else {
            return new SteamGame(appId, gameData);
        }
    }

    /**
     * Creates a new instance of a game with the given data and caches it
     *
     * @param appId The application ID of the game
     * @param gameData The XML data of the game
     */
    private SteamGame(int appId, Element gameData) {
        this.appId = appId;
        this.name  = gameData.getElementsByTagName("name").item(0).getTextContent();
        Node globalStatsLinkNode = gameData.getElementsByTagName("globalStatsLink").item(0);
        if(globalStatsLinkNode != null) {
            String shortName = globalStatsLinkNode.getTextContent();
            Pattern regex = Pattern.compile("http://steamcommunity.com/stats/([^?/]+)/achievements/");
            Matcher matcher = regex.matcher(shortName);
            matcher.find(0);
            shortName = matcher.group(1).toLowerCase();
            this.shortName = shortName;
        } else {
            this.shortName = null;
        }
        this.logoUrl = gameData.getElementsByTagName("logo").item(0).getTextContent();
        this.name  = gameData.getElementsByTagName("name").item(0).getTextContent();
        this.storeUrl = gameData.getElementsByTagName("storeLink").item(0).getTextContent();

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
     * @param id The name of the leaderboard to return
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
     * Returns the URL for an image of the game logo
     *
     * @return URL for game logo
     */
    public String getLogoUrl() {
        return logoUrl;
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
     * Returns the URL to the store page for this game
     *
     * @return the URL for the store page
     */
    public String getStoreUrl() {
        return storeUrl;
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
     * @return <code>true</code if this game has stats
     */
    public boolean hasStats() {
        return this.shortName != null;
    }

}
