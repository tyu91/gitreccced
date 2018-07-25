package codepath.com.gitreccedproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class RecsFragment extends Fragment {
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        RecsFragment recsFragment = new RecsFragment();
        recsFragment.setArguments(bundle);
        return recsFragment;
    }

    public RecyclerView rv_movies;
    public RecyclerView rv_tvShows;
    public RecyclerView rv_books;
    public RecAdapter recAdapter;
    public ArrayList<Item> items;
    public ArrayList<Item> recItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.recsfragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // this is the fragment equivalent of onCreate

        items = new ArrayList<>();

        //setting items to recommend
        if(SearchAdapter.finalRecs != null) {
            //if finalRecs was populated in InputRecActivities
            items = (ArrayList<Item>) SearchAdapter.finalRecs;
        } else {
            items = dummyRecItems();
        }
        // construct the adapter from this datasource
        recAdapter = new RecAdapter(items);
        rv_movies = view.findViewById(R.id.rv_libMovies);
        rv_tvShows = view.findViewById(R.id.rv_tv);
        rv_books = view.findViewById(R.id.rv_books);

        LinearLayoutManager movies = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager tvShows = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager books = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        rv_movies.setLayoutManager(movies);
        rv_tvShows.setLayoutManager(tvShows);
        rv_books.setLayoutManager(books);

        // set the adapter
        rv_movies.setAdapter(recAdapter);
        rv_tvShows.setAdapter(recAdapter);
        rv_books.setAdapter(recAdapter);


        /*//change this to get actual data
        for (int i = 0; i < items.size(); i++) {
            Item item = (Item) recItems.get(i);
            items.add(item);
            recAdapter.notifyItemInserted(items.size() - 1);
        }*/

        // TODO - comment this if statement if we want to enable infinite scrolling only to the right
        if (items.size() > 0) {
            movies.scrollToPosition(items.size()*100);
            tvShows.scrollToPosition(100 * items.size());
            books.scrollToPosition(100 * items.size());
        }
    }

    public ArrayList<Item> dummyRecItems() {
        Item item1 = new Item();
        item1.setIid("-LHoUNVp_jaXb7wXvO1M");
        item1.setTitle("Thor: Ragnarok");
        item1.setGenre("Movie");
        item1.setDetails("Thor is on the other side of the universe and " +
                "finds himself in a race against time to get back to Asgard " +
                "to stop Ragnarok, the prophecy of destruction to his homeworld and the " +
                "end of Asgardian civilization, at the hands of an all-powerful new threat, " +
                "the ruthless Hela.");

        ArrayList dummyItems = new ArrayList();

        dummyItems.add(item1);
        dummyItems.add(item1);
        dummyItems.add(item1);

        return dummyItems;
    }
}