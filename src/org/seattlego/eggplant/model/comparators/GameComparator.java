package org.seattlego.eggplant.model.comparators;

import java.util.Comparator;
import org.seattlego.eggplant.model.Game;
import org.seattlego.eggplant.model.PlacementCriterion;
import org.seattlego.eggplant.model.Player;
import org.seattlego.eggplant.model.Rank;


public class GameComparator implements Comparator<Game> {
    public final static int NO_ORDER = 0;
//    public final static int GAME_NUMBER_ORDER = 1;
    public final static int TABLE_NUMBER_ORDER = 2;
//    public final static int BEST_RATING_ORDER = 3;
    public final static int BEST_MMS_ORDER = 4;

    int gameOrderType = GameComparator.NO_ORDER;

    public GameComparator(int gameOrderType){
        this.gameOrderType = gameOrderType;
    }

    @Override
    public int compare(Game g1, Game g2){
        Player wP1 = g1.getWhitePlayer();
        Player bP1 = g1.getBlackPlayer();
        Player wP2 = g2.getWhitePlayer();
        Player bP2 = g2.getBlackPlayer();
        switch (gameOrderType){
            case TABLE_NUMBER_ORDER :
                if (g1.getTableIndex() < g2.getTableIndex()) return -1;
                else if (g1.getTableIndex() > g2.getTableIndex()) return 1;
                else return 0;
//            case BEST_RATING_ORDER :
//                best1 = wP1.getRating();
//                if (bP1.getRating() > best1) best1 = bP1.getRating();
//                best2 = wP2.getRating();
//                if (bP2.getRating() > best1) best2 = bP2.getRating();
//                if (best1 < best2) return 1;
//                else return -1;
            case BEST_MMS_ORDER :
                int wMms1 = wP1.getScore().getMetric( g1.getRoundIndex()-1, PlacementCriterion.MMS );
                int bMms1 = bP1.getScore().getMetric( g1.getRoundIndex()-1, PlacementCriterion.MMS );
                int mms1 = Math.max(wMms1, bMms1);
                int wMms2 = wP2.getScore().getMetric( g2.getRoundIndex()-1, PlacementCriterion.MMS );
                int bMms2 = bP2.getScore().getMetric( g2.getRoundIndex()-1, PlacementCriterion.MMS );
                int mms2 = Math.max(wMms2, bMms2);
                if (mms1 < mms2) return 1;
                if (mms1 > mms2) return -1;
                
                // If MM scores are equal, compare rank.
                // Rating is not used because a new player will have a null rating (30k).
                // Rank is a more accurate reflection of strength for this comparison at this point.
                Rank rank1, rank2;
                rank1 = Rank.max( wP1.getRank(), bP1.getRank() );
                rank2 = Rank.max( wP2.getRank(), bP2.getRank() );
                int compare = rank1.compareTo(rank2);
                if ( compare != 0 ) { return -compare; }    // Invert, so sort returns strongest (largest) first.
                
                // last artificial criterion to ensure a deterministic order
                String str1 = wP1.getKeyString();
                String str2 = wP2.getKeyString();
                if (str1.compareTo(str2) >= 0) return 1;
                else return -1;
            default :
                    return 0;

        }
    }
}
