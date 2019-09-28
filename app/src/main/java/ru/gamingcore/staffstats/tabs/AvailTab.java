package ru.gamingcore.staffstats.tabs;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import ru.gamingcore.staffstats.MyService;
import ru.gamingcore.staffstats.R;
import ru.gamingcore.staffstats.adapter.AvailAdapter;
import ru.gamingcore.staffstats.adapter.HelpAdapter;
import ru.gamingcore.staffstats.json.Avail;
import ru.gamingcore.staffstats.utils.Polygon;

public class AvailTab extends DialogFragment {
    public AvailAdapter adapter;
    private ExpandableListView listView;
    List<Avail> avails;
    private PieChart pieChart ;
    private List<PieEntry> entries ;
    private PieDataSet pieDataSet ;
    private PieData pieData ;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.avail_page, container, false);
        View ads = v.findViewById(R.id.ads);
        ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://hibrain.ru/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        listView = v.findViewById(R.id.lv);
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,int groupPosition, int childPosition, long id) {
                String obj = adapter.getChildUrl(groupPosition,childPosition);
                Uri uri = Uri.parse(obj);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;  // i missed this
            }
        });

        entries = new ArrayList<>();
        pieChart = v.findViewById(R.id.chart1);
        adapter = new AvailAdapter(getContext());
        listView.setAdapter(adapter);
        update();
        return v;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }


    @Override
    public void onResume() {
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                window.setAttributes(params);
                window.getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                );
            }
        }
        super.onResume();
    }

    public void update() {
        entries.clear();

        for(int i = 0; i< avails.size();i++) {
            entries.add(new PieEntry(33.3f, i));
        }

        if (adapter != null) {
            adapter.values = avails;
            adapter.notifyDataSetChanged();
        }

        if (avails.size() > 0) {
            pieDataSet = new PieDataSet(entries, "");
            pieData = new PieData(pieDataSet);
            pieDataSet.setColors(getContext().getResources().getColor(R.color.color_3),
                    getContext().getResources().getColor(R.color.color_4),
                    getContext().getResources().getColor(R.color.color_5));

            if(pieChart != null)
                pieChart.setData(pieData);
        }
    }

    public void AddValuesToPIEENTRY(List<Avail> avails){
        this. avails = avails;
        update();
    }

}