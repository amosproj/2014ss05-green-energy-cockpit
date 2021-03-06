/*
 * Copyright (c) 2014 by Sven Huprich, Dimitry Abb, Jakob H?bler, Cindy Wiebe, Ferdinand Niedermayer, Dirk Riehle, http://dirkriehle.com
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
	 * 
	 * Reads Information from request and returns respective chart (.png-type) in response
	 * 
	 * @param request Request from website. Contains info about what should be displayed in chart.
	 * @param response Contains .png-file with ready-to-use chart.
	 * 
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

	/**
	 * calls ChartRenderer.doGet() and forwards response and request.
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * 
	 * Creates TimeSeriesCollection by querying energy data from database.
	 * 
	 * @param granularity Accuracy of distinguishment of displayed values (Summarise data to hours, days, months, years).
	 * @param startTime Start of queried period.
	 * @param endTime End of queried period.
	 * @param sumOrAvg Shall values be added or averaged.
	 * @param groupParameters Controlpoints that are affected.
	 * @param unit Sets Unit (kWh or kWh/TNF)
	 * @return TimeSeriesCollection that privedes the basis for creation of a png-chart
	 */
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
									), rs.getDouble(1));					
							break;
						case "day":
							series.add(new Day(Integer.parseInt(rs.getString(2).substring(8,10)),
									Integer.parseInt(rs.getString(2).substring(5,7)),
									Integer.parseInt(rs.getString(2).substring(0,4))
									), rs.getDouble(1));					
							break;

						case "month":
							series.add(new Month(Integer.parseInt(rs.getString(2).substring(5,7)),
									Integer.parseInt(rs.getString(2).substring(0,4))
									), rs.getDouble(1));					
							break;

						case "year":
							series.add(new Year(Integer.parseInt(rs.getString(2).substring(0,4))
									), rs.getDouble(1));					
							break;

							//default: day
						default:
							series.add(new Day(Integer.parseInt(rs.getString(2).substring(8,10)),
									Integer.parseInt(rs.getString(2).substring(5,7)),
									Integer.parseInt(rs.getString(2).substring(0,4))
									), rs.getDouble(1));					

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

	/**
	 * Creates Chart with TimeLine (JFreeChart object) from TimeSeriesCollection. Is used when granularity is set to hours.
	 * Adjusts display of the chart, not the values itself.
	 * 
	 * @param collection TimeSeriesCollection that should be used as basis of chart.
	 * @param timeGranularity Selected granularity. Value is similar to granularity contained in TimeSeriesCollection "collection".
	 * @param time first element of time that is displayed. Is used for caption text and axis text.
	 * @param unit Unit of displayed values (kWh or kWh/TNF).
	 * @return Returns finished JFreeChart object.
	 */
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

	/**
	 * Creates Chart with Bars (JFreeChart object) from TimeSeriesCollection. Is used when granularity is set to days, months or years.
	 * Adjusts display of the chart, not the values itself.
	 * 
	 * @param collection TimeSeriesCollection that should be used as basis of chart.
	 * @param timeGranularity Selected granularity. Value is similar to granularity contained in TimeSeriesCollection "collection".
	 * @param time first element of time that is displayed. Is used for caption text and axis text.
	 * @param unit Unit of displayed values (kWh or kWh/TNF).
	 * @return Returns finished JFreeChart object.
	 */
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

	/**
	 * 
	 * Creates Dataset that provides the basis for a chart. Queries data from database. Is used when Chart Type "Location-Format" or "Format-Location" is selected.
	 * 
	 * @param startTime Start of queried period.
	 * @param endTime End of queried period.
	 * @param sumOrAvg Shall values be added or averaged.
	 * @param locationGroupParameters Controlpoints that are affected by query.
	 * @param formatGroupParameters Formats(=products) that are affected by query.
	 * @param unit Sets Unit (kWh or kWh/TNF).
	 * @param chartType Chart type: either "Location-Format" (2) or "Format-Location" (3).
	 * @return Returns dataset that provides basis for a JFreeChart.
	 */
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

	/**
	 * 
	 * (Deprecated) Creates Dataset that provides the basis for a chart. Queries data from database. Was used when Chart Type  "Format-Location" had been selected.
	 * 
	 * @param startTime Start of queried period.
	 * @param endTime End of queried period.
	 * @param sumOrAvg Shall values be added or averaged.
	 * @param locationGroupParameters Controlpoints that are affected by query.
	 * @param formatGroupParameters Formats(=products) that are affected by query.
	 * @param unit Sets Unit (kWh or kWh/TNF).
	 * @return Returns dataset that provides basis for a JFreeChart.
	 */
	@SuppressWarnings("unused")
	@Deprecated
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

	/**
	 * Creates chart (JFreeChart object) from dataset. Is used when Chart Type "Location-Format" or "Format-Location" is selected.
	 * 
	 * @param collection Collection that provides all data that should be displayed.
	 * @param unit Unit: kWh or kWh/TNF
	 * @return Returns finished JFreeChart object.
	 */
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

	/**
	 * 
	 * Creates TimeSeriesCollection that provides the basis for a Chart. Is used for forecast.
	 * 
	 * @param sYear Selected year.
	 * @param plantId Selected plant.
	 * @param method Is not used.
	 * @param units Is not used.
	 * @return Returns TimeSeriesCollection that provides the basis for a Chart.
	 */
	private TimeSeriesCollection createForecastCollection(String sYear,String plantId,String method,String units){

		int year=0;
		try{
			year=Integer.parseInt(sYear);
		}catch(NumberFormatException e){
			return new TimeSeriesCollection();
		}

		TimeSeriesCollection collection=new TimeSeriesCollection();

		
		/*
		 * get planned tnf
		 */
		TimeSeries planned=new TimeSeries("Planned");
		
		double lastYearKwhPerTnf=0;
		ResultSet rs=SQL.queryToResultSet(
				"select round((select sum(value)from measures where date_trunc('year',measure_time)='"
				+ (year-1)
				+ "-1-1')/(select sum(amount)from productiondata "
				+ "inner join controlpoints on productiondata.controlpoint_id=controlpoints.controlpoints_id "
				+ "where date_trunc('year',measure_time)='"
				+ (year-1)
				+ "-1-1' AND plant_id='"
				+ plantId
				+ "'),4);"
				);
		
		if(rs!=null){
			try {
				if(rs.next()){
					lastYearKwhPerTnf=rs.getDouble(1);
				}
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		String months="";
		for(int i=1;i<=12;i++){
			months+="sum(m_"+i+")";
			if(i!=12){
				months+=", ";
			}
		}
		
		rs=SQL.queryToResultSet(
				
				"select "+months+" from planning_values where planning_year='"
						+ year+"' AND plant_id='"+plantId+"';"
				);
		
		if(rs!=null){
			try {
				while(rs.next()){
					for(int i=1;i<=12;i++){
						planned.add(new Month((i),year),rs.getDouble(i)/(lastYearKwhPerTnf!=0?lastYearKwhPerTnf:1));	
					}
					
				}
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		collection.addSeries(planned);

		TimeSeries is=new TimeSeries("Is");
		int passedMonths=0;
		double lastVal=0;
		rs=SQL.queryToResultSet(
				
				"select * from (select round((select sum(am) from(select sum(amount)as am,date_trunc('month',measure_time)as zeit "
				+ "from productiondata inner join controlpoints on productiondata.controlpoint_id=controlpoints.controlpoints_id "
				+ "where productiondata.measure_time >= '"+year+"-01-01 00:00:00' AND productiondata.measure_time < '"+(year+1)+"-01-01 00:00:00' "
				+ "AND reference_point='t' AND plant_id='"+plantId+"' group by measure_time)as wat where zeit=gruppenZeit group by zeit order by zeit),4), "
				+ "gruppenZeit from(select sum(wert) as gruppenWert,control_point_name, zeit1 as gruppenZeit from("
				+ "select sum(value)as wert,control_point_name,date_trunc('month',measure_time)as zeit1 from measures inner join controlpoints "
				+ "on measures.controlpoint_id=controlpoints.controlpoints_id where measure_time >= '"+year+"-01-01 00:00:00' AND measure_time < '"
				+ (year+1)+"-01-01 00:00:00' AND plant_id='"+plantId+"' group by measure_time,control_point_name)as data group by zeit1,control_point_name) "
				+ "as groupedByTime group by gruppenZeit)as result order by gruppenZeit;"
				);
		if(rs!=null){
			try {
				while(rs.next()){
					passedMonths++;
					lastVal=rs.getDouble(1)/(lastYearKwhPerTnf!=0?lastYearKwhPerTnf:1);
					is.add(new Month((passedMonths),year),lastVal);				
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		collection.addSeries(is);

		TimeSeries forecast=new TimeSeries("Forecast");
		
		if(passedMonths!=0){
		
			forecast.add(new Month(passedMonths,year),lastVal);
		}
		passedMonths++;
		
		double factor=calculateDifferenz(planned,is);
		
		for(int i=passedMonths;i<=12;i++){
			forecast.add(new Month((i),year),planned.getValue(i-1).doubleValue()*factor);
		}
		collection.addSeries(forecast);
		return collection;
	}
	
	/**
	 * Calculates difference between actual date and date the forecast ends (end of year). Is used for forecast.
	 * 
	 * @param planned Timeperiod of forecast.
	 * @param is Timeperiod until real values exist.
	 * @return Returns number of time elements between the end of the two timeperiods.
	 */
	private double calculateDifferenz(TimeSeries planned,TimeSeries is){
		double factor=0;
		
		int invalidValues=0;
		for(int i=0;i<is.getItemCount();i++){
			if(planned.getItemCount()>i&&planned.getValue(i).doubleValue()!=0){
				factor+=is.getValue(i).doubleValue()/planned.getValue(i).doubleValue();
			}else{
				invalidValues++;
			}
		}
		if(is.getItemCount()!=0&&is.getItemCount()-invalidValues!=0){
				factor/=(is.getItemCount()-invalidValues);
		}else{
			factor=1;
		}
		return factor;
	}

	/**
	 * Creates Chart (JFreeChart object) using TimeSeriesCollection. Used for forecast only.
	 * 
	 * @param collection TimeSeriesCollection that provides basis for chart.
	 * @param time Time where "real" data ends.
	 * @param unit Unit of displayed values (kWh,TNF,kWh/TNF)
	 * @return Returns finished JFreeChart.
	 */
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

	/**
	 * Returns String (word that can be used for database query) depending on transmitted (numeral String) granularity.
	 * 
	 * @param timeGranularity Numeral String of granularity.
	 * @return Word String of granularity.
	 */
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

	/**
	 * Returns String (word that can be used for database query) depending on transmitted (numeral String) countType.
	 * 
	 * @param countType Numeral String of countType.
	 * @return Word String of countType.
	 */
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

	/**
	 * Prepares transmitted String with group parameters to be split up into its components.
	 * 
	 * @param in Input String.
	 * @return Output String.
	 */
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