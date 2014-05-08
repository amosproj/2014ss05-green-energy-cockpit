package de.fau.amos;

import java.awt.image.RenderedImage;
import java.io.IOException;

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

public class ChartSven extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ChartSven() {
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
		defaultDataset.addValue(76, "Wert1", "Sandeep");
		defaultDataset.addValue(30, "Wert1", "Sangeeta");
		defaultDataset.addValue(50, "Wert1", "Surabhi");
		defaultDataset.addValue(20, "Wert1", "Sumanta");
		defaultDataset.addValue(10, "Wert2", "Sandeep");
		defaultDataset.addValue(90, "Wert2", "Sangeeta");
		defaultDataset.addValue(23, "Wert2", "Surabhi");
		defaultDataset.addValue(87, "Wert2", "Sumanta");

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
}
