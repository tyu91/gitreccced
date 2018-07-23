package codepath.com.gitreccedproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class libAdapter extends Adapter<libAdapter.ViewHolder> {

    Context context;
    public List<Item> mItems;

    public libAdapter(List<Item> items) {
        mItems = items;
    }

    @NonNull
    @Override
    public libAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View searchView = inflater.inflate(R.layout.item, parent, false);
        libAdapter.ViewHolder viewHolder = new libAdapter.ViewHolder(searchView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull libAdapter.ViewHolder holder, int position) {
        // get the data according to position
        Item item = mItems.get(position % mItems.size());
        // populate the views according to position
        holder.textview1.setText(item.title);
    }

    @Override
    public int getItemCount() {
        //return mItems.size();
        return Integer.MAX_VALUE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textview1;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textview1 = itemView.findViewById(R.id.textview1);
        }
    }
}
