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

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    DatabaseReference dbItemsByUser;
    DatabaseReference dbUsersbyItem;
    DatabaseReference dbBooks;

    BookClient bClient = new BookClient();

    String uid = "adapter: user id not set yet"; //user id (initialized to dummy string for testing)
    String iid = "adapter: item id not set yet"; //item id (initialized to dummy string for testing)


    Context context;
    public List<Item> mItems;

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
                //if item is a book, add to DB
                if(item.getGenre() == "Book") {
                    dbBooks = FirebaseDatabase.getInstance().getReference("books");
                    //create new item id
                    iid = dbBooks.push().getKey();
                    dbBooks.child(iid).setValue(item);
                }
                //TODO: if item exists in DB, do not re-add book!
                addItem(position);
                Toast.makeText(context,"Saved!",Toast.LENGTH_SHORT).show();
                Log.i("select", String.format("Got item at %s", position));


                dbItemsByUser = FirebaseDatabase.getInstance().getReference("itemsbyuser").child(uid);
                dbItemsByUser.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        //get snapshot of item added under user in itemsbyuser
                        Item item = dataSnapshot.getValue(Item.class);

                        //generate user
                        User user = InputRecsMoviesActivity.resultUser;

                        iid = item.getIid();
                        uid = user.getUid();

                        dbUsersbyItem = FirebaseDatabase.getInstance().getReference("usersbyitem").child(iid);

                        //add user to usersbyitem
                        dbUsersbyItem.child(uid)
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
                        Log.i("childeventlistener", "cancelled");
                    }
                });
            }
        }
    }

    private void addItem(int position) {

        iid = mItems.get(position).getIid();
        uid = InputRecsMoviesActivity.resultUser.getUid();

        dbItemsByUser = FirebaseDatabase.getInstance().getReference("itemsbyuser").child(uid);
        dbUsersbyItem = FirebaseDatabase.getInstance().getReference("usersbyitem").child(iid);

        Log.i("test", "setting dbItemsByUser");
        dbItemsByUser.child(iid).setValue(mItems.get(position));
    }
}
