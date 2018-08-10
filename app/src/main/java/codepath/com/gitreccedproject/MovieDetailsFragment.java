package codepath.com.gitreccedproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class MovieDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1, mParam2;

    private TextView tvMovieTitle, director, overview, releaseDate;
    private ImageView backdrop;
    private AsyncHttpClient detailsClient;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static MovieDetailsFragment newInstance(String param1, String param2) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        String imageUrl = "https://image.tmdb.org/t/p/w342" + ((DetailsActivity)getActivity()).item.getBackdropPath();

        detailsClient = new AsyncHttpClient();
        String movieDetailsUrl = "https://api.themoviedb.org/3/movie/" + ((DetailsActivity)getActivity()).item.getMovieId() + "/credits";
        RequestParams movieDetailsParams = new RequestParams();
        movieDetailsParams.put("api_key", getContext().getString(R.string.movieApiKey));

        tvMovieTitle = view.findViewById(R.id.tvMovieTitle);
        director = view.findViewById(R.id.tvDirector);
        releaseDate = view.findViewById(R.id.tvReleaseDate);
        overview = view.findViewById(R.id.tvMovieOverview);
        backdrop = view.findViewById(R.id.ivMovieBackdrop);

        tvMovieTitle.setText(((DetailsActivity)getActivity()).item.getTitle());
        //director.setText(((DetailsActivity)getActivity()).item.getDirector());
        releaseDate.setText(((DetailsActivity)getActivity()).item.getReleaseDate());
        overview.setText(((DetailsActivity)getActivity()).item.getDetails());

        Glide.with(getContext())
                .load(imageUrl)
                .into(backdrop);
        detailsClient.get(movieDetailsUrl, movieDetailsParams, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("SearchAdapter", "SUCCESS: received response");
                try {
                    JSONArray crew = response.getJSONArray("crew");
                    for (int i = 0; i < crew.length(); i++) {
                        String job = crew.getJSONObject(i).get("job").toString();
                        if (job.equalsIgnoreCase("Director")) {
                            director.setText(crew.getJSONObject(i).get("name").toString());
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("SearchAdapter", "FAILURE. Response String: " + responseString);
            }
        });
    }
}
