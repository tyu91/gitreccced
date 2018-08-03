package codepath.com.gitreccedproject;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONMovie {
    public String iid;
    public String genre;
    public String title;
    public String overview;

    public String posterPath;
    public String backdropPath;
    public String releaseDate;
    public int movieId;

    public JSONMovie() {
        //empty constructor
    }

    public JSONMovie(String iid, JSONObject object) throws JSONException {
        this.iid = iid;
        genre = "Movie";
        title = object.getString("title");
        releaseDate = object.getString("release_date");
        overview = object.getString("overview");

        posterPath = object.getString("poster_path");
        backdropPath = object.getString("backdrop_path");
        movieId = object.getInt("movie_id"); //TODO: might not be "id", check JSON

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

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}