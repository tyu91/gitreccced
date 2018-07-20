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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email, password, name;
    private Button createAccount;

    DatabaseReference dbUsers;

    //static User user;

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
        createAccount = findViewById(R.id.btnSignUp);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String em = email.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (em.isEmpty() || pass.isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Please complete all fields before Signing up", Toast.LENGTH_SHORT).show();
                } else {
                    callSignUp(em, pass);
                }
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

                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Sign up Failed", Toast.LENGTH_SHORT).show();
                        } else {
                            //userProfile();
                            Toast.makeText(SignUpActivity.this, "Account created", Toast.LENGTH_LONG).show();
                            Log.d("TESTING", "Created account");
                            addUser(user);
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

    //adds user to db
    private void addUser(FirebaseUser user){
        String mUsername = name.getText().toString();
        String mPassword = password.getText().toString().trim();
        String mEmail = email.getText().toString();
        String uid = user.getUid();

        if(!TextUtils.isEmpty(mUsername)){

            //create new user with dummy uid since current user is actually previous user (fix later)
            User newUser = new User(uid, mUsername, mPassword, mEmail, new Item ());
            dbUsers.child(uid).setValue(newUser);
            newUser.setUid(uid);

            //dbUsers.child(uid).setValue(newUser);

            //pass userid to test recommendations page
            Intent intent = new Intent(SignUpActivity.this, InputRecsMoviesActivity.class);
            intent.putExtra("user", Parcels.wrap(newUser));
            startActivity(intent);

        }
    }
}
