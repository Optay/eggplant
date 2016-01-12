/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.seattlego.eggplant;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.seattlego.eggplant.forms.MainWindow;
import org.seattlego.eggplant.model.Tournament;

/**
 *
 * @author Topsy
 */
public class Eggplant {

    private static FileHandler logFileHandler;
    private static final String logFilename = "log.txt";
    
    public static File rootFolder;
    public static File lastSaveFolder;
    public static File lastSaveFile;
    public static File lastExportFolder;
    
    public static String version;
    
    private MainWindow mainWindow;
    
    private Tournament tournament;
    
    private static Eggplant instance;
    
    public static Eggplant getInstance() {
        return instance;
    }
    
    private Eggplant() {
    }
    private void init() {
        
        Eggplant.rootFolder = new File(System.getProperty("user.dir"));
        Eggplant.lastSaveFolder = Eggplant.rootFolder;
        Eggplant.lastSaveFile = null;
        Eggplant.lastExportFolder = Eggplant.rootFolder;
        
        configureLogging();
        
        // Put version string in easily accessible static variable
        Package pack = Eggplant.class.getPackage();
        if ( ( pack == null )  || ( pack.getImplementationVersion() == null ) ) {
            Eggplant.version = "test build";
        } else {
            Eggplant.version = pack.getImplementationVersion();
        }        
        
        mainWindow = new MainWindow();
        mainWindow.setVisible(true);
        
        // New tournament!
        openTournament();
    }
    
    public void openTournament() {
        openTournament(null);
    }
    public void openTournament( Tournament t ) {
        if ( t == null )
            tournament = new Tournament();
        else
            tournament = t;
        
        mainWindow.setTournament( tournament );
        
        // Ensure new tournament does not prompt for save as initialization will
        // flag it as changed.
        tournament.setChangedSinceLastSave( false );
    }
    
    /**
     * 
     * @return Reference to main window for dialog placement.
     */
    public MainWindow getMainWindow() {
        return mainWindow;
    }
    
    
    
    
    
    private void configureLogging() {
        File logFile = new File( rootFolder, logFilename );
        Logger rootLogger = Logger.getLogger("");
        try {
            logFileHandler = new FileHandler( logFile.getAbsolutePath() );
            
            SimpleFormatter formatter = new SimpleFormatter();
            logFileHandler.setFormatter( formatter );
            
            rootLogger.addHandler( logFileHandler );
        } catch ( IOException | SecurityException ex ) {
            Logger.getLogger( Eggplant.class.getName()  ).log( Level.WARNING, "Unable to add log file handler.");
        }
        
        // TODO - conditional build-type thingy this.
        //rootLogger.setLevel( Level.SEVERE );
        rootLogger.setLevel( Level.INFO );
        
    }
    
    
    
    
    
    
    
    
    /**
     * 
     * Create a new application instance.
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                Eggplant.instance = new Eggplant();
                Eggplant.instance.init();
            }
        });
    }



}
