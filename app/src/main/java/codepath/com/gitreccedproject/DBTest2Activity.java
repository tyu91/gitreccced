package codepath.com.gitreccedproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DBTest2Activity extends AppCompatActivity {

    GoodreadsClient bClient;
    //Index index;


    public android.widget.SearchView search_sv;
    public RecyclerView searchlist_rv;
    public Button finish_btn;

    DatabaseReference dbUsers;
    DatabaseReference dbBooks;

    public SearchAdapter searchAdapter;
    public ArrayList<JSONBook> mBooks;
    public ArrayList<Item> items;

    DatabaseReference dbItemsByUser;
    DatabaseReference dbUsersbyItem;
    EditText etGenre;
    EditText etTitle;
    Button btnSubmitItem;

    String mQuery = "no response";

    String uid = "dbtest2activity: user id not set yet"; //user id (initialized to dummy string for testing)
    String iid = "dbtest2activity: item id not set yet"; //user id (initialized to dummy string for testing)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbtest2);

        dbUsers = FirebaseDatabase.getInstance().getReference("users");
        dbBooks = FirebaseDatabase.getInstance().getReference("books");

        User currentUser = InputRecsMoviesActivity.resultUser;
        String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbUsers.child(uid).setValue(currentUser);
        currentUser.setUid(uid);

        // find the views
        search_sv = findViewById(R.id.search_sv);
        searchlist_rv = findViewById(R.id.searchlist_rv);
        finish_btn = findViewById(R.id.finish_btn);

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
                mQuery = query;
                new BookAsync().execute();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /*if (newText != null && TextUtils.getTrimmedLength(newText) > 0) {
                    newText = newText.trim();
                    Log.i("content", newText);

                    //calls getBooks (which calls OL search functionality to query for books)
                    bClient.getBooks(newText, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                JSONArray docs;
                                if (response != null) {
                                    // Get the docs json array
                                    docs = response.getJSONArray("docs");
                                    // Parse json array into array of model objects
                                    final ArrayList<JSONBook> books = JSONBook.fromJson(docs);
                                    // Remove all books from the adapter
                                    items.clear();
                                    searchAdapter.notifyDataSetChanged();
                                    // Load model objects into the adapter

                                    //if results exist and text hasn't been deleted
                                    String text = search_sv.getQuery().toString();
                                    if (response.getInt("num_found") != 0 && TextUtils.getTrimmedLength(text) > 0) {

                                        //for each entry in response array, add entry to searchAdapter.
                                        int num_results = 10;

                                        Log.i("Books", "num_results before = " + num_results);

                                        if (books.size() < num_results) {
                                            num_results = books.size();
                                        }

                                        Log.i("Books", "books.size = " + books.size());
                                        Log.i("Books", "num_results after = " + num_results);

                                        for(int i = 0; i < num_results; i++){
                                            String title = books.get(i).getTitle().toString();
                                            Log.i("Books", "Title: " + title);

                                            //create item id for new book

                                            //create new item id
                                            iid = dbBooks.push().getKey();

                                            setOverview(books.get(i));

                                            Item bookItem = new Item(iid, "Book", books.get(i).getTitle(), books.get(i).getOverview());
                                            items.add(bookItem);
                                            searchAdapter.notifyDataSetChanged();
                                        }
                                    }
                                } else {
                                    Toast toast = Toast.makeText(getApplicationContext(), "No results. Please try again!",
                                            Toast.LENGTH_SHORT);
                                    toast.show();


                                }
                            } catch (JSONException e) {
                                // Invalid JSON format, show appropriate error.
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                        }
                    });
                } else {
                    Log.i("search", "empty!");
                    items.clear();
                    searchAdapter.notifyDataSetChanged();
                }*/
                return false;
            }
        });
    }

    class BookAsync extends AsyncTask<Void, Void, Void> {
        GoodreadsClient client;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... voids) {
            client = new GoodreadsClient();
            client.searchBooks(mQuery);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i("AsyncTag", "Success!");
            super.onPostExecute(aVoid);
        }
    }

    private void addItem(){
        String genre = etGenre.getText().toString();
        String title = etTitle.getText().toString();

        if(!TextUtils.isEmpty(title)){
            iid = dbItemsByUser.push().getKey();
        }
    }
}