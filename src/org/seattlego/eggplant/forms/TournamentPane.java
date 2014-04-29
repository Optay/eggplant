/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.seattlego.eggplant.forms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.seattlego.eggplant.Eggplant;
import org.seattlego.eggplant.interfaces.ITournament;
import org.seattlego.eggplant.io.ReportGenerator;
import org.seattlego.eggplant.io.XmlEncoderAGA;

/**
 *
 * @author Topsy
 */
public class TournamentPane extends EggplantForm {

    /**
     * Creates new form TournamentPane
     */
    public TournamentPane() {
        initComponents();
        
        // Report version number
/*        ResourceBundle rb = ResourceBundle.getBundle("project.properties"); 
        Logger.getLogger( TournamentPane.class.getName()  ).log( Level.INFO, rb.getString("application.buildnumber") );
        labelText.setText( rb.getString("application.buildnumber") );*/
        //
        
        String version = "";
        Package pack = Eggplant.class.getPackage();
        if ( ( pack == null )  || ( pack.getImplementationVersion() == null ) ) {
            version = "version not available";
        } else {
            version = pack.getImplementationVersion();
        }
        labelText.setText( version );
        
    }

    @Override
    public void setTournament( ITournament tourney ) {
        
        tourney.addIdChangeListener( new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                updateControls();
            }
            
        } );
        
        
        super.setTournament( tourney );
    }
    
    
    private boolean saveTournament() {
        if ( Eggplant.lastSaveFile != null) {
            XmlEncoderAGA.TournamentToFile( tournament, Eggplant.lastSaveFile );
            return true;
        } else {
            return saveTournamentAs();
        }
    }
    private boolean saveTournamentAs() {
        JFileChooser fileChoice = new ConfirmingFileChooser( Eggplant.lastSaveFolder, "XML Tournament", "xml" );
        
        int result = fileChoice.showSaveDialog(Eggplant.getInstance().getMainWindow());
        if (result == JFileChooser.CANCEL_OPTION) {
            return false;
        }
        
        File selectedFile = fileChoice.getSelectedFile();
        
        XmlEncoderAGA.TournamentToFile( tournament, selectedFile );
        Eggplant.lastSaveFolder = selectedFile.getParentFile();
        Eggplant.lastSaveFile = selectedFile;
        
        return true;
    }

    private void openTournament() {
        if ( !saveCurrentTournament() ) return;
        
        JFileChooser fileChoice = new JFileChooser( Eggplant.lastSaveFolder );
        fileChoice.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter( "XML Tournament", "xml" );
        fileChoice.setFileFilter(filter);
        
        
        int result = fileChoice.showOpenDialog( Eggplant.getInstance().getMainWindow() );
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        
        File selectedFile = fileChoice.getSelectedFile();
        Eggplant.getInstance().openTournament( XmlEncoderAGA.TournamentFromFile( selectedFile ) );
        Eggplant.lastSaveFile = selectedFile;
        
        String report = XmlEncoderAGA.getLoadReport();
        if ( !report.equals("") ) {
            JOptionPane.showMessageDialog( Eggplant.getInstance().getMainWindow(), report, "Note", JOptionPane.INFORMATION_MESSAGE );
        }
    }
    
    private void exportReport() {
        JFileChooser fileChoice = new ConfirmingFileChooser( Eggplant.lastExportFolder, "Report", "txt" );
        
        int result = fileChoice.showSaveDialog(Eggplant.getInstance().getMainWindow());
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        
        File selectedFile = fileChoice.getSelectedFile();
        
        ReportGenerator.generateReport( tournament, selectedFile );
        Eggplant.lastExportFolder = selectedFile.getParentFile();
    }
    
    
    private void newTournament() {
        if ( !saveCurrentTournament() ) return;
        
        Eggplant.getInstance().openTournament();
        Eggplant.lastSaveFile = null;
    }
    
    @Override
    protected void updateControls() {
        jLabel2.setText( tournament.getProps().getName() );
        jLabel4.setText( tournament.getProps().getStartDateString() );
        jLabel3.setText( tournament.getProps().getEndDateString() );
    }
    
    /*
     * Prompts to save the current tournament if it has been modified.
     */
    public boolean saveCurrentTournament() {
        if ( tournament == null ) {
            return true;
        }
        if ( !tournament.getChangedSinceLastSave() ) {
            return true;
        }

        int response = JOptionPane.showConfirmDialog(Eggplant.getInstance().getMainWindow(), "Do you want to save the current tournament?", "Query", JOptionPane.YES_NO_CANCEL_OPTION);
        if (response == JOptionPane.CANCEL_OPTION) {
            return false;
        }
        if (response == JOptionPane.YES_OPTION) {
            return saveTournament();
        }
        
        return true;
    }    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton3 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        labelText = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(900, 100));

        jButton3.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jButton3.setText("New");
        jButton3.setMaximumSize(new java.awt.Dimension(83, 20));
        jButton3.setMinimumSize(new java.awt.Dimension(83, 20));
        jButton3.setPreferredSize(new java.awt.Dimension(83, 20));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jButton1.setText("Open");
        jButton1.setMaximumSize(new java.awt.Dimension(83, 20));
        jButton1.setMinimumSize(new java.awt.Dimension(83, 20));
        jButton1.setPreferredSize(new java.awt.Dimension(83, 20));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jButton2.setText("Save");
        jButton2.setMaximumSize(new java.awt.Dimension(83, 20));
        jButton2.setMinimumSize(new java.awt.Dimension(83, 20));
        jButton2.setPreferredSize(new java.awt.Dimension(83, 20));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jButton4.setText("Save as...");
        jButton4.setMaximumSize(new java.awt.Dimension(83, 20));
        jButton4.setMinimumSize(new java.awt.Dimension(83, 20));
        jButton4.setPreferredSize(new java.awt.Dimension(83, 20));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jButton6.setText("Export Report...");
        jButton6.setMaximumSize(new java.awt.Dimension(109, 20));
        jButton6.setMinimumSize(new java.awt.Dimension(109, 20));
        jButton6.setPreferredSize(new java.awt.Dimension(109, 20));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Tournament name");
        jLabel2.setAlignmentX(1.0F);
        jLabel2.setMaximumSize(new java.awt.Dimension(500, 16));
        jLabel2.setMinimumSize(new java.awt.Dimension(500, 16));
        jPanel1.add(jLabel2);

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("9999-99-99");
        jLabel3.setAlignmentX(1.0F);
        jLabel3.setMaximumSize(new java.awt.Dimension(500, 16));
        jLabel3.setMinimumSize(new java.awt.Dimension(500, 16));
        jPanel1.add(jLabel3);

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("9999-99-99");
        jLabel4.setAlignmentX(1.0F);
        jLabel4.setMaximumSize(new java.awt.Dimension(500, 16));
        jLabel4.setMinimumSize(new java.awt.Dimension(500, 16));
        jPanel1.add(jLabel4);

        labelText.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelText.setText("jLabel5");
        labelText.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/seattlego/eggplant/assets/logo.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelText, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 140, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(labelText))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        saveTournament();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        openTournament();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        exportReport();
        
        
        
        
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        newTournament();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        saveTournamentAs();
    }//GEN-LAST:event_jButton4ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel labelText;
    // End of variables declaration//GEN-END:variables



    private class ConfirmingFileChooser extends JFileChooser {

        private String extension;

        public ConfirmingFileChooser( File startingLocation ) {
            this( startingLocation, "", "" );
        }
        public ConfirmingFileChooser( File startingLocation, String filterDescription, String filterExtension ) {
            super( startingLocation );
            
            this.setFileSelectionMode(JFileChooser.FILES_ONLY);
            
            this.extension = filterExtension;
            if ( !filterExtension.equals("") ) {
                setFileFilter( new FileNameExtensionFilter( filterDescription, filterExtension ) );
            }
        }

        /*
         * Automatically append supplied extension if absent:
         */
        @Override
        public File getSelectedFile() {
            File selectedFile = super.getSelectedFile();

            if ( (selectedFile == null) || (extension.equals("")) ) return selectedFile;
            
            // Automatically add 'xml' extension if needed.
            String filename = selectedFile.getName();
            String[] filenameSplit = filename.split("\\.");

            if ( (filenameSplit.length==0) || (! filenameSplit[ filenameSplit.length - 1 ].equalsIgnoreCase( extension ) ) ) {
                filename = selectedFile.getAbsolutePath() + "." + extension;
                selectedFile = new File( filename );
            }
            //

            return selectedFile;
        }

        @Override public void approveSelection() {
            if (getDialogType() == SAVE_DIALOG) {
                File selectedFile = getSelectedFile();
                if ((selectedFile != null) && selectedFile.exists()) {
                    int response = JOptionPane.showConfirmDialog(Eggplant.getInstance().getMainWindow(),
                    "The file " + selectedFile.getName() + " already exists. Do you want to replace the existing file?",
                    "Query", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
                    
                    if (response != JOptionPane.YES_OPTION)
                        return;
                }
            }

            super.approveSelection();
        }
    }    

}
