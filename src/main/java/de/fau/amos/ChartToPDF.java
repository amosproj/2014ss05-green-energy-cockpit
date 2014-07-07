/*
 * Copyright (c) 2014 by Sven Huprich, Dimitry Abb, Jakob Hübler, Cindy Wiebe, Ferdinand Niedermayer, Dirk Riehle, http://dirkriehle.com
 *
 * This file is part of the Green Energy Cockpit for the AMOS Project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.fau.amos;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.jfree.chart.JFreeChart;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class ChartToPDF {

	public void convertChart(JFreeChart chart, String fileName) {
	    if (chart != null) {       

	            	float width = PageSize.A4.getWidth();
	            	float height = PageSize.A4.getHeight();
	            	
		            Document document = new Document(new Rectangle(width, height)); 
	                PdfWriter writer;
					try {
						writer = PdfWriter.getInstance(document, 
								new FileOutputStream(new File(System.getProperty("userdir.location"), fileName + ".pdf")));
					} catch (DocumentException e) {
						e.printStackTrace();
						return;
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						return;
					} 
	                
	                document.addAuthor("Green Energy Cockpit"); 
	                document.open(); 
	                
	                PdfContentByte cb = writer.getDirectContent(); 
	                PdfTemplate tp = cb.createTemplate(width, height); 
	                
	                Graphics2D g2D = new PdfGraphics2D(tp, width, height); 	        
	                Rectangle2D r2D = new Rectangle2D.Double(0, 0, width, height); 
	                
	                chart.draw(g2D, r2D); 
	                
	                g2D.dispose(); 
	                cb.addTemplate(tp, 0, 0);
	                
	                document.close();      
	                
	                
	                writer.flush();
	                writer.close();
	    }
	}
}
