/**
 * 
 */
package com.truefilm.wiikipedia;

import java.io.IOException;
import java.sql.SQLException;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import com.opencsv.exceptions.CsvValidationException;
import com.truefilm.metadata.CalculateRatio;

/**
 * @author njg01
 *
 */
public class CalculateRatioTest {
	
	/**
	 * Testcase to validate Calculate the Budget to Revenue Ratio
	 * 
	 * @throws IOException
	 * @throws XMLStreamException
	 * @throws CsvValidationException
	 * @throws SQLException
	 */

	@Test
    public void testCalculateRatio() throws IOException, XMLStreamException, CsvValidationException, SQLException {
        final CalculateRatio calculateRatio = new CalculateRatio();
        calculateRatio.budgetToRevenue("movies_metadata.csv","ratings.csv","movies_details.csv");
    }
}
