package codepath.com.gitreccedproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class InputRecsActivity extends AppCompatActivity {

    public EditText search_et;
    public RecyclerView searchlist_rv;
    public ImageButton search_btn;

    public SearchAdapter searchAdapter;
    public ArrayList<Item> items;

    //firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        CollectionReference itemsRef = db.collection("items");
        Query query;
        query = itemsRef.orderBy("title").startAt(input).endAt(input + "\uf8ff");


        for (int i=0; i<5; i++) {
            Item item = null;
            items.add(item);
            searchAdapter.notifyItemInserted(items.size() - 1);
        }
    }
}
