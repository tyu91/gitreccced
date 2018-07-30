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
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class GoodreadsClient extends DefaultHandler{

    boolean boolWork = false;
    boolean boolTitle = false;
    boolean boolAuthor = false;
    boolean boolDescription = false;
    boolean boolSmallImgUrl = false;
    boolean boolImgUrl = false;

    private AsyncHttpClient client;

    static final String baseURL = "https://www.goodreads.com/";
    static final String goodreadsApiKey = "NPgqXkuLfDngX9oRJa1oA";

    public XMLBook book;
    public static ArrayList<XMLBook> books;

    public GoodreadsClient() {
        this.client = new AsyncHttpClient();
    }

    private String getApiUrl(String relativeUrl) {
        return baseURL + relativeUrl;
    }

    public void searchBooks(final String query) {
        try {
            books = new ArrayList<>();
            //generate querying url string
            String urlString = getApiUrl("search.xml?key=" + goodreadsApiKey + "&q=" + URLEncoder.encode(query, "utf-8"));
            Log.i("Books", "URL String: " + urlString);
            //TODO: query string OK
            //convert querying url to URL object
            URL url = new URL(urlString);
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();

            //XMLHandler handler = new XMLHandler();

            xmlReader.setContentHandler(this);

            xmlReader.parse(new InputSource(url.openStream()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("work")) {
            //books = new ArrayList<>();
            boolWork = true;
        } else if (qName.equalsIgnoreCase("title")) {
            boolTitle = true;
        } else if (qName.equalsIgnoreCase("author")) {
            boolAuthor = true;
        } else if (qName.equalsIgnoreCase("description")) {
            boolDescription = true;
        } else if (qName.equalsIgnoreCase("small_image_url")) {
            boolSmallImgUrl = true;
        } else if (qName.equalsIgnoreCase("image_url")) {
            boolImgUrl = true;
        }
        else {
            Log.i("XMLBook", "skipped tag");
        }
    }

    //setting the fields of each book
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if(boolWork) {
            Log.i("XMLBook", "Title: " + new String(ch, start, length));
            //create new XMLBook for each one
            book = new XMLBook();
            book.setGenre("Book");
            boolWork = false;
        } else if (boolTitle) {
            book.setTitle(new String(ch, start, length));
            boolTitle = false;
        } else if (boolAuthor) {
            book.setAuthor(new String(ch, start, length));
            boolAuthor = false;
        } else if (boolDescription) {
            book.setDescription(new String(ch, start, length));
            boolDescription = false;
        } else if (boolSmallImgUrl) {
            book.setSmallImgUrl(new String(ch, start, length));
            boolSmallImgUrl = false;
        } else if (boolImgUrl) {
            book.setImgUrl(new String(ch, start, length));
            boolImgUrl = false;
        } else {
            Log.i("XMLBook", "no characters to care about");
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equalsIgnoreCase("work")) {
            //TODO: book added is correct
            books.add(book);
        }
    }
}