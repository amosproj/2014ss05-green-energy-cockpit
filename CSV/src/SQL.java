import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;


public class SQL extends Energiedaten{
	
	public static void main(String args[]) {
	      
		
		String fileName = "Auswertung-Tabelle.csv";
		String inputArray[] = null;
		FileReader fr = null;
		
        try {
        	String lines;
        	String inputFile = ""; 
            fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);
            
            while((lines = br.readLine()) != null) {
                inputFile += lines;           
            }
            br.close();
            
            inputArray = inputFile.split(";");
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }			

        
		SQL Anlage1 = new SQL();
		Anlage1.setStandort("Dresden");
		Anlage1.setColumns(inputArray);
		Anlage1.setValues(inputArray);

		
		Connection c = null;
		Statement stmt = null;
	      try {
	         Class.forName("org.postgresql.Driver");
	         c = DriverManager
	            .getConnection("jdbc:postgresql://faui2o2j.informatik.uni-erlangen.de:5432/ss14-proj5",
	            "ss14-proj5", "quaeF4pigheNgahz");
	         System.out.println("Opened database successfully");
		     stmt = c.createStatement();
		     ResultSet rs;
		     
		     
	         stmt.executeUpdate(Anlage1.createTable(Anlage1.getStandort(), Anlage1.getColumns()));
		     for(int i = 0; i < Anlage1.insertValues(Anlage1.getStandort(), Anlage1.getColumns(), Anlage1.getValues()).length; i++){
		    	 stmt.executeUpdate(Anlage1.insertValues(Anlage1.getStandort(), Anlage1.getColumns(), Anlage1.getValues())[i]);
		     }
		     rs = stmt.executeQuery("SELECT " + Anlage1.getColumns()[0] + " FROM " + Anlage1.getStandort() + ";");
		     while ( rs.next() ) {		    	 		    	 
		            String a = rs.getString(Anlage1.getColumns()[0]);
		           /* String b = rs.getString(Anlage1.getColumns()[1]);
		            String c1 = rs.getString(Anlage1.getColumns()[2]);
		            String d = rs.getString(Anlage1.getColumns()[3]);
		            String e = rs.getString(Anlage1.getColumns()[4]);
		            String f = rs.getString(Anlage1.getColumns()[5]);
		            String g = rs.getString(Anlage1.getColumns()[6]);
		            String h = rs.getString(Anlage1.getColumns()[7]);
		            String i = rs.getString(Anlage1.getColumns()[8]); */
		            System.out.println(Anlage1.getColumns()[0] + " : " + a );
		          /*  System.out.println(Anlage1.getColumns()[1] + " : " + b );
		            System.out.println(Anlage1.getColumns()[2] + " : " + c1 );
		            System.out.println(Anlage1.getColumns()[3] + " : " + d );
		            System.out.println(Anlage1.getColumns()[4] + " : " + e );
		            System.out.println(Anlage1.getColumns()[5] + " : " + f );
		            System.out.println(Anlage1.getColumns()[6] + " : " + g );
		            System.out.println(Anlage1.getColumns()[7] + " : " + h );
		            System.out.println(Anlage1.getColumns()[8] + " : " + i );		*/            
		            System.out.println();
		     } 

		     
		     
		     rs.close();
	         stmt.close();
	         c.close();	         
	      } catch (Exception e) {
	         e.printStackTrace();
	         System.err.println(e.getClass().getName()+": "+e.getMessage());
	         System.exit(0);
	      }
	      System.out.println("Operation done successfully");
	      	      	      	      	    	      
	   }

}
