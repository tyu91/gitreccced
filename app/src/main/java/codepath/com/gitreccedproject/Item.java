package codepath.com.gitreccedproject;

public class Item {
    public String iid;
    public String genre;
    public String title;
    public String details;
    public String user;

    public Item (){
        //empty constructor
    }

    public Item(String iid, String genre, String title, String details, String user) {
        this.iid = iid;
        this.genre = genre;
        this.title = title;
        this.details = details;
        this.user = user;
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

    public String getUser() {
        return user;
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

    public void setUser(String user) {
        this.user = user;
    }
}
