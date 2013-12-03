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
        updateBandProperties();
        
        tfMMBar.setText( tournament.getPairingProps().getMMBar().toString() );
        tfMMFloor.setText( tournament.getPairingProps().getMMFloor().toString() );
        
        populateTree();
        
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
        
        jTextField1.setText( Integer.toString( tournament.getBandOffset( selectedBand ) ) );
        jTextField2.setText( Integer.toString( selectedBand.getSpacing() ) );
        
    }
    
    private void setEnabledBandPropertiesControls( boolean enabled ) {
        for ( Component c : jPanel2.getComponents() ) {
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
    
    private void modifyBandOffset( int newValue ) {
        if ( selectedBand == null ) {
            return;
        }
        tournament.modifyBandOffset( selectedBand, newValue );
        updateBandProperties();
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new JTree() {
            protected void setExpandedState(TreePath path, boolean state) {
                // Ignore requests to collapse.
                if (state) {
                    super.setExpandedState(path, state);
                }
            }
        };
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblMMBar2 = new javax.swing.JLabel();
        lblMMFloor2 = new javax.swing.JLabel();
        tfMMBar = new javax.swing.JTextField();
        tfMMFloor = new javax.swing.JTextField();
        jSpinner1 = new javax.swing.JSpinner();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jTree1.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jTree1.setDragEnabled(true);
        jTree1.setRootVisible(false);
        jTree1.setToggleClickCount(0);
        jScrollPane1.setViewportView(jTree1);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Automatic Banding"));
        jPanel1.setToolTipText("");

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setLabelFor(jSpinner1);
        jLabel1.setText("Player per band");
        jLabel1.setToolTipText("Target number of players in each band.");

        lblMMBar2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        lblMMBar2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblMMBar2.setLabelFor(tfMMBar);
        lblMMBar2.setText("McMahon Bar");
        lblMMBar2.setToolTipText("Players at or above this rank will all be in the same band. Accepts kyu/dan value: 1k, 20k, 5d.");

        lblMMFloor2.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        lblMMFloor2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblMMFloor2.setLabelFor(tfMMFloor);
        lblMMFloor2.setText("McMahon Floor");
        lblMMFloor2.setToolTipText("Players at or below this rank will all be in the same band. Accepts kyu/dan value: 1k, 20k, 5d.");

        tfMMBar.setToolTipText("");
        tfMMBar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfMMBarFocusLost(evt);
            }
        });

        tfMMFloor.setToolTipText("");
        tfMMFloor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfMMFloorFocusLost(evt);
            }
        });

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
        jSpinner1.setToolTipText("");

        jButton1.setText("Create Bands");
        jButton1.setToolTipText("");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblMMFloor2, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblMMBar2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(tfMMFloor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                                .addComponent(tfMMBar, javax.swing.GroupLayout.Alignment.LEADING)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfMMBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMMBar2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfMMFloor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMMFloor2))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Band Properties"));

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel3.setLabelFor(jTextField1);
        jLabel3.setText("MMS Offset");
        jLabel3.setToolTipText("The difference in score between this band and the topmost band.");

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        jLabel4.setLabelFor(jTextField2);
        jLabel4.setText("Spacing");
        jLabel4.setToolTipText("The difference in score between this band and the next highest band.");

        jTextField1.setToolTipText("");
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });

        jTextField2.setToolTipText("");

        jButton2.setText("Increase");
        jButton2.setToolTipText("");
        jButton2.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Decrease");
        jButton3.setToolTipText("");
        jButton3.setMargin(new java.awt.Insets(2, 0, 2, 0));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(75, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(273, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(69, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int numberOfBands = (int) jSpinner1.getValue();
        tournament.createBands( numberOfBands );
        
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

    private void tfMMBarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfMMBarFocusLost
        tournament.getPairingProps().setMMBar(tfMMBar.getText());
        tfMMBar.setText(tournament.getPairingProps().getMMBar().toString());
        tournament.setChangedSinceLastSave(true);
    }//GEN-LAST:event_tfMMBarFocusLost

    private void tfMMFloorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfMMFloorFocusLost
        tournament.getPairingProps().setMMFloor(tfMMFloor.getText());
        tfMMFloor.setText(tournament.getPairingProps().getMMFloor().toString());
        tournament.setChangedSinceLastSave(true);
    }//GEN-LAST:event_tfMMFloorFocusLost

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
        int newOffset;
        try {
            newOffset = Integer.parseInt( jTextField1.getText() );
        } catch ( NumberFormatException ex ) {
            updateBandProperties();
            return;
        }
        modifyBandOffset( newOffset );
        populateTree();
    }//GEN-LAST:event_jTextField1FocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTree jTree1;
    private javax.swing.JLabel lblMMBar2;
    private javax.swing.JLabel lblMMFloor2;
    private javax.swing.JTextField tfMMBar;
    private javax.swing.JTextField tfMMFloor;
    // End of variables declaration//GEN-END:variables
}



