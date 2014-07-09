<!-- 
 - Copyright (c) 2014 by Sven Huprich, Dimitry Abb, Jakob Hübler, Cindy Wiebe, Ferdinand Niedermayer, Dirk Riehle, http://dirkriehle.com
 -
 - This file is part of the Green Energy Cockpit for the AMOS Project.
 -
 - This program is free software: you can redistribute it and/or modify
 - it under the terms of the GNU Affero General Public License as
 - published by the Free Software Foundation, either version 3 of the
 - License, or (at your option) any later version.
 -
 - This program is distributed in the hope that it will be useful, 
 - but WITHOUT ANY WARRANTY; without even the implied warranty of
 - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 - GNU Affero General Public License for more details.
 -
 - You should have received a copy of the GNU Affero General Public
 - License along with this program. If not, see
 - <http://www.gnu.org/licenses/>.
 -->
 
 <%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<%@ page import="de.fau.amos.*"%> 
<%@ page import="java.io.*"%> 
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<%

// this file is a non-displayable webpage, that checks a special folder on the server
// and imports data to the database, if the files have the right format.

// files that should be imported must be assigned to a plant, therefor they get the 
// plantId appended. They get ED or PD appended if the files contain "energy data"
// or "production data"

// filenamepatterns are: 
// [RANDOM-SIX-DIGIT-NUMBER]_[ORIGINAL-FILENAME].csv_[ED or PD][PLANT-ID]
// e.g.: 135790_someEnergyData.csvED1


//location where importfiles are saved
File impFolder=new File(System.getProperty("userdir.location"),"import");

