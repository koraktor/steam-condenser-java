/**
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the new BSD License.
 *
 * Copyright (c) 2011-2013, Sebastian Staudt
 */

package com.github.koraktor.steamcondenser.community;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringEscapeUtils;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;

/**
 * This class provides basic functionality to parse XML data
 *
 * @author Sebastian Staudt
 */
public class XMLData {

    protected static DocumentBuilder documentBuilder;

    protected static XPath xpath;

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
     * Creates a new XML data container for the given URL
     *
     * @param url The URL to load XML data from
     * @throws SteamCondenserException if an error occurs while parsing the
     *         XML data
     */
    public XMLData(String url) throws SteamCondenserException {
        try {
            this.root = getDocumentBuilder().parse(url).getDocumentElement();
        } catch (Exception e) {
            throw new SteamCondenserException("XML data could not be parsed: " + e.getMessage(), e);
        }
    }

    /**
     * Creates a new XML data container from an existing <code>Element</code>
     * object
     *
     * @param element The XML element to wrap
     */
    protected XMLData(Element element) {
        this.root = element;
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
     * Returnes the valued of the attribute of the root element with the given
     * name
     *
     * @param name The name of the attribute
     * @return The value of the attribute
     */
    public String getAttribute(String name) {
        return this.root.getAttribute(name);
    }

    /**
     * Returns the XML data for the children of the element with the given name
     * or path
     *
     * @param names The name of the elements representing the path to the
     *        target element
     * @return The children of the named element
     */
    public List<XMLData> getChildren(String... names) {
        return this.wrapNodeList(this.getElement(names).getRoot().getChildNodes());
    }

    /**
     * Returns the XML data for the element with the given name (or path)
     *
     * @param names The name of the elements representing the path to the
     *        target element
     * @return The named element
     */
    public XMLData getElement(String... names) {
        Element node = this.root;
        for(String name : names) {
            if(node.getElementsByTagName(name).getLength() == 0) {
                return null;
            }
            node = (Element) node.getElementsByTagName(name).item(0);
        }

        return new XMLData(node);
    }

    /**
     * Returns the XML data for the element specified by the given XPath
     *
     * @param path The XPath to get the XML data for
     * @return The element with the given XPath
     */
    public XMLData getXPath(String path) {
        if(xpath == null) {
            xpath = XPathFactory.newInstance().newXPath();
        }

        try {
            return new XMLData((Element) xpath.evaluate(path, this.root, XPathConstants.NODE));
        } catch(XPathExpressionException e) {
            return null;
        }
    }

    /**
     * Returns the XML data for the elements with the given name (or path)
     *
     * @param names The name of the elements representing the path to the
     *        target elements
     * @return The named elements
     */
    public List<XMLData> getElements(String... names) {
        String name = names[names.length - 1];
        String[] baseNames = new String[names.length - 1];
        System.arraycopy(names, 0, baseNames, 0, names.length - 1);

        XMLData baseElement = this.getElement(baseNames);
        if(baseElement == null) {
            return new ArrayList<XMLData>();
        }

        return this.wrapNodeList(baseElement.getRoot().getElementsByTagName(name));
    }

    /**
     * Returns the float value of the element with the given name (or path)
     *
     * @param names The name of the elements representing the path to the
     *        target element
     * @return The float value of the named element
     */
    public Float getFloat(String... names) {
        String value = this.getString(names).replaceAll(",", "").trim();

        return value.isEmpty() ? 0.0f : Float.parseFloat(value);
    }

    /**
     * Returns the integer value of the element with the given name (or path)
     *
     * @param names The name of the elements representing the path to the
     *        target element
     * @return The integer value of the named element
     */
    public Integer getInteger(String... names) {
        String value = this.getString(names).trim();

        return value.isEmpty() ? 0 : Integer.parseInt(value);
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
        String value = this.getString(names).trim();

        return value.isEmpty() ? 0L : Long.parseLong(value);
    }

    /**
     * Returns the name of the root element
     *
     * @return The name of the root element
     */
    public String getName() {
        return this.root.getTagName();
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
        Element element = this.getElement(names).getRoot();

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

    /**
     * Wraps all element objects inside a <code>NodeList</code> instance into
     * data containers and returns a list of them
     *
     * @param nodeList The node list to wrap into data containers
     * @return A list of data containers
     */
    protected List<XMLData> wrapNodeList(NodeList nodeList) {
        List<XMLData> elements = new ArrayList<XMLData>(nodeList.getLength());
        for(int i = 0; i < nodeList.getLength(); i++) {
            elements.add(new XMLData((Element) nodeList.item(i)));
        }

        return elements;
    }

}
