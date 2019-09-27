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
        fragments[1] = new SkillsTab();
    }

    public void updateSkills(Emp_rating emp_rating) {
        double[] skills = new double[6];
        skills[0] = Double.valueOf(emp_rating.knld[11]);
        skills[1] = Double.valueOf(emp_rating.soc[11]);
        skills[2] = Double.valueOf(emp_rating.resp[11]);
        skills[3] = Double.valueOf(emp_rating.activ[11]);
        skills[4] = Double.valueOf(emp_rating.innov[11]);
        skills[5] = Double.valueOf(emp_rating.ent[11]);

        Polygon p = new Polygon(skills);
        ((SkillsTab)fragments[0]).graphs.add(p);

        skills = new double[6];
        skills[0] = Double.valueOf(emp_rating.avr_knld);
        skills[1] = Double.valueOf(emp_rating.avr_soc);
        skills[2] = Double.valueOf(emp_rating.avr_resp);
        skills[3] = Double.valueOf(emp_rating.avr_activ);
        skills[4] = Double.valueOf(emp_rating.avr_innov);
        skills[5] = Double.valueOf(emp_rating.avr_ent);
         p = new Polygon(skills);
        ((SkillsTab)fragments[0]).graphs.add(p);


        ((SkillsTab)fragments[0]).drawPolygon();
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