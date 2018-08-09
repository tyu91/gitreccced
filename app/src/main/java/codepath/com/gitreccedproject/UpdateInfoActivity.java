package codepath.com.gitreccedproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.parceler.Parcels;

public class UpdateInfoActivity extends AppCompatActivity {

    ImageView ivProfileImage;
    TextView tvUsername;
    TextView tvEmail;
    TextView tvPassword;
    Button btnUpdateUsername;
    Button btnUpdateEmail;
    Button btnUpdatePassword;
    Button btn_done;

    UpdateInfoDialog updateInfoDialog;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

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
        user = LoginActivity.currentuser;

        tvUsername.setText(user.getUsername());
        tvEmail.setText(user.getEmail());

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
                Intent i = new Intent(UpdateInfoActivity.this, RecsFragment.class);
                i.putExtra("user", Parcels.wrap(user));
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(UpdateInfoActivity.this, RecsFragment.class);
        i.putExtra("user", Parcels.wrap(user));
        startActivity(i);
        finish();
    }
}
