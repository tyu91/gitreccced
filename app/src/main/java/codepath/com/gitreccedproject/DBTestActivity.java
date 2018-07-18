package codepath.com.gitreccedproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class DBTestActivity extends AppCompatActivity {

    //CONSTANTS
    //base url of API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    //parameter name
    public final static String API__KEY_PARAM = "api_key";

    AsyncHttpClient client;

    DatabaseReference dbUsers;
    EditText enterUsername;
    EditText enterPassword;
    Button btnSubmit;

    DatabaseReference dbItems;
    Button btnAddItems;

    String uid = "1: user id not set yet"; //user id (initialized to dummy string for testing)
    String iid = "1: item id not set yet"; //item id (initialized to dummy string for testing)

    protected static final int DB_TEST_REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbtest);

        /*//reference to users field of json array in database
        dbUsers = FirebaseDatabase.getInstance().getReference("users");
        enterUsername = findViewById(R.id.etSubmitUsername);
        enterPassword = findViewById(R.id.etSubmitPassword);
        btnSubmit = findViewById(R.id.btnSubmit);*/

        //set up movie population button and movies db
        dbItems = FirebaseDatabase.getInstance().getReference("items");
        btnAddItems = findViewById(R.id.btnAddItems);

        client = new AsyncHttpClient();

        //set click listener to populate movies to realtime db
        btnAddItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add movies to db
                addMovies();
            }
        });

        /*//set click listener to submit user info to realtime db
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add new user to users field
                addUser();
            }
        });

        */
    }

    /*//adds user to db


    private void addUser(){
        String username = enterUsername.getText().toString();
        String password = enterPassword.getText().toString();

        if(!TextUtils.isEmpty(username)){
            uid = dbUsers.push().getKey();

            //pass userid to test recommendations page
            Intent intent = new Intent(DBTestActivity.this, DBTest2Activity.class);
            intent.putExtra("uid", uid);
            intent.putExtra("username", username);
            intent.putExtra("password", password);
            startActivityForResult(intent, DB_TEST_REQUEST_CODE);

            //new user to add
            User newUser = new User(uid, username, password, iid);

            dbUsers.push().setValue(newUser);

        }
    }*/

    //adds movies to db from themoviedb.org
    private void addMovies() {

        //create the url
        String url = API_BASE_URL + "/movie/popular";
        //set up request parameters
        RequestParams params = new RequestParams();
        params.put(API__KEY_PARAM, getString(R.string.movieApiKey)); //this is API key: always necessary!!!
        //execute a GET request that expects a response from JSON object

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    JSONArray results = response.getJSONArray("results");
                    //iterate thru result array list and create Movie objects
                    for(int i = 0; i < results.length(); i++) {
                        //TODO: change iid and uid to what they should actually be

                        //create new item id
                        iid = dbItems.push().getKey();

                        JSONItem newItem = new JSONItem(iid, results.getJSONObject(i));

                        //add item to db
                        dbItems.child(iid).setValue(newItem);

                        //movies.add(movie);
                        //notify adapter a new row was added
                        //adapter.notifyItemInserted(movies.size() - 1);
                    }
                    Log.i("TestActivity", "Loaded 5 movies");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

    }

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DB_TEST_REQUEST_CODE) {
            Toast.makeText(this, "successfully entered item into database!", Toast.LENGTH_SHORT).show();
        }
    }*/
}
