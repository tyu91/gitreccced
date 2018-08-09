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

public class SettingsActivity extends AppCompatActivity {

    ImageView ivProfileImage;
    TextView tvUsername;
    TextView tvEmail;
    Button btnUpdate;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //inflate views
        ivProfileImage = findViewById(R.id.ivProfile);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        btnUpdate = findViewById(R.id.btnUpdate);

        user = FirebaseAuth.getInstance().getCurrentUser();
        User dbuser = LoginActivity.currentuser;

        tvEmail.setText(dbuser.getEmail());
        tvUsername.setText(dbuser.getUsername());

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: getFragmentManager() is intended cmd but is deprecated
                FragmentManager manager = getSupportFragmentManager();
                UpdateInfoDialog updateInfoDialog = new UpdateInfoDialog(SettingsActivity.this, 0);
                updateInfoDialog.show(manager, "This is a test");
            }
        });
    }
}
