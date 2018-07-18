package codepath.com.gitreccedproject;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONItem {
    public String iid;
    public String genre;
    public String title;
    public String overview;

    public JSONItem() {
        //empty constructor
    }

    public JSONItem(String iid, JSONObject object) throws JSONException {
        this.iid = iid;
        genre = "Movie";
        title = object.getString("title");
        overview = object.getString("overview");
    }

    public String getIid() {
        return iid;
    }

    public String getGenre() {
        return genre;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }
}
