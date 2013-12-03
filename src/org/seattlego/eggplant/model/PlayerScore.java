package org.seattlego.eggplant.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Player score properties.
 * 
 * @author Topsy
 */
public class PlayerScore {

    private List< Map<PlacementCriterion, Integer> > scores;
    
    
    

/*
 * These values are all in the scores list map.
    // First level scores
    private int[] nbwX2;       // number of wins * 2
    private int[] mmsX2;      // macmahon score * 2
    
    // Second level scores
    private int[] cuswX2;      // Sum of successive nbw2
    private int[] cusmX2;      // Sum of successive mms2
    private int[] soswX2;      // Sum of Opponents nbw2
    private int[] soswM1X2;    // Sum of (n-1) Opponents nbw2
    private int[] soswM2X2;    // Sum of (n-2) Opponents nbw2
    private int[] sdswX4;      // Sum of Defeated Opponents nbw2 X2
    private int[] sosmX2;      // Sum of Opponents mms2
    private int[] sosmM1X2;    // Sum of (n-1) Opponents mms2
    private int[] sosmM2X2;    // Sum of (n-2) Opponents mms2
    private int[] sdsmX4;      // Sum of Defeated Opponents mms2 X2
    private int[] extX2;      // Exploits tentes (based on nbw2, with a weight factor)
    private int[] exrX2;      // Exploits reussis(based on nbw2, with a weight factor)
    
    // Third level scores
    private int[] ssswX2;      // Sum of opponents sosw2 * 2
    private int[] sssmX2;      // Sum of opponents sosm2 * 2
    */
    
    private int initialMms;
    

    // Pairing informations. Unlike preceeding data, these informations are computed for one round only : the current one
    public int numberOfGroups;      // Very redundant
    public int groupNumber;         //
    public int groupSize;           // Redundant
    public int innerPlacement;      // placement in homogeneous group (category and mainScore) beteen 0 and size(group) - 1
    public int nbDU;                // Number of Draw-ups
    public int nbDD;                // Number of Draw-downs
    
    private Player player;

    
    
    public PlayerScore() {
    }

    public PlayerScore( Player p ) {
        this.player = p;
        
        initialMms = 0;
        

//        gameArray = new Game[numberOfRounds];
        /*
        nbwX2  = new int[numberOfRounds];
        mmsX2  = new int[numberOfRounds];

        cuswX2 = new int[numberOfRounds];
        cusmX2 = new int[numberOfRounds];

        soswX2 = new int[numberOfRounds];
        soswM1X2 = new int[numberOfRounds];
        soswM2X2 = new int[numberOfRounds];

        sdswX4 = new int[numberOfRounds];

        sosmX2 = new int[numberOfRounds];
        sosmM1X2 = new int[numberOfRounds];
        sosmM2X2 = new int[numberOfRounds];

        sdsmX4 = new int[numberOfRounds];

        extX2  = new int[numberOfRounds];
        exrX2  = new int[numberOfRounds];

        ssswX2 = new int[numberOfRounds];
        sssmX2 = new int[numberOfRounds];
        * 
        * 
        */

        scores = new ArrayList<>();
        
            
            //gameArray[r] = null;
            /*
            nbwX2[r] = 0;
            mmsX2[r] = 0;

            cuswX2[r] = 0;
            cusmX2[r] = 0;

            soswX2[r] = 0;
            soswM1X2[r] = 0;
            soswM2X2[r] = 0;

            sosmX2[r] = 0;
            sosmM1X2[r] = 0;
            sosmM2X2[r] = 0;

            extX2[r] = 0;
            exrX2[r] = 0;

            ssswX2[r] = 0;
            sssmX2[r] = 0;
            * 
            */
        
    }
    
    private void validateRoundIndex( int roundIndex ) {
        if ( roundIndex < 0 ) { roundIndex = 0; }
        
        if ( roundIndex > (scores.size() - 1) ) {
            scores.add( roundIndex, new EnumMap<PlacementCriterion, Integer>( PlacementCriterion.class ) );
        }
        
    }
    
    /*
     * Set integer metric value. Any coefficient should already be applied 
     * when using this method.
     */
    public void setMetric( int roundIndex, PlacementCriterion criterion, int score ) {
        validateRoundIndex( roundIndex );
        
        scores.get( roundIndex ).put( criterion, score );
    }
    
    
    /*
     * Returns double metric value with coefficient removed.
     */
    public double getMetricUnscaled( int roundIndex, PlacementCriterion criterion ) {
        int metricScaled = getMetric( roundIndex, criterion );
        return (double) metricScaled / (double) criterion.coef;
    }
    
