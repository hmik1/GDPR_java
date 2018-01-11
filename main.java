import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

public class main {

	/*
	 * TO DO
	 * 
	 * 
	 * 
	 * @ Odredi Primarne kljuèeve
	 * 
	 * @ Ako red koji odgovara queryju ima vrijednosti koje su negdje primarni
	 * kljuè, dodaj i vrijednosti iz te tablice za taj primarni kljuè
	 * 
	 * @ CSV pisanje
	 * @ lokacija dumpa iz komandne linije
	 * @ Javadoc
	 */
	public static void main(String[] args) throws IOException, DocumentException {
		List<Table> tables = new ArrayList<Table>();

		// Table table = new Table();
		 String path ="C:/Users/mikul/Documents/PROJEKT/backup.sql";
		//String path = "C:/Users/mikul/Desktop/studAdmin_dump.sql";
		//String path = "C:/Users/mikul/Desktop/proba.sql";
		//String path = "C:/Users/mikul/Documents/PROJEKT/gdpr.sql";
		// String path = args[0];

		File file = new File(path);
		/*
		 * System.out.print("Please enter your query: "); Scanner sc = new
		 * Scanner(System.in); query = sc.nextLine(); sc.close();
		 */

		// uèitavanje pojedinaènih tablica
		readTables(path, tables);

		/*for (Table tab : tables) {
			System.out.println("Table name " + tab.getTableName());
			// System.out.println(tab.getColumnNames().length);
			for (String col : tab.getColumnNames()) {
				System.out.print(col + ", ");
			}
			System.out.println();
			System.out.println("Vrijednosti");
			for (String col : tab.getValues()) {
				System.out.println(col);
			}
		}*/
		// pretraga
		find(tables);

		
		  /*FileWriter writer = new FileWriter("C:\\Users\\mikul\\Desktop\\output.txt");
		  
		  System.out.println(tables.size()); for (Table t : tables) {
		  writer.write(t.getTableName() + "\n"); //
		  System.out.println(t.getTableName()); for (String str : t.getValues()) { //
		  System.out.println(t.getValues().size()); writer.write(str + "\n"); //
		 System.out.println(str); } } writer.close();*/
		 
		System.out.println("KRAJ");
	}

	public static String clean(String data) {
		if (data.charAt(0) == '`' && data.charAt(data.length() - 1) == '`') {
			return data.substring(1, data.length() - 1);
		}
		return data;
	}

