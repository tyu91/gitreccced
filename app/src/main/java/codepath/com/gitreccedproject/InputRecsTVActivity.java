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

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Query;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InputRecsTVActivity extends AppCompatActivity {

    Client client = new Client("IF4OZJWJDV", "08b9cd4c085bb021ef94d0781fd000fe");
    //Index index;


    public SearchView search_et;
    public RecyclerView searchlist_rv;
    public Button algolia_btn;

    DatabaseReference dbUsers;

    public SearchAdapter searchAdapter;
    public ArrayList<Item> items;

    String uid = "irta: user id not set yet"; //user id (initialized to dummy string for testing)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_recs_tv);

        dbUsers = FirebaseDatabase.getInstance().getReference("users");

        /*//add user id from sign up activity
        User resultUser = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbUsers.child(uid).setValue(resultUser);
        resultUser.setUid(uid);*/

        // find the views
        search_et = (SearchView) findViewById(R.id.search_et);
        searchlist_rv = findViewById(R.id.searchlist_rv);
        algolia_btn = findViewById(R.id.algolia_btn);

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

        algolia_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InputRecsTVActivity.this, AlgoliaActivity.class);
                startActivity(i);
            }
        });

        // perform set on query text listener event
        search_et.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
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

                                items.add(item);
                                searchAdapter.notifyItemInserted(items.size() - 1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                client.getIndex("tv").searchAsync(new Query(newText), null, new CompletionHandler() {
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

                                items.add(item);
                                searchAdapter.notifyItemInserted(items.size() - 1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return false;
            }
        });
    }

    public void getSearchResults(String input) {
        client.getIndex("tv").searchAsync(new Query(input), null, new CompletionHandler() {
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

                        items.add(item);
                        searchAdapter.notifyItemInserted(items.size() - 1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
