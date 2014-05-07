package de.fau.amos;

import java.awt.image.RenderedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

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

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(76, "Java", "Sandeep");
		dataset.addValue(30, "Java", "Sangeeta");
		dataset.addValue(50, "Java", "Surabhi");
		dataset.addValue(20, "Java", "Sumanta");
		dataset.addValue(10, "Oracle", "Sandeep");
		dataset.addValue(90, "Oracle", "Sangeeta");
		dataset.addValue(23, "Oracle", "Surabhi");
		dataset.addValue(87, "Oracle", "Sumanta");

		String val1=request.getParameter("param1");
		JFreeChart chart = ChartFactory.createAreaChart("Area Chart", "called chartarea with: "+val1,
				"Value", dataset, PlotOrientation.VERTICAL, true, true, false);

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
