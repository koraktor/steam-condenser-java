/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2012-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.exceptions.WebApiException;

/**
 * Provides item definitions and related data that specify the items of a game
 *
 * @author Sebastian Staudt
 */
public class GameItemSchema {

    private static Map<Integer, Map<String, GameItemSchema>> cache =
        new HashMap<Integer, Map<String, GameItemSchema>>();

    private int appId;
    private Map<Object, JSONObject> attributes;
    private Map<Integer, JSONObject> effects;
    private Date fetchDate;
    private Map<String, Object> itemLevels;
    private Map<String, Integer> itemNames;
    private Map<String, JSONObject> itemSets;
    private Map<Integer, JSONObject> items;
    private String language;
    private Map<Integer, String> origins;
    private Map<Integer, String> qualities;

    /**
     * Clears the item schema cache
     */
    public static void clearCache() {
        cache.clear();
    }

    /**
     * Creates a new item schema for the game with the given application ID and
     * with descriptions in the given language
     *
     * @param appId The application ID of the game
     * @param language The language of description strings
     * @return The item schema for the given game and language
     */
    public static GameItemSchema create(int appId, String language)
            throws SteamCondenserException {
        return create(appId, language, true, false);
    }

    /**
     * Creates a new item schema for the game with the given application ID and
     * with descriptions in the given language
     *
     * @param appId The application ID of the game
     * @param language The language of description strings
     * @param fetch if {@code true} the schemas's data is fetched after creation
     * @param bypassCache if {@code true} the schemas's data is fetched again
     *        even if it has been cached already
     * @return The item schema for the given game and language
     */
    public static GameItemSchema create(int appId, String language, boolean fetch, boolean bypassCache)
            throws SteamCondenserException {
        if (GameItemSchema.isCached(appId, language) && !bypassCache) {
            GameItemSchema itemSchema = cache.get(appId).get(language);
            if(fetch && !itemSchema.isFetched()) {
                itemSchema.fetch();
            }
            return itemSchema;
        } else {
            return new GameItemSchema(appId, language, fetch);
        }
    }

    /**
     * Returns whether the item schema for the given application ID and
     * language is already cached
     *
     * @param appId The application ID of the game
     * @param language The language of the item schema
     * @return {@code true} if the object with the given ID is already cached
     */
    public static boolean isCached(int appId, String language) {
        return cache.containsKey(appId) &&
               cache.get(appId).containsKey(language);
    }

    /**
     * Creates a new item schema for the game with the given application ID and
     * with descriptions in the given language
     *
     * @param appId The application ID of the game
     * @param language The language of description strings
     * @param fetch if {@code true} the schemas's data is fetched after creation
     */
    protected GameItemSchema(int appId, String language, boolean fetch)
            throws WebApiException {
        this.appId    = appId;
        this.language = language;

        if (fetch) {
            this.fetch();
        }
    }

