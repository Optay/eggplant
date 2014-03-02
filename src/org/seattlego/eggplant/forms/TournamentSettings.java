package org.seattlego.eggplant.forms;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.seattlego.eggplant.io.Template;
import org.seattlego.eggplant.io.TemplateManager;
import org.seattlego.eggplant.model.*;

/**
 *
 * @author Topsy
 */
public class TournamentSettings extends EggplantForm {

    /**
     * Creates new form TournamentSettings
     */
    public TournamentSettings() {
        initComponents();
        
        init();
    }
    
    private void init() {
        jRadioButton2.setActionCommand( Ruleset.JAPANESE.name() );
        jRadioButton3.setActionCommand( Ruleset.AGA.name() );
        
        
        jRadioButton5.setActionCommand( TimeSystem.CANADIAN.name() );
        jRadioButton6.setActionCommand( TimeSystem.BYOYOMI.name() );
        jRadioButton7.setActionCommand( TimeSystem.FISCHER.name() );
        jRadioButton4.setActionCommand( TimeSystem.SUDDEN_DEATH.name() );
        
        
        handicapModifier0.setActionCommand( "0" );
        handicapModifier1.setActionCommand( "1" );
        handicapModifier2.setActionCommand( "2" );
        handicapModifier3.setActionCommand( "3" );
        
        CardLayout cl = (CardLayout) jPanel2.getLayout();
        cl.show( jPanel2, TimeSystem.CANADIAN.name() );
        
        handicapBasis0.setActionCommand( HandicapBasis.MMS.name() );
        handicapBasis1.setActionCommand( HandicapBasis.RANK.name() );
        
        rdbFormerSplitAndFold.setActionCommand( PairingMethod.SPLIT_AND_FOLD.name() );
        rdbFormerSplitAndRandom.setActionCommand( PairingMethod.SPLIT_AND_RANDOM.name() );
        rdbFormerSplitAndSlip.setActionCommand( PairingMethod.SPLIT_AND_SLIP.name() );
        
        rdbLatterSplitAndFold.setActionCommand( PairingMethod.SPLIT_AND_FOLD.name() );
        rdbLatterSplitAndRandom.setActionCommand( PairingMethod.SPLIT_AND_RANDOM.name() );
        rdbLatterSplitAndSlip.setActionCommand( PairingMethod.SPLIT_AND_SLIP.name() );
        
        
        rdbAbsentNbw0.setActionCommand("0");
        rdbAbsentNbw1.setActionCommand("1");
        rdbAbsentNbw2.setActionCommand("2");
        rdbAbsentMms0.setActionCommand("0");
        rdbAbsentMms1.setActionCommand("1");
        rdbAbsentMms2.setActionCommand("2");
        rdbByeNbw0.setActionCommand("0");
        rdbByeNbw1.setActionCommand("1");
        rdbByeNbw2.setActionCommand("2");
        rdbByeMms0.setActionCommand("0");
        rdbByeMms1.setActionCommand("1");
        rdbByeMms2.setActionCommand("2");
        
        
        // Two lists, one model?
        DefaultListModel templateModel = new DefaultListModel();
        templateList1.setModel( templateModel );
        templateList2.setModel( templateModel );

        // Set up selection listener for table.
        templateList1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                template1SelectionChanged();
            }
        });        
        templateList2.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                template2SelectionChanged();
            }
        });        
        //
    }
    
    private void template1SelectionChanged() {
        String selectedTemplateName = (String) templateList1.getSelectedValue();
        if ( selectedTemplateName == null ) {
            templateDescription.setText( "" );
        } else {
            templateDescription.setText( TemplateManager.getTemplate( selectedTemplateName ).getDescription() );
        }
    }
    
    private void template2SelectionChanged() {
        String selectedTemplateName = (String) templateList2.getSelectedValue();
        
        if ( selectedTemplateName == null ) {
            newTemplateDescription.setText( "" );
            newTemplateName.setText( "" );
            return;
        }
        
        Template selectedTemplate = TemplateManager.getTemplate( selectedTemplateName );
        newTemplateDescription.setText( selectedTemplate.getDescription() );
        newTemplateName.setText( selectedTemplate.getName() );
        
    }
    
    private void refreshTemplateList() {
        DefaultListModel templateModel = (DefaultListModel) templateList1.getModel();
        
        templateModel.clear();
        
        ArrayList<Template> templates = TemplateManager.getTemplates();
        for ( Template t : templates ) {
            templateModel.addElement( t.getName() );
        }
    }
    
    /*
     * Set control values based on tournament object.
     */
    @Override
    protected void updateControls() {
        // Info
        tfName.setText( tournament.getProps().getName() );
        tfLocation.setText( tournament.getProps().getLocation() );
        taComment.setText( tournament.getProps().getComment() );
        tfStartDate.setText( tournament.getProps().getStartDateString() );
        tfEndDate.setText( tournament.getProps().getEndDateString() );
        
        setRadioSelection( rulesetButtonGroup, tournament.getProps().getRuleset().name() );
        //Logger.getLogger( TournamentSettings.class.getName() ).log( Level.INFO, tournament.getProps().getRuleset().name() );
        
        
        tfBasicTime.setText( Integer.toString( tournament.getProps().getBasicTime() ) );
        setRadioSelection( overtimeButtonGroup, tournament.getProps().getTimeSystem().name() );
        
        tfByoYomiPeriods.setText( tournament.getProps().getTimeSystemProperties().get( "TimeBlockCount" ) );
        tfByoYomiTime.setText( tournament.getProps().getTimeSystemProperties().get( "TimeBlockSize" ) );
        
        tfCanadianStones.setText( tournament.getProps().getTimeSystemProperties().get( "StoneCount" ) );
        tfCanadianTime.setText( tournament.getProps().getTimeSystemProperties().get( "TimeBlockSize" ) );
        
        tfFischerTimeIncrement.setText( tournament.getProps().getTimeSystemProperties().get( "TimeIncrement" ) );
        
        // Click event for the group to show the right card.
        // Must be called after setting values in all time detail fields.
        overtimeRadioActionPerformed( null );
        //
        
        // Pairing
        tfNumberOfRounds.setText( Integer.toString( tournament.getPairingProps().getNumberOfRounds() ) );
        // pairing format - Only one, don't need to do anything right now.
        tfMMBar.setText( tournament.getPairingProps().getMMBar().toString() );
        tfMMFloor.setText( tournament.getPairingProps().getMMFloor().toString() );
        txfLastRoundForSeedSystem1.setText( Integer.toString( tournament.getPairingProps().getLastRoundForSeedSystem1() ) );
        setRadioSelection( formerPairingScheme, tournament.getPairingProps().getPairingScheme1().name() );
        setRadioSelection( latterPairingScheme, tournament.getPairingProps().getPairingScheme2().name() );
        //
        
        
        // Handicap
        useHandicapCheck.setSelected( tournament.getPairingProps().getHandicapGames() );
        maxHandicappedRankField.setText( tournament.getPairingProps().getMaxHandicappedRank().toString() );
        setRadioSelection( handicapBasis, tournament.getPairingProps().getHandicapBasis().name() );
        setRadioSelection( handicapModifier, Integer.toString( tournament.getPairingProps().getHandicapModifier() ) );
        txfHdCeiling.setText( Integer.toString( tournament.getPairingProps().getMaxHandicap() ) );
        //
        
        // Placement
        setRadioSelection( absentMms, Integer.toString( tournament.getPlacementProps().getGenMms2ValueAbsent() ) );
        setRadioSelection( absentNbw, Integer.toString( tournament.getPlacementProps().getGenNbw2ValueAbsent() ) );
        setRadioSelection( byeMms, Integer.toString( tournament.getPlacementProps().getGenMms2ValueBye() ) );
        setRadioSelection( byeNbw, Integer.toString( tournament.getPlacementProps().getGenNbw2ValueBye() ) );
        //
        
        
        /*
        Logger.getLogger( TournamentSettings.class.getName() ).log( Level.INFO, "sweep properties" );
        
        for (Map.Entry<String, String> entry : tournament.getProps().getTimeSystemProperties().entrySet() ) {
            Logger.getLogger( TournamentSettings.class.getName() ).log( Level.INFO, entry.getKey() + " " + entry.getValue() );
        }
        * 
        */
        
        
        
        tfMMBar.setText( tournament.getPairingProps().getMMBar().toString() );
        tfMMFloor.setText( tournament.getPairingProps().getMMFloor().toString() );
                
        
        refreshTemplateList();
    }

    private void setRadioSelection( ButtonGroup group, String actionCommand ) {
        Enumeration<AbstractButton> e = group.getElements();
        AbstractButton button;
        while( e.hasMoreElements() ) {
            button = e.nextElement();
            if ( button.getActionCommand().equals( actionCommand ) ) {
                group.setSelected( button.getModel(), true);
                return;
            }
        }
    }
    
    private int radioButtonGroupActionAsInt( ButtonGroup rbg ) {
        int newValue = 0;
        try {
            newValue = Integer.parseInt( rbg.getSelection().getActionCommand() );
        } catch ( NumberFormatException ex ) {
            Logger.getLogger( TournamentSettings.class.getName() ).log( Level.SEVERE, "Non-integer actionCommand in Radio Button Group: " + rbg.getSelection().getActionCommand() );
        }
        return newValue;
    }
    
    private void updateHandicapModifier() {
        int newModifier = radioButtonGroupActionAsInt( handicapModifier );
        tournament.getPairingProps().setHandicapModifier( newModifier );
        tournament.setChangedSinceLastSave(true);
    }
    
    private void updateHandicapBasis() {
        HandicapBasis newHandicapBasis = HandicapBasis.valueOf( handicapBasis.getSelection().getActionCommand() );
        tournament.getPairingProps().setHandicapBasis( newHandicapBasis );
        tournament.setChangedSinceLastSave(true);
    }
    
    
    private void updateTimeSystemDetails() {
        switch( tournament.getProps().getTimeSystem() ) {
            case CANADIAN:
                tournament.getProps().setTimeSystemProperty("StoneCount", tfCanadianStones.getText() );
                tournament.getProps().setTimeSystemProperty("TimeBlockSize", tfCanadianTime.getText() );
                break;
            case BYOYOMI:
                tournament.getProps().setTimeSystemProperty("TimeBlockCount", tfByoYomiPeriods.getText() );
                tournament.getProps().setTimeSystemProperty("TimeBlockSize", tfByoYomiTime.getText() );
                break;
            case FISCHER:
                tournament.getProps().setTimeSystemProperty("TimeIncrement", tfFischerTimeIncrement.getText() );
                break;
        }
        tournament.setChangedSinceLastSave(true);
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rulesetButtonGroup = new javax.swing.ButtonGroup();
        overtimeButtonGroup = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        absentNbw = new javax.swing.ButtonGroup();
        absentMms = new javax.swing.ButtonGroup();
        byeNbw = new javax.swing.ButtonGroup();
        byeMms = new javax.swing.ButtonGroup();
        handicapBasis = new javax.swing.ButtonGroup();
        handicapModifier = new javax.swing.ButtonGroup();
        formerPairingScheme = new javax.swing.ButtonGroup();
        latterPairingScheme = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tfName = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        taComment = new javax.swing.JTextArea();
        tfStartDate = new javax.swing.JTextField();
        tfEndDate = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tfLocation = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        templateDescription = new javax.swing.JTextArea();
        applyTemplateButton = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        templateList1 = new javax.swing.JList();
        jScrollPane10 = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jLabel14 = new javax.swing.JLabel();
        defaultKomiField = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        tfBasicTime = new javax.swing.JTextField();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jRadioButton7 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        overtimePanelFischer = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        tfFischerTimeIncrement = new javax.swing.JTextField();
        overtimePanelCanadian = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        tfCanadianStones = new javax.swing.JTextField();
        tfCanadianTime = new javax.swing.JTextField();
        overtimePanelByoYomi = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        tfByoYomiPeriods = new javax.swing.JTextField();
        tfByoYomiTime = new javax.swing.JTextField();
        overtimePanelSuddenDeath = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        tfNumberOfRounds = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        useHandicapCheck = new javax.swing.JCheckBox();
        txfHdCeiling = new javax.swing.JTextField();
        handicapBasis1 = new javax.swing.JRadioButton();
        handicapModifier3 = new javax.swing.JRadioButton();
        handicapBasis0 = new javax.swing.JRadioButton();
        maxHandicappedRankField = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        handicapModifier2 = new javax.swing.JRadioButton();
        handicapModifier1 = new javax.swing.JRadioButton();
        jLabel29 = new javax.swing.JLabel();
        handicapModifier0 = new javax.swing.JRadioButton();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jRadioButton8 = new javax.swing.JRadioButton();
        pnlMacMahon2 = new javax.swing.JPanel();
        lblMMBar2 = new javax.swing.JLabel();
        lblMMFloor2 = new javax.swing.JLabel();
        tfMMBar = new javax.swing.JTextField();
        tfMMFloor = new javax.swing.JTextField();
        pnlMain = new javax.swing.JPanel();
        pnlLatter = new javax.swing.JPanel();
        rdbLatterSplitAndRandom = new javax.swing.JRadioButton();
        rdbLatterSplitAndFold = new javax.swing.JRadioButton();
        rdbLatterSplitAndSlip = new javax.swing.JRadioButton();
        jLabel20 = new javax.swing.JLabel();
        txfLastRoundForSeedSystem1 = new javax.swing.JTextField();
        pnlFormer = new javax.swing.JPanel();
        ckbAddSortOnRating = new javax.swing.JCheckBox();
        rdbFormerSplitAndRandom = new javax.swing.JRadioButton();
        rdbFormerSplitAndFold = new javax.swing.JRadioButton();
        rdbFormerSplitAndSlip = new javax.swing.JRadioButton();
        pnlSpecialResults = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        rdbAbsentNbw0 = new javax.swing.JRadioButton();
        rdbAbsentNbw1 = new javax.swing.JRadioButton();
        rdbAbsentNbw2 = new javax.swing.JRadioButton();
        rdbAbsentMms0 = new javax.swing.JRadioButton();
        rdbAbsentMms1 = new javax.swing.JRadioButton();
        rdbAbsentMms2 = new javax.swing.JRadioButton();
        rdbByeNbw0 = new javax.swing.JRadioButton();
        rdbByeNbw1 = new javax.swing.JRadioButton();
        rdbByeNbw2 = new javax.swing.JRadioButton();
        rdbByeMms0 = new javax.swing.JRadioButton();
        rdbByeMms1 = new javax.swing.JRadioButton();
        rdbByeMms2 = new javax.swing.JRadioButton();
        jLabel19 = new javax.swing.JLabel();
        rdbAbsentNbw3 = new javax.swing.JRadioButton();
        jLabel22 = new javax.swing.JLabel();
        rdbAbsentNbw6 = new javax.swing.JRadioButton();
        jLabel27 = new javax.swing.JLabel();
        rdbAbsentNbw9 = new javax.swing.JRadioButton();
        jPanel7 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        addTemplateButton = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        newTemplateDescription = new javax.swing.JTextArea();
        newTemplateName = new javax.swing.JTextField();
        removeTemplateButton = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        templateList2 = new javax.swing.JList();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setLayout(new java.awt.GridLayout(1, 0));

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jTabbedPane1.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Identification"));

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Tournament Name");

        jLabel9.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Comment");

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Start Date");

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("End Date");

        tfName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfNameFocusLost(evt);
            }
        });

        taComment.setColumns(20);
        taComment.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        taComment.setRows(4);
        taComment.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                taCommentFocusLost(evt);
            }
        });
        jScrollPane2.setViewportView(taComment);

        tfStartDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfStartDateFocusLost(evt);
            }
        });

        tfEndDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfEndDateFocusLost(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Location");

        tfLocation.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfLocationFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tfLocation)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfEndDate, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfStartDate, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfName))
                .addGap(39, 39, 39))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(tfLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tfStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tfEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Template"));

        templateDescription.setColumns(20);
        templateDescription.setEditable(false);
        templateDescription.setLineWrap(true);
        templateDescription.setRows(5);
        jScrollPane7.setViewportView(templateDescription);

        applyTemplateButton.setText("Apply Template");
        applyTemplateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyTemplateButtonActionPerformed(evt);
            }
        });

        templateList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane6.setViewportView(templateList1);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(applyTemplateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(75, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                        .addComponent(applyTemplateButton))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel1);

        jTabbedPane1.addTab("Info", jScrollPane1);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Ruleset"));

        rulesetButtonGroup.add(jRadioButton2);
        jRadioButton2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jRadioButton2.setText("Japanese");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rulesetRadioActionPerformed(evt);
            }
        });

        rulesetButtonGroup.add(jRadioButton3);
        jRadioButton3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jRadioButton3.setText("AGA");
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rulesetRadioActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Komi");

        defaultKomiField.setText("6.5");
        defaultKomiField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                defaultKomiFieldFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(4, 4, 4)
                        .addComponent(defaultKomiField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton2)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jRadioButton3)))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(defaultKomiField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Time"));

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Basic Time (minutes)");

        jLabel10.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Overtime System");

        tfBasicTime.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfBasicTimeFocusLost(evt);
            }
        });

        overtimeButtonGroup.add(jRadioButton5);
        jRadioButton5.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jRadioButton5.setText("Canadian");
        jRadioButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overtimeRadioActionPerformed(evt);
            }
        });

        overtimeButtonGroup.add(jRadioButton6);
        jRadioButton6.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jRadioButton6.setText("Byo-yomi");
        jRadioButton6.setActionCommand("ByoYomi");
        jRadioButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overtimeRadioActionPerformed(evt);
            }
        });

        overtimeButtonGroup.add(jRadioButton7);
        jRadioButton7.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jRadioButton7.setText("Fischer");
        jRadioButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overtimeRadioActionPerformed(evt);
            }
        });

        overtimeButtonGroup.add(jRadioButton4);
        jRadioButton4.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jRadioButton4.setText("Sudden Death");
        jRadioButton4.setActionCommand("SuddenDeath");
        jRadioButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overtimeRadioActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new java.awt.CardLayout());

        jLabel21.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Time Increment (seconds)");

        tfFischerTimeIncrement.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                timeDetailsFocusLost(evt);
            }
        });

        javax.swing.GroupLayout overtimePanelFischerLayout = new javax.swing.GroupLayout(overtimePanelFischer);
        overtimePanelFischer.setLayout(overtimePanelFischerLayout);
        overtimePanelFischerLayout.setHorizontalGroup(
            overtimePanelFischerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(overtimePanelFischerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfFischerTimeIncrement, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        overtimePanelFischerLayout.setVerticalGroup(
            overtimePanelFischerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(overtimePanelFischerLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(overtimePanelFischerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(tfFischerTimeIncrement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(51, Short.MAX_VALUE))
        );

        jPanel2.add(overtimePanelFischer, "FISCHER");

        jLabel25.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("Number of Stones");

        jLabel26.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel26.setText("Time (seconds)");

        tfCanadianStones.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                timeDetailsFocusLost(evt);
            }
        });

        tfCanadianTime.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                timeDetailsFocusLost(evt);
            }
        });

        javax.swing.GroupLayout overtimePanelCanadianLayout = new javax.swing.GroupLayout(overtimePanelCanadian);
        overtimePanelCanadian.setLayout(overtimePanelCanadianLayout);
        overtimePanelCanadianLayout.setHorizontalGroup(
            overtimePanelCanadianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(overtimePanelCanadianLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(overtimePanelCanadianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(overtimePanelCanadianLayout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfCanadianStones, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(overtimePanelCanadianLayout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfCanadianTime, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(50, Short.MAX_VALUE))
        );
        overtimePanelCanadianLayout.setVerticalGroup(
            overtimePanelCanadianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(overtimePanelCanadianLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(overtimePanelCanadianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(tfCanadianStones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(overtimePanelCanadianLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(tfCanadianTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel2.add(overtimePanelCanadian, "CANADIAN");

        jLabel23.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("Number of Periods");

        jLabel24.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("Period Time (seconds)");

        tfByoYomiPeriods.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                timeDetailsFocusLost(evt);
            }
        });

        tfByoYomiTime.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                timeDetailsFocusLost(evt);
            }
        });

        javax.swing.GroupLayout overtimePanelByoYomiLayout = new javax.swing.GroupLayout(overtimePanelByoYomi);
        overtimePanelByoYomi.setLayout(overtimePanelByoYomiLayout);
        overtimePanelByoYomiLayout.setHorizontalGroup(
            overtimePanelByoYomiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(overtimePanelByoYomiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(overtimePanelByoYomiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(overtimePanelByoYomiLayout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfByoYomiPeriods, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(overtimePanelByoYomiLayout.createSequentialGroup()
                        .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfByoYomiTime, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        overtimePanelByoYomiLayout.setVerticalGroup(
            overtimePanelByoYomiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(overtimePanelByoYomiLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(overtimePanelByoYomiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(tfByoYomiPeriods, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(overtimePanelByoYomiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(tfByoYomiTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel2.add(overtimePanelByoYomi, "BYOYOMI");

        javax.swing.GroupLayout overtimePanelSuddenDeathLayout = new javax.swing.GroupLayout(overtimePanelSuddenDeath);
        overtimePanelSuddenDeath.setLayout(overtimePanelSuddenDeathLayout);
        overtimePanelSuddenDeathLayout.setHorizontalGroup(
            overtimePanelSuddenDeathLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 293, Short.MAX_VALUE)
        );
        overtimePanelSuddenDeathLayout.setVerticalGroup(
            overtimePanelSuddenDeathLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 83, Short.MAX_VALUE)
        );

        jPanel2.add(overtimePanelSuddenDeath, "SUDDEN_DEATH");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton5)
                            .addComponent(jRadioButton4)
                            .addComponent(jRadioButton7)
                            .addComponent(jRadioButton6)
                            .addComponent(tfBasicTime, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(tfBasicTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jRadioButton5)
                        .addGap(16, 16, 16)
                        .addComponent(jRadioButton7))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jRadioButton6))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jRadioButton4))
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel11.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Number of Rounds");

        tfNumberOfRounds.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfNumberOfRoundsFocusLost(evt);
            }
        });

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Handicapping"));

        useHandicapCheck.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        useHandicapCheck.setText("Handicap");
        useHandicapCheck.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        useHandicapCheck.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        useHandicapCheck.setIconTextGap(12);
        useHandicapCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useHandicapCheckActionPerformed(evt);
            }
        });

        txfHdCeiling.setText("9");
        txfHdCeiling.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txfHdCeilingFocusLost(evt);
            }
        });

        handicapBasis.add(handicapBasis1);
        handicapBasis1.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        handicapBasis1.setText("Rank");
        handicapBasis1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handicapBasis1ActionPerformed(evt);
            }
        });

        handicapModifier.add(handicapModifier3);
        handicapModifier3.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        handicapModifier3.setText("handicap -3");
        handicapModifier3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        handicapModifier3.setMargin(new java.awt.Insets(0, 0, 0, 0));
        handicapModifier3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handicapModifier3ActionPerformed(evt);
            }
        });

        handicapBasis.add(handicapBasis0);
        handicapBasis0.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        handicapBasis0.setText("McMahon score");
        handicapBasis0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handicapBasis0ActionPerformed(evt);
            }
        });

        maxHandicappedRankField.setText("1D");
        maxHandicappedRankField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                maxHandicappedRankFieldFocusLost(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Maximum handicap");

        jLabel30.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel30.setText("Handicap modifier");

        handicapModifier.add(handicapModifier2);
        handicapModifier2.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        handicapModifier2.setText("handicap -2");
        handicapModifier2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        handicapModifier2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        handicapModifier2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handicapModifier2ActionPerformed(evt);
            }
        });

        handicapModifier.add(handicapModifier1);
        handicapModifier1.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        handicapModifier1.setText("handicap -1");
        handicapModifier1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        handicapModifier1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        handicapModifier1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handicapModifier1ActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel29.setText("Handicap basis");

        handicapModifier.add(handicapModifier0);
        handicapModifier0.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        handicapModifier0.setText("handicap not decreased");
        handicapModifier0.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        handicapModifier0.setMargin(new java.awt.Insets(0, 0, 0, 0));
        handicapModifier0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                handicapModifier0ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Maximum handicapped rank");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(97, 97, 97)
                        .addComponent(useHandicapCheck))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txfHdCeiling, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(maxHandicappedRankField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(handicapBasis0, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(handicapBasis1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(handicapModifier1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(handicapModifier2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(handicapModifier3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(handicapModifier0, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 41, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(handicapModifier0, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel30))
                        .addGap(0, 0, 0)
                        .addComponent(handicapModifier1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(handicapModifier2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                        .addComponent(useHandicapCheck)
                        .addGap(8, 8, 8)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(maxHandicappedRankField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(txfHdCeiling, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(handicapModifier3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(handicapBasis0, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addComponent(handicapBasis1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 7, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfNumberOfRounds, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfNumberOfRounds, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
        );

        jScrollPane10.setViewportView(jPanel5);

        jTabbedPane1.addTab("Rules", jScrollPane10);

        jLabel12.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Pairing format");

        jRadioButton8.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jRadioButton8.setSelected(true);
        jRadioButton8.setText("McMahon");
        jRadioButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton8ActionPerformed(evt);
            }
        });

        pnlMacMahon2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "McMahon"));

        lblMMBar2.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        lblMMBar2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblMMBar2.setText("McMahon Bar");

        lblMMFloor2.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        lblMMFloor2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblMMFloor2.setText("McMahon Floor");

        tfMMBar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfMMBarFocusLost(evt);
            }
        });

        tfMMFloor.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfMMFloorFocusLost(evt);
            }
        });

        javax.swing.GroupLayout pnlMacMahon2Layout = new javax.swing.GroupLayout(pnlMacMahon2);
        pnlMacMahon2.setLayout(pnlMacMahon2Layout);
        pnlMacMahon2Layout.setHorizontalGroup(
            pnlMacMahon2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMacMahon2Layout.createSequentialGroup()
                .addGroup(pnlMacMahon2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblMMBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblMMFloor2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlMacMahon2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfMMBar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfMMFloor, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnlMacMahon2Layout.setVerticalGroup(
            pnlMacMahon2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMacMahon2Layout.createSequentialGroup()
                .addGroup(pnlMacMahon2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMMBar2)
                    .addComponent(tfMMBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(pnlMacMahon2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMMFloor2)
                    .addComponent(tfMMFloor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 12, Short.MAX_VALUE))
        );

        pnlMain.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Pairing Scheme"));

        pnlLatter.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Scheme B"));

        latterPairingScheme.add(rdbLatterSplitAndRandom);
        rdbLatterSplitAndRandom.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rdbLatterSplitAndRandom.setText("Split and Random");
        rdbLatterSplitAndRandom.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbLatterSplitAndRandom.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbLatterSplitAndRandom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbLatterSchemeActionPerformed(evt);
            }
        });

        latterPairingScheme.add(rdbLatterSplitAndFold);
        rdbLatterSplitAndFold.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rdbLatterSplitAndFold.setSelected(true);
        rdbLatterSplitAndFold.setText("Split and Fold");
        rdbLatterSplitAndFold.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbLatterSplitAndFold.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbLatterSplitAndFold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbLatterSchemeActionPerformed(evt);
            }
        });

        latterPairingScheme.add(rdbLatterSplitAndSlip);
        rdbLatterSplitAndSlip.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rdbLatterSplitAndSlip.setText("Split and Slip");
        rdbLatterSplitAndSlip.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbLatterSplitAndSlip.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbLatterSplitAndSlip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbLatterSchemeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlLatterLayout = new javax.swing.GroupLayout(pnlLatter);
        pnlLatter.setLayout(pnlLatterLayout);
        pnlLatterLayout.setHorizontalGroup(
            pnlLatterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLatterLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlLatterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rdbLatterSplitAndRandom, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbLatterSplitAndFold, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbLatterSplitAndSlip, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(57, Short.MAX_VALUE))
        );
        pnlLatterLayout.setVerticalGroup(
            pnlLatterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLatterLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(rdbLatterSplitAndRandom)
                .addGap(5, 5, 5)
                .addComponent(rdbLatterSplitAndFold)
                .addGap(5, 5, 5)
                .addComponent(rdbLatterSplitAndSlip)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel20.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("Use Scheme A up to round");

        txfLastRoundForSeedSystem1.setText("2");
        txfLastRoundForSeedSystem1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txfLastRoundForSeedSystem1FocusLost(evt);
            }
        });

        pnlFormer.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Scheme A"));

        ckbAddSortOnRating.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        ckbAddSortOnRating.setSelected(true);
        ckbAddSortOnRating.setText("Add a sorting on rating ");
        ckbAddSortOnRating.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ckbAddSortOnRating.setEnabled(false);
        ckbAddSortOnRating.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ckbAddSortOnRating.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ckbAddSortOnRatingActionPerformed(evt);
            }
        });

        formerPairingScheme.add(rdbFormerSplitAndRandom);
        rdbFormerSplitAndRandom.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rdbFormerSplitAndRandom.setSelected(true);
        rdbFormerSplitAndRandom.setText("Split and Random");
        rdbFormerSplitAndRandom.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbFormerSplitAndRandom.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbFormerSplitAndRandom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbFormerSchemeActionPerformed(evt);
            }
        });

        formerPairingScheme.add(rdbFormerSplitAndFold);
        rdbFormerSplitAndFold.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rdbFormerSplitAndFold.setText("Split and Fold");
        rdbFormerSplitAndFold.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbFormerSplitAndFold.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbFormerSplitAndFold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbFormerSchemeActionPerformed(evt);
            }
        });

        formerPairingScheme.add(rdbFormerSplitAndSlip);
        rdbFormerSplitAndSlip.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rdbFormerSplitAndSlip.setText("Split and Slip");
        rdbFormerSplitAndSlip.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbFormerSplitAndSlip.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbFormerSplitAndSlip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbFormerSchemeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFormerLayout = new javax.swing.GroupLayout(pnlFormer);
        pnlFormer.setLayout(pnlFormerLayout);
        pnlFormerLayout.setHorizontalGroup(
            pnlFormerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormerLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(pnlFormerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ckbAddSortOnRating, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbFormerSplitAndRandom, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbFormerSplitAndFold, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rdbFormerSplitAndSlip, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        pnlFormerLayout.setVerticalGroup(
            pnlFormerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFormerLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(ckbAddSortOnRating)
                .addGap(5, 5, 5)
                .addComponent(rdbFormerSplitAndRandom)
                .addGap(5, 5, 5)
                .addComponent(rdbFormerSplitAndFold)
                .addGap(5, 5, 5)
                .addComponent(rdbFormerSplitAndSlip)
                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlMainLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(pnlFormer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(pnlLatter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlMainLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txfLastRoundForSeedSystem1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMainLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txfLastRoundForSeedSystem1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pnlFormer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlLatter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        pnlSpecialResults.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Result Values"));
        pnlSpecialResults.setLayout(null);

        jLabel15.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel15.setText("NBW for Absent player");
        pnlSpecialResults.add(jLabel15);
        jLabel15.setBounds(10, 80, 130, 15);

        jLabel16.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel16.setText("MMS for Absent player");
        pnlSpecialResults.add(jLabel16);
        jLabel16.setBounds(10, 100, 130, 15);

        jLabel17.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel17.setText("NBW for Bye player ");
        pnlSpecialResults.add(jLabel17);
        jLabel17.setBounds(10, 120, 130, 15);

        jLabel18.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel18.setText("MMS for Bye player ");
        pnlSpecialResults.add(jLabel18);
        jLabel18.setBounds(10, 140, 130, 15);

        absentNbw.add(rdbAbsentNbw0);
        rdbAbsentNbw0.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rdbAbsentNbw0.setText("0");
        rdbAbsentNbw0.setActionCommand("");
        rdbAbsentNbw0.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbAbsentNbw0.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbAbsentNbw0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbAbsentNbwActionPerformed(evt);
            }
        });
        pnlSpecialResults.add(rdbAbsentNbw0);
        rdbAbsentNbw0.setBounds(140, 80, 40, 15);

        absentNbw.add(rdbAbsentNbw1);
        rdbAbsentNbw1.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rdbAbsentNbw1.setText("½");
        rdbAbsentNbw1.setActionCommand("");
        rdbAbsentNbw1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbAbsentNbw1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbAbsentNbw1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbAbsentNbwActionPerformed(evt);
            }
        });
        pnlSpecialResults.add(rdbAbsentNbw1);
        rdbAbsentNbw1.setBounds(180, 80, 40, 15);

        absentNbw.add(rdbAbsentNbw2);
        rdbAbsentNbw2.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rdbAbsentNbw2.setText("1");
        rdbAbsentNbw2.setActionCommand("");
        rdbAbsentNbw2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbAbsentNbw2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbAbsentNbw2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbAbsentNbwActionPerformed(evt);
            }
        });
        pnlSpecialResults.add(rdbAbsentNbw2);
        rdbAbsentNbw2.setBounds(220, 80, 40, 15);

        absentMms.add(rdbAbsentMms0);
        rdbAbsentMms0.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rdbAbsentMms0.setText("0");
        rdbAbsentMms0.setActionCommand("");
        rdbAbsentMms0.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbAbsentMms0.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbAbsentMms0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbAbsentMmsActionPerformed(evt);
            }
        });
        pnlSpecialResults.add(rdbAbsentMms0);
        rdbAbsentMms0.setBounds(140, 100, 40, 15);

        absentMms.add(rdbAbsentMms1);
        rdbAbsentMms1.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rdbAbsentMms1.setText("½");
        rdbAbsentMms1.setActionCommand("");
        rdbAbsentMms1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbAbsentMms1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbAbsentMms1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbAbsentMmsActionPerformed(evt);
            }
        });
        pnlSpecialResults.add(rdbAbsentMms1);
        rdbAbsentMms1.setBounds(180, 100, 40, 15);

        absentMms.add(rdbAbsentMms2);
        rdbAbsentMms2.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rdbAbsentMms2.setText("1");
        rdbAbsentMms2.setActionCommand("");
        rdbAbsentMms2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbAbsentMms2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbAbsentMms2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbAbsentMmsActionPerformed(evt);
            }
        });
        pnlSpecialResults.add(rdbAbsentMms2);
        rdbAbsentMms2.setBounds(220, 100, 40, 15);

        byeNbw.add(rdbByeNbw0);
        rdbByeNbw0.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rdbByeNbw0.setText("0");
        rdbByeNbw0.setActionCommand("");
        rdbByeNbw0.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbByeNbw0.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbByeNbw0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbByeNbwActionPerformed(evt);
            }
        });
        pnlSpecialResults.add(rdbByeNbw0);
        rdbByeNbw0.setBounds(140, 120, 40, 15);

        byeNbw.add(rdbByeNbw1);
        rdbByeNbw1.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rdbByeNbw1.setText("½");
        rdbByeNbw1.setActionCommand("");
        rdbByeNbw1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbByeNbw1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbByeNbw1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbByeNbwActionPerformed(evt);
            }
        });
        pnlSpecialResults.add(rdbByeNbw1);
        rdbByeNbw1.setBounds(180, 120, 40, 15);

        byeNbw.add(rdbByeNbw2);
        rdbByeNbw2.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rdbByeNbw2.setText("1");
        rdbByeNbw2.setActionCommand("");
        rdbByeNbw2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbByeNbw2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbByeNbw2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbByeNbwActionPerformed(evt);
            }
        });
        pnlSpecialResults.add(rdbByeNbw2);
        rdbByeNbw2.setBounds(220, 120, 40, 15);

        byeMms.add(rdbByeMms0);
        rdbByeMms0.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rdbByeMms0.setText("0");
        rdbByeMms0.setActionCommand("");
        rdbByeMms0.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbByeMms0.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbByeMms0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbByeMmsActionPerformed(evt);
            }
        });
        pnlSpecialResults.add(rdbByeMms0);
        rdbByeMms0.setBounds(140, 140, 40, 15);

        byeMms.add(rdbByeMms1);
        rdbByeMms1.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rdbByeMms1.setText("½");
        rdbByeMms1.setActionCommand("");
        rdbByeMms1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbByeMms1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbByeMms1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbByeMmsActionPerformed(evt);
            }
        });
        pnlSpecialResults.add(rdbByeMms1);
        rdbByeMms1.setBounds(180, 140, 40, 15);

        byeMms.add(rdbByeMms2);
        rdbByeMms2.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rdbByeMms2.setText("1");
        rdbByeMms2.setActionCommand("");
        rdbByeMms2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbByeMms2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        rdbByeMms2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbByeMmsActionPerformed(evt);
            }
        });
        pnlSpecialResults.add(rdbByeMms2);
        rdbByeMms2.setBounds(220, 140, 40, 15);

        jLabel19.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel19.setText("NBW/MMS for win");
        pnlSpecialResults.add(jLabel19);
        jLabel19.setBounds(10, 20, 130, 15);

        rdbAbsentNbw3.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rdbAbsentNbw3.setSelected(true);
        rdbAbsentNbw3.setText("1");
        rdbAbsentNbw3.setActionCommand("");
        rdbAbsentNbw3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbAbsentNbw3.setEnabled(false);
        rdbAbsentNbw3.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlSpecialResults.add(rdbAbsentNbw3);
        rdbAbsentNbw3.setBounds(220, 20, 40, 15);

        jLabel22.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel22.setText("NBW/MMS for draw");
        pnlSpecialResults.add(jLabel22);
        jLabel22.setBounds(10, 40, 110, 15);

        rdbAbsentNbw6.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rdbAbsentNbw6.setSelected(true);
        rdbAbsentNbw6.setText("½");
        rdbAbsentNbw6.setActionCommand("");
        rdbAbsentNbw6.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbAbsentNbw6.setEnabled(false);
        rdbAbsentNbw6.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlSpecialResults.add(rdbAbsentNbw6);
        rdbAbsentNbw6.setBounds(180, 40, 40, 15);

        jLabel27.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel27.setText("NBW/MMS for loss");
        pnlSpecialResults.add(jLabel27);
        jLabel27.setBounds(10, 60, 110, 15);

        rdbAbsentNbw9.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        rdbAbsentNbw9.setSelected(true);
        rdbAbsentNbw9.setText("0");
        rdbAbsentNbw9.setActionCommand("");
        rdbAbsentNbw9.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rdbAbsentNbw9.setEnabled(false);
        rdbAbsentNbw9.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pnlSpecialResults.add(rdbAbsentNbw9);
        rdbAbsentNbw9.setBounds(140, 60, 40, 15);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jRadioButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(pnlMacMahon2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pnlSpecialResults, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(104, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jRadioButton8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnlMacMahon2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlSpecialResults, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(pnlMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        jScrollPane3.setViewportView(jPanel3);

        jTabbedPane1.addTab("Pairing", jScrollPane3);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Template Management"));

        addTemplateButton.setText("Add/Replace");
        addTemplateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTemplateButtonActionPerformed(evt);
            }
        });

        newTemplateDescription.setColumns(20);
        newTemplateDescription.setLineWrap(true);
        newTemplateDescription.setRows(5);
        jScrollPane8.setViewportView(newTemplateDescription);

        removeTemplateButton.setText("Remove");
        removeTemplateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeTemplateButtonActionPerformed(evt);
            }
        });

        templateList2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane9.setViewportView(templateList2);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Name");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Description");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(newTemplateName, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(addTemplateButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeTemplateButton)))
                .addContainerGap(104, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addGap(0, 9, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(newTemplateName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(addTemplateButton)
                                    .addComponent(removeTemplateButton)))
                            .addComponent(jLabel6)))
                    .addComponent(jScrollPane9))
                .addGap(177, 177, 177))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(86, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Templates", jPanel7);

        add(jTabbedPane1);
    }// </editor-fold>//GEN-END:initComponents

    private void tfMMFloorFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfMMFloorFocusLost
        tournament.getPairingProps().setMMFloor( tfMMFloor.getText() );
        tfMMFloor.setText( tournament.getPairingProps().getMMFloor().toString() );
        tournament.setChangedSinceLastSave(true);
    }//GEN-LAST:event_tfMMFloorFocusLost

    private void tfMMBarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfMMBarFocusLost
        tournament.getPairingProps().setMMBar( tfMMBar.getText() );
        tfMMBar.setText( tournament.getPairingProps().getMMBar().toString() );
        tournament.setChangedSinceLastSave(true);
    }//GEN-LAST:event_tfMMBarFocusLost

    private void maxHandicappedRankFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_maxHandicappedRankFieldFocusLost
        tournament.getPairingProps().setMaxHandicappedRank( maxHandicappedRankField.getText() );
        maxHandicappedRankField.setText( tournament.getPairingProps().getMaxHandicappedRank().toString() );
        tournament.setChangedSinceLastSave(true);
    }//GEN-LAST:event_maxHandicappedRankFieldFocusLost

    private void handicapModifier0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_handicapModifier0ActionPerformed
        updateHandicapModifier();
    }//GEN-LAST:event_handicapModifier0ActionPerformed

    private void handicapModifier1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_handicapModifier1ActionPerformed
        updateHandicapModifier();
    }//GEN-LAST:event_handicapModifier1ActionPerformed

    private void handicapModifier2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_handicapModifier2ActionPerformed
        updateHandicapModifier();
    }//GEN-LAST:event_handicapModifier2ActionPerformed

    private void handicapModifier3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_handicapModifier3ActionPerformed
        updateHandicapModifier();
    }//GEN-LAST:event_handicapModifier3ActionPerformed

    private void txfHdCeilingFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txfHdCeilingFocusLost
        tournament.getPairingProps().setMaxHandicap( txfHdCeiling.getText()  );
        txfHdCeiling.setText( Integer.toString( tournament.getPairingProps().getMaxHandicap() ) );
        tournament.setChangedSinceLastSave(true);
    }//GEN-LAST:event_txfHdCeilingFocusLost

    private void handicapBasis0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_handicapBasis0ActionPerformed
        updateHandicapBasis();
    }//GEN-LAST:event_handicapBasis0ActionPerformed

    private void handicapBasis1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_handicapBasis1ActionPerformed
        updateHandicapBasis();
    }//GEN-LAST:event_handicapBasis1ActionPerformed

    private void txfLastRoundForSeedSystem1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txfLastRoundForSeedSystem1FocusLost
        String newRoundIndex = txfLastRoundForSeedSystem1.getText();
        tournament.getPairingProps().setLastRoundForSeedSystem1( newRoundIndex );
        txfLastRoundForSeedSystem1.setText( Integer.toString( tournament.getPairingProps().getLastRoundForSeedSystem1() ) );
    }//GEN-LAST:event_txfLastRoundForSeedSystem1FocusLost

    private void useHandicapCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useHandicapCheckActionPerformed
        tournament.getPairingProps().setHandicapGames( useHandicapCheck.isSelected() );
        tournament.setChangedSinceLastSave(true);
    }//GEN-LAST:event_useHandicapCheckActionPerformed

    private void rulesetRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rulesetRadioActionPerformed
        tournament.getProps().setRuleset( Ruleset.valueOf( evt.getActionCommand() )  );
        tournament.setChangedSinceLastSave(true);
    }//GEN-LAST:event_rulesetRadioActionPerformed

    private void overtimeRadioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_overtimeRadioActionPerformed
        String radioKey = overtimeButtonGroup.getSelection().getActionCommand();
        
        CardLayout cl = (CardLayout) jPanel2.getLayout();
        cl.show( jPanel2, radioKey );

        TimeSystem newTimeSystem = TimeSystem.valueOf( radioKey );
        tournament.getProps().setTimeSystem( newTimeSystem );
        
        //Logger.getLogger( TournamentSettings.class.getName() ).log( Level.INFO, radioKey );
        
        updateTimeSystemDetails();
        
    }//GEN-LAST:event_overtimeRadioActionPerformed

    private void tfNumberOfRoundsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfNumberOfRoundsFocusLost
        int numberOfRounds;
        try {
            numberOfRounds = Integer.parseInt( tfNumberOfRounds.getText() );
            numberOfRounds = tournament.getPairingProps().setNumberOfRounds( numberOfRounds );
        } catch( NumberFormatException ex ) {
            numberOfRounds = tournament.getPairingProps().getNumberOfRounds();
        }
        
        tfNumberOfRounds.setText( Integer.toString( numberOfRounds ) );
        tournament.setChangedSinceLastSave(true);
    }//GEN-LAST:event_tfNumberOfRoundsFocusLost

    private void rdbAbsentNbwActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbAbsentNbwActionPerformed
        tournament.getPlacementProps().setGenNbw2ValueAbsent( radioButtonGroupActionAsInt( absentNbw ) );
        tournament.setChangedSinceLastSave(true);
        
    }//GEN-LAST:event_rdbAbsentNbwActionPerformed

    private void rdbAbsentMmsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbAbsentMmsActionPerformed
        tournament.getPlacementProps().setGenNbw2ValueAbsent( radioButtonGroupActionAsInt( absentMms ) );
        tournament.setChangedSinceLastSave(true);
    }//GEN-LAST:event_rdbAbsentMmsActionPerformed

    private void rdbByeNbwActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbByeNbwActionPerformed
        tournament.getPlacementProps().setGenNbw2ValueAbsent( radioButtonGroupActionAsInt( byeNbw ) );
        tournament.setChangedSinceLastSave(true);
    }//GEN-LAST:event_rdbByeNbwActionPerformed

    private void rdbByeMmsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbByeMmsActionPerformed
        tournament.getPlacementProps().setGenNbw2ValueAbsent( radioButtonGroupActionAsInt( byeMms ) );
        tournament.setChangedSinceLastSave(true);
    }//GEN-LAST:event_rdbByeMmsActionPerformed

    private void ckbAddSortOnRatingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ckbAddSortOnRatingActionPerformed
        // Sort on rating is added automatically. This is no longer optoinal.
