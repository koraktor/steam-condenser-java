/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2012, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.community.dota2;

import org.json.JSONObject;

import com.github.koraktor.steamcondenser.exceptions.WebApiException;
import com.github.koraktor.steamcondenser.steam.community.GameItem;

/**
 * Represents a DotA 2 item
 *
 * @author Sebastian Staudt
 */
public class Dota2Item extends GameItem {

    private boolean equipped;

    /**
     * Creates a new instance of a Dota2Item with the given data
     *
     * @param inventory The inventory this item is contained in
     * @param itemData The data specifying this item
     * @throws WebApiException on Web API errors
     */
    public Dota2Item(Dota2Inventory inventory, JSONObject itemData)
            throws WebApiException {
        super(inventory, itemData);

        this.equipped = !itemData.isNull("equipped");
    }

    /**
     * Returns whether this item is equipped by this player at all
     *
     * @return Whether this item is equipped by this player at all
     */
    public boolean isEquipped() {
        return this.equipped;
    }

}
