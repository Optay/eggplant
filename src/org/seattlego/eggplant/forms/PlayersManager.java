package org.seattlego.eggplant.forms;

import java.awt.Color;
import java.awt.print.PrinterException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.seattlego.eggplant.Eggplant;
import org.seattlego.eggplant.model.*;
import org.seattlego.eggplant.printing.CustomPrintableTable;

/**
 *
 * @author Topsy
 */
public class PlayersManager extends EggplantForm {

    private RatingsList ratingsList;
    //private ObservableList<PlayerId> availablePlayers;
    private ArrayList<PlayerId> availablePlayers;
    
    private Player selectedPlayer;
    private ObservableList<Player> registeredPlayers;
    
    
    private PlayersManagerState state;
    
    private JCheckBox[] participationCheckBoxes;

    
    /**
     * Creates new form PlayersManager
     */
    public PlayersManager() {
        
        state = PlayersManagerState.NEW;
        
        // Auto-generated
        initComponents();
        
        init();
        
    }

    /*
     * Any configuration that is not covered in the auto-code.
     */
    private void init() {
        // Initialize registered players table.
        registeredPlayersTable.setAutoCreateColumnsFromModel(false);
        
        registeredPlayers = ObservableCollections.observableList( new ArrayList<Player>() );
        
        // BindingGroup bindingGroup = new BindingGroup();  // Don't think we need this.
        JTableBinding jTableBinding = SwingBindings.createJTableBinding( AutoBinding.UpdateStrategy.READ, registeredPlayers, registeredPlayersTable, "registeredPlayersBinding");
        JTableBinding.ColumnBinding columnBinding;
        columnBinding = jTableBinding.addColumnBinding(ELProperty.create("${id.fullName}"));
        columnBinding.setColumnName("Name");
        columnBinding.setColumnClass( String.class );
        /*columnBinding = jTableBinding.addColumnBinding(ELProperty.create("${id.rating}"));
        columnBinding.setColumnName("Rating");
        columnBinding.setColumnClass( Rating.class );*/
        columnBinding = jTableBinding.addColumnBinding(ELProperty.create("${rank}"));
        columnBinding.setColumnName("Rank");
        columnBinding.setColumnClass( Rank.class );
        // bindingGroup.addBinding(jTableBinding); // Don't think we need this.
        jTableBinding.bind();        
        //
        
        TableRowSorter trsRegisteredPlayers = new TableRowSorter( registeredPlayersTable.getModel() );
        // TODO - EEK! Hardcoded column numbers. For shame... Find these columns by name perhaps.
        registeredPlayersTable.setRowSorter( trsRegisteredPlayers );
        
        RowSorter.SortKey defaultSort = new RowSorter.SortKey( 1 , SortOrder.DESCENDING );
        trsRegisteredPlayers.setSortKeys( Arrays.asList( defaultSort ) );
        //
        
        // Set up selection listener for table.
        registeredPlayersTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                registeredPlayerSelectionChange();
            }
        });        
        //
        
        DefaultListModel listModel = new DefaultListModel();
        listModel.removeAllElements();
        jList1.setModel( listModel );
        
        /*
        // Set up ratings list binding.
        availablePlayers = ObservableCollections.observableList( new ArrayList<PlayerId>() );
        JListBinding listBinding = SwingBindings.createJListBinding( AutoBinding.UpdateStrategy.READ, availablePlayers, jList1, "availablePlayersBinding");
        listBinding.setDetailBinding( BeanProperty.create("summary") );
        listBinding.bind();
        //
        */
        availablePlayers = new ArrayList<>();
        
        
        // Disable filter until ratingslist is loaded.
        searchText.setEnabled( false );
        jLabel9.setEnabled( false );
        
        // Set up document listener on search text.
        searchText.getDocument().addDocumentListener( new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                // Plaintext textfield will not fire this.
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                filterRatingsList();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterRatingsList();
            }
        });
        //
        
    }
    
    
    private void registeredPlayerSelectionChange() {
        int selectedRow = registeredPlayersTable.getSelectedRow();
        if ( selectedRow < 0 ) {
            resetPlayerDetails( false );
        } else {
            setState( PlayersManagerState.EDITING );
            selectedPlayer = registeredPlayers.get( registeredPlayersTable.convertRowIndexToModel( selectedRow ) );
            jList1.clearSelection();
            updatePlayerDetails();
        }
        
    }
    
    private void ratingsListSelectionChanged() {
        int selectedRow = jList1.getSelectedIndex();
        
        if ( selectedRow < 0 ) {
            resetPlayerDetails( false );
            return;
        } else {
            setState( PlayersManagerState.NEW );
            
            PlayerId newPlayerId = availablePlayers.get( selectedRow );
            selectedPlayer = new Player( newPlayerId );
            registeredPlayersTable.clearSelection();
            updatePlayerDetails();
        }
    }
    
    private void resetPlayerDetails() {
        resetPlayerDetails( true );
    }
    private void resetPlayerDetails( Boolean clearSelections ) {
        if (clearSelections) {
            jList1.clearSelection();
            registeredPlayersTable.clearSelection();
        }
        
        selectedPlayer = new Player();
        setState( PlayersManagerState.NEW );
        updatePlayerDetails();
    }
    
    // Update fields based on selectedPlayer object.
    private void updatePlayerDetails() {
        PlayerId selectedId = selectedPlayer.getId();
        
        tfName.setText( selectedId.getName().toUpperCase() );
        tfFirstName.setText( selectedId.getFirstName() );
        tfAgaNo.setText( Integer.toString( selectedId.getAGANo() ) );
        tfExpiration.setText( selectedId.getExpirationString() );
        if ( selectedPlayer.getId().getExpiration().before( new Date() ) )
            tfExpiration.setBackground( new Color(1.0f, 0.5f, 0.5f) );
        else
            tfExpiration.setBackground( tfRating.getBackground());
        
        tfRank.setText( selectedPlayer.getRank().toString() );
        tfRating.setText( selectedId.getRating().toString() );
        
        
        jPanel1.removeAll();
        participationCheckBoxes = new JCheckBox[ tournament.getPairingProps().getNumberOfRounds() ];
        for (int i = 0; i < participationCheckBoxes.length; i++) {
            participationCheckBoxes[i] = new JCheckBox();
            participationCheckBoxes[i].setText("" + (i + 1));
            participationCheckBoxes[i].setSelected( !( selectedPlayer.getParticipation(i) == Participation.ABSENT ) );
            participationCheckBoxes[i].setActionCommand( Integer.toString( i ) );
            
            
            //tabCkbParticipation[i].setFont(new Font("Default", Font.PLAIN, 9));
            jPanel1.add(participationCheckBoxes[i]);
            //participationCheckBoxes[i].setBounds((i % 5) * 36 + 4, (i / 5) * 20 + 20, 36, 15);
        }
        jPanel1.revalidate();
        
    }
    
    private void loadRatingsList() {
        try {
            String strDefaultURL = "https://usgo.org/mm/tdlista.txt";
            File fDefaultFile = new File( Eggplant.rootFolder, "tdlista.txt" );
            String str = JOptionPane.showInputDialog("Download AGA tdlista from:", strDefaultURL);
            
            // User canceled
            if ( str == null )
                return;
            
            // Attempt download
            download( downloadProgress, strDefaultURL, fDefaultFile);
            loadLocalFile();
            
            // TODO - fix the async download
            //downloadBackground( strDefaultURL, fDefaultFile);
            //
        } catch (MalformedURLException ex) {
            JOptionPane.showMessageDialog(Eggplant.getInstance().getMainWindow(), "Malformed URL\nRating list could not be loaded", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(Eggplant.getInstance().getMainWindow(), "Unreachable file\nRating list could not be loaded", "Message", JOptionPane.ERROR_MESSAGE);
            return;
        }

    }
    private void loadLocalFile() {
        File localFile = new File(Eggplant.rootFolder, "tdlista.txt" );
        if ( !localFile.exists() ) {
            JOptionPane.showMessageDialog(Eggplant.getInstance().getMainWindow(), "Ratings list not loaded. Unable to find 'tdlista.txt' in application folder.", "Message", JOptionPane.ERROR_MESSAGE);
        }  else {
            loadLocalFile( localFile );
        }
    }
    
    private void loadLocalFile( File file ) {
        ratingsList = new RatingsList( file );
        
        // Enable searching now we have a ratingslist object.
        searchText.setEnabled( true );
        searchText.setText("");
        jLabel9.setEnabled( true );

        availablePlayers.clear();
        availablePlayers.addAll( ratingsList.getPlayersAvailable() );
        
        syncList();
        
    }
    
    private void syncList() {
        
        // Replace contents of list with availablePlayers.
        // NOTE: Simply repopulating the existing listModel will cause a 
        // selection change event to fire for every call to addElement. This can
        // cause the app to become unresponsive, so we avoid this by simply
        // creating a new listModel.
        DefaultListModel listModel = new DefaultListModel();
        
        //DefaultListModel listModel = (DefaultListModel) jList1.getModel();
        //listModel.clear();
        
        for ( PlayerId pId : availablePlayers ) {
            listModel.addElement( pId.getSummary() );
        }
        
        jList1.setModel( listModel );
    }
    
    /*
    private void downloadBackground( String strURL, File destination) throws MalformedURLException, IOException {
        String strFooRL = "http://www.optay.com/dump/MaximizeTest.zip";
         
        JFrame rootFrame = Eggplant.getInstance().base;
        
        URL url;
        URLConnection urlc;
        BufferedInputStream input;
        final FileOutputStream output;

        final ProgressMonitorInputStream progressMonitor;
                
        try {
            url = new URL( strFooRL );
            urlc = url.openConnection();


            input = new BufferedInputStream( urlc.getInputStream() );
            output = new FileOutputStream( destination );

        } catch ( IOException ex ) {
            return;
        }
        
        progressMonitor = new ProgressMonitorInputStream( rootFrame, "Downloading tdlista...",  input );
        progressMonitor.getProgressMonitor().setMaximum( urlc.getContentLength() );
        progressMonitor.getProgressMonitor().setMillisToDecideToPopup( 0 );
        progressMonitor.getProgressMonitor().setMillisToPopup( 0 );
                
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            
            @Override
            protected Void doInBackground() throws Exception {

                int i;
                
                while ((i = progressMonitor.read()) != -1) {
                    output.write(i);
                    
                }
                output.close();
                progressMonitor.close();
                
                return null;
            }
            
            @Override
            public void done() {
                loadLocalFile();
            }
        };
        worker.execute();
    }
    * 
    */
    
    
    private void download( JProgressBar progress, String strURL, File destination) throws MalformedURLException, IOException {
        BufferedInputStream input;
        FileOutputStream output;
        
        URL url = new URL(strURL);
        URLConnection urlc = url.openConnection();

        // input = new BufferedInputStream( urlc.getInputStream() );
        
        ProgressMonitorInputStream progressMonitor = new ProgressMonitorInputStream( this, "Downloading tdlista...",  new BufferedInputStream(  urlc.getInputStream() ));
        input = new BufferedInputStream( progressMonitor );
        output = new FileOutputStream(destination);
        
        progressMonitor.getProgressMonitor().setMillisToDecideToPopup(0);
        progressMonitor.getProgressMonitor().setMillisToPopup(0);
        
        
        int i;
        int contentLength = urlc.getContentLength();
        int nbChars = 0;
        
        
 
        if (progress != null) {
            if ( contentLength > 0 ) {
                progress.setValue(0);
            } else {
                progress.setIndeterminate(true);
            }
        }
        
        Logger.getLogger( PlayersManager.class.getName() ).log( Level.INFO, Long.toString( contentLength ) );

        while ((i = input.read()) != -1) {
            output.write(i);
            /*
            nbChars++;
            if (nbChars % 2000 == 0) {
                int percent = (int) ( 100 * nbChars / contentLength );
                if (progress != null) {
                    progress.setValue( percent );
                    progress.paintImmediately(0, 0, progress.getWidth(), progress.getHeight());
                }
            }
            * 
            */
        }

        if (progress != null) {
            progress.setValue( 0 );
            progress.setIndeterminate(false);
        }

        output.close();
        input.close();
    }    
    
    private void addPlayer() {
        
        boolean playerIsValid = validateSelectedPlayer();
        
        if (playerIsValid) {
            addValidatedPlayer();
        }
    }
    
    // TODO - validate other fields
    // Name must exist.
    // Validate field values before updating player object.
    private boolean validateSelectedPlayer() {
        boolean isValid = true;
        String errorMessage = "";
        
        // Make sure participation has not been changed for a player already involved in a round:
        for ( int round = 0; round<participationCheckBoxes.length; round++ ) {
            if ( !participationCheckBoxes[ round ].isSelected() && tournament.playerInvolvedInRound( selectedPlayer, round ) ) {
                //participationCheckBoxes[ round ].setSelected( !( selectedPlayer.getParticipation( round ) == Participation.ABSENT ) );
                errorMessage = errorMessage.concat("Unable to remove player from round " + Integer.toString(round + 1) + ". Player is the bye player or is paired for that round.\n" );
                isValid = false;
            }
        }
        //
        
        // Make sure player has unique AGA number:
        Player conflictPlayer = tournament.getPlayerByAGA( tfAgaNo.getText() );
        if ( (conflictPlayer != null) && (conflictPlayer != selectedPlayer) ) {
            errorMessage = errorMessage.concat("Another player with the given AGA number is already registered.\n" );
            isValid = false;
        }
        //
        
        
        if ( !isValid ) {
            JOptionPane.showMessageDialog( Eggplant.getInstance().getMainWindow(), errorMessage, "Alert", JOptionPane.INFORMATION_MESSAGE );
        }
        
        return isValid;
    }
    
    // Transfer field values to player object.
    private void addValidatedPlayer() {
        selectedPlayer.setRank( tfRank.getText() );
        selectedPlayer.getId().setName( tfName.getText() );
        selectedPlayer.getId().setFirstName( tfFirstName.getText() );
        selectedPlayer.getId().setAGANo( tfAgaNo.getText() );
        
        // Update participation only for rounds player is not involved in.
        // Player may already be paired so should not be set back to NOT_ASSIGNED.
        for ( int round = 0; round<participationCheckBoxes.length; round++ ) {
            if ( !tournament.playerInvolvedInRound( selectedPlayer, round ) ) {
                if ( participationCheckBoxes[round].isSelected() ) {
                    selectedPlayer.setParticipation( round, Participation.NOT_ASSIGNED );
                } else {
                    selectedPlayer.setParticipation( round, Participation.ABSENT );
                }
            }
        }
        
        if ( state == PlayersManagerState.NEW ) {
            // This is a redundant validation.
            if ( tournament.addPlayer(selectedPlayer) ) {
                // It worked, sync the observable list for the players table.
                registeredPlayers.add( selectedPlayer );
                resetPlayerDetails();
                updateRegisteredCount();
            } else {
                // It didn't work: report the problem, leave form as is.
                JOptionPane.showMessageDialog( Eggplant.getInstance().getMainWindow(), "Unable to add player.", "Alert", JOptionPane.INFORMATION_MESSAGE );
            }
        } else
        {
            // We've already validated, before calling this method, so we assume we're good.
            // Sync the view and clear the form.
            updateControls();
            tournament.setChangedSinceLastSave(true);
        }
        
    }
    
    private void removePlayer() {
        if ( tournament.removePlayer( selectedPlayer ) ) {
            // It worked, sync the observable list for the players table.
            registeredPlayers.remove( selectedPlayer );
            resetPlayerDetails();
            updateRegisteredCount();
        } else {
            JOptionPane.showMessageDialog( Eggplant.getInstance().getMainWindow(), "Player cannot be removed. Player is involved in a game or is the bye player for a round.", "Message", JOptionPane.ERROR_MESSAGE );
        }
        
    }
    
    private void setState( PlayersManagerState newState ) {
        state = newState;
        switch(state) {
            case NEW:
                removePlayerButton.setEnabled(false);
                addPlayerButton.setText("Add");
                break;
            case EDITING:
                removePlayerButton.setEnabled(true);
                addPlayerButton.setText("Save");
                break;
        }
    }
    
    private void updateRegisteredCount() {
        TitledBorder titledBorder = (TitledBorder) jPanel3.getBorder();
        titledBorder.setTitle("Registered Players (" + Integer.toString( registeredPlayers.size() ) + ")");
        jPanel3.repaint();
        
        //Logger.getLogger( PlayersManager.class.getName() ).log( Level.INFO, Integer.toString( registeredPlayers.size() ) );
        
    }
    
    @Override
    protected void updateControls() {
        
        if ( tournament != null ) {
            registeredPlayers.clear();
            //registeredPlayers.addAll( ObservableCollections.observableList( tournament.getRegisteredPlayers() ) );
            registeredPlayers.addAll( tournament.getRegisteredPlayers() );
        }
        
        updateRegisteredCount();
        resetPlayerDetails();
    }
    
    private void filterRatingsList() {
        if ( ratingsList == null ) {
            return;
        }
        
        availablePlayers.clear();
        availablePlayers.addAll( ratingsList.getPlayersAvailable( searchText.getText() ) );
        
        syncList();
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
        jButton1 = new javax.swing.JButton();
        searchText = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        downloadProgress = new javax.swing.JProgressBar();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        addPlayerButton = new javax.swing.JButton();
        removePlayerButton = new javax.swing.JButton();
        clearFormButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        tfName = new javax.swing.JTextField();
        tfAgaNo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tfExpiration = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tfRating = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        tfRank = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        rankWeakerButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        rankResetButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        rankStrongerButton = new javax.swing.JButton();
        tfFirstName = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        registeredPlayersTable = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Ratings List", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jButton1.setText("Download...");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jButton3.setText("Load Local");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Browse...");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Filter");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(searchText))
                    .addComponent(downloadProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addGap(0, 62, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(downloadProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(8, 8, 8)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Player"));
        jPanel4.setFocusTraversalPolicyProvider(true);

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("Participation");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setFocusable(false);
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        addPlayerButton.setText("Add");
        addPlayerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPlayerButtonActionPerformed(evt);
            }
        });

        removePlayerButton.setText("Remove");
        removePlayerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removePlayerButtonActionPerformed(evt);
            }
        });

        clearFormButton.setText("Clear Form");
        clearFormButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearFormButtonActionPerformed(evt);
            }
        });

        tfName.setText("jTextField3");

        tfAgaNo.setText("jTextField7");

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Rating");

        tfExpiration.setEditable(false);
        tfExpiration.setText("jTextField8");
        tfExpiration.setFocusable(false);

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Rank");

        tfRating.setEditable(false);
        tfRating.setText("jTextField5");
        tfRating.setFocusable(false);

        jLabel5.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("AGA No");

        tfRank.setText("jTextField7");

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Expiration");

        rankWeakerButton.setText("-");
        rankWeakerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rankWeakerButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Name");

        rankResetButton.setText("=");
        rankResetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rankResetButtonActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("First Name");

        rankStrongerButton.setText("+");
        rankStrongerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rankStrongerButtonActionPerformed(evt);
            }
        });

        tfFirstName.setText("jTextField4");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfName, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tfRating)
                            .addComponent(tfRank, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(rankWeakerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(63, 63, 63)
                                .addComponent(rankStrongerButton))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(53, 53, 53)
                                .addComponent(rankResetButton))))
                    .addComponent(tfAgaNo, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfExpiration, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel1))
                    .addComponent(tfName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(tfFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfAgaNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfExpiration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfRating, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfRank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(rankWeakerButton)
                        .addComponent(rankStrongerButton)
                        .addComponent(rankResetButton)))
                .addGap(49, 49, 49))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 348, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(addPlayerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removePlayerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearFormButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(removePlayerButton)
                                .addComponent(addPlayerButton)
                                .addComponent(clearFormButton)))
                        .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Registered Players (0)"));

        registeredPlayersTable.setModel(new javax.swing.table.DefaultTableModel(
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
        registeredPlayersTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        registeredPlayersTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(registeredPlayersTable);
        registeredPlayersTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        registeredPlayersTable.getColumnModel().getColumn(1).setPreferredWidth(15);

        jButton2.setText("Print...");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
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
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void clearFormButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearFormButtonActionPerformed
        
        resetPlayerDetails();
        
    }//GEN-LAST:event_clearFormButtonActionPerformed

    private void removePlayerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removePlayerButtonActionPerformed
        removePlayer();
    }//GEN-LAST:event_removePlayerButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        loadRatingsList();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged
        
        ratingsListSelectionChanged();            

    }//GEN-LAST:event_jList1ValueChanged

    private void addPlayerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPlayerButtonActionPerformed
        addPlayer();
    }//GEN-LAST:event_addPlayerButtonActionPerformed

    private void rankStrongerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rankStrongerButtonActionPerformed
        Rank currentRank = new Rank( tfRank.getText() );
        currentRank.adjust( 1 );
        tfRank.setText( currentRank.toString() );
    }//GEN-LAST:event_rankStrongerButtonActionPerformed

    private void rankWeakerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rankWeakerButtonActionPerformed
        Rank currentRank = new Rank( tfRank.getText() );
        currentRank.adjust( -1 );
        tfRank.setText( currentRank.toString() );
    }//GEN-LAST:event_rankWeakerButtonActionPerformed

    private void rankResetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rankResetButtonActionPerformed
        Rank currentRank = new Rank( Rating.CreateRatingFromAGAString( tfRating.getText() ) );
        tfRank.setText( currentRank.toString() );
    }//GEN-LAST:event_rankResetButtonActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // Create a copy of the table and size the columns for printing:
        CustomPrintableTable printTable = new CustomPrintableTable( registeredPlayersTable.getModel() );
        JScrollPane scroll = new JScrollPane( printTable );
        
        // Inherit sort
        TableRowSorter printSorter = new TableRowSorter( printTable.getModel() );
        printTable.setRowSorter( printSorter );
        printSorter.setSortKeys( registeredPlayersTable.getRowSorter().getSortKeys() );
        //
        
        
        printTable.getColumnModel().getColumn(0).setPreferredWidth( 400 );
        printTable.getColumnModel().getColumn(1).setPreferredWidth( 68 );
        
        scroll.setVisible( false );
        this.add(scroll);
        scroll.validate();  // Not sure why this is required here as scroll is validated by the custom printable...
        
        MessageFormat header = new  MessageFormat( tournament.getPrintHeadingString("Player List") );
        try {
            printTable.print( JTable.PrintMode.FIT_WIDTH, header, null );
            //tblGames.print( JTable.PrintMode.FIT_WIDTH, header, null );
            
        } catch ( PrinterException ex ) {
            Logger.getLogger( PlayersManager.class.getName() ).log( Level.SEVERE, "Unable to print.\n" + ex );
            JOptionPane.showMessageDialog(Eggplant.getInstance().getMainWindow(), "Unable to print.", "Error", JOptionPane.ERROR_MESSAGE );
        }
        
        this.remove( scroll );
        this.repaint();
                
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // Just load the local copy of TDListA
        loadLocalFile();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        JFileChooser fileChoice = new JFileChooser( Eggplant.rootFolder );
        fileChoice.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChoice.showOpenDialog(Eggplant.getInstance().getMainWindow());
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        } else {
            loadLocalFile( fileChoice.getSelectedFile() );
        }
        
    }//GEN-LAST:event_jButton4ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addPlayerButton;
    private javax.swing.JButton clearFormButton;
    private javax.swing.JProgressBar downloadProgress;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton rankResetButton;
    private javax.swing.JButton rankStrongerButton;
    private javax.swing.JButton rankWeakerButton;
    private javax.swing.JTable registeredPlayersTable;
    private javax.swing.JButton removePlayerButton;
    private javax.swing.JTextField searchText;
    private javax.swing.JTextField tfAgaNo;
    private javax.swing.JTextField tfExpiration;
    private javax.swing.JTextField tfFirstName;
    private javax.swing.JTextField tfName;
    private javax.swing.JTextField tfRank;
    private javax.swing.JTextField tfRating;
    // End of variables declaration//GEN-END:variables


}
