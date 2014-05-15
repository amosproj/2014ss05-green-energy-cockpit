package de.fau.amos;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.RenderedImage;
import java.io.IOException;
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
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
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

		//parameter from url
		String val1=request.getParameter("param1");
		String startDay=request.getParameter("startDay");
		String startMonth=request.getParameter("startMonth");
		String startYear=request.getParameter("startYear");
		String endDay=request.getParameter("endDay");
		String endMonth=request.getParameter("endMonth");
		String endYear=request.getParameter("endYear");
		String granularity = request.getParameter("granularity");
		String countType = request.getParameter("countType");
		
		//Create Timestamp for SQL-Query (Start- and End-Date)
		String startTime = TimestampConversion.convertTimestamp(0, 0, Integer.parseInt(startDay), Integer.parseInt(startMonth), Integer.parseInt(startYear));
		String endTime = TimestampConversion.convertTimestamp(0, 0, Integer.parseInt(endDay), Integer.parseInt(endMonth), Integer.parseInt(endYear));
				
	    
		int intCountType=0;
		try{
			intCountType=Integer.parseInt(countType);
		}catch(NumberFormatException e){
			intCountType=0;
		}
		switch(intCountType){
		case 0:
			countType = "avg";
			break;
		case 1:
			countType = "sum";
			break;
		}
			
			
		int intGranularity=0;
		try{
			intGranularity=Integer.parseInt(granularity);
		}catch(NumberFormatException e){
			intGranularity=2;
		}
		switch(intGranularity){
		case 0:
			granularity = "hour";
			break;
		case 1:
			granularity = "day";
			break;
		case 2:
			granularity = "month";
			break;
		case 3:
			granularity = "year";
			break;
		}
		
		//createDataset
		DefaultCategoryDataset defaultDataset = new DefaultCategoryDataset();
		
		//get data for parameters
		getAllData(defaultDataset, granularity, startTime, endTime, countType);
		
		
		
		System.out.println("search for "+startDay+"."+startMonth+"."+startYear+" to "+endDay+"."+endMonth+"."+endYear+ " Granularity: " + granularity);
		
		
		
		
		
