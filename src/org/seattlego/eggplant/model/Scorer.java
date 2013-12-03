package org.seattlego.eggplant.model;

import java.util.ArrayList;
import java.util.Collections;
import org.seattlego.eggplant.model.comparators.PlayerComparator;

/**
 * Contains methods to update score properties of players. Exists solely to 
 * keep Tournament from becoming bloated.
 * 
 * @author Topsy
 */
public class Scorer {
    
    public static void scorePlayers( ArrayList<Player> players, int numberOfRounds, ArrayList<Game> games, PlacementProperties placementProps ) {
        


        // 2) nbwX2 and mmsX2
        for (int r = 0; r < numberOfRounds; r++) {
            // Initialize
            for (Player p : players) {
                /*
                 * PlayerScore automatically returns 0 for all metrics not 
                 * defined at the given round number with one exception: If the 
                 * requested round index is less than 0 and the requested metric
                 * is MMS, it will return the initialMMS value.
                */
                p.getScore().setMetric( r, PlacementCriterion.NBW, p.getScore().getMetric((r-1), PlacementCriterion.NBW) );
                p.getScore().setMetric( r, PlacementCriterion.MMS, p.getScore().getMetric((r-1), PlacementCriterion.MMS) );
                //p.playerScoring.setNBWX2(r, p.playerScoring.getNBWX2(r - 1));
                //p.playerScoring.setMMSX2(r, p.playerScoring.getMMSX2(r - 1));
            }

            // Points from games
            for (Game g : games) {
                if (g.getRoundIndex() != r) {
                    continue;
                }
                Player wP = g.getWhitePlayer();
                Player bP = g.getBlackPlayer();
                if(wP == null) continue;
                if(bP == null) continue;
                PlayerScore wPS = wP.getScore();
                PlayerScore bPS = bP.getScore();
                
                wPS.incrementMetric( r, PlacementCriterion.NBW, g.getResult().getWhiteResult().getValue() );
                wPS.incrementMetric( r, PlacementCriterion.MMS, g.getResult().getWhiteResult().getValue() );
                
                bPS.incrementMetric( r, PlacementCriterion.NBW, g.getResult().getBlackResult().getValue() );
                bPS.incrementMetric( r, PlacementCriterion.MMS, g.getResult().getBlackResult().getValue() );
            }
        }
        
        for (Player p : players) {
            int nbPtsNbw2AbsentOrBye = 0;
            int nbPtsMms2AbsentOrBye = 0;
            for (int r = 0; r < numberOfRounds; r++){
                if (p.getParticipation(r) == Participation.ABSENT){
                    nbPtsNbw2AbsentOrBye += placementProps.getGenNbw2ValueAbsent();
                    nbPtsMms2AbsentOrBye += placementProps.getGenMms2ValueAbsent();
                }
                if (p.getParticipation(r) == Participation.BYE) {
                    nbPtsNbw2AbsentOrBye += placementProps.getGenNbw2ValueBye();
                    nbPtsMms2AbsentOrBye += placementProps.getGenMmw2ValueBye();
                }
                p.getScore().incrementMetric( r, PlacementCriterion.NBW, nbPtsNbw2AbsentOrBye);
                p.getScore().incrementMetric( r, PlacementCriterion.MMS, nbPtsMms2AbsentOrBye);
            }
        }

        // 3) CUSSW and CUSSM
        for (Player p : players) {
            p.getScore().setMetric( 0, PlacementCriterion.CUSSW, 0 );
            p.getScore().setMetric( 0, PlacementCriterion.CUSSM, 0 );
            for (int r = 1; r < numberOfRounds; r++) {
                p.getScore().setMetric( 0, PlacementCriterion.CUSSW,
                        p.getScore().getMetric( (r-1), PlacementCriterion.CUSSW ) +
                        p.getScore().getMetric( r, PlacementCriterion.NBW ) );
                p.getScore().setMetric( 0, PlacementCriterion.CUSSM,
                        p.getScore().getMetric( (r-1), PlacementCriterion.CUSSM ) +
                        p.getScore().getMetric( r, PlacementCriterion.MMS ) );
            }
        }

        // 4.1) SOSW, SOSWM1, SOSWM2, SODOSW
        for (int r = 0; r < numberOfRounds; r++) {
            for (Player p : players) {
                int[] oswX2 = new int[numberOfRounds];
                int[] doswX4 = new int[numberOfRounds];    // Defeated opponents score

                for (int rr = 0; rr <= r; rr++) {
                    
                    if ( p.getParticipation( rr ) != Participation.PAIRED ) {
                        oswX2[rr] = 0;
                        doswX4[rr] = 0;
                    } else {
                        Game g = getGame( games, rr, p );
                        Player opp = g.getOpponent( p );
                        int result = g.getResultValueFor( p );
                        
                        oswX2[rr] = opp.getScore().getMetric( r, PlacementCriterion.NBW ); //playerScoring.getNBWX2(r);
                        doswX4[rr] = oswX2[rr] * result;
                    }
                }
                int soswX2 = 0;
                int sodoswX4 = 0;
                for (int rr = 0; rr <= r; rr++) {
                    soswX2 += oswX2[rr];
                    sodoswX4 += doswX4[rr];
                }
                p.getScore().setMetric( r, PlacementCriterion.SOSW, soswX2 );
                p.getScore().setMetric( r, PlacementCriterion.SODOSW, sodoswX4 );
                //p.playerScoring.setSOSWX2(r, sosX2);
                ///p.playerScoring.setSDSWX4(r, sdsX4);

                // soswM1X2, soswM2X2
                int soswM1X2 = 0;
                int soswM2X2 = 0;
                if (r == 0){
                    soswM1X2 = 0;
                    soswM2X2 = 0;
                }
                else if (r == 1){
                    soswM1X2 = Math.max(oswX2[0], oswX2[1]);
                    soswM2X2 = 0;
                }
                else{
                    int rMin = 0;
                    for (int rr = 1; rr <= r; rr++) {
                        if (oswX2[rr] < oswX2[rMin]) {
                            rMin = rr;
                        }
                    }
                    int rMin2 = 0;
                    if (rMin == 0) rMin2 = 1;
                    for (int rr = 0; rr <= r; rr++) {
                        if (rMin2 == rMin) {
                            continue;
                        }
                        if (oswX2[rr] < oswX2[rMin2]) {
                            rMin2 = rr;
                        }
                    }
                    soswM1X2 = p.getScore().getMetric( r, PlacementCriterion.SOSW ) - oswX2[rMin];
                    //sosM1X2 = p.playerScoring.getSOSWX2(r) - oswX2[rMin];
                    soswM2X2 = soswM1X2 - oswX2[rMin2];
                }
                p.getScore().setMetric( r, PlacementCriterion.SOSWM1, soswM1X2 );
                p.getScore().setMetric( r, PlacementCriterion.SOSWM2, soswM2X2 );
                //p.playerScoring.setSOSWM1X2(r, sosM1X2);
                //p.playerScoring.setSOSWM2X2(r, sosM2X2);

            }
        }

        // 4.2) SOSM, SOSMM1, SOSMM2, SODOSM
        for (int r = 0; r < numberOfRounds; r++) {
            for (Player p : players) {
                int[] osmX2 = new int[numberOfRounds];
                int[] dosmX4 = new int[numberOfRounds];    // Defeated opponents score
                for (int rr = 0; rr <= r; rr++) {
                    if ( p.getParticipation(rr) != Participation.PAIRED ) {
                        osmX2[rr] = 2 * p.getScore().getInitialMms();
                    } else {
                        Game g = getGame( games, rr, p );
                        Player opp = g.getOpponent( p );
                        
                        osmX2[rr] = opp.getScore().getMetric( r, PlacementCriterion.MMS );//playerScoring.getMMSX2(r);
                        if ( p == g.getWhitePlayer() ) {
                            osmX2[rr] += 2 * g.getHandicap();
                        } else {
                            osmX2[rr] -= 2 * g.getHandicap();
                        }
                        dosmX4[rr] = osmX2[rr] * g.getResultValueFor(p);
                        
                    }
                }
                int sosmX2 = 0;
                int sodosmX4 = 0;
                for (int rr = 0; rr <= r; rr++) {
                    sosmX2 += osmX2[rr];
                    sodosmX4 += dosmX4[rr];
                }
                p.getScore().setMetric( r, PlacementCriterion.SOSM, sosmX2 );
                p.getScore().setMetric( r, PlacementCriterion.SODOSM, sodosmX4 );
                //p.playerScoring.setSOSMX2(r, sosX2);
                //p.playerScoring.setSDSMX4(r, sdsX4);

                // sosmM1X2, sosmM2X2
                int sosmM1X2 = 0;
                int sosmM2X2 = 0;
                if (r == 0) {
                    sosmM1X2 = 0;
                    sosmM2X2 = 0;
                } else if (r == 1) {
                    sosmM1X2 = Math.max(osmX2[0], osmX2[1]);
                    sosmM2X2 = 0;
                } else {
                    int rMin = 0;
                    for (int rr = 1; rr <= r; rr++) {
                        if (osmX2[rr] < osmX2[rMin]) {
                            rMin = rr;
                        }
                    }
                    int rMin2 = 0;
                    if (rMin == 0) rMin2 = 1;
                    for (int rr = 0; rr <= r; rr++) {
                        if (rr == rMin) {
                            continue;
                        }
                        if (osmX2[rr] < osmX2[rMin2]) {
                            rMin2 = rr;
                        }
                    }
                    sosmM1X2 = p.getScore().getMetric(r, PlacementCriterion.SOSM) - osmX2[rMin];
                    //sosM1X2 = p.playerScoring.getSOSMX2(r) - osmX2[rMin];
                    sosmM2X2 = sosmM1X2 - osmX2[rMin2];
                }
                p.getScore().setMetric( r, PlacementCriterion.SOSMM1, sosmM1X2 );
                p.getScore().setMetric( r, PlacementCriterion.SOSMM2, sosmM2X2 );
                //p.playerScoring.setSOSMM1X2(r, sosM1X2);
                //p.playerScoring.setSOSMM2X2(r, sosM2X2);
            }
        }

        // 5) SOSOSW and SOSOSM
        for (int r = 0; r < numberOfRounds; r++) {
            for (Player p : players) {
                int sososwX2 = 0;
                int sososmX2 = 0;
                 for (int rr = 0; rr <= r; rr++) {
                    if ( p.getParticipation(rr) != Participation.PAIRED) {
                        sososwX2 += 0;
                        sososmX2 += 2 * p.getScore().getInitialMms() * (r + 1);    // TODO - suspicious scaling. Look this up.
                    } else {
                        Game g = getGame( games, rr, p );//p.playerScoring.getGame(rr);
                        Player opp = g.getOpponent( p );
                        
                        sososwX2 += opp.getScore().getMetric( r, PlacementCriterion.SOSW );
                        sososmX2 += opp.getScore().getMetric( r, PlacementCriterion.SOSM );
                        //sososwX2 += opp.playerScoring.getSOSWX2(r);
                        //sososmX2 += opp.playerScoring.getSOSMX2(r);
                    }
                }
                p.getScore().setMetric( r, PlacementCriterion.SOSOSW, sososwX2);
                p.getScore().setMetric( r, PlacementCriterion.SOSOSM, sososmX2);
                //p.playerScoring.setSSSWX2(r, sososwX2);
                //p.playerScoring.setSSSMX2(r, sososmX2);
            }
        }


        // 6)  EXT EXR
        for (int r = 0; r < numberOfRounds; r++) {
            for (Player p : players) {
                int extX2 = 0;
                int exrX2 = 0;
                for (int rr = 0; rr <= r; rr++) {
                    if ( p.getParticipation(rr) != Participation.PAIRED) continue;
                    Game g = getGame( games, rr, p );//p.playerScoring.getGame(rr);
                    Player opp = g.getOpponent(p);
                    boolean spWasWhite = ( g.getWhitePlayer() == p );

                    int realHd = g.getHandicap();
                    if (!spWasWhite) {
                        realHd = -realHd;
                    }
                    int naturalHd = p.getRank().getValue() - opp.getRank().getValue();
                    int coef = 0;
                    if (realHd - naturalHd <= 0) {
                        coef = 0;
                    }
                    if (realHd - naturalHd == 0) {
                        coef = 1;
                    }
                    if (realHd - naturalHd == 1) {
                        coef = 2;
                    }
                    if (realHd - naturalHd >= 2) {
                        coef = 3;
                    }
                    extX2 += opp.getScore().getMetric( r, PlacementCriterion.NBW ) * coef;
                    //extX2 += opp.playerScoring.getNBWX2(r) * coef;
                    boolean bWin = false;
                    if ( spWasWhite  
                            && (g.getResult().getWhiteResult() == PlayerResult.WIN ) ) {
                        bWin = true;
                    }
                    if (!spWasWhite 
                            && (g.getResult().getBlackResult() == PlayerResult.WIN ) ) {
                        bWin = true;
                    }
                    if (bWin) {
                        exrX2 += opp.getScore().getMetric( r, PlacementCriterion.NBW ) * coef;
                        //exrX2 += opp.playerScoring.getNBWX2(r) * coef;
                    }
                }
                p.getScore().setMetric( r, PlacementCriterion.EXT, extX2 );
                p.getScore().setMetric( r, PlacementCriterion.EXR, exrX2 );
                
                //p.playerScoring.setEXTX2(r, extX2);
                //p.playerScoring.setEXRX2(r, exrX2);
            }
        }
    }    
    
