import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
 /**
  * Main class of the program. Reads the SQL dump file, creates tables, searches for given phrases in tables, creates output files.
  * @author Helena Mikulic
  *
  */
public class DataPortbility {
	/**
	 * Main method of the program. Calls methods for reading the tables and finding the searched phrase
	 * @param args String path to the dump file
	 */
	public static void main(String[] args) {
		List<Table> tables = new ArrayList<Table>();
		String path ="C:/Users/mikul/Documents/PROJEKT/backup.sql";
		//String path = "C:/Users/mikul/Desktop/studAdmin_backup.sql";
		// String path = "C:/Users/mikul/Desktop/prim.sql";
		// String path = "C:/Users/mikul/Desktop/proba.sql";
		// String path = "C:/Users/mikul/Documents/PROJEKT/gdpr.sql";
		//String path = args[0];

		readTables(path, tables);

		find(tables);

		System.out.println("Program End");
	}
	/**
	 * Removes added "`" signs from beginning and end of the table and column names and values
	 * @param data String value to check for and remove surrounding "`" signs
	 * @return
	 */
	public static String clean(String data) {
		if (data.charAt(0) == '`' && data.charAt(data.length() - 1) == '`') {
			return data.substring(1, data.length() - 1);
		}
		return data;
	}
	/**
	 * Method for reading the dump file and creating tables from it.
	 * When encountering "CREATE TABLE" command in dump file, this method creates a new table with a tableName, columnNames 
	 * and primaryKeys and adds it to the tables list. When When encountering "INSERT" command it records the tableName in which to 
	 * insert values and finally, when encountering "VALUES" command it adds all values, from the keyword "VALUES" until end sign ";", to
	 * the table with name registered in tableName variable
	 * @param file String path to the dump file
	 * @param tables List of tables List for storing all the tables generated from the dump file
	 */
	public static void readTables(String file, List<Table> tables) {
		try {
			InputStreamReader fileReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			Table table = new Table();
			while ((line = bufferedReader.readLine()) != null) {

				String[] words = line.split("[(\\s\\)]");

				if (words[0].equals("CREATE") && words[1].equals("TABLE")) {
					String tname = "";
					table.clear();
					if (words[words.length - 1].equals("(")) {
						tname = new String(words[words.length - 1]);
						tname = clean(tname);
					} else {
						tname = new String(words[words.length - 1]);
						if (tname.charAt(tname.length() - 1) == '(') {
							tname = tname.substring(0, tname.length() - 1);
						}
						tname = clean(tname);
					}

					List<String> columns = new ArrayList<>();
					List<String> primKeys = new ArrayList<>();
					while ((line = bufferedReader.readLine()) != null && !line.contains(";")) {
						String[] chunk = line.trim().split(" ");
						String test1 = chunk[0];
						String test2 = chunk[0] + " " + chunk[1];
						String reserved1 = "UNIQUE CHECK DEFAULT REFERENCES KEY";
						String reserved2 = "NOT NULL PRIMARY KEY FOREIGN KEY";
						if (!reserved1.contains(test1) && !reserved2.contains(test2)) {
							columns.add(clean(test1));
						}
						if (test2.equals("PRIMARY KEY") || test1.equals("KEY")) {
							String key = line.split("\\(")[1];
							key = clean(key.split("\\)")[0]);
							for (String k : key.split(",")) {
								primKeys.add(k);
							}

						}
					}
					table.setTableName(tname);
					String[] sCols = columns.toArray(new String[0]);
					table.setColumnNames(sCols);
					table.setPrimaryKeys(primKeys.toArray(new String[0]));
					tables.add(new Table(table));
				}
				String tName = "";
				
				if (Arrays.asList(words).contains("INSERT")) {
					tName = clean(words[2]);
				}

				List<String> values = new ArrayList<String>();
				if (Arrays.asList(words).contains("VALUES")) {
					boolean end = false;
					String tmp = new String("");
					try {
						tmp = new String(line.split("VALUES")[1].trim());
					} catch (Exception e) {
					}
					if (!tmp.equals("")) {
						try {
							if (tmp.substring(tmp.length() - 1).equals(";")) {
								end = true;
							}
						} catch (StringIndexOutOfBoundsException e) {
							System.out.println("Greška!!!!!! " + line);
						}
						try {
							for (Table tab : tables) {
								if (tab.getTableName().equals(tName)) {
									tab.addValue(tmp.substring(1, tmp.length() - 1));
									break;
								}
							}

						} catch (StringIndexOutOfBoundsException e) {
						}

					}
					if (end == false) {
						while ((line = bufferedReader.readLine()) != null) {

							try {
								if (line.substring(line.length() - 1).equals(";")) {
									end = true;
								}
							} catch (StringIndexOutOfBoundsException e) {
							}

							try {
								values.add(line.substring(1, line.length() - 1));
							} catch (StringIndexOutOfBoundsException e) {
							}
							if (end == true) {
								break;
							}
						}
					}
					boolean exists = false;
					for (Table tab : tables) {
						if (tab.getTableName().equals(table.getTableName())) {
							tab.addValues(values);
							exists = true;
						}
					}
					if (!exists) {
						tables.add(new Table(table.getTableName(), table.getColumnNames(), values));
					}
				}
			}
			bufferedReader.close();

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("No File");
			System.out.println("Exiting program");
			System.exit(1);
		}
	}
	/**
	 * This method returns all the tables connected by Primary key or Key values to the tables currently used
	 * @param tables List of tables List for storing all the tables generated from the dump file
	 * @param usedTables List of tables List of all tables where data about the searched phrase is stored 
	 * @return List of Tables tables connected to usedTables through Primary key or Key values
	 */
	public static List<Table> dependableTable(List<Table> tables, List<Table> usedTables) {
		List<Table> dep = new ArrayList<>();
		List<String> names = new ArrayList<>();
		List<String> columns = new ArrayList<>();
		for (Table tab : usedTables) {
			names.add(tab.getTableName());
			for (String col : tab.getColumnNames()) {
				columns.add(col);
			}
		}
		for (Table tab : tables) {
			if (!names.contains(tab.getTableName())) {
				for (String pkey : tab.getPrimaryKeys()) {
					if (columns.contains(pkey)) {
						dep.add(tab);
						break;
					}
				}
			}
		}

		return dep;
	}
	/**
	 * This method extracts data about the searched phrase from all connected tables.
	 * The method recursively passes through the list of tables connected to usedTables and extracts data about the searched phrase
	 * from new tables by getting data by the value of the PRIMARY KEY in dependable table. Returns the data given to it in res variable
	 * merged with new data from connected tables
	 * @param tables List of tables List for storing all the tables generated from the dump file
	 * @param usedTables List of tables List of all tables where data about the searched phrase is stored 
	 * @param res List of String All the data found about searched phrase in the format "columnName: value" 
	 * and "ROW END" to signify end of a single row in table
	 * @return List of String All the data found about searched phrase in the format "columnName: value" 
	 * and "ROW END" to signify end of a single row in table
	 */
	public static List<String> linkTables(List<Table> tables, List<Table> usedTables, List<String> res) {
		List<Table> connectedTable = dependableTable(tables, usedTables);
		for (Table tab : connectedTable) {
			usedTables.add(tab);
			List<String> tmp = new ArrayList<>();
			List<String> novi = new ArrayList<>();
			for (String pair : res) {
				if (pair.equals("ROW END")) {
					novi.addAll(tmp);
					tmp.clear();
				}
				if (Arrays.asList(tab.getPrimaryKeys()).contains(pair.split(": ")[0])) {
					tmp.addAll(tab.find(pair.split(": ")[1].trim()));
				} else {
					novi.add(pair);
				}
			}
			res = new ArrayList<>(novi);
			novi.clear();
		}
		if (connectedTable.size() == 0) {
			return res;
		}
		return linkTables(tables, usedTables, res);
	}
	/**
	 * Method for locating the searched phrase in the database. Method asks for user's phrase, finds it within the created tables 
	 * and then calls for linkTables method to extract the data from connected tables. Once all data is gathered the method calls generatePDF
	 * and generateCSV methods for creating output files. The method runs until the user has no other phrases to search for.
	 * @param tables List of tables List for storing all the tables generated from the dump file
	 */
	public static void find(List<Table> tables){
		String query = "";
		Scanner sc = new Scanner(System.in);
		while (!query.equals("N") || !query.equals("n")) {
			System.out.print("Please enter your query: ");

			query = sc.nextLine();

			List<String> result = new ArrayList<>();
			List<String> res = new ArrayList<>();
			List<Table> usedTables = new ArrayList<>();
			for (Table table : tables) {
				res.addAll(new ArrayList<>(table.find(query)));
				if (!res.isEmpty()) {
					usedTables.add(table);
					result.addAll(linkTables(tables, usedTables, res));
				} else {
					result.addAll(res);
				}
				res.clear();
				usedTables.clear();
			}

			if (result.isEmpty()) {
				System.out.println("NO MATCHES");
			} else {
				generatePDF(result, query);
				generateCSV(result, query);
			}

			while (true) {
				System.out.println("Do you want to search another query? [Y/N]");
				query = sc.nextLine();
				if (query.equals("Y") || query.equals("y") || query.equals("N") || query.equals("n")) {
					break;
				}
			}
			if (query.equals("N") || query.equals("n")) {
				break;
			}

		}
		sc.close();
	}
	/**
	 * This method generates a PDF file with a name <searched phrase>.pdf in the root folder of the program. Each row of data is 
	 * written on a seperate page in the format "<b>columnName:</b> value \n".
	 * @param result List of String All the data found about searched phrase in the format "columnName: value" 
	 * and "ROW END" to signify end of a single row in table
	 * @param query String the searched phrase - used for naming the document
	 */
	public static void generatePDF(List<String> result, String query){
		System.out.println("Creating PDF Document...");
		Document document = new Document();
		try {
		BaseFont arial = BaseFont.createFont("c:\\windows\\fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		Font NormalFont = new com.itextpdf.text.Font(arial, 12, Font.NORMAL);
		Font boldFont = new com.itextpdf.text.Font(arial, 12, Font.BOLD);
		try {
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(query + ".pdf"));

			document.open();
			for (String item : result) {
				if (item.equals("ROW END")) {
					document.newPage();
					continue;
				}
				String columnName = item.split(": ")[0];
				String curValue = "";
				try {
					curValue = item.split(": ")[1];
				} catch (ArrayIndexOutOfBoundsException e) {
				}
				Paragraph p = new Paragraph();
				p.add(new Chunk(columnName + ": ", boldFont));
				p.add(new Chunk(curValue, NormalFont));
				document.add(p);
			}

			document.close();
			writer.close();
			System.out.println("PDF Document created");
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		}catch( DocumentException | IOException e) {
			
		}
	}
	/**
	 * This method generates a CSV file with a name <searched phrase>.csv in the root folder of the program. Each row of data is 
	 * written in two rows: first one consisting of column names and the second one containing values
	 * @param result List of String All the data found about searched phrase in the format "columnName: value" 
	 * and "ROW END" to signify end of a single row in table
	 * @param query String the searched phrase - used for naming the document
	 */
	public static void generateCSV(List<String> result, String query) {
		System.out.println("Creating CSV Document...");
		PrintWriter pw;
		try {
			pw = new PrintWriter(new FileOutputStream(query + ".csv"));

			StringBuilder sb = new StringBuilder();
			StringBuilder key = new StringBuilder();
			StringBuilder value = new StringBuilder();

			for (String item : result) {
				if (item.equals("ROW END")) {
					if (key.length() == 0 && value.length() == 0) {
						continue;
					}
					sb.append(key);
					sb.append('\n');
					sb.append(value);
					sb.append('\n');
					key.setLength(0);
					value.setLength(0);
					continue;
				}
				String[] temp = item.split(": ");
				if (temp.length > 1) {
					key.append(temp[0]);
					value.append(temp[1]);
				} else {
					key.append("");
					value.append("");
				}
				key.append(',');
				value.append(',');
			}

			sb.append('\n');

			pw.write(sb.toString());
			pw.close();
			System.out.println("CSV Document created");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
