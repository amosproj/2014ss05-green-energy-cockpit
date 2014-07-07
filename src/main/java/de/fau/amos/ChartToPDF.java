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

/**
 * 
 * This class is intended for exporting any diagram shown into a PDF file using the JFreeChart and IText java library. 
 *
 */

public class ChartToPDF {

	public void convertChart(JFreeChart chart, String fileName) {
	    if (chart != null) {       
	    	
	    			//set page size
	            	float width = PageSize.A4.getWidth();
	            	float height = PageSize.A4.getHeight();
	            	
	            	//creating a new pdf document
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
	                
	                //sets the pdf page
	                Graphics2D g2D = new PdfGraphics2D(tp, width, height); 	        
	                Rectangle2D r2D = new Rectangle2D.Double(0, 0, width, height); 
	                
	                //draws the passed JFreeChart into the PDF file
	                chart.draw(g2D, r2D); 
	                
	                g2D.dispose(); 
	                cb.addTemplate(tp, 0, 0);
	                
	                document.close();      
	                
	                
	                writer.flush();
	                writer.close();
	    }
	}
}
