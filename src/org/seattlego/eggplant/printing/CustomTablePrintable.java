package org.seattlego.eggplant.printing;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 * 
 * Replacement TablePrintable that supports multi-line headers and automatically
 * resizes table to printable width for consistent appearance. The tables that
 * use this should not be visible on-screen as printing will change their width
 * which could interfere with UI layout.
 * 
 * Includes some sample code from stackoverflow:
 * 
 * http://stackoverflow.com/questions/6144775/messageformat-header-footerformat-how-to-change-font-for-jtable-printing
 * http://stackoverflow.com/questions/11956254/how-to-print-multiple-header-lines-with-messageformat-using-a-jtable
 * @author Topsy
 */
public class CustomTablePrintable implements Printable {

    private final float FONT_SIZE = 14f;
    
    private JTable table;
    private MessageFormat headerFormat; 
    private MessageFormat footerFormat;
    
    /** For quick reference to the table's header. */
    private JTableHeader header;

    /** For quick reference to the table's column model. */
    private TableColumnModel colModel;

    /** To save multiple calculations of total column width. */
    private int totalColWidth;

    /** The most recent page index asked to print. */
    private int last = -1;

    /** The next row to print. */
    private int row = 0;

    /** The next column to print. */
    private int col = 0;

    /** Used to store an area of the table to be printed. */
    private final Rectangle clip = new Rectangle(0, 0, 0, 0);

    /** Used to store an area of the table's header to be printed. */
    private final Rectangle hclip = new Rectangle(0, 0, 0, 0);

    /** Saves the creation of multiple rectangles. */
    private final Rectangle tempRect = new Rectangle(0, 0, 0, 0);

    /** Vertical space to leave between table and header/footer text. */
    private static final int H_F_SPACE = 8;

    /** Font size for the header text. */
    private static final float HEADER_FONT_SIZE = 14.0f;

    /** Font size for the footer text. */
    private static final float FOOTER_FONT_SIZE = 12.0f;

    /** The font to use in rendering header text. */
    private Font headerFont;

    /** The font to use in rendering footer text. */
    private Font footerFont;
    
    public CustomTablePrintable ( JTable table,
                                  MessageFormat headerFormat,
                                  MessageFormat footerFormat ) {

        this.table = table;
        header = table.getTableHeader();
        colModel = table.getColumnModel();

        if (headerFormat != null) {
            // the header clip height can be set once since it's unchanging
            hclip.height = header.getHeight();
        }

        this.headerFormat = headerFormat;
        this.footerFormat = footerFormat;

        // derive the header and footer font from the table's font
        headerFont = table.getFont().deriveFont(Font.BOLD, HEADER_FONT_SIZE);
        footerFont = table.getFont().deriveFont(Font.PLAIN, FOOTER_FONT_SIZE);        
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, 
            int pageIndex) throws PrinterException {
        
        // 1. RESIZE TABLE
        
        // for easy access to these values
        final int imgWidth = (int)pageFormat.getImageableWidth();
        final int imgHeight = (int)pageFormat.getImageableHeight();

        if (imgWidth <= 0) {
            throw new PrinterException("Width of printable area is too small.");
        }
        
        if ( table.getParent().getBounds().getWidth() != imgWidth ) {
            table.getParent().setBounds( 0, 0, imgWidth, imgHeight );
            table.getParent().validate();
            table.doLayout();

            totalColWidth = colModel.getTotalColumnWidth();
        }
        
        
        // 2. PREPARE HEADER AND FOOTER
        
        // to pass the page number when formatting the header and footer text
        Object[] pageNumber = new Object[]{Integer.valueOf(pageIndex + 1)};

        // fetch the formatted header text, if any
        String headerText = null;
        if (headerFormat != null) {
            headerText = headerFormat.format(pageNumber);
        }

        // fetch the formatted footer text, if any
        String footerText = null;
        if (footerFormat != null) {
            footerText = footerFormat.format(pageNumber);
        }

        // the amount of vertical space needed for the header and footer text
        int headerTextSpace = 0;
        int footerTextSpace = 0;

        // the amount of vertical space available for printing the table
        int availableSpace = imgHeight;

        headerTextSpace = calculateFormatHeight( headerFormat, headerFont, (Graphics2D) graphics, pageIndex );
        availableSpace -= headerTextSpace + H_F_SPACE;

        footerTextSpace = calculateFormatHeight( footerFormat, footerFont, (Graphics2D) graphics, pageIndex );
        availableSpace -= footerTextSpace + H_F_SPACE;

        if (availableSpace <= 0) {
            throw new PrinterException("Height of printable area is too small.");
        }
        
        
        // 3. GET THE NEXT CLIP
        
        // This is in a loop for two reasons:
        // First, it allows us to catch up in case we're called starting
        // with a non-zero pageIndex. Second, we know that we can be called
        // for the same page multiple times. The condition of this while
        // loop acts as a check, ensuring that we don't attempt to do the
        // calculations again when we are called subsequent times for the
        // same page.
        while (last < pageIndex) {
            // if we are finished all columns in all rows
            if (row >= table.getRowCount() && col == 0) {
                return NO_SUCH_PAGE;
            }

            // calculate the area of the table to be printed for this page
            findNextClip(imgWidth, (availableSpace - hclip.height) );

            last++;
        }
        
        
        // 4. DRAW ELEMENTS TO GRAPHICS
        
        // grab an untainted graphics
        Graphics2D g2d = (Graphics2D)graphics.create();

        
        // translate into the co-ordinate system of the pageFormat
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        // to save and store the transform
        AffineTransform oldTrans;

        // if there's footer text, print it at the bottom of the imageable area
        if (footerText != null) {
            oldTrans = g2d.getTransform();

            g2d.translate(0, imgHeight - footerTextSpace);
            printText( g2d, footerText, footerFont, imgWidth);

            g2d.setTransform(oldTrans);
        }

        // if there's header text, print it at the top of the imageable area
        // and then translate downwards
        if (headerText != null) {
            printText( g2d, headerText, headerFont, imgWidth);

            g2d.translate(0, headerTextSpace + H_F_SPACE);
        }        
        
        
        
        // constrain the table output to the available space
        tempRect.x = 0;
        tempRect.y = 0;
        tempRect.width = imgWidth;
        tempRect.height = availableSpace;
        g2d.clip(tempRect);

        // otherwise, ensure that the current portion of the table is
        // centered horizontally
        int diff = (imgWidth - clip.width) / 2;
        g2d.translate(diff, 0);

        // store the old transform and clip for later restoration
        oldTrans = g2d.getTransform();
        Shape oldClip = g2d.getClip();

        // if there's a table header, print the current section and
        // then translate downwards
        if (header != null) {
            hclip.x = clip.x;
            hclip.width = clip.width;

            g2d.translate(-hclip.x, 0);
            g2d.clip(hclip);
            header.print(g2d);

            // restore the original transform and clip
            g2d.setTransform(oldTrans);
            g2d.setClip(oldClip);

            // translate downwards
            g2d.translate(0, hclip.height);
        }

        // print the current section of the table
        g2d.translate(-clip.x, -clip.y);
        g2d.clip(clip);
        table.print(g2d);

        // restore the original transform and clip
        g2d.setTransform(oldTrans);
        g2d.setClip(oldClip);

        // draw a box around the table
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, clip.width, hclip.height + clip.height);

