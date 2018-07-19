package codepath.com.gitreccedproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email, password;
    private Button login, signUp;

    private Button btnDbTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        login = findViewById(R.id.btnLogin);
        signUp = findViewById(R.id.btnSignUp);
        btnDbTest = findViewById(R.id.btnDbTest);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String em = email.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (em.isEmpty() || pass.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please complete all fields before Logging In", Toast.LENGTH_SHORT).show();
                } else {
                    callLogIn(em, pass);
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

        btnDbTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, DBTestActivity.class);
                startActivity(intent);
            }

        });
    }

    //Start Sign In process
    private  void callLogIn(String email, String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TESTING", "Sign in successful" + task.isSuccessful());

                        /*
                        If sign in fails, display a message to the user. If sign in succeeds
                        the auth state will be modified and logic to handle the signed in user
                        can be handled in the listener
                         */
                        if (!task.isSuccessful()){
                            Log.v("TESTING", "signInWithEmail : failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent i = new Intent(LoginActivity.this, MyLibraryActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                });
    }
}