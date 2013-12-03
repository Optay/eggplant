package org.seattlego.eggplant.model;

/**
 * Tournament settings that affect pairing. This is separate from other 
 * tournament properties to more easily support different pairing systems 
 * (Swiss, McMahon, and the like).
 * 
 * TODO - some of these variable names need work
 * 
 * @author Topsy
 */
public class PairingProperties {
    private final int MAX_NUMBER_OF_ROUNDS = 10;
    private final int DEFAULT_NUMBER_OF_ROUNDS = 3;
    
    private final int DEFAULT_KOMI = 6;
    
    private int numberOfRounds;
    private Rank MMFloor;
    private Rank MMBar;
    private PairingSystem pairingSystem;
    private boolean handicapGames;

    // Standard NX1 factor ( = Rather N X 1 than 1 X N)
    // This is used for weighting score and geographic/club costs.
    private double paiStandardNX1Factor = 0.5;
    
    
    /**
     * Max value for dupliate game cost.
     * <br> In order to be compatible with max value of long (8 * 10^18),
     * with max number of games (8000),
     * with relative weight of this parameter (1/2)
     * MAX_DUPLICATE_GAME_COST should be strictly limited to 5 * 10^14
     **/ 
    static final long MAX_DUPLICATE_GAME_COST = 500000000000000L;   // 5 * 10^14
    /**
     * Max value for random cost.
     * <br> Due to internal coding,
     * MAX_RANDOM_COST should be strictly limited to 2 * 10^9
     **/
    static final long MAX_RANDOM_COST        =       1000000000L;   // 10^9
    static final long MAX_COLOR_BALANCE_COST     =          1000000L;   // 10^6

    
    private long duplicateGameCost = MAX_DUPLICATE_GAME_COST;    
    private long randomCost = 0;
    private boolean isDeterministic = true;
    private long colorBalanceCost = MAX_COLOR_BALANCE_COST;
    
    static final long MAX_CATEGORY_COST           =   20000000000000L;  // 2. 10^13
    // Ratio between MAX_SCORE_DIFFERENCE_COST and MAX_CATEGORY_COST should stay below 1/ nbcat^2
    static final long MAX_SCORE_DIFFERENCE_COST         =     100000000000L;  // 10^11
    static final long MAX_DUDD_WEIGHT                       =     MAX_SCORE_DIFFERENCE_COST / 1000;  // Draw-ups Draw-downs
    
    // Enum?
    static final int  DUDD_TOP              = 1;
    static final int  DUDD_MID              = 2;
    static final int  DUDD_BOT              = 3;
    //
    
    static final long MAX_MAXIMIZE_SEEDING_COST                  =         MAX_SCORE_DIFFERENCE_COST / 20000;
    
    private long categoryCost             =     MAX_CATEGORY_COST;

    private long scoreDifferenceCost           =      MAX_SCORE_DIFFERENCE_COST;

    private long duddWeight                        =          MAX_DUDD_WEIGHT;
    private int duddUpperMode          = DUDD_MID;
    private int duddLowerMode          = DUDD_MID;
    
    private long maximizeSeedingCost                   =         MAX_MAXIMIZE_SEEDING_COST;      // 5 *10^6
    private int lastRoundForPairingScheme1            = 1;
    
    private PairingMethod pairingScheme1                = PairingMethod.SPLIT_AND_RANDOM;
    private PairingMethod pairingScheme2                = PairingMethod.SPLIT_AND_FOLD;
    
    /*
     * TODO - Fix when use is added.
    private int paiMaAdditionalPlacementCritSystem1     = PlacementProperties.PLA_CRIT_RATING;
    private int paiMaAdditionalPlacementCritSystem2     = PlacementProperties.PLA_CRIT_NUL;
      */
    
    // Make this a rank object?
    private int secondaryCriteriaRankThreshold                           = 0;           // Do not apply secondary criteria above 1D rank
    
