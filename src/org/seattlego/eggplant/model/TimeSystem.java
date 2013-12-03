/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.seattlego.eggplant.model;

/**
 *
 * @author Topsy
 */
public enum TimeSystem {
    CANADIAN ("Canadian"),
    BYOYOMI ("ByoYomi"),
    FISCHER ("Fischer"),
    SUDDEN_DEATH ("SuddenDeath");
    
   private TimeSystem( final String text ) {
        this.text = text;
    }

    private final String text;

    @Override
    public String toString() {
        return text;
    }    
    
    /*
     * Cannot use valueOf for this enum because the encoded string values must
     * be different from the enum values.
     */
    public static TimeSystem getTimeSystemFromString( String timeString ) {
        for ( TimeSystem item : TimeSystem.values() ) {
            if ( item.name().equals( timeString ) ) {
                return item;
            }
            if ( item.toString().equals( timeString ) ) {
                return item;
            }
        }
        return null;
    }
}
