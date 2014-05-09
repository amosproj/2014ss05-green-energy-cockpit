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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SQL{

	private static Connection c;

	public static void main(String[] args){
		System.out.println(getValueOfFieldWithId("controlpoints","control_point_name","2"));
	}
	
	public static void execute(String command){

		Statement stmt = null;
		try {
			if(c==null||c.isClosed()){
				initConnection();
			}
			synchronized(c){
				stmt = c.createStatement();
				stmt.execute(command);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static String getValueOfFieldWithId(String tableName,String column,String id){
		String out="";
		
		Statement stmt = null;
		try {
			if(c==null||c.isClosed()){
				initConnection();
			}
			synchronized(c){
				stmt = c.createStatement();
				ResultSet rs=stmt.executeQuery("select "+column+" from "+tableName+" where "+tableName+"_id='"+id+"';");
				
				if(!rs.next()){
					return null;
				};
				
				out=rs.getString(1);
				
				if(rs.next()){
					return null;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return out;
	}
	
	public static ArrayList<ArrayList<String>> querry(String command){
		ArrayList<ArrayList<String>> data=new ArrayList<ArrayList<String>>();

		Statement stmt = null;
		try {
			if(c==null||c.isClosed()){
				initConnection();
			}
			synchronized(c){
				stmt = c.createStatement();

				ResultSet rs = stmt.executeQuery(command);
				ResultSetMetaData rsmd = rs.getMetaData();


				ArrayList<String> row=new ArrayList<String>();
				for(int i=1;i<=rsmd.getColumnCount();i++){
					row.add(rsmd.getColumnName(i));
				}

				data.add(row);

				while ( rs.next() ) {	

					row=new ArrayList<String>();
					for(int i=1;i<=rsmd.getColumnCount();i++){
						row.add(rs.getString(i));
					}

					data.add(row);
				} 


				rs.close();
				stmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}


		return data;
	}

	public static void createTable(String tableName,String[] columns){
		Statement stmt = null;
		try {
			if(c==null||c.isClosed()){
				initConnection();
			}
			synchronized(c){

				stmt = c.createStatement();

				String commandPart = tableName+"_ID serial primary key";
				//			String commandPart = "ID INT NOT NULL AUTO_INCREMENT";
				for(int i=0; i < columns.length; i++){
					commandPart += ", ";
					commandPart += columns[i] + " TEXT NOT NULL";            
					//				if(columns[i] != columns[columns.length - 1]){
					//					commandPart += ", ";
					//				}
				}
				String command="CREATE TABLE "+tableName.toUpperCase()+" ("+commandPart+");";                     
				stmt.executeUpdate(command);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<String> getColumns(String tableName){

		Statement stmt = null;
		try {
			if(c==null||c.isClosed()){
				initConnection();
			}
			synchronized(c){

				stmt = c.createStatement();

				ResultSet rs = stmt.executeQuery("SELECT * FROM "+tableName+" WHERE "+tableName+"_ID=1");
				ResultSetMetaData rsmd = rs.getMetaData();

				ArrayList<String> row=new ArrayList<String>();

				for(int i=1;i<=rsmd.getColumnCount();i++){
					row.add(rsmd.getColumnName(i));
				}
				return row;	
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void addColumn(String tableName,ArrayList<String> columns,ArrayList<String> values){
		if(columns==null||values==null||columns.size()!=values.size()){
			return;
		}
		Statement stmt = null;
		try {
			if(c==null||c.isClosed()){
				initConnection();
			}
			synchronized(c){
				stmt = c.createStatement();

				String commandColumnPart = "";
				for(int i=0; i < columns.size(); i++){
					if(columns.get(i).equals(tableName+"_id")){
						columns.remove(i);
						values.remove(i);
					}
				}
				for(int i=0; i < columns.size(); i++){
					commandColumnPart += '"'+columns.get(i) + '"';            
					if(i!=columns.size()-1){
						commandColumnPart += ", ";
					}
				}
				String commandValuePart = "";
				for(int i=0; i < values.size(); i++){
					commandValuePart += "'"+values.get(i) + "'";           
					if(i != values.size()-1){
						commandValuePart += ", ";
					}
				}

				String command="INSERT INTO "+tableName.toUpperCase()+
						" ("+commandColumnPart+")"+
						" VALUES ("+commandValuePart+");";
//				System.out.println(command);
				stmt.execute(command);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private static void initConnection(){
		try{

			Class.forName("org.postgresql.Driver");
			c = DriverManager
					.getConnection("jdbc:postgresql://faui2o2j.informatik.uni-erlangen.de:5432/ss14-proj5",
							"ss14-proj5", "quaeF4pigheNgahz");
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