//		defaultDataset.addValue(76, "Wert1", "Sandeep");
//		defaultDataset.addValue(30, "Wert1", "Sangeeta");
//		defaultDataset.addValue(50, "Wert1", "Surabhi");
//		defaultDataset.addValue(20, "Wert1", "Sumanta");
//		defaultDataset.addValue(10, "Wert2", "Sandeep");
//		defaultDataset.addValue(90, "Wert2", "Sangeeta");
//		defaultDataset.addValue(23, "Wert2", "Surabhi");
//		defaultDataset.addValue(87, "Wert2", "Sumanta");

		 DefaultPieDataset pieDataset = new DefaultPieDataset();
	        pieDataset.setValue("One", new Double(43.2));
	        pieDataset.setValue("Two", new Double(10.0));
	        pieDataset.setValue("Three", new Double(27.5));
	        pieDataset.setValue("Four", new Double(17.5));
	        pieDataset.setValue("Five", new Double(11.0));
	        pieDataset.setValue("Six", new Double(19.4));
		
		int type=0;
		try{
			type=Integer.parseInt(val1);
		}catch(NumberFormatException e){
			type=0;
		}
		
		JFreeChart chart=null;
		
		switch(type){
		case 1:
			chart = ChartFactory.createAreaChart("Area Chart", "",
					"Value", defaultDataset, PlotOrientation.VERTICAL, true, true, false);
			break;
		case 2:
			chart = ChartFactory.createBarChart("Bar Chart", "",
				"Value", defaultDataset, PlotOrientation.VERTICAL, true, true, false);
			
			String fontName = "Lucida Sans";
			StandardChartTheme theme = (StandardChartTheme)org.jfree.chart.StandardChartTheme.createJFreeTheme();

		    theme.setTitlePaint( Color.decode( "#4572a7" ) );
		    theme.setExtraLargeFont( new Font(fontName,Font.PLAIN, 16) ); //title
		    theme.setLargeFont( new Font(fontName,Font.BOLD, 15)); //axis-title
		    theme.setRegularFont( new Font(fontName,Font.PLAIN, 11));
		    theme.setRangeGridlinePaint( Color.decode("#C0C0C0"));
		    theme.setPlotBackgroundPaint( Color.white );
		    theme.setChartBackgroundPaint( Color.white );
		    theme.setGridBandPaint( Color.red );
		    theme.setAxisOffset( new RectangleInsets(0,0,0,0) );
		    theme.setBarPainter(new StandardBarPainter());
		    theme.setAxisLabelPaint( Color.decode("#666666")  );
		    theme.apply( chart );
		    chart.getCategoryPlot().setOutlineVisible( false );
		    chart.getCategoryPlot().getRangeAxis().setAxisLineVisible( false );
		    chart.getCategoryPlot().getRangeAxis().setTickMarksVisible( false );
		    chart.getCategoryPlot().setRangeGridlineStroke( new BasicStroke() );
		    chart.getCategoryPlot().getRangeAxis().setTickLabelPaint( Color.decode("#666666") );
		    chart.getCategoryPlot().getDomainAxis().setTickLabelPaint( Color.decode("#666666") );
		    chart.setTextAntiAlias( true );
		    chart.setAntiAlias( true );
		    chart.getCategoryPlot().getRenderer().setSeriesPaint( 0, Color.decode( "#4572a7" ));
		    BarRenderer rend = (BarRenderer) chart.getCategoryPlot().getRenderer();
		    rend.setShadowVisible( true );
		    rend.setShadowXOffset( 2 );
		    rend.setShadowYOffset( 0 );
		    rend.setShadowPaint( Color.decode( "#C0C0C0"));
		    rend.setMaximumBarWidth( 0.1);
//		    rend.set
			break;
		case 3:
			chart = ChartFactory.createBarChart("Bar Chart 3D", "",
				"Value", defaultDataset, PlotOrientation.VERTICAL, true, true, false);
			break;
		case 4:
			chart = ChartFactory.createLineChart("Line Chart", "",
					"Value", defaultDataset, PlotOrientation.VERTICAL, true, true, false);
				break;
		case 5:
			chart = ChartFactory.createPieChart("Pie Chart", pieDataset, true, true, false);
			break;
		case 6:
			chart = new ChartJSPTest(null,null,null,null).getChart();
			break;
	
		}
		
		RenderedImage chartImage = chart.createBufferedImage(870, 500);
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

	}
	
	
	
	private void getAllData(DefaultCategoryDataset data, String granularity, String startTime, String endTime, String countType){
		System.out.println("--> send SQL Query: select * from (select round(avg(wert), 4),control_point_name,zeit from (select "+ countType +"(value)as wert,control_point_name,date_trunc('" + granularity + "',measure_time)as zeit from measures inner join controlpoints on measures.controlpoint_id=controlpoints.controlpoints_id where measure_time >= '"+ startTime + "' AND measure_time < '" + endTime + "' group by measure_time,control_point_name) as tmp group by zeit,control_point_name)as unsorted order by zeit,control_point_name;");
		for(int i=0;i<4;i++){
			ArrayList<ArrayList<String>> ret=SQL.querry(
					//"select control_point_name,value from controlpoints,measures where controlpoint_id='"+(i+1)+"' and controlpoint_id=controlpoints_id;");
					"select * from (select round("+ countType +"(wert), 4),control_point_name,zeit from (select sum(value)as wert,control_point_name,date_trunc('" + granularity + "',measure_time)as zeit from measures inner join controlpoints on measures.controlpoint_id=controlpoints.controlpoints_id where measure_time >= '"+ startTime + "' AND measure_time < '" + endTime + "' group by measure_time,control_point_name) as tmp group by zeit,control_point_name)as unsorted order by zeit,control_point_name;");
			for(int j=1;j<ret.size();j++){
				data.addValue(Double.parseDouble(ret.get(j).get(0)),
						ret.get(j).get(1),
						ret.get(j).get(2));
			}
		}
	}
}
