package org.seattlego.eggplant.io;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.seattlego.eggplant.interfaces.ITournament;
import org.seattlego.eggplant.model.Game;
import org.seattlego.eggplant.model.Player;
import org.seattlego.eggplant.model.PlayerResult;

/**
 *
 * @author Topsy
 */
public class ReportGenerator {
    public static void generateReport( ITournament t, File destination ) {

        Writer output = null;
        try {
            output = new BufferedWriter(new FileWriter( destination ));
        } catch (IOException ex) {
            Logger.getLogger( ReportGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        /* Use Windows line breaks to prevent problems viewing report on
         * Windows. Non-Windows systems will be able to interpret the line 
         * breaks correctly.
         */
        //String eol = System.getProperty("line.separator");
        String eol = "\r\n";
        String todo = "***";
        
        // Headers       
        try {
            output.write("TOURNEY " + t.getProps().getName() + ", " + t.getProps().getLocation() );
            output.write(eol + "\tstart=" + new SimpleDateFormat("MM/dd/yyyy").format( t.getProps().getStartDate() ) );
            output.write(eol + "\tfinish=" + new SimpleDateFormat("MM/dd/yyyy").format( t.getProps().getEndDate() ) );
            output.write(eol + "\trules=Japanese" );
               
            output.write(eol);
        } catch (IOException ex) {
            Logger.getLogger(ReportGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        //

        // Players
        // Check for new or renewed players during this loop.
        
        ArrayList<Player> newRegistrations = new ArrayList<Player>();
        ArrayList<Player> renewals = new ArrayList<Player>();
        
        try {
            output.write(eol + "PLAYERS");
            for (Player p : t.getRegisteredPlayers() ) {
                output.write(eol + p.getId().getAGANoString() );
                output.write("\t" + p.getId().getName() + ", " + p.getId().getFirstName() );
                output.write("\t" + p.getRank().toString() );
                
                // Identify new members and renewals:
                if ( p.getId().getAGANo() >= t.getLastAssignedAGANo() ) {
                    newRegistrations.add( p );
                    continue;
                }
                if ( p.getId().getExpiration().before( new Date() ) ) {
                    renewals.add( p );
                }
                //
            }
            output.write(eol);
        } catch (IOException ex) {
            Logger.getLogger(ReportGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Games
        try {
            for( int roundIndex = 0; roundIndex < t.getPairingProps().getNumberOfRounds(); roundIndex++ ) {
                
                ArrayList<Game> games = t.getGames(roundIndex);
                output.write(eol + "GAMES " + Integer.toString( roundIndex+1 ) );
                
                for ( Game g: games ) {
//                    if ( (g.getResult() != Game.RESULT_BLACKWINS) && (g.getResult() != Game.RESULT_WHITEWINS) )
//                        continue;
                    output.write(eol + g.getWhitePlayer().getId().getAGANoString() );
                    output.write("\t" + g.getBlackPlayer().getId().getAGANoString() );
                    if ( g.getResult().getBlackResult() == PlayerResult.WIN ) { 
                        output.write("\tb");
                    } else if ( g.getResult().getWhiteResult() == PlayerResult.WIN ) { 
                        output.write("\tw");
                    } else {
                        output.write("\t?");
                    }
                    output.write("\t" + g.getHandicapString() );
                    output.write("\t" + Integer.toString( g.getKomi() ) );  // Integer value is used.
                }
                output.write(eol);
            }
        } catch (IOException ex) {
            Logger.getLogger( ReportGenerator.class.getName() ).log(Level.SEVERE, null, ex);
        }
        
        try {
            output.write("END" + eol);
        } catch (IOException ex) {
            Logger.getLogger( ReportGenerator.class.getName() ).log(Level.SEVERE, null, ex);
        }
        
        // registration addendums
        if ( renewals.size() > 0 ) {
            try {
                output.write(eol + "RENEWALS");
                for (Player p : renewals ) {
                    output.write(eol + "AGA - full" );
                    output.write(eol + "AGA Fee: " + todo);
                    output.write(eol + "AGA number: " + p.getId().getAGANoString() );
                    output.write(eol + "Name: " + p.getId().getName() + ", " + p.getId().getFirstName() );
                    output.write(eol);
                }
                output.write(eol);
            } catch (IOException ex) {
                Logger.getLogger(ReportGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        // new members
        if ( newRegistrations.size() > 0 ) {
        
            try {
                output.write(eol + "NEW REGISTRATIONS");
                for (Player p : newRegistrations ) {
                    output.write(eol + "AGA - " + todo + "full/promotional/ratings only" + todo);
                    output.write(eol + "AGA Fee: " + todo);
                    output.write(eol + "AGA number: " + p.getId().getAGANoString() );
                    output.write(eol + "Name: " + p.getId().getName() + ", " + p.getId().getFirstName() );
                    output.write(eol + "Address: " + todo );
                    output.write(eol + "E-mail: " + todo );
                    output.write(eol);
                }
                output.write(eol);
            } catch (IOException ex) {
                Logger.getLogger( ReportGenerator.class.getName() ).log(Level.SEVERE, null, ex);
            }
        }
            
            
        
        // TD
        try {
            output.write(eol + "TD: " + todo);
            output.write(eol + "\t" + todo + "email" + todo);
            output.write(eol + eol + "Organizer: Seattle Go Center" + eol);
        } catch (IOException ex) {
            Logger.getLogger(ReportGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(ReportGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        
    }
    
    
    
}
