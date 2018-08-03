package codepath.com.gitreccedproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ViewHolder> {

    Context context;

    public List<Item> mItems;

    MovieDetailsFragment movieFrag;
    TVDetailsFragment tvFrag;
    BookDetailsFragment bookFrag;

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
    public void onBindViewHolder(@NonNull DetailsAdapter.ViewHolder holder, int position) {
        Item item = mItems.get(position % mItems.size());
        DetailsActivity details = new DetailsActivity();

        if (item.getGenre().equalsIgnoreCase("Movie")){

        } else if (item.getGenre().equalsIgnoreCase("TV")){

        } else {

        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        public ViewHolder(View itemView){
            super(itemView);

        }

        @Override
        public void onClick(View view) {

        }
    }
}