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

import org.parceler.Parcels;

import java.util.ArrayList;


public class InputRecsActivity extends AppCompatActivity {

    public EditText search_et;
    public RecyclerView searchlist_rv;
    public ImageButton search_btn;

    public SearchAdapter searchAdapter;
    public ArrayList<Item> items;

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
                items.clear();
                searchAdapter.notifyDataSetChanged();
                getSearchResults(search_text);
            }
        });
    }

    public void getSearchResults(String input) {
        com.google.firebase.database.Query query = null;
        DatabaseReference itemsRef;
        itemsRef = FirebaseDatabase.getInstance().getReference("movies");

        query = itemsRef.orderByChild("title").startAt(input).endAt(input + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Item item = new Item();

                    item.setIid(postSnapshot.getKey());
                    item.setGenre(postSnapshot.child("genre").getValue().toString());
                    item.setDetails(postSnapshot.child("overview").getValue().toString());
                    item.setTitle(postSnapshot.child("title").getValue().toString());
                    item.setUser((User) Parcels.unwrap(getIntent().getParcelableExtra("user")));

                    items.add(item);
                    searchAdapter.notifyItemInserted(items.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("snapshot", "loadPost:onCancelled");
            }
        });
    }
}
