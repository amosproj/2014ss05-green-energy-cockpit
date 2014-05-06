/**
*
* JFreeChart version 0.9.20
* Called by Pie3DDemo.jsp
*
*/

package de.fau.amos;

import java.io.*;
import java.sql.SQLException;
import java.awt.*;
import java.awt.image.*;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.urls.*;
import org.jfree.chart.entity.*;

import javax.servlet.http.*;

public class Pie3DDemo {

  public Pie3DDemo() { 
  } 

  private DefaultPieDataset getDataset() { 
    // categories...
    String[] section = new String[] {
      "Jan","Feb","Mar","Apr","May","Jun",
      "Jul","Aug","Sep","Oct","Nov","Dec"
   };

    // data...
    double[] data = new double[section.length];
    for (int i = 0; i < data.length; i++) {
        data[i] = 10 + (Math.random() * 10);
    }

    // create the dataset...
    DefaultPieDataset dataset = new DefaultPieDataset();
    for (int i = 0; i < data.length; i++) {
        dataset.setValue(section[i], data[i]);
    }
    return dataset; 
  } 

  public String getChartViewer(HttpServletRequest request, HttpServletResponse response) { 
    DefaultPieDataset dataset = getDataset(); 
    // create the chart...
    JFreeChart chart = ChartFactory.createPieChart(
          "Pie3D Chart Demo",  // chart title
          dataset,             // data
          true,                // include legend
          true,
          false
    );

    // set the background color for the chart...
    chart.setBackgroundPaint(Color.cyan);
    PiePlot plot = (PiePlot) chart.getPlot();
    plot.setNoDataMessage("No data available");
      
    // set drilldown capability...
    plot.setURLGenerator(new StandardPieURLGenerator("index.jsp","section"));
    plot.setLabelGenerator(null);

    // OPTIONAL CUSTOMISATION COMPLETED.

    ChartRenderingInfo info = null;
    HttpSession session = request.getSession(); 
    try {

      //Create RenderingInfo object 
      response.setContentType("text/html"); 
      info = new ChartRenderingInfo(new StandardEntityCollection());
      BufferedImage chartImage = chart.createBufferedImage(640, 400, info); 

      // putting chart as BufferedImage in session,  
      // thus making it available for the image reading action Action. 
      session.setAttribute("chartImage", chartImage); 

      PrintWriter writer = new PrintWriter(response.getWriter()); 
      ChartUtilities.writeImageMap(writer, "imageMap", info, false); 
      writer.flush();
     
    } 
    catch (Exception e) {
       // handle your exception here
    }
   
    String pathInfo = "http://";
    pathInfo += request.getServerName();
    int port = request.getServerPort();
    pathInfo += ":"+String.valueOf(port);
    pathInfo += request.getContextPath();
    pathInfo += "/de/fau/amos";
    String chartViewer = pathInfo + "/ChartViewer";
//    System.out.println("Chartviewer Path: " + chartViewer);
    return chartViewer;
  } 
}