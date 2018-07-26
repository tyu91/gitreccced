package codepath.com.gitreccedproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    DatabaseReference dbItemsByUser;
    DatabaseReference dbUsersbyItem;
    DatabaseReference dbBooks;

    DatabaseReference dbRecItemsByUser;

    BookClient bClient = new BookClient();

    String uid = "adapter: user id not set yet"; //user id (initialized to dummy string for testing)
    String iid = "adapter: item id not set yet"; //item id (initialized to dummy string for testing)


    Context context;
    public List<Item> mItems;
    public List<Item> userItems = new ArrayList<>();
    public List<Item> mRecs = new ArrayList<>();
    //TODO: in here, populate with recsByUser field (to be created) in DB
    public static List<Item> finalRecs = null;
    public static List<Item> finalMovieRecs = null;
    public static List<Item> finalTVRecs = null;
    public static List<Item> finalBookRecs = null;

    public SearchAdapter(List<Item> items) {
        mItems = items;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View searchView = inflater.inflate(R.layout.item_search, parent, false);
        ViewHolder viewHolder = new ViewHolder(searchView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        // get the data according to position
        Item item = mItems.get(position);
        // populate the views according to position
        holder.title_tv.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title_tv;
        public RelativeLayout rlayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title_tv = itemView.findViewById(R.id.title_tv);
            rlayout = itemView.findViewById(R.id.rlayout);

            rlayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {

                // get the item at the position
                final Item item = mItems.get(position);

                bookDecide(item, new FirebaseCallback() {
                    @Override
                    public void onCallback(List<Item> someList) {
                        addItem(position);
                        Toast.makeText(context,"Saved!",Toast.LENGTH_SHORT).show();
                        Log.i("select", String.format("Got item at %s", position));
                    }
                });

                dbItemsByUser = FirebaseDatabase.getInstance().getReference("itemsbyuser").child(uid);
                dbItemsByUser.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        //array list of items that will be pared down eventually
                        mRecs = new ArrayList<>();

                        //get snapshot of item added under user in itemsbyuser
                        //***NOTE: for some reason, iterates through every item under a user. Look into this later.
                        final Item item = dataSnapshot.getValue(Item.class);
                        userItems.add(item);

                        //TODO: get user not from movie recs activity?
                        //TODO: get user from signin/signup activity
                        //generate user
                        User user = InputRecsMoviesActivity.resultUser;

                        iid = item.getIid();

                        dbUsersbyItem = FirebaseDatabase.getInstance().getReference("usersbyitem").child(iid);

                        //add user to usersbyitem
                        dbUsersbyItem.child(uid).setValue(user);

                        readData(new FirebaseCallback() {
                            @Override
                            public void onCallback(List<Item> recList) {
                                //prints out array list of recommendations
                                for(int i = 0; i < recList.size(); i++) {
                                    Item recItem = recList.get(i);
                                    Log.i("RecAlgo", "Rec " + i + ": " + recItem.getTitle());
                                }

                                ArrayList<String> recIids = new ArrayList<>();

                                //convert list of recommendations to list of their IDs
                                for(int i = 0; i < recList.size(); i++) {
                                    String recId = recList.get(i).getIid();
                                    recIids.add(recId);
                                    Log.i("RecAlgo", "Rec " + i + ": " + recList.get(i).getTitle());
                                }

                                //maps recItems to number of appearances
                                final HashMap<String, Integer> recMap = new HashMap<>();

                                for(int i = 0; i < recIids.size(); i++) {
                                    if (recMap.containsKey(recIids.get(i))) {
                                        recMap.put(recIids.get(i), recMap.get(recIids.get(i)) + 1);
                                    } else {
                                        recMap.put(recIids.get(i), 1);
                                    }
                                }

                                //prints out hashmap recMap's items and frequency
                                for(String recId : recMap.keySet()) {
                                    Log.i("RecAlgo", "NoDupesRec: " + recId + ", Num Results: " + recMap.get(recId));
                                }

                                //sorts recItems based on number of appearances
                                List<String> toRecommendIids = new ArrayList<String>(recMap.keySet());

                                Collections.sort(toRecommendIids, new Comparator<String>() {
                                    @Override
                                    public int compare(String s1, String s2) {
                                        return recMap.get(s2).compareTo(recMap.get(s1));
                                    }
                                });

                                //prints out sorted toRecommendIids
                                for(String key : toRecommendIids) {
                                    Log.i("RecAlgo", "FinalRec: " + key + ", Num Results: " + recMap.get(key));
                                }

                                //removes dupes from recList (slow method)
                                for (int i = 0; i < recList.size() - 1; i++) {
                                    for (int j = i + 1; j < recList.size(); j++) {
                                        if(recList.get(i).getIid().equals(recList.get(j).getIid())) {
                                            recList.remove(j);
                                            j--;
                                        }
                                    }
                                }

                                //print out recList w/o dupes
                                for(Item recItem : recList) {
                                    Log.i("RecAlgo", "RecListNoDupes: " + recItem.getTitle());
                                }

                                if(finalRecs != null) {
                                    finalRecs.clear();
                                } else {
                                    finalRecs = new ArrayList<>();
                                }
                                //recreate items list from iid toRecommendIids list:
                                    //does this by checking iid from toRecommendIids with iid's of items in recList
                                for (int i = 0; i < toRecommendIids.size(); i++) {
                                    String currentIid = toRecommendIids.get(i);
                                    for (int j = 0; j < recList.size(); j++) {
                                        if(currentIid.equals(recList.get(j).getIid())) {
                                            finalRecs.add(recList.get(j));
                                        }
                                    }
                                }

                                //print out final recommendations (but including items currently in library)
                                for (int i = 0; i < finalRecs.size(); i++) {
                                    Log.i("RecAlgo", "FinalItemsRec " + i + ": " + finalRecs.get(i).getTitle());
                                }

                                //removes items associated with current user from recommendations
                                for (int i = 0; i < finalRecs.size(); i++) {
                                    String currentIid = finalRecs.get(i).getIid();
                                    for (int j = 0; j < userItems.size(); j++) {
                                        if (currentIid.equals(userItems.get(j).getIid())) {
                                            finalRecs.remove(i);
                                            i--;
                                        }
                                    }
                                }

                                //print out final recommendations (but including items currently in library)
                                for (int i = 0; i < finalRecs.size(); i++) {
                                    Log.i("RecAlgo", "ActuallyFinalRecItems " + i + ": " + finalRecs.get(i).getTitle());
                                }

                                //sorts items based on genre
                                for(int i = 0; i < finalRecs.size(); i++){
                                    Item item = finalRecs.get(i);
                                    if (item.getGenre().equals("Movie")) {
                                        finalMovieRecs.add(item);
                                    } else if (item.getGenre().equals("TV")) {
                                        finalTVRecs.add(item);
                                    } else {
                                        finalBookRecs.add(item);
                                    }
                                }

                                //print out final recommendations (but including items currently in library)
                                for (int i = 0; i < finalMovieRecs.size(); i++) {
                                    Log.i("RecAlgo", "FinalMovieRecItems " + i + ": " + finalMovieRecs.get(i).getTitle());
                                }

                                //print out final recommendations (but including items currently in library)
                                for (int i = 0; i < finalTVRecs.size(); i++) {
                                    Log.i("RecAlgo", "FinalTVRecItems " + i + ": " + finalTVRecs.get(i).getTitle());
                                }

                                //print out final recommendations (but including items currently in library)
                                for (int i = 0; i < finalBookRecs.size(); i++) {
                                    Log.i("RecAlgo", "FinalBookRecItems " + i + ": " + finalBookRecs.get(i).getTitle());
                                }

                            }
                        });
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
                        Log.i("childeventlistener", "cancelled");
                    }
                });
            }
        }
    }

    private void bookDecide (final Item item, final FirebaseCallback firebaseCallback) {
        //if item is a book, add to DB
        if(item.getGenre() == "Book") {
            dbBooks = FirebaseDatabase.getInstance().getReference("books");

            //check if book title already exists in dbBooks
            dbBooks.orderByChild("title").equalTo(item.getTitle()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        //if there exists title in dbBooks, do nothing
                                /*for(DataSnapshot bookItems : dataSnapshot.getChildren()){
                                    iid = bookItems.getValue(Item.class).getIid();
                                    dbBooks.child(iid).setValue(item);
                                }*/
                        Log.i("Books", "this book already exists in the DB");
                    } else {
                        //the title does not exist in dbBooks, create new item id and add to dbBooks
                        //create new item id
                        iid = dbBooks.push().getKey();
                        //TODO: if code doesn't work, set mItems.get(position).setIid(iid)
                        dbBooks.child(iid).setValue(item);
                        Log.i("Books", "Added " + item.getTitle());
                    }

                    List<Item> dummyItems = new ArrayList<>();

                    firebaseCallback.onCallback(dummyItems);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    //reads data from firebase in order to generate recs
    private void readData (final FirebaseCallback firebaseCallback) {
        //set up value event listener for users associated with each item in current user's library
        ValueEventListener valueEventListenerOne = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    User mUser = userSnapshot.getValue(User.class);
                    Log.i("Users", "User ID: " + mUser.getUid());

                    //sets up reference to each salient user under userbyitems
                    dbRecItemsByUser = FirebaseDatabase.getInstance().getReference("itemsbyuser").child(mUser.getUid());

                    //set up value event listener for each item under each user linked to the items in current user's library
                    ValueEventListener valueEventListenerTwo = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                                Item userItem = itemSnapshot.getValue(Item.class);
                                mRecs.add(userItem);
                                //Log.i("RecAlgo", "Rec Item: " + userItem.getTitle());
                            }

                            firebaseCallback.onCallback(mRecs);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };

                    dbRecItemsByUser.addListenerForSingleValueEvent(valueEventListenerTwo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        dbUsersbyItem.addListenerForSingleValueEvent(valueEventListenerOne);
    }

    abstract class FirebaseCallback {
        public abstract void onCallback(List<Item> recList);
    }

    //adds item to firebase
    private void addItem(final int position) {
        iid = mItems.get(position).getIid();
        uid = InputRecsMoviesActivity.resultUser.getUid();

        dbItemsByUser = FirebaseDatabase.getInstance().getReference("itemsbyuser").child(uid);
        dbUsersbyItem = FirebaseDatabase.getInstance().getReference("usersbyitem").child(iid);

        Log.i("test", "setting dbItemsByUser");
        dbItemsByUser.child(iid).setValue(mItems.get(position));
    }
}