//        int newPaiMaAdditionalPlacementCritSystem1 = ckbAddSortOnRating.isSelected() ? PlacementProperties.PLA_CRIT_RATING : 0;
//        tournament.getPairingProps().setPaiMaAdditionalPlacementCritSystem1( newPaiMaAdditionalPlacementCritSystem1 );
    }//GEN-LAST:event_ckbAddSortOnRatingActionPerformed

    private void rdbFormerSchemeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbFormerSchemeActionPerformed
        PairingMethod newScheme = PairingMethod.valueOf( formerPairingScheme.getSelection().getActionCommand() );
        tournament.getPairingProps().setPairingScheme1( newScheme );
        tournament.setChangedSinceLastSave(true);
        
    }//GEN-LAST:event_rdbFormerSchemeActionPerformed

    private void rdbLatterSchemeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbLatterSchemeActionPerformed
        PairingMethod newScheme = PairingMethod.valueOf( latterPairingScheme.getSelection().getActionCommand() );
        tournament.getPairingProps().setPairingScheme2( newScheme );
        tournament.setChangedSinceLastSave(true);
        
    }//GEN-LAST:event_rdbLatterSchemeActionPerformed

    private void tfBasicTimeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfBasicTimeFocusLost
        tournament.getProps().setBasicTime( tfBasicTime.getText() );
        tfBasicTime.setText( Integer.toString( tournament.getProps().getBasicTime() ) );
        tournament.setChangedSinceLastSave(true);
    }//GEN-LAST:event_tfBasicTimeFocusLost

    private void jRadioButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton8ActionPerformed
       // Since there is only one supported system, this button does nothing.
    }//GEN-LAST:event_jRadioButton8ActionPerformed

    private void tfNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfNameFocusLost
        tournament.getProps().setName( tfName.getText() );
        tournament.setChangedSinceLastSave(true);
        
        tournament.fireIdChange();
    }//GEN-LAST:event_tfNameFocusLost

    private void timeDetailsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_timeDetailsFocusLost
        updateTimeSystemDetails();
    }//GEN-LAST:event_timeDetailsFocusLost

    private void defaultKomiFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_defaultKomiFieldFocusLost
        tournament.getPairingProps().setDefaultKomi( defaultKomiField.getText() );
        defaultKomiField.setText( tournament.getPairingProps().getDefaultKomiString() );
        tournament.setChangedSinceLastSave(true);
    }//GEN-LAST:event_defaultKomiFieldFocusLost

    private void taCommentFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_taCommentFocusLost
        tournament.getProps().setComment( taComment.getText() );
        taComment.setText(tournament.getProps().getComment() );
        tournament.setChangedSinceLastSave(true);
    }//GEN-LAST:event_taCommentFocusLost

    private void tfLocationFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfLocationFocusLost
        tournament.getProps().setLocation( tfLocation.getText() );
        tfLocation.setText(tournament.getProps().getLocation() );
        tournament.setChangedSinceLastSave(true);
    }//GEN-LAST:event_tfLocationFocusLost

    private void tfStartDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfStartDateFocusLost
        tournament.getProps().setStartDate( tfStartDate.getText() );
        tfStartDate.setText(tournament.getProps().getStartDateString() );
        tournament.setChangedSinceLastSave(true);
        tournament.fireIdChange();
        
    }//GEN-LAST:event_tfStartDateFocusLost

    private void tfEndDateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tfEndDateFocusLost
        tournament.getProps().setEndDate( tfEndDate.getText() );
        tfEndDate.setText(tournament.getProps().getEndDateString() );
        tournament.setChangedSinceLastSave(true);
        tournament.fireIdChange();
    }//GEN-LAST:event_tfEndDateFocusLost

    private void addTemplateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTemplateButtonActionPerformed
        String name = newTemplateName.getText();
        if ( name.equals("") ) {
            JOptionPane.showMessageDialog(this, "Template must have a name.", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check for existing template and get confirmation.
        if ( TemplateManager.getTemplate( name ) != null ) {
            int response = JOptionPane.showConfirmDialog( this, "Replace template " + name + " with current tournament settings?", "Query", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if ( response != JOptionPane.YES_OPTION ) {
                return;
            }
        }

        TemplateManager.addTemplate( name,
                                     newTemplateDescription.getText(),
                                     tournament );
        
        refreshTemplateList();
        
        
    }//GEN-LAST:event_addTemplateButtonActionPerformed

    private void removeTemplateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeTemplateButtonActionPerformed
        String selectedTemplateName = (String) templateList2.getSelectedValue();
        if ( selectedTemplateName == null ) {
            JOptionPane.showMessageDialog(this, "No template selected.", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        TemplateManager.removeTemplate( selectedTemplateName );
        refreshTemplateList();
        
        
    }//GEN-LAST:event_removeTemplateButtonActionPerformed

    private void applyTemplateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyTemplateButtonActionPerformed
        String selectedTemplateName = (String) templateList1.getSelectedValue();
        if ( selectedTemplateName == null ) {
            JOptionPane.showMessageDialog(this, "No template selected.", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int response = JOptionPane.showConfirmDialog(this, "Applying a template will overwrite current tournament settings. Continue?", "Query", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
        if (response == JOptionPane.YES_OPTION) {
            TemplateManager.applyTemplate( selectedTemplateName, tournament );
            updateControls();            
        }
    }//GEN-LAST:event_applyTemplateButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup absentMms;
    private javax.swing.ButtonGroup absentNbw;
    private javax.swing.JButton addTemplateButton;
    private javax.swing.JButton applyTemplateButton;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup byeMms;
    private javax.swing.ButtonGroup byeNbw;
    private javax.swing.JCheckBox ckbAddSortOnRating;
    private javax.swing.JTextField defaultKomiField;
    private javax.swing.ButtonGroup formerPairingScheme;
    private javax.swing.ButtonGroup handicapBasis;
    private javax.swing.JRadioButton handicapBasis0;
    private javax.swing.JRadioButton handicapBasis1;
    private javax.swing.ButtonGroup handicapModifier;
    private javax.swing.JRadioButton handicapModifier0;
    private javax.swing.JRadioButton handicapModifier1;
    private javax.swing.JRadioButton handicapModifier2;
    private javax.swing.JRadioButton handicapModifier3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JRadioButton jRadioButton8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.ButtonGroup latterPairingScheme;
    private javax.swing.JLabel lblMMBar2;
    private javax.swing.JLabel lblMMFloor2;
    private javax.swing.JTextField maxHandicappedRankField;
    private javax.swing.JTextArea newTemplateDescription;
    private javax.swing.JTextField newTemplateName;
    private javax.swing.ButtonGroup overtimeButtonGroup;
    private javax.swing.JPanel overtimePanelByoYomi;
    private javax.swing.JPanel overtimePanelCanadian;
    private javax.swing.JPanel overtimePanelFischer;
    private javax.swing.JPanel overtimePanelSuddenDeath;
    private javax.swing.JPanel pnlFormer;
    private javax.swing.JPanel pnlLatter;
    private javax.swing.JPanel pnlMacMahon2;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlSpecialResults;
    private javax.swing.JRadioButton rdbAbsentMms0;
    private javax.swing.JRadioButton rdbAbsentMms1;
    private javax.swing.JRadioButton rdbAbsentMms2;
    private javax.swing.JRadioButton rdbAbsentNbw0;
    private javax.swing.JRadioButton rdbAbsentNbw1;
    private javax.swing.JRadioButton rdbAbsentNbw2;
    private javax.swing.JRadioButton rdbAbsentNbw3;
    private javax.swing.JRadioButton rdbAbsentNbw6;
    private javax.swing.JRadioButton rdbAbsentNbw9;
    private javax.swing.JRadioButton rdbByeMms0;
    private javax.swing.JRadioButton rdbByeMms1;
    private javax.swing.JRadioButton rdbByeMms2;
    private javax.swing.JRadioButton rdbByeNbw0;
    private javax.swing.JRadioButton rdbByeNbw1;
    private javax.swing.JRadioButton rdbByeNbw2;
    private javax.swing.JRadioButton rdbFormerSplitAndFold;
    private javax.swing.JRadioButton rdbFormerSplitAndRandom;
    private javax.swing.JRadioButton rdbFormerSplitAndSlip;
    private javax.swing.JRadioButton rdbLatterSplitAndFold;
    private javax.swing.JRadioButton rdbLatterSplitAndRandom;
    private javax.swing.JRadioButton rdbLatterSplitAndSlip;
    private javax.swing.JButton removeTemplateButton;
    private javax.swing.ButtonGroup rulesetButtonGroup;
    private javax.swing.JTextArea taComment;
    private javax.swing.JTextArea templateDescription;
    private javax.swing.JList templateList1;
    private javax.swing.JList templateList2;
    private javax.swing.JTextField tfBasicTime;
    private javax.swing.JTextField tfByoYomiPeriods;
    private javax.swing.JTextField tfByoYomiTime;
    private javax.swing.JTextField tfCanadianStones;
    private javax.swing.JTextField tfCanadianTime;
    private javax.swing.JTextField tfEndDate;
    private javax.swing.JTextField tfFischerTimeIncrement;
    private javax.swing.JTextField tfLocation;
    private javax.swing.JTextField tfMMBar;
    private javax.swing.JTextField tfMMFloor;
    private javax.swing.JTextField tfName;
    private javax.swing.JTextField tfNumberOfRounds;
    private javax.swing.JTextField tfStartDate;
    private javax.swing.JTextField txfHdCeiling;
    private javax.swing.JTextField txfLastRoundForSeedSystem1;
    private javax.swing.JCheckBox useHandicapCheck;
    // End of variables declaration//GEN-END:variables
}
