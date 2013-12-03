package org.seattlego.eggplant.model.comparators;

import java.util.Comparator;
import org.seattlego.eggplant.model.Band;

/**
 *
 * @author Topsy
 */
public class BandComparator implements Comparator<Band> {

    
    public BandComparator(){
    }

    @Override
    public int compare( Band b1, Band b2){
        // We want values in descending order, so b2 and b1 are reversed.
        return Integer.compare( b2.getMms(), b1.getMms() );
    }
}
