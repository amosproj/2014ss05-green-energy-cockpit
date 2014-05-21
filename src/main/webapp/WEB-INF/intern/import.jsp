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

File impFolder=new File(System.getProperty("userdir.location"),"import");
if(impFolder.exists()){
	File[] list=impFolder.listFiles();
	for(int f=0;f<list.length;f++){
		synchronized(list[f]){
			String fileName=list[f].getName();
			
			out.println("found file: "+list[f].getAbsolutePath()+"  "+fileName+"<br>");	
			System.out.println();
			System.out.println("check file "+list[f]);
			
			if(fileName.length()>7){
				if(fileName.contains(".csv_imp")){
					//is a .csv -> can import
					
					//get plantId to import measures to
					String ending=fileName.substring(fileName.lastIndexOf('.')+1);	
					if(ending==null||ending.length()<8||!ending.startsWith("csv_imp")){
						//can't import file
						list[f].delete();
						continue;
					}					
					String plantId=ending.replace("csv_imp","");
					
					
					//createTable measures to store measures
					String command=
						"CREATE TABLE measures (measures_ID serial primary key, controlpoint_id integer NOT NULL, "+ 
						"measure_time timestamp not null, value decimal not null,"+
					  	"CONSTRAINT measure_controlpoint_id_fkey FOREIGN KEY (controlpoint_id) "+
					    "REFERENCES controlpoints (controlpoints_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";					
					SQL.execute(command);			

					
					//create bufferedReader to read file(s) in import folder
					BufferedReader br=new BufferedReader(new FileReader(list[f]));
					
					int lines=0;
					while(br.readLine()!=null){
						lines++;
					}
					br.close();
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
					
					
					int lineCounter=0;
					boolean printed=false;
					long time=System.currentTimeMillis();
					long duration=0;
					//step through file
					while((line=br.readLine())!=null){
						//System.out.println("find:"+findHeaders+" mutlid:"+multilineHeader+" read line: "+line);
						lineCounter++;
						int prozent=(int)((lineCounter/(double)lines)*100);

						
						if(prozent%2==0){
							if(!printed){
								duration=duration+(System.currentTimeMillis()-time);
								time=System.currentTimeMillis();
								long estimated=(prozent==0?0:(100-prozent)*(duration/prozent));
								String durationS=(duration/1000/60/60)+"h "+((duration/1000/60)%60)+"min "+((duration/1000)%60)+"s";
								String estimatedS=(prozent==0?"?":(estimated/1000/60/60)+"h "+((estimated/1000/60)%60)+"min "+((estimated/1000)%60)+"s");
								System.out.println("Fortschritt: "+prozent+"% ("+lineCounter+"/"+lines+") benötigte Zeit bisher: "+durationS+" geschätzt: "+estimatedS);
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
									ArrayList<ArrayList<String>>output=SQL.querry("SELECT controlpoints_id from controlpoints where plant_id='"+plantId+"' and control_point_name='"+headers.get(i)+"';");
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
									ArrayList<ArrayList<String>> knownData=SQL.querry(
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
					
				}else{
					//no importable file
					System.out.println("wrong filenameformat");
				}
			}else{
				//no filename was passed, only random number was generated
				System.out.println("filename to short");
			}
			list[f].delete();
		}
	}
}

response.sendRedirect(request.getContextPath()+"/intern/setup.jsp");

%>
</body>
</html>