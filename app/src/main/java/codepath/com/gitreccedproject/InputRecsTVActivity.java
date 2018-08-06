package codepath.com.gitreccedproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Query;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class InputRecsTVActivity extends AppCompatActivity {

    Client client = new Client("IF4OZJWJDV", "08b9cd4c085bb021ef94d0781fd000fe");
    //Index index;


    public SearchView search_et;
    public RecyclerView searchlist_rv;

    public TextView next;

    DatabaseReference dbUsers;

    public SearchAdapter searchAdapter;
    public ArrayList<Item> items;

    ProgressBar pb;
    boolean isStart;

    String uid = "inputrecstvactivity: user id not set yet"; //user id (initialized to dummy string for testing)

    //CONSTANTS
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    //base url of API
    public final static String API_BASE_URL_TV = "https://api.themoviedb.org/3/tv";
    //parameter name
    public final static String API_KEY_PARAM = "api_key";

    AsyncHttpClient mClient;

    Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_recs_tv);

        mClient = new AsyncHttpClient();

        Toast toast = Toast.makeText(getApplicationContext(), "Recommending TV Shows.",
                Toast.LENGTH_SHORT);
        toast.show();

        next = findViewById(R.id.tvNext2);

        dbUsers = FirebaseDatabase.getInstance().getReference("users");

        //add user id from sign up activity
        final User resultUser = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbUsers.child(uid).setValue(resultUser);
        resultUser.setUid(uid);

        //set progress bar
        pb = (ProgressBar) findViewById(R.id.pbLoading);
        pb.bringToFront();
        isStart = true;

        search_et = (SearchView) findViewById(R.id.search_et);
        searchlist_rv = findViewById(R.id.searchlist_rv);

        search_et.setIconifiedByDefault(false);

        // init the arraylist (data source)
        items = new ArrayList<>();
        // construct the adapter from this datasource
        searchAdapter = new SearchAdapter(items);
        // RecyclerView setup (layout manager, use adapter)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        searchlist_rv.setLayoutManager(linearLayoutManager);
        // set the adapter
        searchlist_rv.setAdapter(searchAdapter);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InputRecsTVActivity.this, InputRecsBooksActivity.class);
                i.putExtra("user",Parcels.wrap(resultUser));
                startActivity(i);
            }
        });

        // perform set on query text listener event
        search_et.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && TextUtils.getTrimmedLength(query) > 0) {
                    query = query.trim();
                    Log.i("content", query);
                    client.getIndex("tv").searchAsync(new Query(query), null, new CompletionHandler() {
                        @Override
                        public void requestCompleted(JSONObject content, AlgoliaException error) {
                            Log.i("content", content.toString());
                            try {
                                items.clear();
                                searchAdapter.notifyDataSetChanged();
                                JSONArray array = content.getJSONArray("hits");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);

                                    Item item = new Item();

                                    item.setIid(object.getString("Iid"));
                                    item.setGenre(object.getString("genre"));
                                    item.setDetails(object.getString("overview"));
                                    item.setTitle(object.getString("title"));
                                    item.setPosterPath(object.getString("posterPath"));
                                    item.setBackdropPath(object.getString("backdropPath"));
                                    item.setMovieId(object.getString("movieId"));

                                    items.add(item);
                                    searchAdapter.notifyItemInserted(items.size() - 1);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Log.i("search", "empty!");
                    items.clear();
                    searchAdapter.notifyDataSetChanged();
                }
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                if (isStart) {
                    pb.setVisibility(ProgressBar.VISIBLE);
                    isStart = false;
                }
                if (newText != null && TextUtils.getTrimmedLength(newText) > 0) {
                    newText = newText.trim();
                    Log.i("content", newText);
                    client.getIndex("tv").searchAsync(new Query(newText), null, new CompletionHandler() {
                        @Override
                        public void requestCompleted(JSONObject content, AlgoliaException error) {
                            Log.i("content", content.toString());
                            try {
                                pb.setVisibility(ProgressBar.GONE);
                                items.clear();
                                searchAdapter.notifyDataSetChanged();

                                String text = search_et.getQuery().toString();
                                if (text != null && TextUtils.getTrimmedLength(text) > 0)
                                {
                                    JSONArray array = content.getJSONArray("hits");
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.getJSONObject(i);

                                        Item item = new Item();

                                        item.setIid(object.getString("Iid"));
                                        item.setGenre(object.getString("genre"));
                                        item.setDetails(object.getString("overview"));
                                        item.setTitle(object.getString("title"));
                                        item.setPosterPath(object.getString("posterPath"));
                                        item.setBackdropPath(object.getString("backdropPath"));
                                        item.setMovieId(object.getString("movieId"));

                                        items.add(item);
                                        searchAdapter.notifyItemInserted(items.size() - 1);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Log.i("search", "empty!");
                    items.clear();
                    searchAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        //get config for movie/tv posters
        getConfiguration();
        //getNumSeasons();
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
                    Log.i("TVMovieDB", String.format("Loaded config w imageBaseUrl %s and posterSize %s", config.getImageBaseUrl(), config.getPosterSize()));
                    searchAdapter.setConfig(config);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("TVMovieDB", "could not generate new config");
            }
        });
    }

    /*//get the config from API
    private void getNumSeasons() {
        //create the url
        String url = API_BASE_URL_TV + "/configuration";
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
    }*/
}
