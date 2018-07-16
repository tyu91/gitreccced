package codepath.com.gitreccedproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText username, password;
    Button login, signUp;

    DatabaseReference dbUsers;
    EditText enterUsername;
    EditText enterPassword;
    Button btnSubmit;

    EditText etGenre;
    EditText etTitle;
    Button btnSubmitItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.etUsername);
        password = findViewById(R.id.etPassword);
        login = findViewById(R.id.btnLogin);
        signUp = findViewById(R.id.btnSignUp);


        dbUsers = FirebaseDatabase.getInstance().getReference("users");
        enterUsername = findViewById(R.id.etSubmitUsername);
        enterPassword = findViewById(R.id.etSubmitPassword);
        btnSubmit = findViewById(R.id.btnSubmit);
        
        etGenre = findViewById(R.id.etGenre);
        etTitle = findViewById(R.id.etTitle);
        btnSubmitItem = findViewById(R.id.btnSubmitItem);

        //set click listener to submit user info to realtime db
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser();
            }
        });

        //set click listener to submit item info to realtime db
        btnSubmitItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void addUser(){
        String username = enterUsername.getText().toString();
        String password = enterPassword.getText().toString();

        if(!TextUtils.isEmpty(username)){
            String id = dbUsers.push().getKey();

            User newUser = new User(id, username, password, "dummy item id");

            dbUsers.child("users").push().setValue(newUser);

        }
    }

    private void addItem(){
        String genre = etGenre.getText().toString();
        String title = etTitle.getText().toString();

        if(!TextUtils.isEmpty(title)){
            String id = dbUsers.push().getKey();

            Item newItem = new Item(id, genre, title, "dummy details string", "dummy users string");

            dbUsers.child("items").push().setValue(newItem);

        }
    }

}