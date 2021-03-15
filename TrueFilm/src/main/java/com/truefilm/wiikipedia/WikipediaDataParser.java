/**
 * 
 */
package com.truefilm.wiikipedia;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import com.truefilm.database.DbHandler;

/**
 * @author njg01
 *
 */
public class WikipediaDataParser {
	private static final String ELEMENT_PAGE = "doc";
	private static final String ELEMENT_NAME = "title";
	private static final String ELEMENT_ID = "url";
	private static final String ATTRIBUTE_NAME = "abstract";
	
	private final String file;
	private final XMLInputFactory factory = XMLInputFactory.newInstance();
	private final List<WikipediaPage> docs;

	/**
	 * 
	 * @param file
	 * @param docs
	 */
	public WikipediaDataParser(final String file, final List<WikipediaPage> docs) {
		this.file = file;
		this.docs = docs;
	}
	
	/**
	 * Parse wiki abstract xml file and convert into csv file
	 * 
	 * @param outputFile
	 * @param wikiPage
	 * @throws IOException
	 * @throws XMLStreamException
	 * @throws CsvValidationException
	 * @throws SQLException
	 */

	public void parse(String wikiPage) throws IOException, XMLStreamException, CsvValidationException, SQLException {
		
		List<String[]> list = new ArrayList<>();
		String[] header = { "title","wikipedia page" };
		list.add(header);
		Map<String, String> movieNameMap = new HashMap<>();
		Pattern pattern = Pattern.compile("~");
		
		// Parse wikipedia abtract with name and wiki url and keep in a hashmap.
		try(final InputStream stream = new FileInputStream(file)) {
            try(final GZIPInputStream zip = new  GZIPInputStream(stream)) {
                final XMLEventReader xmlReader = factory.createXMLEventReader(zip);
				while (xmlReader.hasNext()) {
					final XMLEvent event = xmlReader.nextEvent();
					if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals(ELEMENT_PAGE)) {
						parsePage(xmlReader,movieNameMap);
					}
				}
				xmlReader.close();
            }
		}
		
		// Match Each movie in the IMDB dataset with its corresponding Wikipedia Page
		for (Map.Entry<String, String> entry : movieNameMap.entrySet()) {
				String title = entry.getKey().trim().split(": ")[1];
				String url = entry.getValue();
				//String url = pattern.split(entry.getValue(), -1)[0];
				//String abstractName = pattern.split(entry.getValue(), -1)[1];
				String[] records = { title,url};
				list.add(records);
			    //}
			}
		CSVWriter writer = new CSVWriter(new FileWriter(wikiPage));
		writer.writeAll(list);
		writer.close(); 
		
		// Method to load Movies Wiki Information into PostGres DB
		loadMoviesListDB(wikiPage);
		
		// Method to create Movies details table into PostGres DB
		createMoviesDetailsDB();
				
	}
	
	/**
	 * Method to create Movies details table into PostGres DB
	 * 
	 * @throws SQLException
	 */

	private void createMoviesDetailsDB() throws SQLException {
		Connection conn = DbHandler.getConnection();
		Statement stmt = conn.createStatement();
		String CreateSql = "Create Table if not exists moviesdetails (title varchar, budget float , year varchar , revenue float , rating float ,ratio float , production_companies varchar,wikipedia_page varchar, rank_value int ) ";
		stmt.executeUpdate(CreateSql);
		stmt.close();
		conn.close();
	}

	/**
	 * Method to load Movies Wiki Information into PostGres DB
	 * 
	 * @param wikiPage
	 * @throws SQLException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void loadMoviesListDB(String wikiPage) throws SQLException, IOException, FileNotFoundException {
		Connection conn = DbHandler.getConnection();
		Statement stmt = conn.createStatement();
		String CreateSql = "Create Table if not exists movieslist(title varchar,wikipedia_page varchar) ";
		stmt.executeUpdate(CreateSql);
		CopyManager copyManager = new CopyManager( (BaseConnection) conn);
		long copyIn = copyManager.copyIn("COPY movieslist FROM STDIN WITH CSV HEADER ", new FileReader(wikiPage));
		System.out.println("number of added rows in movielist DB : "+copyIn);
		stmt.close();
		conn.close();
	}
	
	/**
	 * Gunzip and parse the wiki abstract xml file 
	 * 
	 * @param reader
	 * @param outputFile
	 * @param movieNameMap
	 * @throws XMLStreamException
	 * @throws CsvValidationException
	 * @throws IOException
	 */

	private void parsePage(final XMLEventReader reader, Map<String, String> movieNameMap)
			throws XMLStreamException, CsvValidationException, IOException {
		
		String title = null;
		String url = null;
		String abstractName = null;
		
		//while ((lineInArray = csvReader.readNext()) != null) {
			while (reader.hasNext()) {
				final XMLEvent event = reader.nextEvent();
				if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(ELEMENT_PAGE)) {
					return;
				}
				if (event.isStartElement()) {
					final StartElement element = event.asStartElement();
					final String elementName = element.getName().getLocalPart();
					switch (elementName) {
					case ELEMENT_NAME:
						title = reader.getElementText();
						if (title != null) {
							movieNameMap.put(title, url);
						}
						break;
					case ELEMENT_ID:
						url = reader.getElementText();
						if (title != null && url != null) {
							movieNameMap.put(title, url);
						}
						break;
					/* case ATTRIBUTE_NAME:
						abstractName = reader.getElementText();
						if (title != null && url != null && abstractName !=null) {
							movieNameMap.put(title, url +"~"+abstractName);
						}
						break; */
					} 
				}
			}
		//}
	}

	
	/* public void matchMovieWithWikipage(String outputMetadataFile, String finalOutputFile) throws IOException {
		String dataRow1 = null;
		String dataRow2 = null;
		BufferedWriter bw = new BufferedWriter(new FileWriter(finalOutputFile));
		
		Pattern commaPattern = Pattern.compile(",");
		List<String> al1=new ArrayList();
		List<String> al2=new ArrayList();
		Map<String, String> movieNameMap = new HashMap<>();
		
		BufferedReader brWikiPage = new BufferedReader(new FileReader(file));
	    while ((dataRow1 = brWikiPage.readLine()) != null) {
	        String[] dataArray1 = dataRow1.split(",");
	        movieNameMap.put(dataArray1[0], dataArray1[1]);
	        
	    }
	    brWikiPage.close();
		
		BufferedReader brMovieMetadata = new BufferedReader(new FileReader(outputMetadataFile));
	    while ( (dataRow2 = brMovieMetadata.readLine()) != null)
	    {
	        String[] dataArray2 = dataRow2.split(",");
	        for (Map.Entry<String, String> entry : movieNameMap.entrySet()) {
				String title = entry.getKey();
				String url = entry.getValue();
				if( dataArray2[0].equalsIgnoreCase(title) )
				{
					bw.write(dataArray2[0]+","+dataArray2[1]+","+dataArray2[2]+","+dataArray2[3]+"," +url);
					bw.newLine();
				}
	        }
	        
	    }
	    brMovieMetadata.close();
	    bw.close();
	} */
}