    /*
     * Returns integer metric value with coefficient applied.
     */
    public int getMetric( int roundIndex, PlacementCriterion criterion ) {
        
        // The only metric that is defined before the first round.
        if ( roundIndex < 0 ) {
            if ( criterion == PlacementCriterion.MMS ) {
                return initialMms;
            } else {
                return 0;
            }
        }
        //
        
        validateRoundIndex( roundIndex );
        
        Integer value = scores.get( roundIndex ).get( criterion );
        if ( value == null) { return 0; }
        else { return value; }
    }
    
    /*
     * Returns metric value as string without coefficient applied.
     * Formats fractional values as: ½ ¼ ¾
     */
    public String getMetricString( int roundIndex, PlacementCriterion criterion ){
        int rawValue = this.getMetric( roundIndex, criterion );
        int coefficient = criterion.coef;
        
        /*
        //categories are not used in eggplant, and this is a funny use of coefficient. REMOVE!
        if (coefficient == -1)   // only Category
            return "" + (- rawValue + 1);
        //
        * 
        */

        int processedValue = rawValue / coefficient;
        int remainder = rawValue % coefficient;
        String valueString = "" + processedValue;
        String fractionString = "";

        if (coefficient == 2) {
            if (remainder == 1) fractionString = "½";
        }
        if (coefficient == 4) {
            if (remainder == 1) fractionString = "¼";
            else if (remainder == 2) fractionString = "½";
            else if (remainder == 3) fractionString = "¾";
        }
        return valueString + fractionString;
    }
    
    public void incrementMetric( int roundIndex, PlacementCriterion criterion, int amount ) {
        scores.get( roundIndex ).put( criterion, scores.get( roundIndex ).get(criterion) + amount );
    }
    

    // TODO - Are we going to have to handle non-integer MMS values at some point?
    public int getInitialMms() { return initialMms / PlacementCriterion.MMS.coef; }
    public void setInitialMms( int newMms ) { initialMms = newMms * PlacementCriterion.MMS.coef; }
    public void setInitialMms( String mmsString ) { 
        int newMms;
        
        try {
            newMms = Integer.parseInt( mmsString );
        } catch( NumberFormatException ex ) {
            newMms = 0;
        }
        initialMms = newMms * PlacementCriterion.MMS.coef;
    }
    //
    
    
    
    /*
    private boolean isValidRoundNumber(int rn){
        if (rn < 0 || rn > participation.length){
            System.out.println("rn = " + rn + ". Incorrect value.");
            return false;
        }
        else return true;
    }*/

