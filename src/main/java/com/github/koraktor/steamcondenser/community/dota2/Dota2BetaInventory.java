/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2012-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community.dota2;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.exceptions.WebApiException;
import com.github.koraktor.steamcondenser.community.GameInventory;
import com.github.koraktor.steamcondenser.community.GameItem;

/**
 * Represents the inventory of a player of the DotA 2 beta
 *
 * @author Sebastian Staudt
 */
public class Dota2BetaInventory extends GameInventory {

    public static final int APP_ID = 205790;

    /**
     * This checks the cache for an existing inventory. If it exists it is
     * returned. Otherwise a new inventory is created.
     *
     * @param vanityUrl The vanity URL of the user
     * @return The DotA 2 Beta inventory for the given user
     * @throws SteamCondenserException if creating the inventory fails
     */
    public static Dota2BetaInventory create(String vanityUrl)
            throws SteamCondenserException {
        return (Dota2BetaInventory) create(APP_ID, vanityUrl, true, false);
    }

    /**
     * This checks the cache for an existing inventory. If it exists it is
     * returned. Otherwise a new inventory is created.
     *
     * @param steamId64 The 64bit Steam ID of the user
     * @return The DotA 2 Beta inventory for the given user
     * @throws SteamCondenserException if creating the inventory fails
     */
    public static Dota2BetaInventory create(long steamId64)
            throws SteamCondenserException {
        return (Dota2BetaInventory) create(APP_ID, steamId64, true, false);
    }

    /**
     * This checks the cache for an existing inventory. If it exists it is
     * returned. Otherwise a new inventory is created.
     *
     * @param vanityUrl The vanity URL of the user
     * @param fetchNow Whether the data should be fetched now
     * @return The DotA 2 Beta inventory for the given user
     * @throws SteamCondenserException if creating the inventory fails
     */
    public static Dota2BetaInventory create(String vanityUrl, boolean fetchNow)
            throws SteamCondenserException {
        return (Dota2BetaInventory) create(APP_ID, vanityUrl, fetchNow, false);
    }

    /**
     * This checks the cache for an existing inventory. If it exists it is
     * returned. Otherwise a new inventory is created.
     *
     * @param steamId64 The 64bit Steam ID of the user
     * @param fetchNow Whether the data should be fetched now
     * @return The DotA 2 Beta inventory for the given user
     * @throws SteamCondenserException if creating the inventory fails
     */
    public static Dota2BetaInventory create(long steamId64, boolean fetchNow)
            throws SteamCondenserException {
        return (Dota2BetaInventory) create(APP_ID, steamId64, fetchNow, false);
    }

    /**
     * This checks the cache for an existing inventory. If it exists it is
     * returned. Otherwise a new inventory is created.
     *
     * @param vanityUrl The vanity URL of the user
     * @param fetchNow Whether the data should be fetched now
     * @param bypassCache Whether the cache should be bypassed
     * @return The DotA 2 Beta inventory for the given user
     * @throws SteamCondenserException if creating the inventory fails
     */
    public static Dota2BetaInventory create(String vanityUrl, boolean fetchNow, boolean bypassCache)
            throws SteamCondenserException {
        return (Dota2BetaInventory) create(APP_ID, vanityUrl, fetchNow, bypassCache);
    }

    /**
     * This checks the cache for an existing inventory. If it exists it is
     * returned. Otherwise a new inventory is created.
     *
     * @param steamId64 The 64bit Steam ID of the user
     * @param fetchNow Whether the data should be fetched now
     * @param bypassCache Whether the cache should be bypassed
     * @return The DotA 2 Beta inventory for the given user
     * @throws SteamCondenserException if creating the inventory fails
     */
    public static Dota2BetaInventory create(long steamId64, boolean fetchNow, boolean bypassCache)
            throws SteamCondenserException {
        return (Dota2BetaInventory) create(APP_ID, steamId64, fetchNow, bypassCache);
    }

    /**
     * Creates a new inventory instance for the player with the given Steam ID
     *
     * @param steamId64 The 64bit Steam ID of the user
     * @param fetchNow Whether the data should be fetched now
     * @see GameInventory#create
     * @throws WebApiException on Web API errors
     */
    public Dota2BetaInventory(long steamId64, boolean fetchNow)
            throws SteamCondenserException {
        super(APP_ID, steamId64, fetchNow);
    }

    /**
     * Returns the item class for DotA 2
     *
     * @return The item class for DotA 2 is Dota2Item
     * @see Dota2Item
     */
    protected Class<? extends GameItem> getItemClass() {
        return Dota2Item.class;
    }

}
