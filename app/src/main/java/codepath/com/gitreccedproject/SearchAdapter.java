package codepath.com.gitreccedproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    DatabaseReference dbItemsByUser;
    DatabaseReference dbUsersbyItem;

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
        holder.title_tv.setText(item.title); //TODO - change this later
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
                addItem(position);
                Log.i("select", String.format("Got item at %s", position));
                // TODO - insert into firebase
            }
        }
    }

    private void addItem(int position) {

        iid = mItems.get(position).getIid();
        uid = mItems.get(position).getUser().getUid();

        dbItemsByUser = FirebaseDatabase.getInstance().getReference("itemsbyuser").child(uid);
        dbUsersbyItem = FirebaseDatabase.getInstance().getReference("usersbyitem").child(iid);

        Log.i("test", "setting dbItemsByUser");
        dbItemsByUser.child(iid).setValue(mItems.get(position));
    }
}
