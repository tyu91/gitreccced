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

    public String bookId;
    public String author;
    public String smallImgUrl;
    public String imgUrl;

    public Item () {
        //empty constructor
    }

    public Item(String iid, String genre, String title, String details) {
        this.iid = iid;
        this.genre = genre;
        this.title = title;
        this.details = details;
        bookId = "no associated book id";
        author = "no associated author";
        smallImgUrl = "no associated smallImgUrl";
        imgUrl = "no associated imgUrl";
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
}