package codepath.com.gitreccedproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

    public libAdapter movieLibAdapter;
    public libAdapter tvLibAdapter;
    public libAdapter bookLibAdapter;

    public libexpadapter movieLibExpAdapter;
    public libexpadapter tvLibExpAdapter;
    public libexpadapter bookLibExpAdapter;

    public ArrayList<Item> movieItems;
    public ArrayList<Item> tvItems;
    public ArrayList<Item> bookItems;

    public RecyclerView rv_moviesexp;
    public RecyclerView rv_showsexp;
    public RecyclerView rv_booksexp;

    public ImageView movies_btn;
    public ImageView shows_btn;
    public ImageView books_btn;

    Query getUserItemsQuery;

    DatabaseReference dbItemsByUser;

    String uid = "libraryfragment: user id not set yet"; //user id (initialized to dummy string for testing)

    //public EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.libraryfragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //get current user's userid
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //create reference to dbUsersByItem
        dbItemsByUser= FirebaseDatabase.getInstance().getReference("itemsbyuser");
        // this is the fragment equivalent of onCreate
        movieItems = new ArrayList<>();
        tvItems = new ArrayList<>();
        bookItems = new ArrayList<>();

        // construct the adapter from this datasource
        movieLibAdapter = new libAdapter(movieItems);
        tvLibAdapter = new libAdapter(tvItems);
        bookLibAdapter = new libAdapter(bookItems);

        rv_libMovies = view.findViewById(R.id.rv_libMovies);
        rv_libTvShows = view.findViewById(R.id.rv_libTvShows);
        rv_libBooks = view.findViewById(R.id.rv_libBooks);

        final LinearLayoutManager movies = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager shows = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager books = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        movies_btn = view.findViewById(R.id.movies_btn);
        shows_btn = view.findViewById(R.id.shows_btn);
        books_btn = view.findViewById(R.id.books_btn);

        rv_libMovies.setLayoutManager(movies);
        rv_libTvShows.setLayoutManager(shows);
        rv_libBooks.setLayoutManager(books);
        // set the adapter
        rv_libMovies.setAdapter(movieLibAdapter);
        rv_libTvShows.setAdapter(tvLibAdapter);
        rv_libBooks.setAdapter(bookLibAdapter);

        rv_moviesexp = view.findViewById(R.id.rv_moviesexp);
        rv_showsexp = view.findViewById(R.id.rv_showsexp);
        rv_booksexp = view.findViewById(R.id.rv_booksexp);

        movieLibExpAdapter = new libexpadapter(movieItems);
        tvLibExpAdapter = new libexpadapter(tvItems);
        bookLibExpAdapter = new libexpadapter(bookItems);

        rv_moviesexp.setLayoutManager(new GridLayoutManager(getContext(),3));
        rv_moviesexp.setAdapter(movieLibExpAdapter);
        rv_showsexp.setLayoutManager(new GridLayoutManager(getContext(),3));
        rv_showsexp.setAdapter(tvLibExpAdapter);
        rv_booksexp.setLayoutManager(new GridLayoutManager(getContext(),3));
        rv_booksexp.setAdapter(bookLibExpAdapter);

        // queries itemsbyuser for each item, adds book to corresponding <genre>Items array list
        dbItemsByUser.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //clear items before add to them
                movieItems.clear();
                tvItems.clear();
                bookItems.clear();

                //TODO: iterate through datasnapshot to create new item from each subsnapshot, and add to corresponding list
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                        Item tempItem = itemSnapshot.getValue(Item.class);
                        if (tempItem.getGenre().equals("Movie")) {
                            //if item is a movie
                            movieItems.add(tempItem);
                        } else if (tempItem.getGenre().equals("TV")) {
                            //if item is a tv show
                            tvItems.add(tempItem);
                        } else {
                            //item is a book otherwise
                            bookItems.add(tempItem);
                        }
                }

                movieLibAdapter.notifyDataSetChanged();
                movieLibExpAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*//TODO - change this to get actual data
        for (int i = 0; i < 10; i++) {
            Item item = new Item();
            item.setTitle(String.format("%s",i));
            items.add(item);
            libAdapter.notifyItemInserted(items.size() - 1);
            libexpadapter.notifyItemInserted(items.size()-1);
        }*/

        // TODO - comment this if statement if we want to enable infinite scrolling only to the right
        if (movieItems.size() > 0) {
            movies.scrollToPosition(movieItems.size()*100);
            shows.scrollToPosition(100 * movieItems.size());
            books.scrollToPosition(100 * movieItems.size());
        }

        if (tvItems.size() > 0) {
            movies.scrollToPosition(tvItems.size()*100);
            shows.scrollToPosition(100 * tvItems.size());
            books.scrollToPosition(100 * tvItems.size());
        }

        if (bookItems.size() > 0) {
            movies.scrollToPosition(bookItems.size()*100);
            shows.scrollToPosition(100 * bookItems.size());
            books.scrollToPosition(100 * bookItems.size());
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

        shows_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rv_showsexp.getVisibility() == View.GONE) {
                    rv_libTvShows.setVisibility(View.GONE);
                    rv_showsexp.setVisibility(View.VISIBLE);
                    shows_btn.setImageResource(android.R.drawable.arrow_up_float);
                } else {
                    rv_libTvShows.setVisibility(View.VISIBLE);
                    rv_showsexp.setVisibility(View.GONE);
                    shows_btn.setImageResource(android.R.drawable.arrow_down_float);
                }
            }
        });

        books_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rv_booksexp.getVisibility() == View.GONE) {
                    rv_libBooks.setVisibility(View.GONE);
                    rv_booksexp.setVisibility(View.VISIBLE);
                    books_btn.setImageResource(android.R.drawable.arrow_up_float);
                } else {
                    rv_libBooks.setVisibility(View.VISIBLE);
                    rv_booksexp.setVisibility(View.GONE);
                    books_btn.setImageResource(android.R.drawable.arrow_down_float);
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