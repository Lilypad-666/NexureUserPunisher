package com.star.NexureUserPunisher.DataSources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.star.NexureUserPunisher.Configs.Settings;

public class Database implements DataSource {
	String tableName;
	Map<String, String> headers;

	String connectionUrl;

	public Database(String tableName, Map<String, String> headers) {
		this.tableName = "`" + tableName + "`";
		this.headers = headers;

		connectionUrl = "jdbc:mysql://" + Settings.Storage.Database.DatabaseHost + ":"
				+ Settings.Storage.Database.DatabasePort + "/" + Settings.Storage.Database.DatabaseName
				+ "?useSSL=false";

		createTable();
	}

	private void createTable() {

		String createTableQuery = "CREATE TABLE IF NOT EXISTS " + tableName + "(";

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			createTableQuery += entry.getKey() + " " + entry.getValue() + ",";
		}

		// Remove trailing comma
		createTableQuery = createTableQuery.substring(0, createTableQuery.length() - 1);

		createTableQuery += ");";

		try (Connection conn = DriverManager.getConnection(connectionUrl, Settings.Storage.Database.DatabaseUsername,
				Settings.Storage.Database.DatabasePassword);
				PreparedStatement ps = conn.prepareStatement(createTableQuery);) {
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the first row that matches the given value in the given column name
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public Map<String, String> getRow(String columnName, String value) {
		Map<String, String> data = new HashMap<>();

		try (Connection conn = DriverManager.getConnection(connectionUrl, Settings.Storage.Database.DatabaseUsername,
				Settings.Storage.Database.DatabasePassword);
				PreparedStatement ps = conn
						.prepareStatement("SELECT * FROM " + tableName + " WHERE " + columnName + " = ? LIMIT 1;");) {
			ps.setString(1, value);
			ResultSet rs = ps.executeQuery();
			// Gets the first result

			while (rs.next()) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					String headerName = entry.getKey();

					data.put(headerName, rs.getString(headerName));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return data;
	}

	/**
	 * Gets all rows that match the given value in the given column name
	 * 
	 * @param columnName
	 * @param match      - the value to match
	 * @return
	 */
	@Override
	public List<Map<String, String>> getRows(String columnName, String value) {
		List<Map<String, String>> rows = new ArrayList<>();

		try (Connection conn = DriverManager.getConnection(connectionUrl, Settings.Storage.Database.DatabaseUsername,
				Settings.Storage.Database.DatabasePassword);
				PreparedStatement ps = conn
						.prepareStatement("SELECT * FROM " + tableName + " WHERE " + columnName + " = " + value + ";");
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Map<String, String> data = new HashMap<>();

				for (Map.Entry<String, String> entry : headers.entrySet()) {
					String headerName = entry.getKey();
					data.put(headerName, rs.getString(headerName));
				}

				rows.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rows;
	}

	/**
	 * Attempts to add a new row
	 * 
	 * @param rowData
	 * @return true if row was added, false if duplicate
	 */
	@Override
	public void addRow(Map<String, String> rowData) {

		String insertString = "INSERT INTO " + tableName + " (";

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			insertString += entry.getKey() + ", ";
		}

		insertString = insertString.substring(0, insertString.length() - 2);

		insertString += ") values (";

		for (int i = 0; i < headers.size(); i++) {
			insertString += "?, ";
		}

		insertString = insertString.substring(0, insertString.length() - 2);

		insertString += ");";

		try (Connection conn = DriverManager.getConnection(connectionUrl, Settings.Storage.Database.DatabaseUsername,
				Settings.Storage.Database.DatabasePassword);
				PreparedStatement ps = conn.prepareStatement(insertString);) {
			int count = 1;

			for (Map.Entry<String, String> entry : headers.entrySet()) {
				// Use set string to sanitize entries
				ps.setString(count++, rowData.get(entry.getKey()));
			}

			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getMax(String columnName) {
		String max = "";

		try (Connection conn = DriverManager.getConnection(connectionUrl, Settings.Storage.Database.DatabaseUsername,
				Settings.Storage.Database.DatabasePassword);
				PreparedStatement ps = conn.prepareStatement(
						"SELECT MAX(" + columnName + ") AS " + columnName + " FROM " + tableName + ";");
				ResultSet rs = ps.executeQuery()) {

			// Get the first result
			while (rs.next()) {
				max = rs.getString(columnName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return max;
	}

}
