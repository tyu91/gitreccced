package codepath.com.gitreccedproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Query;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

public class InputRecsActivity extends AppCompatActivity {

    Client movieSearchClient = new Client("IF4OZJWJDV", "08b9cd4c085bb021ef94d0781fd000fe");
    Client tvSearchClient = new Client("IF4OZJWJDV", "08b9cd4c085bb021ef94d0781fd000fe");
    //Index index;
    public android.support.v7.widget.SearchView search_et;
    public RecyclerView searchlist_rv;
    public TextView skip, next;

    DatabaseReference dbUsers;
    DatabaseReference dbBooks;
    DatabaseReference dbItemsByUser;
    DatabaseReference dbUsersbyItem;

    DatabaseReference dbRecItemsByUser;

    public SearchAdapter searchAdapter;
    public ArrayList<Item> items;
    public ArrayList<String> watched = new ArrayList<>();

    ProgressBar pb;
    boolean isStart;

    boolean testPrint = true;

    String mQuery = "no response";
    String oldText = "";

    static User resultUser;

    String uid = "inputrecsactivity: user id not set yet"; //user id (initialized to dummy string for testing)
    String iid = "inputrecsactivity: item id not set yet"; //user id (initialized to dummy string for testing)

    //CONSTANTS
    //base url of API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    //parameter name
    public final static String API_KEY_PARAM = "api_key";