    /*
    public Game getGame(int rn){
        if (isValidRoundNumber(rn)) return gameArray[rn];
        else return null;
    }
    public void setGame(int rn, Game g){
        if (isValidRoundNumber(rn)) gameArray[rn] = g;
    }
    * 
    */
    
    
    /*
     * Replaced with generic score collection.
     * Make sure all values are set correctly in Scorer before deleting this block.
     
     
    public int getNBWX2(int rn){
        if (isValidRoundNumber(rn)) return nbwX2[rn];
        else return 0;
    }
    public void setNBWX2(int rn, int value){
        if (isValidRoundNumber(rn)) nbwX2[rn] = value;
    }
    public int getMMSX2(int rn){
        if (isValidRoundNumber(rn)) return mmsX2[rn];
        else return 0;
    }
    public void setMMSX2(int rn, int value){
        if (isValidRoundNumber(rn)) mmsX2[rn] = value;
    }
    public int getCUSWX2(int rn){
        if (isValidRoundNumber(rn)) return cuswX2[rn];
        else return 0;
    }
    public void setCUSWX2(int rn, int value){
        if (isValidRoundNumber(rn)) cuswX2[rn] = value;
    }
    public int getCUSMX2(int rn){
        if (isValidRoundNumber(rn)) return cusmX2[rn];
        else return 0;
    }
    public void setCUSMX2(int rn, int value){
        if (isValidRoundNumber(rn)) cusmX2[rn] = value;
    }
    public int getSOSWX2(int rn){
        if (isValidRoundNumber(rn)) return soswX2[rn];
        else return 0;
    }
    public void setSOSWX2(int rn, int value){
        if (isValidRoundNumber(rn)) soswX2[rn] = value;
    }
    public int getSOSWM1X2(int rn){
        if (isValidRoundNumber(rn)) return soswM1X2[rn];
        else return 0;
    }
    public void setSOSWM1X2(int rn, int value){
        if (isValidRoundNumber(rn)) soswM1X2[rn] = value;
    }
    public int getSOSWM2X2(int rn){
        if (isValidRoundNumber(rn)) return soswM2X2[rn];
        else return 0;
    }
    public void setSOSWM2X2(int rn, int value){
        if (isValidRoundNumber(rn)) soswM2X2[rn] = value;
    }
    public int getSDSWX4(int rn){
        if (isValidRoundNumber(rn)) return sdswX4[rn];
        else return 0;
    }
    public void setSDSWX4(int rn, int value){
        if (isValidRoundNumber(rn)) sdswX4[rn] = value;
    }
    public int getSOSMX2(int rn){
        if (isValidRoundNumber(rn)) return sosmX2[rn];
        else return 0;
    }
    public void setSOSMX2(int rn, int value){
        if (isValidRoundNumber(rn)) sosmX2[rn] = value;
    }
    public int getSOSMM1X2(int rn){
        if (isValidRoundNumber(rn)) return sosmM1X2[rn];
        else return 0;
    }
    public void setSOSMM1X2(int rn, int value){
        if (isValidRoundNumber(rn)) sosmM1X2[rn] = value;
    }
    public int getSOSMM2X2(int rn){
        if (isValidRoundNumber(rn)) return sosmM2X2[rn];
        else return 0;
    }
    public void setSOSMM2X2(int rn, int value){
        if (isValidRoundNumber(rn)) sosmM2X2[rn] = value;
    }
    public int getSDSMX4(int rn){
        if (isValidRoundNumber(rn)) return sdsmX4[rn];
        else return 0;
    }
    public void setSDSMX4(int rn, int value){
        if (isValidRoundNumber(rn)) sdsmX4[rn] = value;
    }
    public int getEXTX2(int rn){
        if (isValidRoundNumber(rn)) return extX2[rn];
        else return 0;
    }
    public void setEXTX2(int rn, int value){
        if (isValidRoundNumber(rn)) extX2[rn] = value;
    }
    public int getEXRX2(int rn){
        if (isValidRoundNumber(rn)) return exrX2[rn];
        else return 0;
    }
    public void setEXRX2(int rn, int value){
        if (isValidRoundNumber(rn)) exrX2[rn] = value;
    }
    public int getSSSWX2(int rn){
        if (isValidRoundNumber(rn)) return ssswX2[rn];
        else return 0;
    }
    public void setSSSWX2(int rn, int value){
        if (isValidRoundNumber(rn)) ssswX2[rn] = value;
    }
    public int getSSSMX2(int rn){
        if (isValidRoundNumber(rn)) return sssmX2[rn];
        else return 0;
    }
    public void setSSSMX2(int rn, int value){
        if (isValidRoundNumber(rn)) sssmX2[rn] = value;
    }
    public int getDC(){
        return dc;
    }
    public void setDC(int value){
        dc = value;
    }
    public int getSDC(){
        return sdc;
    }
    public void setSDC(int value){
        sdc = value;
    }
     
    
    private int[] getSdswX4() {
        return sdswX4;
    }

    private int[] getSdsmX4() {
        return sdsmX4;
    }
    

    public int getCritValue( PlacementCriterion criterion, int rn ){
        switch( criterion ){
            case NULL    : return 0;                      // Null criterion
            case CATEGORY    : return  - player.category(generalParameterSet);// Category
            case RANK   : return  player.getRank();      // Rank
            case RATING : return  player.getRating();    // Rating
            case NBW    : return  (rn >= 0) ? nbwX2[rn] : 0;                     // Number of Wins
            case MMS    : return  (rn >= 0) ? mmsX2[rn] : 2 * player.smms(generalParameterSet);  // McMahon score

            case SOSW   : return  (rn >= 0) ? this.soswX2[rn] : 0;	// Sum of Opponents McMahon scores
            case SOSWM1 : return  (rn >= 0) ? this.soswM1X2[rn] : 0;
            case SOSWM2 : return  (rn >= 0) ? this.soswM2X2[rn] : 0;
            case SODOSW : return  (rn >= 0) ? this.getSdswX4()[rn] : 0;	// Sum of Defeated Opponents Scores
            case SOSOSW : return  (rn >= 0) ? this.ssswX2[rn] : 0;	// Sum of opponents SOS
            case CUSSW  : return  (rn >= 0) ? this.cuswX2[rn] : 0;	// Cuss

            case PlacementParameterSet.PLA_CRIT_SOSM   : return  (rn >= 0) ? this.sosmX2[rn] : 0;	// Sum of Opponents McMahon scores
            case PlacementParameterSet.PLA_CRIT_SOSMM1 : return  (rn >= 0) ? this.sosmM1X2[rn] : 0;
            case PlacementParameterSet.PLA_CRIT_SOSMM2 : return  (rn >= 0) ? this.sosmM2X2[rn] : 0;
            case PlacementParameterSet.PLA_CRIT_SODOSM : return  (rn >= 0) ? this.getSdsmX4()[rn] : 0;	// Sum of Defeated Opponents Scores
            case PlacementParameterSet.PLA_CRIT_SOSOSM : return  (rn >= 0) ? this.sssmX2[rn] : 0;	// Sum of opponents SOS
            case PlacementParameterSet.PLA_CRIT_CUSSM  : return  (rn >= 0) ? this.cusmX2[rn] : 0;	// Cuss

            case PlacementParameterSet.PLA_CRIT_EXT    : return  (rn >= 0) ? this.extX2[rn] : 0;       // Exploits tentes
            case PlacementParameterSet.PLA_CRIT_EXR    : return  (rn >= 0) ? this.exrX2[rn] : 0;       // Exploits reussis


            case PlacementParameterSet.PLA_CRIT_DC      : return dc;
            case PlacementParameterSet.PLA_CRIT_SDC     : return sdc;

            default :
            return 0;
        }
    }
    * 
    */