    /**
     * Updates the item definitions of this schema using the Steam Web API
     *
     * @throws WebApiException if the item schema cannot be fetched
     */
    public void fetch() throws WebApiException {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("language", this.language);
            JSONObject data = WebApi.getJSONData("IEconItems_" + this.appId, "GetSchema", 1, params);

            this.attributes = new HashMap<Object, JSONObject>();
            JSONArray attributesData = data.getJSONArray("attributes");
            for (int i = 0; i < attributesData.length(); i++) {
                JSONObject attribute = attributesData.getJSONObject(i);
                this.attributes.put(attribute.getInt("defindex"), attribute);
                this.attributes.put(attribute.getString("name"), attribute);
            }

            this.effects = new HashMap<Integer, JSONObject>();
            JSONArray effectsData = data.getJSONArray("attribute_controlled_attached_particles");
            for (int i = 0; i < effectsData.length(); i++) {
                JSONObject effect = effectsData.getJSONObject(i);
                this.effects.put(effect.getInt("id"), effect);
            }

            this.items = new HashMap<Integer, JSONObject>();
            this.itemNames = new HashMap<String, Integer>();
            JSONArray itemsData = data.getJSONArray("items");
            for (int i = 0; i < itemsData.length(); i++) {
                JSONObject item = itemsData.getJSONObject(i);
                this.items.put(item.getInt("defindex"), item);
                this.itemNames.put(item.getString("name"), item.getInt("defindex"));
            }

            if (data.has("levels")) {
                this.itemLevels = new HashMap<String, Object>();
                JSONArray itemsLevelsData = data.getJSONArray("item_levels");
                for (int i = 0; i < itemsLevelsData.length(); i++) {
                    JSONObject itemLevelType = itemsLevelsData.getJSONObject(i);
                    HashMap<Integer, String> itemLevels = new HashMap<Integer, String>();
                    this.itemLevels.put(itemLevelType.getString("name"), itemLevels);
                    for (int j = 0; j < itemLevelType.getJSONArray("levels").length(); j ++) {
                        JSONObject itemLevel = itemLevelType.getJSONArray("levels").getJSONObject(j);
                        itemLevels.put(itemLevel.getInt("level"), itemLevel.getString("name"));
                    }
                }
            }

            this.itemSets = new HashMap<String, JSONObject>();
            JSONArray itemSetsData = data.getJSONArray("item_sets");
            for (int i = 0; i < itemSetsData.length(); i++) {
                JSONObject itemSet = itemSetsData.getJSONObject(i);
                this.itemSets.put(itemSet.getString("item_set"), itemSet);
            }

            this.origins = new HashMap<Integer, String>();
            JSONArray originsData = data.getJSONArray("originNames");
            for (int i = 0; i < originsData.length(); i++) {
                JSONObject origin = originsData.getJSONObject(i);
                this.origins.put(origin.getInt("origin"), origin.getString("name"));
            }

            this.qualities = new HashMap<Integer, String>();
            JSONObject qualitiesData = data.getJSONObject("qualities");
            Iterator qualityKeys = qualitiesData.keys();
            int index = -1;
            while (qualityKeys.hasNext()) {
                String key = (String) qualityKeys.next();
                index ++;
                String qualityName = data.getJSONObject("qualityNames").optString(key, WordUtils.capitalize(key));
                this.qualities.put(index, qualityName);
            }
        } catch (JSONException e) {
            throw new WebApiException("Could not parse JSON data.", e);
        }

        this.cache();

        this.fetchDate = new Date();
    }

    /**
     * Returns whether the data for this item schema has already been fetched
     *
     * @return {@code true} if this item schema's data is available
     */
    public boolean isFetched() {
        return this.fetchDate != null;
    }

    /**
     * Returns a short, human-readable string representation of this item
     * schema
     *
     * @return A string representation of this item schema
     */
    public String toString() {
        Object fetchDate = this.isFetched() ? "not fetched" : this.fetchDate;

        return String.format("%s[%d (%s) - %s]", this.getClass().getName(),
                this.appId, this.language, fetchDate);
    }

    /**
     * Returns the application ID of the game this item schema belongs to
     *
     * @return The application ID of the game
     */
    public int getAppId() {
        return this.appId;
    }

    /**
     * The attributes defined for this game's items
     *
     * @return This item schema's attributes
     */
    public Map<Object, JSONObject> getAttributes() {
        return this.attributes;
    }

    /**
     * The effects defined for this game's items
     *
     * @return This item schema's effects
     */
    public Map<Integer, JSONObject> getEffects() {
        return this.effects;
    }

    /**
     * The levels defined for this game's items
     *
     * @return This item schema's item levels
     */
    public Map<String, Object> getItemLevels() {
        return this.itemLevels;
    }

    /**
     * A mapping from the item name to the item's defindex
     *
     * @return The item name mapping
     */
    public Map<String, Integer> getItemNames() {
        return this.itemNames;
    }

    /**
     * The item sets defined for this game's items
     *
     * @return This item schema's item sets
     */
    public Map<String, JSONObject> getItemSets() {
        return this.itemSets;
    }

    /**
     * The items defined for this game
     *
     * @return The items in this schema
     */
    public Map<Integer, JSONObject> getItems() {
        return this.items;
    }

    /**
     * The language of this item schema
     *
     * @return The language of this item schema
     */
    public String getLanguage() {
        return this.language;
    }

    /**
     * The item origins defined for this game's items
     *
     * @return This item schema's origins
     */
    public Map<Integer, String> getOrigins() {
        return this.origins;
    }

    /**
     * The item qualities defined for this game's items
     *
     * @return This item schema's qualities
     */
    public Map<Integer, String> getQualities() {
        return this.qualities;
    }

    /**
     * Saves this item schema in the cache
     */
    private void cache() {
        Map<String, GameItemSchema> gameCache;
        if (cache.containsKey(this.appId)) {
            gameCache = cache.get(this.appId);
        } else {
            gameCache = new HashMap<String, GameItemSchema>();
        }
        gameCache.put(this.language, this);
    }

}