    private boolean secondaryCriteriaWinThresholdActive               = true;        // Do not apply secondary criteria when nbWins >= nbRounds / 2
    private long paiSeDefSecCrit;                            // Should be MAX_SCORE_DIFFERENCE_COST for MM, MAX_CATEGORY_COST for others
    private long minimizeHandicapCost;                      // Should be paiSeDefSecCrit for SwCat, 0 for others
    private long geoCost;                          // Should be paiSeDefSecCrit for SwCat and MM, 0 for Swiss
    private int  preferMmsDiffRatherThanSameCountry;       // Typically = 1
    private int  preferMmsDiffRatherThanSameClub;          // Typically = 3
         
    
    // Handicap
    private HandicapBasis handicapBasis = HandicapBasis.RANK;
    
    /**
     * When one player in the game has a rank of at least hdNoHdRankThreshold,
     * then the game will be without handicap
     */
    //private int maxHandicappedRank = 0;
    private Rank maxHandicappedRank = new Rank("1d");
    
    /**
     * Handicap will be decreased by handicap modifier
     * Possible values are 0, 1, 2 or 3
     */
    private int handicapModifier = 1;
    
    /**
     * Handicap Ceiling
     * Possible values are between 0 and 9;
     */
    private int maxHandicap;
    //
    
    private int defaultKomi;
    
    
    public PairingProperties() {
        numberOfRounds = DEFAULT_NUMBER_OF_ROUNDS;
        MMFloor = new Rank("30k");
        MMBar = new Rank("9d");
        pairingSystem = PairingSystem.MCMAHON;

        defaultKomi = DEFAULT_KOMI;
        
    }
    
    
    public int getNumberOfRounds() { return numberOfRounds; }
    public int setNumberOfRounds( String numberString ) {
        int newNumberOfRounds = 3;  // DEFAULT
        try {
            newNumberOfRounds = Integer.parseInt( numberString );
        } catch ( NumberFormatException ex ) {
            // TODO
            // bad string
        }
        return setNumberOfRounds( newNumberOfRounds );
    }
    public int setNumberOfRounds( int newNumberOfRounds ) {
        if (newNumberOfRounds<0) {
            newNumberOfRounds = 1;
        } else if ( newNumberOfRounds>MAX_NUMBER_OF_ROUNDS ) {
            newNumberOfRounds = MAX_NUMBER_OF_ROUNDS;
        }
        numberOfRounds = newNumberOfRounds;
        
        // This property is bounded by number of rounds, so we're going to reverify it here.
        // This won't update the settings form, however. That's a big:
        // TODO
        setLastRoundForSeedSystem1( getLastRoundForSeedSystem1() );
        
        return numberOfRounds;
    }
    
    public Rank getMMFloor() { return MMFloor; }
    public void setMMFloor( String rankString){
        MMFloor = new Rank( rankString );
    }
            
    public Rank getMMBar() { return MMBar; }
    public void setMMBar( String rankString){
        MMBar = new Rank( rankString );
    }
    
    public PairingSystem getPairingSystem() { return pairingSystem; }
    public void setPairingSystem( PairingSystem p ) { pairingSystem = p; }
    
    public boolean getHandicapGames() { return handicapGames; }
    public void setHandicapGames( boolean useHandicap) { handicapGames = useHandicap; }
    
    
    
    
    
    public long getDuplicateGameCost() {
        return duplicateGameCost;
    }

    public void setDuplicateGameCost(long duplicateGameCost) {
        this.duplicateGameCost = duplicateGameCost;
    }

    public long getRandomCost() {
        return randomCost;
    }

    public void setRandomCost(long randomCost) {
        this.randomCost = randomCost;
    }

    public boolean getIsDeterministic() {
        return isDeterministic;
    }

    public void setIsDeterministic(boolean isDeterministic) {
        this.isDeterministic = isDeterministic;
    }

    public long getColorBalanceCost() {
        return colorBalanceCost;
    }

    public void setColorBalanceCost(long colorBalanceCost) {
        this.colorBalanceCost = colorBalanceCost;
    }

    public long getScoreDifferenceCost() {
        return scoreDifferenceCost;
    }

    public void setScoreDifferenceCost(long scoreDifferenceCost) {
        this.scoreDifferenceCost = scoreDifferenceCost;
    }

    public long getMaximizeSeedingCost() {
        return maximizeSeedingCost;
    }

    public void setMaximizeSeedingCost(long maximizeSeedingCost) {
        this.maximizeSeedingCost = maximizeSeedingCost;
    }

