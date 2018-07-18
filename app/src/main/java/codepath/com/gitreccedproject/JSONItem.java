package codepath.com.gitreccedproject;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONItem {
    public String iid;
    public String genre;
    public String title;
    public String overview;
    public String user;

    public JSONItem() {
        //empty constructor
    }

    public JSONItem(String iid, String user, JSONObject object) throws JSONException {
        iid = this.iid;
        genre = "Movie";
        title = object.getString("title");
        overview = object.getString("overview");
        user = this.user;
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

    public String getUser() {
        return user;
    }
}
