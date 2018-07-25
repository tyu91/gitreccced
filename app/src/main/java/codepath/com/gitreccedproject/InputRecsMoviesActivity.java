package codepath.com.gitreccedproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;


public class InputRecsMoviesActivity extends AppCompatActivity {

    Client client = new Client("IF4OZJWJDV", "08b9cd4c085bb021ef94d0781fd000fe");
    //Index index;


    public android.support.v7.widget.SearchView search_et;
    public RecyclerView searchlist_rv;
    public Button algolia_btn;
    public Button next_btn, skip;

    //DatabaseReference dbUsers;

    public SearchAdapter searchAdapter;
    public ArrayList<Item> items;

    static User resultUser;

    String uid = "inputrecsmovieactivity: user id not set yet"; //user id (initialized to dummy string for testing)

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(SearchAdapter.finalMovieRecs != null) {
            SearchAdapter.finalMovieRecs.clear();
        } else {
            SearchAdapter.finalMovieRecs = new ArrayList<>();
        }

        if(SearchAdapter.finalTVRecs != null) {
            SearchAdapter.finalTVRecs.clear();
        } else {
            SearchAdapter.finalTVRecs = new ArrayList<>();
        }

        if(SearchAdapter.finalBookRecs != null) {
            SearchAdapter.finalBookRecs.clear();
        } else {
            SearchAdapter.finalBookRecs = new ArrayList<>();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_recs_movies);

        Toast toast = Toast.makeText(getApplicationContext(), "Recommending Movies.",
                Toast.LENGTH_SHORT);
        toast.show();

        //dbUsers = FirebaseDatabase.getInstance().getReference("users");

        //add user id from sign up activity
        resultUser = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));
        Log.i("uid",resultUser.getUid().toString());
        //uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //dbUsers.child(uid).setValue(resultUser);
        //resultUser.setUid(uid);

        // find the views
        search_et = findViewById(R.id.search_et);
        searchlist_rv = findViewById(R.id.searchlist_rv);
        next_btn = findViewById(R.id.next_btn);

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

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(InputRecsMoviesActivity.this, InputRecsTVActivity.class);
                i.putExtra("user",Parcels.wrap(resultUser));
                startActivity(i);
            }
        });

        // perform set on query text listener event
        search_et.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && TextUtils.getTrimmedLength(query) > 0) {
                    query = query.trim();
                    Log.i("content", query);
                    client.getIndex("movies").searchAsync(new Query(query), null, new CompletionHandler() {
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
                } else {
                    Log.i("search", "empty!");
                    items.clear();
                    searchAdapter.notifyDataSetChanged();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && TextUtils.getTrimmedLength(newText) > 0) {
                    Log.i("text",String.format("%s, %s", newText, TextUtils.getTrimmedLength(newText)));
                    newText = newText.trim();
                    Log.i("content", newText);
                    client.getIndex("movies").searchAsync(new Query(newText), null, new CompletionHandler() {
                        @Override
                        public void requestCompleted(JSONObject content, AlgoliaException error) {
                            Log.i("content", content.toString());
                            try {
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

        skip = findViewById(R.id.btnSkip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InputRecsMoviesActivity.this);

                final TextView tv = new TextView(InputRecsMoviesActivity.this);
                tv.setText("Are you sure you want to skip this?");
                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(tv);

                // set dialog message
                alertDialogBuilder.setCancelable(true).setPositiveButton("Go to Library", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(InputRecsMoviesActivity.this, MyLibraryActivity.class);
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
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}