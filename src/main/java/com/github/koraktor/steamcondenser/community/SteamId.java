/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2008-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.exceptions.WebApiException;

/**
 * The SteamId class represents a Steam Community profile (also called Steam
 * ID)
 *
 * @author Sebastian Staudt
 */
public class SteamId {

    private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH);

    private static Map<Object, SteamId> steamIds = new HashMap<Object, SteamId>();

    private String customUrl;
    private long fetchTime;
    private List<SteamId> friends;
    private HashMap<Integer, SteamGame> games;
    private List<SteamGroup> groups;
    private String headLine;
    private float hoursPlayed;
    private String imageUrl;
    private boolean limitedAccount;
    private Map<String, String> links;
    private String location;
    private Date memberSince;
    private Map<String, Float> mostPlayedGames;
    private String onlineState;
    private Map<Integer, int[]> playtimes;
    private String privacyState;
    private String realName;
    private String stateMessage;
    private String nickname;
    protected long steamId64;
    private float steamRating;
    private String summary;
    private String tradeBanState;
    private boolean vacBanned;
    private int visibilityState;

    /**
     * Clears the Steam ID cache
     */
    public static void clearCache() {
        steamIds.clear();
    }

    /**
     * Converts a 64bit numeric SteamID as used by the Steam Community to a
     * SteamID as reported by game servers
     *
     * @param communityId The SteamID string as used by the Steam Community
     * @return The converted SteamID, like <code>STEAM_0:0:12345</code>
     * @throws SteamCondenserException if the community ID is to small
     */
    public static String convertCommunityIdToSteamId(long communityId)
            throws SteamCondenserException {
        long steamId1 = communityId % 2;
        long steamId2 = communityId - 76561197960265728L;

        if(steamId2 <= 0) {
            throw new SteamCondenserException("SteamID " + communityId + " is too small.");
        }

        steamId2 = (steamId2 - steamId1) / 2;

        return "STEAM_0:" + steamId1 + ":" + steamId2;
    }

    /**
     * Converts a SteamID as reported by game servers to a 64bit numeric
     * SteamID as used by the Steam Community
     *
     * @param steamId The SteamID string as used on servers, like
     *        <code>STEAM_0:0:12345</code>
     * @return The converted 64bit numeric SteamID
     * @throws SteamCondenserException if the SteamID doesn't have the correct
     *         format
     */
    public static long convertSteamIdToCommunityId(String steamId)
            throws SteamCondenserException {
        if(steamId.equals("STEAM_ID_LAN") || steamId.equals("BOT")) {
            throw new SteamCondenserException("Cannot convert SteamID \"" + steamId + "\" to a community ID.");
        }
        if(steamId.matches("^STEAM_[0-1]:[0-1]:[0-9]+$")) {
            String[] tmpId = steamId.substring(8).split(":");
            return Long.valueOf(tmpId[0]) + Long.valueOf(tmpId[1]) * 2 + 76561197960265728L;
        } else if(steamId.matches("^\\[U:[0-1]:[0-9]+\\]+$")) {
            String[] tmpId = steamId.substring(3, steamId.length() - 1).split(":");
            return Long.valueOf(tmpId[0]) + Long.valueOf(tmpId[1]) + 76561197960265727L;
        } else {
            throw new SteamCondenserException("SteamID \"" + steamId + "\" doesn't have the correct format.");
        }
    }

    /**
     * Creates a new <code>SteamID</code> instance or gets an existing one
     * from the cache for the profile with the given ID
     *
     * @param id The 64bit SteamID of the player
     * @return The <code>SteamId</code> instance of the requested profile
     * @throws SteamCondenserException if the Steam ID data is not available,
     *         e.g. when it is private
     */
    public static SteamId create(long id)
            throws SteamCondenserException {
        return SteamId.create((Object) id, true, false);
    }

    /**
     * Creates a new <code>SteamID</code> instance or gets an existing one
     * from the cache for the profile with the given ID
     *
     * @param id The 64bit SteamID of the player
     * @return The <code>SteamId</code> instance of the requested profile
     * @throws SteamCondenserException if the Steam ID data is not available,
     *         e.g. when it is private
     */
    public static SteamId create(String id)
            throws SteamCondenserException {
        return SteamId.create((Object) id, true, false);
    }

    /**
     * Creates a new <code>SteamID</code> instance or gets an existing one
     * from the cache for the profile with the given ID
     *
     * @param id The 64bit SteamID of the player
     * @param fetch if <code>true</code> the profile's data is loaded into the
     *        object
     * @return The <code>SteamId</code> instance of the requested profile
     * @throws SteamCondenserException if the Steam ID data is not available,
     *         e.g. when it is private
     */
    public static SteamId create(long id, boolean fetch)
            throws SteamCondenserException {
        return SteamId.create((Object) id, fetch, false);
    }

    /**
     * Creates a new <code>SteamID</code> instance or gets an existing one
     * from the cache for the profile with the given ID
     *
     * @param id The 64bit SteamID of the player
     * @param fetch if <code>true</code> the profile's data is loaded into the
     *        object
     * @return The <code>SteamId</code> instance of the requested profile
     * @throws SteamCondenserException if the Steam ID data is not available,
     *         e.g. when it is private
     */
    public static SteamId create(String id, boolean fetch)
            throws SteamCondenserException {
        return SteamId.create((Object) id, fetch, false);
    }

    /**
     * Creates a new <code>SteamID</code> instance or gets an existing one
     * from the cache for the profile with the given ID
     *
     * @param id The 64bit SteamID of the player
     * @param fetch if <code>true</code> the profile's data is loaded into the
     *        object
     * @param bypassCache If <code>true</code> an already cached instance for
     *        this Steam ID will be ignored and a new one will be created
     * @return The <code>SteamId</code> instance of the requested profile
     * @throws SteamCondenserException if the Steam ID data is not available,
     *         e.g. when it is private
     */
    public static SteamId create(long id, boolean fetch, boolean bypassCache)
            throws SteamCondenserException {
        return SteamId.create((Object) id, fetch, bypassCache);
    }

    /**
     * Creates a new <code>SteamID</code> instance or gets an existing one
     * from the cache for the profile with the given ID
     *
     * @param id The custom URL of the Steam ID specified by player
     * @param fetch if <code>true</code> the profile's data is loaded into the
     *        object
     * @param bypassCache If <code>true</code> an already cached instance for
     *        this Steam ID will be ignored and a new one will be created
     * @return The <code>SteamId</code> instance of the requested profile
     * @throws SteamCondenserException if the Steam ID data is not available,
     *         e.g. when it is private
     */
    public static SteamId create(String id, boolean fetch, boolean bypassCache)
            throws SteamCondenserException {
        return SteamId.create((Object) id, fetch, bypassCache);
    }

    /**
     * Creates a new <code>SteamID</code> instance or gets an existing one
     * from the cache for the profile with the given ID
     *
     * @param id The custom URL of the Steam ID specified by player or the 64bit
     *        SteamID
     * @param fetch if <code>true</code> the profile's data is loaded into the
     *        object
     * @param bypassCache If <code>true</code> an already cached instance for
     *        this Steam ID will be ignored and a new one will be created
     * @return The <code>SteamId</code> instance of the requested profile
     * @throws SteamCondenserException if the Steam ID data is not available,
     *         e.g. when it is private
     */
    private static SteamId create(Object id, boolean fetch, boolean bypassCache)
            throws SteamCondenserException {
        if(SteamId.isCached(id) && !bypassCache) {
            SteamId steamId = SteamId.steamIds.get(id);
            if(fetch && !steamId.isFetched()) {
                steamId.fetchData();
            }
            return steamId;
        } else {
            return new SteamId(id, fetch);
        }
    }

    /**
     * Returns whether the requested Steam ID is already cached
     *
     * @param id The custom URL of the Steam ID specified by the player or the
     *        64bit SteamID
     * @return <code>true</code> if this Steam ID is already cached
     */
    public static boolean isCached(Object id) {
        return SteamId.steamIds.containsKey(id);
    }

    /**
     * Resolves a vanity URL of a Steam Community profile to a 64bit numeric
     * SteamID
     *
     * @param vanityUrl The vanity URL of a Steam Community profile
     * @return The 64bit SteamID for the given vanity URL
     * @throws WebApiException if the request to Steam's Web API fails
     */
    public static Long resolveVanityUrl(String vanityUrl)
            throws WebApiException {
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("vanityurl", vanityUrl);

            String json = WebApi.getJSON("ISteamUser", "ResolveVanityURL", 1, params);
            JSONObject result = new JSONObject(json).getJSONObject("response");

            if (result.getInt("success") != 1) {
                return null;
            }

            // org.json.JSONObject#getLong() seems to be broken
            return Long.parseLong(result.getString("steamid"));
        } catch (JSONException e) {
            throw new WebApiException("Could not parse JSON data.", e);
        }
    }

    /**
     * Creates a new <code>SteamId</code> instance for the given ID
     *
     * @param id The custom URL of the group specified by the player or the
     *        64bit SteamID
     * @param fetchData if <code>true</code> the profile's data is loaded into
     *        the object
     * @throws SteamCondenserException if the Steam ID data is not available,
     *         e.g. when it is private
     */
    private SteamId(Object id, boolean fetchData)
            throws SteamCondenserException {
        if(id instanceof String) {
            this.customUrl = (String) id;
        } else {
            this.steamId64 = (Long) id;
        }

        if(fetchData) {
            this.fetchData();
        }

        this.cache();
    }

    /**
     * Saves this <code>SteamId</code> instance in the cache
     *
     * @return <code>false</code> if this group is already cached
     */
    public boolean cache() {
        if(!SteamId.steamIds.containsKey(this.steamId64)) {
            SteamId.steamIds.put(this.steamId64, this);
            if(this.customUrl != null && !SteamId.steamIds.containsKey(this.customUrl)) {
                SteamId.steamIds.put(this.customUrl, this);
            }
            return true;
        }
        return false;
    }

    /**
     * Fetchs data from the Steam Community by querying the XML version of the
     * profile specified by the ID of this Steam ID
     *
     * @throws SteamCondenserException if the Steam ID data is not available,
     *         e.g. when it is private, or when it cannot be parsed
     */
    public void fetchData() throws SteamCondenserException {
        try {
            XMLData profile = new XMLData(this.getBaseUrl() + "?xml=1");

            if(profile.hasElement("error")) {
                throw new SteamCondenserException(profile.getString("error"));
            }

            this.nickname  = profile.getUnescapedString("steamID");
            this.steamId64 = profile.getLong("steamID64");
            this.tradeBanState = profile.getUnescapedString("tradeBanState");
            this.vacBanned = (profile.getString("vacBanned").equals("1"));

            if(profile.hasElement("privacyMessage")) {
                throw new SteamCondenserException(profile.getString("privacyMessage"));
            }

            String avatarIconUrl = profile.getString("avatarIcon");
            this.imageUrl = avatarIconUrl.substring(0, avatarIconUrl.length() - 4);
            this.limitedAccount = (profile.getString("isLimitedAccount").equals("1"));
            this.onlineState = profile.getString("onlineState");
            this.privacyState = profile.getString("privacyState");
            this.stateMessage = profile.getString("stateMessage");
            this.visibilityState = profile.getInteger("visibilityState");

            if(this.privacyState.compareTo("public") == 0) {
                this.customUrl = profile.getString("customURL");
                if(this.customUrl.length() == 0) {
                    this.customUrl = null;
                }

                this.headLine = profile.getUnescapedString("headline");
                this.hoursPlayed = profile.getFloat("hoursPlayed2Wk");
                this.location = profile.getString("location");
                String memberSince = profile.getString("memberSince");
                memberSince = memberSince.replaceAll("(\\d+)st|nd|rd|th", "$1");
                try {
                    this.memberSince = DATE_FORMAT.parse(memberSince);
                } catch (ParseException e) {
                    this.memberSince = DATE_FORMAT.parse(memberSince + ", " + Calendar.getInstance().get(Calendar.YEAR));
                }
                this.realName = profile.getUnescapedString("realname");
                this.steamRating = profile.getFloat("steamRating");
                this.summary = profile.getUnescapedString("summary");

                this.mostPlayedGames = new HashMap<String, Float>();
                for(XMLData mostPlayedGame : profile.getElements("mostPlayedGames", "mostPlayedGame")) {
                    this.mostPlayedGames.put(mostPlayedGame.getString("gameName"), mostPlayedGame.getFloat("hoursPlayed"));
                }

                List<XMLData> groupElements = profile.getElements("groups", "group");
                this.groups = new ArrayList<SteamGroup>(groupElements.size());
                for(int i = 0; i < this.groups.size(); i++) {
                    XMLData group = groupElements.get(i);
                    this.groups.add(SteamGroup.create(group.getLong("groupID64"), false));
                }

                this.links = new HashMap<String, String>();
                for(XMLData weblink : profile.getElements("weblinks", "weblink")) {
                    this.links.put(weblink.getUnescapedString("title"), weblink.getString("link"));
                }
            }
        } catch(Exception e) {
            if (e instanceof SteamCondenserException) {
                throw (SteamCondenserException) e;
            }
            throw new SteamCondenserException("XML data could not be parsed.", e);
        }

        this.fetchTime = new Date().getTime();
    }

    /**
     * Fetches the friends of this user
     * <p>
     * This creates a new <code>SteamId</code> instance for each of the friends
     * without fetching their data.
     *
     * @see #getFriends
     * @see SteamId#SteamId
     * @throws SteamCondenserException if an error occurs while parsing the
     *         data
     */
    private void fetchFriends() throws SteamCondenserException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("relationship", "friend");
            params.put("steamid", this.steamId64);

            JSONObject jsonData = new JSONObject(WebApi.getJSON("ISteamUser", "GetFriendList", 1, params));
            JSONArray friendsData = jsonData.getJSONObject("friendslist").getJSONArray("friends");
            this.friends = new ArrayList<SteamId>();
            for (int i = 0; i < friendsData.length(); i ++) {
                JSONObject friend = friendsData.getJSONObject(i);
                this.friends.add(new SteamId(friend.getLong("steamid"), false));
            }
        } catch(JSONException e) {
            throw new WebApiException("Could not parse JSON data.", e);
        }
    }

    /**
     * Fetches the games this user owns
     *
     * @see #getGames
     * @throws SteamCondenserException if an error occurs while parsing the
     *         data
     */
    private void fetchGames() throws SteamCondenserException {
        try {
            XMLData gamesData = new XMLData(this.getBaseUrl() + "/games?xml=1");

            this.games = new HashMap<Integer, SteamGame>();
            this.playtimes = new HashMap<Integer, int[]>();
            for(XMLData gameData : gamesData.getElements("games", "game")) {
                int appId = gameData.getInteger("appID");
                SteamGame game = SteamGame.create(appId, gameData);
                this.games.put(appId, game);
                float recent;
                try {
                    recent = gameData.getFloat("hoursLast2Weeks");
                } catch(NullPointerException e) {
                    recent = 0;
                }
                float total;
                try {
                    total = gameData.getFloat("hoursOnRecord");
                } catch(NullPointerException e) {
                    total = 0;
                }
                int[] playtimes = { (int) (recent * 60), (int) (total * 60) };
                this.playtimes.put(game.getAppId(), playtimes);
            }
        } catch(Exception e) {
            throw new SteamCondenserException("XML data could not be parsed.", e);
        }
    }

    /**
     * Returns the URL of the full-sized version of this user's avatar
     *
     * @return The URL of the full-sized avatar
     */
    public String getAvatarFullUrl() {
        return this.imageUrl + "_full.jpg";
    }

    /**
     * Returns the URL of the icon version of this user's avatar
     *
     * @return The URL of the icon-sized avatar
     */
    public String getAvatarIconUrl() {
        return this.imageUrl + ".jpg";
    }

    /**
     * Returns the URL of the medium-sized version of this user's avatar
     *
     * @return The URL of the medium-sized avatar
     */
    public String getAvatarMediumUrl() {
        return this.imageUrl + "_medium.jpg";
    }

    /**
     * Returns the base URL for this Steam ID
     * <p>
     * This URL is different for Steam IDs having a custom URL.
     *
     * @return The base URL for this SteamID
     */
    public String getBaseUrl() {
        if(this.customUrl == null) {
            return "http://steamcommunity.com/profiles/" + this.steamId64;
        } else {
            return "http://steamcommunity.com/id/" + this.customUrl;
        }
    }

    /**
     * Returns the custom URL of this Steam ID
     * <p>
     * The custom URL is a user specified unique string that can be used
     * instead of the 64bit SteamID as an identifier for a Steam ID.
     * <p>
     * <strong>Note:</strong> The custom URL is not necessarily the same as the
     * user's nickname.
     *
     * @return The custom URL of this Steam ID
     */
    public String getCustomUrl() {
        return this.customUrl;
    }

    /**
     * Returns the time this group has been fetched
     *
     * @return The timestamp of the last fetch time
     */
    public long getFetchTime() {
        return this.fetchTime;
    }

    /**
     * Returns the Steam Community friends of this user
     * <p>
     * If the friends haven't been fetched yet, this is done now.
     *
     * @return The friends of this user
     * @see #fetchFriends
     * @throws SteamCondenserException if an error occurs while parsing the
     *         data
     */
    public List<SteamId> getFriends()
            throws SteamCondenserException {
        if(this.friends == null) {
            this.fetchFriends();
        }
        return this.friends;
    }

    /**
     * Returns the games this user owns
     * <p>
     * The keys of the hash are the games' application IDs and the values are
     * the corresponding game instances.
     * <p>
     * If the friends haven't been fetched yet, this is done now.
     *
     * @return array The games this user owns
     * @see #fetchGames
     * @throws SteamCondenserException if an error occurs while parsing the
     *         data
     */
    public HashMap<Integer, SteamGame> getGames()
            throws SteamCondenserException {
        if(this.games == null) {
            this.fetchGames();
        }
        return this.games;
    }

    /**
     * Returns the stats for the given game for the owner of this SteamID
     *
     * @param id The full or short name or the application ID of the game stats
     *        should be fetched for
     * @return The statistics for the game with the given name
     * @throws SteamCondenserException if the user does not own this game or it
     *         does not have any stats
     */
    public GameStats getGameStats(Object id)
            throws SteamCondenserException {
        SteamGame game = this.findGame(id);

        if(!game.hasStats()) {
            throw new SteamCondenserException("\"" + game.getName() + "\" does not have stats.");
        }

        if(this.customUrl == null) {
            return GameStats.createGameStats(this.steamId64, game.getShortName());
        } else {
            return GameStats.createGameStats(this.customUrl, game.getShortName());
        }
    }

    /**
     * Returns the groups this user is a member of
     *
     * @return The groups this user is a member of
     */
    public List<SteamGroup> getGroups() {
        return this.groups;
    }

    /**
     * Returns the headline specified by the user
     *
     * @return The headline specified by the user
     */
    public String getHeadLine() {
        return this.headLine;
    }

    /**
     * Returns the number of hours that this user played a game in the last two
     * weeks
     *
     * @return The number of hours the user has played recently
     */
    public float getHoursPlayed() {
        return this.hoursPlayed;
    }

    /**
     * Returns a unique identifier for this Steam ID
     *
     * This is either the 64bit numeric SteamID or custom URL
     *
     * @return The 64bit numeric SteamID or the custom URL
     */
    public Object getId() {
        return (this.customUrl == null) ? this.steamId64 : this.customUrl;
    }

    /**
     * Returns the links that this user has added to his/her Steam ID
     * <p>
     * The keys of the hash contain the titles of the links while the values
     * contain the corresponding URLs.
     *
     * @return The links of this user
     */
    public Map<String, String> getLinks() {
        return this.links;
    }

    /**
     * Returns the location of the user
     *
     * @return The location of the user
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * Returns the date of registration for the Steam account belonging to this
     * SteamID
     *
     * @return The date of the Steam account registration
     */
    public Date getMemberSince() {
        return this.memberSince;
    }

    /**
     * Returns the games this user has played the most in the last two weeks
     *
     * The keys of the hash contain the names of the games while the values
     * contain the number of hours the corresponding game has been played by
     * the user in the last two weeks.
     *
     * @return The games this user has played the most recently
     */
    public Map<String, Float> getMostPlayedGames() {
        return this.mostPlayedGames;
    }

    /**
     * Returns the Steam nickname of the user
     *
     * @return The Steam nickname of the user
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * Returns the privacy state of this Steam ID
     *
     * @return The privacy state of this Steam ID
     */
    public String getPrivacyState() {
        return this.privacyState;
    }

    /**
     * Returns the real name of this user
     *
     * @return The real name of this user
     */
    public String getRealName() {
        return this.realName;
    }

    /**
     * Returns the time in minutes this user has played this game in the last
     * two weeks
     *
     * @param id The full or short name or the application ID of the game
     * @return The number of minutes this user played the given game in the
     *         last two weeks
     * @throws SteamCondenserException if an error occurs while parsing the
     *         data
     */
    public int getRecentPlaytime(Object id)
            throws SteamCondenserException {
        SteamGame game = this.findGame(id);

        return this.playtimes.get(game.getAppId())[0];
    }

    /**
     * Returns the message corresponding to this user's online state
     *
     * @return The message corresponding to this user's online state
     * @see #isInGame
     * @see #isOnline
     */
    public String getStateMessage() {
        return this.stateMessage;
    }

    /**
     * Returns this user's 64bit SteamID
     *
     * @return This user's 64bit SteamID
     */
    public long getSteamId64() {
        return this.steamId64;
    }

    /**
     * Returns the Steam rating calculated over the last two weeks' activity
     *
     * @return The Steam rating of this user
     */
    public float getSteamRating() {
        return this.steamRating;
    }

    /**
     * Returns the summary this user has provided
     *
     * @return This user's summary
     */
    public String getSummary() {
        return this.summary;
    }

    /**
     * Returns the total time in minutes this user has played this game
     *
     * @param id The full or short name or the application ID of the game
     * @return The total number of minutes this user played the given game
     * @throws SteamCondenserException if an error occurs while parsing the
     *         data
     */
    public int getTotalPlaytime(Object id)
            throws SteamCondenserException {
        SteamGame game = this.findGame(id);

        return this.playtimes.get(game.getAppId())[1];
    }

    /**
     * Returns the user's trade ban state
     *
     * @return the user's trade ban state
     */
    public String getTradeBanState() {
        return this.tradeBanState;
    }

    /**
     * Returns the visibility state of this Steam ID
     *
     * @return This Steam ID's visibility State
     */
    public int getVisibilityState() {
        return this.visibilityState;
    }

    /**
     * Tries to find a game instance with the given application ID or full name
     * or short name
     *
     * @param id The full or short name or the application ID of the game
     * @return The game found with the given ID
     * @throws SteamCondenserException if the user does not own the game or no
     *         game with the given ID exists
     */
    private SteamGame findGame(Object id)
            throws SteamCondenserException {
        SteamGame game = null;

        if(id instanceof Integer) {
            game = this.getGames().get(id);
        } else {
            for(SteamGame currentGame : this.getGames().values()) {
                if(id.equals(currentGame.getShortName()) ||
                   id.equals(currentGame.getName())) {
                    game = currentGame;
                    break;
                }
            }
        }

        if(game == null) {
            String message;
            if(id instanceof Integer) {
                message = "This SteamID does not own a game with application ID " + id + ".";
            } else {
                message = "This SteamID does not own the game \"" + id + "\".";
            }
            throw new SteamCondenserException(message);
        }

        return game;
    }

    /**
     * Returns whether the owner of this Steam ID is VAC banned
     *
     * @return <code>true</code> if the user has been banned by VAC
     */
    public boolean isBanned() {
        return this.vacBanned;
    }

    /**
     * Returns whether the data for this Steam ID has already been fetched
     *
     * @return <code>true</code> if the Steam ID's data has been
     *         fetched
     */
    public boolean isFetched() {
        return this.fetchTime != 0;
    }

    /**
     * Returns whether the owner of this Steam ID is playing a game
     *
     * @return <code>true</code> if the user is in-game
     */
    public boolean isInGame() {
        return this.onlineState != null && this.onlineState.equals("in-game");
    }

    /**
     * Returns whether the owner of this Steam ID has a limited account
     *
     * @return <code>true</code> if the user has a limited account
     */
    public boolean isLimitedAccount() {
        return this.limitedAccount;
    }

    /**
     * Returns whether the owner of this Steam ID is currently logged into
     * Steam
     *
     * @return <code>true</code> if the user is online
     */
    public boolean isOnline() {
        return this.onlineState != null &&
                (this.onlineState.equals("online") || this.onlineState.equals("in-game"));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("steamId64", this.steamId64)
            .append("vanityUrl", this.customUrl)
            .append("nickname", this.nickname)
            .append("online", this.isOnline())
            .toString();
    }
}
