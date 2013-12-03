package org.seattlego.eggplant.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Identity of a player. This includes all the usual names and numbers.
 * 
 * @author Topsy
 */
public class PlayerId {
    private int agaNo = 0;
    private Date membershipExpiration;
    private String name;
    private String firstName;
    private Rating rating;
    private String club;
    
    public PlayerId(){
        this( "", "", -30.5, 0, "", "" );
    }
    
            
    public PlayerId(
            String _name,
            String _firstName,
            double _rating,
            int _agaNo,
            String _membershipExpiration,
            String _club
            ) {
        
        if (_agaNo >= 0 )
            this.agaNo = _agaNo;
        this.name = _name;
        this.firstName = _firstName;
        this.club = _club;
        this.rating = Rating.CreateRatingFromAGA( _rating );
        
        
        if ( !_membershipExpiration.equals("") )
        {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                membershipExpiration = formatter.parse(_membershipExpiration);
            } catch (ParseException e) {
                Logger.getLogger( PlayerId.class.getName() ).log( Level.SEVERE, "Unable to parse expiration date string: " + name + ", " + firstName + " " + _membershipExpiration, e );
                /* Set to an expired date. It is easier to delete false 
                 * positives from the report than to add missing players.
                 */
                Calendar expired = Calendar.getInstance();
                expired.add( Calendar.YEAR, -1 );
                
                membershipExpiration = expired.getTime();
            }
        } else {
            /* Set to a valid date. This is to cover valid members who do not
             * yet appear in the ratingslist. New registrations will be
             * recognizable by their AGA number.
             */
            Calendar valid = Calendar.getInstance();
            valid.add( Calendar.YEAR, 1 );
            membershipExpiration = valid.getTime();
        }
    }
    
    
    public Date getExpiration(){
        return membershipExpiration;
    }
    public String getExpirationString() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        return (formatter.format( this.getExpiration() ));
    }
    public void setExpiration( String _d ){
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        try {
            membershipExpiration = formatter.parse(_d);
        } catch (ParseException e) {
            membershipExpiration = new Date(0);
        }
    }
    
    public int getAGANo() {
        return agaNo;
    }
    public String getAGANoString() { return Integer.toString( agaNo ); }
    
    public void setAGANo( int _no ) {
        agaNo = _no;
    }
    public void setAGANo( String _no ) {
        try {
            _no = _no.trim();
            agaNo = Integer.parseInt( _no );
        } catch ( NumberFormatException _e ) {
            // Null value, will be auto-assigned by tournament.
            agaNo = 0;
        }
    }
    
    /**
     * Name and AGA.
     */
    public String getSummary() {
        return name.toUpperCase() + " " + firstName + " " + getAGANoString();
    }
    

    public String getName() {
        return name;
    }
    public void setName( String _str )
    {
        name = _str;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName( String _str )
    {
        firstName = _str;
    }
    
    public String getFullName() {
        return name.toUpperCase() + " " + firstName;
    }
    
    public String getClub() {
        return club;
    }
    public void setClub( String _str ) {
        club = _str;
    }

    public Rating getRating() {
        return rating;
    }
    public void setRating( double _n ){
        rating = Rating.CreateRatingFromAGA(_n);
    }
    public void setRating( String AGARating ){
        rating = Rating.CreateRatingFromAGAString( AGARating );
    }
    
    
    
    
    
    @Override
    public PlayerId clone() {
        return new PlayerId( this.name, this.firstName, this.rating.getAga(), this.agaNo, this.getExpirationString(), this.club );
    }
    
}
