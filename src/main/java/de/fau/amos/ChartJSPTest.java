package de.fau.amos;


	import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

	public class ChartJSPTest {
		
		private JFreeChart chart=null;
		
		public ChartJSPTest (String Database, String table, String User, String Password){
			try {
//				series1 = createSeries(Database, table, User, Password);
				
//				XYSeries series1 = createSeriesQuickTest("Wertgruppe1",5);
//				XYSeries series2 = createSeriesQuickTest("Wertgruppe2",3);
//				XYSeries series3 = createSeriesQuickTest("Wertgruppe3",7);
//				XYSeriesCollection collection1 = createCollection(series1,series2,series3);
				
				TimeSeries time1=createSeriesQuickTestTime("Wertegruppe1",1);
				TimeSeries time2=createSeriesQuickTestTime("Wertegruppe2",3);
				TimeSeries time3=createSeriesQuickTestTime("Wertegruppe3",4);
				
				TimeSeriesCollection collection1=createTimeCollection(time1,time2,time3);
				
				chart = renderChartTime(collection1);	
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

			XYSeries series = new XYSeries("Punkte1");
			ResultSet rs = SQL.querryToResultSet("select * from (select sum(wert),control_point_name,zeit from (select sum(value)as wert,control_point_name,date_trunc('day',measure_time)as zeit from measures inner join controlpoints on measures.controlpoint_id=controlpoints.controlpoints_id where controlpoints.controlpoints_id='1' group by measure_time,control_point_name) as tmp group by zeit,control_point_name)as unsorted order by zeit,control_point_name;");
			while (rs.next()) {
				//TODO remove murks...
//				series.add(rs.getString(3), rs.getDouble(1));
			}
			rs.close();
			
			return series;
		}
		
		private TimeSeries createSeriesQuickTestTime(String name,int id) throws SQLException {

			TimeSeries series = new TimeSeries("Punkte1");
			//day
//			ResultSet rs = SQL.querryToResultSet("select * from (select sum(wert),control_point_name,zeit from (select sum(value)as wert,control_point_name,date_trunc('day',measure_time)as zeit from measures inner join controlpoints on measures.controlpoint_id=controlpoints.controlpoints_id where controlpoints.controlpoints_id='1' group by measure_time,control_point_name) as tmp group by zeit,control_point_name)as unsorted order by zeit,control_point_name;");
			
			//hour
			//			ResultSet rs = SQL.querryToResultSet("select * from (select sum(wert),control_point_name,zeit from (select sum(value)as wert,control_point_name,date_trunc('hour',measure_time)as zeit from measures inner join controlpoints on measures.controlpoint_id=controlpoints.controlpoints_id where controlpoints.controlpoints_id='1' group by measure_time,control_point_name) as tmp group by zeit,control_point_name)as unsorted order by zeit,control_point_name;");

			//minute
			ResultSet rs = SQL.querryToResultSet("select * from (select sum(wert),control_point_name,zeit from (select sum(value)as wert,control_point_name,measure_time as zeit from measures inner join controlpoints on measures.controlpoint_id=controlpoints.controlpoints_id where controlpoints.controlpoints_id='"+id+"' group by measure_time,control_point_name) as tmp group by zeit,control_point_name)as unsorted order by zeit,control_point_name;");

			while (rs.next()) {

				//day
//				series.add(new Day(Integer.parseInt(rs.getString(3).substring(8,10)),
//						Integer.parseInt(rs.getString(3).substring(5,7)),
//						Integer.parseInt(rs.getString(3).substring(0,4))
//						), rs.getDouble(1));
				
				//hour
//				series.add(new Hour(Integer.parseInt(rs.getString(3).substring(11,13)),
//						Integer.parseInt(rs.getString(3).substring(8,10)),
//						Integer.parseInt(rs.getString(3).substring(5,7)),
//						Integer.parseInt(rs.getString(3).substring(0,4))
//						), rs.getDouble(1));
				
				//all
				series.add(new Minute(Integer.parseInt(rs.getString(3).substring(14,15)),
				Integer.parseInt(rs.getString(3).substring(11,13)),
				Integer.parseInt(rs.getString(3).substring(8,10)),
				Integer.parseInt(rs.getString(3).substring(5,7)),
				Integer.parseInt(rs.getString(3).substring(0,4))
				), rs.getDouble(1));
				
			}
			rs.close();
			
			return series;
		}
		
		private XYSeriesCollection createCollection (XYSeries...series){
			XYSeriesCollection collection = new XYSeriesCollection();
			for(int i=0;i<series.length;i++){
				collection.addSeries(series[i]);
			}
			return collection;
		}
		
		private TimeSeriesCollection createTimeCollection (TimeSeries...series){
			TimeSeriesCollection collection = new TimeSeriesCollection();
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

		private JFreeChart renderChartTime(TimeSeriesCollection collection) throws IOException{
			XYLineAndShapeRenderer spline = new XYLineAndShapeRenderer();
			NumberAxis xax = new NumberAxis("x");
			NumberAxis yax = new NumberAxis("y");
//			TimePlot plot = new XYPlot(collection,xax,yax, spline);
			
//			JFreeChart chart = new JFreeChart(plot);
			JFreeChart chart=ChartFactory.createTimeSeriesChart("Title","x-Achse","Y-Achse",collection);
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