    public static Game getGame( ArrayList<Game> games, int roundIndex, Player p ) {
        for ( Game g : games ) {
            if ( ( g.getRoundIndex() == roundIndex ) && g.playersInclude( p ) ) {
                return g;
            }
        }
        return null;
    }
    
    
    
    /*
     * TODO
     * Some refactoring of this and scorePlayers is in order. The one giant 
     * method may serve better than two as one is not usually called without the 
     * other.
     */
    public static void scorePlayers2( ArrayList<Player> players, int roundIndex, ArrayList<Game> games, PlacementProperties placementProps, PairingProperties pairingProps, int mainScoreMin, int mainScoreMax ) {
        //GeneralParameterSet gps = tournamentParameterSet.getGeneralParameterSet();
        //PairingParameterSet paiPS = tournamentParameterSet.getPairingParameterSet();

        // What is the main score in this tps ?
        PlacementCriterion mainCrit = placementProps.mainCriterion();

        // Form groups based on score up to this round.
        int groupNumber = 0;
        for (int mainScore = mainScoreMax; mainScore >= mainScoreMin; mainScore--) {
            ArrayList<Player> groupPlayers = new ArrayList<>();
            for (Player p : players) {
                if ( (p.getScore().getMetric( (roundIndex-1), mainCrit ) / 2) != mainScore) {
                        //getCritValue(mainCrit, roundIndex - 1) / 2 != mainScore) {
                    continue;
                }
                groupPlayers.add(p);
            }
            if (groupPlayers.isEmpty()) {
                continue;
            }

            // Sort alSPGroup to give each SPlayer an innerPlacement
            
            // sort is made according to pps criteria + specified additional criteria
            PlacementCriterion[] paiCrit = new PlacementCriterion[ placementProps.getPlaCriteria().length + 1 ];
            System.arraycopy(placementProps.getPlaCriteria(), 0, paiCrit, 0, placementProps.getPlaCriteria().length);
            
            // Add additional rating sort if round is below specified threshold
            PlacementCriterion additionalCriterion = PlacementCriterion.NULL;
            if ( roundIndex <= pairingProps.getLastRoundForSeedSystem1() ) {
                additionalCriterion = PlacementCriterion.RATING;
            }
            
            paiCrit[paiCrit.length - 1] = additionalCriterion;
            
            PlayerComparator comparator = new PlayerComparator(paiCrit, roundIndex - 1, false);
            Collections.sort( groupPlayers, comparator );
            // Now, we can store group infos into player
            for ( Player p : groupPlayers ) {
                p.getScore().groupNumber = groupNumber;
                p.getScore().groupSize = groupPlayers.size();
                p.getScore().innerPlacement = groupPlayers.indexOf(p);
            }
            groupNumber++;
        }
        
        int numberOfGroups = groupNumber;
        for (Player p : players) {
            p.getScore().numberOfGroups = numberOfGroups;
        }

        // Compute number of DU (Draw-ups) and DD (Draw-downs)
        for (Player p : players) {
            p.getScore().nbDU = p.getScore().nbDD = 0;
        }
        if (roundIndex >= 1) {
            // prepare an Array of scores before round r
            ArrayList<Player> alTempScoredPlayers = new ArrayList<>( players );
            int nbP = alTempScoredPlayers.size();
            int[][] scoreBefore = new int[roundIndex][nbP];
            for (int r = 0; r < roundIndex; r++) {
                for (int iP = 0; iP < nbP; iP++) {
                    Player p = alTempScoredPlayers.get(iP);
                    scoreBefore[r][iP] = p.getScore().getMetric( r-1, mainCrit ) / 2;// getCritValue(mainCrit, r - 1) / 2;
                }
            }

            for (int r = 0; r < roundIndex; r++) {
                for (int iP = 0; iP < nbP; iP++) {
                    Player p = alTempScoredPlayers.get(iP);
                    Game g = getGame( games, r, p );//p.getScore().getGame(r);
                    if (g == null) {
                        continue;
                    }
                    Player opp = g.getOpponent( p );
                    
                    int iOpp = alTempScoredPlayers.indexOf(opp);
                    if (scoreBefore[r][iP] < scoreBefore[r][iOpp]) {
                        p.getScore().nbDU++;
                    }
                    if (scoreBefore[r][iP] > scoreBefore[r][iOpp]) {
                        p.getScore().nbDD++;
                    }
                }
            }
        }
    }
    
    
    
}
