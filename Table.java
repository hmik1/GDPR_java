import java.util.*;

public class Table {
	private String tableName;
	private String[] columnNames;
	private String[] primaryKeys;
	// u svaki element values se upisuje jedan redak tablice, npr. values[i] = "ime","prezime","dob"...
	private List<String> values = new ArrayList<String>();
	
	public Table() {
		tableName = "";
		columnNames = null;
		primaryKeys = null;
		values = new ArrayList<String>();
	}
	
	public Table(String tName, String[] tColumns, List<String> values) {
		tableName = tName;
		columnNames = tColumns;
		this.values = values;
	}
	
	public Table(Table table) {
		this.tableName = table.getTableName();
		this.columnNames = table.getColumnNames();
		this.primaryKeys = table.getPrimaryKeys();
		this.values = new ArrayList<String>(table.getValues());
		/*for(String col : this.columnNames) {
			System.out.println(col);
		}*/
	}
	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}
	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	/**
	 * @return the columnNames
	 */
	public String[] getColumnNames() {
		return columnNames;
	}
	
	/**
	 * @param columnNames the columnNames to set
	 */
	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;	
	}
	/**
	 * @return the primaryKeys
	 */
	public String[] getPrimaryKeys() {
		return primaryKeys;
	}
	/**
	 * @param primaryKeys the primaryKeys to set
	 */
	public void setPrimaryKeys(String[] primaryKeys) {
		this.primaryKeys = primaryKeys;
	}
	/**
	 * @return the values
	 */
	public List<String> getValues() {
		return values;
	}

	public void addValues(List<String> values) {
		for( String val : values) {
			this.values.add(new String(val));
		}
	}
	
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
	            //System.out.println("Broj kolumni: " + columnNames.length + " broj vrijednosti: " + sepVal.size());
	            for(int i = 0; i < sepVal.size(); i++ ) {
					//System.out.println(this.columnNames[i] + ": " + sepVal.get(i));
	            	result.add(new String(this.columnNames[i] + ": " + sepVal.get(i)));
					
	            }
	            //result.addAll(sepVal);
	            result.add("ROW END");
			}
		}
		
		return result;
	}
	
	public boolean contains(String values) {
		for (String val : this.values) {
			if (val.equals(values))
				return true;
		}
		return false;
		
	}
	
	public void clear() {
		tableName = "";
		tableName = "";
		columnNames = null;
		primaryKeys = null;
		values.clear();
				
	}

	
	public void addValues(String substring) {
		this.values.add(substring);
		//System.out.println(this.tableName);
		
		
	}
	

}
