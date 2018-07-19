package codepath.com.gitreccedproject;

import org.parceler.Parcel;

@Parcel
public class User{

    public String uid;
    public String username;
    public String password;
    public String email;
    public Item item;

    public User() {
        //empty constructor
    }

    public User (String uid, String username, String password, String email, Item item) {
        this.uid = uid;
        this.username = username;
        this.password = password;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public Item getItem() {
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

    public void setItem(Item item) {
        this.item = item;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
