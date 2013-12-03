package org.seattlego.eggplant.model;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a rank 30 kyu - 9 dan. Rank is effectively integer value: all 30 
 * kyus have equivalent rank. This is different from Rating.
 * 
 * @author Topsy
 */
public class Rank implements Comparable<Rank> {
    private int value;
    
    public final static Rank MIN_RANK = new Rank("30k");
    public final static Rank MAX_RANK = new Rank("9d");
    
    
    private final static String DEFAULT_RANK = "30k";
    
    public Rank() {
        this( DEFAULT_RANK );
    }
    /*
     * Converts string to numeric rank integer. String must be a number 
     * followed by the letter "k" or "d".
     */
    public Rank( String rankString ) {
        String numerics = rankString.replaceAll("\\D+","");
        value  = -30;
        try {
            value = Integer.parseInt(numerics);
        } catch (NumberFormatException e ) {
            Logger.getLogger( Rank.class.getName() ).log( Level.WARNING, "Invalid rank value." );
        }
        
        if ( rankString.contains("k") || rankString.contains("K") ) {
            value = -value;
        } else if ( rankString.contains("d") || rankString.contains("D")) {
            value = value - 1;
        }
    }    
        
    public Rank( Rating rating ) {
        setTo( rating );
    }
    
    public int getValue() {
        return value;
    }
    
    public void setTo( Rating rating ) {
        value = (int) Math.ceil( rating.getValue() );
    }
    
    public void adjust( int amount ) {
        value += amount;
    }
    
    @Override
    public Rank clone() {
        return new Rank( this.toString() );
    }
    
    /*
     * toString()
     * 
     * Converts integer rank to string kyu/dan rank.
     * 
     */
    @Override
    public String toString() {
        String strRank = "";
        if (value >=0) strRank  = "" + (value +1) + "D";
        if (value < 0) strRank  = "" + (-value)   + "K";
        return strRank;
    }

    @Override
    public int compareTo( Rank otherRank ) {
        return Integer.compare( this.getValue(), otherRank.getValue() );
    }
    
    
    /*
     * Return integer MMS corresponding to this rank. This conversion is 
     * encapsulated here to avoid inconsistent conventions being applied. This
     * is mainly used for initial MMS and for calculating max/min 
     * possible MMS for a given round.
     * 
     * The convention used maps 30k to 0. All MMS are positive.
     */
    public int toMms() {
        return value - Rank.MIN_RANK.value;
    }
    
    /*
     * Create a Rank object with value corresponding to the given MMS. This is
     * effectively the reverse of toMms.
     */
    public static Rank RankFromMms( int MMS ) {
        Rank rank = new Rank();
        rank.value = MMS + Rank.MIN_RANK.value;
        return rank;
    }
    
    /*
     * Max comparison for Ranks.
     * 
     * @param   a   A rank.
     * @param   b   Another rank.
     * @return  a if a>=b
     *          b if b>a
     */
    public static Rank max( Rank a, Rank b ) {
        if ( a.compareTo(b) >= 0 ) return a;
        else return b;
    }
    
    
    
    
    
    
    
    
    
    
    
   
    /* TODO - is this used? Remove it!
     * getRankStr()
     * 
     * Converts internal rating scale to string kyu dan rank.
     * 
     */
    public static String getRankStr( int _rating ){
        String rank;
        _rating = (int) _rating;
        if ( _rating<= 29 ){
            _rating = Math.abs( _rating - 30 );
            rank = "" + _rating + "k";
        } else {
            _rating = _rating - 29;
            rank = "" + _rating + "d";
        }
        return rank;
    }
    
    

    
    
}
