package codepath.com.gitreccedproject;

import android.content.Context;
import android.os.AsyncTask;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    DatabaseReference dbItemsByUser;
    DatabaseReference dbUsersbyItem;
    DatabaseReference dbBooks;

    DatabaseReference dbRecItemsByUser;

    GoodreadsClient client = new GoodreadsClient();

    BookClient bClient = new BookClient();

    String uid = "adapter: user id not set yet"; //user id (initialized to dummy string for testing)
    String iid = "adapter: item id not set yet"; //item id (initialized to dummy string for testing)

    Context context;
    public List<Item> mItems;
    public Item mItem;
    public int mPosition;
    public List<Item> userItems = new ArrayList<>();
    public List<Item> mRecs = new ArrayList<>();
    ArrayList<String> lib;

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
    public void onBindViewHolder(@NonNull final SearchAdapter.ViewHolder holder, int position) {
        // get the data according to position
        final Item item = mItems.get(position);
        // populate the views according to position
        holder.title_tv.setText(item.getTitle());
        // check if item is in user's library
        dbItemsByUser = FirebaseDatabase.getInstance().getReference("itemsbyuser").child(InputRecsMoviesActivity.resultUser.getUid());
        com.google.firebase.database.Query itemsquery = null;
        itemsquery = dbItemsByUser;
        itemsquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lib = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    lib.add(postSnapshot.child("iid").getValue().toString());
                }
                if (lib.contains(item.getIid())) {
                    holder.title_tv.setTextSize(20); // TODO - change this later
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
            mPosition = position;
            if (position != RecyclerView.NO_POSITION) {

                // get the item at the position
                mItem = mItems.get(position);

                Log.i("size",String.format("%s",title_tv.getTextSize()));
                if (title_tv.getTextSize() == 60.0) {
                    Log.i("click", "already in lib");
                    dbItemsByUser = FirebaseDatabase.getInstance().getReference("itemsbyuser").child(InputRecsMoviesActivity.resultUser.getUid()).child(mItem.getIid());
                    dbItemsByUser.removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            dbUsersbyItem = FirebaseDatabase.getInstance().getReference("usersbyitem").child(mItem.getIid()).child(InputRecsMoviesActivity.resultUser.getUid());
                            dbUsersbyItem.removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    title_tv.setTextSize(18);
                                }
                            });
                        }
                    });
                } else {
                    getrecs(mItem);
                    title_tv.setTextSize(20);
                    addItem(position);
                }

                Log.d("mItem", "Title: " + mItem.getTitle());
                //set the overview + additional fields for item
                new BookAsync().execute();

                Toast.makeText(context,"Saved!",Toast.LENGTH_SHORT).show();
                Log.i("select", String.format("Got item at %s", position));

            }
        }
    }

    private void bookDecide (final Item item, final FirebaseCallback firebaseCallback) {
        //if item is a book, add to DB
        if(item.getGenre() == "Book") {
            dbBooks = FirebaseDatabase.getInstance().getReference("books");
            // search up book and get description
            //client.getBook(item.getBookId());

            //check if book title already exists in dbBooks
            //TODO: change title to book id
            dbBooks.orderByChild("title").equalTo(item.getTitle()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        for (DataSnapshot snapIid : dataSnapshot.getChildren()) {
                            Item tempItem = snapIid.getValue(Item.class);
                            iid = snapIid.child("iid").getValue().toString();
                            item.setIid(iid);

                            item.setAuthor(tempItem.getAuthor());
                            item.setDetails(tempItem.getDetails());
                            item.setBookId(tempItem.getBookId());
                            item.setImgUrl(tempItem.getImgUrl());
                            item.setSmallImgUrl(tempItem.getSmallImgUrl());
                            item.setTitle(tempItem.getTitle());

                            //weird way, pls fix later
                            mItems.add(mPosition, item);
                            mItems.remove(mPosition + 1);
                        }

                        //TODO: set item to existing item in db
                        Log.i("Books", "this book already exists in the DB");
                    } else {
                        //the title does not exist in dbBooks, create new item id and add to dbBooks
                        //create new item id
                        iid = item.getIid();

                        Log.i("Books", "adding new book to db");

                        //weird way, pls fix later
                        mItems.add(mPosition, item);
                        mItems.remove(mPosition + 1);

                        Log.d("BookDecide", "iid: " + iid + " || title: " + item.getTitle());
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



    abstract class FirebaseCallback {
        public abstract void onCallback(List<Item> recList);
    }

    //adds item to firebase
    private void addItem(final int position) {
        iid = mItems.get(position).getIid();
        Log.i("IidItem", "Iid of Item added to db: " + iid);
        uid = InputRecsMoviesActivity.resultUser.getUid();
        Log.i("ResultUser", "Uid of current user: " + uid);

        dbItemsByUser = FirebaseDatabase.getInstance().getReference("itemsbyuser").child(uid);
        dbUsersbyItem = FirebaseDatabase.getInstance().getReference("usersbyitem").child(iid);

        Log.i("test", "setting dbItemsByUser");
        dbItemsByUser.child(iid).setValue(mItems.get(position));
        dbUsersbyItem.child(uid).setValue(InputRecsMoviesActivity.resultUser);
    }

    class BookAsync extends AsyncTask<Void, Void, Void> {
        GoodreadsClient client;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... voids) {
            client = new GoodreadsClient();
            Log.i("BookId", "BookId Before getBook call: " + mItem.getBookId());
            client.getBook(mItem);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //TODO: here, populate description field of selected book
            //add this item to database
            bookDecide(mItem, new FirebaseCallback() {
                @Override
                public void onCallback(List<Item> someList) {
                    addItem(mPosition);
                    Toast.makeText(context,"Saved!",Toast.LENGTH_SHORT).show();
                    Log.i("select", String.format("Got item at %s", mPosition));
                }
            });
        }
    }


    public void getrecs(final Item item) {
        dbRecItemsByUser = FirebaseDatabase.getInstance().getReference("recitemsbyuser").child(InputRecsMoviesActivity.resultUser.getUid());
        //TODO - if the item is in dbRecItemsByUser, remove it
        // get the id of the item
        String iid = item.getIid();
        // add the item to library
        lib.add(item.getIid());
        Log.i("item_id",iid);
        // query usersbyitem to get the list of users who also like that item
        dbUsersbyItem = FirebaseDatabase.getInstance().getReference("usersbyitem").child(iid);
        com.google.firebase.database.Query usersquery = null;
        usersquery = dbUsersbyItem;
        usersquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                // for each user who likes that item
                for (DataSnapshot itemSnapshot : userSnapshot.getChildren()) {
                    // get the id of the user
                    String uid = itemSnapshot.child("uid").getValue().toString();
                    Log.i("user_id", uid);
                    // query itemsbyuser to get the list of items that user likes
                    dbItemsByUser = FirebaseDatabase.getInstance().getReference("itemsbyuser").child(uid);
                    com.google.firebase.database.Query itemsquery2 = null;
                    itemsquery2 = dbItemsByUser;
                    itemsquery2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // for each item that that user likes
                            for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                //Log.i("postsnapshot", postSnapshot.toString());
                                // don't add if the item is already in the user's library
                                Log.i("lib", lib.toString());
                                boolean inlib = false;
                                /*if (lib.contains(postSnapshot.child("iid").getValue().toString())) {
                                    inlib = true;
                                } else if (postSnapshot.child("iid").getValue().toString() == item.getIid()) {
                                    inlib = true;
                                } else {
                                    inlib = false;
                                }*/
                                for (int i = 0; i < lib.size(); i++) {
                                    if (postSnapshot.child("iid").getValue().toString().contains(lib.get(i)) || lib.get(i).contains(postSnapshot.child("iid").getValue().toString())) {
                                        inlib = true;
                                    }
                                    Log.i("inlib", postSnapshot.child("iid").getValue().toString() + lib.get(i) + inlib);
                                }
                                if (inlib == false) {
                                    // add the item to the recommendations list
                                    Log.i("adding",item.getIid());
                                    com.google.firebase.database.Query countquery = null;
                                    countquery = dbRecItemsByUser.child(postSnapshot.child("genre").getValue().toString()).child(postSnapshot.child("iid").getValue().toString()).child("count");
                                    countquery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dSnapshot) {
                                            Log.i("add", dSnapshot.toString());
                                            if (dSnapshot.getValue() != null) {
                                                dbRecItemsByUser.child(postSnapshot.child("genre").getValue().toString()).child(postSnapshot.child("iid").getValue().toString()).child("count")
                                                        .setValue((long) dSnapshot.getValue() + 1);
                                                //.setValue((long) dSnapshot.child("count").getValue()+1);
                                            } else {
                                                dbRecItemsByUser.child(postSnapshot.child("genre").getValue().toString()).child(postSnapshot.child("iid").getValue().toString()).setValue(postSnapshot.getValue());
                                                dbRecItemsByUser.child(postSnapshot.child("genre").getValue().toString()).child(postSnapshot.child("iid").getValue().toString()).child("count").setValue((long) 1);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            //
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
