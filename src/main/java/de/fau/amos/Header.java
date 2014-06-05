package de.fau.amos;

public class Header {

	public static String getHeaderButtons(){
		String out="";
				
		out+="<a href="+'"'+
				"planning.jsp"
				+'"'+"><li>"+
				"Planning"
				+"</li></a>";
		out+="<a href="+'"'+
				"charts.jsp"
				+'"'+"><li>"+
				"Charts"
				+"</li></a>";
		out+="<a href="+'"'+
				"funktion3.jsp"
				+'"'+"><li>"+
				"Download"
				+"</li></a>";
		out+="<a href="+'"'+
				"setup.jsp"
				+'"'+"><li>"+
				"Setup"
				+"</li></a>";
		out+="<a href="+'"'+
				"../login/logout"
				+'"'+"><li>"+
				"Logout"
				+"</li></a>";
		
		return out;
	}
}
