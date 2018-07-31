package codepath.com.gitreccedproject;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONMovie {
    public String iid;
    public String genre;
    public String title;
    public String overview;

    public String imageBaseUrl;
    public String posterSize;
    public String backdropSize;
    public String posterPath;
    public String backdropPath;

    public JSONMovie() {
        //empty constructor
    }

    public JSONMovie(String iid, JSONObject object) throws JSONException {
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

    public void setIid(String iid) {
        this.iid = iid;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getImageBaseUrl() {
        return imageBaseUrl;
    }

    public void setImageBaseUrl(String imageBaseUrl) {
        this.imageBaseUrl = imageBaseUrl;
    }

    public String getPosterSize() {
        return posterSize;
    }

    public void setPosterSize(String posterSize) {
        this.posterSize = posterSize;
    }

    public String getBackdropSize() {
        return backdropSize;
    }

    public void setBackdropSize(String backdropSize) {
        this.backdropSize = backdropSize;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }
}


