package org.seattlego.eggplant.model;

import java.util.ArrayList;

/**
 * Contains methods for pairing a group of players. This class exists to keep 
 * Tournament from becoming bloated.
 * 
 * @author Topsy
 */
public class Pairer {
    public static final int PAIRING_GROUP_MIN_SIZE = 100;
    public static final int PAIRING_GROUP_MAX_SIZE = 3 * PAIRING_GROUP_MIN_SIZE; // must be >= 2 * PAIRING_GROUP_MIN_SIZE

    /*
     * Pairs a group of players.
     * @return ArrayList<Game> Pairings of the provided players.
     */
    public static ArrayList<Game> pairPlayers(ArrayList<Player> groupPlayers, int roundIndex, ArrayList<Game> previousGames, PairingProperties pairingProps ) {
        //Logger.getLogger( Pairer.class.getName() ).log( Level.INFO, "pairPlayers " + groupPlayers.size() );
        
        int numberOfPlayersInGroup = groupPlayers.size();

//      Prepare infos about Score groups : sgSize, sgNumber and innerPosition
//      And DUDD information

        long[][] costs = new long[numberOfPlayersInGroup][numberOfPlayersInGroup];
        for (int i = 0; i < numberOfPlayersInGroup; i++) {
            costs[i][i] = 0;
            for (int j = i + 1; j < numberOfPlayersInGroup; j++) {
                Player p1 = groupPlayers.get(i);
                Player p2 = groupPlayers.get(j);
                costs[i][j] = costs[j][i] = costValue(p1, p2, roundIndex, previousGames, pairingProps );
            }
        }

        // match
        int[] mate = WeightedMatchLong.weightedMatchLong(costs, WeightedMatchLong.MAXIMIZE);

        ArrayList<Game> alG = new ArrayList<>();
        // define the games
        for (int i = 1; i <= costs.length; i++) {
            if (i < mate[i]) {
                Player p1 = groupPlayers.get(i - 1);
                Player p2 = groupPlayers.get(mate[i] - 1);

                Game g = gameBetween(p1, p2, roundIndex, pairingProps, previousGames );
                alG.add(g);
            }
        }
        
        //Logger.getLogger( Pairer.class.getName() ).log( Level.INFO, "pairPlayers " + alG.size() );
        
        return alG;
    }

