package ru.gamingcore.staffstats.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import ru.gamingcore.staffstats.R;
import ru.gamingcore.staffstats.json.Avail;

public class AvailAdapter extends BaseExpandableListAdapter {
    public List<Avail> values = new ArrayList<>();
    public LayoutInflater lInflater;

    public AvailAdapter(Context context) {
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return values.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return values.get(i).train.size();
    }

    @Override
    public Object getGroup(int i) {
        return values.get(i);
    }

    @Override
    public String getChild(int i, int i1) {
        return values.get(i).train.get(i1);
    }

    public String getChildUrl(int i, int i1) {
        return values.get(i).url.get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int position, boolean b, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_avail, parent, false);
        }
        Avail p = values.get(position);
        ((TextView) view.findViewById(R.id.value)).setText(p.name);
        return view;
    }

    @Override
    public View getChildView(int position, int i1, boolean b, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_avail_sub, parent, false);
        }
        String p = values.get(position).train.get(i1);
        ((TextView) view.findViewById(R.id.value)).setText(p);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        if (getChildrenCount(i) > 0) {
            return true;
        }
        return false;
    }
}
