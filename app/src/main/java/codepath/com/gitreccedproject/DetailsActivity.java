package codepath.com.gitreccedproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.parceler.Parcels;

public class DetailsActivity extends AppCompatActivity {

    RecyclerView recycleV;
    DetailsAdapter adapterD;

    FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;

    Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable mDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_menu);
        mDrawable.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP));

        //new PorterDuffColorFilter(0xffffff, PorterDuff.Mode.MULTIPLY)

        getSupportActionBar().setHomeAsUpIndicator(mDrawable);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);

        // initialize menu
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.logout).setChecked(false);
        nav_Menu.findItem(R.id.settings).setChecked(false);

        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        if (mAuth.getCurrentUser().getUid().contains("LpPtVsPQWyeOzejQj8uLK49zlCX2")) {
                            Log.i("user","admin");
                            Menu nav_Menu = navigationView.getMenu();
                            nav_Menu.findItem(R.id.algolia).setVisible(true);
                            nav_Menu.findItem(R.id.dbtest).setVisible(true);
                            nav_Menu.findItem(R.id.algolia).setChecked(false);
                            nav_Menu.findItem(R.id.dbtest).setChecked(false);
                        } else {
                            Log.i("user",mAuth.getCurrentUser().getUid());
                        }
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        Menu nav_Menu = navigationView.getMenu();
                        nav_Menu.findItem(R.id.algolia).setChecked(false);
                        nav_Menu.findItem(R.id.dbtest).setChecked(false);
                        nav_Menu.findItem(R.id.logout).setChecked(false);
                        nav_Menu.findItem(R.id.settings).setChecked(false);
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        if (menuItem.getItemId() == R.id.logout) {
                            Log.i("menu","logout selected");
                            mAuth.signOut();
                            final Intent i = new Intent(DetailsActivity.this, LoginActivity.class);
                            startActivity(i);
                            finish();
                            Toast.makeText(getApplicationContext(), "Logged out!", Toast.LENGTH_SHORT).show();
                        }
                        if (menuItem.getItemId() == R.id.algolia) {
                            Intent i = new Intent(getApplicationContext(), AlgoliaActivity.class);
                            startActivity(i);
                        }
                        if (menuItem.getItemId() == R.id.dbtest) {
                            Intent intent = new Intent(getApplicationContext(), DBTestActivity.class);
                            startActivity(intent);
                        }
                        if (menuItem.getItemId() == R.id.settings) {
                            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                            startActivity(intent);
                        }
                        return true;
                    }
                });

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}