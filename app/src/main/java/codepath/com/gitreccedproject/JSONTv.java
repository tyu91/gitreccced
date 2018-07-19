package codepath.com.gitreccedproject;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONTv {
    public String iid;
    public String genre;
    public String title;
    public String overview;

    public JSONTv() {
        //empty constructor
    }

    public JSONTv(String iid, JSONObject object) throws JSONException {
        this.iid = iid;
        genre = "TV";
        title = object.getString("name");
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
