package org.seattlego.eggplant.interfaces;

import java.awt.event.ActionListener;
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
    
    public void addIdChangeListener( ActionListener l );
    public void fireIdChange();
    
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
    public void resetBands();
    public void updateBands();
    public void splitBand( Band band );
    public int getBandOffset( Band band );
    
    
    public ArrayList<Game> pairRound( ArrayList<Player> players, int roundIndex );
    public void unpairRound( ArrayList<Game> games, int roundIndex );
    
    public ArrayList<Game> getGamesBefore( int roundIndex );
    public ArrayList<Game> getGames( int roundIndex );
    public Game getGame( int roundIndex, Player p );
    public void addLoadedGames( int roundIndex, ArrayList<Game> newGames );
    
 
    public void setScoringValidity( Boolean isValid );
    public void updateScoring();
}
