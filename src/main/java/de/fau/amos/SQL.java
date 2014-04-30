package de.fau.amos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.sql.DataSource;


public class SQL extends Energiedaten{

	//	public static String out="";

	//	public static void out(String msg){
	//		System.out.println(msg);
	//		out+=msg+"<br>";
	//	}

	public static void main(String args[]) {

		//		out("hallo");
		dosth(null);
	}


	public static void wasDimiGemachtHat(ServletConfig servletConfig){

		ArrayList<ArrayList<String>> data=new ArrayList<ArrayList<String>>();
		String fileName = "Auswertung-Tabelle.csv";

		File file=new File(servletConfig.getServletContext().getRealPath("/WEB-INF"),fileName);
		//		out("search for file at "+file.getAbsolutePath());


		String inputArray[] = null;
		FileReader fr = null;

		try {
			String lines;
			String inputFile = ""; 
			fr = new FileReader(file);
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



		InitialContext cxt;
		try {
			Class.forName("org.postgresql.Driver");

			cxt = new InitialContext();
			//			if ( cxt == null ) {
			//				try {
			//					throw new Exception("Uh oh -- no context!");
			//				} catch (Exception e) {
			//					// TODO Auto-generated catch block
			//					e.printStackTrace();
			//				}
			//			}

			DataSource ds = (DataSource) cxt.lookup( "java:/comp/env/jdbc/postgres" );






			Connection c = null;
			//			c=ds.getConnection();
			Statement stmt = null;
			try {
				c = DriverManager
						.getConnection("jdbc:postgresql://faui2o2j.informatik.uni-erlangen.de:5432/ss14-proj5",
								"ss14-proj5", "quaeF4pigheNgahz");
				System.out.println("Opened database successfully");
				stmt = c.createStatement();
				ResultSet rs;


				//				stmt.executeUpdate(Anlage1.createTable(Anlage1.getStandort(), Anlage1.getColumns()));
				//				for(int i = 0; i < Anlage1.insertValues(Anlage1.getStandort(), Anlage1.getColumns(), Anlage1.getValues()).length; i++){
				//					stmt.executeUpdate(Anlage1.insertValues(Anlage1.getStandort(), Anlage1.getColumns(), Anlage1.getValues())[i]);
				//				}


				String querryCommand="SELECT " + Anlage1.getColumns()[0] + " FROM " + Anlage1.getStandort() + ";";


				data=querry(servletConfig,querryCommand);




				c.close();	         
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(e.getClass().getName()+": "+e.getMessage());
				System.exit(0);
			}
			System.out.println("Operation done successfully");










		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			//		} catch (SQLException e1) {
			//			// TODO Auto-generated catch block
			//			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


	}


	public static ArrayList<ArrayList<String>> querry(ServletConfig servletConfig,String command){
		ArrayList<ArrayList<String>> data=new ArrayList<ArrayList<String>>();

		Connection c=null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			c = DriverManager
					.getConnection("jdbc:postgresql://faui2o2j.informatik.uni-erlangen.de:5432/ss14-proj5",
							"ss14-proj5", "quaeF4pigheNgahz");
			System.out.println("Opened database successfully");
			stmt = c.createStatement();

			ResultSet rs = stmt.executeQuery(command);
			ResultSetMetaData rsmd = rs.getMetaData();


			System.out.println("colcount"+rsmd.getColumnCount());


			ArrayList<String> row=new ArrayList<String>();
			if(rs.next()){
				System.out.println("entered");
				for(int i=1;i<=rsmd.getColumnCount();i++){
					System.out.println(rsmd.getColumnName(i));
					row.add(rsmd.getColumnName(i));
				}

				data.add(row);
			}		
			while ( rs.next() ) {	

				row=new ArrayList<String>();
				for(int i=1;i<=rsmd.getColumnCount();i++){
					row.add(rs.getString(i));
				}

				data.add(row);

				//			String a = rs.getString(Anlage1.getColumns()[0]);
				//			/* String b = rs.getString(Anlage1.getColumns()[1]);
				//	            String c1 = rs.getString(Anlage1.getColumns()[2]);
				//	            String d = rs.getString(Anlage1.getColumns()[3]);
				//	            String e = rs.getString(Anlage1.getColumns()[4]);
				//	            String f = rs.getString(Anlage1.getColumns()[5]);
				//	            String g = rs.getString(Anlage1.getColumns()[6]);
				//	            String h = rs.getString(Anlage1.getColumns()[7]);
				//	            String i = rs.getString(Anlage1.getColumns()[8]); */
				//			System.out.println(Anlage1.getColumns()[0] + " : " + a );
				//			/*  out(Anlage1.getColumns()[1] + " : " + b );
				//	            out(Anlage1.getColumns()[2] + " : " + c1 );
				//	            out(Anlage1.getColumns()[3] + " : " + d );
				//	            out(Anlage1.getColumns()[4] + " : " + e );
				//	            out(Anlage1.getColumns()[5] + " : " + f );
				//	            out(Anlage1.getColumns()[6] + " : " + g );
				//	            out(Anlage1.getColumns()[7] + " : " + h );
				//	            out(Anlage1.getColumns()[8] + " : " + i );		*/            
				//			System.out.println("");
			} 


			rs.close();
			stmt.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return data;
	}

	public static ArrayList<String> dosth(ServletConfig servletConfig){

		ArrayList<String> data=new ArrayList<String>();
		String fileName = "Auswertung-Tabelle.csv";

		File file=new File(servletConfig.getServletContext().getRealPath("/WEB-INF"),fileName);
		//		out("search for file at "+file.getAbsolutePath());


		String inputArray[] = null;
		FileReader fr = null;

		try {
			String lines;
			String inputFile = ""; 
			fr = new FileReader(file);
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



		InitialContext cxt;
		try {
			Class.forName("org.postgresql.Driver");

			cxt = new InitialContext();
			//			if ( cxt == null ) {
			//				try {
			//					throw new Exception("Uh oh -- no context!");
			//				} catch (Exception e) {
			//					// TODO Auto-generated catch block
			//					e.printStackTrace();
			//				}
			//			}

			DataSource ds = (DataSource) cxt.lookup( "java:/comp/env/jdbc/postgres" );






			Connection c = null;
			//			c=ds.getConnection();
			Statement stmt = null;
			try {
				c = DriverManager
						.getConnection("jdbc:postgresql://faui2o2j.informatik.uni-erlangen.de:5432/ss14-proj5",
								"ss14-proj5", "quaeF4pigheNgahz");
				System.out.println("Opened database successfully");
				stmt = c.createStatement();
				ResultSet rs;


				//				stmt.executeUpdate(Anlage1.createTable(Anlage1.getStandort(), Anlage1.getColumns()));
				//				for(int i = 0; i < Anlage1.insertValues(Anlage1.getStandort(), Anlage1.getColumns(), Anlage1.getValues()).length; i++){
				//					stmt.executeUpdate(Anlage1.insertValues(Anlage1.getStandort(), Anlage1.getColumns(), Anlage1.getValues())[i]);
				//				}
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
					/*  out(Anlage1.getColumns()[1] + " : " + b );
			            out(Anlage1.getColumns()[2] + " : " + c1 );
			            out(Anlage1.getColumns()[3] + " : " + d );
			            out(Anlage1.getColumns()[4] + " : " + e );
			            out(Anlage1.getColumns()[5] + " : " + f );
			            out(Anlage1.getColumns()[6] + " : " + g );
			            out(Anlage1.getColumns()[7] + " : " + h );
			            out(Anlage1.getColumns()[8] + " : " + i );		*/            
					System.out.println("");
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










		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			//		} catch (SQLException e1) {
			//			// TODO Auto-generated catch block
			//			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}



		return data;


	}

}
