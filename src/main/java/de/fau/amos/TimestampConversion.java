package de.fau.amos;

public class TimestampConversion {
	public static String convertTimestamp(String Timestamp){
		return Timestamp.substring(13,17) + "-" + getMonthInt(Timestamp.substring(8,11)) + "-" + Timestamp.substring(5,7) + " " + Timestamp.substring(18,20) + ":" + Timestamp.substring(21) + ":00";
	}
	public static String getMonthInt(String Month)throws IllegalArgumentException{
		switch(Month.toLowerCase()){
		case "jan":
			return "01";
		case "feb":
			return "02";
		case "mär": case "mar":
			return "03";
		case "apr":
			return "04";
		case "mai": case "may":
			return "05";
		case "jun":
			return "06";
		case "jul":
			return "07";
		case "aug":
			return "08";
		case "sep":
			return "09";
		case "okt": case "oct":
			return "10";
		case "nov":
			return "11";
		case "dez": case "dec":
			return "12";
		default:
			throw new IllegalArgumentException("Cannot convert Month ('" + Month + "') from String to Int. Unknown Argument.");
		}
	}

}
