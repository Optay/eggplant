/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.seattlego.eggplant.forms;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import org.seattlego.eggplant.interfaces.ITournament;
import org.seattlego.eggplant.model.*;
import org.seattlego.eggplant.model.comparators.PlayerComparator;

/**
 *
 * @author Topsy
 */
public class Standings extends EggplantForm {

    private ArrayList<JComboBox> criteria;
    private ArrayList<PlacementCriterion> displayedCriteria;
    private TableRowSorter sorter;
    
    /**
     * Creates new form Standings
     */
    public Standings() {
        initComponents();
        init();
    }
    
    private void init() {
        initRoundPane();
        jPanel2.add( roundPane );

        displayedCriteria = new ArrayList<>();
        
        // Default set
        displayedCriteria.add( PlacementCriterion.MMS );
        displayedCriteria.add( PlacementCriterion.SOSM );
        displayedCriteria.add( PlacementCriterion.SOSOSM );
        //displayedCriteria.add( PlacementCriterion.CUSSM );
        displayedCriteria.add( PlacementCriterion.NBW );
        //displayedCriteria.add( PlacementCriterion.SOSW );
        //displayedCriteria.add( PlacementCriterion.SOSOSW );
        //displayedCriteria.add( PlacementCriterion.CUSSW );
        //
        
        
        // Configure table
        // This table's columns will be added manually here and then bound to the observable player list.
        standingsTable.setAutoCreateColumnsFromModel( false );
        
        setTableColumns();
        
        // Configure criterion combos
        criteria = new ArrayList<>();
        for( int i = 0; i < PlacementProperties.MAX_CRITERIA; i++ ) {
            JComboBox<PlacementCriterion> combo = new JComboBox<>( PlacementCriterion.values() );
            combo.setMaximumSize(new Dimension(200, 25));
            /*
            combo.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    criterionComboActionPerformed(evt);
                }
            });  
            * 
            */
            
            combo.addItemListener( new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    criterionComboItemChanged( e );
                }
            });            

            
            jPanel1.add( combo );
            criteria.add( combo );
        }
        //        
        
        // Configure columns checkboxes
        //checkBoxes = new ArrayList<>();
        for( PlacementCriterion pc : PlacementCriterion.values() ) {
            JCheckBox check = new JCheckBox( pc.longName );
            check.setMaximumSize(new Dimension(200, 25));
            check.setActionCommand( pc.name() );
            check.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    columnCheckActionPerformed(evt);
                }
            });            
            check.setSelected( displayedCriteria.contains( pc ) );
            check.setToolTipText( pc.description );
            jPanel3.add( check );
        }
        
        
        
        
        
        /*
        games = ObservableCollections.observableList( new ArrayList<Game>() );
        
        bindings = new BindingGroup();
        
        JTableBinding gamesBinding = SwingBindings.createJTableBinding( AutoBinding.UpdateStrategy.READ, games, tblGames, "gamesBinding");
        bindings.addBinding( gamesBinding );
        bindings.addBinding( Bindings.createAutoBinding( AutoBinding.UpdateStrategy.READ_WRITE,
                                    this,
                                    BeanProperty.create("selectedGame"), 
                                    tblGames,
                                    BeanProperty.create("selectedElement_IGNORE_ADJUSTING") ) );
        
        JTableBinding.ColumnBinding columnBinding;
        columnBinding = gamesBinding.addColumnBinding(ELProperty.create("${tableNumberString}"));
        columnBinding.setColumnName("Table");
        columnBinding = gamesBinding.addColumnBinding(ELProperty.create("${whiteFlagged}"));
        columnBinding.setColumnName("White");
        columnBinding = gamesBinding.addColumnBinding(ELProperty.create("${blackFlagged}"));
        columnBinding.setColumnName("Black");
        columnBinding = gamesBinding.addColumnBinding(ELProperty.create("${resultString}"));
        columnBinding.setColumnName("Result");
        bindings.bind();
        
        TableRowSorter trsGames = new TableRowSorter( tblGames.getModel() );
        tblGames.setRowSorter( trsGames );
        * 
        */
        //
    }
    
    @Override
    protected void selectedRoundChanged() {
        updateControls();
    }
    
    @Override
    public void setTournament( ITournament t ) {
        super.setTournament( t );
        
        updateControls();
    }
    
    private void resetCriteria() {
        PlacementCriterion[] currentSet = tournament.getPlacementProps().getPlaCriteria();
        
        // This causes ActionPerformed to fire which causes a redundant set
        // call.
        for( int i = 0; i < currentSet.length; i++ ) {
            criteria.get(i).setSelectedItem( currentSet[i] );
        }
    }
    
    private void criterionComboItemChanged( ItemEvent evt ) {
        if ( evt.getStateChange() != ItemEvent.SELECTED ) {
            return;
        }
        
        // Update the tournament if necessary
        JComboBox combo = (JComboBox) evt.getSource();
        int index = criteria.indexOf( combo );
        
        PlacementCriterion tournamentValue = tournament.getPlacementProps().getPlaCriteria()[ index ];
        
        if ( tournamentValue == (PlacementCriterion) combo.getSelectedItem() ) {
            return;
        }
        
        tournament.getPlacementProps().setPlaCriterion( index, (PlacementCriterion) combo.getSelectedItem() );
        
        // A bit overkill, but it's what we've got.
        populateTable();
        
        tournament.setChangedSinceLastSave(true);
        
        
    }
    
    /*
     * Action event listener for updating crit when combos change. Abandoned in favor of
     * itemlistener which only fires when selected value is changed.
    private void criterionComboActionPerformed( ActionEvent evt ) {
        
        
        
        // Update placement settings
        JComboBox combo = (JComboBox) evt.getSource();
        int index = criteria.indexOf( combo );
        tournament.getPlacementProps().setPlaCriterion( index, (PlacementCriterion) combo.getSelectedItem() );
        //
        
        
        // A bit overkill, but it's what we've got.
        populateTable();
        
        tournament.setChangedSinceLastSave(true);
    }
    * 
    */
    
    private void columnCheckActionPerformed( ActionEvent evt ) {
        JCheckBox check = (JCheckBox) evt.getSource();
        PlacementCriterion criterion = PlacementCriterion.valueOf( evt.getActionCommand() );
        if ( check.isSelected() ) {
            if ( !displayedCriteria.contains( criterion ) ) { displayedCriteria.add( criterion ); }
        } else {
            displayedCriteria.remove( criterion );
        }
        
        setTableColumns();
        populateTable();
    }
    
    private void setTableColumns() {
        standingsTable.setColumnModel( new DefaultTableColumnModel() );
        
        TableColumn column;
        
        // Player Number
        column = new TableColumn( 0, 25 );
        column.setHeaderValue("");
        standingsTable.addColumn( column );
        
        column = new TableColumn( 1, 200 );
        column.setHeaderValue("Player");
        standingsTable.addColumn( column );

        // Results
        int columnIndex = 1;
        for ( int round = 0; round <= roundPane.getRoundIndex(); round++ ) {
            columnIndex++;
            column = new TableColumn( columnIndex );
            column.setHeaderValue("R" + Integer.toString( round + 1 ) );
            standingsTable.addColumn( column );
        }
        
        // Place
        columnIndex++;
        column = new TableColumn( columnIndex, 25 );
        column.setHeaderValue("Place");
        standingsTable.addColumn( column );
        //
        
        // Scores/Criteria/Tiebreaks
        // Sort criteria, so the columns appear in a consistent order.
        Collections.sort( displayedCriteria, new EnumOrdinalComparator() );
        
        for ( PlacementCriterion pc : displayedCriteria ) {
            columnIndex ++;
            column = new TableColumn( columnIndex );
            column.setHeaderValue( pc.toString() );
            standingsTable.addColumn( column );
        }
        
        // Update table model to correct number of columns.
        DefaultTableModel tm = (DefaultTableModel) standingsTable.getModel();
        tm.setColumnCount( 3 + (roundPane.getRoundIndex() + 1) + displayedCriteria.size() );
        //
        
    }
    
    private PlacementCriterion[] getSelectedCriteria() {
        PlacementCriterion[] selectedCriteria = new PlacementCriterion[ criteria.size() ];
        
        for ( int i=0; i<criteria.size(); i++ ) {
            selectedCriteria[i] = (PlacementCriterion) criteria.get(i).getSelectedItem();
        }
        
        return selectedCriteria;
    }
    
    private void populateTable() {
        int roundIndex = roundPane.getRoundIndex();
        
        DefaultTableModel tm = (DefaultTableModel) standingsTable.getModel();
        
        // TODO - Really? This? Perhaps I should bind to some new collection?
        while ( tm.getRowCount() > 0 ) {
            tm.removeRow(0);
        }
        

        PlayerComparator tieDetector = new PlayerComparator( getSelectedCriteria(), roundIndex, true );
        ArrayList<Player> players = tournament.getRegisteredPlayers();
        Collections.sort( players, tieDetector );
        
        int position = 0;
        Boolean tied = false;
        Player previous = null;
        
        for ( Player p : players ) {
            String[] rowData = new String[ 3 + (roundPane.getRoundIndex() + 1) + displayedCriteria.size() ];

            rowData[0] = Integer.toString( players.indexOf( p ) + 1 );

            rowData[1] = p.getId().getFullName();
            
            int columnIndex = 1;
            // Results
            for ( int round = 0; round <= roundPane.getRoundIndex(); round++ ) {
                columnIndex++;
                rowData[ columnIndex ] = gameResultString( players, p, round );
            }
            //
            
            // Place
            // If player is not tied with previous player, increment and write position.
            columnIndex++;
            if ( previous != null ) {
                tied = ( tieDetector.compare( p, previous ) == 0 );
            }
            if ( !tied ) {
                position++;
                rowData[columnIndex] = Integer.toString( position );
            }
            previous = p;
            //
            
            
            // Scores/Criteria/Tiebreaks
            for ( PlacementCriterion pc : displayedCriteria ) {
                columnIndex++;
                
                switch ( pc ) {
                    case RANK:
                        rowData[ columnIndex ] = p.getRank().toString();
                        break;
                    case RATING:
                        rowData[ columnIndex ] = p.getId().getRating().toString();
                        break;
                    default:
                        rowData[ columnIndex ] = p.getScore().getMetricString( roundIndex, pc );
                }
                
                
            }
            tm.addRow( rowData );
            
        }
        
        
    }
    
    
    @Override
    protected void updateControls() {
        
        // Pull the data for the current round.
        // int currentRoundIndex = roundPane.getRoundIndex();
        
        tournament.updateScoring();
        
        resetCriteria();
        
        setTableColumns();
        populateTable();
        
    }
    
    /**
     * Generate string with a "oooortch" format
     * oooo being opponent number,
     * r being the result "+", "-", "=" or "?"
     * t (type) is either "/" for normal results or "!" for by default results
     * c being the colour, "w", "b" or "?"
     * h being handicap, "0" ... "9"
     * @param tps Tournament parameter set. useful for placement criteria and for absent and values scores
     */
    private String gameResultString( ArrayList<Player> players, Player p, int roundIndex ) {
        
        Game game = tournament.getGame( roundIndex, p );
                
        String strOpponent;
        String strResult;
        String strType;
        String strColor;
        String strHandicap;

        if (game == null){
            strOpponent = "   0";
            strColor = " ";
            strType = " ";
            strHandicap  = " ";
            
            int resultValue;
            switch ( p.getParticipation(roundIndex) ) {
                case ABSENT:
                    resultValue = tournament.getPlacementProps().getGenMms2ValueAbsent();
                    break;
                case BYE:
                    resultValue = tournament.getPlacementProps().getGenMms2ValueBye();
                    break;
                case NOT_ASSIGNED:
                default:
                    resultValue = 0;
            }
            switch( resultValue ) {
                case 2:
                    strResult = "+"; break;
                case 1:
                    strResult = "="; break;
                default:
                    strResult = "-";
            }
        } else {    // Game exists
            Player opp = null;
            GameResult result = game.getResult();
            if ( result == GameResult.UNKNOWN) strType = "/";
            else {
                if ( result.getType() == GameResultType.BY_DEFAULT ) strType = "!";
                else strType = "/";
            }
            strResult = game.getResultFor(p).getAbbreviation();
            if (!game.isKnownColor()) strColor = "?";
            else strColor = (game.getWhitePlayer() == p ) ? "w" : "b";

            int oppNum = players.indexOf( game.getOpponent( p ) );
            strOpponent = "    " + (oppNum + 1);
            strOpponent = strOpponent.substring(strOpponent.length() - 4);  // To have 4 chars exactly
            strHandicap = "" + game.getHandicap();
        }
        return strOpponent + strResult + strType + strColor + strHandicap;
    }    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        resetCriteriaButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        scpGames = new javax.swing.JScrollPane();
        standingsTable = new javax.swing.JTable();

        jPanel2.setLayout(new java.awt.GridLayout(1, 0));

        resetCriteriaButton.setText("Reset");
        resetCriteriaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetCriteriaButtonActionPerformed(evt);
            }
        });

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(resetCriteriaButton)
                        .addGap(0, 58, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(resetCriteriaButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Criteria", jPanel4);

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane1.setViewportView(jPanel3);

        jTabbedPane1.addTab("Columns", jScrollPane1);

        standingsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        standingsTable.setRowHeight(20);
        standingsTable.setRowSelectionAllowed(false);
        standingsTable.getTableHeader().setReorderingAllowed(false);
        scpGames.setViewportView(standingsTable);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scpGames, javax.swing.GroupLayout.DEFAULT_SIZE, 643, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scpGames, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void resetCriteriaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetCriteriaButtonActionPerformed
        resetCriteria();
        // TODO update table row sorter.
    }//GEN-LAST:event_resetCriteriaButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton resetCriteriaButton;
    private javax.swing.JScrollPane scpGames;
    private javax.swing.JTable standingsTable;
    // End of variables declaration//GEN-END:variables
}

