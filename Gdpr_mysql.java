package gdpr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

public class Gdpr_mysql {
	
  private Connection connect = null;
  private Statement statement = null;
  private PreparedStatement preparedStatement = null;
  private static ResultSet resultSet = null;

   private static String host = "localhost:3306";
   private static String user = "root";
   private static String passwd = "password";
   private static String database = "gdpr";
   private static String table = "page";
  private static String path_file = "";
  
  public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Gdpr_mysql db = new Gdpr_mysql();
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Želite li stvoriti dump? [Y/N]");
	    String quer = sc.nextLine();
	      
	    while(!(quer.toLowerCase().equals("n") || quer.toLowerCase().equals("y"))) {
	    	System.out.println("Nepostojeća vrijednost! [Y/N]");
	    	quer = sc.nextLine();
	    }
	    
	    String pathstr;
	    String[] path_dp = new String[1];
	    if(quer.toLowerCase().equals("y")) {
	    	System.out.println("Upisite potrebne vrijednosti za spajanje na bazu:");
	    	System.out.println("host (npr. localhost:3306): ");
	    	host = sc.nextLine();
	    	System.out.println("user (npr. root): ");
	    	user = sc.nextLine();
	    	System.out.println("password: ");
	    	passwd = sc.nextLine();
	    	System.out.println("database (npr. gdpr): ");
	    	database = sc.nextLine();
	    	System.out.println("table (npr. page): ");
	    	table = sc.nextLine();
	    	
	    	System.out.println("Upisite direktorij u kojem zelite da se stvori dump: ");
	    	pathstr = sc.nextLine();
	    	Path path = Paths.get(pathstr);
	    	  
	    	while(!Files.exists(path)) {
	    		System.out.println("Nepostojani direktorij! Upisite novi: ");
	    		pathstr = sc.nextLine();
	    		path = Paths.get(pathstr);
	    	}
	    	  
	    	path_file = pathstr;
	    	  
	    	try {
	    		db.readDataBase();
	  		} catch (Exception e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}
	    	pathstr = pathstr + "dump.sql";
	    	path_dp[0] = pathstr;
	    	forward(path_dp);
	    	  
	    }
	    else {
	    	System.out.println("Upisite put do postojeceg sql file-a: ");
	    	pathstr = sc.nextLine();
	    	Path path2 = Paths.get(pathstr);
	    	  
	    	while(!Files.exists(path2)) {
	    		System.out.println("Nepostojani file! Upisite novi: ");
	    		pathstr = sc.nextLine();
	    		path2 = Paths.get(pathstr);
	    	}  
	    	path_dp[0] = pathstr;
	    	forward(path_dp);
	    }
	}
  
  public void readDataBase() throws Exception {
    try {
      Class.forName("com.mysql.jdbc.Driver");
      
      connect = DriverManager
          .getConnection("jdbc:mysql://" + host + "/" + database + "?"
              + "user=" + user + "&password=" + passwd );

      statement = connect.createStatement();
      
      Scanner sc = new Scanner(System.in);
      
  //    resultSet = statement.executeQuery("select * from page");
  // 	String criteria = writeColumns(resultSet);
      
  //    System.out.println("Unesite vrijednost: ");
  //	String value = sc.nextLine();
	    
  //    resultSet = statement.executeQuery("select * from page");
  //    List<String> values = writeResultSet(resultSet, criteria, value);
          
  //    DataPortbility.generatePDF(values, value);
      
  //    createCSV(values, value);
          
     
    	  
    	  createDumpSql(path_file);
    	
    	 
      
  //    DataPortbility.main(null);
      
      
    } catch (Exception e) {
      throw e;
    } finally {
      close();
    }

  }
  
  
  public static void forward(String[] path) throws IOException {
	  DataPortbility.main(path);
  }
  
  
  public static void createCSV(List<String> values, String query) throws FileNotFoundException, SQLException {
	  System.out.println("Creating CSV Document...");
	  PrintWriter pw = new PrintWriter(new 
				FileOutputStream("C:\\\\Users\\\\Edi\\\\Desktop\\\\gdpr\\\\"+query+".csv"));
      StringBuilder sb = new StringBuilder();
      
      for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
	      sb.append(resultSet.getMetaData().getColumnName(i));
	  }
      sb.append('\n');
      
      for(String item : values) {
    	  if(item.equals("ROW END")) {
    		  sb.setLength(sb.length() - 1);
    		  sb.append('\n');
    		  break;
    	  }
    	  String[] temp = item.split(": ");
    	  if(temp.length > 1) sb.append(temp[1]);
    	  else sb.append("");
    	  sb.append(',');
      }
      
      sb.append('\n');

      pw.write(sb.toString());
      pw.close();
      System.out.println("CSV Document created");
      System.out.println("------------");
      
  }

  private void createDump() {
	  String path = "C:/Users/Edi/Desktop/dump.sql";
	  
	  // C:\Program Files\MySQL\MySQL Server 5.5\bin>mysqldump -u root -ppassword --add-drop-database -B gdpr > C:\Users\Edi\Desktop\asd.sql
	  String executeCmd = "cd C:\\Program Files\\MySQL\\MySQL Server 5.5\\bin\\" + " & " + "mysqldump -u " + user + " -p" 
			  + passwd + " --add-drop-database -B " + database + " > " + "C:\\Users\\Edi\\Desktop\\gdpr\\dump.sql";
      Process runtimeProcess;
      try {
          runtimeProcess = Runtime.getRuntime().exec(new String[] { "cmd.exe", "/c", executeCmd });
    	  
          //runtimeProcess = Runtime.getRuntime().exec(executeCmd);

          int processComplete = runtimeProcess.waitFor();

          if (processComplete == 0) {
              System.out.println("Backup stvoren");
              System.out.println("------------");
          } else {
              System.out.println("Neuspješno stvaranje dumpa");
              System.out.println("------------");
          }
      } catch (Exception ex) {
          ex.printStackTrace();
      }
  }
  
  
  private void createDumpSql(String path) throws SQLException, IOException {
	  resultSet = statement.executeQuery("SHOW CREATE TABLE " + table + "\r\n");
	  ResultSet resultSet2 = null;
	  String set;
	  String name = "NO_NAME";
	  boolean flag = false;
	  StringBuilder sb = new StringBuilder();

	  BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path + "dump.sql"))));
	  while(resultSet.next()) {
    	  for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
    		  if(!flag) {
    			  name = resultSet.getString(i);
    			  flag = true;
    		  }
    		  set = resultSet.getString(i);
    		  sb.append(set);
    		  sb.append(System.lineSeparator());
    	  }
	  }
	  sb.setLength(sb.length() - 2);
	  sb.append(";");
	  sb.append(System.lineSeparator());
	  br.write(sb.toString());
	  
	  br.write(" ");
	  
	  resultSet2 = statement.executeQuery("SELECT * FROM " + table);
   	 
	  String end = "";
	  end = end + "INSERT INTO " + name + " VALUES ";
	  br.write(end);
	  br.newLine();
	  while(resultSet2.next()) {
   		 String temp;
   		 end = "(";
	   	 for (int i = 1; i <= resultSet2.getMetaData().getColumnCount(); i++) {
	   		 temp = resultSet2.getString(i);
	   		// System.out.println(temp);
	   		 if(temp.contains(",")) {
	   			 end = end + "'" + temp + "'" + ",";
	   		 }
	   		 else {
	   			 end = end + temp + ",";
	   		 }
	      }
	   	 end = end.substring(0, end.length() - 1);

	   	 end = end + "),";
	   	 
	   	 if(resultSet2.isLast()) {
	   		 end = end.substring(0, end.length() - 1);
	   	   	 end = end + ";";
	   	 }
	   	 br.write(end);
	   	 br.newLine();
   	 }
   	 br.close();
	  
   	 System.out.println("Dump stvoren");
   	 return;
  }
  
  
  private String writeColumns(ResultSet resultSet) throws SQLException {
		Scanner sc = new Scanner(System.in);
	    System.out.println("Columns:");
	    
	    System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
	    
	    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
	      System.out.println(resultSet.getMetaData().getColumnName(i));
	    }
	    
	    System.out.println("Odaberite jedan od kriterija: ");
	    String criteria = sc.nextLine();
	    
	    return criteria;
  }

 
  private List<String> writeResultSet(ResultSet resultSet, String criteria, String value) throws SQLException {
	  boolean found = false;
      List<String> values = new ArrayList<String>();
      
      while (resultSet.next()) {
    	  String crit = resultSet.getString(criteria.trim());
	      if(crit.toLowerCase().contains(value.toLowerCase())) {
	    	  System.out.println("--> " + crit);
	    	  System.out.println("------------");
	    	  for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
	    	      String x = resultSet.getMetaData().getColumnName(i);
	    	      values.add(x + ": " + resultSet.getString(x));
	    	  }
	    	  values.add("ROW END");
	    	  found = true;
	      }
    }
    
    if(!found) System.out.println("Nije nađen unos s takvom vrijednosti."); 
    
    return values;
  }

  private void close() {
	  try {
		if (resultSet != null) {
		resultSet.close();
      }

      if (statement != null) {
        statement.close();
      }

      if (connect != null) {
        connect.close();
      }
    } catch (Exception e) {

    }
  }

}