package org.seattlego.eggplant.model;

/**
 *
 * @author Topsy
 */
public enum PlacementCriterion {
    NULL("NULL", "NULL", "No tie break", 1),
    CATEGORY("CAT", "CAT", "Category", -1),
    
    RANK("Rank", "Rank", "Rank from 30K to 9D", 1),
    RATING("Rating", "Rating", "Rating from -30.999 to 9.999", 1),
    
    NBW("NBW", "NBW", "Number of Wins", 2),
    MMS("MMS", "MMS", "McMahon Score", 2),
    
    SOSW("SOS", "SOSW", "Sum of Opponents Scores (NBW)", 2),
    SOSWM1("SOS-1", "SOSW-1", "Sum of (n-1) Best Opponents Scores (NBW)", 2),
    SOSWM2("SOS-2", "SOSW-2", "Sum of (n-2) Best Opponents Scores (NBW)", 2),
    SODOSW("SODOS", "SODOSW", "Sum of Defeated Opponents Scores (NBW)", 4),
    SOSOSW("SOSOS", "SOSOSW", "Sum of Opponents SOSW", 2),
    CUSSW("CUSS", "CUSSW", "Cumulative Sum of Scores (NBW)", 2),
    
    SOSM("SOS", "SOSM", "Sum of Opponents Scores (MMS)", 2),
    SOSMM1( "SOS-1", "SOSM-1", "Sum of (n-1) Best Opponents Scores (MMS)", 2),
    SOSMM2( "SOS-2", "SOSM-2", "Sum of (n-2) Best Opponents Scores (MMS)", 2),
    SODOSM("SODOS", "SODOSM", "Sum of Defeated Opponents Scores (MMS)", 4),
    SOSOSM( "SOSOS", "SOSOSM", "Sum of Opponents SOSM", 2),
    CUSSM( "CUSS", "CUSSM", "Cumulative Sum of Scores (MMS)", 2),
    
    // TODO - Rename these, remove these?
    EXT("EXT", "EXT", "Exploits Tentes - weighted sum of opponents scores (NBW)", 2),
    EXR("EXR", "EXR", "Exploits Reussis - weighted sum of defeated opponents scores (NBW)", 2),
    
    DC("DC", "DC", "Direct Confrontation", 1),
    SDC("SDC", "SDC", "Simplified Direct Confrontation", 1);

    
    public String shortName;
    public String longName;
    public String description;
    public int coef;        // coef used for internal computations. Usually -1, 1, 2 or 4
                            // used at display time for division before displaying
    
    private PlacementCriterion(String shortName, String longName, String description, int coef){
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
        this.coef = coef;
    }
    

}
