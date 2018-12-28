package codepath.com.gitreccedproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.parceler.Parcels;

public class MyLibraryActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DrawerLayout mDrawerLayout;
    private ImageView plus_btn;
    private ProgressBar pbloading;
    private ImageView refresh;
    private ImageView info;
    private TextView tvMenuTitle;

    static boolean isVisitedRecs = false;
    static boolean isVisitedLib = false;

    final int RECS_FRAGMENT = 2;

    public void showProgressBar() {
        // Show progress item
        toolbar = findViewById(R.id.toolbar);
        pbloading = toolbar.findViewById(R.id.pbLoading);
        refresh = toolbar.findViewById(R.id.refresh);
        refresh.setVisibility(View.GONE);
        pbloading.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        // Hide progress item
        toolbar = findViewById(R.id.toolbar);
        pbloading = toolbar.findViewById(R.id.pbLoading);
        refresh = toolbar.findViewById(R.id.refresh);
        pbloading.setVisibility(View.GONE);
        refresh.setVisibility(View.VISIBLE);
    }
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_library);

        mAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable mDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_menu);
        mDrawable.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP));

        //new PorterDuffColorFilter(0xffffff, PorterDuff.Mode.MULTIPLY)

        getSupportActionBar().setHomeAsUpIndicator(mDrawable);

        final User currentUser = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        Log.i("libuser",currentUser.toString());
        Log.i("isNewUser", "isNewUser: " + String.valueOf(getIntent().getBooleanExtra("isNewUser", false)));
        boolean isNewUser = getIntent().getBooleanExtra("isNewUser", false);

        info = toolbar.findViewById(R.id.ivInfo);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create dialog
                final FragmentManager manager = getSupportFragmentManager();
                final DescriptionDialog descriptionDialog = new DescriptionDialog(MyLibraryActivity.this, RECS_FRAGMENT);
                descriptionDialog.show(manager, "This is a test");
            }
        });

        pbloading = toolbar.findViewById(R.id.pbLoading);
        refresh = toolbar.findViewById(R.id.refresh);

        plus_btn = toolbar.findViewById(R.id.plus_btn);
        plus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("plus","clicked!");
                Intent i = new Intent(MyLibraryActivity.this, InputRecsActivity.class);
                i.putExtra("user", Parcels.wrap(currentUser));
                startActivity(i);
            }
        });

        if (isNewUser && !(isVisitedRecs)) {
            //create dialog
            final FragmentManager manager = getSupportFragmentManager();
            final DescriptionDialog descriptionDialog = new DescriptionDialog(MyLibraryActivity.this, RECS_FRAGMENT);
            descriptionDialog.show(manager, "This is a test");
        }

        viewPager = findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

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

                        tvMenuTitle = findViewById(R.id.tvMenuTitle);
                        tvMenuTitle.setText("Welcome, " + currentUser.getUsername() + "!");

                        if (mAuth.getCurrentUser().getUid().contains("IqwmPTlbSZRFgBd6VQNPa0Cd0Aw1")) {
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
                            final Intent i = new Intent(MyLibraryActivity.this, LoginActivity.class);
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

    @Override
    public void onBackPressed(){
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}