package codepath.com.gitreccedproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DescriptionDialog extends DialogFragment {
    TextView tvDescription;
    TextView btnOk;

    final int INPUT_RECS_ACTIVITY = 0;
    final int INPUT_RECS_ACTIVITY_SELECT = 1;
    final int RECS_FRAGMENT = 2;
    final int LIB_FRAGMENT = 3;
    final int LIB_FRAGMENT_SELECT = 4;
    final int MENU_FRAGMENT = 5;

    final String inputRecsActivityDescription = "On this screen, search up any movies, TV shows, and books to add to your library. \n\nPress and hold on " +
            "each item in order to view more information. Our recommendation algorithm will use this media in order to suggest new media for you. \n\nHappy adding!";
    final String inputRecsActivitySelectDescription = "Once you search up items, you can tap to add and remove them from your library. Press and hold on each item to view more information. Click \"OK\" to begin adding media!";
    final String recsFragmentDescription = "This screen will display your recommendations, based on your library preferences. You can pull to refresh your recommendations, and click on the item cards to learn more about each one.";
    final String libFragmentDescription = "This screen will display movies, TV shows, and books that you have added. When you click on the \"+\" button in the top right corner, you can add media that will then show up in your library.";
    final String libFragmentSelectDescription = "To learn more about each item, click on the item cards. To delete items from your library, just press and hold on the item you want to delete.";
    final String menuFragmentDescription = "Finally, to log out or access your settings, click on the menu icon at the top left corner on the screen. \n Enjoy Getting Recced!";


    private Activity mActivity;
    private int mCode;

    public DescriptionDialog () {
        //empty constructor
    }

    @SuppressLint("ValidFragment")
    public DescriptionDialog(Activity activity, int code) {
        mActivity = activity;
        mCode = code;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.description_dialog, null);

        //find views by id
        tvDescription = view.findViewById(R.id.tvDescription);
        btnOk = view.findViewById(R.id.btnOk);

        switch (mCode) {
            case INPUT_RECS_ACTIVITY:
                tvDescription.setText(inputRecsActivityDescription);
                btnOk.setText("Begin Adding Media");

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();

                        mCode = INPUT_RECS_ACTIVITY_SELECT;
                    }
                });

                break;
            case INPUT_RECS_ACTIVITY_SELECT:
                tvDescription.setText(inputRecsActivitySelectDescription);
                btnOk.setText("Ok");
                btnOk.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                break;
            case RECS_FRAGMENT:
                tvDescription.setText(recsFragmentDescription);
                btnOk.setText("Next");
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
                break;
            case LIB_FRAGMENT:
                break;
            case LIB_FRAGMENT_SELECT:
                break;
            case MENU_FRAGMENT:
                break;
        }

        return view;
    }

    DescriptionDialog.OnDescriptionDialogResult mDialogResult;
    public void setDialogResult(DescriptionDialog.OnDescriptionDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnDescriptionDialogResult {
        void finish(String result);
    }
}
