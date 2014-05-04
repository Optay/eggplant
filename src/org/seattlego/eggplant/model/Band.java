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
    private int offset;
    private int spacing;
    private ArrayList<Player> players;
    private Rank max;
    private Rank min;
    private boolean mutable;    // Determines if band's rank bounds can be changed.
    
    /**
     * Create a Band for a specified rank. Band will be mutable.
     * @param mms 
     */
    public Band( int mms ) {
        this( "Band", mms, null, null, true );
    }
    
    /**
     * Create a Band for a specified range of ranks. This should only be used
     * for bar/floor bands. The band will be immutable.
     * 
     * @param min
     * @param max 
     */
    public Band( String label, int mms, Rank min, Rank max ) {
        this( label, mms, min, max, false );
    }
    
    /**
     * 
     * @param label
     * @param mms
     * @param min
     * @param max
     * @param mutable 
     */
    public Band( String label, int mms, Rank min, Rank max, boolean mutable ) {
        this.label = label;
        this.mms = mms;
        this.offset = 0;
        
        // Spacing is always 1 by default. If Band is rank-based, it will need to be updated by subsequent sweep.
        this.spacing = 1;
        this.mutable = mutable;
        
        if ( (min==null) || ( max == null ) ) {
            this.max = Rank.RankFromMms(mms);
            this.min = Rank.RankFromMms(mms);
        } else {
            this.max = max;
            this.min = min;
        }
        
        players = new ArrayList<>();
    }
    
    public String getLabel() { return label; }
    public void setLabel( String newLabel ) { label = newLabel; }
    
    @Override
    public String toString() {
        return Integer.toString( offset ) + ": " + label;
    }
    
    public int getMms() { return mms; }
    public void setMms( int newMms ) {
        mms = newMms;
        for ( Player p : players ) {
            p.getScore().setInitialMms( mms );
        }
    }
    
    public int getOffset() { return offset; }
    public void setOffset( int newOffset ) {
        offset = newOffset;
    }
    
    
    public int getSpacing() { return spacing; }
    public void setSpacing( int newSpacing ) { spacing = newSpacing; }
    
    public Rank getMinRank() { return min; }
    public Rank getMaxRank() { return max; }
    
    
    public ArrayList<Player> getPlayers() { return players; }
    public void addPlayer( Player p ) {
        if ( !players.contains(p) ) { players.add(p); }
        p.getScore().setInitialMms( mms );
        
        UpdateMinMax();
        
        Collections.sort( players, new PlayerComparator( new PlacementCriterion[] {PlacementCriterion.RANK}, 0, false ) );
    }
    public void removePlayer( Player p ) {
        if ( players.contains(p) ) {
            players.remove(p);
            UpdateMinMax();
        }
    }
    
    private void UpdateMinMax() {
        if ( players.isEmpty() ) { return; }
        if ( !mutable ) { return; }
        
        Rank minRank = Rank.MAX_RANK;
        Rank maxRank = Rank.MIN_RANK;
        
        for ( Player p : players ) {
            if ( p.getRank().compareTo( minRank ) == -1 ) { minRank = p.getRank(); }
            if ( p.getRank().compareTo( maxRank ) == 1 ) { maxRank = p.getRank(); }
        }
        
        // Ensure we do not reference properties of a player.
        min = minRank.clone();
        max = maxRank.clone();
        
        // Update label
        String rankLabel;
        if ( min.compareTo(max)==0 ) { rankLabel = min.toString(); }
        else { rankLabel = max.toString() + "-" + min.toString(); }
        
        label = "Band " + rankLabel;
    }
    
}
