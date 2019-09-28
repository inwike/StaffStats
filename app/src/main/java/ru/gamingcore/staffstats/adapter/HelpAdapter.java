package ru.gamingcore.staffstats.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.gamingcore.staffstats.R;
import ru.gamingcore.staffstats.json.Detail;


public class HelpAdapter extends BaseAdapter {
    public List<Detail> details = new ArrayList<>();
    public LayoutInflater lInflater;

    public HelpAdapter(Context context) {
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return details.size();
    }

    @Override
    public Object getItem(int i) {
        return details.get(i);
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
            view = lInflater.inflate(R.layout.item, parent, false);
        }
        Detail p = details.get(position);
        ((TextView) view.findViewById(R.id.name)).setText(p.name);
        ((TextView) view.findViewById(R.id.value)).setText(p.value);
        return view;
    }
}
