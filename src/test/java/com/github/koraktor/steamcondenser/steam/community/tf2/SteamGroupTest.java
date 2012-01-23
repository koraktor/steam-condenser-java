package com.github.koraktor.steamcondenser.steam.community.tf2;

import com.github.koraktor.steamcondenser.steam.community.SteamGroup;
import com.github.koraktor.steamcondenser.steam.community.SteamId;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class SteamGroupTest {

    @Test
    public void cachedByGroupId() throws Exception {
        long id = 103582791429521412L;
        assertFalse(SteamGroup.isCached(id));

        SteamGroup group = SteamGroup.create(id, false);

        assertTrue(SteamGroup.isCached(id));
    }

    @Test
    public void cachedByCustomUrl() throws Exception {
        String id = "TF2";
        assertFalse(SteamGroup.isCached(id));

        SteamGroup group = SteamGroup.create(id, false);

        assertTrue(SteamGroup.isCached(id));
    }

    @Test
    public void fetchMembers() throws Exception {
        SteamGroup group = SteamGroup.create("ATXL4D");
        ArrayList<SteamId> members = group.getMembers();

        assertEquals(5, group.getMemberCount());
        assertEquals(76561197993236014L, members.get(0).getSteamId64());
        assertEquals(76561197976856464L, members.get(members.size() - 1).getSteamId64());
    }

    @Test
    public void baseURLFoundByGroupID() {

    }

}
