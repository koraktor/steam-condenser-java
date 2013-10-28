/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011, Guto Maia
 * Copyright (c) 2011-2012, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community.portal2;

import org.junit.Test;

import com.github.koraktor.steamcondenser.community.GameStatsTestCase;

import static org.junit.Assert.assertEquals;

/**
 * @author Guto Maia
 * @author Sebastian Staudt
 */
public class Portal2StatsTest extends GameStatsTestCase<Portal2Stats> {

    public Portal2StatsTest() {
        super("gutomaia", "portal2");
    }

    @Test
    public void getPortal2Stats() throws Exception {
        assertEquals("Portal 2", stats.getGame().getName());
        assertEquals("portal2", stats.getGame().getShortName());
        assertEquals(620, stats.getGame().getAppId());
        assertEquals("0", stats.getHoursPlayed());
        assertEquals("gutomaia", stats.getUser().getCustomUrl());
    }

    @Test
    public void achievements() throws Exception {
        assertEquals(17, stats.getAchievementsDone());
    }

}
