package org.seattlego.eggplant.forms;

import java.awt.Component;
import javax.swing.DropMode;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.seattlego.eggplant.model.Band;
import org.seattlego.eggplant.model.BandSpacingScheme;
import org.seattlego.eggplant.model.BandTreeModel;
import org.seattlego.eggplant.model.BandTreeTransferHandler;


/**
 * Tool for setting initial MMS values.
 * 
 * @author Topsy
 */
public class Bands extends EggplantForm {
    
    private Band selectedBand;

    public Bands() {
        selectedBand = null;
        
        initComponents();
        init();
    }

    /*
     * Any configuration that is not covered in the auto-code.
     */
    private void init() {
        
        jTree1.setDragEnabled(true);
        jTree1.setDropMode( DropMode.ON_OR_INSERT );
        jTree1.getSelectionModel().setSelectionMode( TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION );
        jTree1.setTransferHandler(new BandTreeTransferHandler());
        BandTreeModel treeModel = new BandTreeModel();
        jTree1.setModel( treeModel );
        
        treeModel.addTreeModelListener( new TreeModelListener() {
            @Override
            public void treeStructureChanged(TreeModelEvent e) {
            }
            @Override
            public void treeNodesChanged(TreeModelEvent e) {
                treeModelChanged();
            }
            @Override
            public void treeNodesInserted(TreeModelEvent e) {}
            @Override
            public void treeNodesRemoved(TreeModelEvent e) {}
        });
        
        jTree1.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                updateBandProperties();
            }
        });
        
    }
    
    private void treeModelChanged() {
        tournament.updateBands();
        populateTree();
    }
    
    @Override
    protected void updateControls() {
        /*
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) ( jTree1.getModel() ).getRoot();
        root.add( new DefaultMutableTreeNode("foo") );
        
        Logger.getLogger( Bands.class.getName() ).log( Level.INFO, root.toString() );

        jTree1.revalidate();
        * 
        */
        if ( tournament.getPairingProps().getBandSpacingScheme() == BandSpacingScheme.STACKED ) {
            modifyBandPanel.setVisible(true);
        } else {
            modifyBandPanel.setVisible(false);
        }
        
        
        
        populateTree();
        
        updateBandProperties();
    }
    
    private void updateBandProperties() {
        // Validate the selection.
        // A single node with a band as the user object must be selected.
        if ( jTree1.getSelectionCount() != 1 ) {
            selectedBand = null;
            setEnabledBandPropertiesControls( false );
            return;
        }
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) jTree1.getLastSelectedPathComponent();
        if ( ! (selectedNode.getUserObject() instanceof Band) ) {
            selectedBand = null;
            setEnabledBandPropertiesControls( false );
            return;
        }
        
        setEnabledBandPropertiesControls( true );
        selectedBand = (Band) selectedNode.getUserObject();
        
        jTextField1.setText( Integer.toString( selectedBand.getOffset() ) );
        jTextField2.setText( Integer.toString( selectedBand.getSpacing() ) );
        
    }
    
    private void setEnabledBandPropertiesControls( boolean enabled ) {
        for ( Component c : modifyBandPanel.getComponents() ) {
            c.setEnabled( enabled );
        }
    }
    
    private void populateTree() {
        
        BandTreeModel treeModel = (BandTreeModel) jTree1.getModel();
        int[] selection = jTree1.getSelectionRows();
        
        treeModel.sync( tournament.getBands() );
        treeModel.reload();
        
        for (int i = 0; i < jTree1.getRowCount(); i++) {
            jTree1.expandRow(i);
        }
        
        jTree1.setSelectionRows( selection );
        
    }
    
    private void modifyBandSpacing( int amount ) {
        if ( selectedBand == null ) {
            return;
        }
        
        int spacing = selectedBand.getSpacing();
        spacing += amount;
        
        if ( spacing<1 ) { spacing = 1; }
        
        selectedBand.setSpacing( spacing );
        tournament.updateBands();
        updateBandProperties();
    }
    
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bandStyleButtonGroup = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new JTree() {
            protected void setExpandedState(TreePath path, boolean state) {
                // Ignore requests to collapse.
                if (state) {
                    super.setExpandedState(path, state);
                }
            }
        };
        modifyBandPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jTree1.setDragEnabled(true);
        jTree1.setRootVisible(false);
        jTree1.setToggleClickCount(0);
        jScrollPane1.setViewportView(jTree1);

        modifyBandPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Modify Band"));

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setLabelFor(jTextField1);
        jLabel3.setText("Score Offset");
        jLabel3.setToolTipText("The difference in score between this band and the topmost band.");

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setLabelFor(jTextField2);
        jLabel4.setText("Spacing");
        jLabel4.setToolTipText("The difference in score between this band and the next highest band.");

        jTextField1.setEditable(false);
        jTextField1.setToolTipText("");

        jTextField2.setToolTipText("");

        jButton2.setText("+");
        jButton2.setToolTipText("");
        jButton2.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("-");
        jButton3.setToolTipText("");
        jButton3.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Split");
        jButton4.setToolTipText("");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout modifyBandPanelLayout = new javax.swing.GroupLayout(modifyBandPanel);
        modifyBandPanel.setLayout(modifyBandPanelLayout);
        modifyBandPanelLayout.setHorizontalGroup(
            modifyBandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(modifyBandPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(modifyBandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(modifyBandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(modifyBandPanelLayout.createSequentialGroup()
                        .addGroup(modifyBandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField2)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        modifyBandPanelLayout.setVerticalGroup(
            modifyBandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(modifyBandPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(modifyBandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(modifyBandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addGap(31, 31, 31))
        );

        jButton1.setText("Reset Bands");
        jButton1.setToolTipText("");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(modifyBandPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(300, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(modifyBandPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addGap(42, 42, 42))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        tournament.resetBands();
        
        populateTree();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        modifyBandSpacing( 1 );
        populateTree();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        modifyBandSpacing( -1 );
        populateTree();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // Split a band
        if ( selectedBand == null ) {
            return;
        }
        
        tournament.splitBand( selectedBand );
        
        updateBandProperties();
        
        populateTree();

    }//GEN-LAST:event_jButton4ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bandStyleButtonGroup;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTree jTree1;
    private javax.swing.JPanel modifyBandPanel;
    // End of variables declaration//GEN-END:variables
}



