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

    public User (String uid) {
        this.uid = uid;
        this.username = username;
        this.password = password;
        this.item = item;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getItem() {
        return item;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
