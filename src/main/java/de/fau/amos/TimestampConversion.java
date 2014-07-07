package de.fau.amos;

import java.util.Calendar;

/**
 * 
 * The TimestampConversion.java class is necessary for the conversion into the right format for an easy PostgreSQL handling. 
 * It converses the input time into the required timestamp (e.g. String format). 
 *
 */

public class TimestampConversion {
	
	public static String convertTimestamp(Calendar c){
		return convertTimestamp(c.get(Calendar.MINUTE),c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.DATE),c.get(Calendar.MONTH),c.get(Calendar.YEAR));
	}
	
	//converts any committed Integer time into a String timestamp
	public static String convertTimestamp(int minute,int hour,int day,int month,int year){
		String strYear;
		String strMonth;
		String strDay;
		String strHour;
		String strMinute;
		if(year <1000){
			if(year<10){
				strYear = "200" + year;
			}else if(year >80){
				strYear = "19" + year;
			}else{							
				strYear = "20" + year;
			}
		}else{
			strYear = "" + year;
		}
		
		if(month<10){
			strMonth = "0" + month;
		}else{
			strMonth = "" + month;
		}
		if(day<10){
			strDay = "0" + day;
		}else{
			strDay = "" + day;
		}
		if(hour<10){
			strHour = "0" + hour;
		}else{
			strHour = "" + hour;
		}
		if(minute<10){
			strMinute = "0" + minute;
		}else{
			strMinute = "" + minute;
		}
		
		return strYear + "-" + strMonth + "-" + strDay + " " + strHour + ":" + strMinute + ":" + "00";
	}
	
	//if the timestamp is already a String, this method builds up a new timestamp with correct character in between
	public static String convertTimestamp(String Timestamp){
		return Timestamp.substring(13,17) + "-" + getMonthInt(Timestamp.substring(8,11)) + "-" + Timestamp.substring(5,7) + " " + Timestamp.substring(18,20) + ":" + Timestamp.substring(21) + ":00";
	}
	
	//converts months as a String into Integers (from "01" till "12") taking account the different forms of writing 
	private static String getMonthInt(String Month)throws IllegalArgumentException{
		switch(Month.toLowerCase()){
		case "jan":
			return "01";
		case "feb":
			return "02";
		case "mär": 
		case "mar":
		case "mrz":
			return "03";
		case "apr":
			return "04";
		case "mai":
		case "may":
			return "05";
		case "jun":
			return "06";
		case "jul":
			return "07";
		case "aug":
			return "08";
		case "sep":
			return "09";
		case "okt": 
		case "oct":
			return "10";
		case "nov":
			return "11";
		case "dez": 
		case "dec":
			return "12";
		default:
			System.err.println("Cannot convert Month ('" + Month + "') from String to Int. Unknown Argument.");
			return null;
		}
	}

}
