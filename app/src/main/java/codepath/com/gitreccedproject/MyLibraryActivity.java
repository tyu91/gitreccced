package codepath.com.gitreccedproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyLibraryActivity extends AppCompatActivity {

    Button logOut;
    TextView greeting;
    FirebaseAuth mAuth;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_library);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        //logOut = findViewById(R.id.btnLogOut);
        //greeting = findViewById(R.id.tvGreeting);

        if (user != null) {
            //greeting.setText("Hey, " + user.getDisplayName());
        }

        //logOut.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View view) {
                //signOut();
            //}
        //});

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void signOut() {
        mAuth.signOut();

        final Intent i = new Intent(MyLibraryActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}