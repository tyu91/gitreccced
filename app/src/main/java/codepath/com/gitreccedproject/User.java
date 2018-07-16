package codepath.com.gitreccedproject;

public class User{

    public String uid;
    public String username;
    public String password;
    // item is currently a string for simplicity
    public String item;

    public User() {
        //empty constructor
    }

    public User (String uid, String username, String password, String item) {
        this.uid = uid;
        this.username = username;
        this.password = password;
        this.item = item;
    }
}
