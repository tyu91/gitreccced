package codepath.com.gitreccedproject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static codepath.com.gitreccedproject.LibraryFragment.html2text;

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

    Snackbar bar;

    DatabaseReference Recs;

    public Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        activity = getActivity();
        return inflater.inflate(R.layout.recsfragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // this is the fragment equivalent of onCreate

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        //final ImageView refresh = toolbar.findViewById(R.id.refresh);

        //refresh.setVisibility(View.GONE);

        /*refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MyLibraryActivity)getActivity()).showProgressBar();
                refresh();
            }
        });*/

        movieItems = new ArrayList<>();
        tvItems = new ArrayList<>();
        bookItems = new ArrayList<>();
        //movieItems = dummyMovieRecItems();
        //tvItems = dummyTVRecItems();
        //bookItems = dummyBookRecItems();

        //((MyLibraryActivity)getActivity()).showProgressBar();

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
                //((MyLibraryActivity)getActivity()).showProgressBar();
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
        item1.setIid("-LJFJtZFOUbAoELId93F");
        item1.setTitle("The Incredibles");
        item1.setGenre("Movie");
        item1.setDetails("Bob Parr has given up his superhero days to log in time as an insurance adjuster and raise his three children with his formerly heroic wife in suburbia. But when he receives a mysterious assignment, it's time to get back into costume.");
        item1.setPosterPath("/2LqaLgk4Z226KkgPJuiOQ58wvrm.jpg");
        item1.setBackdropPath("/wiDGnsn9RtNglgKQy4J1jZQBG5v.jpg");
        item1.setReleaseDate("2004-11-05");
        item1.setMovieId("9806");

        Item item2 = new Item();
        item2.setIid("-LJFJtYgQ_t4oMTF9rH6");
        item2.setTitle("Avatar");
        item2.setGenre("Movie");
        item2.setDetails("In the 22nd century, a paraplegic Marine is dispatched to the moon Pandora on a unique mission, but becomes torn between following orders and protecting an alien civilization.");
        item2.setPosterPath("/kmcqlZGaSh20zpTbuoF0Cdn07dT.jpg");
        item2.setBackdropPath("/5XPPB44RQGfkBrbJxmtdndKz05n.jpg");
        item2.setReleaseDate("2009-12-10");
        item2.setMovieId("19995");

        Item item3 = new Item();
        item3.setIid("-LJFJtg7u0nbXo9OWnpH");
        item3.setTitle("Ocean's Twelve");
        item3.setGenre("Movie");
        item3.setDetails("Danny Ocean reunites with his old flame and the rest of his merry band of thieves in carrying out three huge heists in Rome, Paris and Amsterdam – but a Europol agent is hot on their heels.");
        item3.setPosterPath("/oBJ8LF80wX8PzhnPcNO0E9lnmYA.jpg");
        item3.setBackdropPath("/bYfM7W5bPTRLptXTItYExgTZLQV.jpg");
        item3.setReleaseDate("2004-12-09");
        item3.setMovieId("163");

        Item item4 = new Item();
        item4.setIid("-LJFJtgm19lXSZfphIMA");
        item4.setTitle("Downsizing");
        item4.setGenre("Movie");
        item4.setDetails("A kindly occupational therapist undergoes a new procedure to be shrunken to four inches tall so that he and his wife can help save the planet and afford a nice lifestyle at the same time.");
        item4.setPosterPath("/uLlmtN33rMuimRq6bu0OoNzCGGs.jpg");
        item4.setBackdropPath("/rtr0l61RxLPkmZHbZj0WFlGx5G5.jpg");
        item4.setReleaseDate("2017-12-22");
        item4.setMovieId("301337");

        ArrayList dummyItems = new ArrayList();

        dummyItems.add(item1);
        dummyItems.add(item2);
        dummyItems.add(item3);
        dummyItems.add(item4);

        return dummyItems;
    }

    public static ArrayList<Item> dummyTVRecItems() {
        Item item1 = new Item();
        item1.setIid("-LJEq-oADC98ayEqqew7");
        item1.setTitle("Phineas and Ferb");
        item1.setGenre("TV");
        item1.setDetails("Each day, two kindhearted suburban stepbrothers on summer vacation embark on some grand new project, which annoys their controlling sister, Candace, who tries to bust them. Meanwhile, their pet platypus plots against evil Dr. Doofenshmirtz.");
        item1.setPosterPath("/t46F4ExX6nYmNT8m6aGCLcBNLk4.jpg");
        item1.setBackdropPath("/5ZYWZmmOY7GV5RX77dSIpvnfPY1.jpg");
        item1.setMovieId("1877");
        item1.setReleaseDate("2007-08-17");

        Item item2 = new Item();
        item2.setIid("-LJEq-i9WzO5vFMc10xa");
        item2.setTitle("Dexter");
        item2.setGenre("TV");
        item2.setDetails("Dexter is an American television drama series. The series centers on Dexter Morgan, a blood spatter pattern analyst for 'Miami Metro Police Department' who also leads a secret life as a serial killer, hunting down criminals who have slipped through the cracks of justice.");
        item2.setPosterPath("/ydmfheI5cJ4NrgcupDEwk8I8y5q.jpg");
        item2.setBackdropPath("/5m05BIoMHgTd4zvJ5OBh7gZFGWV.jpg");
        item2.setMovieId("1405");
        item2.setReleaseDate("2006-10-01");

        Item item3 = new Item();
        item3.setIid("-LJEq-okEsr3iJ3BEYHs");
        item3.setTitle("Big Little Lies");
        item3.setGenre("TV");
        item3.setDetails("Subversive, darkly comedic drama Big Little Lies tells the tale of three mothers of first graders whose apparently perfect lives unravel to the point of murder.");
        item3.setPosterPath("/6nxTO2tYDBR9twPWlDC3I1eXUnY.jpg");
        item3.setBackdropPath("/we0Z9qjBN0rmQqqhzxonKZjQJak.jpg");
        item3.setMovieId("66292");
        item3.setReleaseDate("2017-02-19");

        Item item4 = new Item();
        item4.setIid("-LJEq-qIncYXWrCoEDm_");
        item4.setTitle("Fullmetal Alchemist");
        item4.setGenre("TV");
        item4.setDetails("Edward and Alphonse Elric are two brothers gifted with the ability of alchemy, the science of taking one thing and changing it into another. However, alchemy works on the theory of Equivalent Exchange -- for something to be created, something else of equal value must be sacrificed. When their mother dies, Edward decides to do the unthinkable -- bringing her back to life by breaking one of Alchemy's biggest taboos and performing Human Alchemy. Thinking they have nothing more to lose, he and Alphonse make their attempt -- but something goes horribly wrong. In the process, Alphonse loses his body and Edward loses his leg. Ed manages to save Al by attaching his spirit to a suit of armor, but at the cost of his arm and leg.");
        item4.setPosterPath("/sNpDtjUVNrTIVM1OR4EfEnRi4OH.jpg");
        item4.setBackdropPath("/dshSjE7LZRR7CK58hLDhOz5ANFe.jpg");
        item4.setMovieId("37863");
        item4.setReleaseDate("2003-10-04");

        ArrayList dummyItems = new ArrayList();

        dummyItems.add(item1);
        dummyItems.add(item2);
        dummyItems.add(item3);
        dummyItems.add(item4);

        return dummyItems;
    }

    public static ArrayList<Item> dummyBookRecItems() {
        Item item1 = new Item();
        item1.setIid("-LJjcLrNLHOwudLzeFmw");
        item1.setTitle("The Tsar of Love and Techno");
        item1.setGenre("Book");
        item1.setDetails("From the New York Times bestselling author of A Constellation of Vital Phenomena—dazzling, poignant, and lyrical interwoven stories about family, sacrifice, the legacy of war, and the redemptive power of art.<br /><br />This stunning, exquisitely written collection introduces a cast of remarkable characters whose lives intersect in ways both life-affirming and heartbreaking. A 1930s Soviet censor painstakingly corrects offending photographs, deep underneath Leningrad, bewitched by the image of a disgraced prima ballerina. A chorus of women recount their stories and those of their grandmothers, former gulag prisoners who settled their Siberian mining town. Two pairs of brothers share a fierce, protective love. Young men across the former USSR face violence at home and in the military. And great sacrifices are made in the name of an oil landscape unremarkable except for the almost incomprehensibly peaceful past it depicts. In stunning prose, with rich character portraits and a sense of history reverberating into the present, The Tsar of Love and Techno is a captivating work from one of our greatest new talents.");
        item1.setPubYear("2015");
        item1.setPubMonth("10");
        item1.setPubDay("6");
        item1.setImgUrl("https://images.gr-assets.com/books/1428086934m/23995336.jpg");
        item1.setSmallImgUrl("https://images.gr-assets.com/books/1428086934s/23995336.jpg");
        item1.setAuthor("Anthony Marra");
        item1.setBookId("23995336");
        item1.setAverageRating((float) 4.28000020980835);


        Item item2 = new Item();
        item2.setIid("-LJ_4hAl3aDU9PAtMnlC");
        item2.setTitle("Green Eggs and Ham");
        item2.setGenre("Book");
        item2.setDetails("“Do you like green eggs and ham?” asks Sam-I-am in this Beginner Book by Dr. Seuss. In a house or with a mouse? In a boat or with a goat? On a train or in a tree? Sam keeps asking persistently. With unmistakable characters and signature rhymes, Dr. Seuss’s beloved favorite has cemented its place as a children’s classic. In this most famous of cumulative tales, the list of places to enjoy green eggs and ham, and friends to enjoy them with, gets longer and longer. Follow Sam-I-am as he insists that this unusual treat is indeed a delectable snack to be savored everywhere and in every way. <br /><br />Originally created by Dr. Seuss, Beginner Books encourage children to read all by themselves, with simple words and illustrations that give clues to their meaning.");
        item2.setAuthor("Dr. Seuss");
        item2.setSmallImgUrl("https://images.gr-assets.com/books/1468680100s/23772.jpg");
        item2.setImgUrl("https://images.gr-assets.com/books/1468680100m/23772.jpg");
        item2.setPubDay("\n");
        item2.setPubMonth("6");
        item2.setPubYear("1960");
        item2.setBookId("23772");


        Item item3 = new Item();
        item3.setIid("-LJoxHNVdY7yf-zy5rlO");
        item3.setTitle("Stardust");
        item3.setGenre("Book");
        item3.setDetails("Life moves at a leisurely pace in the tiny town of Wall—named after the imposing stone barrier which separates the town from a grassy meadow. Here, young Tristran Thorn has lost his heart to the beautiful Victoria Forester and for the coveted prize of her hand, Tristran vows to retrieve a fallen star and deliver it to his beloved. It is an oath that sends him over the ancient wall and into a world that is dangerous and strange beyond imagining...");
        item3.setBookId("16793");
        item3.setAverageRating((float) 4.07);
        item3.setAuthor("Neil Gaiman");
        item3.setSmallImgUrl("https://images.gr-assets.com/books/1459127484s/16793.jpg");
        item3.setImgUrl("https://images.gr-assets.com/books/1459127484m/16793.jpg");
        item3.setPubDay("1");
        item3.setPubMonth("10");
        item3.setPubYear("1998");

        Item item4 = new Item();
        item4.setIid("-LJoxM6dmNV65jSvxAUt");
        item4.setTitle("The Giving Tree");
        item4.setGenre("Book");
        item4.setDetails("“Once there was a tree...and she loved a little boy.” So begins a story of unforgettable perception, beautifully written and illustrated by the gifted and versatile Shel Silverstein. Every day the boy would come to the tree to eat her apples, swing from her branches, or slide down her trunk...and the tree was happy. But as the boy grew older he began to want more from the tree, and the tree gave and gave and gave. This is a tender story, touched with sadness, aglow with consolation. Shel Silverstein has created a moving parable for readers of all ages that offers an affecting interpretation of the gift of giving and a serene acceptance of another's capacity to love in return.");
        item4.setImgUrl("https://images.gr-assets.com/books/1174210942m/370493.jpg");
        item4.setSmallImgUrl("https://images.gr-assets.com/books/1174210942s/370493.jpg");
        item4.setPubMonth("\n");
        item4.setPubYear("1964");
        item4.setPubDay("\n");
        item4.setAuthor("Shel Silverstein");
        item4.setAverageRating((float) 4.369999);
        item4.setBookId("370493");


        ArrayList dummyItems = new ArrayList();

        dummyItems.add(item1);
        dummyItems.add(item2);
        dummyItems.add(item3);
        dummyItems.add(item4);

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
            //((MyLibraryActivity)getActivity()).hideProgressBar();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    bar.dismiss();
                    swipeContainer.setRefreshing(false);
                }
            }, 3000);
        }
    }

    class populateasync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            //((MyLibraryActivity)getActivity()).showProgressBar();

            if (!swipeContainer.isRefreshing()) {
                bar = Snackbar.make(getView(), "Loading", Snackbar.LENGTH_INDEFINITE);
                ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(android.support.design.R.id.snackbar_text).getParent();
                ProgressBar item = new ProgressBar(getContext());
                contentLay.addView(item);
                bar.show();
            }
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
                //empty
            }
        });
    }

    public static void getmovies(DatabaseReference Recs) {
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
                    item.setReleaseDate(postSnapshot.child("releaseDate").getValue().toString());

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
                if (movieItems.size() < 4) {
                    int size = movieItems.size();
                    Log.i("size", String.format("%s", movieItems.size()));
                    for (int i = 0; i < 4 - size; i++) {
                        movieItems.add(dummyMovieRecItems().get(i));
                        Log.i("adding", String.format("%s",i));
                    }
                }
                movieRecAdapter = new RecAdapter(movieItems);
                rv_movies.setAdapter(movieRecAdapter);
                //((MyLibraryActivity)getActivity()).hideProgressBar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //empty
            }
        });
    }

    public static void getshows(DatabaseReference Recs) {
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

                if (tvItems.size() < 4) {
                    int size = tvItems.size();
                    for (int i = 0; i < 4 - size; i++) {
                        tvItems.add(dummyTVRecItems().get(i));
                    }
                }

                tvRecAdapter = new RecAdapter(tvItems);
                rv_tvShows.setAdapter(tvRecAdapter);
                //((MyLibraryActivity)getActivity()).hideProgressBar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //empty
            }
        });
    }

    public static void getbooks(DatabaseReference Recs) {
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

                    if (postSnapshot.child("details").getValue() != null && TextUtils.getTrimmedLength(postSnapshot.child("details").getValue().toString()) > 0) {
                        item.setDetails(html2text(postSnapshot.child("details").getValue().toString()));
                    }

                    item.setBookId(postSnapshot.child("bookId").getValue().toString());
                    item.setSmallImgUrl(postSnapshot.child("smallImgUrl").getValue().toString());
                    item.setImgUrl(postSnapshot.child("imgUrl").getValue().toString());
                    item.setAuthor(postSnapshot.child("author").getValue().toString());
                    item.setPubYear(postSnapshot.child("pubYear").getValue().toString());
                    item.setAverageRating(Float.valueOf(postSnapshot.child("averageRating").getValue().toString()));

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

                if (bookItems.size() < 4) {
                    int size = bookItems.size();
                    for (int i = 0; i < 4 - size; i++) {
                        bookItems.add(dummyBookRecItems().get(i));
                    }
                }

                bookRecAdapter = new RecAdapter(bookItems);
                rv_books.setAdapter(bookRecAdapter);
                //((MyLibraryActivity)getActivity()).hideProgressBar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //empty
            }
        });
    }
}