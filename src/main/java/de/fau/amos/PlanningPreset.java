package de.fau.amos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

public class PlanningPreset {
	
	private static String selectedPlant = null;
	private static String selectedYear = null;
	private static int percentageChange = 0;
	
	
	public static ArrayList<Double> getPrevYearValues(int year, String plantID, int format){
		String startTime = year + ".01.01 00:00:00";
		String endTime = (year+1) + ".01.01 00:00:00";
		ResultSet rs = null;		
		try{
			rs=SQL.queryToResultSet("select controlpoints.controlpoints_id,sum(productiondata.amount), plant_id," 
		 			+ "date_trunc ('month', measure_time), product_id"
					+ "from controlpoints "
					+ "INNER JOIN productiondata "
					+ "ON controlpoints.controlpoints_id = productiondata.controlpoint_id "
					+ "where plant_id = " + Integer.parseInt(plantID)
					+ "AND product_id = " + format
					+ "AND measure_time >= '" + startTime
					+ "' AND measure_time < '" + endTime + "'"  
					+ "GROUP BY date_trunc,controlpoints.controlpoints_id, product_id;"			
			);
		}finally{
			
		}
		ArrayList<Double> prevYearValues = new ArrayList<Double>();
		if(rs!=null){
			try{
				while (rs.next()) {						
					prevYearValues.add(rs.getDouble(1));			
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return prevYearValues;
	}
	
	
	public static ArrayList<String> getProductNames(){		
		ArrayList<ArrayList<String>> pn = SQL.query("SELECT product_name FROM products;");
		
		ArrayList<String> products = new ArrayList<String>();
		
		for(int i=1; i<pn.size();i++){
			products.add(pn.get(i).get(0));
		}	
		return products;
	}
	
	public static ArrayList<String> getPlants(){
		ArrayList<ArrayList<String>> p =SQL.query("SELECT plants_id,plant_name FROM plants ORDER BY plants_id;");
		ArrayList<String> plants = new ArrayList<String>();
		
		for(int i = 1; i<p.size();i++){
			plants.add(p.get(i).get(1));
		}
		return plants;
	}
	
	
	public static String LocationSelection(HttpServletRequest request){
		ArrayList<ArrayList<String>> plants=SQL.query("select plants_id,plant_name from plants order by plants_id;");	
		
		String out = "";
		
		for(int i = 1; i<plants.size();i++){		
			out+= "<input type=\"radio\" name=\"plants\" value=\"" + plants.get(i).get(0) + "\"> " + plants.get(i).get(1) + "<br>";		
		}
		
		String checkedPlants = request.getParameter("plants");
		if(checkedPlants != null){
			selectedPlant = checkedPlants;
		}	
		String checkedYear = request.getParameter("selectYear");
		if(checkedYear != null){
			selectedYear = checkedYear;
		}
		if(request.getParameter("percentageChange") == null){
			percentageChange = 0;
		}else{
			int change = Integer.parseInt(request.getParameter("percentageChange"));
			if(change != 0){
				percentageChange = change;
			}
		}
		//System.out.println(selectedYear + " " + selectedPlant + " " + percentageChange );
		return out;
	}
	
	
	
	
	public static String getTable(){
		
		String[] months = new DateFormatSymbols(Locale.ENGLISH).getMonths();
		
		String out = " ";
		
		ArrayList<Integer> test = new ArrayList<Integer>();
		test.add(83);
		test.add(123);
		test.add(113);
		test.add(100);
		test.add(150);
		test.add(111);
		test.add(96);
		test.add(98);
		test.add(101);
		test.add(50);
		test.add(90);
		test.add(88);
		
		if(percentageChange != 0){
			if(selectedPlant != null && selectedYear != null){
				out += "<table border cellpadding=\"3\" rules=\"all\" id= \"dataTable\" >";
				out += "<tr>";
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" > Format </td>";
				for(int i=0;i<months.length-1;i++){
					out += "<td style=\"word-break:break-all;word-wrap:break-word\" >";
					out += months[i];
					out +="</td>";
				}
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >Total</td>";
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >Average Energy Usage/TNF</td>";
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >Energy Usage Planning</td>";
				out += "</tr>";
				
				for(int i = 0; i < getProductNames().size();i++){
					out += "<tr>";
					out += "<td style=\"word-break:break-all;word-wrap:break-word\" >";
					out += getProductNames().get(i);
					out += "</td>";
					
					if(getPrevYearValues(Integer.parseInt(selectedYear), selectedPlant, i+1).size() != 0){
						for(int j = 0; j < getPrevYearValues(Integer.parseInt(selectedYear), selectedPlant, i+1).size();j++){
							out += "<td style=\"word-break:break-all;word-wrap:break-word\" >";
							out += String.valueOf(getPrevYearValues(Integer.parseInt(selectedYear), selectedPlant, i+1).get(j)) + "<br>";
							out += "<input type=\"text\" size=\"1\" maxlength=\"3\">";
							out += "</td>";
						}
					}else{
						for(int j = 0; j < 12;j++){
							out += "<td style=\"word-break:break-all;word-wrap:break-word\" >";
							out += "so much?" + "<br>";
							out += "<input type=\"text\" size=\"1\" maxlength=\"3\">";
							out += "</td>";
						}
					}					
					out += "</tr>";
				}
				out += "</table>";
				
				out += "<table border cellpadding=\"7\" rules=\"all\" id= \"dataTable\" >";
				out += "<tr>";
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >Total Energy Usage New</td>";
				out += "</tr>";
				out += "<tr>";
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >Total Energy Usage Old</td>";
				out += "</tr>";
				out += "</table>";
			}else{
				out += "<table border cellpadding=\"3\" rules=\"all\" id= \"dataTable\" >";
				out += "<tr>";
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >Format</td>";
				for(int i=0;i<months.length-1;i++){
					out += "<td style=\"word-break:break-all;word-wrap:break-word\" >";
					out += months[i];
					out +="</td>";
				}
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >Total</td>";
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >Average Energy Usage/TNF</td>";
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >Energy Usage Planning</td>";
				out += "</tr>";
			}		
		}else{
			if(selectedPlant != null && selectedYear != null){
				out += "<table border cellpadding=\"3\" rules=\"all\" id= \"dataTable\" >";
				out += "<tr>";
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >Format</td>";
				for(int i=0;i<months.length-1;i++){
					out += "<td style=\"word-break:break-all;word-wrap:break-word\" >";
					out += months[i];
					out +="</td>";
				}
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >Total</td>";
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >Average Energy Usage/TNF</td>";
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >Energy Usage Planning</td>";
				out += "</tr>";
				
				for(int i = 0; i < getProductNames().size();i++){
					out += "<tr>";
					out += "<td style=\"word-break:break-all;word-wrap:break-word\" >";
					out += getProductNames().get(i);
					out += "</td>";
					
					if(getPrevYearValues(Integer.parseInt(selectedYear), selectedPlant, i+1).size() != 0){
						for(int j = 0; j < getPrevYearValues(Integer.parseInt(selectedYear), selectedPlant, i+1).size();j++){
							out += "<td style=\"word-break:break-all;word-wrap:break-word\" >";
							out += String.valueOf(getPrevYearValues(Integer.parseInt(selectedYear), selectedPlant, i+1).get(j)) + "<br>";
							out += "<input type=\"text\" size=\"1\" maxlength=\"3\">";
							out += "</td>";
						}
					}else{
						for(int j = 0; j < 12;j++){
							out += "<td style=\"word-break:break-all;word-wrap:break-word\" >";
							out += "no data yet" + "<br>";
							out += "<input type=\"text\" size=\"1\" maxlength=\"3\">";
							out += "</td>";
						}
					}					
					out += "</tr>";
				}
				out += "</table>";
				
				out += "<table border cellpadding=\"7\" rules=\"all\" id= \"dataTable\" >";
				out += "<tr>";
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >Total Energy Usage New</td>";
				out += "</tr>";
				out += "<tr>";
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >Total Energy Usage Old</td>";
				out += "</tr>";
				out += "</table>";
			}else{
				out += "<table border cellpadding=\"3\" rules=\"all\" id= \"dataTable\" >";
				out += "<tr>";
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >Format</td>";
				for(int i=0;i<months.length-1;i++){
					out += "<td style=\"word-break:break-all;word-wrap:break-word\" >";
					out += months[i];
					out +="</td>";
				}
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >Total</td>";
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >Average Energy Usage/TNF</td>";
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >Energy Usage Planning</td>";
				out += "</tr>";
			}		
		}
		
			return out;
	}
}