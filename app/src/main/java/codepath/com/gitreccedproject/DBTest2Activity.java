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
    public ArrayList<Item> items;

    DatabaseReference dbItemsByUser;
    DatabaseReference dbUsersbyItem;
    EditText etGenre;
    EditText etTitle;
    Button btnSubmitItem;

    String mQuery = "no response";

    ArrayList<Item> mBooks;

    String uid = "dbtest2activity: user id not set yet"; //user id (initialized to dummy string for testing)
    String iid = "dbtest2activity: item id not set yet"; //user id (initialized to dummy string for testing)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbtest2);

        mBooks = new ArrayList<>();

        dbUsers = FirebaseDatabase.getInstance().getReference("users");
        dbBooks = FirebaseDatabase.getInstance().getReference("books");

        User currentUser = LoginActivity.currentuser;
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
    }

    class BooksAsync extends AsyncTask<Void, Void, Void> {
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

            //clear book search adapter
            items.clear();
            searchAdapter.notifyDataSetChanged();

            mBooks = GoodreadsClient.books;
            Log.i("AsyncTag", "Success!");

            // BEGIN TRANSPLANTED CODE
            //clear items array, the array that loads into searchAdapter
            //items.clear();
            Log.i("XMLBookBook", "Items cleared");
            searchAdapter.notifyDataSetChanged();

            //if text hasn't been deleted
            String text = search_sv.getQuery().toString();
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
                    //Item bookItem = new Item(iid, "Book", mBooks.get(i).getTitle(), mBooks.get(i).getDetails());
                    bookItem.setBookId(mBooks.get(i).getBookId());
                    Log.i("XMLBookBook", "Item Added to Adapter: " + bookItem.getTitle());
                    items.add(bookItem);
                    searchAdapter.notifyDataSetChanged();
                }
                //END TRANSPLANTED CODE
                super.onPostExecute(aVoid);
            }
        }
    }
}