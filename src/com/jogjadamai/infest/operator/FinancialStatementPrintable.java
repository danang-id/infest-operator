/*
 * Copyright 2017 Danang Galuh Tegar P.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jogjadamai.infest.operator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import java.awt.print.PrinterException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 * <h1>class <code>FinancialStatementPrintable</code></h1>
 * <p><code>FinancialStatementPrintable</code> printing object class to define
 * what to print when printing Infest Financial Statement.</p>
 * <br>
 * <p><b><i>Coded, built, and packaged with passion by Danang Galuh Tegar P for Infest.</i></b></p>
 * 
 * @author Danang Galuh Tegar P
 * @version 2017.03.10.0001
 */
public final class FinancialStatementPrintable implements Printable {

    private final JTable table;
    private final String date, totalIncome;
    
    public FinancialStatementPrintable(JTable table, String date, String totalIncome) {
        this.table = table;
        this.date = date;
        this.totalIncome = totalIncome;
    }
    
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setColor(Color.black); 
        
        int fontHeight = g2d.getFontMetrics().getHeight();
        int fontDescent = g2d.getFontMetrics().getDescent();

        // reserve spaces for page number 
        double pageHeight = pageFormat.getImageableHeight() - fontHeight - 100;
        double pageWidth = pageFormat.getImageableWidth() - 100;
        double tableWidth = (double) table.getColumnModel().getTotalColumnWidth();
        double scale = 1;
        if (tableWidth >= pageWidth) {
            scale = pageWidth / tableWidth;
        }
        double headerHeightOnPage = table.getTableHeader().getHeight() * scale;
        double tableWidthOnPage = tableWidth * scale;
        double oneRowHeight = (table.getRowHeight() + table.getRowMargin()) * scale;
        int numRowsOnAPage = (int)((pageHeight-headerHeightOnPage) / oneRowHeight);
        double pageHeightForTable = oneRowHeight * numRowsOnAPage;
        int totalNumPages = (int)Math.ceil(((double) table.getRowCount()) / numRowsOnAPage);
        if (pageIndex >= totalNumPages)
            return NO_SUCH_PAGE;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        g2d.drawString("Page: " + (pageIndex +1),(int)pageWidth/2 - 35, (int)(pageHeight + fontHeight - fontDescent - 50));
        g2d.translate(0f, headerHeightOnPage);
        g2d.translate(0f, -pageIndex * pageHeightForTable);
        if (pageIndex + 1 == totalNumPages) {
            int lastRowPrinted = numRowsOnAPage * pageIndex;
            int numRowsLeft = table.getRowCount() - lastRowPrinted;
            g2d.setClip(0, (int)(pageHeightForTable * pageIndex),(int)Math.ceil(tableWidthOnPage),(int)Math.ceil(oneRowHeight * numRowsLeft));
        } else {
            g2d.setClip(0, (int)(pageHeightForTable * pageIndex),(int)Math.ceil(tableWidthOnPage),(int)Math.ceil(pageHeightForTable));
        }
        g2d.scale(scale, scale);
        table.paint(g2d);
        g2d.scale(1/scale, 1/scale);
        g2d.translate(0f, pageIndex*pageHeightForTable);
        g2d.translate(0f, -headerHeightOnPage);
        g2d.setClip(0, 0,(int)Math.ceil(tableWidthOnPage),(int)Math.ceil(headerHeightOnPage));
        g2d.scale(scale, scale);
        table.getTableHeader().paint(g2d);
        
        return Printable.PAGE_EXISTS;
    } 
    
}
