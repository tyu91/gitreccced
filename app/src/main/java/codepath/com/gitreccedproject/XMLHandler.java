package codepath.com.gitreccedproject;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLHandler extends DefaultHandler{

    public XMLHandler() {}

    boolean boolTitle = false;

    XMLBook book = new XMLBook();

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
