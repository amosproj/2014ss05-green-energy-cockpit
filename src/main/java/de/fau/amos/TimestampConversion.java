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

import java.util.Calendar;

public class TimestampConversion {
	
	public static String convertTimestamp(Calendar c){
		return convertTimestamp(c.get(Calendar.MINUTE),c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.DATE),c.get(Calendar.MONTH),c.get(Calendar.YEAR));
	}
	
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
	
	public static String convertTimestamp(String Timestamp){
		return Timestamp.substring(13,17) + "-" + getMonthInt(Timestamp.substring(8,11)) + "-" + Timestamp.substring(5,7) + " " + Timestamp.substring(18,20) + ":" + Timestamp.substring(21) + ":00";
	}
	
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
