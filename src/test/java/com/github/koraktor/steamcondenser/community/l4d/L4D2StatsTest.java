/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011, Guto Maia
 * Copyright (c) 2011-2012, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community.l4d;

import org.junit.Test;

import com.github.koraktor.steamcondenser.community.GameStatsTestCase;

import static org.junit.Assert.assertEquals;

/**
 * @author Guto Maia
 * @author Sebastian Staudt
 */
public class L4D2StatsTest extends GameStatsTestCase<L4D2Stats> {

    public L4D2StatsTest() {
        super("gutomaia", "l4d2");
    }

    @Test
    public void getL4D2Stats() throws Exception {

        assertEquals("Left 4 Dead 2", stats.getGame().getName());
        assertEquals("l4d2", stats.getGame().getShortName());
        assertEquals(550, stats.getGame().getAppId());
        assertEquals("0s", stats.getHoursPlayed());// TODO: strange behavior
        assertEquals("gutomaia", stats.getUser().getCustomUrl());
    }

    @Test
    public void achievements() throws Exception {
        assertEquals(7, stats.getAchievementsDone());
    }

    @Test
    public void getDamagePercentages() throws Exception {
        assertEquals("9.9", stats.getDamagePercentages().get("melee")
                .toString());
        assertEquals("28.8", stats.getDamagePercentages().get("pistols")
                .toString());
        assertEquals("43.8", stats.getDamagePercentages().get("rifles")
                .toString());
        assertEquals("17.0", stats.getDamagePercentages().get("shotguns")
                .toString());
    }

    @Test
    public void getLifetimeStats() {
        assertEquals("0.0", stats.getLifetimeStats().get("avgAdrenalineShared")
                .toString());
        assertEquals("0.263158",
                stats.getLifetimeStats().get("avgAdrenalineUsed").toString());
        assertEquals("0.157895",
                stats.getLifetimeStats().get("avgDefibrillatorsUsed")
                        .toString());
    }

}
