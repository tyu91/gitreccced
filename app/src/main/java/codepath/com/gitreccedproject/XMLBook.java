package codepath.com.gitreccedproject;

public class XMLBook {
    public String iid;
    public String bookId;
    public String title;
    public String author;
    public String genre;
    public String description;
    public String smallImgUrl;
    public String imgUrl;

    public String getIid() {
        return iid;
    }

    public String getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public String getDescription() {
        return description;
    }

    public String getSmallImgUrl() {
        return smallImgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSmallImgUrl(String smallImgUrl) {
        this.smallImgUrl = smallImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
