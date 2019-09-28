package ru.gamingcore.staffstats.tabs;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

import ru.gamingcore.staffstats.R;
import ru.gamingcore.staffstats.adapter.AvailAdapter;
import ru.gamingcore.staffstats.adapter.HelpAdapter;
import ru.gamingcore.staffstats.utils.Polygon;

public class SkillsTab extends DialogFragment {

    public List<Polygon> graphs = new ArrayList<>();
    private String[] header = {"По организации"};
    private int[] colors = new int[2];//blue, red
    private Bitmap up;
    private Bitmap down;
    private ImageView imageView;
    private TextView logo1;
    private TextView logo2;
    private ListView listView;
    public HelpAdapter adapterDetail;
    public AvailAdapter adapterAvail;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.about_page, container, false);

        imageView = v.findViewById(R.id.android2);
        logo1 = v.findViewById(R.id.logo1);
        logo2 = v.findViewById(R.id.logo2);
        listView = v.findViewById(R.id.lv);
        colors[0] = getResources().getColor(R.color.skill_1);
        colors[1] = getResources().getColor(R.color.skill_2);
        drawPolygon();
        adapterDetail = new HelpAdapter(getContext());
        listView.setAdapter(adapterDetail);

        return v;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
        up = BitmapFactory.decodeResource(getResources(), R.drawable.up);
        down = BitmapFactory.decodeResource(getResources(), R.drawable.down);

        up = Bitmap.createScaledBitmap(
                up, 10, 15, false);

        down = Bitmap.createScaledBitmap(
                down, 10, 15, false);
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

    public void drawPolygon() {
        int size = graphs.size() + 1;
        logo1.setText(header[0]);
        logo2.setText("");
        Drawable[] layers = new Drawable[size];
        layers[0] = getResources().getDrawable(R.drawable.skills, null);
        if (size > 1) {
            Polygon p = graphs.get(0);
            Polygon p2 = graphs.get(1);

            p.up = up;
            p2.up = up;

            p.down = down;
            p2.down = down;

            layers[1] = new BitmapDrawable(getResources(), p.getBitmap(colors[0]));
            layers[2] = new BitmapDrawable(getResources(), p2.getBitmap(colors[1]));
        }

        LayerDrawable layerDrawable = new LayerDrawable(layers);

        if (imageView != null)
            imageView.setImageDrawable(layerDrawable);
    }


}