        // dispose the graphics copy
        g2d.dispose();

        return PAGE_EXISTS;
        
    }

    /*
     * Returns height of multi-line text-format (header or footer)
     */
    protected int calculateFormatHeight( MessageFormat format, Font font, Graphics2D g, int pageIndex ) {
        if ( format == null ) return 0;
        Object[] pageNumber = new Object[]{new Integer(pageIndex + 1)};
        String text = format.format(pageNumber);
        g.setFont( font );
        Rectangle2D rect = g.getFontMetrics().getStringBounds(text, g);
        int numberOfLines = text.split("\n").length;
        
        return (int) Math.ceil( rect.getHeight() * numberOfLines );
    }
    
    // Multi-line text draw
    private void printText(Graphics2D g, String text, Font font, int imgWidth) {
        String[] lines = text.split("\n");
        
        g.setColor(Color.BLACK);
        g.setFont(font);
        

        for (int i = 0; i < lines.length; i++) {
            int tx;
            Rectangle2D rect = g.getFontMetrics().getStringBounds( lines[i], g );
            
            // if the text is small enough to fit, center it
            if (rect.getWidth() < imgWidth) {
                tx = (int) (imgWidth / 2 - rect.getWidth() / 2);

                // otherwise, if the table is LTR, ensure the left side of
                // the text shows; the right can be clipped
            } else if (table.getComponentOrientation().isLeftToRight()) {
                tx = 0;

                // otherwise, ensure the right side of the text shows
            } else {
                tx = -(int) (Math.ceil(rect.getWidth()) - imgWidth);
            }

            int ty = (int) Math.ceil( Math.abs(rect.getY()) + i * rect.getHeight() );
            g.drawString(lines[i], tx, ty);
        }
    }
    
    /**
     * Calculate the area of the table to be printed for
     * the next page. This should only be called if there
     * are rows and columns left to print.
     *
     * To avoid an infinite loop in printing, this will
     * always put at least one cell on each page.
     *
     * @param  pw  the width of the area to print in
     * @param  ph  the height of the area to print in
     */
    private void findNextClip(int pw, int ph) {
        final boolean ltr = table.getComponentOrientation().isLeftToRight();

        // if we're ready to start a new set of rows
        if (col == 0) {
            if (ltr) {
                // adjust clip to the left of the first column
                clip.x = 0;
            } else {
                // adjust clip to the right of the first column
                clip.x = totalColWidth;
            }

            // adjust clip to the top of the next set of rows
            clip.y += clip.height;

            // adjust clip width and height to be zero
            clip.width = 0;
            clip.height = 0;

            // fit as many rows as possible, and at least one
            int rowCount = table.getRowCount();
            int rowHeight = table.getRowHeight(row);
            do {
                clip.height += rowHeight;

                if (++row >= rowCount) {
                    break;
                }

                rowHeight = table.getRowHeight(row);
            } while (clip.height + rowHeight <= ph);
        }
        
        // Since we are sizing the width of the table to fit, we assume all
        // columns will fit.
        clip.x = 0;
        clip.width = totalColWidth;
        return;
    }    
}
