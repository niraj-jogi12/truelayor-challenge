/**
 * 
 */
package com.truefilm.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author njg01
 *
 */
public class DbHandler {
	
	/**
	 * Method to get connection with Postgres database.
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException
	{
		Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/truefilm?user=postgres&password=postgres");
		return connection;
	}

}
