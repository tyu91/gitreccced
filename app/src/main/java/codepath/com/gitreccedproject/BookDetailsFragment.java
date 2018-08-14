package codepath.com.gitreccedproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class BookDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private TextView tvBookTitle, author, pub, overview;
    private RatingBar rbRating;
    private ImageView cover;


    //private OnFragmentInteractionListener mListener;

    public BookDetailsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static BookDetailsFragment newInstance(String param1, String param2) {
        BookDetailsFragment fragment = new BookDetailsFragment();
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
        return inflater.inflate(R.layout.fragment_book_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        String imageUrl = ((DetailsActivity)getActivity()).item.getImgUrl();

        tvBookTitle = view.findViewById(R.id.tvBookTitle);
        author = view.findViewById(R.id.tvAuthor);
        pub = view.findViewById(R.id.tvPubDate);
        overview = view.findViewById(R.id.tvBookOverview);
        cover = view.findViewById(R.id.ivBookCover);
        rbRating = view.findViewById(R.id.rbRating);

        if (((DetailsActivity)getActivity()).item.getTitle() != null) {
            tvBookTitle.setText(((DetailsActivity)getActivity()).item.getTitle());
        } else {
            tvBookTitle.setText("");
        }

        if (((DetailsActivity)getActivity()).item.getAuthor() != null) {
            author.setText(((DetailsActivity)getActivity()).item.getAuthor());
        } else {
            author.setText("");
        }

        if (((DetailsActivity)getActivity()).item.getPubYear() != null) {
            pub.setText("Published: " + ((DetailsActivity)getActivity()).item.getPubYear());
        } else {
            pub.setText("");
        }

        if (((DetailsActivity)getActivity()).item.getDetails() != null) {
            overview.setText(LibraryFragment.html2text(((DetailsActivity)getActivity()).item.getDetails()));
        } else {
            overview.setText("");
        }


        //vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = ((DetailsActivity)getActivity()).item.getAverageRating();
        rbRating.setRating(voteAverage > 0 ? voteAverage : 4.0f);

        Glide.with(getContext())
                .load(imageUrl)
                .into(cover);
    }
}
