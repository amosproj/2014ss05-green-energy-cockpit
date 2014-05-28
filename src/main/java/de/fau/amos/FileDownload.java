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
		
		//TODO change this later
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

	
	private static void createCsvFile(ArrayList<ArrayList<String>> data, String fileName){
		
		try{
			String lines = "";
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(System.getProperty("userdir.location"), fileName)));
			
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
