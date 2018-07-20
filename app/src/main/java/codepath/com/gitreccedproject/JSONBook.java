package codepath.com.gitreccedproject;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.io.Serializable;
import java.util.ArrayList;

@Parcel

public class JSONBook extends Item implements Serializable {
    /*public String iid;
    public String genre;
    public String title;
    public String overview;
    */
    public String iid;
    public String overview;
    public String genre;
    public String openLibraryId;
    //public String author;
    public String title;

    public String getOpenLibraryId() {
        return openLibraryId;
    }

    public String getTitle() {
        return title;
    }

    /*public String getAuthor() {
        return author;
    }
*/
    // Get book cover from covers API
    public String getCoverUrl() {
        return "http://covers.openlibrary.org/b/olid/" + openLibraryId + "-L.jpg?default=false";
    }

    // Returns a Book given the expected JSON
    public static JSONBook fromJson(JSONObject jsonObject) {
        JSONBook book = new JSONBook();

        try {
            // Deserialize json into object fields
            // Check if a cover edition is available
            if(jsonObject.has("key")) {
                //final JSONObject ids = jsonObject.getJSONObject("key");
                book.openLibraryId = jsonObject.getString("key");
            }
            book.iid = "";
            book.genre = "Book";
            book.title = jsonObject.has("title_suggest") ? jsonObject.getString("title_suggest") : "";
            //book.author = getAuthor(jsonObject);
            book.overview = "no description available";
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return book;
    }

    // Return comma separated author list when there is more than one author
    private static String getAuthor(final JSONObject jsonObject) {
        try {
            final JSONArray authors = jsonObject.getJSONArray("author_name");
            int numAuthors = authors.length();
            final String[] authorStrings = new String[numAuthors];
            for (int i = 0; i < numAuthors; ++i) {
                authorStrings[i] = authors.getString(i);
            }
            return TextUtils.join(", ", authorStrings);
        } catch (JSONException e) {
            return "";
        }
    }


    public static ArrayList<JSONBook> fromJson(JSONArray jsonArray) {
        ArrayList<JSONBook> books = new ArrayList<JSONBook>(jsonArray.length());
        // Process each result in json array, decode and convert to business
        // object
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject bookJson = null;
            try {
                bookJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            JSONBook book = JSONBook.fromJson(bookJson);
            if (book != null) {
                books.add(book);
            }
        }
        return books;
    }

    @Override
    public String getIid() {
        return iid;
    }

    @Override
    public void setIid(String iid) {
        this.iid = iid;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
}
