/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2008-2013, Sebastian Staudt
 *               2012, Sam Kinard
 */

package com.github.koraktor.steamcondenser.community;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;

/**
 * The SteamGroup class represents a group in the Steam Community
 *
 * @author Sebastian Staudt
 */
public class SteamGroup {

    private static final String AVATAR_URL = "http://media.steampowered.com/steamcommunity/public/images/avatars/%s/%s%s.jpg";

    protected static Map<Object, SteamGroup> steamGroups = new HashMap<Object, SteamGroup>();

    private String avatarHash;

    private String customUrl;

    private long fetchTime;

    private long groupId64;

    private String headline;

    private Integer memberCount;

    private ArrayList<SteamId> members;

    private String name;

    private String summary;

    /**
     * Clears the Steam Community group cache
     */
    public static void clearCache() {
        steamGroups.clear();
    }

    /**
     * Creates a new <code>SteamGroup</code> instance or gets an existing one
     * from the cache for the group with the given ID
     *
     * @param id The 64bit Steam ID of the group
     * @return The <code>SteamGroup</code> instance of the requested group
     * @throws SteamCondenserException if an error occurs while parsing the
     *         data
     */
    public static SteamGroup create(long id) throws SteamCondenserException {
        return SteamGroup.create((Object) id, true, false);
    }

    /**
     * Creates a new <code>SteamGroup</code> instance or gets an existing one
     * from the cache for the group with the given ID
     *
     * @param id The custom URL of the group specified by the group admin
     * @return The <code>SteamGroup</code> instance of the requested group
     * @throws SteamCondenserException if an error occurs while parsing the
     *         data
     */
    public static SteamGroup create(String id) throws SteamCondenserException {
        return SteamGroup.create((Object) id, true, false);
    }

    /**
     * Creates a new <code>SteamGroup</code> instance or gets an existing one
     * from the cache for the group with the given ID
     *
     * @param id The 64bit Steam ID of the group
     * @param fetch if <code>true</code> the groups's data is loaded into the
     *        object
     * @return The <code>SteamGroup</code> instance of the requested group
     * @throws SteamCondenserException if an error occurs while parsing the
     *         data
     */
    public static SteamGroup create(long id, boolean fetch)
            throws SteamCondenserException {
        return SteamGroup.create((Object) id, fetch, false);
    }

    /**
     * Creates a new <code>SteamGroup</code> instance or gets an existing one
     * from the cache for the group with the given ID
     *
     * @param id The custom URL of the group specified by the group admin
     * @param fetch if <code>true</code> the groups's data is loaded into the
     *        object
     * @return The <code>SteamGroup</code> instance of the requested group
     * @throws SteamCondenserException if an error occurs while parsing the
     *         data
     */
    public static SteamGroup create(String id, boolean fetch)
            throws SteamCondenserException {
        return SteamGroup.create((Object) id, fetch, false);
    }

    /**
     * Creates a new <code>SteamGroup</code> instance or gets an existing one
     * from the cache for the group with the given ID
     *
     * @param id The 64bit Steam ID of the group
     * @param fetch if <code>true</code> the groups's data is loaded into the
     *        object
     * @param bypassCache If <code>true</code> an already cached instance for
     *        this group will be ignored and a new one will be created
     * @return The <code>SteamGroup</code> instance of the requested group
     * @throws SteamCondenserException if an error occurs while parsing the
     *         data
     */
    public static SteamGroup create(long id, boolean fetch, boolean bypassCache)
            throws SteamCondenserException {
        return SteamGroup.create((Object) id, fetch, bypassCache);
    }

    /**
     * Creates a new <code>SteamGroup</code> instance or gets an existing one
     * from the cache for the group with the given ID
     *
     * @param id The custom URL of the group specified by the group admin
     * @param fetch if <code>true</code> the groups's data is loaded into the
     *        object
     * @param bypassCache If <code>true</code> an already cached instance for
     *        this group will be ignored and a new one will be created
     * @return The <code>SteamGroup</code> instance of the requested group
     * @throws SteamCondenserException if an error occurs while parsing the
     *         data
     */
    public static SteamGroup create(String id, boolean fetch, boolean bypassCache)
            throws SteamCondenserException {
        return SteamGroup.create((Object) id, fetch, bypassCache);
    }

