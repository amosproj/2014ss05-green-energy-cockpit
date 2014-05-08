package de.fau.amos;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Chart {
	public JFreeChart chart;
	public Chart (String Database, String table, String User, String Password)throws SQLException, IOException{
		XYSeries series1 = createSeries(Database, table, User, Password);
		XYSeriesCollection collection1 = createCollection(series1);
		chart = renderChart(collection1);	
	}
	
	private XYSeries createSeries(String Database, String table, String User, String Password ) throws SQLException {

		XYSeries series = new XYSeries("Punkte1");
		Connection conn = DriverManager.getConnection(
				"jdbc:postgresql:"+Database, User, Password);
		Statement st = conn.createStatement();
		ResultSet rs = st
				.executeQuery("SELECT mdata_id, mvalue FROM " + table);
		while (rs.next()) {
			System.out.print(rs.getString(1) + " ");
			System.out.println(rs.getString(2));

			series.add(rs.getDouble(1), rs.getDouble(2));
		}
		rs.close();
		st.close();

		return series;
	}
	
	private XYSeriesCollection createCollection (XYSeries series){
		XYSeriesCollection collection = new XYSeriesCollection();
		collection.addSeries(series);
		return collection;
	}
	private JFreeChart renderChart(XYSeriesCollection collection) throws IOException{
		XYSplineRenderer spline = new XYSplineRenderer();
		NumberAxis xax = new NumberAxis("x");
		NumberAxis yax = new NumberAxis("y");
		XYPlot plot = new XYPlot(collection,xax,yax, spline);
		JFreeChart chart = new JFreeChart(plot);
		return chart;

		//PNG erstellen
//		org.jfree.chart.servlet.ServletUtilities.saveChartAsPNG(chart, 300, 300, session);
//		ChartRenderingInfo info = new ChartRenderingInfo();
//		String filename = ServletUtilities.saveChartAsPNG(chart, 300, 300, info, request.getSession());

		
		//Fenster erstellen
//		ApplicationFrame punkteframe = new ApplicationFrame("Punkte");
//		ChartPanel chartPanel2 = new ChartPanel(chart);
//		punkteframe.setContentPane(chartPanel2);
//		punkteframe.pack();
//		punkteframe.setVisible(true);
	}
	public JFreeChart getChart(){
		return chart;
	}
	
}
