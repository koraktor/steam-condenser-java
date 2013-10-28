/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.exceptions.WebApiException;
import com.github.koraktor.steamcondenser.community.dota2.Dota2BetaInventory;
import com.github.koraktor.steamcondenser.community.dota2.Dota2Inventory;
import com.github.koraktor.steamcondenser.community.portal2.Portal2Inventory;
import com.github.koraktor.steamcondenser.community.tf2.TF2BetaInventory;
import com.github.koraktor.steamcondenser.community.tf2.TF2Inventory;

/**
 * Provides basic functionality to represent an inventory of player in a game
 *
 * @author Sebastian Staudt
 */
public class GameInventory {

    public static Map<Integer, Map<Long, GameInventory>> cache = new HashMap<Integer, Map<Long, GameInventory>>();

    private static String schemaLanguage = "en";

    private int appId;

    private Date fetchDate;

    private GameItemSchema itemSchema;

    private Map<Integer, GameItem> items;

    private List<GameItem> preliminaryItems;

    private long steamId64;

    private SteamId user;

    /**
     * Clears the inventory cache
     */
    public static void clearCache() {
        cache.clear();
    }

    /**
     * This checks the cache for an existing inventory. If it exists it is
     * returned. Otherwise a new inventory is created.
     *
     * @param appId The application ID of the game
     * @param steamId64 The 64bit Steam ID of the user
     * @return The inventory for the given user and game
     * @throws SteamCondenserException if creating the inventory fails
     */
    public static GameInventory create(int appId, long steamId64)
            throws SteamCondenserException {
        return create(appId, steamId64, true, false);
    }

    /**
     * This checks the cache for an existing inventory. If it exists it is
     * returned. Otherwise a new inventory is created.
     *
     * @param appId The application ID of the game
     * @param vanityUrl The vanity URL of the user
     * @return The inventory for the given user and game
     * @throws SteamCondenserException if creating the inventory fails
     */
    public static GameInventory create(int appId, String vanityUrl)
            throws SteamCondenserException {
        return create(appId, vanityUrl, true, false);
    }

    /**
     * This checks the cache for an existing inventory. If it exists it is
     * returned. Otherwise a new inventory is created.
     *
     * @param appId The application ID of the game
     * @param vanityUrl The vanity URL of the user
     * @param fetchNow Whether the data should be fetched now
     * @param bypassCache Whether the cache should be bypassed
     * @return The inventory for the given user and game
     * @throws SteamCondenserException if creating the inventory fails
     */
    public static GameInventory create(int appId, String vanityUrl, boolean fetchNow, boolean bypassCache)
            throws SteamCondenserException {
        long steamId64 = SteamId.resolveVanityUrl(vanityUrl);
        return create(appId, steamId64, fetchNow, bypassCache);
    }

    /**
     * This checks the cache for an existing inventory. If it exists it is
     * returned. Otherwise a new inventory is created.
     *
     * @param appId The application ID of the game
     * @param steamId64 The 64bit Steam ID of the user
     * @param fetchNow Whether the data should be fetched now
     * @param bypassCache Whether the cache should be bypassed
     * @return The inventory for the given user and game
     * @throws SteamCondenserException if creating the inventory fails
     */
    public static GameInventory create(int appId, long steamId64, boolean fetchNow, boolean bypassCache)
            throws SteamCondenserException {
        if (isCached(appId, steamId64) && !bypassCache) {
            GameInventory inventory = cache.get(appId).get(steamId64);
            if (fetchNow && !inventory.isFetched()) {
                inventory.fetch();
            }
            return inventory;
        } else {
            switch (appId) {
                case Dota2BetaInventory.APP_ID:
                    return new Dota2BetaInventory(steamId64, fetchNow);
                case Dota2Inventory.APP_ID:
                    return new Dota2Inventory(steamId64, fetchNow);
                case Portal2Inventory.APP_ID:
                    return new Portal2Inventory(steamId64, fetchNow);
                case TF2BetaInventory.APP_ID:
                    return new TF2BetaInventory(steamId64, fetchNow);
                case TF2Inventory.APP_ID:
                    return new TF2Inventory(steamId64, fetchNow);
                default:
                    return new GameInventory(appId, steamId64, fetchNow);
            }
        }
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
    protected GameInventory(int appId, Object steamId, boolean fetchNow)
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
            this.preliminaryItems = new ArrayList<GameItem>();
            JSONArray itemsData = result.getJSONArray("items");
            for(int i = 0; i < itemsData.length(); i ++) {
                JSONObject itemData = itemsData.getJSONObject(i);
                if(itemData != null) {
                    try {
                        GameItem item = this.getItemClass().getConstructor(this.getClass(), JSONObject.class).newInstance(this, itemData);
                        if (item.isPreliminary()) {
                            this.preliminaryItems.add(item);
                        } else {
                            this.items.put(item.getBackpackPosition() - 1, item);
                        }
                    } catch(IllegalAccessException e) {
                    } catch(InstantiationException e) {
                    } catch(InvocationTargetException e) {
                        if (e.getCause() instanceof SteamCondenserException) {
                            throw (SteamCondenserException) e.getCause();
                        } else {
                            throw (RuntimeException) e.getCause();
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
    protected Class<? extends GameItem> getItemClass() {
        return GameItem.class;
    }

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
     * Returns an array of all items that this player just found or traded
     *
     * @return All preliminary items of the inventory
     */
    public List<GameItem> getPreliminaryItems() {
        return this.preliminaryItems;
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
