/**
 * 
 */
package com.truefilm.metadata;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.truefilm.database.DbHandler;

/**
 * @author njg01
 *
 */
public class CalculateRatio {

	/**
	 * Calculate ratio of budget to revenue
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws CsvValidationException
	 * @throws SQLException
	 */
	public void budgetToRevenue(String metadataFile, String ratingsFile, String outputFile)
			throws FileNotFoundException, IOException, CsvValidationException, SQLException {
		CSVReader readerMetadata = null;
		CSVReader readerRatings = null;
		Map<String, String> movieRatingMap = new HashMap<>();
		Float budget;
		Float revenue = null;
		Float ratio;
		String[] lineInArray;
		String[] dataArray1;
		readerMetadata = new CSVReader(new FileReader(metadataFile));
		readerRatings = new CSVReader(new FileReader(ratingsFile));
		// skip the header of the csv
		readerMetadata.skip(1);
		readerRatings.skip(1);

		while ((dataArray1 = readerRatings.readNext()) != null) {
			movieRatingMap.put(dataArray1[1], dataArray1[2]);

		}
		readerRatings.close();

		List<String[]> list = new ArrayList<>();
		String[] header = { "id", "title", "budget", "year", "revenue", "ratio", "production_companies" };
		list.add(header);
		while ((lineInArray = readerMetadata.readNext()) != null) {
			if (lineInArray.length == 24) {
				// Calculate ratio
				budget = Float.parseFloat(lineInArray[2]);
				revenue = Float.parseFloat(lineInArray[15]);
				if (budget == 0)
					ratio = 0.0f;
				else if (budget != 0 && revenue == 0)
					ratio = 0.0f;
				else
					ratio = (budget / revenue);
				// get production company from nested structure
				StringBuilder sb = new StringBuilder();
				int cnt = 0;
				String[] productionCompanies = lineInArray[12].split(",", 0);
				for (String value : productionCompanies) {
					String[] productionCompany = value.split(":");
					if (Arrays.asList(productionCompany).get(0).contains("name")) {
						if (cnt == 0) {
							sb.append(productionCompany[1].replaceAll("'", ""));
						} else {
							sb.append(",");
							sb.append(productionCompany[1].replaceAll("'", ""));
						}
						cnt++;
					}
				}
				// List of columns to be loaded into Postgres
				String[] records = { lineInArray[5], lineInArray[8], lineInArray[2], lineInArray[14], lineInArray[15],
						ratio.toString(), sb.toString() };
				list.add(records);
			}
		}
		readerMetadata.close();
		CSVWriter writerMetadata = new CSVWriter(new FileWriter(outputFile));
		writerMetadata.writeAll(list);
		writerMetadata.close();
		
		// Method to load Movies Metadata into PostGres DB
		loadMoviesMetadataDB(outputFile);
		
		// Method to load Movies Ratings into PostGres DB
		loadMoviesRatingsDB(ratingsFile);
				

	}

	/**
	 * Method to load Movies Ratings into PostGres DB
	 * 
	 * @param ratingsFile
	 * @throws SQLException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void loadMoviesRatingsDB(String ratingsFile) throws SQLException, FileNotFoundException, IOException {
		Connection conn = DbHandler.getConnection();
		Statement stmt = conn.createStatement();
		String createSql = "Create Table if not exists moviesratings(userId int ,movieId int, rating float , timestamp int) ";
		stmt.executeUpdate(createSql);
		CopyManager copyManager = new CopyManager((BaseConnection) conn);
		long copyIn = copyManager.copyIn("COPY moviesratings FROM STDIN WITH CSV HEADER", new FileReader(ratingsFile));
		System.out.println("number of added rows in moviesratings DB : " + copyIn);
		stmt.close();
		conn.close();
	}

	/**
	 *  Method to load Movies Metadata into PostGres DB
	 *
	 * @param outputFile
	 * @throws SQLException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void loadMoviesMetadataDB(String outputFile) throws SQLException, IOException, FileNotFoundException {
		Connection conn = DbHandler.getConnection();
		Statement stmt = conn.createStatement();
		String createSql = "Create Table if not exists moviesmetadata(id int,Title varchar, budget float , year varchar , revenue float , ratio float , production_companies varchar) ";
		stmt.executeUpdate(createSql);
		CopyManager copyManager = new CopyManager((BaseConnection) conn);
		long copyIn = copyManager.copyIn("COPY moviesmetadata FROM STDIN WITH CSV HEADER", new FileReader(outputFile));
		System.out.println("number of added rows in moviesmetadata DB : " + copyIn);
		stmt.close();
		conn.close();
	}
}
