package codepath.com.gitreccedproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    Button login, signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.etUsername);
        password = findViewById(R.id.etPassword);
        login = findViewById(R.id.btnLogin);
        signUp = findViewById(R.id.btnSignUp);
    }
}