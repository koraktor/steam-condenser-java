/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011-2012, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.community;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import org.xml.sax.SAXException;

/**
     * This class provides basic functionality to parse XML data
 *
 * @author Sebastian Staudt
 */
public class XMLData {

    protected static DocumentBuilder documentBuilder;

    protected Element root;

    /**
     * Returns a <code>DocumentBuilder</code> instance
     * <p>
     * Creates new instance if none exists yet.
     *
     * @return The <code>DocumentBuilder</code> to parse XML data
     * @throws ParserConfigurationException if the parser settings are
     *         incorrect
     */
    protected static DocumentBuilder getDocumentBuilder()
            throws ParserConfigurationException {
        if(documentBuilder == null) {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }

        return documentBuilder;
    }

    /**
     * Creates a new XML document for the given URL
     *
     * @param url The URL to load XML data from
     */
    public XMLData(String url)
            throws IOException, ParserConfigurationException, SAXException {
        this.root = getDocumentBuilder().parse(url).getDocumentElement();
    }

    /**
     * Presets the <code>DocumentBuilder</code> instance to use for XML parsing
     *
     * @param documentBuilder The <code>DocumentBuilder</code> to use
     */
    public static void setDocumentBuilder(DocumentBuilder documentBuilder) {
        XMLData.documentBuilder = documentBuilder;
    }

    /**
     * Returns the raw object for the element with the given name
     *
     * @param name The name of the element
     * @return The named element
     */
    public Element getElement(String name) {
        return (Element) this.root.getElementsByTagName(name).item(0);
    }

    /**
     * Returns the raw object for the root element of this XML document
     *
     * @return The root element of this document
     */
    public Element getRoot() {
        return this.root;
    }

    /**
     * Returns the string value of the element with the given name
     *
     * @param name The name of the element
     * @return The string value of the named element
     */
    public String getString(String name) {
        return this.getElement(name).getTextContent();
    }

    /**
     * Returns whether the current document has an element with the given name
     *
     * @return <code>true</code> if the named element exists
     */
    public boolean hasElement(String elementName) {
        return this.root.getElementsByTagName(elementName).getLength() > 0;
    }

}
