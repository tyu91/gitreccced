package codepath.com.gitreccedproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class DetailsActivity extends AppCompatActivity {

    RecyclerView recycleV;
    DetailsAdapter adapterD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        recycleV = findViewById(R.id.rvRecycle);
        recycleV.setLayoutManager(new LinearLayoutManager(this));
        recycleV.setAdapter(adapterD);
    }
}