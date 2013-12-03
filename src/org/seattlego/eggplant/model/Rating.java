package org.seattlego.eggplant.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a rating.
 * 
 * @author Topsy
 */
public class Rating implements Comparable<Rating> {
    
    private double value;
    
    private static RatingStyle defaultRatingStyle = RatingStyle.AGA;
    
    private static final double DEFAULT_RATING_VALUE = -30.5;
    
    private Rating() {
    }
    
    public static Rating CreateRatingFromAGA( Double ratingValue ) {
        if ( ratingValue < -31d ) ratingValue = -31d;
        if ( ratingValue > 0d ) ratingValue -= 2d;
        
        Rating rating = new Rating();
        rating.value = ratingValue;
        return rating;
    }
    public static Rating CreateRatingFromAGAString( String ratingString ) {
        double ratingAga = DEFAULT_RATING_VALUE;
        try {
            ratingAga = Double.parseDouble( ratingString );
        } catch (NumberFormatException ex) {
            Logger.getLogger( Rating.class.getName() ).log( Level.WARNING, "Invalid AGA Rating string" );
        }
        
        return CreateRatingFromAGA( ratingAga );
    }
    
    @Override
    public String toString() {
        return toString( defaultRatingStyle );
    }
    
    public String toString( RatingStyle style ) {
        NumberFormat formatter = new DecimalFormat("#0.00");
        return formatter.format( getAga() );
    }
    
    @Override
    public int compareTo( Rating otherRating ) {
        return Double.compare( this.getValue(), otherRating.getValue() );
    }
    
    
    public double getAga() {
        double ratingAGA = value;
        
        if (ratingAGA > -1)
            ratingAGA += 2;
        
        return ratingAGA;
    }
    
    public double getValue() {
        return value;
    }
    
    
}
