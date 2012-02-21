package com.github.koraktor.steamcondenser.steam.community.tf2;

import com.github.koraktor.steamcondenser.steam.community.SteamGroup;
import com.github.koraktor.steamcondenser.steam.community.SteamId;
import org.junit.Test;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
    public void cachedByCustomURL() throws Exception {
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
    public void baseURLFoundByGroupID() throws Exception {
        long id = 103582791429521412L;
        SteamGroup group = SteamGroup.create(id, false);
        String expectedUrl = "http://steamcommunity.com/gid/103582791429521412";
        String actualUrl = group.getBaseUrl();
        assertEquals(expectedUrl, actualUrl);
    }

    @Test
    public void baseURLFoundByCustomURL() throws Exception {
        String id = "valve";
        SteamGroup group = SteamGroup.create(id, false);
        String expectedURL = "http://steamcommunity.com/groups/valve";
        String actualURL = group.getBaseUrl();
        assertEquals(expectedURL, actualURL);
    }

    @Test
    public void getMemberCount() throws Exception {
        DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        String url = "http://steamcommunity.com/groups/controlpoint/memberslistxml?p=1";
        Element memberData = parser.parse(url).getDocumentElement();
        String id = "controlpoint";
        SteamGroup group = SteamGroup.create(id, false);
        int expectedMemberCount = Integer.parseInt(memberData.getElementsByTagName("memberCount").item(0).getTextContent());
        int actualMemberCount = group.getMemberCount();
        assertEquals(expectedMemberCount, actualMemberCount);
    }

    @Test
    public void fetchMembersAfterMemberCount() throws Exception {
        String id = "controlpoint";
        SteamGroup group = SteamGroup.create(id, false);
        group.getMemberCount();
        assertFalse(group.isFetched());
        group.fetchMembers();
        assertTrue(group.isFetched());
    }

    @Test
    public void getMembersAfterMemberCount() throws Exception {
        String id = "controlpoint";
        SteamGroup group = SteamGroup.create(id, false);
        group.getMemberCount();
        ArrayList<SteamId> members = group.getMembers();
        assertTrue(group.isFetched());
        assertEquals(group.getMemberCount(), members.size());
    }

}
