package com.star.NexureUserPunisher.DataSources;

import java.util.List;
import java.util.Map;

/**
 * Interface to allow switching between FlatFile and SQL
 * @author Star
 *
 */
public interface DataSource {

	/**
	 * Gets the first row that matches the given value in the given column name
	 * @param id
	 * @return
	 */
	public abstract Map<String, String> getRow(String columnName, String value);
	
	/**
	 * Gets all rows that match the given value in the given column name
	 * @param columnName
	 * @param match - the value to match
	 * @return
	 */
	public abstract List<Map<String, String>> getRows(String columnName, String value);

	/**
	 * Attempts to add a new row
	 * @param rowData
	 * @return true if row was added, false if duplicate
	 */
	public abstract void addRow(Map<String, String> rowData);
	
	public abstract String getMax(String columnName);
}
