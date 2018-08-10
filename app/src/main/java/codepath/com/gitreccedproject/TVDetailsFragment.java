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

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class TVDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private TextView tvTVTitle, seasons, firstAired, overview;
    private ImageView backdrop;
    AsyncHttpClient detailsClient;


    //private OnFragmentInteractionListener mListener;

    public TVDetailsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TVDetailsFragment newInstance(String param1, String param2) {
        TVDetailsFragment fragment = new TVDetailsFragment();
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
        return inflater.inflate(R.layout.fragment_tv_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        String imageUrl = "https://image.tmdb.org/t/p/w342" + ((DetailsActivity)getActivity()).item.getBackdropPath();

        detailsClient = new AsyncHttpClient();
        String tvDetailsUrl = "https://api.themoviedb.org/3/tv/" + ((DetailsActivity)getActivity()).item.getMovieId();
        RequestParams tvDetailsParams = new RequestParams();
        tvDetailsParams.put("api_key", getContext().getString(R.string.movieApiKey));

        tvTVTitle = view.findViewById(R.id.tvShowTitle);
        seasons = view.findViewById(R.id.tvSeasons);
        firstAired = view.findViewById(R.id.tvFirstAired);
        overview = view.findViewById(R.id.tvShowOverview);
        backdrop = view.findViewById(R.id.ivTVBackdrop);

        tvTVTitle.setText(((DetailsActivity)getActivity()).item.getTitle());
        firstAired.setText(((DetailsActivity)getActivity()).item.getFirstAirDate());
        overview.setText(((DetailsActivity)getActivity()).item.getDetails());

        Glide.with(getContext())
                .load(imageUrl)
                .into(backdrop);

        detailsClient.get(tvDetailsUrl, tvDetailsParams, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("SearchAdapter", "SUCCESS: received response");
                try {
                    seasons.setText(response.getString("number_of_seasons"));
                    firstAired.setText(response.getString("first_air_date"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("SearchAdapter", "FAILURE. Response String: " + responseString);
            }
        });

        detailsClient = new AsyncHttpClient();
        tvDetailsUrl = "https://api.themoviedb.org/3/tv/" + ((DetailsActivity)getActivity()).item.getMovieId() + "/credits";
    }

}
