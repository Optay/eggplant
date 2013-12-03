package org.seattlego.eggplant.model;

import java.util.ArrayList;
import java.util.Collections;
import org.seattlego.eggplant.model.comparators.PlayerComparator;

/**
 * Represents a Band as used in the Bands form tree. Wraps a String label and a 
 * MMS integer assigned as initialMms to players within.
 * 
 * @author Topsy
 */
public class Band {
    
    private String label;
    private int mms;
    private int spacing;
    private ArrayList<Player> players;
    
    public Band( String label, int mms ) {
        this( label, mms, 1 );
    }
    public Band( String label, int mms, int spacing ) {
        this.label = label;
        this.mms = mms;
        this.spacing = spacing;
        
        players = new ArrayList<>();
    }
    
    public String getLabel() { return label; }
    public void setLabel( String newLabel ) { label = newLabel; }
    
    @Override
    public String toString() { return label; }
    
    public int getMms() { return mms; }
    public void setMms( int newMms ) {
        mms = newMms;
        for ( Player p : players ) {
            p.getScore().setInitialMms( mms );
        }
    }
    
    public int getSpacing() { return spacing; }
    public void setSpacing( int newSpacing ) { spacing = newSpacing; }
    
    
    public ArrayList<Player> getPlayers() { return players; }
    public void addPlayer( Player p ) {
        if ( !players.contains(p) ) { players.add(p); }
        p.getScore().setInitialMms( mms );
        
        Collections.sort( players, new PlayerComparator( new PlacementCriterion[] {PlacementCriterion.RANK}, 0, false ) );
    }
    public void removePlayer( Player p ) {
        if ( players.contains(p) ) { players.remove(p); }
    }
}
