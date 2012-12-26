/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2010-2012, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.community.tf2;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.exceptions.WebApiException;
import com.github.koraktor.steamcondenser.steam.community.GameInventory;
import com.github.koraktor.steamcondenser.steam.community.GameItem;

/**
 * Represents the inventory (aka. Backpack) of a Team Fortress 2 player
 *
 * @author Sebastian Staudt
 */
public class TF2Inventory extends GameInventory {

    public static final int APP_ID = 440;

    /**
     * Creates a new inventory instance for the player with the given Steam ID
     *
     * @param steamId64 The 64bit Steam ID of the user
     * @param fetchNow Whether the data should be fetched now
     * @see GameInventory#create
     * @throws WebApiException on Web API errors
     */
    public TF2Inventory(long steamId64, boolean fetchNow)
            throws SteamCondenserException {
        super(APP_ID, steamId64, fetchNow);
    }

    /**
     * Returns the item class for Team Fortress 2
     *
     * @return The item class for Team Fortress 2 is TF2Item
     * @see TF2Item
     */
    protected Class<? extends GameItem> getItemClass() {
        return TF2Item.class;
    }

}
