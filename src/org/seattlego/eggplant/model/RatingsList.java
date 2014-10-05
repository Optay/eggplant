package org.seattlego.eggplant.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
    
    public ArrayList<PlayerId> getPlayersAvailable( String filterText ) {
        String[] filterWords = filterText.split(" ");
        
        if ( (filterWords.length == 1) && (filterWords[0].length() == 0 ) ) {
            return playersAvailable;
        }
        
        // Make a copy of the available players list and prune it as we go.
        ArrayList<PlayerId> sourceList = (ArrayList) playersAvailable.clone();
        ArrayList<PlayerId> filteredList = new ArrayList<>();
        
        for ( String filter : filterWords ) {
            int filterLength = filter.length();
            if ( filterLength == 0 ) { continue; }
            
            boolean exact = false;
            char[] filterray = filter.toCharArray();
            if ( (filterray.length > 1) && (filterray[0] == '"') && (filterray[filterray.length - 1] == '"' ) ) {
                exact = true;
                filter = filter.substring(1, filter.length()-1);
            }
            
            filteredList = new ArrayList<>();
            if ( exact ) {
                for ( PlayerId pId : sourceList ) {
                    for( String name : pId.getNames() ) {
                        if ( name.equalsIgnoreCase(filter) ) { filteredList.add( pId ); continue; }
                    }
                    if ( pId.getAGANoString().equalsIgnoreCase(filter) ) { filteredList.add( pId ); continue; }
                }
            } else {
                for ( PlayerId pId : sourceList ) {
                    for( String name : pId.getNames() ) {
                        if ( name.regionMatches(true, 0, filter, 0, filterLength) ) { filteredList.add( pId ); continue; }
                    }
                    if ( pId.getAGANoString().regionMatches(true, 0, filter, 0, filterLength) ) { filteredList.add( pId ); continue; }
                    }
            }
            sourceList = (ArrayList) filteredList.clone();;
        }
        
        return filteredList;
    }
    
}
