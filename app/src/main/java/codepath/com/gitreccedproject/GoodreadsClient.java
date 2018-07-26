package codepath.com.gitreccedproject;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class GoodreadsClient extends DefaultHandler{

    boolean boolTitle = false;

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

            //XMLHandler handler = new XMLHandler();

            xmlReader.setContentHandler(this);

            xmlReader.parse(new InputSource(url.openStream()));
            //TODO: set fields of XMLBook

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //if (localName.equals("GoodreadsResponse")) {
        if (qName.equalsIgnoreCase("title")) {
            boolTitle = true;
                /*String title = attributes.getLocalName(1);
                Log.i("XMLBook", "XML Book Title: " + title);
                book.setTitle(title);*/
        }
        //}
        else {
            Log.i("XMLBook", "no local name equal to book");
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if(boolTitle) {
            Log.i("XMLBook", "Title: " + new String(ch, start, length));
            //TODO: populate search query using these entries
            boolTitle = false;
        }
    }
}