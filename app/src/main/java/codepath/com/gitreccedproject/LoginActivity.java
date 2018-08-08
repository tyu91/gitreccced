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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email, password;
    private Button login, signUp;

    static User currentuser;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null){
            Log.i("signin", mAuth.getCurrentUser().getEmail());
            getUserfromdb(mAuth.getCurrentUser().getEmail());
        }

        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        login = findViewById(R.id.btnLogin);
        signUp = findViewById(R.id.btnSignUp);

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
                finish();
            }
        });
    }

    //Start Sign In process
    private  void callLogIn(final String email, String password){

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
                            Log.v("TESTING", task.getException().toString());
                            if (task.getException().toString().contains("The email address is badly formatted")) {
                                Toast.makeText(LoginActivity.this, "Invalid email!", Toast.LENGTH_SHORT).show();
                            } else if (task.getException().toString().contains("There is no user record corresponding to this identifier")) {
                                Toast.makeText(LoginActivity.this, "There is no account associated with this email", Toast.LENGTH_SHORT).show();
                            } else if (task.getException().toString().contains("The password is invalid or the user does not have a password")) {
                                Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            getUserfromdb(email);
                        }
                    }
                });
    }

    private void getUserfromdb(final String email) {
        DatabaseReference usersRef;
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        com.google.firebase.database.Query usersquery = null;
        usersquery = usersRef.orderByChild("email").equalTo(email.toLowerCase());
        Log.i("e",email.toLowerCase());

        usersquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    currentuser = new User(postSnapshot.child("uid").getValue().toString(), postSnapshot.child("username").getValue().toString(), postSnapshot.child("password").getValue().toString(), email, new Item());
                    Log.i("snapshot","!");
                    Toast.makeText(getApplicationContext(), String.format("Welcome, %s!", postSnapshot.child("username").getValue()), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoginActivity.this, MyLibraryActivity.class);
                    intent.putExtra("user", Parcels.wrap(currentuser));
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("snapshot", "loadPost:onCancelled");
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}