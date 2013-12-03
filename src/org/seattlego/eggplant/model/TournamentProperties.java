package org.seattlego.eggplant.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * These properties are isolated to make templates more convenient and to keep
 * the Tournament class tidy.
 * 
 * @author Topsy
 */
public class TournamentProperties {
    
    private String name;
    private Date startDate;
    private Date endDate;
    private String location;
    private String comment;
    private Ruleset ruleset;
    private int basicTime;
    private TimeSystem timeSystem;
    private HashMap<String, String> timeSystemProperties;
    
    public TournamentProperties() {
        startDate = new Date();
        endDate = new Date();
        
        name = "";
        location = "";
        comment = "";
        
        ruleset = Ruleset.JAPANESE;
        
        basicTime = 0;
        timeSystem = TimeSystem.CANADIAN;
        timeSystemProperties = new HashMap<>();
    }
    
    
    
    public Date getStartDate() { return startDate; }
    public String getStartDateString() { return new SimpleDateFormat("yyyy-MM-dd").format(startDate); }
    public void setStartDate( Date date ) { startDate = date; }
    public void setStartDate( String dateString ) {
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd").parse( dateString );
        } catch ( ParseException ex ) {
            startDate = new Date();
        }
    }
            
    public Date getEndDate() { return endDate; }
    public String getEndDateString() { return new SimpleDateFormat("yyyy-MM-dd").format(endDate); }
    public void setEndDate( Date date ) { endDate = date; }
    public void setEndDate( String dateString ) {
        try {
            endDate = new SimpleDateFormat("yyyy-MM-dd").parse( dateString );
        } catch ( ParseException ex ) {
            endDate = new Date();
        }
    }
 
    public String getName() { return name; }
    public void setName( String newName ) { name = newName; }
    
    public String getLocation() { return location; }
    public void setLocation( String newLocation ) { location = newLocation; }
    
    public String getComment() { return comment; }
    public void setComment( String newComment ) { comment = newComment; }
    
    public Ruleset getRuleset() { return ruleset; }
    public void setRuleset( Ruleset newRuleset ) { ruleset = newRuleset; }
    
    public int getBasicTime() { return basicTime; }
    public void setBasicTime( int time ) { basicTime = time; }
    public void setBasicTime( String timeString ) {
        int newTime = basicTime;
        try {
            newTime = Integer.parseInt( timeString );
        } catch ( NumberFormatException ex ) {
        }
        
        basicTime = newTime;
    }
    
    public TimeSystem getTimeSystem() { return timeSystem; }
    public void setTimeSystem( TimeSystem newTimeSystem ) {
        if ( timeSystem != newTimeSystem ) {
            timeSystem = newTimeSystem;
            timeSystemProperties = new HashMap<>();
        }
    }
            
    public HashMap<String, String> getTimeSystemProperties() { return timeSystemProperties; }
    public void setTimeSystemProperty( String key, String value ) { timeSystemProperties.put( key, value ); }
    
}
