package codepath.com.gitreccedproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class DBTestActivity extends AppCompatActivity {

    //CONSTANTS
    //base url of API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    //parameter name
    public final static String API_KEY_PARAM = "api_key";

    AsyncHttpClient client;

    Config config;

    DatabaseReference dbUsers;
    EditText enterUsername;
    EditText enterPassword;
    Button btnSubmit;

    ArrayList<JSONMovie> mMovies = new ArrayList<>();

    DatabaseReference dbMovies;
    DatabaseReference dbTVShows;
    Button btnAddItems;
    Button btnAddToDb;
    Button btnNext;

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
        dbMovies = FirebaseDatabase.getInstance().getReference("movies");
        dbTVShows = FirebaseDatabase.getInstance().getReference("tv");
        btnAddItems = findViewById(R.id.btnAddItems);
        btnAddToDb = findViewById(R.id.btnAddToDb);
        btnNext = findViewById(R.id.btnNext);

        client = new AsyncHttpClient();

        //set click listener to populate movies to realtime db
        btnAddItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add movies to db
                addMovies();
                //new MoviePopulateAsync().execute();
                //add tv shows to db
                //addTVShows();
                Toast.makeText(getApplicationContext(), "Added TV", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddToDb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                updateMovies();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DBTestActivity.this, DBTest2Activity.class);
                startActivity(intent);
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
        String pagenum;

        for (int i = 1; i <= 994; i++) {

            pagenum = String.valueOf(i);

            params.put(API_KEY_PARAM, getString(R.string.movieApiKey));

            //adds &page=<pagenum>
            params.put("page", pagenum);
            //execute a GET request to get basic details from the search endpoint
            client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {
                        JSONArray results = response.getJSONArray("results");
                        //iterate thru result array list and create Movie objects
                        for (int j = 0; j < results.length(); j++) {
                            //TODO: change iid and uid to what they should actually be

                            //create new item id
                            iid = dbMovies.push().getKey();

                            JSONMovie newItem = new JSONMovie(iid, results.getJSONObject(j));

                            //mMovies.add(newItem);

                            //add item to db
                            dbMovies.child(iid).setValue(newItem);
                        }

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

        /*for (int i = 0; i < mMovies.size(); i++) {
            client = new AsyncHttpClient();
            //create the url
            url = API_BASE_URL + "/movie/" + mMovies.get(i).getMovieId() + "/credits";

            final int finalI = i;

            //execute GET request to get cast and director
            client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {
                        JSONArray cast = response.getJSONArray("cast");

                        int num_cast = 5;

                        if (num_cast > cast.length()) {
                            num_cast = cast.length();
                        }

                        //iterate thru cast JSON array list and set cast string
                        for (int j = 0; j < num_cast; j++) {
                            String currentCast = mMovies.get(finalI).getCast();
                            mMovies.get(finalI).setCast(currentCast + ", " + cast.getJSONObject(j).getString("name"));
                        }

                        //set director

                        JSONArray crew = response.getJSONArray("crew");
                        for (int j = 0; j < crew.length(); j++) {
                            if (crew.getJSONObject(j).getString("job").equalsIgnoreCase("director")) {
                                //if job title in object is director
                                mMovies.get(finalI).setDirector(crew.getJSONObject(j).getString("name"));
                            }
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

        //push JSONMovie objects in mMovies to firebase db
        for (int i = 0; i < mMovies.size(); i++) {
            //add item to db
            dbMovies.child(mMovies.get(i).getIid()).setValue(mMovies.get(i));
        }*/

    }

    private void updateMovies() {
        /*dbMovies.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot movieSnapshot : dataSnapshot.getChildren()) {
                    Log.i("DBTest", "Movie Snapshot: " + movieSnapshot);
                    JSONMovie tempMovie = movieSnapshot.getValue(JSONMovie.class);
                    iid = tempMovie.getIid();
                    String movieId = tempMovie.getMovieId();

                    if (!(tempMovie.getDirector().equalsIgnoreCase("director unassigned"))) {
                        //if director assigned (not unassigned), do nothing
                    } else {

                        //execute get request to get director
                        client = new AsyncHttpClient();

                        //create the url
                        String url = API_BASE_URL + "/movie/" + movieId + "/credits";

                        //set up request parameters
                        RequestParams params = new RequestParams();

                        params.put(API_KEY_PARAM, getString(R.string.movieApiKey));

                        //execute GET request to get cast and director
                        client.get(url, params, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                                try {
                                    JSONArray cast = response.getJSONArray("cast");

                                    //set director
                                    JSONArray crew = response.getJSONArray("crew");
                                    for (int j = 0; j < crew.length(); j++) {
                                        if (crew.getJSONObject(j).getString("job").equalsIgnoreCase("director")) {
                                            //if job title in object is director
                                            dbMovies.child(iid).child("director").setValue(crew.getJSONObject(j).getString("name"));
                                            Log.i("DBTest", "Director Name: " + crew.getJSONObject(j).getString("name"));
                                        }
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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        for (int i = 0; i < mMovies.size(); i++) {
            client = new AsyncHttpClient();
            //create the url
            String url = API_BASE_URL + "/movie/" + mMovies.get(i).getMovieId() + "/credits";

            Log.i("DBTest", "URL: " + url);

            //set up request parameters
            RequestParams params = new RequestParams();

            params.put(API_KEY_PARAM, getString(R.string.movieApiKey));

            final int finalI = i;

            //execute GET request to get cast and director
            client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONArray cast = response.getJSONArray("cast");

                        int num_cast = 5;

                        if (num_cast > cast.length()) {
                            num_cast = cast.length();
                        }

                        //iterate thru cast JSON array list and set cast string
                        for (int j = 0; j < num_cast; j++) {
                            String currentCast = mMovies.get(finalI).getCast();
                            mMovies.get(finalI).setCast(currentCast + ", " + cast.getJSONObject(j).getString("name"));
                        }

                        //set director

                        JSONArray crew = response.getJSONArray("crew");
                        for (int j = 0; j < crew.length(); j++) {
                            if (crew.getJSONObject(j).getString("job").equalsIgnoreCase("director")) {
                                //if job title in object is director
                                mMovies.get(finalI).setDirector(crew.getJSONObject(j).getString("name"));
                            }
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

        //push JSONMovie objects in mMovies to firebase db
        for (int i = 0; i < mMovies.size(); i++) {
            //add item to db
            dbMovies.child(mMovies.get(i).getIid()).setValue(mMovies.get(i));
        }
    }

    //adds tv shows to db from themoviedb.org
    private void addTVShows() {

        //create the url
        String url = API_BASE_URL + "/tv/popular";
        //set up request parameters
        RequestParams params = new RequestParams();
        String pagenum;

        for (int i = 1; i <= 1003; i++) {

            pagenum = String.valueOf(i);

            //adds apikey to request
            params.put(API_KEY_PARAM, getString(R.string.movieApiKey));

            //adds &page=<pagenum>
            params.put("page", pagenum);
            //execute a GET request that expects a response from JSON object
            client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {
                        JSONArray results = response.getJSONArray("results");
                        //iterate thru result array list and create Movie objects
                        for (int j = 0; j < results.length(); j++) {
                            //TODO: change iid and uid to what they should actually be

                            //create new item id
                            iid = dbTVShows.push().getKey();

                            JSONTv newItem = new JSONTv(iid, results.getJSONObject(j));

                            //add item to db
                            dbTVShows.child(iid).setValue(newItem);

                            //movies.add(movie);
                            //notify adapter a new row was added
                            //adapter.notifyItemInserted(movies.size() - 1);
                        }

                        Log.i("TestActivity", "Loaded some tv shows");

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

    }

    class MoviePopulateAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


        }
    }

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DB_TEST_REQUEST_CODE) {
            Toast.makeText(this, "successfully entered item into database!", Toast.LENGTH_SHORT).show();
        }
    }*/
}
