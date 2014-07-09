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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@WebServlet("/FileDownload")
public class FileDownload extends HttpServlet {

	private static final long serialVersionUID = 1L;


	public FileDownload() {
		super();
	}
	
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		
		String fileName=request.getParameter("downloadFileName");
		
		//creates a file containing the values of the requested SQL query 
		System.out.println("create file "+fileName+"");
		ArrayList<ArrayList<String>>data=SQL.query("select * from plants");
		createCsvFile(data,fileName); 
		
		File file=new File(System.getProperty("userdir.location"),fileName);
		
		if(fileName==null||!file.exists()){
			request.getRequestDispatcher("/intern/funktion3.jsp").forward(request, response);
			return;
		}
		
		response.setHeader("Content-Length", String.valueOf(file.length()));
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

		InputStream input = new BufferedInputStream(new FileInputStream(file), 1024 * 10);
		OutputStream output = new BufferedOutputStream(response.getOutputStream(), 1024 * 10);

		byte[] buffer = new byte[1024 * 10];
		int length;
		while ((length = input.read(buffer)) > 0) {
		    output.write(buffer, 0, length);
		}

		output.flush();
		output.close();
		input.close();
		

	}

	/**
	 * Creates a .csv file containing the data of the passed "ArrayList<ArrayList<String>> data" and saves it into the "userdir.location" directory
	 * @param data
	 * @param fileName
	 */
	private static void createCsvFile(ArrayList<ArrayList<String>> data, String fileName){
		
		try{
			String lines = "";
			
			//using Buffered Writer to write a file into the "userdir.location" directory
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(System.getProperty("userdir.location"), fileName)));
			
			//every single value will be written down into the file separated by a semicolon
			for(int i=0;i<data.size();i++){
				for(int j=0;j<data.get(i).size();j++){
					lines = data.get(i).get(j) + "; ";
					bw.write(lines);
				}
				bw.newLine();
				
			}	
			bw.flush();
			bw.close();
			
			System.out.println("Success!");
		}catch(IOException e){
			System.out.println("Couldn't create File!");
		}				
	}

}
