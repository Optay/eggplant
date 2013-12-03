package org.seattlego.eggplant.model;

/**
 *
 * @author Topsy
 */
public class Game {
    
    private int roundIndex;
    
    /**
    * Unique for a given round
    */
    private int tableIndex = -1;
    
    private Player whitePlayer;
    private Player blackPlayer;

    /**
     * true if colors are known.
     * Note that, in some no-handicap tournaments, color is not published.
     * When it happens, players are randomly said to be white or black.
     * <code>knownColor</code> remembers whether color was actually known.
     */
    private boolean knownColor = false;

    private int unboundHandicap = 0;
    private int handicap = 0;
    private int komi = 0;

    //private int result = Game.RESULT_UNKNOWN;
    private GameResult result;


    
    public Game() {
    }
    
    public Game(    int roundIndex,
                    int tableIndex,
                    Player whitePlayer,
                    Player blackPlayer,
                    boolean knownColor,
                    int handicap,
                    int komi) {
        
        this(roundIndex, tableIndex, whitePlayer, blackPlayer, knownColor, handicap, komi, GameResult.UNKNOWN );
    }
    
    public Game(    int roundIndex,
                    int tableIndex,
                    Player whitePlayer,
                    Player blackPlayer,
                    boolean knownColor,
                    int handicap,
                    int komi,
                    GameResult result ) {
        
        this.roundIndex = roundIndex;
        this.tableIndex = tableIndex;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.knownColor  = knownColor;
        this.handicap    = handicap;
        this.komi        = komi;
        this.result      = result;
        
        this.unboundHandicap = 0;
    }

    public Player getBlackPlayer()   {
        return blackPlayer;
    }
    public void setBlackPlayer(Player p) {
        this.blackPlayer = p;
    }

    public Player getWhitePlayer()   {
        return whitePlayer;
    }
    public void setWhitePlayer(Player p) {
        this.whitePlayer = p;
    }
    
    public FlaggedPlayerName getWhiteFlagged()   {
        boolean won = ( result.getWhiteResult() == PlayerResult.WIN );
        
        return new FlaggedPlayerName( whitePlayer.getId().getFullName(), won );
    }
    public FlaggedPlayerName getBlackFlagged()   {
        boolean won = ( result.getBlackResult() == PlayerResult.WIN );
        
        return new FlaggedPlayerName( blackPlayer.getId().getFullName(), won );
    }

    public int getTableIndex() {
        return tableIndex;
    }
    public String getTableNumberString() {
        return Integer.toString( tableIndex + 1 );
    }
    public void setTableIndex(int tableIndex) {
        this.tableIndex = tableIndex;
    }

    public int getHandicap() {
        return handicap;
    }
    /* Returns handicap as string.
     * If handicap is 1, returns 0. A handicap of one need not be indicated
     * when komi is also being displayed.
     */
    public String getHandicapString() {
        if (handicap == 1)
            return "0";
        else
            return Integer.toString( handicap );
    }
    
    public void setHandicap(int newHandicap) {
        handicap = newHandicap;
        if (handicap < 0) handicap = 0;
        //if (handicap > 9) handicap = 9;  
    }
    
    public int getUnboundHandicap() {
        return unboundHandicap;
    }
    public void setUnboundHandicap(int newHandicap) {
        unboundHandicap = newHandicap;
    }
    

    public GameResult getResult()   {
        return result;
    }
    public String getResultString() {
        return result.toString();
    }

    /**
     * 
     * TODO: Test refactored version with player equality comparison.
     * 
     * 
     * @param p player
     * @return 0 1 or 2 for loss, draw or win
     */
    public int getWX2( Player p ){
        if ( p == whitePlayer ) return getResult().getWhiteResult().getValue();
        if ( p == blackPlayer ) return getResult().getBlackResult().getValue();

        return 0;
    }
    public int getOpponentWX2( Player p ){
        if ( p == whitePlayer ) return getResult().getWhiteResult().getValue();
        if ( p == blackPlayer ) return getResult().getBlackResult().getValue();

        return 0;
    }
    
    
    public void setResult( GameResult newResult )   {
        this.result = newResult;
    }

    public int getRoundIndex() {
        return roundIndex;
    }
    public void setRoundIndex(int round) {
        this.roundIndex = round;
    }
     
     public boolean isKnownColor() {
         return knownColor;
     }
     public void setKnownColor(boolean knownColor) {
         this.knownColor = knownColor;
     }

    /**
     * @return the komi
     */
    public int getKomi() {
        return komi;
    }

    /**
     * @param komi the komi to set
     */
    public void setKomi(int komi) {
        this.komi = komi;
    }
    public String getKomiString() {
        return Integer.toString( komi ) + ".5";
    }
    
    public String setKomi( String strKomi ) {
        try {
            this.komi = (int) Float.parseFloat( strKomi );
        } catch(NumberFormatException ex) {
        }
        return getKomiString();
    }
    
    
    
    public Boolean playersInclude( Player p ) {
        return ( ( getWhitePlayer() == p ) || ( getBlackPlayer() == p ) );
    }
    
    public Player getOpponent( Player p ) {
        if ( !playersInclude(p) ) {
            return null;
        }
        if ( getWhitePlayer() == p ) {
            return getBlackPlayer();
        } else {
            return getWhitePlayer();
        }
    }
    
    public int getResultValueFor( Player p ) {
        if ( getWhitePlayer() == p ) {
            return getResult().getWhiteResult().getValue();
        } else {
            return getResult().getBlackResult().getValue();
        }
    }
    
    
}
 