    public int getLastRoundForSeedSystem1() {
        return lastRoundForPairingScheme1;
    }

    public void setLastRoundForSeedSystem1( String roundString ) {
        int newRoundIndex;
        try {
            newRoundIndex = Integer.parseInt( roundString );
        } catch ( NumberFormatException ex ) {
            newRoundIndex = getLastRoundForSeedSystem1();
        }
        
        setLastRoundForSeedSystem1( newRoundIndex );
        
    }
    public void setLastRoundForSeedSystem1(int lastRoundForSeedSystem1) {
        if ( lastRoundForSeedSystem1 < 1 ) { lastRoundForSeedSystem1 = 1; }
        if ( lastRoundForSeedSystem1 > numberOfRounds ) { lastRoundForSeedSystem1 = numberOfRounds; }
        
        this.lastRoundForPairingScheme1 = lastRoundForSeedSystem1;
    }

    public PairingMethod getPairingScheme1() {
        return pairingScheme1;
    }
    public void setPairingScheme1( PairingMethod newScheme ) {
        pairingScheme1 = newScheme;
    }
    
    public PairingMethod getPairingScheme2() {
        return pairingScheme2;
    }
    public void setPairingScheme2( PairingMethod newScheme ) {
        pairingScheme2 = newScheme;
    }
    
/*
    public int getPaiMaAdditionalPlacementCritSystem1() {
        return paiMaAdditionalPlacementCritSystem1;
    }

    public void setPaiMaAdditionalPlacementCritSystem1(int paiMaAdditionalPlacementCritSystem1) {
        this.paiMaAdditionalPlacementCritSystem1 = paiMaAdditionalPlacementCritSystem1;
    }

    public int getPaiMaAdditionalPlacementCritSystem2() {
        return paiMaAdditionalPlacementCritSystem2;
    }

    public void setPaiMaAdditionalPlacementCritSystem2(int paiMaAdditionalPlacementCritSystem2) {
        this.paiMaAdditionalPlacementCritSystem2 = paiMaAdditionalPlacementCritSystem2;
    }*/

    public long getCategoryCost() {
        return categoryCost;
    }

    public void setCategoryCost(long categoryCost) {
        this.categoryCost = categoryCost;
    }

    public long getDuddWeight() {
        return duddWeight;
    }

    public void setDuddWeight(long duddWeight) {
        this.duddWeight = duddWeight;
    }

    public int getDuddUpperMode() {
        return duddUpperMode;
    }

    public void setDuddUpperMode(int duddUpperMode) {
        this.duddUpperMode = duddUpperMode;
    }

    public int getDuddLowerMode() {
        return duddLowerMode;
    }

    public void setDuddLowerMode(int duddLowerMode) {
        this.duddLowerMode = duddLowerMode;
    }

    public int getSecondaryCriteriaRankThreshold() {
        return secondaryCriteriaRankThreshold;
    }

    public void setSecondaryCriteriaRankThreshold(int rankThreshold) {
        this.secondaryCriteriaRankThreshold = rankThreshold;
    }

    public boolean getSecondaryCriteriaWinThresholdActive() {
        return secondaryCriteriaWinThresholdActive;
    }

    public void setSecondaryCriteriaWinThresholdActive(boolean thresholdActive) {
        this.secondaryCriteriaWinThresholdActive = thresholdActive;
    }

    public long getMinimizeHandicapCost() {
        return minimizeHandicapCost;
    }

    public void setMinimizeHandicapCost(long minimizeHandicapCost) {
        this.minimizeHandicapCost = minimizeHandicapCost;
    }

    public int getPreferMmsDiffRatherThanSameCountry() {
        return preferMmsDiffRatherThanSameCountry;
    }

    public void setPreferMmsDiffRatherThanSameCountry(int preferMmsDiffRatherThanSameCountry) {
        this.preferMmsDiffRatherThanSameCountry = preferMmsDiffRatherThanSameCountry;
    }

    public int getPreferMmsDiffRatherThanSameClub() {
        return preferMmsDiffRatherThanSameClub;
    }

