/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011-2012, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.community;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.exceptions.WebApiException;

/**
 * Provides basic functionality to represent an inventory of player in a game
 *
 * @author Sebastian Staudt
 */
public abstract class GameInventory {

    public static Map<Integer, Map<Long, GameInventory>> cache = new HashMap<Integer, Map<Long, GameInventory>>();

    private static String schemaLanguage = "en";

    private int appId;

    private Date fetchDate;

    private GameItemSchema itemSchema;

    private Map<Integer, GameItem> items;

    private long steamId64;

    private SteamId user;

    /**
     * Clears the inventory cache
     */
    public static void clearCache() {
        cache.clear();
    }

    /**
     * Returns whether the requested inventory is already cached
     *
     * @param appId The application ID of the game
     * @param steamId64 The 64bit Steam ID of the user
     * @return {@code true} if the inventory of the given user for the given
     *         game is already cached
     */
    public static boolean isCached(int appId, long steamId64) {
        return cache.containsKey(appId) &&
               cache.get(appId).containsKey(steamId64);
    }

    /**
     * Sets the language the schema should be fetched in (default is:
     * {@code "en"})
     *
     * @param language The language code for the language item descriptions
     *        should be fetched in
     */
    public static void setSchemaLanguage(String language) {
        schemaLanguage = language;
    }

    /**
     * Creates a new inventory object for the given user. This calls
     * {@code fetch()} to update the data and create the GameItem instances
     * contained in this player's inventory
     *
     * @param appId The 64bit Steam ID of the user
     * @param steamId The 64bit Steam ID or the vanity URL of the user
     * @param fetchNow Whether the data should be fetched now
     * @throws WebApiException on Web API errors
     */
    public GameInventory(int appId, Object steamId, boolean fetchNow)
            throws SteamCondenserException {
        this.appId = appId;
        if (steamId instanceof String) {
            this.steamId64 = SteamId.resolveVanityUrl((String) steamId);
        } else {
            this.steamId64 = (Long) steamId;
        }
        this.user = SteamId.create(this.steamId64, false);

        if (fetchNow) {
            this.fetch();
        }

        this.cache();
    }

    /**
     * Updates the contents of the backpack using Steam Web API
     *
     * @throws WebApiException on Web API errors
     */
    public void fetch() throws SteamCondenserException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("SteamID", this.steamId64);
            JSONObject result = WebApi.getJSONData("IEconItems_" + this.getAppId(), "GetPlayerItems", 1, params);

            this.items = new HashMap<Integer, GameItem>();
            JSONArray itemsData = result.getJSONArray("items");
            for(int i = 0; i < itemsData.length(); i ++) {
                JSONObject itemData = itemsData.getJSONObject(i);
                if(itemData != null) {
                    try {
                        GameItem item = this.getItemClass().getConstructor(this.getClass(), JSONObject.class).newInstance(this, itemData);
                        this.items.put(item.getBackpackPosition() - 1, item);
                    } catch(IllegalAccessException e) {
                    } catch(InstantiationException e) {
                    } catch(InvocationTargetException e) {
                        if (e.getCause() instanceof SteamCondenserException) {
                            throw (SteamCondenserException) e.getCause();
                        }
                    } catch(NoSuchMethodException e) {
                    }
                }
            }
        } catch(JSONException e) {
            throw new WebApiException("Could not parse JSON data.", e);
        }

        this.fetchDate = new Date();
    }

    /**
     * Returns the application ID of the game this inventory belongs to
     *
     * @return The application ID of the game this inventory belongs to
     */
    public int getAppId() {
        return this.appId;
    }

    /**
     * Returns the item at the given position in the backpack. The positions
     * range from 1 to 100 instead of the usual array indices (0 to 99).
     *
     * @param index The position of the item in the backpack
     * @return The item at the given position
     */
    public GameItem getItem(int index) {
        return this.items.get(index - 1);
    }

    /**
     * Returns the item class for the game this inventory belongs to
     *
     * @return The item class for the game this inventory belongs to
     * @see GameItem
     */
    protected abstract Class<? extends GameItem> getItemClass();

    /**
     * Returns the item schema
     *
     * The item schema is fetched first if not done already
     *
     * @return The item schema for the game this inventory belongs to
     * @throws SteamCondenserException if the item schema cannot be fetched
     */
    public GameItemSchema getItemSchema()
            throws SteamCondenserException {
        if (this.itemSchema == null) {
            this.itemSchema = GameItemSchema.create(this.appId, schemaLanguage);
        }

        return this.itemSchema;
    }

    /**
     * Returns an array of all items in this players inventory.
     *
     * @return All items in the backpack
     */
    public Map<Integer, GameItem> getItems() {
        return this.items;
    }

    /**
     * Returns the Steam ID of the player owning this inventory
     *
     * @return The Steam ID of the owner of this inventory
     */
    public SteamId getUser() {
        return this.user;
    }

    /**
     * Returns whether the items contained in this inventory have been already
     * fetched
     *
     * @return Whether the contents backpack have been fetched
     */
    public boolean isFetched() {
        return this.fetchDate != null;
    }

    /**
     * Returns the number of items in the user's backpack
     *
     * @return The number of items in the backpack
     */
    public int size() {
        return this.items.size();
    }

    /**
     * Saves this inventory in the cache
     */
    private void cache() {
        Map<Long, GameInventory> gameCache;
        if (cache.containsKey(this.appId)) {
            gameCache = cache.get(this.appId);
        } else {
            gameCache = new HashMap<Long, GameInventory>();
        }
        gameCache.put(this.steamId64, this);
    }

}
