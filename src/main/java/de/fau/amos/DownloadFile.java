package de.fau.amos;

import java.io.*;
import java.util.ArrayList;

public class DownloadFile {
		

	public static void download(ArrayList<ArrayList<String>> data, String fileName){
		
		try{
			String lines = "";
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(System.getProperty("userdir.location"), fileName)));
			
			for(int i=0;i<data.size();i++){
				for(int j=0;j<data.get(i).size();j++){
					lines = data.get(i).get(j) + ";";
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
