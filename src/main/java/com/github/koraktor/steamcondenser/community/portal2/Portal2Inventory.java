/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community.portal2;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.exceptions.WebApiException;
import com.github.koraktor.steamcondenser.community.GameInventory;
import com.github.koraktor.steamcondenser.community.GameItem;

/**
 * Represents the inventory (aka. Robot Enrichment) of a Portal 2 player
 *
 * @author Sebastian Staudt
 */
public class Portal2Inventory extends GameInventory {

    public static final int APP_ID = 620;

    /**
     * This checks the cache for an existing inventory. If it exists it is
     * returned. Otherwise a new inventory is created.
     *
     * @param vanityUrl The vanity URL of the user
     * @return The Portal 2 inventory for the given user
     * @throws SteamCondenserException if creating the inventory fails
     */
    public static Portal2Inventory create(String vanityUrl)
            throws SteamCondenserException {
        return (Portal2Inventory) create(APP_ID, vanityUrl, true, false);
    }

    /**
     * This checks the cache for an existing inventory. If it exists it is
     * returned. Otherwise a new inventory is created.
     *
     * @param steamId64 The 64bit Steam ID of the user
     * @return The Portal 2 inventory for the given user
     * @throws SteamCondenserException if creating the inventory fails
     */
    public static Portal2Inventory create(long steamId64)
            throws SteamCondenserException {
        return (Portal2Inventory) create(APP_ID, steamId64, true, false);
    }

    /**
     * This checks the cache for an existing inventory. If it exists it is
     * returned. Otherwise a new inventory is created.
     *
     * @param vanityUrl The vanity URL of the user
     * @param fetchNow Whether the data should be fetched now
     * @return The Portal 2 inventory for the given user
     * @throws SteamCondenserException if creating the inventory fails
     */
    public static Portal2Inventory create(String vanityUrl, boolean fetchNow)
            throws SteamCondenserException {
        return (Portal2Inventory) create(APP_ID, vanityUrl, fetchNow, false);
    }

    /**
     * This checks the cache for an existing inventory. If it exists it is
     * returned. Otherwise a new inventory is created.
     *
     * @param steamId64 The 64bit Steam ID of the user
     * @param fetchNow Whether the data should be fetched now
     * @return The Portal 2 inventory for the given user
     * @throws SteamCondenserException if creating the inventory fails
     */
    public static Portal2Inventory create(long steamId64, boolean fetchNow)
            throws SteamCondenserException {
        return (Portal2Inventory) create(APP_ID, steamId64, fetchNow, false);
    }

    /**
     * This checks the cache for an existing inventory. If it exists it is
     * returned. Otherwise a new inventory is created.
     *
     * @param vanityUrl The vanity URL of the user
     * @param fetchNow Whether the data should be fetched now
     * @param bypassCache Whether the cache should be bypassed
     * @return The Portal 2 inventory for the given user
     * @throws SteamCondenserException if creating the inventory fails
     */
    public static Portal2Inventory create(String vanityUrl, boolean fetchNow, boolean bypassCache)
            throws SteamCondenserException {
        return (Portal2Inventory) create(APP_ID, vanityUrl, fetchNow, bypassCache);
    }

    /**
     * This checks the cache for an existing inventory. If it exists it is
     * returned. Otherwise a new inventory is created.
     *
     * @param steamId64 The 64bit Steam ID of the user
     * @param fetchNow Whether the data should be fetched now
     * @param bypassCache Whether the cache should be bypassed
     * @return The Portal 2 inventory for the given user
     * @throws SteamCondenserException if creating the inventory fails
     */
    public static Portal2Inventory create(long steamId64, boolean fetchNow, boolean bypassCache)
            throws SteamCondenserException {
        return (Portal2Inventory) create(APP_ID, steamId64, fetchNow, bypassCache);
    }

    /**
     * Creates a new inventory instance for the player with the given Steam ID
     *
     * @param steamId64 The 64bit Steam ID of the user
     * @param fetchNow Whether the data should be fetched now
     * @see GameInventory#create
     * @throws WebApiException on Web API errors
     */
    public Portal2Inventory(long steamId64, boolean fetchNow)
            throws SteamCondenserException {
        super(APP_ID, steamId64, fetchNow);
    }

    /**
     * Returns the item class for Portal 2
     *
     * @return The item class for Portal 2 is Portal2Item
     * @see Portal2Item
     */
    protected Class<? extends GameItem> getItemClass() {
        return Portal2Item.class;
    }

}
