package codepath.com.gitreccedproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

import java.util.List;

public class libAdapter extends Adapter<libAdapter.ViewHolder> {

    Context context;
    public List<Item> mItems;

    DatabaseReference dbItemsByUser;
    DatabaseReference dbUsersbyItem;

    //config required for img urls
    Config config;

    public libAdapter(List<Item> items) {
        mItems = items;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    @NonNull
    @Override
    public libAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View libView = inflater.inflate(R.layout.item, parent, false);
        libAdapter.ViewHolder viewHolder = new libAdapter.ViewHolder(libView);



        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull libAdapter.ViewHolder holder, int position) {
        // get the data according to position
        Item item = mItems.get(position % mItems.size());

        String imageUrl = "https://image.tmdb.org/t/p/w342" + item.getPosterPath();

        if (item.getIid().equals("")) {
            holder.textview1.setVisibility(View.VISIBLE);
            holder.textview1.bringToFront();
            holder.textview1.setText(item.getTitle());
        } else if (item.getGenre().equalsIgnoreCase("Book")) {
            if (item.getImgUrl() != null && !(item.getImgUrl().equalsIgnoreCase(""))) {
                if (item.getImgUrl().contains("nophoto")) {
                    Log.i("BookImageLibNoPhoto", "Title: " + item.getTitle() + " || ImgUrl: " + item.getImgUrl());
                    //if book does not have photo associated with it, put title instead
                    holder.posterImage.setImageResource(0);
                    holder.posterImage.setBackgroundColor(Color.parseColor("#000000"));
                    holder.textview1.setVisibility(View.VISIBLE);
                    holder.textview1.bringToFront();
                    holder.textview1.setText(item.getTitle());
                } else {
                    Log.i("BookImageLibPhoto", "Title: " + item.getTitle() + " || ImgUrl: " + item.getImgUrl());

                    holder.textview1.setVisibility(View.INVISIBLE);

                    Glide.with(context)
                            .load(item.getImgUrl())
                            .into(holder.posterImage);
                    holder.textview1.setText(item.getTitle());
                }

            } else {
                Log.i("BookImageLib", "no image url");

            }
        } else {
            // populate the views according to position
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.posterImage);
            holder.textview1.setVisibility(View.INVISIBLE);
            holder.textview1.setText(item.title);
        }

        Log.i("BookImageLib", "FINISHED");
    }

    @Override
    public int getItemCount() {
        //return mItems.size();
        if (mItems.size() < 4) {
            return mItems.size();
        }
        return Integer.MAX_VALUE;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textview1;
        CardView cardview;
        ImageView posterImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textview1 = itemView.findViewById(R.id.textview1);
            posterImage = itemView.findViewById(R.id.ivPosterImage);
            cardview = itemView.findViewById(R.id.cardview);
            cardview.setOnClickListener(this);

            cardview.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final int position = getAdapterPosition() % mItems.size();
                    final Item mItem = mItems.get(position);

                    if (!mItem.getIid().equals("")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(context);

                        alert.setTitle("Are you sure you want to delete " + mItem.getTitle() + " from your library?");
                        //alert.setMessage("Message");

                        // Set an EditText view to get user input
                        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // remove from db
                                dbItemsByUser = FirebaseDatabase.getInstance().getReference("itemsbyuser").child(LoginActivity.currentuser.getUid()).child(mItem.getIid());
                                dbItemsByUser.removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        dbUsersbyItem = FirebaseDatabase.getInstance().getReference("userbyitem").child(mItem.getIid()).child(LoginActivity.currentuser.getUid());
                                        dbUsersbyItem.removeValue(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                // remove from lib
                                                mItems.remove(position);
                                                Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                                                // TODO - reload recs
                                                RecsFragment.lib.remove(mItem.getIid());
                                                new refreshasync().execute();
                                            }
                                        });
                                    }
                                });
                            }
                        });

                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(context, "Cancelled!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        alert.show();
                    }
                    return false;
                }
            });
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition() % mItems.size();
//            Toast.makeText(context, String.format("Clicked %s!", position), Toast.LENGTH_SHORT).show();
            Log.i("overview", mItems.get(position).getDetails());
            Toast.makeText(context, String.format("Clicked %s!", position), Toast.LENGTH_SHORT).show();

            if (mItems.get(position).getIid().equals("")) {
                final Intent i = new Intent(context, InputRecsActivity.class);
                i.putExtra("user", Parcels.wrap(LoginActivity.currentuser));
                context.startActivity(i);
            } else {
                final Intent i = new Intent(context, DetailsActivity.class);
                i.putExtra("item", Parcels.wrap(mItems.get(position)));
                context.startActivity(i);
            }
        }
    }

    class refreshasync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            SearchAdapter.getrecs(RecsFragment.lib);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            DatabaseReference Recs = FirebaseDatabase.getInstance().getReference("recitemsbyuser").child(LoginActivity.currentuser.getUid());
            RecsFragment.getmovies(Recs);
            RecsFragment.getshows(Recs);
            RecsFragment.getbooks(Recs);
        }
    }
}