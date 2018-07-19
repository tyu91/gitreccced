package codepath.com.gitreccedproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;

public class DBTest2Activity extends AppCompatActivity {

    DatabaseReference dbItemsByUser;
    DatabaseReference dbUsersbyItem;
    EditText etGenre;
    EditText etTitle;
    Button btnSubmitItem;

    String uid = "2: user id not set yet"; //user id (initialized to dummy string for testing)
    String iid = "2: item id not set yet"; //item id (initialized to dummy string for testing)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbtest2);

        /*Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        //reference to items field of json array in database
        dbItemsByUser = FirebaseDatabase.getInstance().getReference("itemsbyuser").child(uid);
        //dbUsersbyItem = FirebaseDatabase.getInstance().getReference("usersbyitem").child(iid);
        dbItemsByUser.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //get snapshot of item added under user in itemsbyuser
                Item item = dataSnapshot.getValue(Item.class);

                //generate user from item
                User user = new User(item.getUser(), getIntent().getStringExtra("username"), getIntent().getStringExtra("password"), item.getIid());

                iid = item.getIid();

                dbUsersbyItem = FirebaseDatabase.getInstance().getReference("usersbyitem").child(iid);

                //add user to usersbyitem
                dbUsersbyItem
                        .setValue(user);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        etGenre = findViewById(R.id.etGenre);
        etTitle = findViewById(R.id.etTitle);
        btnSubmitItem = findViewById(R.id.btnSubmitItem);

        //set click listener to submit item info to realtime db
        btnSubmitItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add new item to items field
                addItem();
                //finish();
            }
        });*/
    }

    private void addItem(){
        String genre = etGenre.getText().toString();
        String title = etTitle.getText().toString();

        if(!TextUtils.isEmpty(title)){
            iid = dbItemsByUser.push().getKey();

            //new item to add
            //Item newItem = new Item(iid, genre, title, "dummy details string", uid);

            //add item to db
            //dbItemsByUser.child(iid).setValue(newItem);

            /*//add iid to return intent
            Intent resultIntent = new Intent();
            resultIntent.putExtra("iid", iid);
            setResult(DBTestActivity.DB_TEST_REQUEST_CODE, resultIntent);
*/
        }
    }
}