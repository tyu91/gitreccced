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

public class GoodreadsClient extends DefaultHandler {

    boolean boolWork = false;
    boolean boolTitle = false;
    boolean boolAuthor = false;
    boolean boolAuthorName = false;
    boolean boolDetails = false;
    boolean boolSmallImgUrl = false;
    boolean boolImgUrl = false;
    boolean boolId = false;
    boolean boolPubYear = false;
    boolean boolPubMonth = false;
    boolean boolPubDay = false;
    boolean boolAverageRating = false;
    boolean inBook = false;

    private AsyncHttpClient client;

    static final String baseURL = "https://www.goodreads.com/";
    static final String goodreadsApiKey = "NPgqXkuLfDngX9oRJa1oA";

    public Item book;
    public static ArrayList<Item> books;

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
            Log.i("Books", "Search URL String: " + urlString);
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

    public void getBook(final Item item) {
        try {
            //set book to item
            book = item;
            //generate querying url string
            String urlString = getApiUrl("book/show.xml?key=" + goodreadsApiKey + "&id=" + String.valueOf(item.getBookId()));
            Log.i("Books", "Book URL String: " + urlString);
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
            boolId = false;
        } else if (qName.equalsIgnoreCase("name")) {
            boolAuthorName = true;
        } else if (qName.equalsIgnoreCase("description")) {
            boolDetails = true;
        } else if (qName.equalsIgnoreCase("small_image_url")) {
            boolSmallImgUrl = true;
        } else if (qName.equalsIgnoreCase("image_url")) {
            boolImgUrl = true;
        } else if (qName.equalsIgnoreCase("best_book")) {
            Log.i("BookIdClient", "best_book" + "==true");
            inBook = true;
        } else if (qName.equalsIgnoreCase("id")) {
            Log.i("BookIdClient", "best_book id" + "==true");
            boolId = true;
        } else if (qName.equalsIgnoreCase("original_publication_year")) {
            boolPubYear = true;
        } else if (qName.equalsIgnoreCase("original_publication_month")) {
            boolPubMonth = true;
        } else if (qName.equalsIgnoreCase("original_publication_day")) {
            boolPubDay = true;
        } else if (qName.equalsIgnoreCase("average_rating")) {
            boolAverageRating = true;
        } else {
            Log.i("XMLBook", "skipped tag");
        }
    }


    //setting the fields of each book
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (boolWork) {
            Log.i("XMLBook", "Title: " + new String(ch, start, length));
            //create new XMLBook for each one
            book = new Item();
            book.setGenre("Book");
            boolWork = false;
        } else if (boolTitle) {
            Log.i("BookIdClient", "boolTitle is True: " + book.getDetails());
            book.setTitle(new String(ch, start, length));
            boolTitle = false;
        } else if (boolAuthor && boolAuthorName) {
            book.setAuthor(new String(ch, start, length));
            boolAuthor = false;
            boolAuthorName = false;
        } else if (boolDetails) {
            book.setDetails(new String(ch, start, length));
            boolDetails = false;
        } else if (boolSmallImgUrl) {
            book.setSmallImgUrl(new String(ch, start, length));
            boolSmallImgUrl = false;
        } else if (boolImgUrl) {
            book.setImgUrl(new String(ch, start, length));
            boolImgUrl = false;
        } else if (boolPubYear) {
            book.setPubYear(new String(ch, start, length));
            boolPubYear = false;
        } else if (boolPubMonth) {
            book.setPubMonth(new String(ch, start, length));
            boolPubMonth = false;
        } else if (boolPubDay) {
            book.setPubDay(new String(ch, start, length));
            boolPubDay = false;
        } else if (boolAverageRating) {
            book.setAverageRating(Float.valueOf(new String(ch, start, length)));
            boolAverageRating = false;
        } else if (inBook && boolId) {
            book.setBookId(new String(ch, start, length));
            Log.i("BookIdClient", "Book Id: " + new String(ch, start, length) + " || Book Title: " + book.getTitle());
            inBook = false;
            boolId = false;
        } else if (boolId) {
            boolId = false;
        } else {
            Log.i("XMLBook", "no characters to care about");
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equalsIgnoreCase("work")) {
            //TODO: book added is correct
            Log.i("BookIdClient", "BookId before added to books list: " + book.getBookId() +
                    "Book Title before added to books list: " + book.getTitle());
            books.add(book);

            //after add book, reset all variables
            boolWork = false;
            boolTitle = false;
            boolAuthor = false;
            boolAuthorName = false;
            boolDetails = false;
            boolSmallImgUrl = false;
            boolImgUrl = false;
            boolId = false;
            boolPubYear = false;
            boolPubMonth = false;
            boolPubDay = false;
            boolAverageRating = false;
            inBook = false;
        } else if (localName.equalsIgnoreCase("best_book")) {
            Log.i("BookIdClient", "best_book" + "==false");
            inBook = false;
        } else {
            Log.i("BookIdClient", "endElement unaccounted for");
        }
    }
}