    private static long costValue(Player p1, Player p2, int roundIndex, ArrayList<Game> games, PairingProperties pairingProps ) {
        
        

        long cost = 1L;   // 1 is minimum value because 0 means "no matching allowed"

        // Base Criterion n° 1 : Avoid Duplicating Game
        // Did p1 and p2 already play ?
        //
        int numberOfPreviousGamesP1P2 = 0;

        for (int r = 0; r < roundIndex; r++) {
            Game game = Scorer.getGame( games, r, p1 );//p1.getScore().getGame(r);
            if (game == null) {
                continue;
            }
            if ( game.playersInclude(p1) && game.playersInclude(p2) ) {
                numberOfPreviousGamesP1P2++;
            }
        }
        if (numberOfPreviousGamesP1P2 == 0) {           // TODO - Should this be != 0? Which way does cost go?
            cost += pairingProps.getDuplicateGameCost();
        }

        // Base Criterion n° 2 : Random
        long nR;
        if (pairingProps.getIsDeterministic()) {
            nR = Pairer.detRandom(pairingProps.getRandomCost(), p1, p2);
        } else {
            nR = Pairer.nonDetRandom(pairingProps.getRandomCost());
        }
        cost += nR;

        // Base Criterion n° 3 : Balance W and B
        // This cost is never applied if potential Handicap != 0
        // It is fully applied if wbBalance(sP1) and wbBalance(sP2) are strictly of different signs
        // It is half applied if one of wbBalance is 0 and the other is >=2

        long bwBalanceCost = 0;
        Game g = gameBetween(p1, p2, roundIndex, pairingProps, games );
        int potHd = g.getHandicap();
        if (potHd == 0) {
            int wb1 = Pairer.wbBalance(p1, roundIndex - 1, games);
            int wb2 = Pairer.wbBalance(p2, roundIndex - 1, games);
            if (wb1 * wb2 < 0)
                bwBalanceCost = pairingProps.getColorBalanceCost();
            else if (wb1 == 0 && Math.abs(wb2) >= 2)
                bwBalanceCost = pairingProps.getColorBalanceCost()/2;
            else if (wb2 == 0 && Math.abs(wb1) >= 2)
                bwBalanceCost = pairingProps.getColorBalanceCost()/2;
        }
        cost += bwBalanceCost;


        // Main Criterion n° 1 : Avoid mixing categories
        long catCost = 0;
        /*
         * Categories are not supported in Eggplant.
        
        int numberOfCategories = gps.getNumberOfCategories();
        if (numberOfCategories > 1) {
            // cost is f(x) = (1-x) * (1 + kx) where  0<=x<=1 and k is the NX1 factor 0<=k<=1
            double x = (double) Math.abs(p1.category(gps) - p2.category(gps)) / (double) numberOfCategories;
            double k = pairingProps.getPaiStandardNX1Factor();
            catCost = (long) (pairingProps.getCategoryCost() * (1.0 - x) * (1.0 + k * x));
            // But if both players have lost 1 or more games, that is less important (added in 3.11)
            if (roundIndex > 0) {
                if (p1.getScore().getNBWX2(roundIndex - 1) < roundIndex && p2.getScore().getNBWX2(roundIndex - 1) < roundIndex) {
                    catCost /= 3;
                }
            }
        }
        * 
        */

        cost += catCost;

        // Main Criterion n° 2 : Minimize score difference
        long scoCost = 0;
        int scoRange = p1.getScore().numberOfGroups;
        
        // Everyone is in the same category.
        //if (p1.category(gps) == p2.category(gps)) {
        if (true) {     // Badly-named local variables x, k are used elsewhere. The conditional block keeps scopes isolated.
            double x = (double) Math.abs(p1.getScore().groupNumber - p2.getScore().groupNumber) / (double) scoRange;
            double k = pairingProps.getPaiStandardNX1Factor();
            scoCost = (long) (pairingProps.getScoreDifferenceCost() * (1.0 - x) * (1.0 + k * x));
        }
        cost += scoCost;

        // Main Criterion n° 3 : If different groups, make a directed Draw-up/Draw-down
        long duddCost = 0;
        if (Math.abs(p1.getScore().groupNumber - p2.getScore().groupNumber) < 4 &&
                p1.getScore().groupNumber != p2.getScore().groupNumber) {
            // 4 scenarii
            // scenario = 0 : One of the players has already been drawn in the same sense
            // scenario = 1 : normal conditions
            // scenario = 2 : it corrects a previous DU/DD
            // scenario = 3 : it corrects a previous DU/DD for both
            int scenario = 1;
            if (p1.getScore().nbDU > 0 && p1.getScore().groupNumber > p2.getScore().groupNumber) {
                scenario = 0;
            }
            if (p1.getScore().nbDD > 0 && p1.getScore().groupNumber < p2.getScore().groupNumber) {
                scenario = 0;
            }
            if (p2.getScore().nbDU > 0 && p2.getScore().groupNumber > p1.getScore().groupNumber) {
                scenario = 0;
            }
            if (p2.getScore().nbDD > 0 && p2.getScore().groupNumber < p1.getScore().groupNumber) {
                scenario = 0;
            }

            if (scenario != 0 && p1.getScore().nbDU > 0 && p1.getScore().nbDD < p1.getScore().nbDU && p1.getScore().groupNumber < p2.getScore().groupNumber) {
                scenario++;
            }
            if (scenario != 0 && p1.getScore().nbDD > 0 && p1.getScore().nbDU < p1.getScore().nbDD && p1.getScore().groupNumber > p2.getScore().groupNumber) {
                scenario++;
            }
            if (scenario != 0 && p2.getScore().nbDU > 0 && p2.getScore().nbDD < p2.getScore().nbDU && p2.getScore().groupNumber < p1.getScore().groupNumber) {
                scenario++;
            }
            if (scenario != 0 && p2.getScore().nbDD > 0 && p2.getScore().nbDU < p2.getScore().nbDD && p2.getScore().groupNumber > p1.getScore().groupNumber) {
                scenario++;
            }

            if (scenario != 0) {
                long duddWeight = pairingProps.getDuddWeight() / 3;

                PlayerScore upperSP = (p1.getScore().groupNumber < p2.getScore().groupNumber) ? p1.getScore() : p2.getScore();
                PlayerScore lowerSP = (p1.getScore().groupNumber < p2.getScore().groupNumber) ? p2.getScore() : p1.getScore();
                if (pairingProps.getDuddUpperMode() == PairingProperties.DUDD_TOP) {
                    duddCost += duddWeight / 2 * (upperSP.groupSize - 1 - upperSP.innerPlacement) / upperSP.groupSize;
                } else if (pairingProps.getDuddUpperMode() == PairingProperties.DUDD_MID) {
                    duddCost += duddWeight / 2 * (upperSP.groupSize - 1 - Math.abs(2 * upperSP.innerPlacement - upperSP.groupSize + 1)) / upperSP.groupSize;
                } else if (pairingProps.getDuddUpperMode() == PairingProperties.DUDD_BOT) {
                    duddCost += duddWeight / 2 * (upperSP.innerPlacement) / upperSP.groupSize;
                }
                if (pairingProps.getDuddLowerMode() == PairingProperties.DUDD_TOP) {
                    duddCost += duddWeight / 2 * (lowerSP.groupSize - 1 - lowerSP.innerPlacement) / lowerSP.groupSize;
                } else if (pairingProps.getDuddLowerMode() == PairingProperties.DUDD_MID) {
                    duddCost += duddWeight / 2 * (lowerSP.groupSize - 1 - Math.abs(2 * lowerSP.innerPlacement - lowerSP.groupSize + 1)) / lowerSP.groupSize;
                } else if (pairingProps.getDuddLowerMode() == PairingProperties.DUDD_BOT) {
                    duddCost += duddWeight / 2 * (lowerSP.innerPlacement) / lowerSP.groupSize;
                }

                if (scenario == 2) {
                    duddCost += duddWeight;
                }
                if (scenario == 3) {
                    duddCost += 2 * duddWeight;
                }
            }
        }
        
        // Again, no categories in Eggplant.
        // But, if players come from different categories, decrease duddCost(added in 3.11)
        //int catGap = Math.abs(p1.category(gps) - p2.category(gps));
        //duddCost = duddCost / (catGap + 1) / (catGap + 1) / (catGap + 1) / (catGap + 1);

        cost += duddCost;

        // Main Criterion n° 4 : Seeding
        long seedCost = 0;
        if (p1.getScore().groupNumber == p2.getScore().groupNumber) {
            int groupSize = p1.getScore().groupSize;
            int p1InnerPlacement = p1.getScore().innerPlacement;
            int p2InnerPlacement = p2.getScore().innerPlacement;
            long maxSeedingWeight = pairingProps.getMaximizeSeedingCost();
            PairingMethod currentSeedSystem = (roundIndex <= pairingProps.getLastRoundForSeedSystem1()) ? pairingProps.getPairingScheme1() : pairingProps.getPairingScheme2();
            //int currentSeedSystem = (roundIndex <= pairingProps.getLastRoundForSeedSystem1()) ? pairingProps.getPaiMaSeedSystem1() : pairingProps.getPaiMaSeedSystem2();
            if ( currentSeedSystem == PairingMethod.SPLIT_AND_RANDOM ) {
                if ((2 * p1InnerPlacement < groupSize && 2 * p2InnerPlacement >= groupSize) || (2 * p1InnerPlacement >= groupSize && 2 * p2InnerPlacement < groupSize)) {
                    long randRange = (long) (pairingProps.getMaximizeSeedingCost() * 0.2);
                    long rand = Pairer.detRandom(randRange, p1, p2);
                    seedCost = maxSeedingWeight - rand;
                }
            } else if (currentSeedSystem == PairingMethod.SPLIT_AND_FOLD ) {
                // The best is to get cla1 + cla2 - (groupSize - 1) close to 0
                int x = p1InnerPlacement + p2InnerPlacement - (groupSize - 1);
                seedCost = maxSeedingWeight - (maxSeedingWeight * x / (groupSize - 1) * x / (groupSize - 1));
            } else if (currentSeedSystem == PairingMethod.SPLIT_AND_SLIP ) {
                // The best is to get 2 * |Cla1 - Cla2| - groupSize    close to 0
                int x = 2 * Math.abs(p1InnerPlacement - p2InnerPlacement) - groupSize;
                seedCost = maxSeedingWeight - (maxSeedingWeight * x / groupSize * x / groupSize);
            } else {
                System.out.println("Internal Error on seed system");
            }
        }
        cost += seedCost;

        // Secondary Criteria
        // Do we apply ?
        // secCase = 0 : No player is above thresholds
        // secCase = 1 : One player is above thresholds
        // secCase = 2 : Both players are above thresholds
        // pseudoMMS is MMS adjusted according to applying thresholds

        int secCase = 0;
        int nbw2Threshold;
        if (pairingProps.getSecondaryCriteriaWinThresholdActive()) {
            nbw2Threshold = pairingProps.getNumberOfRounds();
        } else {
            nbw2Threshold = 2 * pairingProps.getNumberOfRounds();
        }
        
        // Needs work
        int pseudoMmsSP1 = p1.getScore().getMetric( roundIndex-1, PlacementCriterion.MMS ) / 2;//getCritValue(PlacementParameterSet.PLA_CRIT_MMS, roundIndex - 1) / 2;
        int pseudoMmsSP2 = p2.getScore().getMetric( roundIndex-1, PlacementCriterion.MMS ) / 2;//getCritValue(PlacementParameterSet.PLA_CRIT_MMS, roundIndex - 1) / 2;
        int maxMms = pairingProps.getMMBar().toMms() + roundIndex;
        //int maxMms = gps.getGenMMBar() + PlacementParameterSet.PLA_SMMS_CORR_MAX - Gotha.MIN_RANK + roundIndex;

        int nbwSP1X2 = p1.getScore().getMetric( roundIndex-1, PlacementCriterion.NBW );//getCritValue(PlacementParameterSet.PLA_CRIT_NBW, roundIndex - 1);
        int nbwSP2X2 = p2.getScore().getMetric( roundIndex-1, PlacementCriterion.NBW );//getCritValue(PlacementParameterSet.PLA_CRIT_NBW, roundIndex - 1);

        // What happened here? The following commented code does not seem to match with this code correctly.
        // Error in translation?
        if ( (nbwSP1X2 >= nbw2Threshold) || 
             (2 * p1.getRank().getValue() + p1.getScore().getMetric( roundIndex-1, PlacementCriterion.NBW ) >= 2 * pairingProps.getSecondaryCriteriaRankThreshold() ) ) {
            secCase++;
            pseudoMmsSP1 = maxMms;
        }
        if ( (nbwSP2X2 >= nbw2Threshold) || 
             (2 * p2.getRank().getValue() + p2.getScore().getMetric( roundIndex-1, PlacementCriterion.NBW ) >= 2 * pairingProps.getSecondaryCriteriaRankThreshold() ) ) {
            secCase++;
            pseudoMmsSP2 = maxMms;
        }
        /*
        if (nbwSP1X2 >= nbw2Threshold || sP1.player.getRank() >= paiPS.getSecondaryCriteriaRankThreshold()){
        secCase++;
        pseudoMmsSP1 = maxMms;
        }
        if (nbwSP2X2 >= nbw2Threshold || sP2.player.getRank() >= paiPS.getSecondaryCriteriaRankThreshold()){
        secCase++;
        pseudoMmsSP2 = maxMms;
        }
         */
        //


        // Secondary Criterion n° 1 : Minimize handicap
        long hdCost = 0;
        int secRange;
        /*
        int tType = TournamentParameterSet.TYPE_UNDEFINED;
        try {
            tType = tournamentType();
        } catch (RemoteException ex) {
            Logger.getLogger(Tournament.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        // TODO !!! Highly suspicious. Usually for McMahon, we count the range based on bar and floor !!!
        // In this case, we appear to be using the plain round-based range for MM and the bar/floor version for non-MM.
        //if (tType == TournamentParameterSet.TYPE_MACMAHON) {
            secRange = scoRange;
        //} else {
        //    secRange = (gps.getGenMMBar() - gps.getGenMMFloor() + PlacementParameterSet.PLA_SMMS_CORR_MAX - PlacementParameterSet.PLA_SMMS_CORR_MIN) + roundIndex;
        //}

        double x = (double) Math.abs(pseudoMmsSP1 - pseudoMmsSP2) / (double) secRange;
        double k = pairingProps.getPaiStandardNX1Factor();
        hdCost = (long) (pairingProps.getMinimizeHandicapCost() * (1.0 - x) * (1.0 + k * x));

        cost += hdCost;

        // Secondary criteria n° 2 and 3 : Geographical Criteria
        int countryFactor = pairingProps.getPreferMmsDiffRatherThanSameCountry();
        double xCountry = (double) Math.abs(countryFactor + 0.1) / (double) secRange;
        if (xCountry > 1.0) {
            xCountry = 1.0;
        }
        double malusCountry = (1.0 - k) * xCountry + k * xCountry * xCountry;

        int clubFactor = pairingProps.getPreferMmsDiffRatherThanSameClub();
        double xClub = (double) Math.abs(clubFactor + 0.9) / (double) secRange;
        if (xClub > 1.0) {
            xClub = 1.0;
        }
        double malusClub = (1.0 - k) * xClub + k * xClub * xClub;

        long geoMaxCost = pairingProps.getGeoCost();
        long geoMinCost = (long) (geoMaxCost * (1.0 - Math.max(malusCountry, malusClub)));

        double malusGeo = 0.0;

        /*
        if (p1.getCountry().compareTo(p2.getCountry()) == 0) {
            malusGeo = malusCountry;
        }
        */
        
        if (p1.getId().getClub().compareTo(p2.getId().getClub()) == 0) {
            malusGeo = Math.max(malusGeo, malusClub);
        }
        
        long geoCost = (long) (geoMaxCost * (1.0 - malusGeo));

        if (secCase == 0) {
//            geoCost = geoCost;
        }
        if (secCase == 2) {
            geoCost = geoMaxCost;
        }
        if (secCase == 1) {
            geoCost = geoMinCost;
        }
        cost += geoCost;

        return cost;
    }


