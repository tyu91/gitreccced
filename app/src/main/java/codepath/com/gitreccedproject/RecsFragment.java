package codepath.com.gitreccedproject;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    public RecAdapter movieRecAdapter;
    public RecAdapter tvRecAdapter;
    public RecAdapter bookRecAdapter;
    public ArrayList<Item> movieItems;
    public ArrayList<Pair<Item,String>> movieItem;
    public ArrayList<Item> tvItems;
    public ArrayList<Item> bookItems;

    public ArrayList<String> lib;

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

        movieItems = dummyMovieRecItems();
        tvItems = dummyTVRecItems();
        bookItems = dummyBookRecItems();

        movieItem = new ArrayList<>();

        // check if item is in user's library
        DatabaseReference dbItemsByUser = FirebaseDatabase.getInstance().getReference("itemsbyuser").child(LoginActivity.currentuser.getUid());
        com.google.firebase.database.Query itemsquery = null;
        itemsquery = dbItemsByUser;
        itemsquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lib = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    lib.add(postSnapshot.child("iid").getValue().toString());
                }
                SearchAdapter.getrecs(lib);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Recs = FirebaseDatabase.getInstance().getReference("recitemsbyuser").child(((MyLibraryActivity)this.getActivity()).mAuth.getUid());
        Log.i("user",((MyLibraryActivity)this.getActivity()).mAuth.getUid());

        com.google.firebase.database.Query moviesquery = null;
        moviesquery = Recs.child("Movie");
        moviesquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("shot",dataSnapshot.toString());
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.i("shott", postSnapshot.toString());
                    Item item = new Item(postSnapshot.child("iid").getValue().toString(),
                            "Movie", postSnapshot.child("title").getValue().toString(),
                            postSnapshot.child("details").getValue().toString());
                    //movieItems.add(item);

                    if (postSnapshot.child("count").getValue() != null) {
                        movieItem.add(Pair.create(item,postSnapshot.child("count").getValue().toString()));
                    }
                    Log.i("item", item.getTitle());
                }
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
                movieRecAdapter = new RecAdapter(movieItems);
                rv_movies.setAdapter(movieRecAdapter);
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
                Log.i("shot",dataSnapshot.toString());
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.i("shott", postSnapshot.toString());
                    Item item = new Item(postSnapshot.child("iid").getValue().toString(), "TV", postSnapshot.child("title").getValue().toString(), postSnapshot.child("details").getValue().toString());
                    tvItems.add(item);
                    Log.i("item", item.getTitle());
                }
                tvRecAdapter = new RecAdapter(tvItems);
                rv_tvShows.setAdapter(tvRecAdapter);
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
                Log.i("shot",dataSnapshot.toString());
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.i("shott", postSnapshot.toString());
                    Item item = new Item(postSnapshot.child("iid").getValue().toString(), "Book", postSnapshot.child("title").getValue().toString(), "");
                    bookItems.add(item);
                    Log.i("item", item.getTitle());
                }
                bookRecAdapter = new RecAdapter(bookItems);
                rv_books.setAdapter(bookRecAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //
            }
        });

        /*//setting movies to recommend
        if(SearchAdapter.finalMovieRecs != null && SearchAdapter.finalMovieRecs.size() != 0) {
            //if finalRecs was populated in InputRecActivities
            //movieItems = (ArrayList<Item>) SearchAdapter.finalMovieRecs;
        } else {
            movieItems = dummyMovieRecItems();
        }

        //setting tv shows to recommend
        if(SearchAdapter.finalTVRecs != null && SearchAdapter.finalTVRecs.size() != 0) {
            //if finalRecs was populated in InputRecActivities
            //tvItems = (ArrayList<Item>) SearchAdapter.finalTVRecs;
        } else {
            tvItems = dummyTVRecItems();
        }

        //setting books to recommend
        if(SearchAdapter.finalBookRecs != null && SearchAdapter.finalBookRecs.size() != 0 ) {
            //if finalRecs was populated in InputRecActivities
            //bookItems = (ArrayList<Item>) SearchAdapter.finalBookRecs;
        } else {
            bookItems = dummyBookRecItems();
        }
*/

        // construct the adapter from this datasource
        //movieRecAdapter = new RecAdapter(movieItems);

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
        //rv_movies.setAdapter(movieRecAdapter);

        // TODO - comment these if-statements if we want to enable infinite scrolling only to the right
        /*if (movieItems.size() > 0) {
            movies.scrollToPosition(100 * movieItems.size());
        }

        if (tvItems.size() > 0) {
            tvShows.scrollToPosition(100 * tvItems.size());
        }

        if (bookItems.size() > 0) {
            books.scrollToPosition(100 * bookItems.size());
        }*/
    }

    public ArrayList<Item> dummyMovieRecItems() {
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

        return dummyItems;
    }

    public ArrayList<Item> dummyTVRecItems() {
        Item item1 = new Item();
        item1.setIid("-LHo_O2XGJEFUHxxrKNi");
        item1.setTitle("Game of Thrones");
        item1.setGenre("TV");
        item1.setDetails("Seven noble families fight for control of the mythical" +
                " land of Westeros. Friction between the houses leads to full-scale " +
                "war. All while a very ancient evil awakens in the farthest north. Amidst" +
                " the war, a neglected military order of misfits, the Night's Watch, is" +
                " all that stands between the realms of men and icy horrors beyond.");

        ArrayList dummyItems = new ArrayList();

        dummyItems.add(item1);

        return dummyItems;
    }

    public ArrayList<Item> dummyBookRecItems() {
        Item item1 = new Item();
        item1.setIid("-LHtne3w212L5ERVRd-P");
        item1.setTitle("Harry Potter and the Goblet of Fire");
        item1.setGenre("Book");
        item1.setDetails("no description available");

        ArrayList dummyItems = new ArrayList();

        dummyItems.add(item1);

        return dummyItems;
    }
}