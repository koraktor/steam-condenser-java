/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.exceptions.WebApiException;

/**
 * Provides basic functionality to represent an item in a game
 *
 * @author Sebastian Staudt
 */
public class GameItem {

    private List<JSONObject> attributes;

    private int backpackPosition;

    private int count;

    private boolean craftable;

    private int defindex;

    private int id;

    private GameInventory inventory;

    private String itemClass;

    private JSONObject itemSet;

    private int level;

    private String name;

    private String origin;

    private int originalId;

    private boolean preliminary;

    private String quality;

    private boolean tradeable;

    private String type;

    /**
     * Creates a new instance of a GameItem with the given data
     *
     * @param inventory The inventory this item is contained in
     * @param itemData The data specifying this item
     * @throws WebApiException on Web API errors
     */
    public GameItem(GameInventory inventory, JSONObject itemData)
            throws SteamCondenserException {
        this.inventory = inventory;

        try {
            this.defindex         = itemData.getInt("defindex");
            this.backpackPosition = (int) itemData.getLong("inventory") & 0xffff;
            this.count            = itemData.getInt("quantity");
            this.craftable        = !itemData.optBoolean("flag_cannot_craft");
            this.id               = itemData.getInt("id");
            this.itemClass        = this.getSchemaData().getString("item_class");
            this.itemSet          = this.inventory.getItemSchema().getItemSets().get(this.getSchemaData().optString("item_set"));
            this.level            = itemData.getInt("level");
            this.name             = this.getSchemaData().getString("item_name");
            this.preliminary      = (itemData.getLong("inventory") & 0x40000000) != 0;
            this.originalId       = itemData.getInt("original_id");
            this.quality          = this.inventory.getItemSchema().getQualities().get(itemData.getInt("quality"));
            this.tradeable        = !itemData.optBoolean("flag_cannot_trade");
            this.type             = this.getSchemaData().getString("item_type_name");

            if (itemData.has("origin")) {
                this.origin = this.inventory.getItemSchema().getOrigins().get(itemData.getInt("origin"));
            }

            JSONArray attributesData = this.getSchemaData().optJSONArray("attributes");
            if (attributesData == null) {
                attributesData = new JSONArray();
            }
            if (itemData.has("attributes")) {
                JSONArray itemAttributes = itemData.getJSONArray("attributes");
                for (int i = 0; i < itemAttributes.length(); i ++) {
                    attributesData.put(itemAttributes.get(i));
                }
            }

            this.attributes = new ArrayList<JSONObject>();
            for (int i = 0; i < attributesData.length(); i ++) {
                JSONObject attributeData = attributesData.getJSONObject(i);
                Object attributeKey = attributeData.opt("defindex");
                if (attributeKey == null) {
                    attributeKey = attributeData.opt("name");
                }

                if (attributeKey != null) {
                    JSONObject schemaAttributeData = inventory.getItemSchema().getAttributes().get(attributeKey);
                    for (String key : JSONObject.getNames(schemaAttributeData)) {
                        attributeData.put(key, schemaAttributeData.get(key));
                    }
                    this.attributes.add(attributeData);
                }
            }
        } catch(JSONException e) {
            throw new WebApiException("Could not parse JSON data.", e);
        }
    }

    /**
     * Return the attributes of this item
     *
     * @return The attributes of this item
     */
    public List<JSONObject> getAttributes() {
        return this.attributes;
    }

    /**
     * Returns the position of this item in the player's inventory
     *
     * @return The position of this item in the player's inventory
     */
    public int getBackpackPosition() {
        return this.backpackPosition;
    }

    /**
     * Returns the number of items the player owns of this item
     *
     * @return The quanitity of this item
     */
    public int getCount() {
        return this.count;
    }

    /**
     * Returns the index where the item is defined in the schema
     *
     * @return The schema index of this item
     */
    public int getDefIndex() {
        return this.defindex;
    }

    /**
     * Returns the ID of this item
     *
     * @return The ID of this item
     */
    public int getId() {
        return this.id;
    }

    /**
     * Returns the inventory this items belongs to
     *
     * @return The inventory this item belongs to
     */
    public GameInventory getInventory() {
        return this.inventory;
    }

    /**
     * Returns the class of this item
     *
     * @return The class of this item
     */
    public String getItemClass() {
        return this.itemClass;
    }

    /**
     * Returns the item set this item belongs to
     *
     * @return The set this item belongs to
     */
    public JSONObject getItemSet() {
        return this.itemSet;
    }

    /**
     * Returns the level of this item
     *
     * @return The level of this item
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Returns the level of this item
     *
     * @return The level of this item
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the origin of this item
     *
     * @return The origin of this item
     */
    public String getOrigin() {
        return this.origin;
    }

    /**
     * Returns the original ID of this item
     *
     * @return The original ID of this item
     */
    public int getOriginalId() {
        return this.originalId;
    }

    /**
     * Returns the quality of this item
     *
     * @return The quality of this item
     */
    public String getQuality() {
        return this.quality;
    }

    /**
     * Returns the data for this item that's defined in the item schema
     *
     * @return The schema data for this item
     * @throws SteamCondenserException if the item schema cannot be loaded
     */
    public JSONObject getSchemaData() throws SteamCondenserException {
        return this.inventory.getItemSchema().getItems().get(this.defindex);
    }

    /**
     * Returns the type of this item
     *
     * @return The type of this item
     */
    public String getType() {
        return this.type;
    }

    /**
     * Returns whether this item is craftable
     *
     * @return {@code true} if this item is craftable
     */
    public boolean isCraftable() {
        return this.craftable;
    }

    /**
     * Returns whether this item is preliminary
     * <p>
     * Preliminary means that this item was just found or traded and has not
     * yet been added to the inventory
     *
     * @return {@code true} if this item is preliminary
     */
    public boolean isPreliminary() {
        return this.preliminary;
    }

    /**
     * Returns whether this item is tradeable
     *
     * @return {@code true} if this item is tradeable
     */
    public boolean isTradeable() {
        return this.tradeable;
    }

}
