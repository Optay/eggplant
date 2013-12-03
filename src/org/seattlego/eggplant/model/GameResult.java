package org.seattlego.eggplant.model;

/**
 *
 * @author Topsy
 */
public enum GameResult {
    UNKNOWN             (PlayerResult.UNKNOWN, PlayerResult.UNKNOWN, GameResultType.NORMAL, " - "),
    WHITE               (PlayerResult.WIN, PlayerResult.LOSE, GameResultType.NORMAL, "1-0"),
    WHITE_BY_DEFAULT    (PlayerResult.WIN, PlayerResult.LOSE, GameResultType.BY_DEFAULT, "1-0!"),
    BLACK               (PlayerResult.LOSE, PlayerResult.WIN, GameResultType.NORMAL, "0-1"),
    BLACK_BY_DEFAULT    (PlayerResult.LOSE, PlayerResult.WIN, GameResultType.BY_DEFAULT, "0-1!"),
    DRAW                (PlayerResult.DRAW, PlayerResult.DRAW, GameResultType.NORMAL, "½-½"),
    DRAW_BY_DEFAULT     (PlayerResult.DRAW, PlayerResult.DRAW, GameResultType.BY_DEFAULT, "½-½!"),
    BOTH_WIN            (PlayerResult.WIN, PlayerResult.WIN, GameResultType.NORMAL, "1-1"),
    BOTH_WIN_BY_DEFAULT (PlayerResult.WIN, PlayerResult.WIN, GameResultType.BY_DEFAULT, "1-1!"),
    BOTH_LOSE           (PlayerResult.LOSE, PlayerResult.LOSE, GameResultType.NORMAL, "0-0"),
    BOTH_LOSE_BY_DEFAULT(PlayerResult.LOSE, PlayerResult.LOSE, GameResultType.BY_DEFAULT, "0-0!");

    
    private final PlayerResult whiteResult;
    private final PlayerResult blackResult;
    private final GameResultType type;
    private final String text;

    private GameResult( final PlayerResult whiteResult, final PlayerResult blackResult, final GameResultType type, final String text ) {
        this.whiteResult = whiteResult;
        this.blackResult = blackResult;
        this.type = type;
        this.text = text;
    }
    
    @Override
    public String toString() { return text; }
    
    public PlayerResult getWhiteResult() {
        return whiteResult;
    }
    
    public PlayerResult getBlackResult() {
        return blackResult;
    }
    
    public GameResultType getType() {
        return type;
    }
    
    public GameResult getNext() {
        int ordinal = this.ordinal() + 1;
        if ( ordinal >= GameResult.values().length ) { ordinal = 0; }
        return GameResult.values()[ ordinal ];

    }

    /*
     * Choices: Black win, White win, Draw, Void, Black forfeit, White forfeit, 
     * Double loss, Double forfeit, Voluntary Bye, Involuntary Bye, Unknown
     */
    static public String toAGAString( GameResult gameResult ) {
        switch( gameResult ) {
            case BLACK:
            case BLACK_BY_DEFAULT:
                return "Black win";
            case WHITE:
            case WHITE_BY_DEFAULT:
                return "White win";
            case BOTH_LOSE:
            case BOTH_LOSE_BY_DEFAULT:
                return "Double loss";
            case BOTH_WIN:
            case BOTH_WIN_BY_DEFAULT:
                return "INVALID";
            case DRAW:
            case DRAW_BY_DEFAULT:
                return "Draw";
            case UNKNOWN:
                return "Unknown";
        }
        return "INVALID";
    }
    
    
}
