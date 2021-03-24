package com.star.NexureUserPunisher.DataSources;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlatFile implements DataSource {

	// The path for the FlatFile
	private Path filePath;
	private String formatting;
	private String[] headers;
	
	public FlatFile(Path filePath, String formatting, Map<String, String> headerMap) {
		this.filePath = filePath;
		this.formatting = formatting;
		
		headers = new String[headerMap.size()];
		
		int count = 0;
		
		for (Map.Entry<String, String> entry : headerMap.entrySet()) {
			headers[count++] = entry.getKey();
		}
		
		verifyFileExists();
	}

	private void verifyFileExists() {
		File file = filePath.toFile();
		
		try {
			if (file.createNewFile()) {
				writeFileHeaders();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeFileHeaders() {
		appendToFile(String.format(formatting, headers));
	}
	
	/**
	 * Gets the first row that matches the given value in the given column name
	 * @param id
	 * @return
	 */
	@Override
	public Map<String, String> getRow(String columnName, String value) {
		try (BufferedReader reader = Files.newBufferedReader(filePath)) {
			String currentLine = "";

			// Skip the headers
			reader.readLine();
			
			int columnId = -1;
			
			//Find the column that matches the given name
			for (int i = 0; i < headers.length; i++) {
				if (headers[i].equals(columnName)) {
					columnId = i;
					break;
				}
			}
			
			if (columnId == -1) {
				System.out.println("NO COLUMN NAME FOUND! FLATFILE GETROW");
				return null;
			}

			while ((currentLine = reader.readLine()) != null) {
				String[] rowData = currentLine.split("\\s+");

				// Check if the row matches the id
				if (rowData[columnId].equals(value)) {
					Map<String, String> data = new HashMap<>();
					
					for (int i = 0; i < rowData.length; i++) {
						data.put(headers[i], rowData[i]);
					}

					// return the data that we found
					return data;
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public List<Map<String, String>> getRows(String columnName, String value) {
		List<Map<String, String>> rows = new ArrayList<>();
		
		try (BufferedReader reader = Files.newBufferedReader(filePath)) {
			String currentLine = "";

			// Skip the headers
			reader.readLine();
			
			int columnId = -1;
			
			//Find the column that matches the given name
			for (int i = 0; i < headers.length; i++) {
				if (headers[i].equals(columnName)) {
					columnId = i;
					break;
				}
			}
			
			if (columnId == -1) {
				System.out.println("NO COLUMN NAME FOUND! FLATFILE GETROWS");
				return null;
			}
			
			while ((currentLine = reader.readLine()) != null) {
				String[] rowData = currentLine.split("\\s+");

				// Check if the row matches the id
				if (rowData[columnId].equals(value)) {
					Map<String, String> dataMap = new HashMap<>();
					
					for (int i = 0; i < rowData.length; i++) {
						dataMap.put(headers[i], rowData[i]);
					}

					// add the data to the list
					rows.add(dataMap);
				}

			}
			
			return rows;

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return rows;
	}
	
	/**
	 * Attempts to add a new row
	 * @param rowData
	 * @return true if row was added, false if duplicate
	 */
	@Override
	public void addRow(Map<String, String> rowData) {
		if (getRow(headers[0], rowData.get(headers[0])) != null) {
			return;
		}
		
		String[] values = new String[headers.length];
		
		for (int i = 0; i < headers.length; i++) {
			String header = headers[i];
			
			if (rowData.containsKey(header)) {
				values[i] = rowData.get(header);
			} else {
				values[i] = "";
			}
		}
		
		appendToFile(String.format(formatting, values));
	}

	@Override
	public String getMax(String columnName) {
		try (BufferedReader reader = Files.newBufferedReader(filePath)) {
			String currentLine = "";

			//Skip the headers and ensure that there is data
			if (reader.readLine() == null) {
				return "";
			}
			
			int headerId = -1;
			
			//Try to locate the requested header
			for (int i = 0; i < headers.length; i++) {
				if (headers[i].equals(columnName)) {
					headerId = i;
					break;
				}
			}
			
			if (headerId == -1) {
				return "";
			}
			
			String firstDataLine = reader.readLine();
			
			//No data
			if (firstDataLine == null) {
				return "";
			}
			
			String maxId = firstDataLine.split("\\s+")[headerId];
			
			while ((currentLine = reader.readLine()) != null) {
				String[] rowData = currentLine.split("\\s+");

				//If the new value is bigger
				if (rowData[headerId].compareTo(maxId) > 0) {
					maxId = rowData[headerId];
				}

			}
			
			//Return the max
			return maxId;

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}

	private void appendToFile(String... lines) {
		try (FileWriter fw = new FileWriter(filePath.toString(), true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    PrintWriter out = new PrintWriter(bw)) {
			
			for (String line : lines) {
				out.println(line);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
