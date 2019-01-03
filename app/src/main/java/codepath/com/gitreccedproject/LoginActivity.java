package codepath.com.gitreccedproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
    private Snackbar snackbar;
    private String snackbarString;
    CoordinatorLayout loginLayout;

    boolean isNewUser = false;

    static User currentUser;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginLayout = findViewById(R.id.loginLayout);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null){
            Log.i("signin", mAuth.getCurrentUser().getEmail());
            getUserFromDb(mAuth.getCurrentUser().getEmail().trim());
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
                    snackbarString = "Please complete all fields before logging in!";
                    snackbar = Snackbar.make(loginLayout, snackbarString, Snackbar.LENGTH_LONG);
                    snackbar.show();
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

                        /*If sign in fails, display a message to the user. If sign in succeeds
                        the auth state will be modified and logic to handle the signed in user
                        can be handled in the listener
                         */
                        if (!task.isSuccessful()){
                            if (task.getException().toString().contains("The email address is badly formatted")) {
                                snackbarString = "Invalid Email! Please try again.";
                                snackbar = Snackbar.make(loginLayout, snackbarString, Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            } else if (task.getException().toString().contains("There is no user record corresponding to this identifier")) {
                                snackbarString = "There is no account associated with this email!";
                                snackbar = Snackbar.make(loginLayout, snackbarString, Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            } else if (task.getException().toString().contains("The password is invalid or the user does not have a password")) {
                                snackbarString = "Password Incorrect! Please try again.";
                                snackbar = Snackbar.make(loginLayout, snackbarString, Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            } else {
                                snackbarString = "Login Unsuccessful! Please try again.";
                                snackbar = Snackbar.make(loginLayout, snackbarString, Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                        } else {
                            getUserFromDb(email);
                        }
                    }
                });
    }

    private void getUserFromDb(final String email) {
        DatabaseReference usersRef;
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        com.google.firebase.database.Query usersquery = null;
        usersquery = usersRef.orderByChild("email").equalTo(email.toLowerCase());

        usersquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    currentUser = new User(postSnapshot.child("uid").getValue().toString(), postSnapshot.child("username").getValue().toString(), postSnapshot.child("password").getValue().toString(), email);
                    snackbarString = String.format("Welcome, %s!", postSnapshot.child("username").getValue());
                    snackbar = Snackbar.make(loginLayout, snackbarString, Snackbar.LENGTH_SHORT);
                    snackbar.show();

                    Intent intent = new Intent(LoginActivity.this, MyLibraryActivity.class);
                    intent.putExtra("user", Parcels.wrap(currentUser));
                    intent.putExtra("isNewUser", isNewUser);
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