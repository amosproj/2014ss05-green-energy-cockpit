package de.fau.amos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class PlanningPreset {
	
	private static int selectedPlant = 0;
	private static int selectedYear = 0;
	private static int percentageChange = 0;
	private static int selectedPrecision = 1;
	private static HttpServletRequest currentRequest = null;
	private static HttpSession currentSession = null;
	private static boolean isReset = false;
	private static String loadedSave = null;
	private static ArrayList<ArrayList<Double>> loadedData = new ArrayList<ArrayList<Double>>();

	
	public static void setValues(HttpServletRequest request, HttpSession session){
		//Set isReset
		if(request.getParameter("reset") != null){
			isReset = true;
		}else{
			isReset = false;
		}
		
		//set current Request and Session
		currentRequest = request;
		currentSession = session;
		
		//set Year
		String checkedYear = request.getParameter("selectYear");
		if(checkedYear != null){
			selectedYear = Integer.parseInt(checkedYear);
		}
		//set Plant
		String checkedPlant = request.getParameter("plants");
		if(checkedPlant != null){
			selectedYear = Integer.parseInt(checkedYear);
		}
		
		//set Precision
		String checkedPrecision = request.getParameter("selectedPrecision");
		if(checkedPrecision != null){
			try{
				selectedPrecision = Integer.parseInt(checkedPrecision);
			}catch(NumberFormatException e){
				System.err.println("Problem with precision adjustment: NumberFormatException by cast of '" + request.getParameter("selectedPrecision") + "'.");
			}
		}
		
		//set global Percentage Change
		if(request.getParameter("percentageChange") != null && request.getParameter("percentageChange")!=""){
			try{
				percentageChange=Integer.parseInt(request.getParameter("percentageChange"));
			}catch(NumberFormatException e){
				System.err.println("Problem with global percentage adjustment: NumberFormatException by cast of '" + request.getParameter("percentageChange") + "'.");
			}
		}
		
		//if save Planning Data
		if(request.getParameter("saveInput") != null){
			saveValuesToDatabase();
		}
		
		//if Load Data
		if(request.getParameter("savedData") != null){
			loadedSave = request.getParameter("savedData");
			getSavedPlanningValues();
			//get Percentage Change
			ResultSet rs = SQL.queryToResultSet("SELECT global_change FROM planning_cockpit WHERE planning_year = '" + selectedYear + "' AND plant_id = '" + selectedPlant + "';");
			if(rs!=null){
				try{
					while (rs.next()) {	
						percentageChange = (int)stringNegativeNumberToDouble(rs.getString(1));
					}
				} catch (SQLException e) {
					System.err.println("SQL Exception when querying Data from planning_cockpit");
					e.printStackTrace();
				}
			}	
		}
		System.out.println("LOADED Save:" + loadedSave);
		//if isDelete
		if(request.getParameter("Delete") != null){
			deleteSaveFromDB();
		}
		
	}
		
	public static String LocationSelection(HttpServletRequest request){
		
		ArrayList<ArrayList<String>> plants=SQL.query("select plants_id,plant_name from plants order by plants_id;");			
		String out = "";		
		String checkedPlants = request.getParameter("plants");
		if(checkedPlants != null){
			selectedPlant = Integer.parseInt(checkedPlants);
		}		
		for(int i = 1; i<plants.size();i++){		
			out+= "<input type=\"radio\" name=\"plants\" value=\"" + plants.get(i).get(0) + "\"" + ((plants.get(i).get(0).equals(Integer.toString(selectedPlant)))?" checked" : "") + "> " + plants.get(i).get(1) + "<br>";		
		}
		return out;
	}

	private static boolean checkIfStringIsNumber(String checkString){
		if(checkString == null){
			return false;
		}
		int numberOfSigns = 0;
		for (int i = 0; i < checkString.length(); i++){
			if(!(Character.isDigit(checkString.charAt(i)) || (checkString.charAt(i) == ',' || checkString.charAt(i) == '.'))){				
			return false;
			}
			//check amount of signs (, and .)
			if((checkString.charAt(i) == ',' || checkString.charAt(i) == '.')){
				numberOfSigns++;
			}
		}
		if(numberOfSigns>1){
			return false;
		}
		return true;
	}
	
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
		
	public static ArrayList<String> getPlants(){
		ArrayList<ArrayList<String>> p =SQL.query("SELECT plants_id,plant_name FROM plants ORDER BY plants_id;");
		ArrayList<String> plants = new ArrayList<String>();
		
		for(int i = 1; i<p.size();i++){
			plants.add(p.get(i).get(1));
		}
		return plants;
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
	
	public static String getPlantNameFromID(int plantID) throws SQLException{
		String result = "";
		ResultSet rs = SQL.queryToResultSet("SELECT plant_name FROM plants WHERE plants_id = '" + plantID + "';");
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
	
	private static double stringNumberToDouble(String value){
		Double result = 0.0;
		if (value == null){
			return result;
		}
		if(value.contains(",")){
			NumberFormat nf_in = NumberFormat.getNumberInstance(Locale.GERMANY);
			try {
				result = modifyIfNegativeValueToZero(roundToDigits((nf_in.parse(value)).doubleValue()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			result = modifyIfNegativeValueToZero(roundToDigits(Double.parseDouble(value)));
		}		
		return result;
	}	
	
	private static double stringNegativeNumberToDouble(String value){
		Double result = 0.0;
		if (value == null){
			return result;
		}
		if(value.contains(",")){
			NumberFormat nf_in = NumberFormat.getNumberInstance(Locale.GERMANY);
			try {
				result = roundToDigits((nf_in.parse(value)).doubleValue());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			result = roundToDigits(Double.parseDouble(value));
		}		
		return result;
	}
	
	private static ArrayList<Double> getFormatYearValues(int year, int plantID, int format, int globalPercentageChange){

		String startTime = (year-1) + ".01.01 00:00:00";
		String endTime = year + ".01.01 00:00:00";
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
		ArrayList<Double> formatYearValues = new ArrayList<Double>();
		if(globalPercentageChange < -100){
			globalPercentageChange = -100;
		}
		double globalPercentageFactor = 1.0 + ((double)globalPercentageChange/100);
		int i = 3;
		if(rs!=null){
			try{	
				formatYearValues.add(0,(double)year);
				formatYearValues.add(1,(double)plantID);
				formatYearValues.add(2,(double)format);
				if (rs.next()) {						
					do{	
						//if value is modified manually in input field and is a number
						if(!isReset && currentRequest.getParameter(format + "X" + i) != null 
								&& checkIfStringIsNumber(currentRequest.getParameter(format + "X" + i)) 
								&& currentRequest.getParameter(format + "X" + i) != "" ){
							formatYearValues.add(i,stringNumberToDouble(currentRequest.getParameter(format + "X" + i++)));
							
						}else{ //if value is empty in input field --> Value from Previous year
							//check if month is correct, otherwise add 0.0
							while((double)(i-2) < stringNumberToDouble(rs.getString(3).substring(5,7))){
								formatYearValues.add(i++, 0.0);
							}							
							formatYearValues.add(i++,roundToDigits(rs.getDouble(1)*globalPercentageFactor));
							
						}
					}while(rs.next());
					
				}
				//fill ArrayList with 0.0 if values are not available 
					while(i<15){
						formatYearValues.add(i++,0.0);
					}
					if (format == 4){
						for (int j = 0; j<  formatYearValues.size();j++){
						}
					}
				
			}catch (SQLException e) {
				System.err.println("SQL Exception at creation of Arraylist");
				e.printStackTrace();
			}
		
		}
		return formatYearValues;
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
		
		//Add columns with conclusion values at the end of the table(Sum, avg kwh/TNF, kwh)
		for (int i = 0; i < allPlanningDataList.size(); i++){
			
			//add Sum of monthly values of Result (Column Total)	
			Double sumOfRowValues = 0.0;			
			for (int j =3; j < allPlanningDataList.get(i).size() ; j++){
					sumOfRowValues += allPlanningDataList.get(i).get(j);				
			}
			allPlanningDataList.get(i).add(15, roundToDigits(sumOfRowValues));
			
			//add  avg kwh/TNF
			Double tmpAverageEnergyPerAmount = getAverageEnergyPerAmount(allPlanningDataList.get(i).get(2).intValue());
			allPlanningDataList.get(i).add(16, roundToDigits(tmpAverageEnergyPerAmount));
			
			//add kwh (= avg kwh/TNF * SUM)
			allPlanningDataList.get(i).add(17, roundToDigits(tmpAverageEnergyPerAmount*sumOfRowValues));
		}
		return allPlanningDataList;
	}

	public static String getTable(){
		
		String[] months = new DateFormatSymbols(Locale.ENGLISH).getShortMonths();
		String out = " ";
		
		//First Row with titles
		if(selectedPlant != 0 && selectedYear != 0){
			out += "<table border cellpadding=\"3\" rules=\"all\" id= \"dataTable\" >";
			out += "<tr>";
			out += "<td style=\"word-break:break-all;word-wrap:break-word\" > </td>";
			
			for(int i=0;i<months.length-1;i++){
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >";
				out += months[i];
				out +="<br>[TNF]</td>";
			}
			out += "<td style=\"word-break:break-all;word-wrap:break-word\" >Total<br>[TNF]</td>";
			out += "<td style=\"word-break:break-all;word-wrap:break-word\" >AVG<br>[kWh/TNF]</td>";
			out += "<td style=\"word-break:break-all;word-wrap:break-word\" >Energy<br>[kWh]</td>";
			out += "</tr>";
			
		//Rows per Format with values
			ArrayList<ArrayList<Double>> allData = getPlanningData(selectedYear, selectedPlant, percentageChange);
			for(int i = 0; i < allData.size() ;i++){
				ArrayList<Double> tempValueList = allData.get(i);
				if(tempValueList.get(15) != 0.0){	//display only Rows, where at least one value is available (Sum>0)
					out += "<tr>";				
					try {
						out += "<td style=\"word-break:break-all;word-wrap:break-word\" >"+ getProductNameFromID(tempValueList.get(2).intValue());
						//add percentage to Format Field
						if(percentageChange != 0){
							out += "</br>"+ (percentageChange>0?"+":"") + percentageChange + "%";
						}
					} catch (SQLException e) {
						out += "<td style=\"word-break:break-all;word-wrap:break-word\" >" + tempValueList.get(2).intValue();
						e.printStackTrace();
					}
					for(int j = 3; j < tempValueList.size(); j++){
						
						//change color of displayed value
						String colorOfValue = "\"";
						if(!isReset 
								&& currentRequest.getParameter(tempValueList.get(2).intValue() + "X" + j) != null 
								&& currentRequest.getParameter(tempValueList.get(2).intValue() + "X" + j) !="" 
								&& j+1 < tempValueList.size()){
							colorOfValue = "; color: yellow\"";
						}else if(percentageChange!= 0 && j+3 < tempValueList.size()){
							colorOfValue = "; color: orange\"";
						}
						
						//display value
						out += "<td style=\"word-break:break-all;word-wrap:break-word"  +colorOfValue + ">"+ tempValueList.get(j);
						
						//display input fields (only for monthly values)
						if(j+3 < tempValueList.size()){
							out += "</br><input type=\"text\" ";
							out += "name = \"" + tempValueList.get(2).intValue() + "X" + j + "\" ";
							out += "size=\"1\" maxlength=\"5\"";
							
						//insert values from request
							if((loadedSave == null)&& checkIfStringIsNumber(currentRequest.getParameter(tempValueList.get(2).intValue() + "X" + j)) && (!isReset) && currentRequest != null && currentRequest.getParameter(tempValueList.get(2).intValue() + "X" + j) !="" && currentRequest.getParameter(tempValueList.get(2).intValue() + "X" + j) != null){
								out+= " value = \"" + currentRequest.getParameter(tempValueList.get(2).intValue() + "X" + j) + "\"";
							}
						//if Data is loaded from saved query
							if(loadedSave != null && loadedData.get(i).get(j) >= 0.0){
								out+= " value = \"" + loadedData.get(i).get(j) + "\"";
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
			for (int i = 3; i < allData.get(0).size();i++){
				sumPerMonth =  0.0;
				for (int j = 0; j < allData.size();j++){
					sumPerMonth += allData.get(j).get(i);
				}
				sumPerMonth = roundToDigits(sumPerMonth);
				out += "<td style=\"word-break:break-all;word-wrap:break-word\" >"+ sumPerMonth + "</td>";
			}
			
			out+="</tr>";
		}	
		loadedSave = null;
		return out;
	}
	
	public static String getPercentageChange(){
		if(isReset){
			percentageChange = 0;
		}
		String out = "<input type=\"number\"  name=\"percentageChange\" value = \"";
		out+= percentageChange;
		out+= "\" style=\"text-align: right\">";
		return out;
	}

	private static void deleteSaveFromDB(){
		//Delete Information about saved planning data
		String query = "DELETE FROM planning_cockpit WHERE planning_year = '" + selectedYear 
				+ "' AND plant_id = '" + selectedPlant + "';";
		//Delete values of saved planning data
		String query2 = "DELETE FROM planning_values WHERE planning_year = '" + selectedYear 
				+ "' AND plant_id = '" + selectedPlant + "';";
		System.out.println(query);
		System.out.println(query2);
		SQL.execute(query);
		SQL.execute(query2);
	}	
	
	private static void saveValuesToDatabase(){
	//Modify planning_cockpit
		//check if planning data to year/plant already exists
		ResultSet rs = null;
		String check = "SELECT * FROM planning_cockpit WHERE planning_year = '" + selectedYear 
				+ "' AND plant_id = '" + selectedPlant + "';";
		rs = SQL.queryToResultSet(check);
		System.out.println("Check: " + check);
		String query = "";
		try {
			if(rs!=null && rs.next()){ //already exists -> update
				query = "UPDATE planning_cockpit SET global_change = '" + percentageChange + "', planning_created_on = 'now', '" + currentSession.getAttribute(Const.SessionAttributs.LOGGED_IN_USERNAME) + "'  WHERE planning_year = '" + selectedYear + "' AND plant_id = '" + selectedPlant + "'; ";
				System.out.println(query);
			}else{ //Planning doesn't exist yet -> Insert Into
				query = "INSERT INTO planning_cockpit (planning_year, plant_id, global_change, planning_created_on, saved_by_user) VALUES ('" + selectedYear + "', '" + selectedPlant + "', '" + percentageChange + "', 'now', '" + currentSession.getAttribute(Const.SessionAttributs.LOGGED_IN_USERNAME) + "');";
				System.out.println(query);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		SQL.execute(query);
		
	//Modify planning_values
		//Delete old Planning Values
		String delQuery = "DELETE FROM planning_values WHERE planning_year = '" + selectedYear + "' AND plant_id = '" + selectedPlant + "';";
		System.out.println("delQuery" + delQuery);
		SQL.execute(delQuery);
		
		//Create ArrayList<ArrayList<Double>> with all values from the browser- input field
		ArrayList<ArrayList<Double>> allInsertedData = new ArrayList<ArrayList<Double>>();
		ArrayList<Integer> allFormats = getAllFormats();		
		for (int i = 0; i<allFormats.size(); i++){
			//Check if Format id displayed
				if(currentRequest.getParameter(allFormats.get(i) + "X" + 3) != null){ 	
					ArrayList<Double> tempValues = new ArrayList<Double>();
					//Add format to first column of ArrayList
					tempValues.add(0, (double)allFormats.get(i));
					for(int j = 3; j<15;j++){
						//Check if a value was entered into the input field, otherwise set the value to -3.0
						Double tempDouble = currentRequest.getParameter(allFormats.get(i) + "X" + j) == ""? -3.0:stringNumberToDouble(currentRequest.getParameter(allFormats.get(i) + "X" + j));
						tempValues.add(j-2, tempDouble);
					}
					allInsertedData.add(i, tempValues);
			}
		}
		
		

		//Insert Planning Values into Database
		String queryValueInsertion = "INSERT INTO planning_values (planning_year, plant_id, product_id, m_1, m_2, m_3, m_4, m_5, m_6, m_7, m_8, m_9, m_10, m_11, m_12) VALUES";
				
		for (int i = 0; i<allInsertedData.size();i++){
			queryValueInsertion += "('" + selectedYear + "', '" + selectedPlant + "'" + ", '" + allInsertedData.get(i).get(0) + "'";			
			for (int j = 0; j<12;j++){
				queryValueInsertion += ", '" + allInsertedData.get(i).get(j+1) + "'";
			}
			queryValueInsertion += i+1<allInsertedData.size()? ")," : ");" ;			
		}
		System.out.println("queryValueInsertion: " + queryValueInsertion);
		SQL.execute(queryValueInsertion);
	}

	public static String getSavedPlannings(){
		//Load Data from planning_cockpit into ArrayList
		ArrayList<ArrayList<String>> planningCockpitData = new ArrayList<ArrayList<String>>();
		ResultSet rs = SQL.queryToResultSet("SELECT planning_year, plant_id, global_change, planning_created_on, saved_by_user  FROM planning_cockpit WHERE planning_year = '" + selectedYear + "' AND plant_id = '" + selectedPlant + "';");		
		if(rs!=null){
			try{
				while (rs.next() && rs.getString(1) != null) {	
					ArrayList<String> tmpCockpitData = new ArrayList<String>();
					for (int i = 0; i<5;i++){
						tmpCockpitData.add(i, rs.getString(i+1));
					}
					planningCockpitData.add(tmpCockpitData);
				}
			} catch (SQLException e) {
				System.err.println("SQL Exception when querying Data from planning_cockpit");
				e.printStackTrace();
			}
		}		
		
		//Create Selection with saved Data		
		String out = "";
		for(int i = 0; i<planningCockpitData.size(); i++){
			try {
				out +=  "<input type=\"radio\" name=\"savedData\" value=\"" + planningCockpitData.get(i).get(0) + "X" + planningCockpitData.get(i).get(1)+ planningCockpitData.get(i).get(3) + "\"> Year: " + planningCockpitData.get(i).get(0) + "; Plant: " + getPlantNameFromID(Integer.parseInt(planningCockpitData.get(i).get(1))) + "; User: "+ planningCockpitData.get(i).get(4) + "; Saved: " + planningCockpitData.get(i).get(3).substring(0,10)+"<br>";
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		
		return out;
	}

	private static void getSavedPlanningValues(){
		//Get Data from Database
		ResultSet rs = SQL.queryToResultSet("SELECT * FROM planning_values WHERE planning_year = '" + loadedSave.substring(0,4) + "' AND plant_id = '" + loadedSave.substring(5,6) + "';");
		if(rs!=null){
			try{
				int j = 0;
				while (rs.next()) {	
					ArrayList<Double> tmpLoadData = new ArrayList<Double>();
					for (int i = 0; i<15;i++){
						tmpLoadData.add(i, stringNegativeNumberToDouble(rs.getString(i+1)));
					}
					loadedData.add(j++,tmpLoadData);
				}
			} catch (SQLException e) {
				System.err.println("SQL Exception when querying Data from planning_cockpit");
				e.printStackTrace();
			}
		}	
	}

	private static double getAverageEnergyPerAmount(int format){
		Double result = 0.0;
		//Query to DB
		String startTime = (selectedYear-1) + ".01.01 00:00:00";
		String endTime = selectedYear + ".01.01 00:00:00";
		
//		System.out.println("select round(avg(measures.value/productiondata.amount),4) from productiondata"
//				+ " inner join measures on measures.controlpoint_id=productiondata.controlpoint_id and measures.measure_time=productiondata.measure_time"
//				+ " INNER JOIN controlpoints ON productiondata.controlpoint_id = controlpoints.controlpoints_id"
//				+ " where productiondata.measure_time >= '"
//				+ startTime
//				+ "' AND productiondata.measure_time < '"
//				+ endTime
//				+ "' AND plant_id in('"
//				+ selectedPlant
//				+ "') AND productiondata.product_id in('"
//				+ format
//				+ "')"
//				+ ";");

		ResultSet rs = SQL.queryToResultSet("select round(avg(measures.value/productiondata.amount),4) from productiondata"
		+ " inner join measures on measures.controlpoint_id=productiondata.controlpoint_id and measures.measure_time=productiondata.measure_time"
		+ " INNER JOIN controlpoints ON productiondata.controlpoint_id = controlpoints.controlpoints_id"
		+ " where productiondata.measure_time >= '"
		+ startTime
		+ "' AND productiondata.measure_time < '"
		+ endTime
		+ "' AND plant_id in('"
		+ selectedPlant
		+ "') AND productiondata.product_id in('"
		+ format
		+ "')"
		+ ";");
		
		try {
			while(rs.next()){
				result = stringNumberToDouble(rs.getString(1));
			}
		} catch (SQLException e) {
			System.err.println("Error at query to get average Kwh/TNF of format " + format + ". --> Set to 0.0");
		}		
		return result;
	}
}