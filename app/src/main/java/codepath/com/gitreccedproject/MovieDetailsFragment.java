package codepath.com.gitreccedproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;


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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MovieDetailsFragment.
     */
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
        String tvDetailsUrl = "https://api.themoviedb.org/3/tv/" + ((DetailsActivity)getActivity()).item.getMovieId();
        RequestParams tvDetailsParams = new RequestParams();
        tvDetailsParams.put("api_key", getContext().getString(R.string.movieApiKey));

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

//        detailsClient.get(tvDetailsUrl, tvDetailsParams, new JsonHttpResponseHandler(){
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Log.i("SearchAdapter", "SUCCESS: received response");
//                try {
//                    releaseDate.setText(response.getString("release_date"));
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Log.i("SearchAdapter", "FAILURE. Response String: " + responseString);
//            }
//        });
    }

    /*// TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
         //TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
