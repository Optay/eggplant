package org.seattlego.eggplant.model.comparators;

import java.util.Comparator;
import org.seattlego.eggplant.model.PlacementCriterion;
import org.seattlego.eggplant.model.Player;

/**
 * implements comparison methods between 2 Player
 * according to criteria.
 * if strict == true, comparison is made on placement criteria only
 * if strict == false, players are compared according alphabetical order of name and first name if criteria evaluate as equivalent
 *
 * Note that PlayerComparator returns an inverted sort: highest values are at
 * the beginning of the list.
 * 
 * @author Vannier
 * @author Topsy
 */
public class PlayerComparator implements Comparator<Player> {

    private PlacementCriterion[] criteria = null;
    private int roundIndex = 0;
    private boolean strict = false;
    
    public PlayerComparator( PlacementCriterion[] crit, int roundIndex, boolean strict ){
        criteria = new PlacementCriterion[crit.length];
        System.arraycopy(crit, 0, criteria, 0, crit.length);
        this.roundIndex = roundIndex;
        this.strict = strict;
    }

    @Override
    public int compare(Player p1, Player p2){
        // Note inversion of comparison value here so highest scores are first.
        int result = -betterByScore(p1, p2);
        if (result != 0) return result;

        if (strict) return 0;

        if (p1.getId().getName().compareTo(p2.getId().getName()) > 0) return 1;
        else if (p1.getId().getName().compareTo(p2.getId().getName()) < 0) return -1;
        if (p1.getId().getFirstName().compareTo(p2.getId().getFirstName()) > 0) return 1;
        else if (p1.getId().getFirstName().compareTo(p2.getId().getFirstName()) < 0) return -1;

        return 0;
    }

    /*
     * Compares metrics in order. Only returns 0 if all metric comparisons 
     * return 0.
     */
    public int betterByScore(Player p1, Player p2){
        for (int cr = 0; cr < criteria.length; cr++){
            int comparison;
            // Rank and Rating are not stored with the other metrics and must be pulled separately.
            switch ( criteria[cr] ) {
                case RANK:
                    comparison = p1.getRank().compareTo( p2.getRank() );
                    if ( comparison != 0 ) return comparison;
                    break;
                case RATING:
                    comparison = p1.getId().getRating().compareTo( p2.getId().getRating() );
                    if ( comparison != 0 ) return comparison;
                    //
                    break;
                default:
                    // Standardized metrics
                    comparison = Integer.compare( p1.getScore().getMetric( roundIndex, criteria[cr] ), p2.getScore().getMetric( roundIndex, criteria[cr] ) );
                    if ( comparison !=0 ) return comparison;
            }
            //if (p1.playerScoring.getCritValue(criteria[cr], roundIndex) < p2.playerScoring.getCritValue(criteria[cr], roundNumber)) return 1;
            //else if (p1.playerScoring.getCritValue(criteria[cr], roundIndex) > p2.playerScoring.getCritValue(criteria[cr], roundNumber)) return -1;
        }
        return 0;
    }
}
