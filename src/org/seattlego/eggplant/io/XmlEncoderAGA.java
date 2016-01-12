package org.seattlego.eggplant.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.seattlego.eggplant.interfaces.ITournament;
import org.seattlego.eggplant.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Converts tournament object to and from AGA-compliant XML according to 
 * [ http://www.usgo.org/files/pdf/AGATournamentSchema.xsd ]. This is done
 * with DOM. I considered using XStream to serialize the tournament object
 * and then postprocess the XML with XSL to match the AGA schema. However, 
 * the extra translation steps seemed to add up to more work than manually 
 * serializing.
 * 
 * This will, perhaps, be revisited. XStream serialization may be more useful
 * elsewhere for saving objects that do not need to conform to a schema.
 * 
 * @author Topsy
 */
public class XmlEncoderAGA {
    
    private static String loadReport = "";
    
    /*
     * Encode a tournament object to a target file.
     */
    public static void TournamentToFile( ITournament tournament, File destination, String version ) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger( XmlEncoderAGA.class.getName() ).log(Level.SEVERE, null, ex);
            return;
        }
        
        Document document = documentBuilder.newDocument();
        
        // TournamentReport
        Element rootElement = document.createElement("TournamentReport");
        rootElement.setAttributeNS( "http://www.w3.org/2001/XMLSchema-instance", "xsi:noNamespaceSchemaLocation", "http://www.usgo.org/tournaments/TournamentStandards/AGATournamentSchema.xsd");
        rootElement.setAttribute("eggplant-version", version);
        document.appendChild(rootElement);
        
        
        // Header
        Element header = document.createElement( "Header" );
        rootElement.appendChild( header );
        
        header.appendChild( XmlUtils.CreateElement( document, "Software", "Eggplant" ) );
        header.appendChild( XmlUtils.CreateElement( document, "StartDate", tournament.getProps().getStartDateString() ) );
        header.appendChild( XmlUtils.CreateElement( document, "EndDate", tournament.getProps().getEndDateString() ) );
        header.appendChild( XmlUtils.CreateElement( document, "TournamentName", tournament.getProps().getName() ) );
        
        Element venue = XmlUtils.CreateElement( document, "TournamentVenue", "" );
        venue.appendChild( XmlUtils.CreateElement( document, "Name", tournament.getProps().getLocation() ) );
        header.appendChild( venue );
        
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
        
        header.appendChild( XmlUtils.CreateElement( document, "Comment", tournament.getProps().getComment() ) );
        
        /*
         * HEADER TAGS
+Software
+TournamentName
+TournamentVenue
TournamentDirector
TournamentSponsor
TournamentConvenor
+Start Date
+End Date
+Pairing Format
+Rules
+Basic Time
+Overtime Format
TiebreakSystemList
TournamentStaffList
+Comment
*/

        
        
        
        // PlayerList
        Element playerList = document.createElement( "PlayerList" );
        rootElement.appendChild( playerList );
        
        for ( Player p : tournament.getRegisteredPlayers() )
        {
            playerList.appendChild( CreatePlayerElement( document, p, tournament.getPairingProps().getNumberOfRounds()  ) );
        }
        //
        
        
        // RoundList
        Element rounds = document.createElement( "RoundList" );
        rootElement.appendChild( rounds );
        
        for ( int i = 0; i < tournament.getPairingProps().getNumberOfRounds(); i++ ) {
            // <Round RoundNumber="1">
            Element round = document.createElement( "Round" );
            round.setAttribute("RoundNumber", Integer.toString( i + 1 ) );
            rounds.appendChild( round );
            
            ArrayList<Game> games = tournament.getGames( i );
            for (Game game : games ) {
                Element gameElement = document.createElement( "Game" );
                gameElement.setAttribute("WhitePlayerID", game.getWhitePlayer().getId().getAGANoString() );
                gameElement.setAttribute("BlackPlayerID", game.getBlackPlayer().getId().getAGANoString() );
                gameElement.setAttribute("Komi", Integer.toString( game.getKomi() ) );
                gameElement.setAttribute("Handicap", Integer.toString( game.getHandicap() ) );
                gameElement.setAttribute("GameResult", GameResult.toAGAString( game.getResult() ) );
                // AGA results don't map perfectly to internal results.
                gameElement.setAttribute("eggplantresult", game.getResult().name() );
                
                round.appendChild( gameElement );
                //<Game BlackPlayerID="1910" WhitePlayerID="10297" Komi="6" Handicap="0" GameResult="White Win"/>
                //Choices: Black win, White win, Draw, Void, Black forfeit, White forfeit, Double loss, Double forfeit, Voluntary Bye, Involuntary Bye, Unknown
            }

            // Bye player
            Player byePlayer = tournament.getByePlayer( i );
            if ( byePlayer != null ) {
                Element gameElement = document.createElement( "Game" );
                gameElement.setAttribute("WhitePlayerID", byePlayer.getId().getAGANoString() );
                gameElement.setAttribute("BlackPlayerID", byePlayer.getId().getAGANoString() );
                gameElement.setAttribute("Komi", "0" );
                gameElement.setAttribute("Handicap", "0" );
                gameElement.setAttribute("GameResult", "Voluntary Bye" );
                
                round.appendChild( gameElement );
            }
            
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
        
        eggplant.appendChild( XmlUtils.CreateElement( document, "lastassignedagano", Integer.toString( tournament.getLastAssignedAGANo() ) ) );
        
        eggplant.appendChild( XmlUtils.CreateElement( document, "absentnbwx2", Integer.toString( tournament.getPlacementProps().getGenNbw2ValueAbsent() ) ) );
        eggplant.appendChild( XmlUtils.CreateElement( document, "absentmmsx2", Integer.toString( tournament.getPlacementProps().getGenMms2ValueAbsent() ) ) );
        eggplant.appendChild( XmlUtils.CreateElement( document, "byenbwx2", Integer.toString( tournament.getPlacementProps().getGenNbw2ValueBye() ) ) );
        eggplant.appendChild( XmlUtils.CreateElement( document, "byemmsx2", Integer.toString( tournament.getPlacementProps().getGenMms2ValueBye() ) ) );
        
        //
        
        
        
        
        
        
        
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger( XmlEncoderAGA.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
           
        DOMSource source = new DOMSource( document );
        
        
        try {
            FileOutputStream fos = new FileOutputStream( destination );
            StreamResult result = new StreamResult( fos );
            transformer.transform( source, result );
            
            // Debug
            //StreamResult result = new StreamResult(System.out);
            
        } catch ( TransformerException | FileNotFoundException ex  ) {
            Logger.getLogger( XmlEncoderAGA.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        /*
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "tournament.dtd");
            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(ExternalTournamentDocument.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        *
        */
        
        
        
        
        //Logger.getLogger( XmlEncoderAGA.class.getName() ).log( Level.INFO, document.toString() );
        
    }
    
    /*
     * Updates a tournament object from an XML file.
     * 
     * @param  tournamentFile    The file to load.
     * contents of this object will be replaced by what is loaded.
     * @return  A new Tournament populated with what cuold be loaded from the
     * file.
     * 
     */
    public static DecodeResult TournamentFromFile( File tournamentFile, Tournament tournament ) {
        loadReport = "";
        StringBuilder reportBuilder = new StringBuilder("");
        
        //Tournament tournament = new Tournament();
    
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch( ParserConfigurationException ex )
        {
            Logger.getLogger( XmlEncoderAGA.class.getName()).log(Level.SEVERE, null, ex);
            reportBuilder.append("Eggplant is unable to parse files.");
            
            return DecodeResult.Failure;
        }
        Document document;
        try {
            document = dBuilder.parse( tournamentFile );
        } catch ( SAXException | IOException ex ) {
            Logger.getLogger( XmlEncoderAGA.class.getName()).log(Level.SEVERE, null, ex);
            reportBuilder.append("\nEggplant is unable to parse the file.");
            return DecodeResult.Failure;
        }
        
        document.getDocumentElement().normalize();
        
        // Version
        Element root = (Element) document.getElementsByTagName( "TournamentReport" ).item( 0 );
        boolean unversionedSave = false; // Unversioned saves (versions 1.0.45 and earlier) have white and black players flipped (WOWZA!)
        if ( root.hasAttribute("eggplant-version") ) {
            // We don't have any version-filtering to do right now since the save format has not changed.
            // The addition of version number in the save is solely to fix the player inversion bug
            /*
            String version = root.getAttribute("eggplant-version");
            String[] versionParts = version.split(".");
            if ( versionParts.length == 3 ) {
                // Version <= 1.0.45
                flippedSave = ( ( Integer.parseInt(versionParts[0]) <= 1) && ( Integer.parseInt(versionParts[1]) <= 0 ) && ( Integer.parseInt(versionParts[2]) <= 45) );
            } else {
                // Invalid version string, probably a test (IDE) build
            }
            */
        } else {
            unversionedSave = true; // Unversioned save
        }
        
        // Header
        Element header = (Element) document.getElementsByTagName( "Header" ).item( 0 );
        if ( header != null ) {
            
            Date startDate;
            try {
                startDate = new SimpleDateFormat("yyyy-MM-dd").parse( XmlUtils.GetTextContent( header, "StartDate" ) );
                tournament.getProps().setStartDate( startDate );
            } catch ( ParseException ex ) {
                reportBuilder.append("\nInvalid start date.");
            }
            
            Date endDate;
            try {
                endDate = new SimpleDateFormat("yyyy-MM-dd").parse( XmlUtils.GetTextContent( header, "EndDate" ) );
                tournament.getProps().setEndDate( endDate );
            } catch ( ParseException ex ) {
                reportBuilder.append("\nInvalid end date.");
            }
            
            tournament.getProps().setName( XmlUtils.GetTextContent( header,"TournamentName") );

            Element venue = (Element) header.getElementsByTagName("TournamentVenue").item(0);
            tournament.getProps().setLocation( XmlUtils.GetTextContent( venue, "Name" ) );

            try {
                PairingSystem loadedPairingSystem = PairingSystem.valueOf( XmlUtils.GetTextContent( header, "PairingFormat") );
                tournament.getPairingProps().setPairingSystem( loadedPairingSystem );
            } catch ( IllegalArgumentException ex ) {
                reportBuilder.append("\nInvalid pairing format.");
            }

            try {
                Ruleset loadedRuleset = Ruleset.valueOf( XmlUtils.GetTextContent( header, "Rules") );
                tournament.getProps().setRuleset( loadedRuleset );
            } catch ( IllegalArgumentException ex ) {
                reportBuilder.append("\nInvalid rules.");
            }

            try {
                int basicTime = Integer.parseInt( XmlUtils.GetTextContent( header, "BasicTime") );
                tournament.getProps().setBasicTime( basicTime );
            } catch ( NumberFormatException ex ) {
                reportBuilder.append("\nInvalid basic time.");
            }
            
            
            Element overtime = (Element) header.getElementsByTagName("OvertimeFormat").item(0);
            if ( overtime != null ) {
                Element overtimeDetails = XmlUtils.getFirstElementChild( overtime );

                if ( overtimeDetails != null ) {
                    TimeSystem loadedTimeSystem = TimeSystem.getTimeSystemFromString( overtimeDetails.getTagName() );
                    if ( loadedTimeSystem != null ) {
                        tournament.getProps().setTimeSystem( loadedTimeSystem );
                        
                        // Attributes cannot be validated at present as they are simply dumped as key/value pairs into a map.
                        for ( int i = 0; i < overtimeDetails.getAttributes().getLength(); i++ ) {
                            Node timeSystemAttribute = overtimeDetails.getAttributes().item(i);

                            tournament.getProps().setTimeSystemProperty( timeSystemAttribute.getNodeName(), timeSystemAttribute.getNodeValue() );

                            /*
                            Logger.getLogger(XmlEncoderAGA.class.getName()).log( Level.INFO, "Attribute: " + timeSystemAttribute.getNodeName() + " " + timeSystemAttribute.getNodeValue() );
                            Logger.getLogger(XmlEncoderAGA.class.getName()).log( Level.INFO, "props value: " + tournament.getProps().getTimeSystemProperties().get( timeSystemAttribute.getNodeName() ) );
                            */
                        }
                    } else {
                        reportBuilder.append("Invalid overtime system.");
                    }
                } else {
                    reportBuilder.append("\nMissing overtime details.");
                }
            } else {
                reportBuilder.append("\nMissing overtime format.");
            }

            // TODO - awkward array sizing/generation here and in PlacementProps.
            Element placement = (Element) header.getElementsByTagName( "TiebreakSystemList" ).item(0);
            if ( placement != null ) {
                NodeList criteriaTags = placement.getElementsByTagName( "System" );

                for ( int i = 0; i < criteriaTags.getLength(); i++ ) {
                    try {
                        Element criteriaTag = (Element) criteriaTags.item( i );
                        PlacementCriterion pc = PlacementCriterion.valueOf( criteriaTag.getAttribute("Method") );
                        int index = Integer.parseInt( criteriaTag.getAttribute("Tier") ) - 1;
                        tournament.getPlacementProps().setPlaCriterion( index, pc );
                    } catch ( NumberFormatException ex ) {
                        reportBuilder.append("\nInvalid tiebreak system tier value.");
                    } catch ( IllegalArgumentException ex ) {
                        reportBuilder.append("\nInvalid tiebreak system method.");
                    }
                }
            } else {
                reportBuilder.append("Invalid tiebreak system list.");
            }

            tournament.getProps().setComment( XmlUtils.GetTextContent( header, "Comment") );
        } else {
            reportBuilder.append("\nTournament file has no header block.");
        }
        //
        
        /*
        Logger.getLogger( XmlEncoderAGA.class.getName() ).log( Level.INFO, "sweep" );
        for (Map.Entry<String, String> entry : tournament.getProps().getTimeSystemProperties().entrySet() ) {
            Logger.getLogger( XmlEncoderAGA.class.getName() ).log( Level.INFO, entry.getKey() + " " + entry.getValue() );
        }
        */

        // Eggplant
        // Must be loaded before players/games as placement information is required in order to insert games.
        Element eggplant = (Element) document.getElementsByTagName( "eggplant" ).item( 0 );
        if ( eggplant != null ) {
            try {
                int newNumberOfRounds = Integer.parseInt( XmlUtils.GetTextContent( eggplant, "numberofrounds" ) );
                tournament.getPairingProps().setNumberOfRounds( newNumberOfRounds );
            } catch ( NumberFormatException ex ) {
                reportBuilder.append("\nInvalid number of rounds.");
            }
    
            // TODO - throw exceptions up the chain, so we can validate here?
            tournament.getPairingProps().setMMBar( XmlUtils.GetTextContent( eggplant, "mmbar") );
            tournament.getPairingProps().setMMFloor( XmlUtils.GetTextContent( eggplant, "mmfloor") );
            //
            
            try {
                tournament.getPairingProps().setBandSpacingScheme( BandSpacingScheme.valueOf( XmlUtils.GetTextContent( eggplant, "bandspacingscheme") ) );
            } catch ( IllegalArgumentException ex ) {
                reportBuilder.append("\nInvalid band spacing scheme.");
                
                // Default to stacked as that was how things operated before this option existed.
                tournament.getPairingProps().setBandSpacingScheme(BandSpacingScheme.STACKED);
            }
            

            try {
                int lastRound = Integer.parseInt( XmlUtils.GetTextContent( eggplant, "lastroundforseedsystem" ) );
                tournament.getPairingProps().setLastRoundForSeedSystem1( lastRound );
            } catch ( NumberFormatException ex ) {
                reportBuilder.append("\nInvalid last round for seed system.");
            }
            
            try {
                PairingMethod loadedPairingScheme1 = PairingMethod.valueOf( XmlUtils.GetTextContent( eggplant, "pairingscheme1") );
                tournament.getPairingProps().setPairingScheme1( loadedPairingScheme1 );
            } catch ( IllegalArgumentException ex ) {
                reportBuilder.append("\nInvalid pairing scheme 1.");
            }
            
            try {
                PairingMethod loadedPairingScheme2 = PairingMethod.valueOf( XmlUtils.GetTextContent( eggplant, "pairingscheme2") );
                tournament.getPairingProps().setPairingScheme2( loadedPairingScheme2 );
            } catch ( IllegalArgumentException ex ) {
                reportBuilder.append("\nInvalid pairing scheme 2.");
            }

            tournament.getPairingProps().setHandicapGames( Boolean.parseBoolean( XmlUtils.GetTextContent( eggplant, "handicapgames") ) );
            tournament.getPairingProps().setMaxHandicappedRank( XmlUtils.GetTextContent( eggplant, "maxhandicappedrank") );
            
            try {
                tournament.getPairingProps().setHandicapBasis( HandicapBasis.valueOf( XmlUtils.GetTextContent( eggplant, "handicapbasis") ) );
            } catch ( IllegalArgumentException ex ) {
                reportBuilder.append("\nInvalid handicap basis.");
            }
            
            try {
                int loadedHandicapModifier = Integer.parseInt( XmlUtils.GetTextContent( eggplant, "handicapmodifier") );
                tournament.getPairingProps().setHandicapModifier( loadedHandicapModifier );
            } catch ( NumberFormatException ex ) {
                reportBuilder.append("\nInvalid handicap modifier.");
            }
            
            try {
                int maxHandicap = Integer.parseInt( XmlUtils.GetTextContent( eggplant, "handicapceiling") );
                tournament.getPairingProps().setMaxHandicap( maxHandicap );
            } catch ( NumberFormatException ex ) {
                reportBuilder.append("\nInvalid max handicap.");
            }
            
            try {
                float komiFloat = Float.parseFloat( XmlUtils.GetTextContent( eggplant, "defaultkomi") );
                tournament.getPairingProps().setDefaultKomi( (int) Math.floor( komiFloat ) );
            } catch ( NumberFormatException ex ) {
                reportBuilder.append("\nInvalid default komi.");
            }

            tournament.setNewPlayerAGANo( XmlUtils.GetTextContent( eggplant, "lastassignedagano") );

            try {
                int value = Integer.parseInt( XmlUtils.GetTextContent( eggplant, "absentnbwx2") );
                tournament.getPlacementProps().setGenNbw2ValueAbsent( value );
            } catch ( NumberFormatException ex ) {
                reportBuilder.append("\nInvalid number of wins for absent player.");
            }
            try {
                int value = Integer.parseInt( XmlUtils.GetTextContent( eggplant, "absentmmsx2") );
                tournament.getPlacementProps().setGenMms2ValueAbsent( value );
            } catch ( NumberFormatException ex ) {
                reportBuilder.append("\nInvalid MMS for absent player.");
            }
            try {
                int value = Integer.parseInt( XmlUtils.GetTextContent( eggplant, "byenbwx2") );
                tournament.getPlacementProps().setGenNbw2ValueBye( value );
            } catch ( NumberFormatException ex ) {
                reportBuilder.append("\nInvalid number of wins for bye player.");
            }
            try {
                int value = Integer.parseInt( XmlUtils.GetTextContent( eggplant, "byemmsx2") );
                tournament.getPlacementProps().setGenMms2ValueBye( value );
            } catch ( NumberFormatException ex ) {
                reportBuilder.append("\nInvalid MMS for bye player.");
            }
            
        } else {
            reportBuilder.append("\nTournament file has no eggplant block.");
        }
        
        //
        
        
        
        // PlayerList
        Element playerList = (Element) document.getElementsByTagName( "PlayerList" ).item( 0 );

        if ( playerList != null ) {
            NodeList playerNodes = playerList.getElementsByTagName("Player");

            ArrayList<Player> loadedPlayers = new ArrayList<>();

            for (int playerIndex = 0; playerIndex < playerNodes.getLength(); playerIndex++ ) {
                Element playerElement = (Element) playerNodes.item( playerIndex );
                Player newPlayer = new Player();

                // non-AGA data
                newPlayer.getId().setRating( playerElement.getAttribute( "eggplantagarating" ) );

                String[] participations = playerElement.getAttribute( "eggplantparticipation" ).split(",");
                for ( int i = 0; i<participations.length; i++ ) {
                    newPlayer.setParticipation( i, Participation.valueOf( participations[i] ) );
                }
                //

                newPlayer.getId().setAGANo( playerElement.getAttribute("ID") );
                newPlayer.getId().setName( playerElement.getAttribute("FamilyName") );
                newPlayer.getId().setFirstName( playerElement.getAttribute("GivenName") );
                newPlayer.setRank( playerElement.getAttribute("Rating") );
                newPlayer.getScore().setInitialMms( playerElement.getAttribute("InitialTournamentScore") );

                loadedPlayers.add( newPlayer );
            }
            tournament.addLoadedPlayers( loadedPlayers );
        }
        //
        
        
        // RoundList
        Element rounds = (Element) document.getElementsByTagName( "RoundList" ).item( 0 );
        NodeList roundNodes = null;
        
        if ( rounds != null ) { roundNodes = rounds.getElementsByTagName("Round"); }
        
        if ( roundNodes != null ) {
            
            for (int roundNodeIndex = 0; roundNodeIndex < roundNodes.getLength(); roundNodeIndex++ ) {
                Element roundElement = (Element) roundNodes.item( roundNodeIndex );
                int roundIndex = Integer.parseInt( roundElement.getAttribute( "RoundNumber" ) ) - 1;

                NodeList gameNodes = roundElement.getElementsByTagName( "Game" );
                ArrayList<Game> games = new ArrayList<>();

                for ( int i = 0; i < gameNodes.getLength(); i++ ) {
                    Element gameElement = (Element) gameNodes.item( i );
                    
                    // Is this an actual pairing or a bye player?
                    if ( gameElement.getAttribute( "WhitePlayerID" ).equals( gameElement.getAttribute( "BlackPlayerID" ) ) ) {
                        tournament.assignByePlayer( tournament.getPlayerByAGA( gameElement.getAttribute( "WhitePlayerID" ) ), roundIndex);
                    } else {
                        Game game = new Game( roundIndex,
                                            -1,
                                            tournament.getPlayerByAGA( gameElement.getAttribute( "WhitePlayerID" ) ),
                                            tournament.getPlayerByAGA( gameElement.getAttribute( "BlackPlayerID" ) ),
                                            true,
                                            Integer.parseInt( gameElement.getAttribute( "Handicap" ) ),
                                            Integer.parseInt( gameElement.getAttribute( "Komi" ) ),
                                            GameResult.valueOf( gameElement.getAttribute( "eggplantresult" ) ) );
                        games.add( game );
                    }
                }
                tournament.addLoadedGames( roundNodeIndex, games );
            }
        }
        //        
        
        

        
        if ( reportBuilder.length() > 0 ) {
            reportBuilder.insert(0, "The following errors were found in the loaded tournament file:\n");
            loadReport = reportBuilder.toString();
        }
        
        if ( unversionedSave ) {
            return DecodeResult.Unversioned;
        } else {
            return DecodeResult.Success;
        }
    }
    
    private static Element CreatePlayerElement( Document document, Player player, int numberOfRounds ) {
        Element element = document.createElement( "Player" );
        element.setAttribute("ID", player.getId().getAGANoString() );
        element.setAttribute("GivenName", player.getId().getFirstName() );
        element.setAttribute("FamilyName", player.getId().getName() );
        element.setAttribute("Rating", player.getRank().toString() );
        element.setAttribute("InitialTournamentScore", Integer.toString( player.getScore().getInitialMms() ) );
        // non-AGA data
        element.setAttribute("eggplantagarating", player.getId().getRating().toString() );
        
        String participation = "";
        for ( int i=0; i<numberOfRounds; i++ ) {
            participation += player.getParticipation(i).name() + ",";
        }
        
        element.setAttribute("eggplantparticipation", participation );
        
        //<Player ID="10297" GivenName="Yun" FamilyName="Feng" Rating="7D" InitialTournamentScore="0"/>
        return element;
    }
    
    
    
    
    public static String getLoadReport() {
        return loadReport;
    }

}