    public void setPreferMmsDiffRatherThanSameClub(int preferMmsDiffRatherThanSameClub) {
        this.preferMmsDiffRatherThanSameClub = preferMmsDiffRatherThanSameClub;
    }

    public double getPaiStandardNX1Factor() {
        return paiStandardNX1Factor;
    }

    public void setPaiStandardNX1Factor(double paiStandardNX1Factor) {
        this.paiStandardNX1Factor = paiStandardNX1Factor;
    }

    public long getGeoCost() {
        return geoCost;
    }

    public void setGeoCost(long geoCost) {
        this.geoCost = geoCost;
    }

    public void initForMM() {
        duplicateGameCost                      =   MAX_DUPLICATE_GAME_COST;    
        randomCost                             =   0;
        isDeterministic                      =   true;
        colorBalanceCost                          =   MAX_COLOR_BALANCE_COST;
    
        categoryCost              =   0;         // Not relevant in McMahon

        scoreDifferenceCost            =   MAX_SCORE_DIFFERENCE_COST;

        duddWeight                         =   MAX_DUDD_WEIGHT;
            duddUpperMode                  =   DUDD_MID;
            duddLowerMode                  =   DUDD_MID;
    
        maximizeSeedingCost                    =   MAX_MAXIMIZE_SEEDING_COST;      // 10^5
            lastRoundForPairingScheme1        =   1;
//            paiMaSeedSystem1                    =   PAIMA_SEED_SPLITANDRANDOM;                              
//            paiMaSeedSystem2                    =   PAIMA_SEED_SPLITANDFOLD; 
//            paiMaAdditionalPlacementCritSystem1 =   PlacementProperties.PLA_CRIT_RATING;
//            paiMaAdditionalPlacementCritSystem2 =   PlacementProperties.PLA_CRIT_NUL;
        
        secondaryCriteriaRankThreshold                      =   0;           // Do not apply secondary criteria above 1D rank
        secondaryCriteriaWinThresholdActive              =   false;       // Do not apply secondary criteria when nbWins >= nbRounds / 2
        paiSeDefSecCrit                         =   MAX_SCORE_DIFFERENCE_COST;
        minimizeHandicapCost                   =   0;           // Not relevant in McMahon
        geoCost                       =   scoreDifferenceCost;
        preferMmsDiffRatherThanSameCountry =   1;
        preferMmsDiffRatherThanSameClub    =   3;
        
        
        handicapBasis = HandicapBasis.MMS;
        
        maxHandicappedRank = new Rank("1d");
        handicapModifier = 1;       
        maxHandicap = 9;
        
    }
    public void initForSwiss(){
        duplicateGameCost                      =   MAX_DUPLICATE_GAME_COST;    
        randomCost                             =   0;
        isDeterministic                      =   true;
        colorBalanceCost                          =   MAX_COLOR_BALANCE_COST;
    
        categoryCost              =   0;          // Not relevant         

        scoreDifferenceCost            =   MAX_SCORE_DIFFERENCE_COST;

        duddWeight                         =   MAX_DUDD_WEIGHT;
            duddUpperMode                  =   DUDD_MID;
            duddLowerMode                  =   DUDD_MID;
    
        maximizeSeedingCost                    =   MAX_MAXIMIZE_SEEDING_COST;      // 10^5
            lastRoundForPairingScheme1        =   1;
//            paiMaSeedSystem1                    =   PAIMA_SEED_SPLITANDSLIP;                              
//            paiMaSeedSystem2                    =   PAIMA_SEED_SPLITANDSLIP; 
//            paiMaAdditionalPlacementCritSystem1 =   PlacementProperties.PLA_CRIT_RATING;
//            paiMaAdditionalPlacementCritSystem2 =   PlacementProperties.PLA_CRIT_RATING;
        
        secondaryCriteriaRankThreshold                      =   -30;            // Do not apply secondary criteria above rank 
        secondaryCriteriaWinThresholdActive              =   true;           // Not Relevant
        paiSeDefSecCrit                         =   MAX_CATEGORY_COST;
        minimizeHandicapCost                   =   0;     
        geoCost                       =   0;
        preferMmsDiffRatherThanSameCountry =   0;              // Not Relevant
        preferMmsDiffRatherThanSameClub    =   0;              // Not Relevant
        
        handicapBasis = HandicapBasis.RANK;
        
        maxHandicappedRank = new Rank("30k");
        handicapModifier = 0;       
        maxHandicap = 0;
        
    }
    public void initForSwissCat() {
        duplicateGameCost                      =   MAX_DUPLICATE_GAME_COST;    
        randomCost                             =   0;
        isDeterministic                      =   true;
        colorBalanceCost                          =   MAX_COLOR_BALANCE_COST  ;
    
        categoryCost              =   MAX_CATEGORY_COST;         

        scoreDifferenceCost            =   MAX_SCORE_DIFFERENCE_COST;

        duddWeight                         =   MAX_DUDD_WEIGHT;
            duddUpperMode                  =   DUDD_MID;
            duddLowerMode                  =   DUDD_MID;
    
        maximizeSeedingCost                    =   MAX_MAXIMIZE_SEEDING_COST;      // 10^5
            lastRoundForPairingScheme1        =   1;
//            paiMaSeedSystem1                    =   PAIMA_SEED_SPLITANDRANDOM;                              
//            paiMaSeedSystem2                    =   PAIMA_SEED_SPLITANDFOLD; 
//            paiMaAdditionalPlacementCritSystem1 =   PlacementProperties.PLA_CRIT_RATING;
//            paiMaAdditionalPlacementCritSystem2 =   PlacementProperties.PLA_CRIT_NUL;
        
        secondaryCriteriaRankThreshold                      =   0;           // Do not apply secondary criteria above 1D rank
        secondaryCriteriaWinThresholdActive              =   true;        // Do not apply secondary criteria when nbWins >= nbRounds / 2
        paiSeDefSecCrit                         =   MAX_CATEGORY_COST;
        minimizeHandicapCost                   =   paiSeDefSecCrit;     
        geoCost                       =   paiSeDefSecCrit;
        preferMmsDiffRatherThanSameCountry =   1;
        preferMmsDiffRatherThanSameClub    =   3;
        
        
        handicapBasis = HandicapBasis.MMS;
        
        maxHandicappedRank = new Rank("9d");
        handicapModifier = 1;       
        maxHandicap = 9;
    }    