    AsyncHttpClient configClient;

    Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        configClient = new AsyncHttpClient();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_recs);

        dbUsers = FirebaseDatabase.getInstance().getReference("users");
        dbBooks = FirebaseDatabase.getInstance().getReference("books");

        //add user id from sign up activity
        resultUser = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));
        Log.i("uid",resultUser.getUid().toString());


        // find search views
        search_et = findViewById(R.id.search_et);
        searchlist_rv = findViewById(R.id.searchlist_rv);
        next = findViewById(R.id.tvNext);

        // find progress bar
        pb = (ProgressBar) findViewById(R.id.pbLoading);
        pb.bringToFront();
        isStart = true;

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

        //set skip button
        skip = findViewById(R.id.tvSkip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InputRecsActivity.this);

                final TextView tv = new TextView(InputRecsActivity.this);
                tv.setText("Are you sure you want to skip this?");
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(tv);

                // set dialog message
                alertDialogBuilder.
                        setCancelable(false).
                        setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                closeContextMenu();
                            }
                        }).
                        setPositiveButton("Go to Library", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(InputRecsActivity.this, MyLibraryActivity.class);
                                i.putExtra("user", Parcels.wrap(resultUser));
                                startActivity(i);
                                finish();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();

            }
        });

        dbItemsByUser = FirebaseDatabase.getInstance().getReference("itemsbyuser").child(resultUser.getUid());
        com.google.firebase.database.Query query = null;
        query = dbItemsByUser;

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String id = postSnapshot.child("iid").getValue().toString();
                    Log.i("id",postSnapshot.child("title").getValue().toString());
                    watched.add(id);
                }

                // perform set on query text listener event
                search_et.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
                            query = query.trim();
                            Log.i("content", query);
                            movieSearchClient.getIndex("movietv").searchAsync(new Query(query), null, new CompletionHandler() {
                                @Override
                                public void requestCompleted(JSONObject content, AlgoliaException error) {
                                    Log.i("content", content.toString());
                                    try {
                                        items.clear();
                                        searchAdapter.notifyDataSetChanged();
                                        JSONArray array = content.getJSONArray("hits");
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject object = array.getJSONObject(i);
                                            Log.i("array", Arrays.asList(watched).toString());
                                            Log.i("Iid", object.getString("Iid"));

                                            // check iid is not in watched
                                            if (!Arrays.asList(watched).contains(object.getString("Iid"))) {
                                                Item item = new Item();

                                                item.setIid(object.getString("Iid"));
                                                item.setGenre(object.getString("genre"));
                                                item.setDetails(object.getString("overview"));
                                                item.setTitle(object.getString("title"));
                                                item.setPosterPath(object.getString("posterPath"));
                                                item.setBackdropPath(object.getString("backdropPath"));
                                                item.setMovieId(object.getString("movieId"));
                                                if(item.getGenre().equalsIgnoreCase("Movie")) {
                                                    item.setReleaseDate(object.getString("releaseDate"));
                                                } else {
                                                    item.setFirstAirDate(object.getString("firstAirDate"));
                                                }

                                                items.add(item);
                                                searchAdapter.notifyItemInserted(items.size() - 1);
                                            } else {
                                                Log.i("watched", object.getString("title"));
                                            }

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } else {
                            //if search is empty, do not see progress bar
                            Log.i("search", "empty!");
                            items.clear();
                            searchAdapter.notifyDataSetChanged();
                            pb.setVisibility(ProgressBar.GONE);
                            isStart = true;

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
                            Log.i("text",String.format("%s, %s", newText, TextUtils.getTrimmedLength(newText)));
                            newText = newText.trim();
                            Log.i("content", newText);
                            movieSearchClient.getIndex("movietv").searchAsync(new Query(newText), null, new CompletionHandler() {
                                @Override
                                public void requestCompleted(JSONObject content, AlgoliaException error) {
                                    Log.i("content", content.toString());
                                    try {
                                        pb.setVisibility(ProgressBar.VISIBLE);
                                        items.clear();
                                        searchAdapter.notifyDataSetChanged();

                                        String text = search_et.getQuery().toString();
                                        if (text != null && TextUtils.getTrimmedLength(text) > 0) {
                                            JSONArray array = content.getJSONArray("hits");
                                            int num_results = 10;

                                            if (array.length() < num_results) {
                                                num_results = array.length();
                                            }

                                            for (int i = 0; i < num_results; i++) {
                                                JSONObject object = array.getJSONObject(i);

                                                // check iid is not in watched
                                                if (!Arrays.asList(watched).contains(object.getString("Iid"))) {

                                                    Item item = new Item();

                                                    item.setIid(object.getString("Iid"));
                                                    item.setGenre(object.getString("genre"));
                                                    item.setDetails(object.getString("overview"));
                                                    item.setTitle(object.getString("title"));
                                                    item.setPosterPath(object.getString("posterPath"));
                                                    item.setBackdropPath(object.getString("backdropPath"));
                                                    item.setMovieId(object.getString("movieId"));
                                                    if(item.getGenre().equalsIgnoreCase("Movie")) {
                                                        item.setReleaseDate(object.getString("releaseDate"));
                                                    } else {
                                                        item.setFirstAirDate(object.getString("firstAirDate"));
                                                    }

                                                    pb.setVisibility(ProgressBar.GONE);
                                                    items.add(item);
                                                    searchAdapter.notifyItemInserted(items.size() - 1);
                                                } else {
                                                    Log.i("watched", object.getString("title"));
                                                }
                                            }
                                        } else {
                                            pb.setVisibility(ProgressBar.GONE);
                                            isStart = true;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            mQuery = newText;
                            oldText = newText;
                            new BooksAsync().execute();

                        } else {
                            Log.i("search", "empty!");
                            items.clear();
                            searchAdapter.notifyDataSetChanged();
                        }
                        return false;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
            }
        });

        //get config for movie/tv posters
        getConfiguration();
    }

    //get the config from API
    private void getConfiguration() {
        //create the url
        String url = API_BASE_URL + "/configuration";
        //set up request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.movieApiKey)); //this is API key: always necessary!!!
        //execute a GET request that expects a response from JSON object
        configClient.get(url, params, new JsonHttpResponseHandler(){
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
            //items.clear();
            //searchAdapter.notifyDataSetChanged();

            //temp array for books in InputRecsActivity
            ArrayList<Item> mBooks = GoodreadsClient.books;

            Log.i("AsyncTag", "Success!");

            // BEGIN TRANSPLANTED CODE
            //clear items array, the array that loads into searchAdapter
            //items.clear();
            Log.i("XMLBookBook", "Items cleared");
            //searchAdapter.notifyDataSetChanged();

            //if text hasn't been deleted
            String text = search_et.getQuery().toString();
            if (TextUtils.getTrimmedLength(text) > 0) {
                //for each entry in response array, add entry to searchAdapter.
                int num_results = 10;

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

}