    /**
     * Creates a new <code>SteamGroup</code> instance or gets an existing one
     * from the cache for the group with the given ID
     *
     * @param id The custom URL of the group specified by the group admin or
     *        the 64bit group ID
     * @param fetch if <code>true</code> the groups's data is loaded into the
     *        object
     * @param bypassCache If <code>true</code> an already cached instance for
     *        this group will be ignored and a new one will be created
     * @return The <code>SteamGroup</code> instance of the requested group
     * @throws SteamCondenserException if an error occurs while parsing the
     *         data
     */
    private static SteamGroup create(Object id, boolean fetch, boolean bypassCache)
            throws SteamCondenserException {
        SteamGroup group;

        if(SteamGroup.isCached(id) && !bypassCache) {
            group = SteamGroup.steamGroups.get(id);
            if(fetch && !group.isFetched()) {
                group.fetchMembers();
            }
        } else {
            group = new SteamGroup(id, fetch);
            group.cache();
        }

        return group;
    }

    /**
     * Returns whether the requested group is already cached
     *
     * @param id The custom URL of the group specified by the group admin or
     *        the 64bit group ID
     * @return <code>true</code> if this group is already cached
     */
    public static boolean isCached(Object id) {
        return SteamGroup.steamGroups.containsKey(id);
    }

    /**
     * Creates a new <code>SteamGroup</code> instance for the group with the
     * given ID
     *
     * @param id The custom URL of the group specified by the group admin or
     *        the 64bit group ID
     * @param fetch if <code>true</code> the groups's data is loaded into the
     *        object
     * @throws SteamCondenserException if an error occurs while parsing the
     *         data
     */
    protected SteamGroup(Object id, boolean fetch)
            throws SteamCondenserException {
        if(id instanceof String) {
            this.customUrl = (String) id;
        } else {
            this.groupId64 = (Long) id;
        }

        this.members = new ArrayList<SteamId>();

        if(fetch) {
            this.fetchMembers();
        }
    }

    /**
     * Saves this <code>SteamGroup</code> instance in the cache
     *
     * @return <code>false</code> if this group is already cached
     */
    public boolean cache() {
        if(!SteamGroup.steamGroups.containsKey(this.groupId64)) {
            SteamGroup.steamGroups.put(this.groupId64, this);
            if(this.customUrl != null && !SteamGroup.steamGroups.containsKey(this.customUrl)) {
                SteamGroup.steamGroups.put(this.customUrl, this);
            }
            return true;
        }
        return false;
    }

    /**
     * Loads the members of this group
     * <p>
     * This might take several HTTP requests as the Steam Community splits this
     * data over several XML documents if the group has lots of members.
     *
     * @throws SteamCondenserException if an error occurs while parsing the
     *         data
     */
    public void fetchMembers() throws SteamCondenserException {
        int page;
        int totalPages;

        if(this.memberCount == null || this.members.size() != this.memberCount) {
            page = 0;
        } else {
            page = 1;
        }

        try {
            do {
                totalPages = fetchPage(++page);
            } while(page < totalPages);
        } catch(Exception e) {
            throw new SteamCondenserException("XML data could not be parsed.", e);
        }
        this.fetchTime = new Date().getTime();
    }

    /**
     * Returns the URL to this group's full avatar
     *
     * @return The URL to this group's full avatar
     */
    public String getAvatarFullUrl() {
        return String.format(AVATAR_URL, this.avatarHash.substring(0, 2), this.avatarHash, "_full");
    }

    /**
     * Returns the URL to this group's icon avatar
     *
     * @return The URL to this group's icon avatar
     */
    public String getAvatarIconUrl() {
        return String.format(AVATAR_URL, this.avatarHash.substring(0, 2), this.avatarHash, "");
    }

    /**
     * Returns the URL to this group's medium avatar
     *
     * @return The URL to this group's medium avatar
     */
    public String getAvatarMediumUrl() {
        return String.format(AVATAR_URL, this.avatarHash.substring(0, 2), this.avatarHash, "_medium");
    }

