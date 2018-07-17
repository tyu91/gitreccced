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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main";

    private FirebaseAuth mAuth;
    private EditText email, password, name;
    private Button login, signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Check if user is already logged in
        if (mAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), InputRecsActivity.class));
        }

        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.etName);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        login = findViewById(R.id.btnLogin);
        signUp = findViewById(R.id.btnSignUp);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String em = email.getText().toString().trim();
                String pass = password.getText().toString().trim();

                callLogIn(em, pass);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String em = email.getText().toString().trim();
                String pass = password.getText().toString().trim();

                callSignUp(em, pass);
            }
        });
    }

    //Create Account
    private void callSignUp(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TESTING", "Sign Up Successful" + task.isSuccessful());
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Sign up Failed", Toast.LENGTH_SHORT).show();
                        } else {
                            userProfile();
                            Toast.makeText(MainActivity.this, "Account created", Toast.LENGTH_LONG).show();
                            Log.d("TESTING", "Created account");
                        }
                    }
                });
    }

    private void userProfile(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name.getText().toString().trim())
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.d("TESTING", "User profile updated");
                            }
                        }
                    });
        }
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
                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent i = new Intent(MainActivity.this, InputRecsActivity.class);
                            finish();
                            startActivity(i);
                        }
                    }
                });
    }
}