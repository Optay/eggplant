package org.seattlego.eggplant.forms;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.*;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.seattlego.eggplant.Eggplant;
import org.seattlego.eggplant.model.Game;
import org.seattlego.eggplant.model.Participation;
import org.seattlego.eggplant.model.Player;
import org.seattlego.eggplant.model.Rank;
import org.seattlego.eggplant.printing.CustomPrintableTable;

/**
 *
 * @author Topsy
 */
public class PairingsManager extends EggplantForm {

    private ObservableList<Player> pairablePlayers;
    private ObservableList<Player> unpairablePlayers;
    private ObservableList<Game> games;
    
    public PairingsManager() {
        initComponents();
        
        init();
    }

    /*
     * Any configuration that is not covered in the auto-code.
     */
    private void init() {
        initRoundPane();
        roundPanel.add( roundPane );
        
        
        // Set up table bindings
        pairablePlayers = ObservableCollections.observableList( new ArrayList<Player>() );
        
        // prevents table widths from being reset when data is updated
        tblPairablePlayers.setAutoCreateColumnsFromModel(false);
        JTableBinding jTableBinding = SwingBindings.createJTableBinding( AutoBinding.UpdateStrategy.READ, pairablePlayers, tblPairablePlayers, "pairablePlayersBinding");
        JTableBinding.ColumnBinding columnBinding;
        columnBinding = jTableBinding.addColumnBinding(ELProperty.create("${id.fullName}"));
        columnBinding.setColumnName("Name");
        columnBinding.setColumnClass( String.class );
        columnBinding = jTableBinding.addColumnBinding(ELProperty.create("${rank}"));
        columnBinding.setColumnName("Rank");
        columnBinding.setColumnClass( Rank.class );
        jTableBinding.bind();        
        //
        
        TableRowSorter trsPairablePlayers = new TableRowSorter( tblPairablePlayers.getModel() );
        tblPairablePlayers.setRowSorter( trsPairablePlayers );
        
        // TODO - EEK! Hardcoded column numbers. For shame... Find these columns by name perhaps.
        // This default sort is by table number which is in column 1.
        RowSorter.SortKey defaultSort = new RowSorter.SortKey( 1 , SortOrder.DESCENDING );
        trsPairablePlayers.setSortKeys( Arrays.asList( defaultSort ) );
        //        
        
        
        // Unpairable Players table
        unpairablePlayers = ObservableCollections.observableList( new ArrayList<Player>() );
        
        // prevents table widths from being reset when data is updated
        tblUnpairablePlayers.setAutoCreateColumnsFromModel(false);

        JTableBinding unpairableBinding = SwingBindings.createJTableBinding( AutoBinding.UpdateStrategy.READ, unpairablePlayers, tblUnpairablePlayers, "unpairablePlayersBinding");
        columnBinding = unpairableBinding.addColumnBinding(ELProperty.create("${id.fullName}"));
        columnBinding.setColumnName("Name");
        columnBinding.setColumnClass( String.class );
        columnBinding = unpairableBinding.addColumnBinding(ELProperty.create("${rank}"));
        columnBinding.setColumnName("Rank");
        columnBinding.setColumnClass( Rank.class );
        unpairableBinding.bind();        
        //
        
/*        TableRowSorter trsUnpairablePlayers = new TableRowSorter( tblUnpairablePlayers.getModel() );
        // TODO - EEK! Hardcoded column numbers. For shame... Find these columns by name perhaps.
        trsUnpairablePlayers.setComparator( 1, compRank );
        * 
        */
        
        TableRowSorter trsUnpairablePlayers = new TableRowSorter( tblUnpairablePlayers.getModel() );
        // TODO - EEK! Hardcoded column numbers. For shame... Find these columns by name perhaps.
        tblUnpairablePlayers.setRowSorter( trsUnpairablePlayers );
        
        // Same as pairable
        trsUnpairablePlayers.setSortKeys( Arrays.asList( defaultSort ) );
        
/*        RowSorter.SortKey defaultSort = new RowSorter.SortKey( 1 , SortOrder.DESCENDING );
        trsPairablePlayers.setSortKeys( Arrays.asList( defaultSort ) );
        * 
        */
        //
        
        
        
        
        // Games table
        games = ObservableCollections.observableList( new ArrayList<Game>() );
        
        // prevents table widths from being reset when data is updated
        tblGames.setAutoCreateColumnsFromModel(false);
        
        JTableBinding gamesBinding = SwingBindings.createJTableBinding( AutoBinding.UpdateStrategy.READ, games, tblGames, "gamesBinding");
        columnBinding = gamesBinding.addColumnBinding(ELProperty.create("${tableNumber}"));
        columnBinding.setColumnName("Table");
        columnBinding.setColumnClass( Integer.class );
        columnBinding = gamesBinding.addColumnBinding(ELProperty.create("${whitePlayer.id.fullName}"));
        columnBinding.setColumnName("White");
        columnBinding = gamesBinding.addColumnBinding(ELProperty.create("${blackPlayer.id.fullName}"));
        columnBinding.setColumnName("Black");
        columnBinding = gamesBinding.addColumnBinding(ELProperty.create("${handicapString}"));
        columnBinding.setColumnName("Handicap");
        columnBinding = gamesBinding.addColumnBinding(ELProperty.create("${komiString}"));
        columnBinding.setColumnName("Komi");
        gamesBinding.bind();
        
        tblGames.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                //Logger.getLogger( PairingsManager.class.getName() ).log( Level.INFO, Boolean.toString( e.isPopupTrigger() ) );
                showPopup(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                //Logger.getLogger( PairingsManager.class.getName() ).log( Level.INFO, Boolean.toString( e.isPopupTrigger() ) );
                showPopup(e);
            }
            private void showPopup( MouseEvent e ) {
                if ( e.isPopupTrigger() ) {
                    // Update selection
                    int r = tblGames.rowAtPoint(e.getPoint());
                    if (r >= 0 && r < tblGames.getRowCount()) {
                        tblGames.setRowSelectionInterval(r, r);
                    } else {
                        tblGames.clearSelection();
                    }
                    // Show popu
                    gameContextMenu.show( tblGames, e.getX(), e.getY() );
                } else {
                    gameContextMenu.setVisible(false);
                }
            }
        });
        
        TableRowSorter trsGames = new TableRowSorter( tblGames.getModel() );
        // TODO - EEK! Hardcoded column numbers. For shame... Find these columns by name perhaps.
        tblGames.setRowSorter( trsGames );
        
        RowSorter.SortKey defaultTableSort = new RowSorter.SortKey( 0 , SortOrder.ASCENDING );
        trsGames.setSortKeys( Arrays.asList( defaultTableSort ) );
        
        
        //
        
        
    }
    
    @Override
    protected void selectedRoundChanged() {
        updateControls();
    }
    
    /*
     * Not correct use of binding, but it's a start.
     */
    @Override
    protected void updateControls() {
        // Pull the data for the current round.
        
        int currentRoundIndex = roundPane.getRoundIndex();
        
        
        games.clear();
        games.addAll( tournament.getGames( currentRoundIndex ) );
        
        pairablePlayers.clear();
        pairablePlayers.addAll( tournament.getPairablePlayers( currentRoundIndex ) );
        
        unpairablePlayers.clear();
        unpairablePlayers.addAll( tournament.getUnpairablePlayers( currentRoundIndex ) );
        
        Player byePlayer = tournament.getByePlayer( currentRoundIndex );
        if ( byePlayer == null ){
            txfByePlayer.setText( "" );
        } else {
            txfByePlayer.setText( byePlayer.getId().getFullName() );
        }
        
        txfNbPairablePlayers.setText( Integer.toString( pairablePlayers.size() ));
        txfNbUnpairablePlayers.setText( Integer.toString( unpairablePlayers.size() ));
        txfNbGames.setText( Integer.toString( games.size() ));
    }
    
    /*
     * TODO
     * Awkward!
     */
    private ArrayList<Player> getSelectedPlayers() {
        int[] selectedIndices = tblPairablePlayers.getSelectedRows();
        
        // Return entire set if nothing is selected.
        if ( selectedIndices.length == 0 )
            return new ArrayList<Player> ( pairablePlayers );
        
        // Translate selected indices to table model
        for (int i = 0; i < selectedIndices.length; i++) {
            selectedIndices[i] = tblPairablePlayers.convertRowIndexToModel( selectedIndices[i] );
        }
        
        ArrayList<Player> selectedPlayers = new ArrayList<>();
        
        for (int i = 0; i < selectedIndices.length; i++) {
            selectedPlayers.add( pairablePlayers.get( selectedIndices[i] ) );
        }
        
        return selectedPlayers;
    }
    
    /*
     * TODO
     * Awkward!
     */
    private ArrayList<Player> getSelectedUnpairablePlayers() {
        int[] selectedIndices = tblUnpairablePlayers.getSelectedRows();
        
        // Return entire set if nothing is selected.
        if ( selectedIndices.length == 0 )
            return new ArrayList<Player> ( unpairablePlayers );
        
        // Translate selected indices to table model
        for (int i = 0; i < selectedIndices.length; i++) {
            selectedIndices[i] = tblUnpairablePlayers.convertRowIndexToModel( selectedIndices[i] );
        }
        
        ArrayList<Player> selectedPlayers = new ArrayList<>();
        
        for (int i = 0; i < selectedIndices.length; i++) {
            selectedPlayers.add( unpairablePlayers.get( selectedIndices[i] ) );
        }
        
        return selectedPlayers;
    }    

    /*
     * TODO
     * Awkward!
     */
    private ArrayList<Game> getSelectedGames() {
        int[] selectedIndices = tblGames.getSelectedRows();
        
        // Return entire set if nothing is selected.
        if ( selectedIndices.length == 0 )
            return new ArrayList<Game>( games );
        
        // Translate selected indices to table model
        for (int i = 0; i < selectedIndices.length; i++) {
            selectedIndices[i] = tblGames.convertRowIndexToModel( selectedIndices[i] );
        }
        
        ArrayList<Game> selectedGames = new ArrayList<>();
        
        for (int i = 0; i < selectedIndices.length; i++) {
            selectedGames.add( games.get( selectedIndices[i] ) );
        }
        
        return selectedGames;
    }

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        gameContextMenu = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        roundPanel = new javax.swing.JPanel();
        pnlPlayers = new javax.swing.JPanel();
        txfNbPairablePlayers = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        scpPairablePlayers = new javax.swing.JScrollPane();
        tblPairablePlayers = new javax.swing.JTable();
        txfNbUnpairablePlayers = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        scpUnairablePlayers = new javax.swing.JScrollPane();
        tblUnpairablePlayers = new javax.swing.JTable();
        btnPair = new javax.swing.JButton();
        btnUnpair = new javax.swing.JButton();
        btnByePlayer = new javax.swing.JButton();
        btnUnbyePlayer = new javax.swing.JButton();
        btnAddPlayer = new javax.swing.JButton();
        btnRemovePlayer = new javax.swing.JButton();
        pnlGames = new javax.swing.JPanel();
        txfNbGames = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        scpGames = new javax.swing.JScrollPane();
        tblGames = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        txfByePlayer = new javax.swing.JTextField();
        btnPrint = new javax.swing.JButton();

        jMenuItem1.setText("Change colors");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        gameContextMenu.add(jMenuItem1);

        jMenuItem2.setText("Change handicap");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        gameContextMenu.add(jMenuItem2);

        jMenuItem3.setText("Change komi");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        gameContextMenu.add(jMenuItem3);

        roundPanel.setLayout(new javax.swing.BoxLayout(roundPanel, javax.swing.BoxLayout.LINE_AXIS));

        pnlPlayers.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Players"));

        txfNbPairablePlayers.setEditable(false);
        txfNbPairablePlayers.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txfNbPairablePlayers.setText("1999");

        jLabel1.setText("pairable players");
        jLabel1.setToolTipText("");

        tblPairablePlayers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Rank"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPairablePlayers.getTableHeader().setReorderingAllowed(false);
        scpPairablePlayers.setViewportView(tblPairablePlayers);
        tblPairablePlayers.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        if (tblPairablePlayers.getColumnModel().getColumnCount() > 0) {
            tblPairablePlayers.getColumnModel().getColumn(0).setPreferredWidth(200);
            tblPairablePlayers.getColumnModel().getColumn(1).setPreferredWidth(15);
        }

        txfNbUnpairablePlayers.setEditable(false);
        txfNbUnpairablePlayers.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txfNbUnpairablePlayers.setText("1999");

        jLabel2.setText("not pairable players");

        tblUnpairablePlayers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Rank"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblUnpairablePlayers.getTableHeader().setReorderingAllowed(false);
        scpUnairablePlayers.setViewportView(tblUnpairablePlayers);
        tblUnpairablePlayers.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        if (tblUnpairablePlayers.getColumnModel().getColumnCount() > 0) {
            tblUnpairablePlayers.getColumnModel().getColumn(0).setPreferredWidth(200);
            tblUnpairablePlayers.getColumnModel().getColumn(1).setPreferredWidth(15);
        }

        javax.swing.GroupLayout pnlPlayersLayout = new javax.swing.GroupLayout(pnlPlayers);
        pnlPlayers.setLayout(pnlPlayersLayout);
        pnlPlayersLayout.setHorizontalGroup(
            pnlPlayersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPlayersLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPlayersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPlayersLayout.createSequentialGroup()
                        .addComponent(txfNbPairablePlayers, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlPlayersLayout.createSequentialGroup()
                        .addGroup(pnlPlayersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlPlayersLayout.createSequentialGroup()
                                .addComponent(txfNbUnpairablePlayers, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(scpUnairablePlayers, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(scpPairablePlayers, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        pnlPlayersLayout.setVerticalGroup(
            pnlPlayersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPlayersLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(pnlPlayersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txfNbPairablePlayers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scpPairablePlayers, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPlayersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txfNbUnpairablePlayers, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scpUnairablePlayers, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        btnPair.setText("Pair >>");
        btnPair.setMargin(new java.awt.Insets(2, 0, 2, 0));
        btnPair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPairActionPerformed(evt);
            }
        });

        btnUnpair.setText("<< Unpair");
        btnUnpair.setMargin(new java.awt.Insets(2, 0, 2, 0));
        btnUnpair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnpairActionPerformed(evt);
            }
        });

        btnByePlayer.setText("Bye >>");
        btnByePlayer.setMargin(new java.awt.Insets(2, 0, 2, 0));
        btnByePlayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnByePlayerActionPerformed(evt);
            }
        });

        btnUnbyePlayer.setText("<< Unbye");
        btnUnbyePlayer.setMargin(new java.awt.Insets(2, 0, 2, 0));
        btnUnbyePlayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnbyePlayerActionPerformed(evt);
            }
        });

        btnAddPlayer.setText("Add");
        btnAddPlayer.setMargin(new java.awt.Insets(2, 0, 2, 0));
        btnAddPlayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddPlayerActionPerformed(evt);
            }
        });

        btnRemovePlayer.setText("Remove");
        btnRemovePlayer.setMargin(new java.awt.Insets(2, 0, 2, 0));
        btnRemovePlayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemovePlayerActionPerformed(evt);
            }
        });

        pnlGames.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Games"));

        txfNbGames.setEditable(false);
        txfNbGames.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txfNbGames.setText("1000");

        jLabel3.setText("tables");

        tblGames.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Table", "White", "Black", "Handicap", "Komi"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblGames.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        tblGames.getTableHeader().setReorderingAllowed(false);
        scpGames.setViewportView(tblGames);
        tblGames.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        if (tblGames.getColumnModel().getColumnCount() > 0) {
            tblGames.getColumnModel().getColumn(0).setPreferredWidth(10);
            tblGames.getColumnModel().getColumn(1).setPreferredWidth(200);
            tblGames.getColumnModel().getColumn(2).setPreferredWidth(200);
            tblGames.getColumnModel().getColumn(3).setPreferredWidth(15);
            tblGames.getColumnModel().getColumn(4).setPreferredWidth(15);
        }

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Bye player");

        txfByePlayer.setEditable(false);

        btnPrint.setText("Print...");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlGamesLayout = new javax.swing.GroupLayout(pnlGames);
        pnlGames.setLayout(pnlGamesLayout);
        pnlGamesLayout.setHorizontalGroup(
            pnlGamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGamesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlGamesLayout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txfByePlayer, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlGamesLayout.createSequentialGroup()
                        .addGroup(pnlGamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlGamesLayout.createSequentialGroup()
                                .addComponent(txfNbGames, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(scpGames, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                            .addGroup(pnlGamesLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        pnlGamesLayout.setVerticalGroup(
            pnlGamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGamesLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(pnlGamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txfNbGames, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(scpGames, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGamesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txfByePlayer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(19, 19, 19)
                .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlPlayers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnPair, javax.swing.GroupLayout.DEFAULT_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnUnbyePlayer, javax.swing.GroupLayout.DEFAULT_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnByePlayer, javax.swing.GroupLayout.DEFAULT_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnUnpair, javax.swing.GroupLayout.DEFAULT_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAddPlayer, javax.swing.GroupLayout.DEFAULT_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnRemovePlayer, javax.swing.GroupLayout.DEFAULT_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(roundPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(208, 208, 208)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlGames, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(6, 6, 6))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(roundPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(103, 103, 103)
                                .addComponent(btnPair, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnUnpair, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(btnByePlayer, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnUnbyePlayer, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(55, 55, 55)
                                .addComponent(btnAddPlayer, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRemovePlayer, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 46, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnlPlayers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addComponent(pnlGames, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        
        // Create a copy of the table and size the columns for printing:
        CustomPrintableTable printTable = new CustomPrintableTable( tblGames.getModel() );
        JScrollPane scroll = new JScrollPane( printTable );
        
        // Inherit sort
        TableRowSorter printSorter = new TableRowSorter( printTable.getModel() );
        printTable.setRowSorter( printSorter );
        printSorter.setSortKeys( tblGames.getRowSorter().getSortKeys() );
        //
        
        
        printTable.getColumnModel().getColumn(0).setPreferredWidth( 22 );
        printTable.getColumnModel().getColumn(1).setPreferredWidth( 200 );
        printTable.getColumnModel().getColumn(2).setPreferredWidth( 200 );
        printTable.getColumnModel().getColumn(3).setPreferredWidth( 22 );
        printTable.getColumnModel().getColumn(4).setPreferredWidth( 22 );
        
        scroll.setVisible( false );
        this.add(scroll);
        scroll.validate();  // Not sure why this is required here as scroll is validated by the custom printable...
        
        MessageFormat header = new  MessageFormat( tournament.getPrintHeadingString("Round " + roundPane.getRoundNumberString() ) );
        try {
            printTable.print( JTable.PrintMode.FIT_WIDTH, header, null );
            
        } catch ( PrinterException ex ) {
            Logger.getLogger( PairingsManager.class.getName() ).log( Level.SEVERE, "Unable to print.\n" + ex );
            JOptionPane.showMessageDialog(Eggplant.getInstance().getMainWindow(), "Unable to print.", "Error", JOptionPane.ERROR_MESSAGE );
        }
        
        this.remove( scroll );
        this.repaint();
        
    }//GEN-LAST:event_btnPrintActionPerformed

    private void btnPairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPairActionPerformed
        int roundIndex = roundPane.getRoundIndex();
        
        ArrayList<Player> playersToPair = getSelectedPlayers();
        
        if (playersToPair.size() % 2 != 0) {
            // if no possibility to choose a bye player, Error message
            Player byePlayer = null;
            byePlayer = tournament.getByePlayer( roundIndex );
            
            if ( byePlayer != null) {
                JOptionPane.showMessageDialog(Eggplant.getInstance().getMainWindow(), "Please select an even number of players.", "Message", JOptionPane.ERROR_MESSAGE);
                return;
            } else {
                int response = JOptionPane.showConfirmDialog(this, "An odd number of players are selected. Allow Eggplant to select a bye player automatically?", "Query", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
                if (response == JOptionPane.YES_OPTION) {
                    tournament.chooseByePlayer( playersToPair, roundIndex );
                    byePlayer = tournament.getByePlayer(roundIndex);
                    Player pToRemove = null;
                    playersToPair.remove( byePlayer );
                } else {
                    return;
                }
            }
        }

        ArrayList<Game> alNewGames;
        alNewGames = tournament.pairRound( playersToPair, roundIndex );

        
        
        updateControls();
        
        
        
        // Check if there is a previously paired couple of players
        ArrayList<Game> alOldGames;
        alOldGames = tournament.getGamesBefore( roundIndex );
        
        StringBuilder alertBuilder = new StringBuilder("");
        
        for (Game newG : alNewGames) {
            Player newWP = newG.getWhitePlayer();
            Player newBP = newG.getBlackPlayer();
            for (Game oldG : alOldGames) {
                if ( oldG.playersInclude( newWP ) && (oldG.playersInclude( newBP ) ) ) {
                    alertBuilder.append("Table ").
                        append(newG.getTableNumberString()).
                        append(": Players have already been paired in round ").
                        append(Integer.toString(oldG.getRoundIndex() +1 )).
                        append(".\n");
                    break;
                }
            }
            
            if ( newG.getUnboundHandicap() > tournament.getPairingProps().getMaxHandicap() ) {
                alertBuilder.append("Table ").
                    append(newG.getTableNumberString()).
                    append(": Correct handicap exceeds maximum handicap for this tournament.\n");
            }
        }
        
        String alertMessage = alertBuilder.toString();
        
        if ( !alertMessage.equals("") ) {
            JOptionPane.showMessageDialog(Eggplant.getInstance().getMainWindow(), alertMessage, "Alert", JOptionPane.WARNING_MESSAGE );
        }

    }//GEN-LAST:event_btnPairActionPerformed

    private void btnUnpairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnpairActionPerformed
        ArrayList<Game> alGamesToRemove = getSelectedGames();

        int nbGamesToRemove = alGamesToRemove.size();
        if (nbGamesToRemove > 1) {
            int response = JOptionPane.showConfirmDialog(Eggplant.getInstance().getMainWindow(),
                    "Unpair " + nbGamesToRemove + " games?",
                    "Query",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE );
            if (response != JOptionPane.YES_OPTION ) {
                return;
            }

        }
        // And now, remove games from tournament
        tournament.unpairRound(alGamesToRemove, roundPane.getRoundIndex());
        
        tournament.setScoringValidity( false );
        
        updateControls();
    }//GEN-LAST:event_btnUnpairActionPerformed

    private void btnByePlayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnByePlayerActionPerformed
        ArrayList<Player> alP = getSelectedPlayers();
        tournament.chooseByePlayer( alP, roundPane.getRoundIndex() );
        
        updateControls();
    }//GEN-LAST:event_btnByePlayerActionPerformed

    private void btnAddPlayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddPlayerActionPerformed
        ArrayList<Player> selectedPlayers = getSelectedUnpairablePlayers();
        if (selectedPlayers.isEmpty()) return;
        
        for ( Player p : selectedPlayers ) {
            p.setParticipation( roundPane.getRoundIndex(), tournament.getPairingProps().getNumberOfRounds() - 1, Participation.NOT_ASSIGNED );
        }
        updateControls();
        tournament.setChangedSinceLastSave(true);
        
    }//GEN-LAST:event_btnAddPlayerActionPerformed

    private void btnRemovePlayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemovePlayerActionPerformed
        ArrayList<Player> selectedPlayers = getSelectedPlayers();
        if (selectedPlayers.isEmpty()) return;
        
        for ( Player p : selectedPlayers ) {
            p.setParticipation( roundPane.getRoundIndex(), tournament.getPairingProps().getNumberOfRounds() - 1, Participation.ABSENT );
        }
        updateControls();
        tournament.setChangedSinceLastSave(true);
    }//GEN-LAST:event_btnRemovePlayerActionPerformed

    private void btnUnbyePlayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnbyePlayerActionPerformed
        tournament.unassignByePlayer( roundPane.getRoundIndex() );
        updateControls();
    }//GEN-LAST:event_btnUnbyePlayerActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // Change colors

        // Get selected game
        ArrayList<Game> selectedGames = getSelectedGames();
        if ( selectedGames.size()!= 1 ) { return; }
        
        Game selectedGame = selectedGames.get(0);
        // change colors
        Player white = selectedGame.getWhitePlayer();
        Player black = selectedGame.getBlackPlayer();
        selectedGame.setWhitePlayer( black );
        selectedGame.setBlackPlayer( white );
        
        // refresh view
        // overkill refresh!
        updateControls();
        tournament.setChangedSinceLastSave(true);

        
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // Change handicap
        
        // Get selected game
        ArrayList<Game> selectedGames = getSelectedGames();
        if ( selectedGames.size()!= 1 ) { return; }
        
        Game selectedGame = selectedGames.get(0);
        // prompt for new handicap
        
        String strOld = selectedGame.getHandicapString();
        String strResponse = JOptionPane.showInputDialog("Enter new handicap:", strOld );
        
        int newHandicap;
        try {
            newHandicap = Integer.parseInt( strResponse );
        } catch ( NumberFormatException ex ) {
            newHandicap = selectedGame.getHandicap();
        }
        if ( newHandicap > tournament.getPairingProps().getMaxHandicap() ) {
            // Message dialog here?
            newHandicap = tournament.getPairingProps().getMaxHandicap();
        }
        
        selectedGame.setHandicap( newHandicap );
        
        // refresh view
        // overkill refresh!
        updateControls();
        tournament.setChangedSinceLastSave(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        // Change komi
        
        // Get selected game
        ArrayList<Game> selectedGames = getSelectedGames();
        if ( selectedGames.size()!= 1 ) { return; }
        
        Game selectedGame = selectedGames.get(0);
        // prompt for new handicap
        
        String strOld = selectedGame.getKomiString();
        String strResponse = JOptionPane.showInputDialog("Enter new komi:", strOld );
        selectedGame.setKomi( strResponse );
        
        // refresh view
        // overkill refresh!
        updateControls();
        tournament.setChangedSinceLastSave(true);
        
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddPlayer;
    private javax.swing.JButton btnByePlayer;
    private javax.swing.JButton btnPair;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnRemovePlayer;
    private javax.swing.JButton btnUnbyePlayer;
    private javax.swing.JButton btnUnpair;
    private javax.swing.JPopupMenu gameContextMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel pnlGames;
    private javax.swing.JPanel pnlPlayers;
    private javax.swing.JPanel roundPanel;
    private javax.swing.JScrollPane scpGames;
    private javax.swing.JScrollPane scpPairablePlayers;
    private javax.swing.JScrollPane scpUnairablePlayers;
    private javax.swing.JTable tblGames;
    private javax.swing.JTable tblPairablePlayers;
    private javax.swing.JTable tblUnpairablePlayers;
    private javax.swing.JTextField txfByePlayer;
    private javax.swing.JTextField txfNbGames;
    private javax.swing.JTextField txfNbPairablePlayers;
    private javax.swing.JTextField txfNbUnpairablePlayers;
    // End of variables declaration//GEN-END:variables
}
