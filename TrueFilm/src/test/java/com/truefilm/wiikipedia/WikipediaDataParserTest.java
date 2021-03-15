/**
 * 
 */
package com.truefilm.wiikipedia;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import com.opencsv.exceptions.CsvValidationException;


/**
 * @author njg01
 *
 */
public class WikipediaDataParserTest {
	
	/**
	 *  Testcase to validate parsing of the the xml , converting it to csv and loading into Postgres.
	 * 
	 * @throws IOException
	 * @throws XMLStreamException
	 * @throws CsvValidationException
	 * @throws SQLException
	 */
	@Test
    public void testParse() throws IOException, XMLStreamException, CsvValidationException, SQLException {
        final List<WikipediaPage> docs = new ArrayList();
        final WikipediaDataParser parser = new WikipediaDataParser("enwiki-latest-abstract.xml.gz", docs);
        parser.parse("wikipage.csv");
    }

}
