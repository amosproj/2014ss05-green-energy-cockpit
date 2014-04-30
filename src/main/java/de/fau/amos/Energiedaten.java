/*
 * Copyright (c) 2014 by Sven Huprich, Dimitry Abb, Jakob Hübler, Cindy Wiebe, Ferdinand Niedermayer, Dirk Riehle, http://dirkriehle.com
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
