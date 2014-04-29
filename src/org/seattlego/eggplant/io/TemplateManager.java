package org.seattlego.eggplant.io;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamResult;
import org.seattlego.eggplant.Eggplant;
import org.seattlego.eggplant.interfaces.ITournament;
import org.seattlego.eggplant.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Loads, saves and applies templates. A template is a collection of tournament 
 * settings without name or date. A template is applied to an existing 
 * tournament, overwriting the settings with the template's values.
 * 
 * Current implementation is clumsy as hell with regards to XML parsing. There
 * is a lot of repeated encoding/decoding operations.
 * 
 * @author Topsy
 */
public class TemplateManager {
    
    private static final String TEMPLATE_FILE = "templates.xml";
    private static Map<String, Template> templates = null;
    
    public static void loadTemplates() {
        // Check for template file.
        File templateFile = new File( Eggplant.rootFolder, TEMPLATE_FILE );
        
        templates = new HashMap<>();
        if ( !templateFile.exists() ) {
            saveTemplates();
        }
        // Load file.
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch( ParserConfigurationException ex )
        {
            Logger.getLogger( TemplateManager.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        Document document;
        try {
            document = dBuilder.parse( templateFile );
        } catch ( SAXException | IOException ex ) {
            // Template file invalid!
            Logger.getLogger( TemplateManager.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        document.getDocumentElement().normalize();        
        
        NodeList templateNodes = document.getDocumentElement().getElementsByTagName("template");
	for (int templateIndex = 0; templateIndex < templateNodes.getLength(); templateIndex++ ) {
            Element templateElement = (Element) templateNodes.item( templateIndex );
            
            // Reverse the decoding! Regenerate the XML string.
            // TODO - this is slightly terrible, but I don't have an alternative right now.
            String tag;
            StringWriter writer = new StringWriter();
            XmlUtils.encodeDocument( templateElement, new StreamResult( writer ) );
            tag = writer.getBuffer().toString();
            
            Template template = new Template( templateElement.getAttribute("name"),
                                              templateElement.getAttribute("description"),
                                              tag );
            
            templates.put( template.getName(), template );
        }

    }
    
    public static ArrayList<Template> getTemplates() {
        if ( templates == null ) {
            loadTemplates();
        }
        
        return new ArrayList<>( templates.values() );
    }
    
    public static void removeTemplate( String name ) {
        // Refresh list before performing modifications
        loadTemplates();
        
        if ( templates.containsKey( name ) ) {
            templates.remove( name );
            saveTemplates();
        }
        
    }
    public static void addTemplate( String name, String description, ITournament tournament ) {
        // TODO - this loading doesn't work as implemented!
        // Refresh list to make sure nothing is being overwritten
        loadTemplates();
        //
        
        
        // Encode relevant tournament settings as in AGA encoder.
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger( XmlEncoderAGA.class.getName() ).log(Level.SEVERE, null, ex);
            return;
        }
        
        Document document = documentBuilder.newDocument();
        
        // template element
        Element rootElement = document.createElement("template");
        rootElement.setAttribute("name", name);
        rootElement.setAttribute("description", description);
        document.appendChild(rootElement);
        
        
        // Header
        Element header = document.createElement( "Header" );
        rootElement.appendChild( header );
        
        header.appendChild( XmlUtils.CreateElement( document, "PairingFormat", tournament.getPairingProps().getPairingSystem().name() ) );
        header.appendChild( XmlUtils.CreateElement( document, "Rules", tournament.getProps().getRuleset().name() ) );
        header.appendChild( XmlUtils.CreateElement( document, "BasicTime", Integer.toString( tournament.getProps().getBasicTime() ) ) );
        
        Element overtime = document.createElement( "OvertimeFormat" );
        header.appendChild( overtime );
        Element overtimeDetails = document.createElement( tournament.getProps().getTimeSystem().toString() );
        for ( Map.Entry<String, String> entry : tournament.getProps().getTimeSystemProperties().entrySet()) {
            overtimeDetails.setAttribute( entry.getKey(), entry.getValue() );
        }
        overtime.appendChild( overtimeDetails );
        
        Element placement = document.createElement( "TiebreakSystemList" );
        header.appendChild( placement );
        int tier = 1;
        for ( PlacementCriterion pc : tournament.getPlacementProps().getPlaCriteria() ) {
            Element crit = document.createElement( "System" );
            crit.setAttribute( "Tier", Integer.toString( tier ) );
            crit.setAttribute( "Method", pc.name() );
            placement.appendChild( crit );
            tier++;
        }
        //
        // Eggplant (non-AGA data)
        Element eggplant = document.createElement( "eggplant" );
        rootElement.appendChild( eggplant );
        
        eggplant.appendChild( XmlUtils.CreateElement( document, "numberofrounds", Integer.toString( tournament.getPairingProps().getNumberOfRounds() ) ) );
        eggplant.appendChild( XmlUtils.CreateElement( document, "mmbar", tournament.getPairingProps().getMMBar().toString() ) );
        eggplant.appendChild( XmlUtils.CreateElement( document, "mmfloor", tournament.getPairingProps().getMMFloor().toString() ) );
        eggplant.appendChild( XmlUtils.CreateElement( document, "bandspacingscheme", tournament.getPairingProps().getBandSpacingScheme().name() ) );
        
        eggplant.appendChild( XmlUtils.CreateElement( document, "lastroundforseedsystem", Integer.toString( tournament.getPairingProps().getLastRoundForSeedSystem1() ) ) );
        eggplant.appendChild( XmlUtils.CreateElement( document, "pairingscheme1", tournament.getPairingProps().getPairingScheme1().name() ) );
        eggplant.appendChild( XmlUtils.CreateElement( document, "pairingscheme2", tournament.getPairingProps().getPairingScheme2().name() ) );

        eggplant.appendChild( XmlUtils.CreateElement( document, "handicapgames", Boolean.toString( tournament.getPairingProps().getHandicapGames() ) ) );
        eggplant.appendChild( XmlUtils.CreateElement( document, "maxhandicappedrank", tournament.getPairingProps().getMaxHandicappedRank().toString() ) );
        eggplant.appendChild( XmlUtils.CreateElement( document, "handicapbasis", tournament.getPairingProps().getHandicapBasis().name() ) );
        eggplant.appendChild( XmlUtils.CreateElement( document, "handicapmodifier", Integer.toString( tournament.getPairingProps().getHandicapModifier() ) ) );
        eggplant.appendChild( XmlUtils.CreateElement( document, "handicapceiling", Integer.toString( tournament.getPairingProps().getMaxHandicap() ) ) );
        eggplant.appendChild( XmlUtils.CreateElement( document, "defaultkomi", Integer.toString( tournament.getPairingProps().getDefaultKomi() ) ) );
        eggplant.appendChild( XmlUtils.CreateElement( document, "absentnbwx2", Integer.toString( tournament.getPlacementProps().getGenNbw2ValueAbsent() ) ) );
        eggplant.appendChild( XmlUtils.CreateElement( document, "absentmmsx2", Integer.toString( tournament.getPlacementProps().getGenMms2ValueAbsent() ) ) );
        eggplant.appendChild( XmlUtils.CreateElement( document, "byenbwx2", Integer.toString( tournament.getPlacementProps().getGenNbw2ValueBye() ) ) );
        eggplant.appendChild( XmlUtils.CreateElement( document, "byemmsx2", Integer.toString( tournament.getPlacementProps().getGenMms2ValueBye() ) ) );
        //
        
        
        String tag;
        StringWriter writer = new StringWriter();
        XmlUtils.encodeDocument( document, new StreamResult( writer ) );
        tag = writer.getBuffer().toString();

        Template template = new Template( name,
                                          description,
                                          tag );
        
        templates.put( name, template );
        
        saveTemplates();
        
        
    }
    
    public static Template getTemplate( String name ) {
        return templates.get( name );
    }
    
    public static void applyTemplate( String name, ITournament tournament ) {
        
        Template template = getTemplate( name );
        
        // Parse XML of selected template.
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch( ParserConfigurationException ex )
        {
            Logger.getLogger( TemplateManager.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        Document document;
        try {
            InputStream templateMarkupStream = new ByteArrayInputStream( template.getMarkup().getBytes("UTF-8") );
            document = dBuilder.parse( templateMarkupStream );
        } catch ( SAXException | IOException ex ) {
            Logger.getLogger( TemplateManager.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        document.getDocumentElement().normalize();

        
        // Apply tournament settings.
        
        // Header
        Element header = (Element) document.getElementsByTagName( "Header" ).item( 0 );
        
        PairingSystem loadedPairingSystem = PairingSystem.valueOf( XmlUtils.GetTextContent( header, "PairingFormat") );
        tournament.getPairingProps().setPairingSystem( loadedPairingSystem );
        
        Ruleset loadedRuleset = Ruleset.valueOf( XmlUtils.GetTextContent( header, "Rules") );
        tournament.getProps().setRuleset( loadedRuleset );
        
        tournament.getProps().setBasicTime( XmlUtils.GetTextContent( header, "BasicTime") );
        Element overtime = (Element) header.getElementsByTagName("OvertimeFormat").item(0);
        Element overtimeDetails = XmlUtils.getFirstElementChild( overtime );
        
        if ( overtimeDetails != null ) {
            TimeSystem loadedTimeSystem = TimeSystem.getTimeSystemFromString( overtimeDetails.getTagName() );
            tournament.getProps().setTimeSystem( loadedTimeSystem );
            for ( int i = 0; i < overtimeDetails.getAttributes().getLength(); i++ ) {
                Node timeSystemAttribute = overtimeDetails.getAttributes().item(i);
                
                tournament.getProps().setTimeSystemProperty( timeSystemAttribute.getNodeName(), timeSystemAttribute.getNodeValue() );
            }
        }
        
        // TODO - awkward array sizing/generation here and in PlacementProps.
        Element placement = (Element) header.getElementsByTagName( "TiebreakSystemList" ).item(0);
        if ( placement != null ) {
            NodeList criteriaTags = placement.getElementsByTagName( "System" );

            for ( int i = 0; i < criteriaTags.getLength(); i++ ) {
                Element criteriaTag = (Element) criteriaTags.item( i );
                PlacementCriterion pc = PlacementCriterion.valueOf( criteriaTag.getAttribute("Method") );
                int index = Integer.parseInt( criteriaTag.getAttribute("Tier") ) - 1;
                tournament.getPlacementProps().setPlaCriterion( index, pc );
            }
            
        }
        //
        
        // Eggplant
        // Must be loaded before players/games as placement information is required in order to insert games.
        Element eggplant = (Element) document.getElementsByTagName( "eggplant" ).item( 0 );
        
        tournament.getPairingProps().setNumberOfRounds( XmlUtils.GetTextContent( eggplant, "numberofrounds" ) );
        tournament.getPairingProps().setMMBar( XmlUtils.GetTextContent( eggplant, "mmbar") );
        tournament.getPairingProps().setMMFloor( XmlUtils.GetTextContent( eggplant, "mmfloor") );
        try { tournament.getPairingProps().setBandSpacingScheme( BandSpacingScheme.valueOf( XmlUtils.GetTextContent( eggplant, "bandspacingscheme") ) ); }
        catch (IllegalArgumentException ex) { tournament.getPairingProps().setBandSpacingScheme( BandSpacingScheme.STACKED ); }
        
        tournament.getPairingProps().setLastRoundForSeedSystem1( XmlUtils.GetTextContent( eggplant, "lastroundforseedsystem") );
        PairingMethod loadedPairingScheme1 = PairingMethod.valueOf( XmlUtils.GetTextContent( eggplant, "pairingscheme1") );
        tournament.getPairingProps().setPairingScheme1( loadedPairingScheme1 );
        PairingMethod loadedPairingScheme2 = PairingMethod.valueOf( XmlUtils.GetTextContent( eggplant, "pairingscheme2") );
        tournament.getPairingProps().setPairingScheme2( loadedPairingScheme2 );
        
        tournament.getPairingProps().setHandicapGames( Boolean.parseBoolean( XmlUtils.GetTextContent( eggplant, "handicapgames") ) );
        tournament.getPairingProps().setMaxHandicappedRank( XmlUtils.GetTextContent( eggplant, "maxhandicappedrank") );
        tournament.getPairingProps().setHandicapBasis( HandicapBasis.valueOf( XmlUtils.GetTextContent( eggplant, "handicapbasis") ) );
        int loadedHandicapModifier = 1;
        try { loadedHandicapModifier = Integer.parseInt( XmlUtils.GetTextContent( eggplant, "handicapmodifier") ); }
        catch ( NumberFormatException ex ) { /* TODO, invalid value in save file.*/ }
        tournament.getPairingProps().setHandicapModifier( loadedHandicapModifier );
        tournament.getPairingProps().setMaxHandicap( XmlUtils.GetTextContent( eggplant, "handicapceiling") );
        tournament.getPairingProps().setDefaultKomi( XmlUtils.GetTextContent( eggplant, "defaultkomi") );
        
        tournament.getPlacementProps().setGenNbw2ValueAbsent( XmlUtils.GetTextContent( eggplant, "absentnbwx2") );
        tournament.getPlacementProps().setGenMms2ValueAbsent( XmlUtils.GetTextContent( eggplant, "absentmmsx2") );
        tournament.getPlacementProps().setGenNbw2ValueBye( XmlUtils.GetTextContent( eggplant, "byenbwx2") );
        tournament.getPlacementProps().setGenMms2ValueBye( XmlUtils.GetTextContent( eggplant, "byemmsx2") );
        //
    }
    
    /*
     * Write templates to file. Template information is already encoded as XML
     * in template objects.
     */
    private static void saveTemplates() {
        File templateFile = new File( Eggplant.rootFolder, TEMPLATE_FILE );
        
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger( TemplateManager.class.getName() ).log(Level.SEVERE, null, ex);
            return;
        }
        
        Document document = documentBuilder.newDocument();
        Element rootElement = document.createElement("templates");
        document.appendChild( rootElement );
        
        for ( Template template : templates.values() ) {
            try {
                InputStream templateMarkupStream = new ByteArrayInputStream( template.getMarkup().getBytes("UTF-8") );
                
                Element templateElement = documentBuilder.parse( templateMarkupStream ).getDocumentElement();
                rootElement.appendChild( document.importNode( templateElement, true ) );
                
            } catch ( SAXException | IOException ex ) {
                Logger.getLogger( TemplateManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        StreamResult result;
        try {
            FileOutputStream fos = new FileOutputStream( templateFile );
            result = new StreamResult( fos );
            
            // Debug
            //result = new StreamResult(System.out);
        } catch ( FileNotFoundException ex  ) {
            Logger.getLogger( TemplateManager.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        XmlUtils.encodeDocument( document, result );
        
        
    }
    
    
}
