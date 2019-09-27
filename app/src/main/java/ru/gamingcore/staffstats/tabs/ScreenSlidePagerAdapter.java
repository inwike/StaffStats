package ru.gamingcore.staffstats.tabs;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private DialogFragment[] fragments = new DialogFragment[2];

    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        fragments[0] = new AboutTab();
        fragments[1] = new AboutTab();
    }

    @NonNull
    @Override
    public DialogFragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}