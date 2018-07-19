package codepath.com.gitreccedproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.algolia.search.saas.Client;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

//this activity loads data from firebase into algolia
public class AlgoliaActivity extends AppCompatActivity {

    Client client = new Client("IF4OZJWJDV", ""); //add api key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_algolia);

        client.getIndex("items").clearIndexAsync(null,null);

        DatabaseReference itemsRef;
        itemsRef = FirebaseDatabase.getInstance().getReference("items");

        com.google.firebase.database.Query query = null;
        query = itemsRef;

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.i("children", "children");

                    Item item = new Item();

                    item.setIid(postSnapshot.getKey());
                    item.setGenre(postSnapshot.child("genre").getValue().toString());
                    item.setDetails(postSnapshot.child("overview").getValue().toString());
                    item.setTitle(postSnapshot.child("title").getValue().toString());

                    {
                        try {
                            client.getIndex("item").addObjectAsync(new JSONObject()
                                    .put("Iid", postSnapshot.getKey())
                                    .put("genre", postSnapshot.child("genre").getValue().toString())
                                    .put("overview", postSnapshot.child("overview").getValue().toString())
                                    .put("title", postSnapshot.child("title").getValue().toString()), null);
                            Log.i("algolia",postSnapshot.child("title").getValue().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("snapshot", "loadPost:onCancelled");
            }
        });
    }
}
