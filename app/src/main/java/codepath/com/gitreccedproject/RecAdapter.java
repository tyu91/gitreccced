package codepath.com.gitreccedproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecAdapter extends RecyclerView.Adapter<RecAdapter.ViewHolder> {

    Context context;
    public List<Item> mItems;

    public RecAdapter(List<Item> items) {
        mItems = items;
    }

    @NonNull
    @Override
    public RecAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View searchView = inflater.inflate(R.layout.item, parent, false);
        RecAdapter.ViewHolder viewHolder = new RecAdapter.ViewHolder(searchView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecAdapter.ViewHolder holder, int position) {
        // get the data according to position
        Item item = mItems.get(position % mItems.size());

        String imageUrl = "https://image.tmdb.org/t/p/w342" + item.getPosterPath();

        // populate the views according to position
        Glide.with(context)
                .load(imageUrl)
                //.transform(new RoundedCornersTransformation(15, 0))
                .into(holder.posterImage);
        holder.textview1.setText(item.title);
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //TODO: rename textview1 to something more appealing
        TextView textview1;
        CardView cardview;
        ImageView posterImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textview1 = itemView.findViewById(R.id.textview1);
            posterImage = itemView.findViewById(R.id.ivPosterImage);
            cardview = itemView.findViewById(R.id.cardview);
            cardview.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition() % mItems.size();
            Toast.makeText(context, String.format("Clicked %s!", position), Toast.LENGTH_SHORT).show();
        }
    }
}