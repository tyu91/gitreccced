package codepath.com.gitreccedproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class UpdateInfoActivity extends AppCompatActivity {

    ImageView ivProfileImage;
    TextView tvUsername;
    TextView tvEmail;
    TextView tvPassword;
    Button btnUpdateUsername;
    Button btnUpdateEmail;
    Button btnUpdatePassword;
    Button btn_done;

    FirebaseAuth mAuth;
    UpdateInfoDialog updateInfoDialog;
    User currentUser;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

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
                            final Intent i = new Intent(UpdateInfoActivity.this, LoginActivity.class);
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

        //set views
        ivProfileImage = findViewById(R.id.ivProfile);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvPassword = findViewById(R.id.tvPassword);
        btnUpdateUsername = findViewById(R.id.btnUpdateUsername);
        btnUpdateEmail = findViewById(R.id.btnUpdateEmail);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        btn_done = findViewById(R.id.btn_done);

        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUser = LoginActivity.currentUser;

        tvUsername.setText(currentUser.getUsername());
        tvEmail.setText(currentUser.getEmail());

        btnUpdateUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                UpdateInfoDialog updateInfoDialog = new UpdateInfoDialog(UpdateInfoActivity.this, 1);
                updateInfoDialog.show(manager, "This is a test");

                updateInfoDialog.setDialogResult(new UpdateInfoDialog.OnMyDialogResult(){
                    public void finish(String result){
                        tvUsername.setText(result);
                    }
                });
            }
        });

        btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                UpdateInfoDialog updateInfoDialog = new UpdateInfoDialog(UpdateInfoActivity.this, 2);
                updateInfoDialog.show(manager, "This is a test");

                updateInfoDialog.setDialogResult(new UpdateInfoDialog.OnMyDialogResult(){
                    public void finish(String result){
                        tvEmail.setText(result);
                    }
                });
            }
        });

        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                UpdateInfoDialog updateInfoDialog = new UpdateInfoDialog(UpdateInfoActivity.this, 3);
                updateInfoDialog.show(manager, "This is a test");
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    /*@Override
    public void onBackPressed(){
        Intent i = new Intent(UpdateInfoActivity.this, RecsFragment.class);
        i.putExtra("user", Parcels.wrap(user));
        startActivity(i);
        finish();
    }*/

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