    /**
     * Returns the custom URL of this group
     * <p>
     * The custom URL is a admin specified unique string that can be used
     * instead of the 64bit SteamID as an identifier for a group.
     *
     * @return The custom URL of this group
     */
    public String getCustomUrl() {
        return this.customUrl;
    }

    /**
     * Returns this group's 64bit SteamID
     *
     * @return This group's 64bit SteamID
     */
    public long getGroupId64() {
        return this.groupId64;
    }

    /**
     * Returns the base URL for this group's page
     * <p>
     * This URL is different for groups having a custom URL.
     *
     * @return The base URL for this group
     */
    public String getBaseUrl() {
        if(this.customUrl == null) {
            return "http://steamcommunity.com/gid/" + this.groupId64;
        } else {
            return "http://steamcommunity.com/groups/" + this.customUrl;
        }
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
     * Returns this group's headline text
     *
     * @return This group's headline text
     */
    public String getHeadline() {
        return this.headline;
    }

    /**
     * Returns this group's 64bit SteamID
     *
     * @return This group's 64bit SteamID
     */
    public long getId() {
        return this.groupId64;
    }

    /**
     * Returns the number of members this group has
     * <p>
     * If the members have already been fetched the size of the member array is
     * returned. Otherwise the the first page of the member listing is fetched
     * and the member count and the first batch of members is stored.
     *
     * @return The number of this group's members
     * @throws SteamCondenserException if an error occurs while parsing the
     *         data
     */
    public int getMemberCount() throws SteamCondenserException {
        try {
            if(this.memberCount == null) {
                int totalPages = fetchPage(1);
                if(totalPages == 1) {
                    this.fetchTime = new Date().getTime();
                }
            }

            return memberCount;
        } catch(Exception e) {
            throw new SteamCondenserException(e.getMessage(), e);
        }
    }

    /**
     * Returns this group's name
     *
     * @return This group's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns this group's summary text
     *
     * @return This group's summary text
     */
    public String getSummary() {
        return this.summary;
    }

    /**
     * Fetches a specific page of the member listing of this group
     *
     * @param page The member page to fetch
     * @return The total number of pages of this group's member listing
     * @throws SteamCondenserException if an error occurs while parsing the
     *         data
     */
    private int fetchPage(int page) throws SteamCondenserException {
        int totalPages;

        try {
            XMLData memberData = new XMLData(this.getBaseUrl() + "/memberslistxml?p=" + page);

            if (page == 1) {
                XMLData groupDetails = memberData.getElement("groupDetails");

                Matcher matcher = Pattern.compile("/([0-9a-f]+)\\.jpg$").matcher(groupDetails.getString("avatarIcon"));
                matcher.find();
                this.avatarHash = matcher.group(1);
                this.customUrl  = groupDetails.getString("groupURL");
                this.groupId64  = memberData.getLong("groupID64");
                this.name       = groupDetails.getString("groupName");
                this.headline   = groupDetails.getString("headline");
                this.summary    = groupDetails.getString("summary");
            }

            this.memberCount = memberData.getInteger("memberCount");
            totalPages = memberData.getInteger("totalPages");

            for(XMLData member : memberData.getElements("members", "steamID64")) {
                this.members.add(SteamId.create(member.getLong(), false));
            }
        } catch(Exception e) {
            if (e instanceof SteamCondenserException) {
                throw (SteamCondenserException) e;
            }
            throw new SteamCondenserException("XML data could not be parsed.", e);
        }

        return totalPages;
    }

    /**
     * Returns the members of this group
     * <p>
     * If the members haven't been fetched yet, this is done now.
     *
     * @return The Steam ID's of the members of this group
     * @see #fetchMembers
     * @throws SteamCondenserException if an error occurs while parsing the
     *         data
     */
    public ArrayList<SteamId> getMembers() throws SteamCondenserException {
        if(this.members.size() != this.memberCount) {
            this.fetchMembers();
        }

        return this.members;
    }

    /**
     * Returns whether the data for this group has already been fetched
     *
     * @return <code>true</code> if the group's members have been
     *         fetched
     */
    public boolean isFetched() {
        return this.fetchTime != 0;
    }
}
