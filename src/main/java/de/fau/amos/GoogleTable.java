package de.fau.amos;

import java.util.ArrayList;

public class GoogleTable {

	private final int rows;
	
	private ArrayList<Object[]> data;
	
	public GoogleTable(String...header){
		
		rows=header.length;
		
		data=new ArrayList<Object[]>();
		data.add(header);
	}
	
	public void addColumn(Object...objects){
		int num=objects.length;
		if(num!=rows){
			return;
		}
		data.add(objects);
	}
	
	public String toString(){
		
		String out="";
		
		out+="[";
		for(int i=0;i<data.size();i++){
			out+="[";
			for(int j=0;j<data.get(i).length;j++){
				
				if(data.get(i)[j] instanceof String){
					out+="'"+data.get(i)[j]+"'";
				}else if(data.get(i)[j] instanceof Integer){
					Integer val=(Integer)data.get(i)[j];
					out+=""+val.intValue();
				}else if(data.get(i)[j] instanceof Double){
					Double val=(Double)data.get(i)[j];
					out+=""+val.doubleValue();
				}else{
					out+="'"+data.get(i)[j]+"'";
				}
				
				
				if(j!=data.get(i).length-1){
					out+=",";
				}
			}
			out+="]";
			if(i!=data.size()-1){
				out+=",";
			}
		}
		out+="]";
		
		return out;
	}
	
	
}