//if not exsiting, there are no files to import
if(impFolder.exists()){
	
	//get all files in directory
	File[] list=impFolder.listFiles();
	
	//check each file
	for(int f=0;f<list.length;f++){
		
		//handle files synchronized, so no other user changes content while uploading
		synchronized(list[f]){
			String fileName=list[f].getName();
		
			System.out.println("check file "+list[f]);
			
			//filenames shorter than 7 characters can't be files to be imported
			if(fileName.length()>7){
				
				//importfiles must have ending .csv_imp
				if(fileName.contains(".csv_imp")){
					
					//is a .csv -> can import
					
					//get plantId to import measures to
					//take whole ending and work on it later
					String ending=fileName.substring(fileName.lastIndexOf('.')+1);	
					if(ending==null||ending.length()<10||(!ending.startsWith("csv_impED")&&!ending.startsWith("csv_impPD"))){
						
						//can't import file -> delete it from importfolder
						list[f].delete();
						continue;
					}					
					
					//flags for energydata or productiondata
					boolean isImportED=false;
					boolean isImportPD=false;
					
					String plantId="";
					
					//set flags and delete information from "plantId" -> only the real plantId remains in "plantId"
					if(ending.startsWith("csv_impED")){
						isImportED=true;
						plantId=ending.replace("csv_impED","");
					}else if(ending.startsWith("csv_impPD")){
						isImportPD=true;
						plantId=ending.replace("csv_impPD","");
					}

					if(isImportED){
						//import Energy Data
						
						//createTable measures to store measures
						String command=
							"CREATE TABLE measures (measures_ID serial primary key, controlpoint_id integer NOT NULL, "+ 
							"measure_time timestamp not null, value decimal not null,"+
						  	"CONSTRAINT measure_controlpoint_id_fkey FOREIGN KEY (controlpoint_id) "+
						    "REFERENCES controlpoints (controlpoints_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";					
						SQL.execute(command);			
	
						
						//create bufferedReader to read file(s) in import folder
						BufferedReader br=new BufferedReader(new FileReader(list[f]));
						
						//before upload count lines, to be able to display progress
						int lines=0;
						while(br.readLine()!=null){
							lines++;
						}
						br.close();
						
						//reset bufferedReader
						br=new BufferedReader(new FileReader(list[f]));
						
						//tmpVar to step through file line by line
						String line=null;
						
						//columnHeaders of csv file
						ArrayList<String> headers=new ArrayList<String>();
						//get controlPointIds matching names in headers
						ArrayList<String> controlPointIds=new ArrayList<String>();
						//at index 0 "Zeitstempelt" is located -> no controlpoint column is here
						controlPointIds.add("0");
						
						//var to readout headers
						boolean findHeaders=true;
						boolean multilineHeader=false;
						
						//headers that have to be set in measures
						ArrayList<String>tableHeaders=new ArrayList<String>();
						tableHeaders.add("measure_time");
						tableHeaders.add("controlpoint_id");
						tableHeaders.add("value");
						
						//counter for progressed lines
						int lineCounter=0;

						//flag for progressdisplaying
						boolean printed=false;
		
						//variables for estimaed time calculation
						long time=System.currentTimeMillis();
						long duration=0;
								
						//step through file
						while((line=br.readLine())!=null){
							
							lineCounter++;
							
							//calculate progress as integer
							int prozent=(int)((lineCounter/(double)lines)*100);
	
							//display progress every 2%
							if(prozent%2==0){
								//display progress only once per 2%
								if(!printed){
									//time calculation
									duration=duration+(System.currentTimeMillis()-time);
									time=System.currentTimeMillis();
									long estimated=(prozent==0?0:(100-prozent)*(duration/prozent));
									String durationS=(duration/1000/60/60)+"h "+((duration/1000/60)%60)+"min "+((duration/1000)%60)+"s";
									String estimatedS=(prozent==0?"?":(estimated/1000/60/60)+"h "+((estimated/1000/60)%60)+"min "+((estimated/1000)%60)+"s");
									
									//print progress and time calculation
									System.out.println("Progress: "+prozent+"% ("+lineCounter+"/"+lines+") time taken: "+durationS+"; estimated remaining: "+estimatedS);
									printed=true;
								}
							}else{
								printed=false;
							}
							
							//in "findHeaders"-mode headers are not read out completely
							if(findHeaders){
								//split arguments separated by ";"
								String[] parts=line.split(";");
								for(int i=0;i<parts.length;i++){
									//put arguments to headers
									
									if(i==0&&multilineHeader){
										//if "multilineHeader", add to the last existing header
										String old=headers.get(headers.size()-1);
										headers.set(headers.size()-1,old+parts[0]);
									}else{
										//add to headers
										headers.add(parts[i]);
									}
								}
								if(parts[parts.length-1].startsWith('"'+"")&&parts[parts.length-1].endsWith('"'+"")){
									//if last argument in line starts and ends with " it is the last argument
									multilineHeader=false;
									findHeaders=false;
								}else if(parts[parts.length-1].startsWith('"'+"")){
									//if last argument in line starts with " it could be a header over more lines
									multilineHeader=true;
									findHeaders=true;
								}if(multilineHeader&&parts[parts.length-1].endsWith('"'+"")){
									//if last argument in line ends with " its the last argument
									multilineHeader=false;
									findHeaders=false;
								}else if(multilineHeader&&!parts[parts.length-1].contains('"'+"")){
									//mutlilineheader over more lines
									findHeaders=true;
									multilineHeader=true;
								}else{
									findHeaders=false;								
								}
								
								if(!findHeaders){
									//finished readout headers
									
									//get ids for controlpoints
									for(int i=1;i<headers.size();i++){
										ArrayList<ArrayList<String>>output=SQL.query("SELECT controlpoints_id from controlpoints where plant_id='"+plantId+"' and control_point_name='"+headers.get(i)+"';");
										if(output==null||output.size()<2){
											//no controlpoint found matching the name
											controlPointIds.add("0");
										}else{
											controlPointIds.add(output.get(1).get(0));
										}
									}
	
								}
							}else{
								//headers found, import values to database
								//System.out.println("read valueLine: "+line);
								String[] values=line.split(";");
								for(int i=1;i<values.length;i++){
									ArrayList<String>valuesArr=new ArrayList<String>();
									valuesArr.add(TimestampConversion.convertTimestamp(values[0])); //time
									valuesArr.add(controlPointIds.get(i));	//controlPointId
									valuesArr.add(values[i].replace(",",".")); //value
									if(!controlPointIds.get(i).equals("0")){
										ArrayList<ArrayList<String>> knownData=SQL.query(
											"Select measures_id from measures where measure_time='"+valuesArr.get(0)+"' and controlpoint_id='"+valuesArr.get(1)+"';");
										if(knownData!=null&&knownData.size()>1){
											//allready exists! skip it
											
											//System.out.println("there is allready a entity of ["+valuesArr.get(0)+"]["+valuesArr.get(1)+"]");
										}else{
											SQL.addColumn("measures",tableHeaders,valuesArr);
										}
									}
								}
														
							}
						}
						
						for(int i=0;i<headers.size();i++){
							if(headers.get(i).startsWith('"'+"")){
								headers.set(i,headers.get(i).substring(1));
							}
							if(headers.get(i).endsWith('"'+"")){
								headers.set(i,headers.get(i).substring(0,headers.get(i).length()-1));
							}
						}
						
						br.close();
					}else if(isImportPD){
						//import production data

						//createTable productiondata to store productiondata
						String command=
							"CREATE TABLE productiondata (productiondatas_ID serial primary key, controlpoint_id integer NOT NULL, "+ 
							"measure_time timestamp not null, product_id integer NOT NULL, amount decimal not null,"+
							"CONSTRAINT productiondata_controlpoint_id_fkey FOREIGN KEY (controlpoint_id) "+
						    "REFERENCES controlpoints (controlpoints_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,"+
				    		"CONSTRAINT productiondata_product_id_fkey FOREIGN KEY (product_id) "+
						    "REFERENCES products (products_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION"+
								    	    ")";					
						SQL.execute(command);			
	
						//create bufferedReader to read file(s) in import folder
						BufferedReader br=new BufferedReader(new FileReader(list[f]));
						
						//before upload count lines, to be able to display progress
						int lines=0;
						while(br.readLine()!=null){
							lines++;
						}
						br.close();
						
						//reset bufferedReader
						br=new BufferedReader(new FileReader(list[f]));
						
						//tmpVar to step through file line by line
						String line=null;
						
						//columnHeaders of csv file
						ArrayList<String> headers=new ArrayList<String>();
						//get controlPointIds matching names in headers
						ArrayList<String> controlPointIds=new ArrayList<String>();
						//get productIds matching names in columns
						ArrayList<ArrayList<String>> productIds=new ArrayList<ArrayList<String>>();
						
						//at index 0 "Zeitstempelt" is located -> no controlpoint column is here
						controlPointIds.add("0");
						
						//var to readout headers
						boolean findHeaders=true;
						boolean multilineHeader=false;
						
						//headers that have to be set in productiondata
						ArrayList<String>tableHeaders=new ArrayList<String>();
						tableHeaders.add("controlpoint_id");
						tableHeaders.add("measure_time");
						tableHeaders.add("product_id");
						tableHeaders.add("amount");
						
						
						//counter for progressed lines
						int lineCounter=0;

						//flag for progressdisplaying
						boolean printed=false;
		
						//variables for estimaed time calculation
						long time=System.currentTimeMillis();
						long duration=0;
								
						//step through file
						while((line=br.readLine())!=null){
							
							lineCounter++;
							
							//calculate progress as integer
							int prozent=(int)((lineCounter/(double)lines)*100);
	
							//display progress every 2%
							if(prozent%2==0){
								//display progress only once per 2%
								if(!printed){
									//time calculation
									duration=duration+(System.currentTimeMillis()-time);
									time=System.currentTimeMillis();
									long estimated=(prozent==0?0:(100-prozent)*(duration/prozent));
									String durationS=(duration/1000/60/60)+"h "+((duration/1000/60)%60)+"min "+((duration/1000)%60)+"s";
									String estimatedS=(prozent==0?"?":(estimated/1000/60/60)+"h "+((estimated/1000/60)%60)+"min "+((estimated/1000)%60)+"s");
									
									//print progress and time calculation
									System.out.println("Progress: "+prozent+"% ("+lineCounter+"/"+lines+") time taken: "+durationS+"; estimated remaining: "+estimatedS);
									printed=true;
								}
							}else{
								printed=false;
							}
							
							//in "findHeaders"-mode headers are not read out completely
							if(findHeaders){
								
								//split arguments separated by ";"
								String[] parts=line.split(";");
								for(int i=0;i<parts.length;i++){
									//add arguments to headers		
									if(i==0&&multilineHeader){
										//if "multilineHeader", add to the last existing header
										String old=headers.get(headers.size()-1);
										headers.set(headers.size()-1,old+parts[0]);
									}else{
										//add to headers
										headers.add(parts[i]);
									}
								}
								if(parts[parts.length-1].startsWith('"'+"")&&parts[parts.length-1].endsWith('"'+"")){
									//if last argument in line starts and ends with " it is the last argument
									multilineHeader=false;
									findHeaders=false;
								}else if(parts[parts.length-1].startsWith('"'+"")){
									//if last argument in line starts with " it could be a header over more lines
									multilineHeader=true;
									findHeaders=true;
								}if(multilineHeader&&parts[parts.length-1].endsWith('"'+"")){
									//if last argument in line ends with " its the last argument
									multilineHeader=false;
									findHeaders=false;
								}else if(multilineHeader&&!parts[parts.length-1].contains('"'+"")){
									//mutlilineheader over more lines
									findHeaders=true;
									multilineHeader=true;
								}else{
									findHeaders=false;								
								}
								
								if(!findHeaders){
									//finished readout headers
									
									//get ids for controlpoints
									for(int i=1;i<headers.size();i+=2){
										//search for controlpoint_id with plant_id given from filename and controlpointname given from header
										ArrayList<ArrayList<String>>output=SQL.query("SELECT controlpoints_id from controlpoints where plant_id='"+plantId+"' and control_point_name='"+headers.get(i)+"';");
										if(output==null||output.size()<2){
											//no controlpoint found matching the name
											//set controlpointIds for this column to 0 to skip it when steping through file
											controlPointIds.add("0");
										}else{
											//add id to controlpointIds
											controlPointIds.add(output.get(1).get(0));
										}
										//add column for values->id for this column not needed->set value to 0
										controlPointIds.add("0");
									}
	
									//get ProductIds to for faster import
									productIds=SQL.query("SELECT product_short_name, products_id from products");
								}
							}else{

								//headers found, import values to database
								String[] values=line.split(";");
								
								//skip column 0(timestamp)
								for(int i=1;i<values.length;i+=2){

									ArrayList<String>valuesArr=new ArrayList<String>();
									valuesArr.add(controlPointIds.get(i));	//controlPointId
									valuesArr.add(TimestampConversion.convertTimestamp(values[0])); //time
									for(int j=0;j<productIds.size();j++){
										if(values[i].equals(productIds.get(j).get(0))){
											valuesArr.add(productIds.get(j).get(1));//productId
								
											valuesArr.add(values[i+1].replace(",",".")); //value
																						
											if(!controlPointIds.get(i).equals("0")){
												ArrayList<ArrayList<String>> knownData=SQL.query(
													"Select productiondatas_id from productiondata where measure_time='"+valuesArr.get(1)+"' and controlpoint_id='"+valuesArr.get(0)+"' and product_id='"+valuesArr.get(2)+"';");
												if(knownData!=null&&knownData.size()>1){
													//allready exists! skip it
													
													//System.out.println("there is allready an entity of ["+valuesArr.get(0)+"]["+valuesArr.get(1)+"]");
												}else{
													SQL.addColumn("productiondata",tableHeaders,valuesArr);
												}
											}
										
											
											break;
										}
									}
									
								}
														
							}
						}
						
						for(int i=0;i<headers.size();i++){
							if(headers.get(i).startsWith('"'+"")){
								headers.set(i,headers.get(i).substring(1));
							}
							if(headers.get(i).endsWith('"'+"")){
								headers.set(i,headers.get(i).substring(0,headers.get(i).length()-1));
							}
						}
						
						br.close();

						
						
					}
				}else{
					//no importable file
					System.out.println("wrong filenameformat");
				}
			}else{
				//no filename was passed, only random number was generated
				System.out.println("filename to short");
			}
			
			//delete files that can't be imported and files that have been imported.
			//reaching this line covers both criteria
			list[f].delete();
		}
	}
}

//finishing import (-> no more files in importfolder) redirects to setup page
response.sendRedirect(request.getContextPath()+"/intern/setup.jsp");

%>
</body>
</html>