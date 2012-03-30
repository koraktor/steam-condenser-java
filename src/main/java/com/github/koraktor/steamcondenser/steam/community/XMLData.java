/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011-2012, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.steam.community;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringEscapeUtils;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
     * Returns the raw object for the element with the given name (or path)
     *
     * @param names The name of the elements representing the path to the
     *        target element
     * @return The named element
     */
    public Element getElement(String... names) {
        Element node = this.root;
        for(String name : names) {
            if(node.getElementsByTagName(name).getLength() == 0) {
                return null;
            }
            node = (Element) node.getElementsByTagName(name).item(0);
        }

        return node;
    }

    public List<Element> getElements(String... names) {
        String name = names[names.length - 1];
        String[] baseNames = new String[names.length - 1];
        System.arraycopy(names, 0, baseNames, 0, names.length - 1);

        Element baseElement = this.getElement(baseNames);
        if(baseElement == null) {
            return null;
        }
        NodeList nodeList = baseElement.getElementsByTagName(name);
        List<Element> elements = new ArrayList<Element>(nodeList.getLength());
        for(int i = 0; i < nodeList.getLength(); i++) {
            elements.add((Element) nodeList.item(i));
        }

        return elements;
    }

    /**
     * Returns the float value of the element with the given name (or path)
     *
     * @param names The name of the elements representing the path to the
     *        target element
     * @return The float value of the named element
     */
    public Float getFloat(String... names) {
        return Float.parseFloat(this.getString(names));
    }

    /**
     * Returns the integer value of the element with the given name (or path)
     *
     * @param names The name of the elements representing the path to the
     *        target element
     * @return The integer value of the named element
     */
    public Integer getInteger(String... names) {
        return Integer.parseInt(this.getString(names));
    }

    /**
     * Returns the long integer value of the element with the given name (or
     * path)
     *
     * @param names The name of the elements representing the path to the
     *        target element
     * @return The long integer value of the named element
     */
    public Long getLong(String... names) {
        return Long.parseLong(this.getString(names));
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
     * Returns the string value of the element with the given name (or path)
     *
     * @param names The name of the elements representing the path to the
     *        target element
     * @return The string value of the named element
     */
    public String getString(String... names) {
        Element element = this.getElement(names);

        if(element == null) {
            return null;
        } else {
            return element.getTextContent();
        }
    }

    /**
     * Returns the string value of the element with the given name (or path)
     * with converted XML escaped characters
     *
     * @param names The name of the elements representing the path to the
     *        target element
     * @return The unescaped string value of the named element
     */
    public String getUnescapedString(String... names) {
        return StringEscapeUtils.unescapeXml(this.getString(names));
    }


    /**
     * Returns whether the current document has an element with the given name
     * (or path)
     *
     * @param names The name of the elements representing the path to the
     *        target element
     * @return <code>true</code> if the named element exists
     */
    public boolean hasElement(String... names) {
        return this.getElement(names) != null;
    }

}
