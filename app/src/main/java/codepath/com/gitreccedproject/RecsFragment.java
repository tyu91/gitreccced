package codepath.com.gitreccedproject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RecsFragment extends Fragment {
    private SwipeRefreshLayout swipeContainer;

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        RecsFragment recsFragment = new RecsFragment();
        recsFragment.setArguments(bundle);
        return recsFragment;
    }

    public static RecyclerView rv_movies;
    public static RecyclerView rv_tvShows;
    public static RecyclerView rv_books;
    public static RecAdapter movieRecAdapter;
    public static RecAdapter tvRecAdapter;
    public static RecAdapter bookRecAdapter;
    public static ArrayList<Item> movieItems;
    public static ArrayList<Pair<Item,String>> movieItem;
    public static ArrayList<Pair<Item,String>> tvItem;
    public static ArrayList<Pair<Item,String>> bookItem;
    public static ArrayList<Item> tvItems;
    public static ArrayList<Item> bookItems;

    public static ArrayList<String> lib;

    DatabaseReference Recs;

    public Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        activity = (MyLibraryActivity) getActivity();
        return inflater.inflate(R.layout.recsfragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // this is the fragment equivalent of onCreate

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        final ImageView refresh = toolbar.findViewById(R.id.refresh);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MyLibraryActivity)getActivity()).showProgressBar();
                refresh();
            }
        });

        movieItems = new ArrayList<>();
        tvItems = new ArrayList<>();
        bookItems = new ArrayList<>();
        //movieItems = dummyMovieRecItems();
        //tvItems = dummyTVRecItems();
        //bookItems = dummyBookRecItems();

        ((MyLibraryActivity)getActivity()).showProgressBar();

        refresh();

        rv_movies = view.findViewById(R.id.rv_libMovies);
        rv_tvShows = view.findViewById(R.id.rv_tv);
        rv_books = view.findViewById(R.id.rv_books);

        LinearLayoutManager movies = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager tvShows = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager books = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        rv_movies.setLayoutManager(movies);
        rv_tvShows.setLayoutManager(tvShows);
        rv_books.setLayoutManager(books);

        swipeContainer = view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                ((MyLibraryActivity)getActivity()).showProgressBar();
                new loadasync().execute();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //swipeContainer.setDistanceToTriggerSync(1000);
        //swipeContainer.setSlingshotDistance(50);
    }

    public static ArrayList<Item> dummyMovieRecItems() {
        Item item1 = new Item();
        item1.setIid("-LHoUNVp_jaXb7wXvO1M");
        item1.setTitle("Thor: Ragnarok");
        item1.setGenre("Movie");
        item1.setDetails("Thor is on the other side of the universe and " +
                "finds himself in a race against time to get back to Asgard " +
                "to stop Ragnarok, the prophecy of destruction to his homeworld and the " +
                "end of Asgardian civilization, at the hands of an all-powerful new threat, " +
                "the ruthless Hela.");
        item1.setPosterPath("/rzRwTcFvttcN1ZpX2xv4j3tSdJu.jpg");

        ArrayList dummyItems = new ArrayList();

        dummyItems.add(item1);

        return dummyItems;
    }

    public static ArrayList<Item> dummyTVRecItems() {
        Item item1 = new Item();
        item1.setIid("-LHo_O2XGJEFUHxxrKNi");
        item1.setTitle("Game of Thrones");
        item1.setGenre("TV");
        item1.setDetails("Seven noble families fight for control of the mythical" +
                " land of Westeros. Friction between the houses leads to full-scale " +
                "war. All while a very ancient evil awakens in the farthest north. Amidst" +
                " the war, a neglected military order of misfits, the Night's Watch, is" +
                " all that stands between the realms of men and icy horrors beyond.");
        item1.setPosterPath("/gwPSoYUHAKmdyVywgLpKKA4BjRr.jpg");

        ArrayList dummyItems = new ArrayList();

        dummyItems.add(item1);

        return dummyItems;
    }

    public static ArrayList<Item> dummyBookRecItems() {
        Item item1 = new Item();
        item1.setIid("-LHtne3w212L5ERVRd-P");
        item1.setTitle("Harry Potter and the Goblet of Fire");
        item1.setGenre("Book");
        item1.setDetails("no description available");

        ArrayList dummyItems = new ArrayList();

        dummyItems.add(item1);

        return dummyItems;
    }

    class loadasync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            new populateasync().execute();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ((MyLibraryActivity)getActivity()).hideProgressBar();
            swipeContainer.setRefreshing(false);
        }
    }

    class populateasync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            ((MyLibraryActivity)getActivity()).showProgressBar();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SearchAdapter.getrecs(lib);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i("postexecute", "postexecute");
            Recs = FirebaseDatabase.getInstance().getReference("recitemsbyuser").child(((MyLibraryActivity)getActivity()).mAuth.getUid());
            //Log.i("user",((MyLibraryActivity)this.getActivity()).mAuth.getUid());

            com.google.firebase.database.Query moviesquery = null;
            moviesquery = Recs.child("Movie");
            moviesquery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //((MyLibraryActivity)getActivity()).showProgressBar();
                    movieItem = new ArrayList<>();
                    movieItems = new ArrayList<>();
                    Log.i("shot",dataSnapshot.toString());
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Log.i("shott", postSnapshot.toString());
                        Item item = new Item(postSnapshot.child("iid").getValue().toString(), "Movie", postSnapshot.child("title").getValue().toString(), postSnapshot.child("details").getValue().toString());
                        //TODO: set posterPath, backdropPath, associated sizes, movieId
                        item.setMovieId(postSnapshot.child("movieId").getValue().toString());
                        item.setPosterPath(postSnapshot.child("posterPath").getValue().toString());
                        item.setBackdropPath(postSnapshot.child("backdropPath").getValue().toString());
                        movieItems.add(item);
                        Log.i("TAG1", item.getTitle());
                        if (postSnapshot.child("count").getValue() != null) {
                            movieItem.add(Pair.create(item,postSnapshot.child("count").getValue().toString()));
                            Log.i("TAG", item.getTitle());
                        }
                        Log.i("item", item.getTitle());
                    }
                    Log.i("movieItem",movieItem.toString());
                    Collections.sort(movieItem, new Comparator<Pair<Item,String>>() {
                        @Override
                        public int compare(Pair<Item,String> lhs, Pair<Item,String> rhs) {
                            // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                            return Long.parseLong(lhs.second) > Long.parseLong(rhs.second) ? -1 : (Long.parseLong(lhs.second) < Long.parseLong(rhs.second)) ? 1 : 0;
                        }
                    });
                    for (int i = 0; i < movieItem.size(); i++) {
                        Log.i("sorted",movieItem.get(i).first.getTitle() + movieItem.get(i).second);
                        movieItems.add(movieItem.get(i).first);
                    }
                    if (movieItems.size() == 0) {
                        movieItems = dummyMovieRecItems();
                    }
                    movieRecAdapter = new RecAdapter(movieItems);
                    rv_movies.setAdapter(movieRecAdapter);
                    //((MyLibraryActivity)getActivity()).hideProgressBar();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //
                }
            });

            com.google.firebase.database.Query showsquery = null;
            showsquery = Recs.child("TV");
            showsquery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //((MyLibraryActivity)getActivity()).showProgressBar();
                    tvItem = new ArrayList<>();
                    tvItems = new ArrayList<>();
                    Log.i("shot",dataSnapshot.toString());
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Log.i("shott", postSnapshot.toString());
                        Item item = new Item(postSnapshot.child("iid").getValue().toString(), "TV", postSnapshot.child("title").getValue().toString(), postSnapshot.child("details").getValue().toString());
                        item.setMovieId(postSnapshot.child("movieId").getValue().toString());
                        item.setPosterPath(postSnapshot.child("posterPath").getValue().toString());
                        item.setBackdropPath(postSnapshot.child("backdropPath").getValue().toString());
                        movieItems.add(item);
                        Log.i("TAG1", item.getTitle());
                        if (postSnapshot.child("count").getValue() != null) {
                            tvItem.add(Pair.create(item,postSnapshot.child("count").getValue().toString()));
                            Log.i("TAG", item.getTitle());
                        }
                        Log.i("item", item.getTitle());
                    }
                    Log.i("tvItem",tvItem.toString());
                    Collections.sort(tvItem, new Comparator<Pair<Item,String>>() {
                        @Override
                        public int compare(Pair<Item,String> lhs, Pair<Item,String> rhs) {
                            // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                            return Long.parseLong(lhs.second) > Long.parseLong(rhs.second) ? -1 : (Long.parseLong(lhs.second) < Long.parseLong(rhs.second)) ? 1 : 0;
                        }
                    });
                    for (int i = 0; i < tvItem.size(); i++) {
                        Log.i("sorted",tvItem.get(i).first.getTitle() + tvItem.get(i).second);
                        tvItems.add(tvItem.get(i).first);
                    }
                    if (tvItems.size() == 0) {
                        tvItems = dummyTVRecItems();
                    }
                    tvRecAdapter = new RecAdapter(tvItems);
                    rv_tvShows.setAdapter(tvRecAdapter);
                    //((MyLibraryActivity)getActivity()).hideProgressBar();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //
                }
            });

            com.google.firebase.database.Query booksquery = null;
            booksquery = Recs.child("Book");
            booksquery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //((MyLibraryActivity)getActivity()).showProgressBar();
                    bookItem = new ArrayList<>();
                    bookItems = new ArrayList<>();
                    Log.i("shot",dataSnapshot.toString());
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Log.i("shott", postSnapshot.toString());
                        Item item = new Item(postSnapshot.child("iid").getValue().toString(), "Book", postSnapshot.child("title").getValue().toString(), "");
                        item.setBookId(postSnapshot.child("bookId").getValue().toString());
                        item.setSmallImgUrl(postSnapshot.child("smallImgUrl").getValue().toString());
                        item.setImgUrl(postSnapshot.child("imgUrl").getValue().toString());
                        //movieItems.add(item);
                        Log.i("TAG1", item.getTitle());
                        if (postSnapshot.child("count").getValue() != null) {
                            bookItem.add(Pair.create(item,postSnapshot.child("count").getValue().toString()));
                            Log.i("TAG", item.getTitle());
                        }
                        Log.i("item", item.getTitle());
                    }
                    Log.i("bookItem",bookItem.toString());
                    Collections.sort(bookItem, new Comparator<Pair<Item,String>>() {
                        @Override
                        public int compare(Pair<Item,String> lhs, Pair<Item,String> rhs) {
                            // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                            return Long.parseLong(lhs.second) > Long.parseLong(rhs.second) ? -1 : (Long.parseLong(lhs.second) < Long.parseLong(rhs.second)) ? 1 : 0;
                        }
                    });
                    for (int i = 0; i < bookItem.size(); i++) {
                        Log.i("sorted",bookItem.get(i).first.getTitle() + bookItem.get(i).second);
                        bookItems.add(bookItem.get(i).first);
                    }
                    if (bookItems.size() == 0) {
                        bookItems = dummyBookRecItems();
                    }
                    bookRecAdapter = new RecAdapter(bookItems);
                    rv_books.setAdapter(bookRecAdapter);
                    //((MyLibraryActivity)getActivity()).hideProgressBar();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //Empty
                }

                getmovies(Recs);

            getshows(Recs);

            getbooks(Recs);
        }

    }

    public void refresh() {
        // check if item is in user's library
        DatabaseReference dbItemsByUser = FirebaseDatabase.getInstance().getReference("itemsbyuser").child(((MyLibraryActivity)getActivity()).mAuth.getUid());
        com.google.firebase.database.Query itemsquery = null;
        itemsquery = dbItemsByUser;
        itemsquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lib = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    lib.add(postSnapshot.child("iid").getValue().toString());
                }
                new loadasync().execute();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getmovies(DatabaseReference Recs) {
        com.google.firebase.database.Query moviesquery = null;
        moviesquery = Recs.child("Movie");
        moviesquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //((MyLibraryActivity)getActivity()).showProgressBar();
                movieItem = new ArrayList<>();
                movieItems = new ArrayList<>();
                Log.i("shot",dataSnapshot.toString());
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.i("shott", postSnapshot.toString());
                    Item item = new Item(postSnapshot.child("iid").getValue().toString(), "Movie", postSnapshot.child("title").getValue().toString(), postSnapshot.child("details").getValue().toString());
                    //TODO: set posterPath, backdropPath, associated sizes, movieId
                    item.setMovieId(postSnapshot.child("movieId").getValue().toString());
                    item.setPosterPath(postSnapshot.child("posterPath").getValue().toString());
                    item.setBackdropPath(postSnapshot.child("backdropPath").getValue().toString());
                    //movieItems.add(item);
                    Log.i("TAG1", item.getTitle());
                    if (postSnapshot.child("count").getValue() != null) {
                        movieItem.add(Pair.create(item,postSnapshot.child("count").getValue().toString()));
                        Log.i("TAG", item.getTitle());
                    }
                    Log.i("item", item.getTitle());
                }
                Log.i("movieItem",movieItem.toString());
                Collections.sort(movieItem, new Comparator<Pair<Item,String>>() {
                    @Override
                    public int compare(Pair<Item,String> lhs, Pair<Item,String> rhs) {
                        // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                        return Long.parseLong(lhs.second) > Long.parseLong(rhs.second) ? -1 : (Long.parseLong(lhs.second) < Long.parseLong(rhs.second)) ? 1 : 0;
                    }
                });
                for (int i = 0; i < movieItem.size(); i++) {
                    Log.i("sorted",movieItem.get(i).first.getTitle() + movieItem.get(i).second);
                    movieItems.add(movieItem.get(i).first);
                }
                if (movieItems.size() == 0) {
                    movieItems = dummyMovieRecItems();
                }
                movieRecAdapter = new RecAdapter(movieItems);
                rv_movies.setAdapter(movieRecAdapter);
                //((MyLibraryActivity)getActivity()).hideProgressBar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
            }
        });
    }

    public void getshows(DatabaseReference Recs) {
        com.google.firebase.database.Query showsquery = null;
        showsquery = Recs.child("TV");
        showsquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //((MyLibraryActivity)getActivity()).showProgressBar();
                tvItem = new ArrayList<>();
                tvItems = new ArrayList<>();
                Log.i("shot",dataSnapshot.toString());
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.i("shott", postSnapshot.toString());
                    Item item = new Item(postSnapshot.child("iid").getValue().toString(), "TV", postSnapshot.child("title").getValue().toString(), postSnapshot.child("details").getValue().toString());
                    item.setMovieId(postSnapshot.child("movieId").getValue().toString());
                    item.setPosterPath(postSnapshot.child("posterPath").getValue().toString());
                    item.setBackdropPath(postSnapshot.child("backdropPath").getValue().toString());
                    //movieItems.add(item);
                    Log.i("TAG1", item.getTitle());
                    if (postSnapshot.child("count").getValue() != null) {
                        tvItem.add(Pair.create(item,postSnapshot.child("count").getValue().toString()));
                        Log.i("TAG", item.getTitle());
                    }
                    Log.i("item", item.getTitle());
                }
                Log.i("tvItem",tvItem.toString());
                Collections.sort(tvItem, new Comparator<Pair<Item,String>>() {
                    @Override
                    public int compare(Pair<Item,String> lhs, Pair<Item,String> rhs) {
                        // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                        return Long.parseLong(lhs.second) > Long.parseLong(rhs.second) ? -1 : (Long.parseLong(lhs.second) < Long.parseLong(rhs.second)) ? 1 : 0;
                    }
                });
                for (int i = 0; i < tvItem.size(); i++) {
                    Log.i("sorted",tvItem.get(i).first.getTitle() + tvItem.get(i).second);
                    tvItems.add(tvItem.get(i).first);
                }
                if (tvItems.size() == 0) {
                    tvItems = dummyTVRecItems();
                }
                tvRecAdapter = new RecAdapter(tvItems);
                rv_tvShows.setAdapter(tvRecAdapter);
                //((MyLibraryActivity)getActivity()).hideProgressBar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
            }
        });
    }

    public void getbooks(DatabaseReference Recs) {
        com.google.firebase.database.Query booksquery = null;
        booksquery = Recs.child("Book");
        booksquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //((MyLibraryActivity)getActivity()).showProgressBar();
                bookItem = new ArrayList<>();
                bookItems = new ArrayList<>();
                Log.i("shot",dataSnapshot.toString());
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.i("shott", postSnapshot.toString());
                    Item item = new Item(postSnapshot.child("iid").getValue().toString(), "Book", postSnapshot.child("title").getValue().toString(), "");
                    item.setBookId(postSnapshot.child("bookId").getValue().toString());
                    item.setSmallImgUrl(postSnapshot.child("smallImgUrl").getValue().toString());
                    item.setImgUrl(postSnapshot.child("imgUrl").getValue().toString());
                    //movieItems.add(item);
                    Log.i("TAG1", item.getTitle());
                    if (postSnapshot.child("count").getValue() != null) {
                        bookItem.add(Pair.create(item,postSnapshot.child("count").getValue().toString()));
                        Log.i("TAG", item.getTitle());
                    }
                    Log.i("item", item.getTitle());
                }
                Log.i("bookItem",bookItem.toString());
                Collections.sort(bookItem, new Comparator<Pair<Item,String>>() {
                    @Override
                    public int compare(Pair<Item,String> lhs, Pair<Item,String> rhs) {
                        // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                        return Long.parseLong(lhs.second) > Long.parseLong(rhs.second) ? -1 : (Long.parseLong(lhs.second) < Long.parseLong(rhs.second)) ? 1 : 0;
                    }
                });
                for (int i = 0; i < bookItem.size(); i++) {
                    Log.i("sorted",bookItem.get(i).first.getTitle() + bookItem.get(i).second);
                    bookItems.add(bookItem.get(i).first);
                }
                if (bookItems.size() == 0) {
                    bookItems = dummyBookRecItems();
                }
                bookRecAdapter = new RecAdapter(bookItems);
                rv_books.setAdapter(bookRecAdapter);
                //((MyLibraryActivity)getActivity()).hideProgressBar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
                }
            });
        }
    }
}