    /**
     * Generate strings with a "oooortch" format
     * oooo being opponent number,
     * r being the result "+", "-", "=" or "?"
     * t (type) is either "/" for normal results or "!" for by default results
     * c being the colour, "w", "b" or "?"
     * h being handicap, "0" ... "9"
     * @param tps Tournament parameter set. useful for placement criteria and for absent and values scores
     */
    /* TODO - Fix this when it is needed.
    public static String[][] halfGamesStrings(ArrayList<Player> alOrderedScoredPlayers, int roundNumber, TournamentParameterSet tps) {
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        // Prepare hmPos for fast retrieving
        HashMap<String, Integer> hmPos = new HashMap<String, Integer>();
        for (int i = 0; i < alOrderedScoredPlayers.size(); i++){
            hmPos.put(alOrderedScoredPlayers.get(i).getKeyString(), i);
        }
        String[][] hG = new String[roundNumber +1][alOrderedScoredPlayers.size()];
        for (int i = 0; i < alOrderedScoredPlayers.size(); i++){
            Player p = alOrderedScoredPlayers.get(i);
            for(int r = 0; r <= roundNumber; r++){
                String strOpp = "   0";
                String strRes = " ";
                String strTyp = "/";
                String strCol = " ";
                String strHd  = "0";

                Game g = p.playerScoring.gameArray[r];
                if (g == null){
                    strOpp = "   0";
                    strCol = " ";
                    strTyp = " ";
                    strHd  = " ";
                    if (p.playerScoring.participation[r] == PlayerScoring.NOT_ASSIGNED) strRes = "-";
                    else{
                        int res = 0;
                        if (p.playerScoring.participation[r] == PlayerScoring.ABSENT)
                            if (tps.tournamentType() == TournamentParameterSet.TYPE_MACMAHON) res = gps.getGenMMS2ValueAbsent();
                            else res = gps.getGenNBW2ValueAbsent();
                        else if (p.playerScoring.participation[r] == PlayerScoring.BYE)
                            if (tps.tournamentType() == TournamentParameterSet.TYPE_MACMAHON) res = gps.getGenMMS2ValueBye();
                            else res = gps.getGenNBW2ValueBye();
                        if (res == 2) strRes = "+";
                        else if (res == 1) strRes = "=";
                        else strRes = "-";
                    }
                }
                else{   //Real Game
                   Player opp = null;
                   int result = g.getResult();
                   if (result == Game.RESULT_UNKNOWN) strTyp = "/";
                   else strTyp = (result >= Game.RESULT_BYDEF) ? "!" : "/";
                   int res = result;
                   if (result >= Game.RESULT_BYDEF) res = result - Game.RESULT_BYDEF;
                   if (g.getWhitePlayer().hasSameKeyString(p)){
                       opp = g.getBlackPlayer();
                       strCol = "w";
                       if (res == Game.RESULT_WHITEWINS || res == Game.RESULT_BOTHWIN) strRes = "+";
                       else if (res == Game.RESULT_BLACKWINS || res == Game.RESULT_BOTHLOSE) strRes = "-";
                       else if (res == Game.RESULT_EQUAL) strRes = "=";
                       else strRes = "?";
                   }
                   else{
                       opp = g.getWhitePlayer();
                       strCol = "b";
                       if (res == Game.RESULT_BLACKWINS || res == Game.RESULT_BOTHWIN) strRes = "+";
                       else if (res == Game.RESULT_WHITEWINS || res == Game.RESULT_BOTHLOSE) strRes = "-";
                       else if (res == Game.RESULT_EQUAL) strRes = "=";
                       else strRes = "?" ;

                   }
                   if (!g.isKnownColor()) strCol = "?";

                   int oppNum = hmPos.get(opp.getKeyString());
                   strOpp = "    " + (oppNum +1);
                   strOpp = strOpp.substring(strOpp.length() - 4);  // To have 4 chars exactly
                   strHd = "" + g.getHandicap();
                }
                hG[r][i] = strOpp + strRes + strTyp + strCol + strHd;

            }
        }
        return hG;
    }*/

