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

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        RecsFragment recsFragment = new RecsFragment();
        recsFragment.setArguments(bundle);
        return recsFragment;
    }

    public RecyclerView rv_movies;
    public RecyclerView.LayoutManager RecyclerViewLayoutManager;
    public RecAdapter recAdapter;
    public LinearLayoutManager HorizontalLayout ;
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
        rv_movies = view.findViewById(R.id.rv_movies);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_movies.setLayoutManager(linearLayoutManager);
        // set the adapter
        rv_movies.setAdapter(recAdapter);

        //TODO - change this to get actual data
        for (int i = 0; i < 5; i++) {
            Item item = null;items.add(item);
            recAdapter.notifyItemInserted(items.size() - 1);
        }
    }
}