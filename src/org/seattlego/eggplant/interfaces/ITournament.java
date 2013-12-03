package org.seattlego.eggplant.interfaces;

import java.util.ArrayList;
import org.seattlego.eggplant.model.*;

/**
 *
 * @author Topsy
 */
public interface ITournament {
    public boolean addPlayer( Player p );
    public boolean removePlayer( Player p );
    public boolean addLoadedPlayers( ArrayList<Player> loadedPlayers );
    
    public ArrayList<Player> getRegisteredPlayers();
    
    public TournamentProperties getProps();
    public PairingProperties getPairingProps();
    public PlacementProperties getPlacementProps();
    
    public String getPrintHeadingString( String title );
    
    public boolean playerInvolvedInRound( Player player, int roundIndex );
    public boolean playerInvolvedInAnyRound( Player player );
    
    public void setChangedSinceLastSave( boolean changed );
    public boolean getChangedSinceLastSave();
    
    public ArrayList<Player> getPairablePlayers( int roundIndex );
    public ArrayList<Player> getUnpairablePlayers( int roundIndex );
    public Player getByePlayer( int roundIndex );
    public void chooseByePlayer( ArrayList<Player> playersToPair, int roundIndex );
    public void assignByePlayer( Player p, int roundIndex );
    public void unassignByePlayer( int roundIndex );
    public Player getPlayerByAGA( String agaNo );
    public int getLastAssignedAGANo();
    public void setNewPlayerAGANo( String startingNumber );
    
    
    public ArrayList<Band> getBands();
    public void createBands( int numberOfBands );
    public void updateBands();
    public void modifyBandOffset( Band band, int newOffset );
    public int getBandOffset( Band band );
    
    
    public ArrayList<Game> pairRound( ArrayList<Player> players, int roundIndex );
    public ArrayList<Game> getGamesBefore( int roundIndex );
    public ArrayList<Game> getGames( int roundIndex );
    public void removeGame( Game game );
    public void addLoadedGames( int roundIndex, ArrayList<Game> newGames );
    
 
    public void setScoringValidity( Boolean isValid );
    public void updateScoring();
}
