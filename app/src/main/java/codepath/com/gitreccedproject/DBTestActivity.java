package codepath.com.gitreccedproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DBTestActivity extends AppCompatActivity {

    //CONSTANTS
    //base url of API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    //parameter name
    public final static String API__KEY_PARAM = "api_key";

    DatabaseReference dbUsers;
    EditText enterUsername;
    EditText enterPassword;
    Button btnSubmit;

    DatabaseReference dbMovies;
    Button btnAddMovies;

    String uid = "1: user id not set yet"; //user id (initialized to dummy string for testing)
    String iid = "1: item id not set yet"; //item id (initialized to dummy string for testing)

    protected static final int DB_TEST_REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbtest);

        //reference to users field of json array in database
        dbUsers = FirebaseDatabase.getInstance().getReference("users");
        enterUsername = findViewById(R.id.etSubmitUsername);
        enterPassword = findViewById(R.id.etSubmitPassword);
        btnSubmit = findViewById(R.id.btnSubmit);

        //set up movie population button and movies db
        dbMovies = FirebaseDatabase.getInstance().getReference("movies");
        btnAddMovies = findViewById(R.id.btnAddMovies);

        //set click listener to submit user info to realtime db
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add new user to users field
                addUser();
            }
        });

        //set click listener to populate movies to realtime db
        btnAddMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add movies to db
                addMovies();
            }
        });
    }

    //adds user to db
    private void addUser(){
        String username = enterUsername.getText().toString();
        String password = enterPassword.getText().toString();

        if(!TextUtils.isEmpty(username)){
            uid = dbUsers.push().getKey();

            //pass userid to test recommendations page
            Intent intent = new Intent(DBTestActivity.this, DBTest2Activity.class);
            intent.putExtra("uid", uid);
            startActivityForResult(intent, DB_TEST_REQUEST_CODE);

            //new user to add
            User newUser = new User(uid, username, password, iid);

            dbUsers.push().setValue(newUser);

        }
    }

    //adds movies to db from themoviedb.org
    private void addMovies() {
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DB_TEST_REQUEST_CODE) {
            Toast.makeText(this, "successfully entered item into database!", Toast.LENGTH_SHORT).show();
        }
    }
}
