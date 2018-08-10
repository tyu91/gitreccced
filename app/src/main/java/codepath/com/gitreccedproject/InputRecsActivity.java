package codepath.com.gitreccedproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Query;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

public class InputRecsActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    Client movieSearchClient = new Client("IF4OZJWJDV", "08b9cd4c085bb021ef94d0781fd000fe");
    Client tvSearchClient = new Client("IF4OZJWJDV", "08b9cd4c085bb021ef94d0781fd000fe");
    //Index index;
    public android.support.v7.widget.SearchView search_et;
    public RecyclerView searchlist_rv;
    public TextView finish;
    private DrawerLayout mDrawerLayout;

    DatabaseReference dbUsers;
    DatabaseReference dbBooks;
    DatabaseReference dbItemsByUser;
    DatabaseReference dbUsersbyItem;

    DatabaseReference dbRecItemsByUser;


    public SearchAdapter searchAdapter;
    public ArrayList<Item> items;
    public ArrayList<String> itemIds;
    public ArrayList<String> watched = new ArrayList<>();

    ProgressBar pb;
    boolean isStart;

    boolean testPrint = true;

    static String mQuery = "no response";
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_recs);

        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable mDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_menu);
        mDrawable.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP));

        //new PorterDuffColorFilter(0xffffff, PorterDuff.Mode.MULTIPLY)

        getSupportActionBar().setHomeAsUpIndicator(mDrawable);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);

        // initialize menu
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.logout).setChecked(false);
        nav_Menu.findItem(R.id.settings).setChecked(false);

        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        if (mAuth.getCurrentUser().getUid().contains("LpPtVsPQWyeOzejQj8uLK49zlCX2")) {
                            Log.i("user","admin");
                            Menu nav_Menu = navigationView.getMenu();
                            nav_Menu.findItem(R.id.algolia).setVisible(true);
                            nav_Menu.findItem(R.id.dbtest).setVisible(true);
                            nav_Menu.findItem(R.id.algolia).setChecked(false);
                            nav_Menu.findItem(R.id.dbtest).setChecked(false);
                        } else {
                            Log.i("user",mAuth.getCurrentUser().getUid());
                        }
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        Menu nav_Menu = navigationView.getMenu();
                        nav_Menu.findItem(R.id.algolia).setChecked(false);
                        nav_Menu.findItem(R.id.dbtest).setChecked(false);
                        nav_Menu.findItem(R.id.logout).setChecked(false);
                        nav_Menu.findItem(R.id.settings).setChecked(false);
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        if (menuItem.getItemId() == R.id.logout) {
                            Log.i("menu","logout selected");
                            mAuth.signOut();
                            final Intent i = new Intent(InputRecsActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                            Toast.makeText(getApplicationContext(), "Logged out!", Toast.LENGTH_SHORT).show();
                        }
                        if (menuItem.getItemId() == R.id.algolia) {
                            Intent i = new Intent(getApplicationContext(), AlgoliaActivity.class);
                            startActivity(i);
                        }
                        if (menuItem.getItemId() == R.id.dbtest) {
                            Intent intent = new Intent(getApplicationContext(), DBTestActivity.class);
                            startActivity(intent);
                        }
                        if (menuItem.getItemId() == R.id.settings) {
                            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                            startActivity(intent);
                        }
                        return true;
                    }
                });

        configClient = new AsyncHttpClient();

        //testing
        String OGString = "The Raven";
        ArrayList<String> toCompare = new ArrayList<>(Arrays.asList("raven", "he raven", "theraven", "amazing race", "rave", "raven cycle", "the raven", "The Raven", "maven"));
        ArrayList<String> toCompare1 = new ArrayList<>(Arrays.asList("The Revenant", "Good Witch", "M*A*S*H", "Jaws: The Revenge", "Resident Evil", "raven cycle", "the raven", "The Raven", "maven"));

        //insertion sort by comparing Levenshtein distances
        for (int i = 0; i < toCompare.size() - 1; i++) {
            for (int j = i + 1; j < toCompare.size(); j++) {
                LevenshteinDistance d1 = new LevenshteinDistance(100);
                LevenshteinDistance d2 = new LevenshteinDistance(100);

                int iDistance = d1.apply(OGString, toCompare.get(i));
                int jDistance = d2.apply(OGString, toCompare.get(j));

                if (jDistance < iDistance) {
                    String tempString = toCompare.get(i);
                    toCompare.set(i, toCompare.get(j));
                    toCompare.set(j, tempString);
                }
            }
        }

        for (int i = 0; i < toCompare.size(); i++) {
            LevenshteinDistance distance = new LevenshteinDistance(100);
            Log.i("fLevenshtein", "Query: " + OGString + "|| Title: " + toCompare.get(i)
                    + "   ||   LDistance: " + distance.apply(OGString, toCompare.get(i)));
        }

        Log.i("fLevenshtein", "********** F I N I S H E D **********");

        dbUsers = FirebaseDatabase.getInstance().getReference("users");
        dbBooks = FirebaseDatabase.getInstance().getReference("books");

        //add user id from sign up activity
        resultUser = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));
        Log.i("uid", resultUser.getUid().toString());


        // find search views
        search_et = findViewById(R.id.search_et);
        searchlist_rv = findViewById(R.id.searchlist_rv);

        // find progress bar
        pb = (ProgressBar) findViewById(R.id.pbLoading);
        pb.bringToFront();
        isStart = true;

        search_et.setIconifiedByDefault(false);

        // init the arraylist (data source)
        items = new ArrayList<>();
        itemIds = new ArrayList<>();
        // construct the adapter from this datasource
        searchAdapter = new SearchAdapter(items);
        // RecyclerView setup (layout manager, use adapter)
        WrapContentLinearLayoutManager linearLayoutManager = new WrapContentLinearLayoutManager(this);
        searchlist_rv.setLayoutManager(linearLayoutManager);
        // set the adapter
        searchlist_rv.setAdapter(searchAdapter);

        //set skip button
        finish = findViewById(R.id.tvFins);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InputRecsActivity.this, MyLibraryActivity.class);
                i.putExtra("user", Parcels.wrap(resultUser));
                startActivity(i);
                finish();
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
                    Log.i("id", postSnapshot.child("title").getValue().toString());
                    watched.add(id);
                }

                // perform set on query text listener event
                search_et.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
                            query = query.trim();
                            Log.i("content", query);
                            final String finalQuery = query;
                            movieSearchClient.getIndex("movietv").searchAsync(new Query(query), null, new CompletionHandler() {
                                @Override
                                public void requestCompleted(JSONObject content, AlgoliaException error) {
                                    Log.i("content", content.toString());
                                    try {
                                        items.clear();
                                        itemIds.clear();
                                        searchAdapter.notifyItemRangeChanged(0, items.size());
                                        Log.i("LevenshteinMovieTV", "ADAPTER CLEARED");
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
                                                if (item.getGenre().equalsIgnoreCase("Movie")) {
                                                    item.setReleaseDate(object.getString("releaseDate"));
                                                } else {
                                                    item.setFirstAirDate(object.getString("firstAirDate"));
                                                }

                                                if (!(itemIds.contains(item.getMovieId()))) {
                                                    //if movieId does not yet exist in items array list, add to items and notify adapter
                                                    items.add(item);
                                                    itemIds.add(item.getMovieId());
                                                    searchAdapter.sortedNotifyItemInserted(finalQuery, items.size() - 1);
                                                    Log.i("LevenshteinMovieTV", "ADAPTER ITEM INSERTED");
                                                }

                                            } else {
                                                Log.i("watched", object.getString("title"));
                                            }

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            oldText = mQuery;
                            mQuery = query;

                            new BooksAsync().execute();

                        } else {
                            //if search is empty, do not see progress bar
                            Log.i("search", "empty!");
                            items.clear();
                            itemIds.clear();
                            searchAdapter.notifyItemRangeChanged(0, items.size());
                            Log.i("Levenshtein", "ADAPTER CLEARED");
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
                            Log.i("text", String.format("%s, %s", newText, TextUtils.getTrimmedLength(newText)));
                            newText = newText.trim();
                            Log.i("content", newText);
                            final String finalNewText = newText;
                            movieSearchClient.getIndex("movietv").searchAsync(new Query(newText), null, new CompletionHandler() {
                                @Override
                                public void requestCompleted(JSONObject content, AlgoliaException error) {
                                    Log.i("content", content.toString());
                                    try {
                                        pb.setVisibility(ProgressBar.VISIBLE);
                                        items.clear();
                                        itemIds.clear();
                                        searchAdapter.notifyItemRangeChanged(0, items.size());
                                        Log.i("LevenshteinMovieTV", "ADAPTER CLEARED");

                                        String text = search_et.getQuery().toString();
                                        if (text != null && TextUtils.getTrimmedLength(text) > 0) {
                                            JSONArray array = content.getJSONArray("hits");
                                            int num_results = 15;

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
                                                    if (item.getGenre().equalsIgnoreCase("Movie")) {
                                                        item.setReleaseDate(object.getString("releaseDate"));
                                                    } else {
                                                        item.setFirstAirDate(object.getString("firstAirDate"));
                                                    }

                                                    pb.setVisibility(ProgressBar.GONE);
                                                    if (!(itemIds.contains(item.getMovieId()))) {
                                                        //if movieId does not yet exist in items array list, add to items and notify adapter
                                                        items.add(item);
                                                        itemIds.add(item.getMovieId());
                                                        searchAdapter.sortedNotifyItemInserted(finalNewText, items.size() - 1);
                                                        Log.i("LevenshteinMovieTV", "ADAPTER ITEM INSERTED");
                                                    }
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

                            oldText = mQuery;
                            mQuery = newText;

                            new BooksAsync().execute();

                        } else {
                            Log.i("search", "empty!");
                            items.clear();
                            itemIds.clear();
                            searchAdapter.notifyItemRangeChanged(0, items.size());
                            Log.i("Levenshtein", "ADAPTER CLEARED");
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
        configClient.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    config = new Config(response);
                    Log.i("MovieDB", String.format("Loaded config w imageBaseUrl %s and posterSize %s", config.getImageBaseUrl(), config.getPosterSize()));
                    searchAdapter.setConfig(config);
                } catch (JSONException e) {
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

            //temp array for books in InputRecsActivity
            ArrayList<Item> mBooks = GoodreadsClient.books;

            Log.i("AsyncTag", "Success!");

            // BEGIN TRANSPLANTED CODE
            //clear items array, the array that loads into searchAdapter
            //items.clear();
            Log.i("XMLBookBook", "Items cleared");

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

                    if (!(itemIds.contains(bookItem.getBookId()))) {
                        //if movieId does not yet exist in items array list, add to items and notify adapter
                        items.add(bookItem);
                        itemIds.add(bookItem.getBookId());
                        searchAdapter.sortedNotifyItemInserted(text, items.size() - 1);
                        Log.i("LevenshteinBook", "ADAPTER ITEM INSERTED");
                    }
                }

                //END TRANSPLANTED CODE
                super.onPostExecute(aVoid);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}