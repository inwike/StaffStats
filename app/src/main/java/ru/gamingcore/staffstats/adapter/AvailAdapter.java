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

public class AvailAdapter extends BaseAdapter {
    public List<String> values = new ArrayList<>();
    public LayoutInflater lInflater;

    public AvailAdapter(Context context) {
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int i) {
        return values.get(i);
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
            view = lInflater.inflate(R.layout.item_avail, parent, false);
        }
        String p = values.get(position);
        ((TextView) view.findViewById(R.id.value)).setText(p);
        return view;
    }
}
