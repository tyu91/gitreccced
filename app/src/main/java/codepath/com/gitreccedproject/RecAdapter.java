package codepath.com.gitreccedproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.util.List;

public class RecAdapter extends RecyclerView.Adapter<RecAdapter.ViewHolder> {

    private Context context;
    private Item item;
    public List<Item> mItems;

    public RecAdapter(List<Item> items) {
        mItems = items;
    }

    @NonNull
    @Override
    public RecAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View recView = inflater.inflate(R.layout.item, parent, false);
        RecAdapter.ViewHolder viewHolder = new RecAdapter.ViewHolder(recView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecAdapter.ViewHolder holder, int position) {
        // get the data according to position
        item = mItems.get(position % mItems.size());

        String imageUrl = "https://image.tmdb.org/t/p/w342" + item.getPosterPath();

        if (item.getGenre().equalsIgnoreCase("Book")) {
            if (item.getImgUrl() != null && !(item.getImgUrl().equalsIgnoreCase(""))) {
                if (item.getImgUrl().contains("nophoto")) {
                    Log.i("BookImageRecsNoPhoto", "Title: " + item.getTitle() + " || ImgUrl: " + item.getImgUrl());
                    //if book does not have photo associated with it, put title instead
                    holder.posterImage.setImageResource(0);
                    holder.posterImage.setBackgroundColor(Color.parseColor("#000000"));
                    holder.textview1.setVisibility(View.VISIBLE);
                    holder.textview1.bringToFront();
                    holder.textview1.setText(item.getTitle());
                } else {
                    Log.i("BookImageRecsPhoto", "Title: " + item.getTitle() + " || ImgUrl: " + item.getImgUrl());

                    holder.textview1.setVisibility(View.INVISIBLE);

                    Glide.with(context)
                            .load(item.getImgUrl())
                            .into(holder.posterImage);
                    holder.textview1.setText(item.getTitle());
                }

            } else {
                Log.i("BookImageRecs", "no image url");

            }
        } else {
            // populate the views according to position
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.posterImage);
            holder.textview1.setVisibility(View.INVISIBLE);
            holder.textview1.setText(item.title);
        }

        Log.i("BookImageRecs", "FINISHED");
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
//            Toast.makeText(context, String.format("Clicked %s!", position), Toast.LENGTH_SHORT).show();

            final Intent i = new Intent(context, DetailsActivity.class);
            i.putExtra("item", Parcels.wrap(mItems.get(position)));
            context.startActivity(i);

            //final Intent i = new Intent(MyLibraryActivity.this, DetailsActivity.class);
            //context.startActivity(new Intent(context, DetailsActivity.class));
        }
    }
}