package de.fau.amos;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jfree.chart.JFreeChart;

import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.awt.FontMapper;
import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.awt.geom.Rectangle2D.Double;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class ChartToPDF {

	public void convertChart(JFreeChart chart, String fileName) throws IOException {
	    if (chart != null) {       

	            	float width = PageSize.A4.getWidth();
	            	float height = PageSize.A4.getHeight();
	            	
		            Document document = new Document(new Rectangle(width, height)); 
	                PdfWriter writer = PdfWriter.getInstance(document, 
	                		new FileOutputStream(System.getProperty("userdir.location") + fileName + ".pdf")); 
	                
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
	    }
	}
}
