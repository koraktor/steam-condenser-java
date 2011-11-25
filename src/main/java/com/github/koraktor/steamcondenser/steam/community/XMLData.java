/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.community;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * This class provides basic functionality to parse XML data
 *
 * @author Sebastian Staudt
 */
public class XMLData {

    protected static DocumentBuilder documentBuilder;

    /**
     * Returns a <code>DocumentBuilder</code> instance
     * <p>
     * Creates new instance if none exists yet.
     *
     * @return The <code>DocumentBuilder</code> to parse XML data
     * @throws ParserConfigurationException if the parser settings are
     *         incorrect
     */
    public static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        if(documentBuilder == null) {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }

        return documentBuilder;
    }

    /**
     * Presets the <code>DocumentBuilder</code> instance to use for XML parsing
     *
     * @param documentBuilder The <code>DocumentBuilder</code> to use
     */
    public static void setDocumentBuilder(DocumentBuilder documentBuilder) {
        XMLData.documentBuilder = documentBuilder;
    }

}
