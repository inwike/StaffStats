package ru.gamingcore.staffstats.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.gamingcore.staffstats.R;
import ru.gamingcore.staffstats.json.Detail;


public class HelpAdapter extends BaseAdapter {
    public Map<String,List<Detail>> details = new HashMap<>();
    public List<Detail> detailsView = new ArrayList<>();
    public LayoutInflater lInflater;


    public void Update() {
        detailsView = new ArrayList<>();
        Collection<List<Detail>> values = details.values();
        for (List<Detail> value : values) {
            Collections.sort(value, new Detail());
            detailsView.addAll(value);
        }
    }

    public HelpAdapter(Context context) {
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return detailsView.size();
    }

    @Override
    public Object getItem(int i) {
        return detailsView.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_help, parent, false);
        }

        Detail p = detailsView.get(position);
        ((TextView) view.findViewById(R.id.name)).setText(p.name);
        ((TextView) view.findViewById(R.id.value)).setText(p.value);
        return view;
    }
}