    /**
     * builds and return a new Game with everything defined except table index
     */
    private static Game gameBetween(Player p1, Player p2, int roundIndex, PairingProperties pairingProps, ArrayList<Game> games ) {

        Game g = new Game();

        // handicap, komi
        int komi = pairingProps.getDefaultKomi();
        int hd = 0;
        
        Rank pseudoRank1;
        Rank pseudoRank2;
        
        switch (pairingProps.getHandicapBasis()) {
            case MMS:
                // Note: MMS may not be an integer, but since we are creating a rank, we can just floor it safely.
                pseudoRank1 = Rank.RankFromMms( (int)Math.floor( p1.getScore().getMetricUnscaled( roundIndex-1, PlacementCriterion.MMS ) ) );
                pseudoRank2 = Rank.RankFromMms( (int)Math.floor( p2.getScore().getMetricUnscaled( roundIndex-1, PlacementCriterion.MMS ) ) );
                /*
                pseudoRank1 = Rank.RankFromMms( p1.getScore().getMetric( roundIndex-1, PlacementCriterion.MMS ) / PlacementCriterion.MMS.coef );
                pseudoRank2 = Rank.RankFromMms( p2.getScore().getMetric( roundIndex-1, PlacementCriterion.MMS ) / PlacementCriterion.MMS.coef );
                * 
                */
                break;
            case RANK:
            default:
                pseudoRank1 = p1.getRank();
                pseudoRank2 = p2.getRank();
        }
        
        // Bound values
        // Be careful not to adjust these references as they are not copies.
        if ( pseudoRank1.compareTo( pairingProps.getMaxHandicappedRank() ) > 0 ) { pseudoRank1 = pairingProps.getMaxHandicappedRank(); }
        if ( pseudoRank2.compareTo( pairingProps.getMaxHandicappedRank() ) > 0 ) { pseudoRank2 = pairingProps.getMaxHandicappedRank(); }
        /*
         * pre-comparable implementation
        if ( pseudoRank1.getValue() > pairingProps.getMaxHandicappedRank().getValue() ) { pseudoRank1 = pairingProps.getMaxHandicappedRank(); }
        if ( pseudoRank2.getValue() > pairingProps.getMaxHandicappedRank().getValue() ) { pseudoRank2 = pairingProps.getMaxHandicappedRank(); }
        * 
        */
        
        hd = pseudoRank1.getValue() - pseudoRank2.getValue();

        if (hd > 0) {
            hd = hd - pairingProps.getHandicapModifier();
            if (hd < 0) {
                hd = 0;
            }
        }
        if (hd < 0) {
            hd = hd + pairingProps.getHandicapModifier();
            if (hd > 0) {
                hd = 0;
            }
        }
        
        //Logger.getLogger( Pairer.class.getName() ).log( Level.INFO, Integer.toString( hd ) );
        
        // So tournament will know that handicap exceeded bounds.
        g.setUnboundHandicap( Math.abs( hd) );
        
        if (hd > pairingProps.getMaxHandicap()) {
            hd = pairingProps.getMaxHandicap();
        }
        if (hd < -pairingProps.getMaxHandicap()) {
            hd = -pairingProps.getMaxHandicap();
        }
        
        
        
        //Logger.getLogger( Pairer.class.getName() ).log( Level.INFO, Integer.toString( hd ) );
        
        if (hd > 0) {
            komi = 0;
            g.setWhitePlayer(p1);
            g.setBlackPlayer(p2);
            g.setHandicap(hd);
        } else if (hd < 0) {
            komi = 0;
            g.setWhitePlayer(p2);
            g.setBlackPlayer(p1);
            g.setHandicap(-hd);
        } else { // hd == 0
            g.setHandicap(0);
            if (Pairer.wbBalance(p1, roundIndex - 1, games) > Pairer.wbBalance(p2, roundIndex - 1, games)) {
                g.setWhitePlayer(p2);
                g.setBlackPlayer(p1);
            } else if (Pairer.wbBalance(p1, roundIndex - 1, games) < Pairer.wbBalance(p2, roundIndex - 1, games)) {
                g.setWhitePlayer(p1);
                g.setBlackPlayer(p2);
            } else { // choose color from a det random
                if (Pairer.detRandom(1, p1, p2) == 0) {
                    g.setWhitePlayer(p1);
                    g.setBlackPlayer(p2);
                } else {
                    g.setWhitePlayer(p2);
                    g.setBlackPlayer(p1);
                }
            }
        }
        g.setKnownColor(true);
        g.setResult( GameResult.UNKNOWN );
        g.setRoundIndex( roundIndex );
        g.setKomi( komi );

        return g;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * 
     * TODO - Add white balance to playerScore, and calculate it with the other
     * values. Remove this method and just compare the previously calculated 
     * white balance values directly.
     * 
     * returns the number of games played by sP as White
     * - the number of games played by sP as Black
     *   from round 0 to rn included
     */
    public static int wbBalance( Player p, int rn, ArrayList<Game> games ) {
        if (rn < 0) {
            return 0;
        }
        int balance = 0;
        for (int r = 0; r <= rn; r++) {
            Game g = Scorer.getGame( games, r, p );
            if (g == null) {
                continue;
            }
            if (g.getHandicap() != 0) {
                continue;
            }
            if ( p == g.getWhitePlayer() ) {
                balance++;
            } else {
                balance--;
            }
        }
        return balance;
    }

    public static long detRandom(long max, Player p1, Player p2) {
        long nR = 0;
        String s = p1.getId().getName() + p1.getId().getFirstName() + p2.getId().getName() + p2.getId().getFirstName();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            nR += c * (i + 1);
        }
        nR = (nR * 1234567) % (max + 1);
        return nR;
    }

    public static long nonDetRandom(long max) {
        if (max == 0) {
            return 0;
        }
        double r = Math.random() * (max + 1);
        return (long) r;
    }
    
    
    
    
    
}