    /**
     * Generate an array of strings representing placement between 1 and number of players.
     * Basically placement is the position in alOrderedScoredPlayers + 1.
     * Except for ex-aequos
     */
    /* TODO - Fix this when it is used.
    public static String[] positionStrings(ArrayList<Player> alOrderedScoredPlayers, int roundNumber, TournamentParameterSet tps) {
        PlacementParameterSet pps = tps.getPlacementParameterSet();
        ScoredPlayerComparator spc = new ScoredPlayerComparator(pps.getPlaCriteria(), roundNumber, true);
        int[] place = new int[alOrderedScoredPlayers.size()];
        if (place.length > 0) place[0] = 0;
        for (int i = 1; i < alOrderedScoredPlayers.size(); i++){
            if (spc.compare(alOrderedScoredPlayers.get(i), alOrderedScoredPlayers.get(i-1)) == 0) place[i] = place[i-1];
            else place[i] = i;
        }
        String[] strPlace = new String[alOrderedScoredPlayers.size()];
        for (int i = 0; i < alOrderedScoredPlayers.size(); i++){
            if ( i > 0 && place[i] == place[i-1] ) strPlace[i] = "    ";
            else strPlace[i] = "    " + (place[i] + 1);
            strPlace[i] = strPlace[i].substring(strPlace[i].length() - 4);
        }
        return strPlace;
    }
    * 
    */

    /**
     * Generate a array of strings representing placement inside category between 1 and number of players.
     * Basically placement is the position in alOrderedScoredPlayers + 1.
     * Except for ex-aequos
     */
    /* TODO - Fix this when it is used.
    public static String[] catPositionStrings(ArrayList<Player> alOrderedScoredPlayers, int roundNumber, TournamentParameterSet tps) {
        PlacementParameterSet pps = tps.getPlacementParameterSet();
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        ScoredPlayerComparator spc = new ScoredPlayerComparator(pps.getPlaCriteria(), roundNumber, true);
        int[] place = new int[alOrderedScoredPlayers.size()];
        if (place.length > 0) place[0] = 0;
        int curCat = 0;
        int nbPlayersBeforeCurCat = 0;
        for (int i = 1; i < alOrderedScoredPlayers.size(); i++){
            int newCat = alOrderedScoredPlayers.get(i).category(gps);
            if (newCat != curCat){
                curCat = newCat;
                nbPlayersBeforeCurCat = i;
                place[i] = 0;
            }
            if (spc.compare(alOrderedScoredPlayers.get(i), alOrderedScoredPlayers.get(i-1)) == 0) place[i] = place[i-1];
            else place[i] = i - nbPlayersBeforeCurCat;
        }
        String[] strPlace = new String[alOrderedScoredPlayers.size()];
        for (int i = 0; i < alOrderedScoredPlayers.size(); i++){
            if (i > 0 && alOrderedScoredPlayers.get(i).category(gps) != alOrderedScoredPlayers.get(i-1).category(gps) )
                strPlace[i] = "    " + (place[i] + 1);
            else if (i > 0 && place[i] == place[i-1] ) strPlace[i] = "    ";
            else strPlace[i] = "    " + (place[i] + 1);
            strPlace[i] = strPlace[i].substring(strPlace[i].length() - 4);
        }
        return strPlace;
    }
    * 
    */


}
