package org.seattlego.eggplant.model;

/**
 * This class is for use in the results table which displays winner's names
 * differently. It contains a player's name and a boolean. This encapsulation
 * keeps the results table simple and avoids cryptic dependencies.
 * 
 * @author Topsy
 */
public class FlaggedPlayerName {
    public String playerName;
    public boolean isWinner;
    
    public FlaggedPlayerName( String name, boolean won ) {
        playerName = name;
        isWinner = won;
    }
}
