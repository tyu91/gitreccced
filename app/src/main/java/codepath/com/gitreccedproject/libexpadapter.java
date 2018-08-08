package codepath.com.gitreccedproject;

import android.content.Context;
import android.content.Intent;
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

import org.parceler.Parcels;

import java.util.List;

public class libexpadapter extends RecyclerView.Adapter<libexpadapter.ViewHolder> {

    Context context;
    public List<Item> mItems;

    //config required for img urls
    Config config;

    public libexpadapter(List<Item> items) {
        mItems = items;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    @NonNull
    @Override
    public libexpadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View searchView = inflater.inflate(R.layout.item, parent, false);
        libexpadapter.ViewHolder viewHolder = new libexpadapter.ViewHolder(searchView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull libexpadapter.ViewHolder holder, int position) {
        // get the data according to position
        Item item = mItems.get(position);
        // populate the views according to position
        //holder.textview1.setText(item.title);


        String imageUrl = "https://image.tmdb.org/t/p/w342" + item.getPosterPath();

        if (item.getGenre().equalsIgnoreCase("Book")){
            Glide.with(context)
                    .load(item.getSmallImgUrl())
                    .into(holder.posterImage);
            holder.textview1.setText(item.getTitle());

        } else {
            // populate the views according to position
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.posterImage);
            holder.textview1.setText(item.title);
        }

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textview1;
        CardView cardview;
        ImageView posterImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textview1 = itemView.findViewById(R.id.textview1);
            cardview = itemView.findViewById(R.id.cardview);
            posterImage = itemView.findViewById(R.id.ivPosterImage);
            cardview.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Toast.makeText(context, String.format("Clicked %s!", position), Toast.LENGTH_SHORT).show();

            final Intent i = new Intent(context, DetailsActivity.class);
            i.putExtra("item", Parcels.wrap(mItems.get(position)));
            context.startActivity(i);
        }
    }
}

