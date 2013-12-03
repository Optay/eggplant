package org.seattlego.eggplant.model;

/**
 * Note that the values associated with win/loss are x2 values that are directly
 * added to stored score metrics without any scaling required.
 * 
 * @author Topsy
 */
public enum PlayerResult {
    WIN     (2),
    LOSE    (0),
    DRAW    (1),
    UNKNOWN (0);
    
    private final int value;
    
    private PlayerResult( int value ) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}
