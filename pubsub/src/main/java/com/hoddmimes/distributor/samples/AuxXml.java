package com.hoddmimes.distributor.samples;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AuxXml
{
    private static Transformer cTransformer = null;
    private static HashMap<String,Transformer> cXltCache = new HashMap<String,Transformer>();

    public static Document loadXMLFromString(String pXml) throws Exception
    {
        DocumentBuilderFactory tFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = tFactory.newDocumentBuilder();
        InputSource tInputStream = new InputSource(new StringReader(pXml));
        return builder.parse(tInputStream);
    }

    public static Document loadXMLFromFile(String pXmlFile) throws Exception
    {
        DocumentBuilderFactory tFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = tFactory.newDocumentBuilder();
        InputSource tInputStream = new InputSource(new InputStreamReader( new FileInputStream( pXmlFile )));
        return builder.parse(tInputStream);
    }


    public static List<Element> getChildrenElement( Element pElement, String pParentElementname ) {
        NodeList tNodeList = pElement.getElementsByTagName(pParentElementname);
        if ((tNodeList == null) || (tNodeList.getLength() != 1)) {
            return null;
        }
        Element tParent = (Element) tNodeList.item(0);
        tNodeList = tParent.getChildNodes();
        if (tNodeList == null) {
           return null;
        }
        List<Element> tResult = new ArrayList<>();
        for( int i = 0; i < tNodeList.getLength(); i++) {
            if (tNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                tResult.add( (Element) tNodeList.item(i));
            }
        }
        return tResult;
    }

    public static String getElementValue( Element pElement ) {
        Node tNode = pElement.getFirstChild();
        if (tNode == null) {
            return new String("");
        } else {
            return pElement.getFirstChild().getTextContent();
        }
    }

    public static boolean isAttributePresent( Element pElement, String pAttributeName ) {
        if ((pElement.getAttribute( pAttributeName) != null) && (pElement.getAttribute( pAttributeName).length() > 0)) {
            return true;
        }
        return false;
    }

    public static String getStringAttribute(Element pElement, String pAttributeName, String pDefaultValue ) {
        if ((pElement.getAttribute( pAttributeName) != null) && (pElement.getAttribute( pAttributeName).length() > 0)) {
            return  pElement.getAttribute( pAttributeName);
        } else {
            return pDefaultValue;
        }
    }

    public static long getLongAttribute(Element pElement, String pAttributeName, long pDefaultValue ) {
        if ((pElement.getAttribute( pAttributeName) != null) && (pElement.getAttribute( pAttributeName).length() > 0)) {
            return  Long.parseLong(pElement.getAttribute( pAttributeName));
        } else {
            return pDefaultValue;
        }
    }


    public static int getIntAttribute(Element pElement, String pAttributeName, int pDefaultValue ) {
        if ((pElement.getAttribute( pAttributeName) != null) && (pElement.getAttribute( pAttributeName).length() > 0)) {
            return  Integer.parseInt(pElement.getAttribute( pAttributeName));
        } else {
            return pDefaultValue;
        }
    }

    public static boolean getBooleanAttribute(Element pElement, String pAttributeName, boolean pDefaultValue ) {
        if ((pElement.getAttribute( pAttributeName) != null) && (pElement.getAttribute( pAttributeName).length() > 0)) {
            return  Boolean.parseBoolean(pElement.getAttribute( pAttributeName));
        } else {
            return pDefaultValue;
        }
    }

    public static double getDoubleAttribute(Element pElement, String pAttributeName, double pDefaultValue ) {
        if ((pElement.getAttribute( pAttributeName) != null) && (pElement.getAttribute( pAttributeName).length() > 0)) {
            return  Double.parseDouble(pElement.getAttribute( pAttributeName));
        } else {
            return pDefaultValue;
        }
    }

    private static boolean isEndNode( Node pNode ) {
        NodeList tNodeList = pNode.getChildNodes();
        for( int i = 0; i < tNodeList.getLength(); i++) {
            if (tNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return false;
            }
        }
        return true;
    }

    private static void  trimNode( Node pNode, int pLevel ) {
       // System.out.println("Node[" + pLevel + "]" + pNode.getNodeName());
        NodeList tNodeList = pNode.getChildNodes();
        for (int i = 0; i < tNodeList.getLength(); i++) {
            Node tNode = tNodeList.item(i);
            if (tNode.getNodeType() == Node.ELEMENT_NODE) {
                if (isEndNode( tNode )) {

                    if ((tNode.getFirstChild() != null) && (tNode.getFirstChild().getTextContent() != null)) {
                        String tNewTextContent = tNode.getFirstChild().getTextContent().trim();
                        tNode.getFirstChild().setTextContent(tNewTextContent);
                    }
                }
                trimNode( tNode, (i+1));
            }
        }
    }


    public static boolean isElementPresent( Element pElementRoot, String pTagName ) {
        NodeList nl = pElementRoot.getElementsByTagName( pTagName );
        if ((nl == null) || (nl.getLength() == 0)) {
            return false;
        }
        return true;
    }

    public static Element getElement( Element pElementRoot, String pTagName ) {
        if (isElementPresent( pElementRoot, pTagName)) {
            return (Element) pElementRoot.getElementsByTagName(pTagName).item(0);
        }
        return null;
    }
}
