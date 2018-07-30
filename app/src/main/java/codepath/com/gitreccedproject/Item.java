package codepath.com.gitreccedproject;

import org.parceler.Parcel;

@Parcel
public class Item {
    public String iid;
    public String genre;
    public String title;
    public String details;
    public String bookId;

    public Item () {
        //empty constructor
    }

    public Item(String iid, String genre, String title, String details) {
        this.iid = iid;
        this.genre = genre;
        this.title = title;
        this.details = details;
        bookId = "no book id";
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
}
