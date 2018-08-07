package codepath.com.gitreccedproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ViewHolder> {

    Context context;
    AsyncHttpClient tvDetailsClient;
    JSONTv tv;

    public List<Item> mItems;

    public DetailsAdapter() {
        //empty
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        //get context from the inflater
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //create the view using the item_movie layout
        View detailView = inflater.inflate(R.layout.activity_details, parent, false);
        //return new ViewHolder
        return new ViewHolder(detailView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DetailsAdapter.ViewHolder holder, int position) {
        Item item = mItems.get(position % mItems.size());
        DetailsActivity details = new DetailsActivity();

        String imageUrl = "https://image.tmdb.org/t/p/w342" + item.getBackdropPath();

        if (item.getGenre().equalsIgnoreCase("Movie")){
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.moviePoster);

            holder.movieTitle.setText(item.getTitle());
            //holder.director.setText(item.getDirector());
            holder.releaseDate.setText(item.getReleaseDate());
            holder.movieOverview.setText(item.getDetails());

        } else if (item.getGenre().equalsIgnoreCase("TV")){
            //if item is TV
            tvDetailsClient = new AsyncHttpClient();
            String tvDetailsUrl = "https://api.themoviedb.org/3/tv/" + item.getMovieId();
            RequestParams tvDetailsParams = new RequestParams();
            tvDetailsParams.put("api_key", context.getString(R.string.movieApiKey));

            tvDetailsClient.get(tvDetailsUrl, tvDetailsParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.i("SearchAdapter", "SUCCESS: received response");
                    try {
                        holder.seasons.setText(String.format(" %s", response.getString("number_of_seasons")));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.i("DetailsAdapter", "FAILURE. Response String: " + responseString);
                }
            });

            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.tvPoster);

            holder.tvShowName.setText(item.getTitle());
//            holder.seasons.setText(tv.getSeasons());
            holder.firstAired.setText(item.getFirstAirDate());
            holder.tvShowOverview.setText(item.getDetails());

        } else {
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.bookCover);
            holder.bookTitle.setText(item.getTitle());
            holder.bookOverview.setText(item.getDetails());
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView movieOverview, tvShowOverview, bookOverview, movieTitle, tvShowName, bookTitle, director, seasons, author, releaseDate, firstAired;
        ImageView moviePoster, tvPoster, bookCover;

        public ViewHolder(View itemView){
            super(itemView);

            moviePoster = itemView.findViewById(R.id.ivMoviePoster);
            tvPoster = itemView.findViewById(R.id.ivTVPoster);
            bookCover = itemView.findViewById(R.id.ivBookCover);

            movieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvShowName = itemView.findViewById(R.id.tvShowTitle);
            bookTitle = itemView.findViewById(R.id.tvBookTitle);

            director = itemView.findViewById(R.id.tvDirector);
            seasons = itemView.findViewById(R.id.tvSeasons);
            author = itemView.findViewById(R.id.tvAuthor);

            releaseDate = itemView.findViewById(R.id.tvReleaseDate);
            firstAired = itemView.findViewById(R.id.tvFirstAired);

            movieOverview = itemView.findViewById(R.id.tvMovieOverview);
            tvShowOverview = itemView.findViewById(R.id.tvShowOverview);
            bookOverview = itemView.findViewById(R.id.tvBookOverview);
        }

        @Override
        public void onClick(View view) {

        }
    }
}