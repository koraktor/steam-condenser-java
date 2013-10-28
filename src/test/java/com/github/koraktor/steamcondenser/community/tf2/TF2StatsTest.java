/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011, Guto Maia
 * Copyright (c) 2011-2012, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community.tf2;

import java.util.Date;

import org.junit.Test;

import com.github.koraktor.steamcondenser.community.GameAchievement;
import com.github.koraktor.steamcondenser.community.GameStatsTestCase;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Guto Maia
 * @author Sebastian Staudt
 */
public class TF2StatsTest extends GameStatsTestCase<TF2Stats> {

    public TF2StatsTest() {
        super("gutomaia", "tf2");
    }

    @Test
    public void getTF2Stats() throws Exception {
        assertEquals("Team Fortress 2", stats.getGame().getName());
        assertEquals("tf2", stats.getGame().getShortName());
        assertEquals(440, stats.getGame().getAppId());
        assertEquals("0", stats.getHoursPlayed());
        assertEquals("gutomaia", stats.getUser().getCustomUrl());
    }

    @Test
    public void achievements() throws Exception {
        assertEquals(328, stats.getAchievementsDone());
        GameAchievement achievement = stats.getAchievements().get(124);
        assertEquals("A Year to Remember", achievement.getName());
        assertEquals("Get 2004 lifetime kills.", achievement.getDescription());
        assertThat(achievement.getTimestamp(), is(equalTo(new Date(1315454886000L))));
        assertEquals("http://media.steampowered.com/steamcommunity/public/images/apps/440/37a4bfc04901a7057fee7eca4c1bb7c14fa1b32c.jpg", achievement.getIconClosedURL());
        assertEquals("http://media.steampowered.com/steamcommunity/public/images/apps/440/70ac5536184a497d5471cf7518ad72efbc7e9038.jpg", achievement.getIconOpenURL());
    }

