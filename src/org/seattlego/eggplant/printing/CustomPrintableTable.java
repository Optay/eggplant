package org.seattlego.eggplant.printing;

import java.awt.print.Printable;
import java.text.MessageFormat;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * Terribly named...
 * @author Topsy
 */
public class CustomPrintableTable extends JTable {
    
    public CustomPrintableTable() {
        super();
    }
    public CustomPrintableTable( TableModel tableModel ) {
        super( tableModel );
    }
    public CustomPrintableTable( TableModel tableModel, TableColumnModel columnModel ) {
        super( tableModel, columnModel );
    }
    
    /**
     * @return CustomTablePrintable
    * @inherited <p>
    */
    @Override
    public Printable getPrintable(JTable.PrintMode printMode, MessageFormat headerFormat, MessageFormat footerFormat) {
        
        CustomTablePrintable custom = new CustomTablePrintable( this, headerFormat, footerFormat);
        return custom;
    }
    
}
