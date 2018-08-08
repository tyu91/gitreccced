package codepath.com.gitreccedproject;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.parceler.Parcels;

public class DetailsActivity extends AppCompatActivity {

    RecyclerView recycleV;
    DetailsAdapter adapterD;

    Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        item = Parcels.unwrap(getIntent().getParcelableExtra("item"));

//        recycleV = findViewById(R.id.rvRecycle);
//        recycleV.setLayoutManager(new LinearLayoutManager(this));
//        recycleV.setAdapter(adapterD);

        final FragmentManager fragManager = getSupportFragmentManager();

        final MovieDetailsFragment movieFrag = new MovieDetailsFragment();
        final TVDetailsFragment tvFrag = new TVDetailsFragment();
        final BookDetailsFragment bookFrag = new BookDetailsFragment();

        FragmentTransaction fragTrans = fragManager.beginTransaction();

        if (item.getGenre().equalsIgnoreCase("Movie")){
            fragTrans.replace(R.id.flShell, movieFrag).commit();
            Log.i("fragment", "movie");

        } else if (item.getGenre().equalsIgnoreCase("TV")){
            fragTrans.replace(R.id.flShell, tvFrag).commit();
        } else {
            fragTrans.replace(R.id.flShell, bookFrag).commit();
        }
    }
}