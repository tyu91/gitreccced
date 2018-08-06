package codepath.com.gitreccedproject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Credits {

    public String job, director, castMember;
    public ArrayList<String> castNames;
    private int numCast = 3;

    public Credits(JSONObject object) throws JSONException {
        JSONObject cast = object.getJSONObject("cast");
        castMember = cast.getString("name");

        for (int i = 0; i < numCast; i++){
            castNames.add(castMember);
        }

        JSONObject crew = object.getJSONObject("crew");
        job = crew.getString("job");

        if (job.equalsIgnoreCase("director")){
            director = crew.getString("name");
        }

    }
}
