package codepath.com.gitreccedproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class InputRecsBooksActivity extends AppCompatActivity {

    BookClient bClient = new BookClient();
    //Index index;


    public android.widget.SearchView search_sv;
    public RecyclerView searchlist_rv;
    public Button algolia_btn;
    public Button next_btn;

    DatabaseReference dbUsers;
    DatabaseReference dbBooks;

    public SearchAdapter searchAdapter;
    public ArrayList<Item> items;

    String uid = "inputrecsbooksactivity: user id not set yet"; //user id (initialized to dummy string for testing)
    String iid = "inputrecsbooksactivity: item id not set yet"; //user id (initialized to dummy string for testing)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_recs_books);

        Toast toast = Toast.makeText(getApplicationContext(), "Recommending Books.",
                Toast.LENGTH_SHORT);
        toast.show();

        dbUsers = FirebaseDatabase.getInstance().getReference("users");
        dbBooks = FirebaseDatabase.getInstance().getReference("books");

        //add user id from previous activity, the inputrecstv activity
        final User resultUser = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbUsers.child(uid).setValue(resultUser);
        resultUser.setUid(uid);

        // find the views
        search_sv = findViewById(R.id.search_sv);
        searchlist_rv = findViewById(R.id.searchlist_rv);
        algolia_btn = findViewById(R.id.algolia_btn);
        next_btn = findViewById(R.id.next_btn);

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
                items.clear();
                searchAdapter.notifyDataSetChanged();
                bClient.getBooks(query, new JsonHttpResponseHandler() {
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
                                // Load model objects into the adapter

                                //if results exist
                                if (response.getInt("num_found") != 0) {

                                        JSONBook book = books.get(0);

                                        String title = book.getTitle().toString();
                                        Log.i("Books", "Title: " + title);

                                        //create item id for new book

                                        //create new item id
                                        iid = dbBooks.push().getKey();

                                        setOverview(book);

                                        Item bookItem = new Item(iid, "Book", book.getTitle(), book.getOverview());

                                        //add item to db
                                        dbBooks.child(iid).setValue(bookItem);
                                        items.add(bookItem); // add book through the adapter
                                        Log.i("Books", "Title: " + title);
                                        searchAdapter.notifyDataSetChanged();
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

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        algolia_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InputRecsBooksActivity.this, AlgoliaActivity.class);
                startActivity(i);
            }
        });

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InputRecsBooksActivity.this, InputRecsTVActivity.class);
                i.putExtra("user", Parcels.wrap(resultUser));
                startActivity(i);
            }
        });
    }

    public void setOverview(final JSONBook book) {
        bClient.getExtraBookDetails(book.getOpenLibraryId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String description = "description unavailable";
                    if (response != null && response.has("description")) {
                        // Get the docs json array
                        description = response.getString("description");
                        //set overview of book
                        book.setOverview(description);
                    }

                    Log.i("Books", "description: " + description);
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
    }

}
