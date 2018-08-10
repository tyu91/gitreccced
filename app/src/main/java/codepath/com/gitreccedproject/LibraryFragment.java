package codepath.com.gitreccedproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class LibraryFragment extends Fragment {

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        LibraryFragment libraryFragment = new LibraryFragment();
        libraryFragment.setArguments(bundle);
        return libraryFragment;
    }

    //CONSTANTS
    //base url of API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    //parameter name
    public final static String API_KEY_PARAM = "api_key";

    AsyncHttpClient client;

    Config config;

    public RecyclerView rv_libMovies;
    public RecyclerView rv_libTvShows;
    public RecyclerView rv_libBooks;

    public libAdapter movieslibAdapter;
    public libAdapter TVlibAdapter;
    public libAdapter bookslibAdapter;
    public libexpadapter movieslibexpadapter;
    public libexpadapter TVlibexpadapter;
    public libexpadapter bookslibexpadapter;
    public ArrayList<Item> items;

    public RecyclerView rv_moviesexp;
    public RecyclerView rv_showsexp;
    public RecyclerView rv_booksexp;

    public ImageView movies_btn;
    public ImageView shows_btn;
    public ImageView books_btn;


    DatabaseReference dbItemsByUser;
    public ArrayList<Item> movieslib;
    public ArrayList<Item> TVlib;
    public ArrayList<Item> booklib;

    //public EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        client = new AsyncHttpClient();

        getConfiguration();
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.libraryfragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //create reference to dbUsersByItem
        dbItemsByUser= FirebaseDatabase.getInstance().getReference("itemsbyuser");

        // construct the adapter from this datasource
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

        rv_libMovies.setAdapter(movieslibAdapter);
        rv_libTvShows.setAdapter(TVlibAdapter);
        rv_libBooks.setAdapter(bookslibAdapter);

        rv_moviesexp = view.findViewById(R.id.rv_moviesexp);
        rv_showsexp = view.findViewById(R.id.rv_showsexp);
        rv_booksexp = view.findViewById(R.id.rv_booksexp);

        rv_moviesexp.setLayoutManager(new GridLayoutManager(getContext(),4));
        rv_showsexp.setLayoutManager(new GridLayoutManager(getContext(),4));
        rv_booksexp.setLayoutManager(new GridLayoutManager(getContext(),4));

        rv_moviesexp.setAdapter(movieslibexpadapter);
        rv_showsexp.setAdapter(TVlibexpadapter);
        rv_booksexp.setAdapter(bookslibexpadapter);

        dbItemsByUser = FirebaseDatabase.getInstance().getReference("itemsbyuser").child(((MyLibraryActivity)getActivity()).mAuth.getUid());
        com.google.firebase.database.Query itemsquery = null;
        itemsquery = dbItemsByUser;
        itemsquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                movieslib = new ArrayList<>();
                TVlib = new ArrayList<>();
                booklib = new ArrayList<>();
                //actual data
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.i("shottt",postSnapshot.toString());
                    Item item = new Item(postSnapshot.child("iid").getValue().toString(),postSnapshot.child("genre").getValue().toString(),postSnapshot.child("title").getValue().toString(),"");

                    if (item.getGenre().contains("Movie")) {
                        item.setMovieId(postSnapshot.child("movieId").getValue().toString());
                        item.setPosterPath(postSnapshot.child("posterPath").getValue().toString());
                        item.setBackdropPath(postSnapshot.child("backdropPath").getValue().toString());
                        item.setReleaseDate(postSnapshot.child("releaseDate").getValue().toString());
                        //item.setDetails(postSnapshot.child("overview").getValue().toString());
                        movieslib.add(item);

                    } else if (item.getGenre().contains("TV")) {
                        item.setMovieId(postSnapshot.child("movieId").getValue().toString());
                        item.setPosterPath(postSnapshot.child("posterPath").getValue().toString());
                        item.setBackdropPath(postSnapshot.child("backdropPath").getValue().toString());
                        //item.setDetails(postSnapshot.child("overview").getValue().toString());
                        TVlib.add(item);

                    } else {
                        item.setBookId(postSnapshot.child("bookId").getValue().toString());
                        item.setSmallImgUrl(postSnapshot.child("smallImgUrl").getValue().toString());
                        item.setImgUrl(postSnapshot.child("imgUrl").getValue().toString());
                        item.setAuthor(postSnapshot.child("author").getValue().toString());
                        item.setPubYear(postSnapshot.child("pubYear").getValue().toString());
                        booklib.add(item);
                    }
                }
                if (movieslib.size() == 0) {
                    movieslib.add(new Item("","","",""));
                }
                if (TVlib.size() == 0) {
                    TVlib.add(new Item("","","",""));
                }
                if (booklib.size() == 0) {
                    booklib.add(new Item("","","",""));
                }
                if (movieslib.size() <= 4) {
                    movies_btn.setVisibility(View.GONE);
                }
                if (TVlib.size() <= 4) {
                    shows_btn.setVisibility(View.GONE);
                }
                if (booklib.size() <= 4) {
                    books_btn.setVisibility(View.GONE);
                }
                movieslibAdapter = new libAdapter(movieslib);
                rv_libMovies.setAdapter(movieslibAdapter);
                TVlibAdapter = new libAdapter(TVlib);
                rv_libTvShows.setAdapter(TVlibAdapter);
                bookslibAdapter = new libAdapter(booklib);
                rv_libBooks.setAdapter(bookslibAdapter);

                movieslibexpadapter = new libexpadapter(movieslib);
                bookslibexpadapter = new libexpadapter(booklib);
                TVlibexpadapter = new libexpadapter(TVlib);
                rv_moviesexp.setAdapter(movieslibexpadapter);
                rv_showsexp.setAdapter(TVlibexpadapter);
                rv_booksexp.setAdapter(bookslibexpadapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // TODO - comment this if statement if we want to enable infinite scrolling only to the right
        /*if (items.size() > 0) {
            movies.scrollToPosition(items.size()*100);
            shows.scrollToPosition(100 * items.size());
            books.scrollToPosition(100 * items.size());
        }*/

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

//        getConfiguration();
    }

    //get the config from API
    private void getConfiguration() {
        //create the url
        String url = API_BASE_URL + "/configuration";
        //set up request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.movieApiKey)); //this is API key: always necessary!!!
        //execute a GET request that expects a response from JSON object
        RequestHandle requestHandle = client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("MovieDB", response.toString());
                try {
                    config = new Config(response);
                    //TODO: set config fields, etc in Item class (and also <genre> classes as well?)
                    Log.i("MovieDB", String.format("Loaded config w imageBaseUrl %s and posterSize %s", config.getImageBaseUrl(), config.getPosterSize()));
                    movieslibAdapter.setConfig(config);
                    movieslibexpadapter.setConfig(config);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.i("MovieDB", response.toString());
//                try {
//                    config = new Config(response);
//                    //TODO: set config fields, etc in Item class (and also <genre> classes as well?)
//                    Log.i("MovieDB", String.format("Loaded config w imageBaseUrl %s and posterSize %s", config.getImageBaseUrl(), config.getPosterSize()));
//                    movieslibAdapter.setConfig(config);
//
//                } catch(JSONException e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.i("MovieDB", responseString);
//                try {
//                    config = new Config(response);
//                    //TODO: set config fields, etc in Item class (and also <genre> classes as well?)
//                    Log.i("MovieDB", String.format("Loaded config w imageBaseUrl %s and posterSize %s", config.getImageBaseUrl(), config.getPosterSize()));
//                    movieslibAdapter.setConfig(config);
//
//                } catch(JSONException e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("MovieDB", "could not generate new config");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("MovieDB", "could not generate new config");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("MovieDB", "could not generate new config");
            }
        });
    }
}