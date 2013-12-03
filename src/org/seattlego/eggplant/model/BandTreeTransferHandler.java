package org.seattlego.eggplant.model;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author Topsy
 */
public class BandTreeTransferHandler extends TransferHandler {
    DataFlavor nodesFlavor;
    DataFlavor[] flavors = new DataFlavor[1];
    DefaultMutableTreeNode[] nodesToRemove;

    public BandTreeTransferHandler() {
        try {
            String mimeType = DataFlavor.javaJVMLocalObjectMimeType +
                              ";class=\"" +
                javax.swing.tree.DefaultMutableTreeNode[].class.getName() +
                              "\"";
            nodesFlavor = new DataFlavor(mimeType);
            flavors[0] = nodesFlavor;
        } catch(ClassNotFoundException e) {
            System.out.println("ClassNotFound: " + e.getMessage());
        }
        /*
        nodesFlavor = new DataFlavor( Player.class, "player" );
        flavors[0] = nodesFlavor;
        * 
        */
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        if(!support.isDrop()) {
            return false;
        }
        if(!support.isDataFlavorSupported(nodesFlavor)) {
            return false;
        }
        
        support.setShowDropLocation(true);

        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        
        
        // Do not allow dropping on root node.
        TreePath dest = dl.getPath();
        if ( dest.getPathCount() == 1 ) { return false; }
        
        
        
        
        return true;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTree tree = (JTree) c;
        TreePath[] paths = tree.getSelectionPaths();
        if(paths != null) {
            // Make up a node array of copies for transfer and
            // another for/of the nodes that will be removed in
            // exportDone after a successful drop.
            List<DefaultMutableTreeNode> copies = new ArrayList<>();
            List<DefaultMutableTreeNode> toRemove = new ArrayList<>();
            
            // Dumb copy because we're enforcing a particular depth structure
            // for this tree.
            
            for(int i = 0; i < paths.length; i++) {
                // Only player nodes...
                // It might be more sensible to test the object type in the node against Player.
                if ( paths[i].getPathCount() == 3 ) {
                    DefaultMutableTreeNode next = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
                    //copies.add( copy( next ) );
                    copies.add( next );
                    toRemove.add( next );
                    
                }
            }
            
            DefaultMutableTreeNode[] nodes =
                copies.toArray(new DefaultMutableTreeNode[copies.size()]);
            nodesToRemove =
                toRemove.toArray(new DefaultMutableTreeNode[toRemove.size()]);
            return new BandTreeTransferHandler.NodesTransferable(nodes);
        }
        return null;
    }

    
    // Defensive copy used in createTransferable.
    private DefaultMutableTreeNode copy( DefaultMutableTreeNode node) {
        return new DefaultMutableTreeNode( node.getUserObject(), false );
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        /*
        if ( action == MOVE ) {
            JTree tree = (JTree)source;
            BandTreeModel model = (BandTreeModel)tree.getModel();
            // Remove nodes saved in nodesToRemove in createTransferable.
            for(int i = 0; i < nodesToRemove.length; i++) {
                model.removeNodeFromParent(nodesToRemove[i]);
            }
        }*/
    }

    @Override
    public int getSourceActions(JComponent c) {
        return MOVE;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        if(!canImport(support)) {
            return false;
        }
        
        // Extract transfer data.
        DefaultMutableTreeNode[] nodes = null;
        try {
            Transferable t = support.getTransferable();
            nodes = (DefaultMutableTreeNode[])t.getTransferData(nodesFlavor);
        } catch(UnsupportedFlavorException ufe) {
            System.out.println("UnsupportedFlavor: " + ufe.getMessage());
        } catch(java.io.IOException ioe) {
            System.out.println("I/O error: " + ioe.getMessage());
        }
        // Get drop location info.
        JTree.DropLocation dl = (JTree.DropLocation)support.getDropLocation();
        TreePath dest = dl.getPath();
        
        while ( dest.getPathCount() > 2 ) {
            dest = dest.getParentPath();
        }
        
        DefaultMutableTreeNode newParent = (DefaultMutableTreeNode)dest.getLastPathComponent();
        
        JTree tree = (JTree)support.getComponent();
        BandTreeModel model = (BandTreeModel)tree.getModel();
        
        Logger.getLogger( BandTreeTransferHandler.class.getName() ).log( Level.INFO, nodes.toString() );
        
        
        // Update Band/Player.
        for(int i = 0; i < nodes.length; i++) {
            // Do not attempt to insert node that is already a child of this parent.
            DefaultMutableTreeNode oldParent = (DefaultMutableTreeNode) nodes[i].getParent();
            if ( oldParent == newParent ) { continue; }
            
            Player player = (Player) nodes[i].getUserObject();
            
            Band oldBand = (Band) oldParent.getUserObject();
            oldBand.removePlayer( player );
            
            Band newBand = (Band) newParent.getUserObject();
            newBand.addPlayer( player );
            
        }
        
        // Fire a changed event, so that empty bands get cleaned up and tree
        // gets repopulated. This full repopulation is perhaps a bit overkill.
        model.nodeChanged( (TreeNode) model.getRoot() );
        
        return true;
    }

    public class NodesTransferable implements Transferable {
        DefaultMutableTreeNode[] nodes;

        public NodesTransferable(DefaultMutableTreeNode[] nodes) {
            this.nodes = nodes;
         }

        @Override
        public Object getTransferData(DataFlavor flavor)
                                 throws UnsupportedFlavorException {
            if(!isDataFlavorSupported(flavor))
                throw new UnsupportedFlavorException(flavor);
            return nodes;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return nodesFlavor.equals(flavor);
        }
    }
}
