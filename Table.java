import java.util.*;
/**
 * Class for storing tables. Contains String tableName, two arrays of Strings: columnNames and primaryKeys and a List of Strings that contain values.
 * Each item in the list represents one whole row in the table.
 * @author Helena Mikulic
 *
 */
public class Table {
	private String tableName;
	private String[] columnNames;
	private String[] primaryKeys;
	private List<String> values = new ArrayList<String>();
	/**
	 * Empty constructor
	 */
	public Table() {
		tableName = "";
		columnNames = null;
		primaryKeys = null;
		values = new ArrayList<String>();
	}
	/**
	 * 
	 * @param tName String tableName
	 * @param tColumns Array of strings representing column names
	 * @param values List of strings representing table values
	 */
	public Table(String tName, String[] tColumns, List<String> values) {
		tableName = tName;
		columnNames = tColumns;
		this.values = values;
	}
	/**
	 * 
	 * @param table A table from which new table is derived
	 */
	public Table(Table table) {
		this.tableName = table.getTableName();
		this.columnNames = table.getColumnNames();
		this.primaryKeys = table.getPrimaryKeys();
		this.values = new ArrayList<String>(table.getValues());
	}
	/**
	 * @return String tableName
	 */
	public String getTableName() {
		return tableName;
	}
	/**
	 * @param tableName String tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	/**
	 * @return Array of strings that represent the columns
	 */
	public String[] getColumnNames() {
		return columnNames;
	}
	
	/**
	 * @param columnNames Array of strings that represent the columnNames to set
	 */
	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;	
	}
	/**
	 * @return Array of strings that represent the primaryKeys
	 */
	public String[] getPrimaryKeys() {
		return primaryKeys;
	}
	/**
	 * @param primaryKeys Array of strings that represent the primaryKeys to set
	 */
	public void setPrimaryKeys(String[] primaryKeys) {
		this.primaryKeys = primaryKeys;
	}
	/**
	 * @return List of Strings that represent table values
	 */
	public List<String> getValues() {
		return values;
	}
	/**
	 * Method for adding values to the table
	 * @param values List of Strings representing the values to be added to the table
	 */
	public void addValues(List<String> values) {
		for( String val : values) {
			this.values.add(new String(val));
		}
	}
	/**
	 * Mathod for adding a single row to the table
	 * @param substring String a row to be added to the table
	 */
	public void addValue(String substring) {
		this.values.add(substring);
	}
	/**
	 * Method that finds all rows in a table and returns a list of Strings with matching rows. Returned list
	 * contains Strings in format "columnName: value" and a String "ROW END" which marks the end of a single row in table
	 * @param query String a phrase to search for in the table
	 * @return List of String items containing values of rows in which the query has been found
	 */
	public List<String> find( String query){
		List<String> result = new ArrayList<String>();		
		for (String row : values) {
			if (row.matches("(?i).*\\b" + query + "\\b.*")){
				List<String> sepVal = new ArrayList<String>();
	            String value = "";
	            boolean ignore = false;
	            boolean flag = false;
	            for (int i = 0; i < row.length()-1; i++)
	            {
	            	char character = row.charAt(i);
	                if (character == '\\')
	                {
	                    flag = true;
	                }
	                else if (character == '\'' && flag == false)
	                {
	                    ignore = !ignore;
	                }
	                else if (character != ',' || ignore == true)
	                {
	                    value += character;
	                    flag = false;
	                }
	                else
	                {
	                    sepVal.add(value);
	                    value = "";
	                }
	            }
	            sepVal.add(value);
	            for(int i = 0; i < sepVal.size(); i++ ) {
	            	result.add(new String(this.columnNames[i] + ": " + sepVal.get(i)));					
	            }
	            
	            result.add("ROW END");
			}
		}		
		return result;
	}
	/**
	 * Method for checking if table contains a row with a searched for value. Returns true if the row is found, false otherwise.
	 * @param value String a value to check for in the table
	 * @return boolean true if table contains a row containing the searched value
	 */
	public boolean contains(String value) {
		for (String val : this.values) {
			if (val.equals(value))
				return true;
		}
		return false;
	}
	/**
	 * Method for emptying the table
	 */
	public void clear() {
		tableName = "";
		tableName = "";
		columnNames = null;
		primaryKeys = null;
		values.clear();		
	}
}