    public long getPaiSeDefSecCrit() {
        return paiSeDefSecCrit;
    }

    public void setPaiSeDefSecCrit(long paiSeDefSecCrit) {
        this.paiSeDefSecCrit = paiSeDefSecCrit;
    }
    
    
    // Handicap
    
    public HandicapBasis getHandicapBasis() { return handicapBasis; }
    public void setHandicapBasis( HandicapBasis hb ) { handicapBasis = hb; }
    

    public Rank getMaxHandicappedRank() {
        return maxHandicappedRank;
    }

    public void setMaxHandicappedRank( String newRank ) {
        this.maxHandicappedRank = new Rank( newRank );
    }

    public int getHandicapModifier() {
        return handicapModifier;
    }

    /*
     * Value should be positive. It is the amount by which the handicap is
     * reduced: 0, 1, 2, 3.
     */
    public void setHandicapModifier(int modifier) {
        this.handicapModifier = modifier;
    }

    public int getMaxHandicap() {
        return maxHandicap;
    }

    public void setMaxHandicap( String handicapCeiling ) {
        int newMax;
        try {
            newMax = Integer.parseInt( handicapCeiling );
        } catch ( NumberFormatException ex ) {
            newMax = this.getMaxHandicap();
        }
        setMaxHandicap( newMax );
    }    
    public void setMaxHandicap(int max) {
        max = Math.max( 0, max );
        max = Math.min( 9, max );
        
        this.maxHandicap = max;
    }    
    
    
    public int getDefaultKomi() { return defaultKomi; }
    public String getDefaultKomiString() {
        return ( Integer.toString( defaultKomi ) + ".5" );
    }
    public void setDefaultKomi( int komi ) { defaultKomi = komi; }
    public void setDefaultKomi( String komiString ) {
        float komiFloat = DEFAULT_KOMI;
        try {
             komiFloat = Float.parseFloat( komiString );
        } catch ( NumberFormatException ex ) {
            // bleh!
        }
        setDefaultKomi( (int) komiFloat );
    }
}
