package de.fau.amos;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
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

		JFreeChart chart = null;

		// Parameters from URL
		String chartType=request.getParameter("selectedChartType");
		if(chartType!=null){
			chartType=chartType.trim();
		}else{
			chartType="";
		}
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		String timeGranularity=request.getParameter("timeGranularity");
		String stringTimeGranularity=timeGranularityToString(timeGranularity);
		String countType = countTypeToString(request.getParameter("countType"));
		String groupLocationParameters=encodeGroupParameters(request.getParameter("groupLocationParameters"));
		String groupFormatParameters=encodeGroupParameters(request.getParameter("groupFormatParameters"));
		String unit=request.getParameter("unit");

		int width=100;
		try{
			width=Integer.parseInt(request.getParameter("w"));
		}catch(NumberFormatException e){}
		int height=100;
		try{
			height=Integer.parseInt(request.getParameter("h"));
		}catch(NumberFormatException e){}

		if(chartType.equals("1")){
			//show time chart
			// Create TimeSeriesCollection from URL-Parameters. This includes the SQL Query
			TimeSeriesCollection dataset = null;
			dataset = createTimeCollection(stringTimeGranularity, startTime, endTime, countType, groupLocationParameters,unit);


			// Create Chart from TimeSeriesCollection and do graphical modifications.
			if(timeGranularity.equals("0")){       
				chart = createTimeLineChart(dataset, timeGranularity, startTime,unit);
			}else{
				chart = createTimeBarChart(dataset, timeGranularity, startTime,unit);
			}
		}else if(chartType.equals("2")){

			//show location-format chart
			DefaultCategoryDataset dataset=createLocationFormatCollection(startTime,endTime,countType,groupLocationParameters, groupFormatParameters,unit,chartType);
			chart=createLocationFormatChart(dataset,unit);

		}else if(chartType.equals("3")){

			//show location-format chart
			DefaultCategoryDataset dataset=createLocationFormatCollection(startTime,endTime,countType,groupLocationParameters, groupFormatParameters,unit,chartType);
			chart=createLocationFormatChart(dataset,unit);

		}else if(chartType.equals("forecast")){

			TimeSeriesCollection dataset=createForecastCollection(request.getParameter("forecastYear"),request.getParameter("forecastPlant"),
					request.getParameter("forecastMethod"),request.getParameter("forecastUnits"));
			chart = createForecastChart(dataset, "2004-01-01 00:00:00","1");
		}

		//create Image and clear output stream
		RenderedImage chartImage = chart.createBufferedImage(width,height);
		ImageIO.write(chartImage, "png", os);
		os.flush();
		os.close();

	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	//for time chart
	private TimeSeriesCollection createTimeCollection(String granularity, String startTime, String endTime, String sumOrAvg, String groupParameters,String unit){

		//time series containing all data
		TimeSeriesCollection collection = new TimeSeriesCollection();
		//split groupParameter string to get all queryed groups seperated
		groupParameters=groupParameters.replace("||", "splitHere");
		String[] groups=groupParameters.split("splitHere");

		//handle groups one after another
		for(int i=0;i<groups.length;i++){
			//get group name
			String groupName=groups[i].contains("'")?groups[i].substring(0, groups[i].indexOf("'")):groups[i];
			groups[i]=groups[i].contains("'")?groups[i].substring(groupName.length()):"";

			if(!groups[i].contains("|")){
				continue;
			}
			//get used plants
			String plants=groups[i].substring(groups[i].indexOf("|")+1);

			//prepare queryString
			groups[i]=groups[i].substring(0,groups[i].indexOf("|"));

			//generate series for group
			TimeSeries series = new TimeSeries(groupName);

			ResultSet rs = null;

			//skip group if nothing is selected to query
			if(groups[i].trim()!=""){
				if("1".equals(unit)){
					//query kWh
					rs=SQL.queryToResultSet(
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
											+") group by measure_time,control_point_name)as data group by zeit1,control_point_name)as groupedByTime group by gruppenZeit)as result order by gruppenZeit"
											+ ";"
							);
				}else if("2".equals(unit)){
					//query kWh/TNF (only as sum, not avg)

					rs=SQL.queryToResultSet(			

							"select * from (select round(sum(gruppenWert)/(select sum(am) from(select sum(amount)as am,date_trunc('"
									+ granularity
									+ "',measure_time)as zeit from productiondata inner join controlpoints on productiondata.controlpoint_id=controlpoints.controlpoints_id "
									+ "where productiondata.measure_time >= '"
									+ startTime
									+ "' AND productiondata.measure_time < '"
									+ endTime
									+ "' AND reference_point='t' AND plant_id in("
									+ plants
									+ ") group by measure_time)as wat where zeit=gruppenZeit group by zeit order by zeit),4), gruppenZeit from("
									+ "select sum(wert) as gruppenWert,control_point_name, zeit1 as gruppenZeit from (select sum(value)as wert,control_point_name,date_trunc('"
									+ granularity
									+ "',measure_time)as zeit1 from measures inner join controlpoints on measures.controlpoint_id=controlpoints.controlpoints_id where measure_time >= '"
									+ startTime
									+ "' AND measure_time < '"
									+ endTime
									+ "' AND controlpoints_id in("
									+ groups[i]
											+ ")group by measure_time,control_point_name)as data group by zeit1,control_point_name)as groupedByTime group by gruppenZeit)as result order by gruppenZeit"
											+ ";"
							);

				}
			}
			if(rs!=null){
				try{
					while (rs.next()) {
						switch(granularity){

						case "minute":
							series.add(new Minute(
									Integer.parseInt(rs.getString(2).substring(14,16)),
									Integer.parseInt(rs.getString(2).substring(11,13)),
									Integer.parseInt(rs.getString(2).substring(8,10)),
									Integer.parseInt(rs.getString(2).substring(5,7)),
									Integer.parseInt(rs.getString(2).substring(0,4))
									), rs.getDouble(1)/1000);					
							break;
						case "day":
							series.add(new Day(Integer.parseInt(rs.getString(2).substring(8,10)),
									Integer.parseInt(rs.getString(2).substring(5,7)),
									Integer.parseInt(rs.getString(2).substring(0,4))
									), rs.getDouble(1)/1000);					
							break;

						case "month":
							series.add(new Month(Integer.parseInt(rs.getString(2).substring(5,7)),
									Integer.parseInt(rs.getString(2).substring(0,4))
									), rs.getDouble(1)/1000);					
							break;

						case "year":
							series.add(new Year(Integer.parseInt(rs.getString(2).substring(0,4))
									), rs.getDouble(1)/1000);					
							break;

							//default: day
						default:
							series.add(new Day(Integer.parseInt(rs.getString(2).substring(8,10)),
									Integer.parseInt(rs.getString(2).substring(5,7)),
									Integer.parseInt(rs.getString(2).substring(0,4))
									), rs.getDouble(1)/1000);					

						}
					}
					rs.close();	

				}catch(NumberFormatException e){

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			//Add the series to the collection
			collection.addSeries(series);
		}
		return collection;
	}

	private JFreeChart createTimeLineChart(TimeSeriesCollection collection, String timeGranularity, String time,String unit){



		// Modification of X-Axis Label
		int day = Integer.parseInt(time.substring(8,10));
		int month = Integer.parseInt(time.substring(5,7));
		int year = Integer.parseInt(time.substring(0,4));
		//get Weekday
		Calendar c = Calendar.getInstance();
		c.set(year, month - 1, day, 0, 0);
		int weekDay = c.get(Calendar.DAY_OF_WEEK);

		String dayString = new DateFormatSymbols(Locale.US).getWeekdays()[weekDay] + ", " + day + ". ";
		String monthString = new DateFormatSymbols(Locale.US).getMonths()[month -1];
		String xAxisLabel = "" + dayString +   monthString + "  " +  time.substring(0,4);

		//Creation of the lineChart
		JFreeChart lineChart = ChartFactory.createTimeSeriesChart(
				"Line Chart",              	// title
				xAxisLabel,             	// x-axis label
				//				"Energy Consumption "+("1".equals(unit)?"[kWh]":("2".equals(unit)?"[kWh/TNF]":("3".equals(unit)?"[TNF]":""))),       // y-axis label
				("1".equals(unit)?"Energy Consumption [kWh]":("2".equals(unit)?"Energy Consumption [kWh/TNF]":("3".equals(unit)?"Produced Pieces [TNF]":""))),
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

	private JFreeChart createTimeBarChart(TimeSeriesCollection collection, String timeGranularity, String time,String unit){

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
				//				"Energy Consumption "+("1".equals(unit)?"[kWh]":("2".equals(unit)?"[kWh/TNF]":("3".equals(unit)?"[TNF]":""))),       // y-axis label
				("1".equals(unit)?"Energy Consumption [kWh]":("2".equals(unit)?"Energy Consumption [kWh/TNF]":("3".equals(unit)?"Produced Pieces [TNF]":""))),
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

	//for chart 2
	private DefaultCategoryDataset createLocationFormatCollection(String startTime, String endTime, String sumOrAvg, String locationGroupParameters,String formatGroupParameters,String unit,String chartType){
		//create collection to store data
		DefaultCategoryDataset collection=new DefaultCategoryDataset();

		if(!"2".equals(chartType)&&!"3".equals(chartType)){
			return collection;
		}

		//get location groups
		locationGroupParameters=locationGroupParameters.replace("||", "splitHere");
		String[] locationGroups=locationGroupParameters.split("splitHere");
		formatGroupParameters=formatGroupParameters.replace("|", "splitHere");
		String[] formatGroups=formatGroupParameters.split("splitHere");

		for(int f=0;f<formatGroups.length;f++){
			String formatGroupName=formatGroups[f].contains("'")?formatGroups[f].substring(0, formatGroups[f].indexOf("'")):formatGroups[f];
			String formatGroupParam=formatGroups[f].contains("'")?formatGroups[f].substring(formatGroupName.length()):"";
			if(formatGroupParam.trim().equals("")){
				continue;
			}
			for(int l=0;l<locationGroups.length;l++){
				String locationGroupName=locationGroups[l].contains("'")?locationGroups[l].substring(0, locationGroups[l].indexOf("'")):locationGroups[l];
				String locationGroupParam=locationGroups[l].contains("'")?locationGroups[l].substring(locationGroupName.length()):"";
				if(!locationGroupParam.contains("|")){
					continue;
				}

				//get used plants
				//				String plants=locationGroupParam.substring(locationGroupParam.indexOf("|")+1);

				//prepare queryString
				locationGroupParam=locationGroupParam.substring(0,locationGroupParam.indexOf("|"));

				ResultSet rs = null;

				if(locationGroups[l].trim()!=""){
					if("1".equals(unit)){
						//query kWh
						rs=SQL.queryToResultSet(
								"select round(sum(measures.value),4)from productiondata"
										+ " inner join measures on measures.controlpoint_id=productiondata.controlpoint_id and measures.measure_time=productiondata.measure_time"
										+ " where productiondata.measure_time >= '"
										+ startTime
										+ "' AND productiondata.measure_time < '"
										+ endTime
										+ "' AND measures.controlpoint_id in("
										+ locationGroupParam
										+ ") AND productiondata.product_id in("
										+ formatGroupParam
										+ ")"
										+ ";"


//								"select round(sum(value)*(SELECT round(sum(amount)/(SELECT sum(amount) FROM productiondata "
//								+ "inner join controlpoints on productiondata.controlpoint_id=controlpoints.controlpoints_id "
//								+ "WHERE measure_time >='"
//								+ startTime
//								+ "' AND measure_time <'"
//								+ endTime
//								+ "' AND plant_id in("
//								+ plants
//								+ ")),4)FROM productiondata WHERE measure_time >='"
//								+ startTime
//								+ "' AND measure_time <'"
//								+ endTime
//								+ "' AND controlpoint_id in("
//								+ locationGroupParam
//								+ ") AND product_id in("
//								+ formatGroupParam
//								+ ")),4)from measures "
//								+ "inner join controlpoints on measures.controlpoint_id=controlpoints.controlpoints_id "
//								+ "WHERE measure_time >='"
//								+ startTime
//								+ "' AND measure_time <'"
//								+ endTime
//								+ "' AND controlpoint_id in("
//								+ locationGroupParam
//								+ ")"
//								+ ";"
								);
					}else if("2".equals(unit)){
						//query kWh/TNF
						rs=SQL.queryToResultSet(

								"select round(avg(measures.value/productiondata.amount),4)from productiondata"
										+ " inner join measures on measures.controlpoint_id=productiondata.controlpoint_id and measures.measure_time=productiondata.measure_time"
										+ " where productiondata.measure_time >= '"
										+ startTime
										+ "' AND productiondata.measure_time < '"
										+ endTime
										+ "' AND measures.controlpoint_id in("
										+ locationGroupParam
										+ ") AND productiondata.product_id in("
										+ formatGroupParam
										+ ")"
										+ ";"

//								"select round((sum(value)*(SELECT round(sum(amount)/(SELECT sum(amount) "
//								+ "FROM productiondata inner join controlpoints on productiondata.controlpoint_id=controlpoints.controlpoints_id WHERE measure_time >='"
//								+ startTime
//								+ "' AND measure_time <'"
//								+ endTime
//								+ "' AND plant_id in("
//								+ plants
//								+ ")),4)FROM productiondata WHERE measure_time >='"
//								+ startTime
//								+ "' AND measure_time <'"
//								+ endTime
//								+ "' AND controlpoint_id in("
//								+ locationGroupParam
//								+ ") AND product_id in("
//								+ formatGroupParam
//								+ ")))/(select sum(amount) from productiondata inner join controlpoints on productiondata.controlpoint_id=controlpoints.controlpoints_id WHERE measure_time >='"
//								+ startTime
//								+ "' AND measure_time <'"
//								+ endTime
//								+ "'AND controlpoint_id in("
//								+ locationGroupParam
//								+ ")AND product_id in("
//								+ formatGroupParam
//								+ ")),4)from measures inner join controlpoints on measures.controlpoint_id=controlpoints.controlpoints_id WHERE measure_time >='"
//								+ startTime
//								+ "' AND measure_time <'"
//								+ endTime
//								+ "' AND controlpoint_id in("
//								+ locationGroupParam
//								+ ")"
//								+ ";"
								);

					}else if("3".equals(unit)){
						rs=SQL.queryToResultSet(
								"select round(sum(productiondata.amount),4)from productiondata"
										+ " inner join measures on measures.controlpoint_id=productiondata.controlpoint_id and measures.measure_time=productiondata.measure_time"
										+ " where productiondata.measure_time >= '"
										+ startTime
										+ "' AND productiondata.measure_time < '"
										+ endTime
										+ "' AND measures.controlpoint_id in("
										+ locationGroupParam
										+ ") AND productiondata.product_id in("
										+ formatGroupParam
										+ ")"
										+ ";"


//								"select sum(amount) from productiondata "
//								+ "inner join controlpoints on productiondata.controlpoint_id=controlpoints.controlpoints_id "
//								+ "WHERE measure_time >='"
//								+ startTime
//								+ "' AND measure_time <'"
//								+ endTime
//								+ "'AND controlpoint_id in("
//								+ locationGroupParam
//								+ ")AND product_id in("
//								+ formatGroupParam
//								+ ");"
								);
					}
				}
				if(rs!=null){

					try {
						rs.next();
						if("2".equals(chartType)){
							collection.addValue(rs.getDouble(1),formatGroupName,locationGroupName);
						}else if("3".equals(chartType)){
							collection.addValue(rs.getDouble(1),locationGroupName,formatGroupName);							
						}
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}	
				}

			}
		}
		return collection;
	}

	@SuppressWarnings("unused")
	@Deprecated
	//was for chart 3 -> added to createLocationFormatCollection
	private DefaultCategoryDataset createFormatLocationCollection(String startTime, String endTime, String sumOrAvg, String locationGroupParameters,String formatGroupParameters,String unit){
		DefaultCategoryDataset collection=new DefaultCategoryDataset();
		locationGroupParameters=locationGroupParameters.replace("||", "splitHere");
		String[] locationGroups=locationGroupParameters.split("splitHere");
		formatGroupParameters=formatGroupParameters.replace("|", "splitHere");
		String[] formatGroups=formatGroupParameters.split("splitHere");

		for(int l=0;l<locationGroups.length;l++){
			String locationGroupName=locationGroups[l].contains("'")?locationGroups[l].substring(0, locationGroups[l].indexOf("'")):locationGroups[l];
			String locationGroupParam=locationGroups[l].contains("'")?locationGroups[l].substring(locationGroupName.length()):"";
			if(!locationGroupParam.contains("|")){
				continue;
			}

			//get used plants
			//			String plants=locationGroupParam.substring(locationGroupParam.indexOf("|")+1);

			//prepare queryString
			locationGroupParam=locationGroupParam.substring(0,locationGroupParam.indexOf("|"));

			for(int f=0;f<formatGroups.length;f++){
				String formatGroupName=formatGroups[f].contains("'")?formatGroups[f].substring(0, formatGroups[f].indexOf("'")):formatGroups[f];
				String formatGroupParam=formatGroups[f].contains("'")?formatGroups[f].substring(formatGroupName.length()):"";
				if(formatGroupParam.trim().equals("")){
					continue;
				}

				ResultSet rs = null;

				if(locationGroups[l].trim()!=""){
					if("1".equals(unit)){

					}else if("2".equals(unit)){

					}else if("3".equals(unit)){
						rs=SQL.queryToResultSet(
								"SELECT sum(amount)"
										+" FROM productiondata"
										+" WHERE measure_time >='"
										+startTime
										+"' AND measure_time <'"
										+endTime
										+"' AND controlpoint_id in("
										+ locationGroupParam
										+") AND product_id in("
										+ formatGroupParam
										+");"
								);
					}
				}
				if(rs!=null){

					try {
						rs.next();
						collection.addValue(rs.getDouble(1),locationGroupName,formatGroupName);
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}	
				}

			}
		}
		return collection;
	}

	private JFreeChart createLocationFormatChart(DefaultCategoryDataset collection,String unit){

		JFreeChart barChart = ChartFactory.createBarChart("Production Consumption","",
				("1".equals(unit)?"Energy Consumption [kWh]":("2".equals(unit)?"Energy Consumption [kWh/TNF]":("3".equals(unit)?"Produced Pieces [TNF]":""))),
				collection, PlotOrientation.VERTICAL, true, true, false);

		//graphical modifications for BarChart
		barChart.setBackgroundPaint(Color.white);
		CategoryPlot plot = barChart.getCategoryPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0));
		plot.setShadowGenerator(null);

		//Barmodifications
		BarRenderer renderer=(BarRenderer) plot.getRenderer();
		renderer.setBarPainter(new StandardBarPainter());
		renderer.setItemMargin(0);
		renderer.setShadowVisible(false);
		plot.setRenderer(renderer);

		return barChart;

	}

	private TimeSeriesCollection createForecastCollection(String sYear,String plantId,String method,String units){

		int year=0;
		try{
			year=Integer.parseInt(sYear);
		}catch(NumberFormatException e){
			return new TimeSeriesCollection();
		}

		TimeSeriesCollection collection=new TimeSeriesCollection();

		double[] p={100,102,105,107,108,108.5,110,110.5,109,107,106,106.5};

		TimeSeries planned=new TimeSeries("Planned");
		for(int i=0;i<12;i++){
			planned.add(new Month((i+1),year),p[i]);	
		}
		collection.addSeries(planned);

		TimeSeries is=new TimeSeries("Is");
		for(int i=0;i<4;i++){
			is.add(new Month((i+1),year),p[i]-(3+i*0.5));
		}
		collection.addSeries(is);

		TimeSeries forecast=new TimeSeries("Forecast");
		for(int i=3;i<12;i++){
			forecast.add(new Month((i+1),year),p[i]-(3+i*0.5));
		}
		collection.addSeries(forecast);
		return collection;
	}

	private JFreeChart createForecastChart(final TimeSeriesCollection collection, String time,String unit){

		// Modification of X-Axis Label
		int day = Integer.parseInt(time.substring(8,10));
		int month = Integer.parseInt(time.substring(5,7));
		int year = Integer.parseInt(time.substring(0,4));
		//get Weekday
		Calendar c = Calendar.getInstance();
		c.set(year, month - 1, day, 0, 0);
		int weekDay = c.get(Calendar.DAY_OF_WEEK);

		String dayString = new DateFormatSymbols(Locale.US).getWeekdays()[weekDay] + ", " + day + ". ";
		String monthString = new DateFormatSymbols(Locale.US).getMonths()[month -1];
		String xAxisLabel = "" + dayString +   monthString + "  " +  time.substring(0,4);

		//Creation of the lineChart
		JFreeChart lineChart = ChartFactory.createTimeSeriesChart(
				"Forecast",              	// title
				xAxisLabel,             	// x-axis label
				//				"Energy Consumption "+("1".equals(unit)?"[kWh]":("2".equals(unit)?"[kWh/TNF]":("3".equals(unit)?"[TNF]":""))),       // y-axis label
				("1".equals(unit)?"Energy Consumption [kWh]":("2".equals(unit)?"Energy Consumption [kWh/TNF]":("3".equals(unit)?"Produced Pieces [TNF]":""))),
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


		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(){
			private static final long serialVersionUID = 1L;
//			Stroke soild = new BasicStroke(2.0f);
			Stroke dashed =  new BasicStroke(1.0f,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] {10.0f}, 0.0f);
			@Override
			public Stroke getItemStroke(int row, int column) {
				//third series in collection -> forecast collection
				if (row == 2){
					return dashed;
					//partial dashed->not needed now, maybe later

//					double x = collection.getXValue(row, column);
//					
//					if ( x > 4){
//						return dashed;
//					} else {
//						return soild;
//					} 
				} else
					return super.getItemStroke(row, column);
			}
		};
		renderer.setBaseShapesVisible(false);
		renderer.setBaseShapesFilled(true);
		renderer.setBaseStroke(new BasicStroke(3));
		plot.setRenderer(renderer);

		return lineChart;
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