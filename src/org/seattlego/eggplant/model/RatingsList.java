package org.seattlego.eggplant.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * 
 * TODO: Charset for reader should perhaps be different. We want UTF-8 support.
 * 
 * @author Topsy
 */
public class RatingsList {
    
    private ArrayList<String> rawList;
    private ArrayList<PlayerId> playersAvailable;
    
    
    public RatingsList( File f ) {
        parseFile(f);
    }

    private void parseFile(File f) {
        // Transfer file content to a ArrayList<String>
        rawList = new ArrayList();
        try {
            FileInputStream input = new FileInputStream(f);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, java.nio.charset.Charset.forName("ISO8859-15")));

            String s;
            do{
                 s = reader.readLine();
                if (s != null){
                    rawList.add(s);
                }
            } while (s !=null);
            reader.close();
            input.close();
        } catch (Exception ex){
            //Logger.getLogger(RatingList.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        playersAvailable = new ArrayList();
        // Parse rating list
        for (String line : rawList){
            if (line.length() == 0) continue;
            
            String[] cols = line.split("\t");

            String[] names = cols[0].split(",");
            String strName = "", strFirstName = "";
            if (names.length>=1)
                strName = names[0].trim();
            if (names.length>=2)
                strFirstName = names[1].trim();

            String strClub = "";
            if (cols.length>=6)
                strClub = cols[5];

            int agaNo = 0;
            if (cols.length>=2)
                agaNo = Integer.parseInt( cols[1] );

            String expiration = "";
            if (cols.length>=4)
                expiration = cols[4];

            float agaRating = (float) -30.5;
            if ( (cols.length>=4) && (!cols[3].isEmpty()) ){
                agaRating = Float.parseFloat( cols[3] );
            }
            
            PlayerId playerId = new PlayerId( strName, strFirstName, agaRating, agaNo, expiration, strClub );
            playersAvailable.add( playerId );

        }
                
    }    
    public ArrayList<PlayerId> getPlayersAvailable() {
        return playersAvailable;
    }
    
    public ArrayList<PlayerId> getPlayersAvailable( String filter ) {
        int filterLength = filter.length();
        
        if ( filterLength == 0 ) { return playersAvailable; }
        
        ArrayList<PlayerId> filteredList = new ArrayList<>();
        
        
        for ( PlayerId pId : playersAvailable ) {
            if ( pId.getName().regionMatches(true, 0, filter, 0, filterLength) ) { filteredList.add( pId ); continue; }
            if ( pId.getFirstName().regionMatches(true, 0, filter, 0, filterLength) ) { filteredList.add( pId ); continue; }
            if ( pId.getAGANoString().regionMatches(true, 0, filter, 0, filterLength) ) { filteredList.add( pId ); continue; }
        }
        
        return filteredList;
    }
    
}
