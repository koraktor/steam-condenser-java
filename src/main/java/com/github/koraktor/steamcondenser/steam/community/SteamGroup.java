/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2008-2011, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.community;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;

/**
 * The SteamGroup class represents a group in the Steam Community
 *
 * @author Sebastian Staudt
 */
public class SteamGroup {

    private static Map<Object, SteamGroup> steamGroups = new HashMap<Object, SteamGroup>();

    private String customUrl;

    private long fetchTime;

    private long groupId64;

    private int memberCount;

    private ArrayList<SteamId> members;

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
        if(SteamGroup.isCached(id) && !bypassCache) {
            SteamGroup group = SteamGroup.steamGroups.get(id);
            if(fetch && !group.isFetched()) {
                group.fetchMembers();
            }
            return group;
        } else {
            return new SteamGroup(id, fetch);
        }
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
    private SteamGroup(Object id, boolean fetch)
            throws SteamCondenserException {
        if(id instanceof String) {
            this.customUrl = (String) id;
        } else {
            this.groupId64 = (Long) id;
        }

        if(fetch) {
            this.fetchMembers();
        }

        this.cache();
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
        int page = 0;
        int totalPages;
        this.members = new ArrayList<SteamId>();

        try {
            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            do {
                totalPages = fetchPage(++page);
            } while(page < totalPages);
        } catch(Exception e) {
            throw new SteamCondenserException("XML data could not be parsed.", e);
        }
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
     * If the members have not already been fetched the first page is
     * fetched and memberCount is set and returned. Otherwise memberCount
     * has already been set and is returned.
     *
     * @return The number of this group's members
     * @throws SteamCondenserException if an error occurs while parsing the
     *         data
     */
    public int getMemberCount() throws SteamCondenserException {
        try {
            if(this.members == null) {
                members = new ArrayList<SteamId>();
                fetchPage(1);
            }
            return memberCount;
        } catch(Exception e) {
            throw new SteamCondenserException(e.getMessage(), e);
        }
    }

    /**
     * Fetches a specific page of the member listing of this group
     *
     * @param page desired page to be fetched
     * @return The total number of pages of this group's member listing
     * @throws SteamCondenserException if error occurs while parsing the
     *         data
     */
    private int fetchPage(int page) throws SteamCondenserException {
        int totalPages;
        String url;

        try {
            DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            url = this.getBaseUrl() + "/memberslistxml?p=" + page;
            Element memberData = parser.parse(url).getDocumentElement();

            if (page == 1) {
                if (members.size() == 0) {
                    memberCount = Integer.parseInt(memberData.getElementsByTagName("memberCount").item(0).getTextContent());
                    storeMembers(memberData);
                }
            } else {
                storeMembers(memberData);
            }
            totalPages = Integer.parseInt(memberData.getElementsByTagName("totalPages").item(0).getTextContent());
        } catch(Exception e) {
            throw new SteamCondenserException("XML data could not be parsed.", e);
        }
        if (members.size() == memberCount) {
            this.fetchTime = new Date().getTime();
        }

        return totalPages;
    }

    /**
     * Stores member information in internal ArrayList storage.
     *
     * @param memberData member data parsed from XML
     * @throws SteamCondenserException if error occurs while creating a SteamId
     */
    private void storeMembers(Element memberData) throws SteamCondenserException {
        NodeList membersList = ((Element) memberData.getElementsByTagName("members").item(0)).getElementsByTagName("steamID64");
        for(int i = 0; i < membersList.getLength(); i++) {
            Element member = (Element) membersList.item(i);
            this.members.add(SteamId.create(Long.parseLong(member.getTextContent()), false));
        }
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
        if(this.members == null) {
            this.fetchMembers();
        } else if (members.size() != memberCount) {
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
