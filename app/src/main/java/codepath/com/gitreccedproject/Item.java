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
}
