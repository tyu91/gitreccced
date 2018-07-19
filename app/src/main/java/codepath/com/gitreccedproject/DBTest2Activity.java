package codepath.com.gitreccedproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;

public class DBTest2Activity extends AppCompatActivity {

    DatabaseReference dbItemsByUser;
    DatabaseReference dbUsersbyItem;
    EditText etGenre;
    EditText etTitle;
    Button btnSubmitItem;

    String uid = "2: user id not set yet"; //user id (initialized to dummy string for testing)
    String iid = "2: item id not set yet"; //item id (initialized to dummy string for testing)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbtest2);
    }

    private void addItem(){
        String genre = etGenre.getText().toString();
        String title = etTitle.getText().toString();

        if(!TextUtils.isEmpty(title)){
            iid = dbItemsByUser.push().getKey();
        }
    }
}