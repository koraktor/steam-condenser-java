/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011, Guto Maia
 * Copyright (c) 2011-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.After;
import org.junit.Before;

import org.w3c.dom.Document;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Guto Maia
 * @author Sebastian Staudt
 */
public abstract class GameStatsTestCase<STATS extends GameStats> {

    private String game;

    private DocumentBuilder parser;

    protected STATS stats;

    private String user;

    public GameStatsTestCase(String user, String game){
        this.game = game;
        this.user = user;
    }

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        this.parser = mock(DocumentBuilder.class);
        XMLData.setDocumentBuilder(this.parser);
        Document statsDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this.getClass().getResourceAsStream(this.user + "-" + this.game + ".xml"));
        when(parser.parse("http://steamcommunity.com/id/" + this.user + "/stats/" + this.game + "?xml=all")).thenReturn(statsDocument);

        this.stats = (STATS) GameStats.createGameStats(this.user, this.game);
    }

    @After
    public void tearDown() throws Exception{
        verify(parser).parse("http://steamcommunity.com/id/"+ this.user + "/stats/" + this.game + "?xml=all");
    }

}
