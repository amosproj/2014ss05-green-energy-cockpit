package de.fau.amos;

import java.sql.*;


public class Energiedaten {
	
	private String standort;
	private String[] columns = new String[9];
	private String[] values = null;
	ResultSet rs;
	Connection c = null;
	Statement stmt = null;
	
	public String getStandort() {
		return standort;
	}
	public void setStandort(String standort) {
		this.standort = standort;
	}
	
	public String[] getColumns() {
		return columns;
	}
	public void setColumns(String[] inputArray) {
				for(int i=0; i < 9; i++){
			columns[i] = inputArray[i] ;
			if(columns[i].contains("\"") || columns[i].contains(" ") || columns[i].contains("(") ||
					 columns[i].contains(")")) {
				columns[i] = columns[i].replaceAll("\"", "");
				columns[i] = columns[i].replaceAll(" ", "_");
				columns[i] = columns[i].replaceAll("\\(", "");
				columns[i] = columns[i].replaceAll("\\)", "");
		    }
		};
	}
	
	public String[] getValues() {
		return values;
	}
	public void setValues(String[] inputArray) {
		String line = "";
		for(int i = 9; i < inputArray.length;i++){
			line += inputArray[i];
			if(i != inputArray.length-1){
				line += ";";
			}
		}
		values = line.split(";");
	}
		
	public String createTable(String table, String[] columns){
  	  String column = "";
  	  for(int i=0; i < columns.length; i++){
     	   column += columns[i] + " TEXT NOT NULL";            
     	   if(columns[i] != columns[columns.length - 1]){
     		   column += ", ";
     	       }
        }
  	  String sql = "CREATE TABLE " + table.toUpperCase() + " (" + column
                      + ")";                     
                     return sql;
    }
	
	public String[] insertValues(String table, String[] columns, String[] values){
		String column = "";
  	  for(int i=0; i < columns.length; i++){
     	   column += columns[i];            
     	   if(columns[i] != columns[columns.length - 1]){
     		   column += ", ";
     	       }
        }
  	  String sql[] = new String[values.length/9];
	  
	  String value = "";
	  int k = 0;
	  
  	  for(int i = 0; i < values.length/9;i++){
  		  
  		  while(k%9 != 0 || k == 0 && k < values.length){
  			  
  			value += "'" + values[k] +"'";
  			if((k+1) % 9 != 0){
  				value += ",";
  			}
  			sql[i] = "INSERT INTO " + table.toUpperCase() + " (" + column + 
  				     ") " + "VALUES (" + value + ");";	
  			k++;  
  		  }
  		  value = "";
  		  value += "'" + values[k] +"', ";
          k++; 	
  	  }
		return sql;
	}
	
	
	public static String selectAll(SQL Anlage){
		String select = "SELECT * FROM " + Anlage.getStandort() + ";";
		return select;			
	}
	
	public void selectColumn(SQL Anlage, String column){
		
		String select = "SELECT " + column + " FROM " + Anlage.getStandort() + ";";
		
		try{
			rs = stmt.executeQuery(select);
			while ( rs.next() ) {
				String a = rs.getString(column);
				System.out.println(column + " : " + a );
				System.out.println();
			}
			rs.close();
		}catch (Exception e) {
	         e.printStackTrace();
	         System.err.println(e.getClass().getName()+": "+e.getMessage());
	         System.exit(0);
	      }		
	}
		

	
	
	
}
