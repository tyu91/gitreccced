package codepath.com.gitreccedproject;

import org.parceler.Parcel;

@Parcel
public class Item {
    public String iid;
    public String genre;
    public String title;
    public String details;

    public String imageBaseUrl;
    public String posterSize;
    public String backdropSize;
    public String posterPath;
    public String backdropPath;
    public String movieId;
    public String releaseDate;

    public String firstAirDate;

    public String bookId;
    public String author;
    public String smallImgUrl;
    public String imgUrl;
    public String pubYear;
    public String pubMonth;
    public String pubDay;

    public Item() {
        //empty constructor
    }

    public Item(String iid, String genre, String title, String details) {
        this.iid = iid;
        this.genre = genre;
        this.title = title;
        this.details = details;

         imageBaseUrl = " no associated imageBaseUrl";
         posterSize = " no associated posterSize";
         backdropSize = " no associated backdropSize";
         posterPath = " no associated posterPath";
         backdropPath = " no associated backdropPath";
         movieId = " no associated movieTvId";
         releaseDate = " no associated release date";
         firstAirDate = " no associated first air date";

        bookId = " no associated book id";
        author = " no associated author";
        smallImgUrl = " no associated smallImgUrl";
        imgUrl = " no associated imgUrl";
        pubYear = " no associated pubYear";
        pubMonth = " no associated pubMonth";
        pubDay = " no associated pubDay";
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

    public String getDetails() {
        return details;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSmallImgUrl() {
        return smallImgUrl;
    }

    public void setSmallImgUrl(String smallImgUrl) {
        this.smallImgUrl = smallImgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
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

    public void setDetails(String details) {
        this.details = details;
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

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getPubYear() {
        return pubYear;
    }

    public void setPubYear(String pubYear) {
        this.pubYear = pubYear;
    }

    public String getPubMonth() {
        return pubMonth;
    }

    public void setPubMonth(String pubMonth) {
        this.pubMonth = pubMonth;
    }

    public String getPubDay() {
        return pubDay;
    }

    public void setPubDay(String pubDay) {
        this.pubDay = pubDay;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;

    }
}