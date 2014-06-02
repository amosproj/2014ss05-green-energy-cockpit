package de.fau.amos;


import java.awt.Color;
import java.awt.Font;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
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
		
		//parameter from url
		System.out.println("Time from URL:" + request.getParameter("time"));
		System.out.println("StartTime from URL:" + request.getParameter("startTime"));
		
		String selectedChartType=request.getParameter("selectedChartType");
		String time=request.getParameter("time");
		String endTime=request.getParameter("endTime");
		String timeGranularity=request.getParameter("timeGranularity");
		String stringTimeGranularity=timeGranularityToString(timeGranularity);
		String countType = countTypeToString(request.getParameter("countType"));
		String groupParameters=encodeGroupParameters(request.getParameter("groupParameters"));
		
		String chartType="1";
		
		if(stringTimeGranularity.equals("minute")){
			//show as line chart
			chartType="4";
		}else{
			//show as bar chart
			chartType="2";
		}
				
		//old
//		String chartType=request.getParameter("chartType");
		String startTime = request.getParameter("startTime");
//		String endTime = request.getParameter("endTime");
		String granularity = timeGranularityToString(request.getParameter("granularity"));
//		String countType = countTypeToString(request.getParameter("countType"));
//		String groupParameters=encodeGroupParameters(request.getParameter("groupParameters"));

//		System.out.println(time+" "+endTime+" ["+groupParameters+"]");
		
		//createDataset
//		DefaultCategoryDataset defaultDataset = new DefaultCategoryDataset();
		
		//get data for parameters
//		fetchData(defaultDataset, stringTimeGranularity, time, endTime, countType, groupParameters);		
					
		//create Chart
