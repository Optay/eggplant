package org.seattlego.eggplant.model;

import java.util.Arrays;

/**
 *
 * @author Topsy
 */
public class Player {
    
    private PlayerId id;
    private PlayerScore score;
    private Rank rank;
    private String keyString = "";
    private Participation[] participation;
    
    
    public Player() {
        this( new PlayerId() );
    }
    
    public Player( PlayerId newId ) {
        id = newId.clone();
        
        score = new PlayerScore( this );
        participation = new Participation[ 100 ];   // TODO - initialize this size sensibly.
        Arrays.fill( participation, Participation.UNKNOWN );
        
        setRankFromRating();
    }
    
    public Participation getParticipation( int roundIndex ){
        return participation[roundIndex];
    }
    public void setParticipation( int roundIndex, Participation newParticipation ){
        participation[roundIndex] = newParticipation;
    }
    
    public Rank getRank() {
        return rank;
    }
    // Rank is bounded to prevent self-demotion.
    public void setRank( String rankString ) {
        Rank minRank = new Rank( id.getRating() );
        Rank newRank = new Rank( rankString );
        
        if ( newRank.compareTo( Rank.MAX_RANK ) > 0 ) { newRank = Rank.MAX_RANK.clone(); }
        if ( newRank.compareTo( minRank ) < 0 ) {
            this.rank = minRank;
        } else {
            this.rank = newRank;
        }
        
        score.setInitialMms( rank.toMms() );
    }
    
    public void setRankFromRating() {
        rank = new Rank( id.getRating() );
        
        score.setInitialMms( rank.toMms() );
    }
    
    public PlayerScore getScore() { return score; }
    
    /*
     * TODO - an attempt to encapsulate PlayerScore a bit, so it can be hidden.
     * This fails because it has many properties that are not covered by the
     * PlacementCriterion enum. Rank and Rating are still the odd ducks in that
     * collection however, and I'm not completely happy with the implementation.
    public int getMetric( int roundIndex, PlacementCriterion criterion ) {
        switch( criterion ) {
            case RANK:
                return rank.getValue();
            case RATING:
                // Rating is not an int!!! Arg!
                return 0;
            default:
                return score.getMetric( roundIndex, criterion );
        }
    }
    public String getMetricString( int roundIndex, PlacementCriterion criterion ) {
        switch( criterion ) {
            case RANK:
                return rank.toString();
            case RATING:
                return id.getRating().toString();
            default:
                return score.getMetricString( roundIndex, criterion );
        }
        
    }
     */
    
    
    public PlayerId getId() { return id; }

    /**
     * Returns a key String for the player
     * fast and convenient for hash tables
     */
    public String getKeyString(){
        return keyString;
    }
    /**
    * Assign the keystring.
    * @param newKey 
    */
    public void setKeyString( String newKey ) {
        keyString = newKey;
    }
    
    
    @Override
    public String toString() {
        return getId().getFullName() + " " + getRank().toString();
    }

    
}