class EnumOrdinalComparator implements Comparator<PlacementCriterion> {

    @Override
    public int compare(PlacementCriterion pc1, PlacementCriterion pc2){
        if ( pc1.ordinal() > pc2.ordinal() )  { return 1; }
        else if ( pc1.ordinal() < pc2.ordinal() ) { return -1; }
        else return 0;
    }
}


    /**
     * Generate strings with a "oooortch" format
     * oooo being opponent number,
     * r being the result "+", "-", "=" or "?"
     * t (type) is either "/" for normal results or "!" for by default results
     * c being the colour, "w", "b" or "?"
     * h being handicap, "0" ... "9"
     * @param tps Tournament parameter set. useful for placement criteria and for absent and values scores
     */
    /* TODO - Fix this when it is needed.
    public static String[][] halfGamesStrings(ArrayList<Player> alOrderedScoredPlayers, int roundNumber, TournamentParameterSet tps) {
        GeneralParameterSet gps = tps.getGeneralParameterSet();
        // Prepare hmPos for fast retrieving
        HashMap<String, Integer> hmPos = new HashMap<String, Integer>();
        for (int i = 0; i < alOrderedScoredPlayers.size(); i++){
            hmPos.put(alOrderedScoredPlayers.get(i).getKeyString(), i);
        }
        String[][] hG = new String[roundNumber +1][alOrderedScoredPlayers.size()];
        for (int i = 0; i < alOrderedScoredPlayers.size(); i++){
            Player p = alOrderedScoredPlayers.get(i);
            for(int r = 0; r <= roundNumber; r++){
                String strOpp = "   0";
                String strRes = " ";
                String strTyp = "/";
                String strCol = " ";
                String strHd  = "0";

                Game g = p.playerScoring.gameArray[r];
                if (g == null){
                    strOpp = "   0";
                    strCol = " ";
                    strTyp = " ";
                    strHd  = " ";
                    if (p.playerScoring.participation[r] == PlayerScoring.NOT_ASSIGNED) strRes = "-";
                    else{
                        int res = 0;
                        if (p.playerScoring.participation[r] == PlayerScoring.ABSENT)
                            if (tps.tournamentType() == TournamentParameterSet.TYPE_MACMAHON) res = gps.getGenMMS2ValueAbsent();
                            else res = gps.getGenNBW2ValueAbsent();
                        else if (p.playerScoring.participation[r] == PlayerScoring.BYE)
                            if (tps.tournamentType() == TournamentParameterSet.TYPE_MACMAHON) res = gps.getGenMMS2ValueBye();
                            else res = gps.getGenNBW2ValueBye();
                        if (res == 2) strRes = "+";
                        else if (res == 1) strRes = "=";
                        else strRes = "-";
                    }
                }
                else{   //Real Game
                   Player opp = null;
                   int result = g.getResult();
                   if (result == Game.RESULT_UNKNOWN) strTyp = "/";
                   else strTyp = (result >= Game.RESULT_BYDEF) ? "!" : "/";
                   int res = result;
                   if (result >= Game.RESULT_BYDEF) res = result - Game.RESULT_BYDEF;
                   if (g.getWhitePlayer().hasSameKeyString(p)){
                       opp = g.getBlackPlayer();
                       strCol = "w";
                       if (res == Game.RESULT_WHITEWINS || res == Game.RESULT_BOTHWIN) strRes = "+";
                       else if (res == Game.RESULT_BLACKWINS || res == Game.RESULT_BOTHLOSE) strRes = "-";
                       else if (res == Game.RESULT_EQUAL) strRes = "=";
                       else strRes = "?";
                   }
                   else{
                       opp = g.getWhitePlayer();
                       strCol = "b";
                       if (res == Game.RESULT_BLACKWINS || res == Game.RESULT_BOTHWIN) strRes = "+";
                       else if (res == Game.RESULT_WHITEWINS || res == Game.RESULT_BOTHLOSE) strRes = "-";
                       else if (res == Game.RESULT_EQUAL) strRes = "=";
                       else strRes = "?" ;

                   }
                   if (!g.isKnownColor()) strCol = "?";

                   int oppNum = hmPos.get(opp.getKeyString());
                   strOpp = "    " + (oppNum +1);
                   strOpp = strOpp.substring(strOpp.length() - 4);  // To have 4 chars exactly
                   strHd = "" + g.getHandicap();
                }
                hG[r][i] = strOpp + strRes + strTyp + strCol + strHd;

            }
        }
        return hG;
    }*/