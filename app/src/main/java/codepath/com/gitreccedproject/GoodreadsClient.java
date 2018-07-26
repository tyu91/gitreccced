package codepath.com.gitreccedproject;

import com.loopj.android.http.AsyncHttpClient;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class GoodreadsClient {

    private AsyncHttpClient client;

    public GoodreadsClient() {
        this.client = new AsyncHttpClient();
    }

    static final String baseURL = "https://www.goodreads.com/";
    static final String goodreadsApiKey = "NPgqXkuLfDngX9oRJa1oA";

    private String getApiUrl(String relativeUrl) {
        return baseURL + relativeUrl;
    }

    public void searchBooks(final String query) {
        try {
            //generate querying url string
            String urlString = getApiUrl("search.xml?key=" + goodreadsApiKey + "&q=" + URLEncoder.encode(query, "utf-8"));
            //convert querying url to URL object
            URL url = new URL(urlString);
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();

            XMLHandler handler = new XMLHandler();

            xmlReader.setContentHandler(handler);

            xmlReader.parse(new InputSource(url.openStream()));
            //TODO: set fields of XMLBook

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

/*

 private static final String API_BASE_URL = "http://openlibrary.org/";
    private AsyncHttpClient client;

    public BookClient() {
        this.client = new AsyncHttpClient();
    }

    private String getApiUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }

    // Method for accessing the search API
    public void getBooks(final String query, JsonHttpResponseHandler handler) {
        try {
            String url = getApiUrl("search.json?q=");
            client.get(url + URLEncoder.encode(query, "utf-8"), handler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // Method for accessing books API to get publisher and no. of pages in a book.
    public void getExtraBookDetails(String openLibraryId, JsonHttpResponseHandler handler) {
        String url = getApiUrl("");
        client.get(url + openLibraryId + ".json", handler);
    }

 */
