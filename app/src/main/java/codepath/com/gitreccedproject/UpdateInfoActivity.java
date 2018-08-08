package codepath.com.gitreccedproject;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateInfoActivity extends AppCompatActivity {

    ImageView ivProfileImage;
    TextView tvUsername;
    TextView tvEmail;
    TextView tvPassword;
    Button btnUpdateUsername;
    Button btnUpdateEmail;
    Button btnUpdatePassword;

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        tvUsername.setText(user.getDisplayName());
        tvEmail.setText(user.getEmail());

        btnUpdateUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                UpdateInfoDialog updateInfoDialog = new UpdateInfoDialog(UpdateInfoActivity.this, 1);
                updateInfoDialog.show(manager, "This is a test");
            }
        });

        btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getSupportFragmentManager();
                UpdateInfoDialog updateInfoDialog = new UpdateInfoDialog(UpdateInfoActivity.this, 2);
                updateInfoDialog.show(manager, "This is a test");
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
    }
}
