package codepath.com.gitreccedproject;

import org.json.JSONException;
import org.json.JSONObject;

public class TVDetails {
    public String numSeasons;

    public TVDetails(JSONObject object) throws JSONException {
        numSeasons = object.getString("number_of_seasons");
    }

    public String getNumSeasons() {
        return numSeasons;
    }
}
