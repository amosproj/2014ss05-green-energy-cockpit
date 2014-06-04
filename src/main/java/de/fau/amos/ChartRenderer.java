package de.fau.amos;


import java.awt.Color;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.data.time.Day;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.ui.RectangleInsets;

/**
 * Servlet implementation class ChartSven
 */

public class ChartRenderer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ChartRenderer() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("image/png");

		ServletOutputStream os = response.getOutputStream();


		//<img src="../ChartRenderer?selectedChartType=<%=chartType%>&time=<%out.println(time+(timeGranularity==3?"&endTime="+endTime:""));%>&timeGranularity=<%=timeGranularity%>&countType=<%=countType%>&groupParameters=<%out.println(ChartPreset.createParameterString(request));%>" />

		// Parameters from URL
		String selectedChartType=request.getParameter("selectedChartType");
		String time=request.getParameter("time");
		String endTime=request.getParameter("endTime");
		String timeGranularity=request.getParameter("timeGranularity");
		String stringTimeGranularity=timeGranularityToString(timeGranularity);
		String countType = countTypeToString(request.getParameter("countType"));
		String groupParameters=encodeGroupParameters(request.getParameter("groupParameters"));


		// Create TimeSeriesCollection from URL-Parameters. This includes the SQL Query
		TimeSeriesCollection dataset = null;
		try {
			dataset = createTimeCollection(stringTimeGranularity, time, endTime, countType, groupParameters);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		
		// Create Chart from TimeSeriesCollection and do graphical modifications.
		JFreeChart chart = null;
		if(timeGranularity.equals("0")){       
			chart = createTimeLineChart(dataset, timeGranularity, time);
		}else{
			chart = createTimeBarChart(dataset, timeGranularity, time);
		}


		//create Image and clear output stream
		RenderedImage chartImage = chart.createBufferedImage(870-201-15, 512);
		ImageIO.write(chartImage, "png", os);
		os.flush();
		os.close();

	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private TimeSeriesCollection createTimeCollection(String granularity, String startTime, String endTime, String sumOrAvg, String groupParameters) throws NumberFormatException, SQLException{
		TimeSeriesCollection collection = new TimeSeriesCollection();
		groupParameters=groupParameters.replace("|", "splitHere");
		String[] groups=groupParameters.split("splitHere");
		for(int i=0;i<groups.length;i++){
			System.out.println("search for group "+groups[i]+" groupName: "+(groups[i].contains("'")?groups[i].substring(0,groups[i].indexOf("'")):groups[i]));
			String groupName=groups[i].contains("'")?groups[i].substring(0, groups[i].indexOf("'")):groups[i];
			groups[i]=groups[i].contains("'")?groups[i].substring(groupName.length()):"";
//			TimeSeries series = new TimeSeries(""+SQL.getValueOfFieldWithId("controlpoints","control_point_name",""+i));
			TimeSeries series = new TimeSeries(groupName);

			//			ArrayList<ArrayList<String>> ret=SQL.query(
			//"select control_point_name,value from controlpoints,measures where controlpoint_id='"+(i+1)+"' and controlpoint_id=controlpoints_id;"
			//"select * from (select round("+ countType +"(wert), 4),control_point_name,zeit from (select "+ countType +"(value)as wert,control_point_name,date_trunc('" + granularity + "',measure_time)as zeit from measures inner join controlpoints on measures.controlpoint_id=controlpoints.controlpoints_id where measure_time >= '"+ startTime + "' AND measure_time < '" + endTime + "' group by measure_time,control_point_name) as tmp group by zeit,control_point_name)as unsorted order by zeit,control_point_name;"
			ResultSet rs = SQL.queryToResultSet(
					"select * from (select round("
							+sumOrAvg
							+"(gruppenWert),4), gruppenZeit from(select "
							+sumOrAvg
							+"(wert) as gruppenWert,control_point_name, zeit1 as gruppenZeit from (select "
							+sumOrAvg
							+"(value)as wert,control_point_name,date_trunc('"
							+granularity
							+"',measure_time)as zeit1 from measures inner join controlpoints on measures.controlpoint_id=controlpoints.controlpoints_id where measure_time >= '"
							+startTime
							+"' AND measure_time < '"
							+endTime
							+"' AND controlpoints_id in("
							+groups[i]
									+") group by measure_time,control_point_name)as data group by zeit1,control_point_name)as groupedByTime group by gruppenZeit)as result order by gruppenZeit;"
					);

			if(rs!=null){
				switch(granularity){
				
				case "minute":
				while (rs.next()) {
						series.add(new Minute(
								Integer.parseInt(rs.getString(2).substring(14,16)),
								Integer.parseInt(rs.getString(2).substring(11,13)),
								Integer.parseInt(rs.getString(2).substring(8,10)),
								Integer.parseInt(rs.getString(2).substring(5,7)),
								Integer.parseInt(rs.getString(2).substring(0,4))
								), rs.getDouble(1)/1000);					
					}
				break;
				
				case "day":
				while (rs.next()) {	
						series.add(new Day(Integer.parseInt(rs.getString(2).substring(8,10)),
						Integer.parseInt(rs.getString(2).substring(5,7)),
						Integer.parseInt(rs.getString(2).substring(0,4))
						), rs.getDouble(1)/1000);					
					}
				break;
				
				case "month":
					while (rs.next()) {	
						series.add(new Month(Integer.parseInt(rs.getString(2).substring(5,7)),
						Integer.parseInt(rs.getString(2).substring(0,4))
						), rs.getDouble(1)/1000);					
					}
				break;
					
				case "year":
					while (rs.next()) {	
						series.add(new Year(Integer.parseInt(rs.getString(2).substring(0,4))
						), rs.getDouble(1)/1000);					
					}
					break;
				
					//default: day
				default:
					while (rs.next()) {	
						series.add(new Day(Integer.parseInt(rs.getString(2).substring(8,10)),
						Integer.parseInt(rs.getString(2).substring(5,7)),
						Integer.parseInt(rs.getString(2).substring(0,4))
						), rs.getDouble(1)/1000);					
					}
				}
				
				
				
				rs.close();	
			}
			//Add the series to the collection
			collection.addSeries(series);
		}
		return collection;
	}

	private JFreeChart createTimeLineChart(TimeSeriesCollection collection, String timeGranularity, String time){
		
		// Modification of X-Axis Label
		int day = Integer.parseInt(time.substring(8,10));
		int month = Integer.parseInt(time.substring(5,7));
		String dayString = new DateFormatSymbols(Locale.US).getWeekdays()[day] + ", " + day + ". ";
		String monthString = new DateFormatSymbols(Locale.US).getMonths()[month -1];
		String xAxisLabel = "" + dayString +   monthString + "  " +  time.substring(0,4);
		 
		//Creation of the lineChart
		JFreeChart lineChart = ChartFactory.createTimeSeriesChart(
				"Line Chart",              	// title
				xAxisLabel,             	// x-axis label
				"Energy Consumption [kW]",       // y-axis label
				collection,            		// data
				true,               		// create legend?
				false,               		// generate tooltips?
				false               		// generate URLs?
				);
		
		//graphical modifications for LineChart
		lineChart.setBackgroundPaint(Color.white);
		XYPlot plot = lineChart.getXYPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0));
		return lineChart;
	}
	
	private JFreeChart createTimeBarChart(TimeSeriesCollection collection, String timeGranularity, String time){
		
		String xAxisLabel = null;
		
		// Modification of X-Axis Label (depending on the granularity)
		int month;

		String monthString = null;
		switch(timeGranularity){
		//for Case "0" see method "createTimeLineChart"
			case "1":
				month = Integer.parseInt(time.substring(5,7));
				monthString = new DateFormatSymbols(Locale.US).getMonths()[month -1];
				xAxisLabel = "" +  monthString + "  " +  time.substring(0,4);
			break;
		
			case "2":
			xAxisLabel = time.substring(0,4);
			break;
			
			case "3":
				xAxisLabel = "Years";
				break;

			default:
				xAxisLabel = "Timespan";
		}
		
		JFreeChart barChart = ChartFactory.createXYBarChart(
				"Bar Chart",              		// title
				xAxisLabel,             		// x-axis label
				true,               			// date axis?
				"Energy Consumption [kW]",           // y-axis label
				collection,            			// data
				PlotOrientation.VERTICAL,       // orientation
				true,               			// create legend?
				true,               			// generate tooltips?
				false               			// generate URLs?
				);
		
		//graphical modifications for BarChart
		barChart.setBackgroundPaint(Color.white);
		XYPlot plot = barChart.getXYPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0));
				
		//Axis modification: Set Axis X-Value directly below the bars
		DateAxis dateAxis = (DateAxis) plot.getDomainAxis();
		dateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
		
		//Axis modification: Remove Values from x-Axis that belong to former/later time element (Month/Day)
		dateAxis.setLowerMargin(0.01);
		dateAxis.setUpperMargin(0.01);
		
		//Axis modification: Axis values (depending on timeGranularity)
		if(timeGranularity.equals("1")){
			dateAxis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY, 2,new SimpleDateFormat("  dd.  ", Locale.US)));
		}
		if(timeGranularity.equals("2")){
			dateAxis.setTickUnit(new DateTickUnit(DateTickUnitType.MONTH, 1,new SimpleDateFormat(" MMM ", Locale.US)));
		}
		if(timeGranularity.equals("3")){
			dateAxis.setTickUnit(new DateTickUnit(DateTickUnitType.YEAR, 1,new SimpleDateFormat(" yyyy ", Locale.US)));
		}

		
		ClusteredXYBarRenderer clusteredxybarrenderer = new ClusteredXYBarRenderer(
                0.25, false);
		clusteredxybarrenderer.setShadowVisible(false);
		clusteredxybarrenderer.setBarPainter(new StandardXYBarPainter());
		plot.setRenderer(clusteredxybarrenderer);
		return barChart;

	}

	private String timeGranularityToString(String timeGranularity){
		if(timeGranularity==null){
			return "minute";
		}
		switch(timeGranularity){
		case "0":
			//show one day, show all data->don't trunc
			return "minute";
		case "1":
			//show one month -> trunc to day
			return "day";
		case "2":
			//show one year -> trunc to month
			return "month";
		case "3":
			//show two or more years -> trunc to year
			return "year";
		default:
			//default, don't trunc
			return "minute";
		}
	}

	
	private String countTypeToString(String countType){
		int intCountType=0;
		try{
			intCountType=Integer.parseInt(countType);
		}catch(NumberFormatException e){
			intCountType=0;
		}
		switch(intCountType){
		case 0:
			return "avg";
		case 1:
			return "sum";
		default:
			return "sum";			
		}
	}

	private String encodeGroupParameters(String in){
		if(in==null){
			return "";
		}
		in=in.replace("%2C",",");
		in=in.replace("%27","'");
		in=in.replace("%7C","|");
		return in;
	}
}