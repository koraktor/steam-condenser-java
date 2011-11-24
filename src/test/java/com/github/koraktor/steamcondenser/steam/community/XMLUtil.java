package com.github.koraktor.steamcondenser.steam.community;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;

public class XMLUtil {

	public static Document loadXml(String file) throws Exception {
		try {
			DOMParser parser = new DOMParser();
			parser.parse("src/test/resources/" + file);
			return parser.getDocument();
		} catch (Exception e) {
			throw e;
		}
	}

}
