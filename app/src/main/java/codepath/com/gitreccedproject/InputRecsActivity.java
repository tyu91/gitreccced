package codepath.com.gitreccedproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Query;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;


public class InputRecsActivity extends AppCompatActivity {

    Client client = new Client("IF4OZJWJDV", "08b9cd4c085bb021ef94d0781fd000fe");
    //Index index;


    public EditText search_et;
    public RecyclerView searchlist_rv;
    public ImageButton search_btn;
    public Button algolia_btn;

    DatabaseReference dbUsers;

    public SearchAdapter searchAdapter;
    public ArrayList<Item> items;

    String uid = "su: user id not set yet"; //user id (initialized to dummy string for testing)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_recs);

        dbUsers = FirebaseDatabase.getInstance().getReference("users");

        //add user id from sign up activity
        User resultUser = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));
        uid = SignUpActivity.mAuth.getCurrentUser().getUid();
        dbUsers.child(uid).setValue(resultUser);
        resultUser.setUid(uid);

        // find the views
        search_et = findViewById(R.id.search_et);
        searchlist_rv = findViewById(R.id.searchlist_rv);
        search_btn = findViewById(R.id.search_btn);
        algolia_btn = findViewById(R.id.algolia_btn);

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
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search_text = search_et.getText().toString();
                items.clear();
                searchAdapter.notifyDataSetChanged();
                getSearchResults(search_text);
            }
        });

        algolia_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InputRecsActivity.this, AlgoliaActivity.class);
                startActivity(i);
            }
        });

        search_et.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
            }

            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                client.getIndex("items").searchAsync(new Query(search_et.getText().toString()), null, new CompletionHandler() {
                    @Override
                    public void requestCompleted(JSONObject content, AlgoliaException error) {
                        Log.i("content", content.toString());
                        items.clear();
                        searchAdapter.notifyDataSetChanged();
                        try {
                            JSONArray array = content.getJSONArray("hits");
                            for (int i=0; i<array.length(); i++) {
                                JSONObject object = (JSONObject) array.getJSONObject(i);

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


            @Override public void afterTextChanged(Editable editable)
            {
            }
        });
    }

    public void getSearchResults(String input) {
        client.getIndex("items").searchAsync(new Query(input), null, new CompletionHandler() {
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