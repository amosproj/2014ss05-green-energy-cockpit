package de.fau.amos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

public class PlanningPreset {
	
	private static String selectedPlant = null;
	private static String selectedYear = null;
	private static int percentageChange = 0;
	private static int selectedPrecision = 1;
	private static ArrayList<ArrayList<Double>> valuesOfInputFields = new ArrayList<ArrayList<Double>>();
	private static HttpServletRequest currentRequest = null;
	
	private static Double roundToDigits(double value){
		double factor = Math.pow(10, selectedPrecision);
		return ((double)Math.round(value * factor)) / factor;
	}
	

	private static Double modifyIfNegativeValueToZero(Double value){
		if (value < 0.0){
			value = 0.0;
		}
		return value;
	}
	
	private static ArrayList<Double> getFormatYearValues(int year, int plantID, int format, int globalPercentageChange){
		NumberFormat nf_in = NumberFormat.getNumberInstance(Locale.GERMANY);
		String startTime = year + ".01.01 00:00:00";
		String endTime = (year+1) + ".01.01 00:00:00";
		ResultSet rs = null;		
		try{		
			
			System.out.println("select sum(productiondata.amount), plant_id," 
		 			+ " date_trunc ('month', measure_time), product_id"
					+ " from controlpoints"
					+ " INNER JOIN productiondata"
					+ " ON controlpoints.controlpoints_id = productiondata.controlpoint_id"
					+ " where plant_id = " + plantID
					+ " AND product_id = " + format
					+ " AND measure_time >= '" + startTime
					+ "' AND measure_time < '" + endTime + "'"  
					+ " GROUP BY date_trunc, plant_id, product_id ORDER BY date_trunc asc;");
			
			
			rs=SQL.queryToResultSet("select sum(productiondata.amount), plant_id," 
		 			+ " date_trunc ('month', measure_time), product_id"
					+ " from controlpoints"
					+ " INNER JOIN productiondata"
					+ " ON controlpoints.controlpoints_id = productiondata.controlpoint_id"
					+ " where plant_id = " + plantID
					+ " AND product_id = " + format
					+ " AND measure_time >= '" + startTime
					+ "' AND measure_time < '" + endTime + "'"  
					+ " GROUP BY date_trunc, plant_id, product_id ORDER BY date_trunc asc;"			
			);
		}
			finally{
				
			}
		ArrayList<Double> prevYearValues = new ArrayList<Double>();
		if(globalPercentageChange < -100){
			globalPercentageChange = -100;
		}
		double globalPercentageFactor = 1.0 + ((double)globalPercentageChange/100);
		int i = 3;
		if(rs!=null){
			try{	
				prevYearValues.add(0,(double)year);
				prevYearValues.add(1,(double)plantID);
				prevYearValues.add(2,(double)format);
				if (rs.next()) {						
					do{	
						System.out.println("TESTPOINT: " + currentRequest.getParameter(format + "X" + i));
						//if value is modified manually in input field
						if(currentRequest.getParameter(format + "X" + i) != "" && currentRequest.getParameter(format + "X" + i) != null){
//							prevYearValues.add(i,roundToDigits(Double.parseDouble(currentRequest.getParameter(format + "X" + i++))));
							try {
								prevYearValues.add(i,modifyIfNegativeValueToZero(roundToDigits((nf_in.parse(currentRequest.getParameter(format + "X" + i++))).doubleValue())));
							} catch (ParseException e) {
								prevYearValues.add(i,modifyIfNegativeValueToZero(roundToDigits(Double.parseDouble(currentRequest.getParameter(format + "X" + i++)))));
								e.printStackTrace();
							}
							
						}else{ //if value is empty in input field --> Value from Previous year
							prevYearValues.add(i++,roundToDigits(rs.getDouble(1)*globalPercentageFactor));	
						}
					}while(rs.next());
					
				}else{ //fill ArrayList with 0.0 if values are not available 
					while(i<15){
						prevYearValues.add(i++,0.0);
					}
					if (format == 4){
						for (int j = 0; j<  prevYearValues.size();j++){
						}
					}
				}
			}catch (SQLException e) {
				System.err.println("SQL Exception at creation of Arraylist");
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

	public static String getProductNameFromID(int productID) throws SQLException{
		String result = "";
		ResultSet rs = null;
		try{		
				rs = SQL.queryToResultSet("SELECT product_name FROM products WHERE products_id = '" + productID + "';");
		}finally{}
		if(rs!=null){
			try{
				while (rs.next()) {						
					result += rs.getString(1);			
				}
			} catch (SQLException e) {
				System.err.println("SQL Exception when geting ProductNameFromID");
				e.printStackTrace();
			}
		}
		return result;
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
		
		currentRequest = request;

		
		
		ArrayList<ArrayList<String>> plants=SQL.query("select plants_id,plant_name from plants order by plants_id;");	
		
		String out = "";
		

		
		String checkedPlants = request.getParameter("plants");
		if(checkedPlants != null){
			selectedPlant = checkedPlants;
		}
		
		for(int i = 1; i<plants.size();i++){		
			out+= "<input type=\"radio\" name=\"plants\" value=\"" + plants.get(i).get(0) + "\"" + ((plants.get(i).get(0).equals(selectedPlant))?" checked" : "") + "> " + plants.get(i).get(1) + "<br>";		
		}
		
	
		String checkedYear = request.getParameter("selectYear");
		if(checkedYear != null){
			selectedYear = checkedYear;
		}
		String checkedPrecision = request.getParameter("selectedPrecision");
		if(checkedPrecision != null){
			try{
				selectedPrecision = Integer.parseInt(checkedPrecision);
			}catch(NumberFormatException e){
				System.err.println("Problem with precision adjustment: NumberFormatException by cast of '" + request.getParameter("selectedPrecision") + "'.");
			}
		}
		if(request.getParameter("percentageChange") != null && request.getParameter("percentageChange")!=""){
			try{
				percentageChange=Integer.parseInt(request.getParameter("percentageChange"));
			}catch(NumberFormatException e){
				System.err.println("Problem with global percentage adjustment: NumberFormatException by cast of '" + request.getParameter("percentageChange") + "'.");
			}
		}
		return out;
	}
	
	private static ArrayList<ArrayList<Double>> getPlanningData (int year, int plant, int globalPercentageChange){
		ArrayList<ArrayList<Double>> allPlanningDataList = new ArrayList<ArrayList<Double>>();		

		
		//get all formats (product_id) and save in ArrayList
		ArrayList<Integer> allFormatsList = getAllFormats();
		
		//create for each Format an ArrayList with monthly values (plus year, plant, productID in first three cells)
		for (int i = 0; i < allFormatsList.size();i++){
			int productID = allFormatsList.get(i);		
			ArrayList<Double> valueList = getFormatYearValues(year, plant, productID, globalPercentageChange);
			allPlanningDataList.add(i, valueList);		
		}
		
		//add Sum of monthly values of Result (Column Total)
		
		for (int i = 0; i < allPlanningDataList.size(); i++){
			Double sumOfRowValues = 0.0;			
			for (int j =3; j < allPlanningDataList.get(i).size() ; j++){
					sumOfRowValues += allPlanningDataList.get(i).get(j);
				
			}

			allPlanningDataList.get(i).add(roundToDigits(sumOfRowValues));
			
		}
		return allPlanningDataList;
	}
	
	private static ArrayList<Integer> getAllFormats(){		
		ArrayList<Integer> allFormatsList = new ArrayList<Integer>();
		ResultSet rs = null;	
		try{
			rs = SQL.queryToResultSet("SELECT products_id from products;");
		}
		finally{}
		if(rs!=null){
			try{
				while (rs.next()) {		
					allFormatsList.add(rs.getInt(1));			
				}
			} catch (SQLException e) {
				System.err.println("SQL Exception at creation of Arraylist");
				e.printStackTrace();
			}
		}
		return allFormatsList;
	}

	public static String getTable(){
		
		String[] months = new DateFormatSymbols(Locale.ENGLISH).getShortMonths();
		String out = " ";
		
		//First Row with titles
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
			out += "<td style=\"word-break:break-all;word-wrap:break-word\" >AVG kwh/TNF</td>";
			out += "<td style=\"word-break:break-all;word-wrap:break-word\" >kwh Planning</td>";
			out += "</tr>";
			
		//Rows per Format with values
			ArrayList<ArrayList<Double>> allData = getPlanningData(Integer.parseInt(selectedYear), Integer.parseInt(selectedPlant), percentageChange);
			for(int i = 0; i < allData.size() ;i++){
				ArrayList<Double> tempValueList = allData.get(i);
				if(tempValueList.get(15) != 0.0){	//display only Rows, where at least one value is available (Sum>0)
					out += "<tr>";				
					try {
						out += "<td style=\"word-break:break-all;word-wrap:break-word\" >"+ getProductNameFromID(tempValueList.get(2).intValue());
						//add percentage to Format Field
						if(percentageChange != 0){
							out += "</br> +" + percentageChange + "%";
						}
					} catch (SQLException e) {
						out += "<td style=\"word-break:break-all;word-wrap:break-word\" >" + tempValueList.get(2).intValue();
						e.printStackTrace();
					}
					for(int j = 3; j < tempValueList.size(); j++){
						String colorOfValue = "orange";
						if(currentRequest != null && currentRequest.getParameter(tempValueList.get(2).intValue() + "X" + j) !=""){
							colorOfValue = "red";
						}
						out += "<td style=\"word-break:break-all;word-wrap:break-word" + ((percentageChange!=0 && j+1 < tempValueList.size())? "; color:" +colorOfValue + "\"" :"\"") + ">"+ tempValueList.get(j);
						
						if(j+1 < tempValueList.size()){		//input field not for Sum of values
							out += "</br><input type=\"text\" ";
							out += "name = \"" + tempValueList.get(2).intValue() + "X" + j + "\" ";
							out += "size=\"1\" maxlength=\"3\"";
							if(currentRequest != null && currentRequest.getParameter(tempValueList.get(2).intValue() + "X" + j) !="" && currentRequest.getParameter(tempValueList.get(2).intValue() + "X" + j) != null){
								out+= " value = \"" + currentRequest.getParameter(tempValueList.get(2).intValue() + "X" + j) + "\"";
							}
							out += "></td>";
						}
					}
					
					out += "</tr>";
				}
			}
			//add Row with Sum Y-Values for each Month
			out+="<tr>";
			out+="<td>Sum</td>";
			Double sumPerMonth;
			for (int i = 3; i < 16;i++){
				sumPerMonth =  0.0;
				for (int j = 0; j < allData.size();j++){
					sumPerMonth += allData.get(j).get(i);
				}
				sumPerMonth = roundToDigits(sumPerMonth);
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >"+ sumPerMonth + "</td>";
			}
			
			out+="</tr>";
		}	
		return out;
	}
	
}