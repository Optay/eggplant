/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.seattlego.eggplant.forms;

import javax.swing.JPanel;
import org.seattlego.eggplant.interfaces.ITournament;

/**
 *
 * @author Topsy
 */
abstract public class EggplantForm extends JPanel {
    
    protected ITournament tournament;
    
    protected RoundPane roundPane;
    
    
    public void setTournament( ITournament tourney ) {
        tournament = tourney;
        if ( roundPane != null ) {
            roundPane.setTournament( tourney );
        }
        
        updateControls();
    }
    
    protected void initRoundPane() {
        roundPane = new RoundPane();
        roundPane.addChangeListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectedRoundChanged();
            }
        });
    }
    /*
     * Code to transfer toolTips from controls to labels for those controls.
     * ToolTips have been moved to the labels, so it is not used.
     * 
    protected void propagateToolTips() {
        toolTipWalk(this);
    }
    
    private void toolTipWalk( JComponent comp ) {
        for ( Component child : comp.getComponents() ) {
            if ( child instanceof JLabel ) {
                JLabel label = (JLabel) child;
                if ( label.getLabelFor() != null ) {
                    label.setToolTipText( ((JComponent)label.getLabelFor()).getToolTipText() );
                }
            }
            if ( child instanceof JComponent ) {
                 toolTipWalk( (JComponent) child );
            }
        }
    }
    * 
    */
        
    protected void selectedRoundChanged() {
        
    }
    
    
    protected void updateControls() {
        
    }
    
}
