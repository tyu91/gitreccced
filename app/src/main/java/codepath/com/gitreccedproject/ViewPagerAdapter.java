package codepath.com.gitreccedproject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class ViewPagerAdapter extends FragmentPagerAdapter {

    private String title[] = {"Recommendations","Library"};

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            MyLibraryActivity.isVisitedLib = true;
            return LibraryFragment.getInstance(position);
        }
        MyLibraryActivity.isVisitedRecs = true;
        return RecsFragment.getInstance(position);
    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}