	public static void readTables(String file, List<Table> tables) {
		try {
			// List<Table> tables = new ArrayList<Table>();
			// List<String> allLines = Files.readAllLines(Paths.get(path));
			InputStreamReader fileReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			// StringBuffer stringBuffer = new StringBuffer();
			String line;
			Table table = new Table();
			while ((line = bufferedReader.readLine()) != null) {

				String[] words = line.split("[(\\s\\)]");
				// for( String word : words)System.out.println(word);

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
					//System.out.println("TABLE NAME IS " + tname);
					List<String> columns = new ArrayList<>();
					while ((line = bufferedReader.readLine()) != null && !line.contains(";")) {
						String[] chunk = line.trim().split(" ");
						// System.out.println(chunk[0]);
						String test1 = chunk[0];
						String test2 = chunk[0] + " " + chunk[1];
						// !!!!KAKO RAZLIKOVATI DEFINICIJU STUPCA I POÈETAK ATRIBUTA TABLICE (KEYS,
						// OGRANIÈENJA...)
						// ako je stupac onda ne smije poèinjati sa NOT NULL, UNIQUE, PRIMARY KEY,
						// FOREIGN KEY, CHECK, DEFAULT, REFERENCES
						String reserved1 = "UNIQUE CHECK DEFAULT REFERENCES KEY";
						String reserved2 = "NOT NULL PRIMARY KEY FOREIGN KEY";
						if (!reserved1.contains(test1) && !reserved2.contains(test2)) {
							// System.out.println("Definicija stupca: " + test1);
							columns.add(clean(test1));
						}
					}
					table.setTableName(tname);
					String[] sCols = columns.toArray(new String[0]);
					/*
					 * for(String col : sCols) { System.out.println(col); }
					 */
					table.setColumnNames(sCols);
					/*
					 * System.out.println("IZ TABICE"); for(String col : table.getColumnNames()) {
					 * System.out.println(col); }
					 */
					tables.add(new Table(table));
				}

				/*
				 * for(Table tab : tables) { System.out.println("Table name " +
				 * tab.getTableName()); for(String col : tab.getColumnNames())
				 * System.out.print(col + ", " ); }
				 */
				String tName = "";
				if (Arrays.asList(words).contains("INSERT")) {
					// System.out.println(words[0]);
					//table.clear();
					tName = clean(words[2]);
					/*
					 * String[] tColumnNames = words[3].split(","); for( int i = 0; i <
					 * tColumnNames.length; i++) {
					 * 
					 * tColumnNames[i] = clean(tColumnNames[i]); }
					 */

					//table.setTableName(clean(tName));
					// table.setColumnNames(tColumnNames);

					// System.out.println("OVDJE SAM " + table.getTableName());
					// System.out.println(table.getColumnNamesString());

				}
				
				// System.out.println(table.getTableName());
				List<String> values = new ArrayList<String>();
				if (Arrays.asList(words).contains("VALUES")) {
					boolean end = false;
					// System.out.println("CURRENT LINE: " + line );
					String tmp = new String("");
					try {
						tmp = new String(line.split("VALUES")[1].trim());
					} catch (Exception e) {
					}
					//System.out.println("Rest of line: " + tmp);
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
								//System.out.println("Table name " + tab.getTableName());
								if(tab.getTableName().equals(tName)) {
									//System.out.println("Dodajem");
									//System.out.println("Tab1 " + tab.getTableName() + " tab2 " + tName);
									tab.addValues(tmp.substring(1, tmp.length()-1));
									break;
								}
							}
							

							/*for (Table tab : tables) {
								System.out.println("Table name " + tab.getTableName());
								// System.out.println(tab.getColumnNames().length);
								for (String col : tab.getColumnNames()) {
									System.out.print(col + ", ");
								}
								System.out.println();
								System.out.println("Vrijednosti");
								for (String col : tab.getValues()) {
									System.out.println(col);
								}
							}
							Scanner s = new Scanner(System.in);
							s.nextLine();
							s.close();*/
						} catch (StringIndexOutOfBoundsException e) {
						}
						
					
					}
					if (end == false) {
						while ((line = bufferedReader.readLine()) != null) {
							// System.out.println(line);

							try {
								if (line.substring(line.length() - 1).equals(";")) {
									end = true;
								}
							} catch (StringIndexOutOfBoundsException e) {
								// System.out.println("Greška!!!!!! " + line);
							}
							// table.addValues(new String(line));
							try {
								//System.out.println("Stavljam");
								values.add(line.substring(1, line.length() - 1));
							} catch (StringIndexOutOfBoundsException e) {
							}
							if (end == true) {
								break;

							}
						}
					}
					/*for( String val : values) {
						System.out.println("VRIJEDNOST __> " + val);
					}*/
					boolean exists = false;
					for (Table tab : tables) {
						if (tab.getTableName().equals(table.getTableName())) {
							tab.addValues(values);
							//System.out.println("DODANA VRIJEDNOST u " + tab.getTableName());
							exists = true;
						}
					}
					if (!exists) {
						// System.out.println("NE postoji");
						tables.add(new Table(table.getTableName(), table.getColumnNames(), values));
					}
				}
			}
			bufferedReader.close();

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("No File");
		}

		// return tables;
	}

	public static void find(List<Table> tables) throws DocumentException, IOException {
		String query = "";
		Scanner sc = new Scanner(System.in);
		while (!query.equals("N") || !query.equals("n")) {
			System.out.print("Please enter your query: ");

			query = sc.nextLine();

			List<String> result = new ArrayList<>();
			for (Table table : tables) {
				result.addAll(table.find(query));
			}

			if (result.isEmpty()) {
				System.out.println("NO MATCHES");
				// result.add("NO MATCHES");
			} else {
				generatePDF(result, query);
				generateCSV(result, query);
			}
			/*
			 * try { FileWriter out = new
			 * FileWriter("C:\\Users\\mikul\\Desktop\\" + query + ".txt"); for (String row :
			 * result) { out.write(row + "\n"); } out.close(); } catch (IOException e) {
			 * 
			 * }
			 */

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

	public static void generatePDF(List<String> result, String query) throws DocumentException, IOException {
		System.out.println("Creating PDF Document...");
		Document document = new Document();
		BaseFont arial = BaseFont.createFont("c:\\windows\\fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		Font NormalFont = new com.itextpdf.text.Font(arial, 12, Font.NORMAL);
		Font boldFont = new com.itextpdf.text.Font(arial, 12, Font.BOLD);
		try {
			// PdfWriter writer = PdfWriter.getInstance(document, new
			// FileOutputStream("C:\\\\Users\\\\mikul\\\\Desktop\\\\"+query+".pdf"));
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
	}

	public static void generateCSV(List<String> result, String query) {

	}
}
