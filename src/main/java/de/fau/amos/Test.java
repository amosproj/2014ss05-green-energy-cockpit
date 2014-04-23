package de.fau.amos;


public class Test {

	public static Object[][] statArr={{new String("Jahr"),new String("Verkaeufe"),new String("Einnahmen")},
		{new String("2000"),new Integer(50),new Integer(30)},{new String("2002"),new Integer(20),new Integer(40)}};
	
	
	public static String convert2DArray(Object[][] a){
		
		String out="";
		
		out+="[";
		for(int i=0;i<a.length;i++){
			out+="[";
			for(int j=0;j<a[i].length;j++){
				
				if(a[i][j] instanceof String){
					out+="'"+a[i][j]+"'";
				}else if(a[i][j] instanceof Integer){
					Integer integ=(Integer)a[i][j];
					out+=""+integ.intValue();
				}else{
					out+="'"+a[i][j]+"'";
				}
				
				
				if(j!=a[i].length-1){
					out+=",";
				}
			}
			out+="]";
			if(i!=a.length-1){
				out+=",";
			}
		}
		out+="]";
		
		return out;
		
	}
	
	
	public static String getArray(){
		return "[['Year', 'Sales', 'Expenses' , 'Some more'],['2004',  1000,      400, 1],['2005',  1170,      460,2],['2006',  660,       1120,3],['2007',  1030,      540,4]]";
		        	
	
	}
	

}
