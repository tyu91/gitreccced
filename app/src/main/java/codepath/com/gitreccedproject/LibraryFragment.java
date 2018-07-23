package codepath.com.gitreccedproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class LibraryFragment extends Fragment {

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        LibraryFragment libraryFragment = new LibraryFragment();
        libraryFragment.setArguments(bundle);
        return libraryFragment;
    }

    public RecyclerView rv_libMovies;
    public RecyclerView rv_libTvShows;
    public RecyclerView rv_libBooks;
    public libAdapter libAdapter;
    public ArrayList<Item> items;

    //public EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.libraryfragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // this is the fragment equivalent of onCreate
        items = new ArrayList<>();

        // construct the adapter from this datasource
        libAdapter = new libAdapter(items);
        rv_libMovies = view.findViewById(R.id.rv_libMovies);
        rv_libTvShows = view.findViewById(R.id.rv_libTvShows);
        rv_libBooks = view.findViewById(R.id.rv_libBooks);
        LinearLayoutManager movies = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager shows = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager books = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        rv_libMovies.setLayoutManager(movies);
        rv_libTvShows.setLayoutManager(shows);
        rv_libBooks.setLayoutManager(books);
        // set the adapter
        rv_libMovies.setAdapter(libAdapter);
        rv_libTvShows.setAdapter(libAdapter);
        rv_libBooks.setAdapter(libAdapter);

        //TODO - change this to get actual data
        for (int i = 0; i < 5; i++) {
            Item item = new Item();
            item.setTitle(String.format("%s",i));
            items.add(item);
            libAdapter.notifyItemInserted(items.size() - 1);
        }

        // TODO - uncomment this if statement if we want to enable infinite scrolling in both directions
        if (items.size() > 0) {
            movies.scrollToPosition((int) Math.floor(Integer.MAX_VALUE / (2 * items.size())) * items.size());
            shows.scrollToPosition((int) Math.floor(Integer.MAX_VALUE / (2 * items.size())) * items.size());
            books.scrollToPosition((int) Math.floor(Integer.MAX_VALUE / (2 * items.size())) * items.size());
        }


        /*scrollListener = new EndlessRecyclerViewScrollListener(movies) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                rv_libMovies.getLayoutManager().scrollToPosition(page*items.size());
            }
        };
        // Adds the scroll listener to RecyclerView
        rv_libMovies.addOnScrollListener(scrollListener);*/
    }
}