//		JFreeChart chart= createTypeChart(chartType, defaultDataset);

		JFreeChart chart = null;
		TimeSeriesCollection dataset = null;
		try {
			dataset = createTimeCollection(stringTimeGranularity, time, endTime, countType, groupParameters);
		} catch (NumberFormatException | SQLException e) {
			e.printStackTrace();
		}
		
		// Dataset (Collection) for BarChart
		if(timeGranularity.equals("0")){

			JFreeChart lineChart = ChartFactory.createTimeSeriesChart(
		            "Line Chart",              // title
		            "Timespan",             // x-axis label
		            "Energy Consumption",           // y-axis label
		            dataset,            // data
		            true,               // create legend?
		            true,               // generate tooltips?
		            false               // generate URLs?
		        );
	        lineChart.setBackgroundPaint(Color.white);
	        XYPlot plot = lineChart.getXYPlot();
	        plot.setBackgroundPaint(Color.white);
	        plot.setDomainGridlinePaint(Color.white);
	        plot.setRangeGridlinePaint(Color.white);
	        plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0));

			chart = lineChart;
		}else{


		JFreeChart barChart = createTimeBarChart(dataset);

        // Set chart styles and Set plot styles
        barChart.setBackgroundPaint(Color.white);
        XYPlot plot = barChart.getXYPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0));
        chart = barChart;
		}
	
		//create Image and clear output stream
		RenderedImage chartImage = chart.createBufferedImage(870-201-15, 512);
		ImageIO.write(chartImage, "png", os);
		os.flush();
		os.close();

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	
	
	
	
	
	
	// Create Chart from TimeSeriesCollection
	private JFreeChart renderTimeChart(TimeSeriesCollection collection) throws IOException{
		
		JFreeChart chart=ChartFactory.createTimeSeriesChart("Title","x-Achse","Y-Achse",collection);
		chart.setBackgroundPaint(Color.white);
		return chart;
		
	}
		
	
	
	private TimeSeriesCollection createTimeCollection(String granularity, String startTime, String endTime, String sumOrAvg, String groupParameters) throws NumberFormatException, SQLException{
		TimeSeriesCollection collection = new TimeSeriesCollection();
		String[] groups=groupParameters.split("s");
		for(int i=0;i<groups.length;i++){
			String groupNumber=groups[i].contains("'")?groups[i].substring(0, groups[i].indexOf("'")):groups[i];
			groups[i]=groups[i].contains("'")?groups[i].substring(groupNumber.length()):"";
			TimeSeries series = new TimeSeries(""+SQL.getValueOfFieldWithId("controlpoints","control_point_name",""+i));
			
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
//			if(ret!=null){
//				for(int j=1;j<ret.size();j++){
//					data.addValue(Double.parseDouble(ret.get(j).get(0)),
//							"Group"+groupNumber,
//							ret.get(j).get(1));
//				}
//			}
			while (rs.next()) {
//				System.out.println("Spalte 2 " + rs.getString(2));
				series.add(new Minute(Integer.parseInt(rs.getString(2).substring(14,15)),
						Integer.parseInt(rs.getString(2).substring(11,13)),
						Integer.parseInt(rs.getString(2).substring(8,10)),
						Integer.parseInt(rs.getString(2).substring(5,7)),
						Integer.parseInt(rs.getString(2).substring(0,4))
						), rs.getDouble(1));	

			}
			rs.close();	
			//Add the series to the collection
			collection.addSeries(series);
		}
		return collection;
	}
	
	
	
	
	
	
	
	
	
	private JFreeChart createTimeBarChart(TimeSeriesCollection collection){
		
        JFreeChart chart = ChartFactory.createXYBarChart(
                "Bar Chart",              // title
                "Timespan",             // x-axis label
                true,               // date axis?
                "Energy Consumption",           // y-axis label
                collection,            // data
                PlotOrientation.VERTICAL,       // orientation
                true,               // create legend?
                true,               // generate tooltips?
                false               // generate URLs?
        );
        return chart;
		
	}
	
	
	
	
	
	private JFreeChart createTypeChart(String val1, DefaultCategoryDataset defaultDataset){
		JFreeChart tempChart=null;
		//select Type of chart
		int type=0;
		try{
			type=Integer.parseInt(val1);
		}catch(NumberFormatException e){
			type=0;
		}
		
		//Graphic options for all types of charts
		String fontName = "Lucida Sans";
		StandardChartTheme theme = (StandardChartTheme)org.jfree.chart.StandardChartTheme.createJFreeTheme();
	    theme.setTitlePaint( Color.decode( "#555555" ) );
	    theme.setExtraLargeFont( new Font(fontName,Font.PLAIN, 22) ); //title
	    theme.setLargeFont( new Font(fontName,Font.BOLD, 15)); //axis-title
	    theme.setRegularFont( new Font(fontName,Font.PLAIN, 11));
	    theme.setRangeGridlinePaint( Color.decode("#C0C0C0"));
	    theme.setPlotBackgroundPaint( Color.white );
	    theme.setChartBackgroundPaint( Color.white );
	    theme.setGridBandPaint( Color.red );
	    theme.setAxisOffset( new RectangleInsets(0,0,0,0) );
	    theme.setBarPainter(new StandardBarPainter());
	    theme.setAxisLabelPaint( Color.decode("#666666")  );
		
		
		
		switch(type){
//		case 1:
//			tempChart = ChartFactory.createAreaChart("Area Chart", "",
//					"Value", defaultDataset, PlotOrientation.VERTICAL, true, true, false);
//			return tempChart;
			
			//Bar Chart
		case 2:
			tempChart = ChartFactory.createBarChart("Energy Consumption", "",
				"Value", defaultDataset, PlotOrientation.VERTICAL, true, true, false);
			

		    theme.apply( tempChart );
		    generalChartModification(tempChart);
		    BarRenderer rend = (BarRenderer) tempChart.getCategoryPlot().getRenderer();
		    rend.setShadowVisible( true );
		    rend.setShadowXOffset( 2 );
		    rend.setShadowYOffset( 0 );
		    rend.setShadowPaint( Color.decode( "#C0C0C0"));
		    rend.setMaximumBarWidth( 0.1);
			return tempChart;
//		case 3:
//			tempChart = ChartFactory.createBarChart("Bar Chart 3D", "",
//				"Value", defaultDataset, PlotOrientation.VERTICAL, true, true, false);
//			return tempChart;
//			
			//Line Chart
		case 4:
			tempChart = ChartFactory.createLineChart("Energy Consumption", "",
					"Value", defaultDataset, PlotOrientation.VERTICAL, true, true, false);
		    theme.apply( tempChart );
		    generalChartModification(tempChart);
				return tempChart;
//		case 5:
//			 DefaultPieDataset pieDataset = new DefaultPieDataset();
//		        pieDataset.setValue("One", new Double(43.2));
//		        pieDataset.setValue("Two", new Double(10.0));
//		        pieDataset.setValue("Three", new Double(27.5));
//		        pieDataset.setValue("Four", new Double(17.5));
//		        pieDataset.setValue("Five", new Double(11.0));
//		        pieDataset.setValue("Six", new Double(19.4));
//			tempChart = ChartFactory.createPieChart("Pie Chart", pieDataset, true, true, false);
//			return tempChart;
//		case 6:
//			tempChart = new ChartJSPTest(null,null,null,null).getChart();
//			return tempChart;
		default: 
//			System.err.println("Error creating Chart. Chart type ('" + type + "') could not be resolved.");
			return null;
		}
		

	}
	
	
	private void generalChartModification (JFreeChart tempChart){
//	    tempChart.getCategoryPlot().setOutlineVisible( false );
//	    tempChart.getCategoryPlot().getRangeAxis().setAxisLineVisible( false );
//	    tempChart.getCategoryPlot().getRangeAxis().setTickMarksVisible( false );
//	    tempChart.getCategoryPlot().setRangeGridlineStroke( new BasicStroke() );
//	    tempChart.getCategoryPlot().getRangeAxis().setTickLabelPaint( Color.decode("#555555") );
//	    tempChart.getCategoryPlot().getDomainAxis().setTickLabelPaint( Color.decode("#555555") );
	    tempChart.setTextAntiAlias( true );
	    tempChart.setAntiAlias( true );
//	    tempChart.getCategoryPlot().getRenderer().setSeriesPaint( 0, Color.decode( "#4572a7" ));
	}
	
	private void fetchData(DefaultCategoryDataset data, String granularity, String startTime, String endTime, String sumOrAvg, String groupParameters){

//		System.out.println("fetch groups "+groupParameters);
		
		String[] groups=groupParameters.split("s");
		for(int i=0;i<groups.length;i++){
			String groupNumber=groups[i].contains("'")?groups[i].substring(0, groups[i].indexOf("'")):groups[i];
			groups[i]=groups[i].contains("'")?groups[i].substring(groupNumber.length()):"";

			ArrayList<ArrayList<String>> ret=SQL.query(
					//"select control_point_name,value from controlpoints,measures where controlpoint_id='"+(i+1)+"' and controlpoint_id=controlpoints_id;"
					//"select * from (select round("+ countType +"(wert), 4),control_point_name,zeit from (select "+ countType +"(value)as wert,control_point_name,date_trunc('" + granularity + "',measure_time)as zeit from measures inner join controlpoints on measures.controlpoint_id=controlpoints.controlpoints_id where measure_time >= '"+ startTime + "' AND measure_time < '" + endTime + "' group by measure_time,control_point_name) as tmp group by zeit,control_point_name)as unsorted order by zeit,control_point_name;"
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
			if(ret!=null){
				for(int j=1;j<ret.size();j++){
					data.addValue(Double.parseDouble(ret.get(j).get(0)),
							"Group"+groupNumber,
							ret.get(j).get(1));
				}
			}
		}
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
		return in;
	}
}