package codepath.com.gitreccedproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class InputRecsActivity extends AppCompatActivity {

    public EditText search_et;
    public RecyclerView searchlist_rv;
    public ImageButton search_btn;

    public SearchAdapter searchAdapter;
    public ArrayList<Item> items;

    //firebase
    //FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_recs);

        // find the views
        search_et = findViewById(R.id.search_et);
        searchlist_rv = findViewById(R.id.searchlist_rv);
        search_btn = findViewById(R.id.search_btn);

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
                getSearchResults(search_text);
            }
        });
    }

    public void getSearchResults(String input) {
        // TODO  - firebase query
        //goOnline();
        com.google.firebase.database.Query query = null;
        DatabaseReference itemsRef;
        itemsRef = FirebaseDatabase.getInstance().getReference().getRoot().child("movies");
        //DatabaseReference itemsRef = database.getReference("items");
        query = itemsRef.orderByChild("title").startAt(input);
        //goOnline();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("snapshot", "loadPost:onDataChange");
                Log.i("Snapshot", dataSnapshot.toString());
                List<String> cities = new ArrayList<String>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    cities.add(postSnapshot.getValue().toString());
                    Log.i("snapshot", postSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("snapshot", "loadPost:onCancelled");
            }
        });


        for (int i=0; i<5; i++) {
            Item item = null;
            items.add(item);
            searchAdapter.notifyItemInserted(items.size() - 1);
        }
    }
}

///implementation 'com.google.firebase:firebase-database:16.0.1'
