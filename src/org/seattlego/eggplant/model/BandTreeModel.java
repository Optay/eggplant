package org.seattlego.eggplant.model;

import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Band form tree model for translating tree view to and from Player objects.
 * 
 * @author Topsy
 */
public class BandTreeModel extends DefaultTreeModel {
    
    
    public BandTreeModel() {
        super( new DefaultMutableTreeNode( "root", true ), true);
    }
    
    /*
     * Update all nodes to match provided bandlist.
     */
    public void sync( ArrayList<Band> bands ) {
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) root;
        
        // Remove excess nodes
        while( rootNode.getChildCount() > bands.size() ) {
            rootNode.remove( 0 );
        }
        
        for ( int i = 0; i < bands.size(); i++ ) {
            Band band = bands.get(i);
            DefaultMutableTreeNode bandNode;
            
            if ( i < rootNode.getChildCount() ) {
                bandNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);
                bandNode.setUserObject( band );
            } else {
                bandNode = new DefaultMutableTreeNode( band, true );
                rootNode.add( bandNode );
            }
            
            syncBandNode( bandNode );

        }
    }

    private void syncBandNode( DefaultMutableTreeNode bandNode ) {
        Band band = (Band) bandNode.getUserObject();
        
        // Remove excess.
        while( bandNode.getChildCount() > band.getPlayers().size() ) {
            bandNode.remove( bandNode.getChildCount() - 1 );
        }

        // Reassign and add.
        for ( int i = 0; i < band.getPlayers().size(); i++ ) {
            Player player = band.getPlayers().get(i);
            DefaultMutableTreeNode playerNode;

            if ( i < bandNode.getChildCount() ) {
                playerNode = (DefaultMutableTreeNode) bandNode.getChildAt(i);
                playerNode.setUserObject( player );
            } else {
                playerNode = new DefaultMutableTreeNode( player , false );
                bandNode.add( playerNode );
            }
        }        
    }
    
}


