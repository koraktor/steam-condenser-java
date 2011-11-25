package com.github.koraktor.steamcondenser.steam.community;

import static com.github.koraktor.steamcondenser.steam.community.XMLUtil.loadXml;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.After;
import org.junit.Before;

public abstract class StatsTestCase <STATS extends GameStats> {
	
	String user;
	String game;
	
	
	DocumentBuilder parser = mock(DocumentBuilder.class);
	DocumentBuilderFactory factory = mock(DocumentBuilderFactory.class);

	public StatsTestCase(String user, String game){
		this.user = user;
		this.game = game;
	}

	@Before
	public void setUp() throws Exception {
		mockStatic(DocumentBuilderFactory.class);
		when(DocumentBuilderFactory.newInstance()).thenReturn(factory);
		when(factory.newDocumentBuilder()).thenReturn(parser);
		when(parser.parse("http://steamcommunity.com/id/"+user+"?xml=1"))
				.thenReturn(loadXml(user+".xml"));
		when(parser.parse("http://steamcommunity.com/id/"+user+"/games?xml=1"))
				.thenReturn(loadXml(user+"-games.xml"));
		when(
				parser.parse("http://steamcommunity.com/id/"+user+"/stats/"+game+"?xml=all"))
				.thenReturn(loadXml(user+"-"+game+".xml"));
		SteamId steamId = SteamId.create(user, true, true);
		stats = (STATS) steamId.getGameStats(game);
	}
	
	STATS stats;

	@After
	public void tearDown() throws Exception{
		verify(parser).parse("http://steamcommunity.com/id/"+user+"?xml=1");
		verify(parser).parse("http://steamcommunity.com/id/"+user+"/games?xml=1");
		verify(parser).parse("http://steamcommunity.com/id/"+user+"/stats/"+game+"?xml=all");
	}

}
