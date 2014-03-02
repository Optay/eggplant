package org.seattlego.eggplant.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.seattlego.eggplant.interfaces.ITournament;
import org.seattlego.eggplant.model.comparators.BandComparator;
import org.seattlego.eggplant.model.comparators.GameComparator;
import org.seattlego.eggplant.model.comparators.PlayerComparator;

/**
 * TODO
 * Move constants and other constraints somewhere more sensible.
 * 
 * @author Topsy
 */
public class Tournament implements ITournament {
    
    private final int MAX_NUMBER_OF_PLAYERS = 100;
    private final int MAX_NUMBER_OF_TABLES = 600;   // TODO - do something sensible about these constants.
    
    private HashMap<String, Player> playersMap;
    Player[] byePlayers;
    
    /** HashMap of Games
     * The key is (roundIndex * MAX_NUMBER_OF_TABLES + tableNumber)
     */
    private HashMap<Integer, Game> gamesMap;
    
    // There is always a top band and a bottom band, but they may be empty
    // depending on how the bar/floor is set.
    private ArrayList<Band> bands;
    
    private ArrayList<ActionListener> idChangeListeners;
    
    
    private int newMemberNumber;
    private int uniqueKeyGenerator;
    private boolean changedSinceLastSave = true;
    
    private TournamentProperties props;
    private PairingProperties pairingProps;
    private PlacementProperties placementProps;
    
    private Boolean scoringValid;
    
    
    public Tournament() {
        playersMap = new HashMap<>();
        gamesMap = new HashMap<>();
        
        bands = new ArrayList<>();
        // Add a starting band
        bands.add( new Band("Band", Rank.MAX_RANK.toMms(), 0 ));
        
        
        props = new TournamentProperties();
        pairingProps = new PairingProperties();
        placementProps = new PlacementProperties();
        byePlayers = new Player[ MAX_NUMBER_OF_PLAYERS ];  // TODO - size this more sensibly based on rounds or use a List of some type.
        
        scoringValid = false;
        
        newMemberNumber = 99999;
        uniqueKeyGenerator = 0;
        
        idChangeListeners = new ArrayList<>();
    }
    
