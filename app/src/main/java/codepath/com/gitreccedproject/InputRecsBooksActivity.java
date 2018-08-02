package codepath.com.gitreccedproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class InputRecsBooksActivity extends AppCompatActivity {

    GoodreadsClient bClient;
    //Index index;


    public android.widget.SearchView search_sv;
    public RecyclerView searchlist_rv;
    public TextView finish_btn;

    boolean testPrint = true;

    ProgressBar pb;
    boolean isStart;

    DatabaseReference dbUsers;
    DatabaseReference dbBooks;

    public SearchAdapter searchAdapter;
    public ArrayList<Item> items;

    DatabaseReference dbItemsByUser;
    DatabaseReference dbUsersbyItem;
    EditText etGenre;
    EditText etTitle;
    Button btnSubmitItem;

    String mQuery = "no response";

    ArrayList<Item> mBooks;

    String uid = "inputrecsbooksactivity: user id not set yet"; //user id (initialized to dummy string for testing)
    String iid = "inputrecsbooksactivity: item id not set yet"; //user id (initialized to dummy string for testing)

    //CONSTANTS
    //base url of API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    //parameter name
    public final static String API_KEY_PARAM = "api_key";

    AsyncHttpClient mClient;

    Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_recs_books);

        mClient = new AsyncHttpClient();

        mBooks = new ArrayList<>();

        dbUsers = FirebaseDatabase.getInstance().getReference("users");
        dbBooks = FirebaseDatabase.getInstance().getReference("books");

        final User currentUser = InputRecsMoviesActivity.resultUser;
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbUsers.child(uid).setValue(currentUser);
        currentUser.setUid(uid);

        // find the views
        pb = (ProgressBar) findViewById(R.id.pbLoading);
        pb.bringToFront();
        isStart = true;

        search_sv = findViewById(R.id.search_sv);
        searchlist_rv = findViewById(R.id.searchlist_rv);
        finish_btn = findViewById(R.id.tvFinish);

        search_sv.setIconifiedByDefault(false);

        // init the arraylist (data source)
        items = new ArrayList<>();
        // construct the adapter from this datasource
        searchAdapter = new SearchAdapter(items);
        // RecyclerView setup (layout manager, use adapter)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        searchlist_rv.setLayoutManager(linearLayoutManager);
        // set the adapter
        searchlist_rv.setAdapter(searchAdapter);

        // implement onclick listener
        search_sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //do nothing when you submit, or maybe later make the keyboard disappear

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mQuery = newText;
                new BooksAsync().execute();
                return false;
            }
        });

        finish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InputRecsBooksActivity.this, MyLibraryActivity.class);
                i.putExtra("user", Parcels.wrap(currentUser));
                startActivity(i);
            }
        });

        //get config for movie/tv posters
        getConfiguration();
    }

    class BooksAsync extends AsyncTask<Void, Void, Void> {
        GoodreadsClient client;

        @Override
        protected void onPreExecute() {
            if (isStart) {
                pb.setVisibility(ProgressBar.VISIBLE);
                isStart = false;
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            client = new GoodreadsClient();
            client.searchBooks(mQuery);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pb.setVisibility(ProgressBar.GONE);
            //clear book search adapter
            items.clear();
            searchAdapter.notifyDataSetChanged();

            mBooks = GoodreadsClient.books;
            Log.i("AsyncTag", "Success!");

            // BEGIN TRANSPLANTED CODE
            //clear items array, the array that loads into searchAdapter
            //items.clear();
            Log.i("XMLBookBook", "Items cleared");
            //searchAdapter.notifyDataSetChanged();

            //if text hasn't been deleted
            String text = search_sv.getQuery().toString();
            if (TextUtils.getTrimmedLength(text) > 0) {
                //for each entry in response array, add entry to searchAdapter.
                //todo: change back to 10 results later
                int num_results = 1;

                Log.i("Books", "num_results before = " + num_results);

                if (mBooks.size() < num_results) {
                    num_results = mBooks.size();
                }

                Log.i("Books", "books.size = " + mBooks.size());
                Log.i("Books", "num_results after = " + num_results);

                for (int i = 0; i < num_results; i++) {
                    String title = mBooks.get(i).getTitle().toString();
                    Log.i("Books", "Title: " + title);

                    Item bookItem = mBooks.get(i);

                    //create new item id
                    iid = dbBooks.push().getKey();

                    bookItem.setIid(iid);
                    bookItem.setBookId(mBooks.get(i).getBookId());
                    Log.i("XMLBookBook", "Item Added to Adapter: " + bookItem.getTitle());

                    if (testPrint) {
                        Log.i("IidItem", "BookId Before bookItem added to adapter: " + bookItem.getIid());
                        testPrint = false;
                    }
                    items.add(bookItem);
                }

                searchAdapter.notifyDataSetChanged();

                //END TRANSPLANTED CODE
                super.onPostExecute(aVoid);
            }
        }
    }

    //get the config from API
    private void getConfiguration() {
        //create the url
        String url = API_BASE_URL + "/configuration";
        //set up request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.movieApiKey)); //this is API key: always necessary!!!
        //execute a GET request that expects a response from JSON object
        mClient.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    config = new Config(response);
                    Log.i("MovieDB", String.format("Loaded config w imageBaseUrl %s and posterSize %s", config.getImageBaseUrl(), config.getPosterSize()));
                    searchAdapter.setConfig(config);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("MovieDB", "could not generate new config");
            }
        });
    }

}
