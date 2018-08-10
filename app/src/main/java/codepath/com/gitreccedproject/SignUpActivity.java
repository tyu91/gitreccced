package codepath.com.gitreccedproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email, password, name;
    private TextView logIn;
    private Button createAccount;

    DatabaseReference dbUsers;

    boolean isNewUser = true;

    static User newUser;

    String value;

    //String uid = "su: user id not set yet"; //user id (initialized to dummy string for testing)
    String iid = "su: item id not set yet"; //item id (initialized to dummy string for testing)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        dbUsers = FirebaseDatabase.getInstance().getReference("users");

        name = findViewById(R.id.etUID);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPass);
        logIn = findViewById(R.id.tvLogin);
        createAccount = findViewById(R.id.btnSignUp);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String em = email.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (em.isEmpty() || pass.isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Please complete all fields before Signing up", Toast.LENGTH_SHORT).show();
                } else {
                    callSignUp(em, pass, name.getText().toString());

                }
            }
        });

    }

    //Create Account
    private void callSignUp(String email, String password, final String name){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TESTING", "Sign Up Successful" + task.isSuccessful());
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (!task.isSuccessful()) {
                            Log.i("task",task.getException().toString());
                            if (task.getException().toString().contains("The email address is badly formatted")) {
                                Toast.makeText(SignUpActivity.this, "Invalid email!", Toast.LENGTH_SHORT).show();
                            } else if (task.getException().toString().contains("The given password is invalid")) {
                                Toast.makeText(SignUpActivity.this, "Password too short!", Toast.LENGTH_SHORT).show();
                            } else if (task.getException().toString().contains("The email address is already in use by another account")) {
                                Toast.makeText(SignUpActivity.this, "Email address already associated with an account!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //userProfile();
                            Toast.makeText(SignUpActivity.this, String.format("Welcome, %s!", name), Toast.LENGTH_LONG).show();
                            Log.d("TESTING", "Created account");
                            addUser(user);
                        }
                    }
                });
    }

    //adds user to db
    private void addUser(FirebaseUser user){
        String mUsername = name.getText().toString();
        String mPassword = password.getText().toString().trim();
        String mEmail = email.getText().toString().toLowerCase();
        String uid = user.getUid();

        if(!TextUtils.isEmpty(mUsername)){

            //create new user with dummy uid since current user is actually previous user (fix later)
            newUser = new User(uid, mUsername, mPassword, mEmail, new Item ());
            dbUsers.child(uid).setValue(newUser);
            newUser.setUid(uid);
            LoginActivity.currentuser = newUser;

            MyLibraryActivity.isVisitedRecs = false;
            MyLibraryActivity.isVisitedLib = false;

            //pass userid to test recommendations page
            Intent intent = new Intent(SignUpActivity.this, InputRecsActivity.class);
            intent.putExtra("user", Parcels.wrap(newUser));
            intent.putExtra("isNewUser", isNewUser);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}