    @Override
    public void addIdChangeListener( ActionListener l ) {
        idChangeListeners.add( l );
    }
    @Override
    public void fireIdChange() {
        for ( ActionListener l : idChangeListeners ) {
            l.actionPerformed( new ActionEvent(this, ActionEvent.ACTION_FIRST, "Tournament Id properties changed.") );
        }
    }
    
    
    @Override
    public boolean playerInvolvedInRound( Player player, int roundIndex ) {
        // Has pairings?
        for ( Game g : getGames(roundIndex) ) {
            if ( g.playersInclude(player) ) { return true; }
        }
        
        // Bye players
        if ( byePlayers[ roundIndex ] == player ) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean playerInvolvedInAnyRound( Player player ) {
        for ( int i = 0; i <  pairingProps.getNumberOfRounds(); i++ ) {
            if ( playerInvolvedInRound( player, i ) ) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public TournamentProperties getProps() { return props; }
    
    @Override
    public PairingProperties getPairingProps() { return pairingProps; }

    @Override
    public PlacementProperties getPlacementProps() { return placementProps; }
    
    
    @Override
    public String getPrintHeadingString( String title ) {
        return MessageFormat.format( "{0} \u0077 {1}\n{2}", getProps().getName(), getProps().getStartDateString(), title );
        
    }
    
    /*
     * TODO
     * Add and hook up exception.
     */
    //public boolean addPlayer(Player p) throws TournamentException {
    @Override
    public boolean addPlayer(Player p) {
        /*
        if (playersMap.size() >= MAX_NUMBER_OF_PLAYERS) {
            throw new TournamentException("Player" + " " + p.fullName() + " " + "could not be inserted" +
                    "\n" + "Maximum number of players exceeded");
        }
        */
        if ( p.getId().getAGANo() == 0 )
            p.getId().setAGANo( getNewPlayerAGANo() );
        
        // Check for duplicate AGA number
        for (Player existingPlayer : playersMap.values()) {
            if ( existingPlayer.getId().getAGANo() == p.getId().getAGANo() ) {
                //throw new TournamentException("Player" + " " + p.fullName() + " " + "could not be inserted." + "\n" + "A player with that AGA number is already exists in the tournament.");
                return false;
            }
        }
        //
        
        p.setKeyString( getNewPlayerKey() );
        
        playersMap.put( p.getKeyString(), p );
        
        // Find closest band
        assignPlayerToClosestBand( p );
        //
        
        
        setChangedSinceLastSave(true);
        
        Logger.getLogger( Tournament.class.getName() ).log( Level.INFO, "Player added" );
        return true;
        
        //throw new TournamentException("Player" + " " + p.fullName() + " " + "could not be inserted" + "\n" + "A player named" + " " + homonymPlayer.fullName() + " " + "already exists in the tournament");
    }
    
    /*
     * Adds a player to the tournament and creates a band for them if one with
     * the appropriate value does not exist.
     */
    @Override
    public boolean addLoadedPlayers( ArrayList<Player> loadedPlayers ) {
        
        for ( Player p : loadedPlayers ) {
            // Add player without running band check.
            p.setKeyString( getNewPlayerKey() );

            playersMap.put( p.getKeyString(), p );
            
            // Find or create band for player.
            Band playerBand = null;
            int bandMms = p.getScore().getInitialMms();

            for ( Band band : bands ) {
                if ( band.getMms() == bandMms ) {
                    playerBand = band;
                    break;
                }
            }
            if ( playerBand == null ) {
                playerBand = new Band( "Foo", bandMms );
                bands.add( playerBand );
            }
            playerBand.addPlayer( p );
        }
        
        // Sort bands.
        Collections.sort( bands, new BandComparator() );
        
        updateBandSpacing();
        
        // Called just to update band names as MMSs will already be correct.
        updateBands();
        
        return true;  
    }
    
    
    /*
     */
    @Override
    public boolean removePlayer(Player p) {
        if ( playerInvolvedInAnyRound( p ) ) {
            return false;
        }
        
        if ( playersMap.remove( p.getKeyString() ) == null ) {
            // Null player or player was not registered.
            return false;
        }
        
        setChangedSinceLastSave(true);
        return true;
    }
    
    
    @Override
    public ArrayList<Player> getRegisteredPlayers() {
        return new ArrayList<>( playersMap.values() );
    }
    
    @Override
    public Player getPlayerByAGA( String agaNo ) {
        for ( Player p : playersMap.values() ) {
            if ( p.getId().getAGANoString().equals( agaNo ) ) return p;
        }
        return null;
    }
    
    /*
     * For determining membership status when generating report.
     */
    @Override
    public int getLastAssignedAGANo() {
        return newMemberNumber;
    }
    
    /*
     * For loaded tournaments to ensure proper behavior when adding new players
     * and exporting. It is possible to sweep players for next available number,
     * but that introduces complications during export.
     */
    @Override
    public void setNewPlayerAGANo( String startingNumber ) {
        try {
            newMemberNumber = Integer.parseInt( startingNumber );
        } catch ( NumberFormatException ex ) {
            Logger.getLogger( Tournament.class.getName() ).log( Level.SEVERE, "New player AGA number seed invalid." );
        }
    }
    
    private int getNewPlayerAGANo() {
        while ( getPlayerByAGA(Integer.toString( newMemberNumber )) != null ) {
            newMemberNumber--;
        }
        
        return newMemberNumber;
    }
    private String getNewPlayerKey() {
        uniqueKeyGenerator++;
        return Integer.toString(uniqueKeyGenerator);
    }
    
    
    /*
     * Assign band values and names.
     */
    @Override
    public void updateBands() {
        // Remove empty.
        ArrayList<Band> nonEmptyBands = new ArrayList<>();
        
        for ( Band band : bands ) {
            if ( !band.getPlayers().isEmpty() ) { nonEmptyBands.add( band ); }
        }
        
        bands = nonEmptyBands;
        
        // Update scores from the top down based on spacing.
        int mmsAbove = Rank.MAX_RANK.toMms();
        bands.get(0).setSpacing(0);
        
        int nameCounter = 65;
        for ( Band band : bands ) {
            band.setMms( mmsAbove - band.getSpacing() );
            band.setLabel( "Band " + Character.toString( (char) nameCounter) + ": " + Integer.toString( getBandOffset(band) ));
            
            nameCounter++;
            mmsAbove = band.getMms();
        }
        setScoringValidity( false );
        setChangedSinceLastSave(true);
    }
    
    @Override
    public void createBands( int playersPerBand ) {
        bands.clear();
        
        if ( playersPerBand < 1 ) { playersPerBand = 1; }
        
        // Create and assign bar and floor bands
        Band topBand = new Band("", 0, 1 );
        Band bottomBand = new Band("", 0, 1 );

        ArrayList<Player> players = getRegisteredPlayers();
        for ( Player p : players ) {
            if ( p.getRank().toMms() >= pairingProps.getMMBar().toMms() ) {
                topBand.addPlayer( p );
                continue;
            }
            if ( p.getRank().toMms() <= pairingProps.getMMFloor().toMms() ) {
                bottomBand.addPlayer( p );
                continue;
            }
        }
        //
        
        players.removeAll( topBand.getPlayers() );
        players.removeAll( bottomBand.getPlayers() );
        
        Collections.sort( players, new PlayerComparator( new PlacementCriterion[]{ PlacementCriterion.RANK }, 1, false ) );
        
        // Add bands
        int numberOfBands = Math.max( 1, Math.round( players.size()/playersPerBand ) );
        for ( int i = 0; i < numberOfBands; i++ ) {
            bands.add( new Band("", 0, 1) );
        }
        
        // Add players to bands
        for ( int i = 0; i < players.size(); i++ ) {
            int bandIndex = Math.min( (int) i / playersPerBand, bands.size()-1 );
            bands.get( bandIndex ).addPlayer( players.get(i) );
        }
        
        // Assemble all the bands, update names and values.
        bands.add( 0, topBand );
        bands.add( bottomBand );
        updateBands();
    }
    
    /*
     * Warning, this method does NOT remove previous band assignment. This 
     * should only be called when adding a new player that is not currently 
     * found in any band's collection.
     * 
     */
    private void assignPlayerToClosestBand( Player player ) {
        
        int minDistance = Integer.MAX_VALUE;
        Band oldBand = null;
        Band closestBand = bands.get(0);
        for ( Band b : bands ) {
            for ( Player otherPlayer : b.getPlayers() ) {
                if ( otherPlayer == player ) {
                    oldBand = b;
                    continue;
                }
                int distance = Math.abs( player.getRank().getValue() - otherPlayer.getRank().getValue() );
                if ( distance < minDistance ) {
                    minDistance = distance;
                    closestBand = b;
                }
            }
        }
        if ( oldBand != null ) {
            oldBand.removePlayer( player );
        }
        closestBand.addPlayer( player );
        setChangedSinceLastSave(true);
    }
    
    @Override
    public int getBandOffset( Band band ) {
        int baseMms = bands.get(0).getMms();
        return (band.getMms() - baseMms);
    }
    
    @Override
    public void modifyBandOffset( Band band, int newOffset ) {
        if ( newOffset >= 0 ) {
            return;
        }
        int baseMms = bands.get(0).getMms();
        
        band.setMms( baseMms + newOffset );
        
        Collections.sort( bands, new BandComparator() );
        
        updateBandSpacing();
        updateBands();  // For names.
    }
    
    @Override
    public ArrayList<Band> getBands() {
        return bands;
    }
    
    /*
     * Sweep bands and update their spacing based on their values.
     */
    private void updateBandSpacing() {
        int mmsAbove = bands.get(0).getMms();
        for ( Band b : bands ) {
            b.setSpacing( mmsAbove - b.getMms() );
            mmsAbove = b.getMms();
        }
    }

    private int getMinBandMms() {
        return bands.get( bands.size() - 1 ).getMms();
    }
    
    private int getMaxBandMms() {
        return bands.get( 0 ).getMms();
    }
    
    
    
    
    @Override
    public Player getByePlayer(int roundIndex) {
        return byePlayers[roundIndex];
    }
    
    @Override
    public void assignByePlayer( Player p, int roundIndex ) {
        unassignByePlayer( roundIndex );
        
        byePlayers[roundIndex] = p;
        p.setParticipation( roundIndex, Participation.BYE );
        setChangedSinceLastSave(true);
    }
    @Override
    public void unassignByePlayer( int roundIndex ) {
        Player p = byePlayers[roundIndex];
        if ( p != null ) {
            p.setParticipation( roundIndex, Participation.NOT_ASSIGNED );
        }
        
        byePlayers[roundIndex] = null;
        setChangedSinceLastSave(true);
    }
    @Override
    public void chooseByePlayer(ArrayList<Player> alPlayers, int roundIndex) {

        // The weight allocated to each player is 1000 * number of previous byes + rank
        // The chosen player will be the player with the minimum weight
        Player bestPlayerForBye = null;
        int lowestWeight = Integer.MAX_VALUE;

        for ( Player p : alPlayers ) {
            int byeWeight = p.getRank().getValue();
            for (int r = 0; r < roundIndex; r++) {
                if ( byePlayers[r] == null ) {
                    continue;
                }
                if ( p == byePlayers[r] ) {
                    byeWeight += 1000;
                }
            }
            if ( byeWeight < lowestWeight ) {
                lowestWeight = byeWeight;
                bestPlayerForBye = p;
            }
        }
        assignByePlayer(bestPlayerForBye, roundIndex);
    }    

    @Override
    public ArrayList<Player> getPairablePlayers(int roundIndex) {
        ArrayList<Player> pairablePlayers = new ArrayList<>();
        
        for ( Player p: this.playersMap.values() ) {
            if ( ( p.getParticipation( roundIndex ) == Participation.NOT_ASSIGNED ) ||
                 ( p.getParticipation( roundIndex ) == Participation.UNKNOWN ) ) {
                pairablePlayers.add( p );
            }
        }
        return pairablePlayers;
    }
    
    @Override
    public ArrayList<Player> getUnpairablePlayers(int roundIndex) {
        ArrayList<Player> unpairablePlayers = new ArrayList<>();
        
        for ( Player p: this.playersMap.values() ) {
            if ( ( p.getParticipation( roundIndex ) == Participation.ABSENT ) ) {
                unpairablePlayers.add( p );
            }
        }
        
        return unpairablePlayers;
    }
    
    
    
    
        
    

    /*
     * Recalculate scoring if necessary.
     */
    @Override
    public void updateScoring() {
        if (!scoringValid) {
            Scorer.scorePlayers( getRegisteredPlayers(), pairingProps.getNumberOfRounds(), getAllGames(), placementProps);
            scoringValid = true;
        }
    }
    
    @Override
    public void setScoringValidity( Boolean isValid ) {
        this.scoringValid = isValid;
    }
    
    @Override
    public boolean getChangedSinceLastSave() {
        return changedSinceLastSave;
    }

    @Override
    public void setChangedSinceLastSave( boolean changed ) {
        this.changedSinceLastSave = changed;
    }    
    
    
    
    
    
    
    
    
    /*
     * Will only pair an even number of players. Bye player must be removed
     * before calling pairRound.
     */
    @Override
    public ArrayList<Game> pairRound( ArrayList<Player> players, int roundIndex ) {
        if (players.size() % 2 != 0) {
            Logger.getLogger( Tournament.class.getName() ).log( Level.INFO, "Attempted to pair an odd number of players. Bye player should have been removed before this call." );
            
            return null;
        }

        // Get alPreviousGames
        ArrayList<Game> alPreviousGames = getGamesBefore(roundIndex);
        
        
        PlacementCriterion mainCrit = placementProps.mainCriterion();
        
        int mainScoreMin;
        int mainScoreMax;
        switch (mainCrit) {
            case MMS:
                mainScoreMin = getMinBandMms();
                mainScoreMax = getMaxBandMms() + roundIndex;
                break;
            case NBW:
            default:
                mainScoreMin = 0;
                mainScoreMax = roundIndex;
        }
        
        
        // Update player score metrics in order to pair.
        updateScoring();

        // Update player pairing metrics for this round.
        Scorer.scorePlayers2( getRegisteredPlayers(), roundIndex, alPreviousGames, placementProps, pairingProps, mainScoreMin, mainScoreMax );

        ArrayList<Game> alGames = new ArrayList<>();

        // If necessary, split alPlayersToPair into smaller players groups
        ArrayList<Player> alRemainingPlayers = new ArrayList<>( players );

        // TODO - Refactor to remove repetitious call to Pairer.pairPlayers.
        while ( alRemainingPlayers.size() > Pairer.PAIRING_GROUP_MAX_SIZE ) {
            boolean bGroupReady = false;
            ArrayList<Player> alGroupedPlayers = new ArrayList<>();
            
            for (int mainScore = mainScoreMax; mainScore >= mainScoreMin; mainScore--) {
                for (Iterator<Player> it = alRemainingPlayers.iterator(); it.hasNext();) {
                    Player p = it.next();
                    
                    if ( (int)p.getScore().getMetricUnscaled( roundIndex-1, mainCrit ) < mainScore) {
                    //if ( p.getScore().getMetric( roundIndex-1, mainCrit ) / 2 < mainScore) {
                    //if (p.playerScoring.getCritValue(mainCrit, roundIndex - 1) / 2 < mainScore) {
                        continue;
                    }
                    alGroupedPlayers.add(p);
                    it.remove();
                    
                    // 2 Emergency breaks
                    if ( alGroupedPlayers.size() >= Pairer.PAIRING_GROUP_MAX_SIZE) {
                        bGroupReady = true;
                        break;
                    }
                    if ( alRemainingPlayers.size() <= Pairer.PAIRING_GROUP_MIN_SIZE) {
                        bGroupReady = true;
                        break;
                    }
                }
                // Is the group ready for pairing ?
                if (alGroupedPlayers.size() >= Pairer.PAIRING_GROUP_MIN_SIZE && alGroupedPlayers.size() % 2 == 0) {
                    bGroupReady = true;
                }
                if (bGroupReady) {
                    break;
                }
            }
            if (bGroupReady) {
                break;
            }
            
            ArrayList<Game> alG = Pairer.pairPlayers( alGroupedPlayers, roundIndex, alPreviousGames, pairingProps );
            //ArrayList<Game> alG = pairAGroup(alGroupedPlayers, roundIndex, alPreviousGames);
            alGames.addAll(alG);
        }
        
        /* TODO - This appears to be completely redundant as the equivalent 
         * method (Scorer.scorePlayers2) is called before the grouping. The 
         * method in question only updates quantities relevant to the prior 
         * rounds, so the fact that some players are paired before this second
         * call appears irrelevant.
         * All that is left here is to pair the remaining players.
         */
        // fillPairingInfo(roundIndex);
        
        ArrayList<Game> alG = Pairer.pairPlayers( alRemainingPlayers, roundIndex, alPreviousGames, pairingProps );
        alGames.addAll(alG);
        
        // Number the tables in the round
        addGames( roundIndex, alGames );
        
        setChangedSinceLastSave(true);
        return alGames;        
    }
    
    
    private ArrayList<Game> getAllGames() {
        return getGamesBefore( pairingProps.getNumberOfRounds() + 1 );
    }
    
    
    @Override
    public ArrayList<Game> getGamesBefore(int roundIndex) {
        ArrayList<Game> gL = new ArrayList<>();
        for (Game g : gamesMap.values()) {
            if (g.getRoundIndex() < roundIndex) {
                gL.add(g);
            }
        }
        return gL;
    }
    
    @Override
    public ArrayList<Game> getGames(int roundIndex) {
        ArrayList<Game> gL = new ArrayList<>();
        for (Game g : gamesMap.values()) {
            if (g.getRoundIndex() == roundIndex) {
                gL.add(g);
            }
        }
        return gL;
    }
    
    @Override
    public Game getGame( int roundIndex, Player p ) {
        ArrayList<Game> games = getGames( roundIndex );
        for ( Game game : games ) {
            if (game.playersInclude(p)) { return game; }
        }
        return null;
    }
    

    /*
     * Inserts games loaded by save file parser. First scores players for the 
     * round, as this won't have been done. Then, calls addGames.
    */
    @Override
    public void addLoadedGames( int roundIndex, ArrayList<Game> newGames ) {
        ArrayList<Game> alPreviousGames = getGamesBefore(roundIndex);
        
        // Update player score metrics, so table numbers can be set correctly.
        Scorer.scorePlayers( getRegisteredPlayers(), roundIndex, alPreviousGames, placementProps);
        
        addGames( roundIndex, newGames );
    }
    
    /*
     * Sorts all games in a round and renumbers them based on an MMS sort. Then
     * adds them to game map.
     * 
     */
    private void addGames( int roundIndex, ArrayList<Game> newGames ) {
        // If we only renumber tables after pairing, this should never be necessary.
        //fillBaseScoringInfoIfNecessary();
        
        if ( (newGames == null) || newGames.isEmpty() ) {
            return;
        }
        
        ArrayList<Game> games = getGames( roundIndex );
        games.addAll( newGames );
        
        Collections.sort( games, new GameComparator(GameComparator.BEST_MMS_ORDER) );

        // Since key is related to table number, we must rebuild the map for this round.
        // Remove games from map.
        for (Game g : games) {
            removeGame(g);
        }

        for ( int tableIndex = 0; tableIndex < games.size(); tableIndex++ ) {
            Game game = games.get( tableIndex );
            game.setTableIndex( tableIndex );
            addGame( game );
        }
        
        setChangedSinceLastSave( true );
    }    


    private void addGame( Game g ) {
        // TODO - error check
        gamesMap.put( getGameKey( g ), g );
        setChangedSinceLastSave( true );
        
        g.getWhitePlayer().setParticipation( g.getRoundIndex(), Participation.PAIRED );
        g.getBlackPlayer().setParticipation( g.getRoundIndex(), Participation.PAIRED );
        
        setChangedSinceLastSave(true);
    }
    
    @Override
    public void removeGame( Game g ) {
        // TODO - error check
        gamesMap.remove( getGameKey( g ) );
        
        g.getWhitePlayer().setParticipation( g.getRoundIndex(), Participation.NOT_ASSIGNED );
        g.getBlackPlayer().setParticipation( g.getRoundIndex(), Participation.NOT_ASSIGNED );
        
        setChangedSinceLastSave( true );
    }
    
    private int getGameKey( Game g ) {
        int r = g.getRoundIndex();
        int t = g.getTableIndex();
        Integer key = r * MAX_NUMBER_OF_TABLES + t;
        return key;
    }
    
    
    
}
