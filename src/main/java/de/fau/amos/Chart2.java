package de.fau.amos;

import java.io.IOException;
import java.sql.SQLException;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;

public class Chart2 {
	public static void main(String[] args) throws SQLException, IOException {

		// von http://jdbc.postgresql.org/documentation/head/connect.html
		// http://www.math.hu-berlin.de/~ccafm/lehre_BZQ_Numerik/allg/JAVA_Pakete/JFreeChart/JFreeChart-Tutorial.html#Schritt1
//		new createDataset("dvdrental", "messstelle_1", "postgres", "bgtbgt");
		ChartJSPTest tempChart = new ChartJSPTest("dvdrental", "messstelle_1", "postgres", "bgtbgt");
		JFreeChart chart = tempChart.getChart();
		
		//Fenster erstellen
		ApplicationFrame punkteframe = new ApplicationFrame("Punkte");
		ChartPanel chartPanel2 = new ChartPanel(chart);
		punkteframe.setContentPane(chartPanel2);
		punkteframe.pack();
		punkteframe.setVisible(true);
	}

}
