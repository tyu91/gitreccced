package codepath.com.gitreccedproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Query;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;


public class InputRecsMoviesActivity extends AppCompatActivity {

    Client client = new Client("IF4OZJWJDV", "08b9cd4c085bb021ef94d0781fd000fe");
    //Index index;
    public android.support.v7.widget.SearchView search_et;
    public RecyclerView searchlist_rv;
    public TextView skip, next;

    //DatabaseReference dbUsers;
    DatabaseReference dbItemsByUser;
    DatabaseReference dbUsersbyItem;

    DatabaseReference dbRecItemsByUser;

    public SearchAdapter searchAdapter;
    public ArrayList<Item> items;
    public ArrayList<String> watched = new ArrayList<>();

    static User resultUser;

    String uid = "inputrecsmovieactivity: user id not set yet"; //user id (initialized to dummy string for testing)

    @Override
    protected void onCreate(Bundle savedInstanceState) {


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
        next = findViewById(R.id.tvNext);

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
                //getrecs();
                Intent i = new Intent(InputRecsMoviesActivity.this, InputRecsTVActivity.class);
                i.putExtra("user",Parcels.wrap(resultUser));
                startActivity(i);
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
                                            Log.i("array", Arrays.asList(watched).toString());
                                            Log.i("Iid", object.getString("Iid"));

                                            // check iid is not in watched
                                            if (!Arrays.asList(watched).contains(object.getString("Iid"))) {
                                                Item item = new Item();

                                                item.setIid(object.getString("Iid"));
                                                item.setGenre(object.getString("genre"));
                                                item.setDetails(object.getString("overview"));
                                                item.setTitle(object.getString("title"));

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

                                                // check iid is not in watched
                                                if (!Arrays.asList(watched).contains(object.getString("Iid"))) {
                                                    Item item = new Item();

                                                    item.setIid(object.getString("Iid"));
                                                    item.setGenre(object.getString("genre"));
                                                    item.setDetails(object.getString("overview"));
                                                    item.setTitle(object.getString("title"));

                                                    items.add(item);
                                                    searchAdapter.notifyItemInserted(items.size() - 1);
                                                } else {
                                                    Log.i("watched", object.getString("title"));
                                                }
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
            }
        });

        skip = findViewById(R.id.tvSkip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InputRecsMoviesActivity.this);

                final TextView tv = new TextView(InputRecsMoviesActivity.this);
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
                                Intent i = new Intent(InputRecsMoviesActivity.this, MyLibraryActivity.class);
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
    }
}