    @Test
    public void classStats() throws Exception{
        assertEquals(9, stats.getClassStats().size());

        TF2Class soldier = stats.getClassStats().get(0);
        assertEquals("Soldier", soldier.getName());
        assertEquals(7, soldier.getMaxBuildingsDestroyed());
        assertEquals(5, soldier.getMaxCaptures());
        assertEquals(3981, soldier.getMaxDamage());
        assertEquals(3, soldier.getMaxDefenses());
        assertEquals(4, soldier.getMaxDominations());
        assertEquals(11, soldier.getMaxKillAssists());
        assertEquals(13, soldier.getMaxKills());
        assertEquals(2, soldier.getMaxRevenges());
        assertEquals(18, soldier.getMaxScore());
        assertEquals(344, soldier.getMaxTimeAlive());
        assertEquals(246153, soldier.getPlayTime());

        TF2Class pyro = stats.getClassStats().get(1);
        assertEquals("Pyro", pyro.getName());
        assertEquals(4, pyro.getMaxBuildingsDestroyed());
        assertEquals(3, pyro.getMaxCaptures());
        assertEquals(1905, pyro.getMaxDamage());
        assertEquals(4, pyro.getMaxDefenses());
        assertEquals(3, pyro.getMaxDominations());
        assertEquals(7, pyro.getMaxKillAssists());
        assertEquals(13, pyro.getMaxKills());
        assertEquals(2, pyro.getMaxRevenges());
        assertEquals(16, pyro.getMaxScore());
        assertEquals(611, pyro.getMaxTimeAlive());
        assertEquals(206292, pyro.getPlayTime());

        TF2Class spy = stats.getClassStats().get(2);
        assertEquals("Spy", spy.getName());
        assertEquals(9, spy.getMaxBuildingsDestroyed());
        assertEquals(2, spy.getMaxCaptures());
        assertEquals(2825, spy.getMaxDamage());
        assertEquals(5, spy.getMaxDefenses());
        assertEquals(2, spy.getMaxDominations());
        assertEquals(5, spy.getMaxKillAssists());
        assertEquals(15, spy.getMaxKills());
        assertEquals(1, spy.getMaxRevenges());
        assertEquals(31, spy.getMaxScore());
        assertEquals(438, spy.getMaxTimeAlive());
        assertEquals(202230, spy.getPlayTime());

        TF2Class engineer = stats.getClassStats().get(3);
        assertEquals("Engineer", engineer.getName());
        assertEquals(3, engineer.getMaxBuildingsDestroyed());
        assertEquals(5, engineer.getMaxCaptures());
        assertEquals(3193, engineer.getMaxDamage());
        assertEquals(4, engineer.getMaxDefenses());
        assertEquals(5, engineer.getMaxDominations());
        assertEquals(10, engineer.getMaxKillAssists());
        assertEquals(15, engineer.getMaxKills());
        assertEquals(2, engineer.getMaxRevenges());
        assertEquals(21, engineer.getMaxScore());
        assertEquals(795, engineer.getMaxTimeAlive());
        assertEquals(197030, engineer.getPlayTime());

        TF2Class sniper = stats.getClassStats().get(4);
        assertEquals("Sniper", sniper.getName());
        assertEquals(4, sniper.getMaxBuildingsDestroyed());
        assertEquals(3, sniper.getMaxCaptures());
        assertEquals(2799, sniper.getMaxDamage());
        assertEquals(4, sniper.getMaxDefenses());
        assertEquals(3, sniper.getMaxDominations());
        assertEquals(7, sniper.getMaxKillAssists());
        assertEquals(15, sniper.getMaxKills());
        assertEquals(1, sniper.getMaxRevenges());
        assertEquals(22, sniper.getMaxScore());
        assertEquals(588, sniper.getMaxTimeAlive());
        assertEquals(125114, sniper.getPlayTime());

        TF2Class heavy = stats.getClassStats().get(5);
        assertEquals("Heavy", heavy.getName());
        assertEquals(6, heavy.getMaxBuildingsDestroyed());
        assertEquals(14, heavy.getMaxCaptures());
        assertEquals(3827, heavy.getMaxDamage());
        assertEquals(5, heavy.getMaxDefenses());
        assertEquals(4, heavy.getMaxDominations());
        assertEquals(9, heavy.getMaxKillAssists());
        assertEquals(15, heavy.getMaxKills());
        assertEquals(3, heavy.getMaxRevenges());
        assertEquals(29, heavy.getMaxScore());
        assertEquals(478, heavy.getMaxTimeAlive());
        assertEquals(111031, heavy.getPlayTime());

        TF2Class demoman = stats.getClassStats().get(6);
        assertEquals("Demoman", demoman.getName());
        assertEquals(5, demoman.getMaxBuildingsDestroyed());
        assertEquals(7, demoman.getMaxCaptures());
        assertEquals(3050, demoman.getMaxDamage());
        assertEquals(5, demoman.getMaxDefenses());
        assertEquals(2, demoman.getMaxDominations());
        assertEquals(7, demoman.getMaxKillAssists());
        assertEquals(13, demoman.getMaxKills());
        assertEquals(2, demoman.getMaxRevenges());
        assertEquals(22, demoman.getMaxScore());
        assertEquals(403, demoman.getMaxTimeAlive());
        assertEquals(74825, demoman.getPlayTime());

        TF2Class scout = stats.getClassStats().get(7);
        assertEquals("Scout", scout.getName());
        assertEquals(5, scout.getMaxBuildingsDestroyed());
        assertEquals(3, scout.getMaxCaptures());
        assertEquals(1486, scout.getMaxDamage());
        assertEquals(4, scout.getMaxDefenses());
        assertEquals(2, scout.getMaxDominations());
        assertEquals(4, scout.getMaxKillAssists());
        assertEquals(8, scout.getMaxKills());
        assertEquals(1, scout.getMaxRevenges());
        assertEquals(12, scout.getMaxScore());
        assertEquals(266, scout.getMaxTimeAlive());
        assertEquals(61666, scout.getPlayTime());

        TF2Class medic = stats.getClassStats().get(8);
        assertEquals("Medic", medic.getName());
        assertEquals(1, medic.getMaxBuildingsDestroyed());
        assertEquals(5, medic.getMaxCaptures());
        assertEquals(1304, medic.getMaxDamage());
        assertEquals(4, medic.getMaxDefenses());
        assertEquals(2, medic.getMaxDominations());
        assertEquals(15, medic.getMaxKillAssists());
        assertEquals(6, medic.getMaxKills());
        assertEquals(2, medic.getMaxRevenges());
        assertEquals(19, medic.getMaxScore());
        assertEquals(410, medic.getMaxTimeAlive());
        assertEquals(42841, medic.getPlayTime());
    }

}
