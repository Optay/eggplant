package org.seattlego.eggplant.io;

import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility functions for parsing and writing XML. These are used by templates 
 * and by the XML encoder classes.
 * 
 * 
 * @author Topsy
 */
public class XmlUtils {
    
    public static void encodeDocument( Node document, StreamResult result ) {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger( XmlUtils.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        DOMSource source = new DOMSource( document );

        try {
            transformer.transform( source, result );
        } catch ( TransformerException ex ) {
            Logger.getLogger( XmlUtils.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }    
    }

    public static Element CreateElement( Document document, String tag, String content ) {
        Element element = document.createElement( tag );
        element.appendChild( document.createTextNode( content ));
        return element;
    }

    
    public static String GetTextContent( Element parent, String tag ) {
        if ( parent == null ) return "";
        
        NodeList children = parent.getElementsByTagName( tag );
        if ( children.getLength() > 0 ) {
            return parent.getElementsByTagName( tag ).item(0).getTextContent();
        }
        return "";
    }
    

    public static Element getFirstElementChild( Node parent ) {
        if (parent == null) {
            return null;
        }

        Node first = parent.getFirstChild();
        if (first == null) {
            return null;
        }

        for (Node node = first; node != null; node = node.getNextSibling()) {
            if ( node.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) node;
            }
        }
        return null;
    }
    
    
}
