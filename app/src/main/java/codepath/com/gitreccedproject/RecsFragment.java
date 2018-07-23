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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.recsfragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // this is the fragment equivalent of onCreate
        items = new ArrayList<>();

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

        //TODO - change this to get actual data
        for (int i = 0; i < 5; i++) {
            Item item = new Item();
            item.setTitle(String.format("%s",i));
            items.add(item);
            recAdapter.notifyItemInserted(items.size() - 1);
        }

        // TODO - uncomment this if statement if we want to enable infinite scrolling in both directions
        if (items.size() > 0) {
            movies.scrollToPosition((int) Math.floor(Integer.MAX_VALUE / (2 * items.size())) * items.size());
            tvShows.scrollToPosition((int) Math.floor(Integer.MAX_VALUE / (2 * items.size())) * items.size());
            books.scrollToPosition((int) Math.floor(Integer.MAX_VALUE / (2 * items.size())) * items.size());
        }
    }
}