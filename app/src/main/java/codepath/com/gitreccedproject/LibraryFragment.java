package codepath.com.gitreccedproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
    public libexpadapter libexpadapter;
    public ArrayList<Item> items;

    public RecyclerView rv_moviesexp;

    public ImageView movies_btn;

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
        final LinearLayoutManager movies = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager shows = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager books = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        movies_btn = view.findViewById(R.id.movies_btn);

        rv_libMovies.setLayoutManager(movies);
        rv_libTvShows.setLayoutManager(shows);
        rv_libBooks.setLayoutManager(books);
        // set the adapter
        rv_libMovies.setAdapter(libAdapter);
        rv_libTvShows.setAdapter(libAdapter);
        rv_libBooks.setAdapter(libAdapter);

        rv_moviesexp = view.findViewById(R.id.rv_moviesexp);
        libexpadapter = new libexpadapter(items);
        rv_moviesexp.setLayoutManager(new GridLayoutManager(getContext(),3));
        rv_moviesexp.setAdapter(libexpadapter);

        //TODO - change this to get actual data
        for (int i = 0; i < 10; i++) {
            Item item = new Item();
            item.setTitle(String.format("%s",i));
            items.add(item);
            libAdapter.notifyItemInserted(items.size() - 1);
            libexpadapter.notifyItemInserted(items.size()-1);
        }

        // TODO - comment this if statement if we want to enable infinite scrolling only to the right
        if (items.size() > 0) {
            movies.scrollToPosition(items.size()*100);
            shows.scrollToPosition(100 * items.size());
            books.scrollToPosition(100 * items.size());
        }

        movies_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rv_moviesexp.getVisibility() == View.GONE) {
                    rv_libMovies.setVisibility(View.GONE);
                    rv_moviesexp.setVisibility(View.VISIBLE);
                    movies_btn.setImageResource(android.R.drawable.arrow_up_float);
                } else {
                    rv_libMovies.setVisibility(View.VISIBLE);
                    rv_moviesexp.setVisibility(View.GONE);
                    movies_btn.setImageResource(android.R.drawable.arrow_down_float);
                }
            }
        });


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