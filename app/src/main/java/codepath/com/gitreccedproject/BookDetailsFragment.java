package codepath.com.gitreccedproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        tvBookTitle.setText(((DetailsActivity)getActivity()).item.getTitle());
        author.setText(((DetailsActivity)getActivity()).item.getAuthor());
        pub.setText(((DetailsActivity)getActivity()).item.getPubYear());
        overview.setText(((DetailsActivity)getActivity()).item.getDetails());

        Glide.with(getContext())
                .load(imageUrl)
                .into(cover);
    }
}
