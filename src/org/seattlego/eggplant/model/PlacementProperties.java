package org.seattlego.eggplant.model;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Properties that affect the scoring and placement of players in a tournament.
 * 
 * @author Topsy
 */
public class PlacementProperties {
    
    public static final int MAX_CRITERIA = 6;
    


    private PlacementCriterion[] plaCriteria;

    
    private int genNbw2ValueAbsent    = 0;  // 2 * Number of NBW points for a player absent of a round
    private int genNbw2ValueBye = 1;        // 2 * Number of NBW points for a player not paired in a round (uneven)

    private int genMms2ValueAbsent = 0;     // 2 * Number of MMS points for a player absent of a round
    private int genMms2ValueBye = 1;        // 2 * Number of MMS points for a player not paired in a round   
    
    public PlacementProperties() {
        plaCriteria = new PlacementCriterion[ MAX_CRITERIA ];
        Arrays.fill( plaCriteria, PlacementCriterion.NULL );
        
    }
    
    public int getGenNbw2ValueAbsent() { return genNbw2ValueAbsent; }
    public void setGenNbw2ValueAbsent(int genNbw2ValueAbsent) { this.genNbw2ValueAbsent = genNbw2ValueAbsent; }
    public void setGenNbw2ValueAbsent( String value ) {
        try {
            this.genNbw2ValueAbsent = Integer.parseInt( value );
        } catch ( NumberFormatException ex ) {
            Logger.getLogger( PlacementProperties.class.getName() ).log( Level.SEVERE, "Invalid value in save file." );
        }
        
    }

    public int getGenNbw2ValueBye() { return genNbw2ValueBye; }
    public void setGenNbw2ValueBye(int genNbw2ValueBye) { this.genNbw2ValueBye = genNbw2ValueBye; }
    public void setGenNbw2ValueBye( String value ) {
        try {
            this.genNbw2ValueBye = Integer.parseInt( value );
        } catch ( NumberFormatException ex ) {
            Logger.getLogger( PlacementProperties.class.getName() ).log( Level.SEVERE, "Invalid value in save file." );
        }
        
    }

    public int getGenMms2ValueAbsent() { return genMms2ValueAbsent; }
    public void setGenMms2ValueAbsent(int genMms2ValueAbsent) { this.genMms2ValueAbsent = genMms2ValueAbsent; }
    public void setGenMms2ValueAbsent( String value ) {
        try {
            this.genMms2ValueAbsent = Integer.parseInt( value );
        } catch ( NumberFormatException ex ) {
            Logger.getLogger( PlacementProperties.class.getName() ).log( Level.SEVERE, "Invalid value in save file." );
        }
        
    }

    public int getGenMms2ValueBye() { return genMms2ValueBye; }
    public void setGenMms2ValueBye(int genMms2ValueBye) { this.genMms2ValueBye = genMms2ValueBye; }    
    public void setGenMms2ValueBye( String value ) {
        try {
            this.genMms2ValueBye = Integer.parseInt( value );
        } catch ( NumberFormatException ex ) {
            Logger.getLogger( PlacementProperties.class.getName() ).log( Level.SEVERE, "Invalid value in save file." );
        }
        
    }
    
    
    
    public void initForMM(){
        Arrays.fill( plaCriteria, PlacementCriterion.NULL );
        
        plaCriteria[0] = PlacementCriterion.MMS;
        plaCriteria[1] = PlacementCriterion.SOSM;
        plaCriteria[2] = PlacementCriterion.SOSOSM;
        
    }
    
    public void initForSwiss(){
        Arrays.fill( plaCriteria, PlacementCriterion.NULL );
        
        plaCriteria[0] = PlacementCriterion.NBW;
        plaCriteria[1] = PlacementCriterion.SOSW;
        plaCriteria[2] = PlacementCriterion.SOSOSW;
        
    }
    
    public void initForSwissCat(){
        Arrays.fill( plaCriteria, PlacementCriterion.NULL );
        
        plaCriteria[0] = PlacementCriterion.CATEGORY;
        plaCriteria[1] = PlacementCriterion.NBW;
        plaCriteria[2] = PlacementCriterion.EXT;
        plaCriteria[3] = PlacementCriterion.EXR;
    }
    
    public String checkCriteriaCoherence(javax.swing.JFrame jfr){
        // DIR Coherence
        boolean bOK = true;
        String strMes = "Your criteria look strange :";
        List<PlacementCriterion> criteria = Arrays.asList( this.getPlaCriteria() );
        
        // 1st coherence test : DC or SDC should not appear twice
        if ( criteria.contains( PlacementCriterion.DC ) &&
             criteria.contains( PlacementCriterion.SDC ) ) {
            strMes += "\nOnly one Direct Confrontation criteria (DC or SDC) should appear";
            bOK = false;
        }
        // 2nd coherence test : Criteria should not mix elements from McMahon group with elements from Swiss group
        int nbSWCriteria = 0;
        int nbMMCriteria = 0;
        for (int i = 0; i < criteria.size(); i++){
            switch( criteria.get(i) ){
                case CATEGORY:
                case NBW:
                case SOSW:
                case SOSWM1:
                case SOSWM2:
                case SODOSW:
                case SOSOSW:
                case CUSSW:
                case EXR:
                case EXT:
                    nbSWCriteria++;
                    break;
                case MMS:
                case SOSM:
                case SOSMM1:
                case SOSMM2:
                case SODOSM:
                case SOSOSM:
                case CUSSM:
                    nbMMCriteria++;
                    break;
            } 
        }
        if (nbSWCriteria > 0 && nbMMCriteria > 0){
            strMes += "\nMcMahon and Swiss Criteria mixed";
            bOK = false;
        }

        // 3rd test : SODOSM is taboo
        if ( criteria.contains( PlacementCriterion.SODOSM ) ){
            strMes += "\nSODOSM is not recommended";
            bOK = false;
        }

        if (bOK) return "";
        else return strMes;
        
    }
    
    public PlacementCriterion[] getPlaCriteria() {
        return plaCriteria;
        /*
         * Returns a copy instead of a reference. Probably not necessary.
        PlacementCriterion[] plaC = new PlacementCriterion[plaCriteria.length];
        System.arraycopy( plaCriteria, 0, plaC, 0, plaCriteria.length );
        return plaC;
        * 
        */
    }

    public void setPlaCriterion( int index, PlacementCriterion plaCriteron ) {
        if ( ( index >= 0 ) && ( index < plaCriteria.length ) ) {
            plaCriteria[index] = plaCriteron;
        }
    }
    
    public PlacementCriterion mainCriterion(){
        PlacementCriterion defaultCriterion = PlacementCriterion.NBW;
        
        List<PlacementCriterion> crit = Arrays.asList( getPlaCriteria() );
        if ( crit.contains( PlacementCriterion.NBW ) ) {
            return PlacementCriterion.NBW;
        }
        if ( crit.contains( PlacementCriterion.MMS ) ) {
            return PlacementCriterion.MMS;
        }
        return defaultCriterion;
    }
}
    

