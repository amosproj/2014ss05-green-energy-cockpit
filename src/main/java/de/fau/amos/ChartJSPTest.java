package de.fau.amos;


	import java.io.IOException;
	import java.sql.Connection;
	import java.sql.DriverManager;
	import java.sql.ResultSet;
	import java.sql.SQLException;
	import java.sql.Statement;

	import javax.servlet.http.HttpSession;


	import org.jfree.chart.renderer.xy.XYSplineRenderer;
	import org.jfree.chart.servlet.ServletUtilities;
	import org.jfree.chart.ChartPanel;
	import org.jfree.chart.ChartRenderingInfo;
	import org.jfree.chart.JFreeChart;
	import org.jfree.chart.axis.NumberAxis;
	import org.jfree.chart.plot.XYPlot;
	import org.jfree.data.xy.XYSeries;
	import org.jfree.data.xy.XYSeriesCollection;
	import org.jfree.ui.ApplicationFrame;

	public class ChartJSPTest {
		
		private JFreeChart chart=null;
		
		public ChartJSPTest (String Database, String table, String User, String Password){
			try {
//				series1 = createSeries(Database, table, User, Password);
				XYSeries series1 = createSeriesQuickTest("Wertgruppe1",5);
				XYSeries series2 = createSeriesQuickTest("Wertgruppe2",3);
				XYSeries series3 = createSeriesQuickTest("Wertgruppe3",7);
				XYSeriesCollection collection1 = createCollection(series1,series2,series3);
				chart = renderChart(collection1);	
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private XYSeries createSeries(String Database, String table, String User, String Password ) throws SQLException {

			XYSeries series = new XYSeries("Punkte1");
			Connection conn = DriverManager.getConnection(
					"jdbc:postgresql:"+Database, User, Password);
			Statement st = conn.createStatement();
			ResultSet rs = st
					.executeQuery("SELECT mdata_id, mvalue FROM " + table);
			while (rs.next()) {
//				System.out.print(rs.getString(1) + " ");
//				System.out.println(rs.getString(2));

				series.add(rs.getDouble(1), rs.getDouble(2));
			}
			rs.close();
			st.close();

			return series;
		}
		
		private XYSeries createSeriesQuickTest(String name,double startVal) throws SQLException {

			XYSeries series = new XYSeries(name);
//			Connection conn = DriverManager.getConnection(
//					"jdbc:postgresql:"+Database, User, Password);
//			Statement st = conn.createStatement();
//			ResultSet rs = st
//					.executeQuery("SELECT mdata_id, mvalue FROM " + table);
//			while (rs.next()) {
//				System.out.print(rs.getString(1) + " ");
//				System.out.println(rs.getString(2));

			double prev=startVal;
			for(int i=0;i<10;i++){
				series.add(i+1,prev=prev+((Math.random()>0.5)?-1:1)*Math.random()*1);
			}
//				series.add(rs.getDouble(1), rs.getDouble(2));
//			}
//			rs.close();
//			st.close();

			return series;
		}
		
		private XYSeriesCollection createCollection (XYSeries...series){
			XYSeriesCollection collection = new XYSeriesCollection();
			for(int i=0;i<series.length;i++){
				collection.addSeries(series[i]);
			}
			return collection;
		}
		
		private JFreeChart renderChart(XYSeriesCollection collection) throws IOException{
			XYSplineRenderer spline = new XYSplineRenderer();
			NumberAxis xax = new NumberAxis("x");
			NumberAxis yax = new NumberAxis("y");
			XYPlot plot = new XYPlot(collection,xax,yax, spline);
			JFreeChart chart = new JFreeChart(plot);
			return chart;
			
			//Fenster erstellen
//			ApplicationFrame punkteframe = new ApplicationFrame("Punkte");
//			ChartPanel chartPanel2 = new ChartPanel(chart);
//			punkteframe.setContentPane(chartPanel2);
//			punkteframe.pack();
//			punkteframe.setVisible(true);
		}
		public JFreeChart getChart(){
			return chart;
		}
		
	}

