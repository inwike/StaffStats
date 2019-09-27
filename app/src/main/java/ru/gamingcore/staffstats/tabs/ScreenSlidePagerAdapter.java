package ru.gamingcore.staffstats.tabs;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import ru.gamingcore.staffstats.json.Emp_rating;
import ru.gamingcore.staffstats.utils.Polygon;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private DialogFragment[] fragments = new DialogFragment[2];

    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
        fragments[0] = new SkillsTab();
        fragments[1] = new SkillsTab2();
    }

    public void updateSkills(Emp_rating emp_rating) {
        Polygon p = new Polygon(emp_rating.getCurrent());
        ((SkillsTab)fragments[0]).graphs.add(p);
        p = new Polygon(emp_rating.getAvr());
        ((SkillsTab)fragments[0]).graphs.add(p);


        ((SkillsTab2)fragments[1]).emp_rating = emp_rating;


        ((SkillsTab)fragments[0]).drawPolygon();
        ((SkillsTab)fragments[1]).drawPolygon();

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