package de.fau.amos;

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
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

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

		
		
		DefaultCategoryDataset defaultDataset = new DefaultCategoryDataset();
		getAllData(defaultDataset);
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
		
		String val1=request.getParameter("param1");
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
			break;
		case 3:
			chart = ChartFactory.createBarChart3D("Bar Chart 3D", "",
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
		
		RenderedImage chartImage = chart.createBufferedImage(500, 300);
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
	
	private void getAllData(DefaultCategoryDataset data){
		for(int i=0;i<4;i++){
			ArrayList<ArrayList<String>> ret=SQL.querry(
					//"select control_point_name,value from controlpoints,measures where controlpoint_id='"+(i+1)+"' and controlpoint_id=controlpoints_id;");
					"select * from (select sum(wert),control_point_name,zeit from (select sum(value)as wert,control_point_name,date_trunc('day',measure_time)as zeit from measures inner join controlpoints on measures.controlpoint_id=controlpoints.controlpoints_id group by measure_time,control_point_name) as tmp group by zeit,control_point_name)as unsorted order by zeit,control_point_name;");
			for(int j=1;j<ret.size();j++){
				data.addValue(Double.parseDouble(ret.get(j).get(0)),
						ret.get(j).get(1),
						ret.get(j).get(2));
			}
		}
	}
}
