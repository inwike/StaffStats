package ru.gamingcore.staffstats.tabs;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.gamingcore.staffstats.R;
import ru.gamingcore.staffstats.adapter.HelpAdapter;
import ru.gamingcore.staffstats.json.Detail;
import ru.gamingcore.staffstats.json.Emp_rating;
import ru.gamingcore.staffstats.utils.Polygon;

public class SkillsTab2 extends DialogFragment implements View.OnClickListener {
    public Emp_rating emp_rating = new Emp_rating();
    private int[] colors = new int[2];//blue, red
    public Map<String, List<Detail>> details = new HashMap<>();
    public HelpAdapter adapterDetail  ;
    private ListView listView;
    private BottomSheetBehavior sheetBehavior;
    private String[] help = {"knld", "soc", "resp", "activ", "innov", "ent"};
    private ImageView imageView;

    private Bitmap up;
    private Bitmap down;
    private RelativeLayout InfoMain;

    private TextView logo1;
    private TextView logo2;


    private int currentBlockId = 10;
    private TextView[] view = new TextView[6];

    private boolean animating = false;

    public void drawYellow(int pos) {
        for (int i = 0; i < 6; i++) {
            view[i].setTextColor(0xFFFFFACD);
            if (i == pos) {
                view[i].setTextColor(0xFFFFD700);
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.about_page2, container, false);

        InfoMain = v.findViewById(R.id.about);
        InfoMain.setOnClickListener(this);
        colors[0] = getResources().getColor(R.color.skill_1);
        colors[1] = getResources().getColor(R.color.skill_2);
        imageView = v.findViewById(R.id.android2);
        logo1 = v.findViewById(R.id.logo1);

        view[0] = v.findViewById(R.id.hint_1);
        view[1] = v.findViewById(R.id.hint_2);
        view[2] = v.findViewById(R.id.hint_3);
        view[3] = v.findViewById(R.id.hint_4);
        view[4] = v.findViewById(R.id.hint_5);
        view[5] = v.findViewById(R.id.hint_6);
        drawYellow(-1);
        logo2 = v.findViewById(R.id.logo2);
        drawPolygon();

        adapterDetail = new HelpAdapter(getContext());
        listView = v.findViewById(R.id.lv);
        listView.setAdapter(adapterDetail);
        RelativeLayout linearLayout = v.findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(linearLayout);
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull final View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.e("err", "STATE_HIDDEN");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.e("err", "STATE_EXPANDED");
                        for (int i = 0; i < 6; i++) {
                            final int t = i;
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.setMargins(0, 25, 0, 0);
                            view[t].setLayoutParams(params);
                        }
                        drawYellow(0);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.e("err", "STATE_COLLAPSED");
                        for (int i = 0; i < 6; i++) {
                            final int t = i;
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.setMargins(0, 0, 0, 0);
                            view[t].setLayoutParams(params);
                        }
                        drawYellow(-1);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.e("err", "STATE_DRAGGING");

                        for (int i = 0; i < 6; i++) {
                            final int t = i;
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.setMargins(0, 0, 0, 0);
                            view[t].setLayoutParams(params);
                        }
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.e("err", "STATE_SETTLING");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        for (int i = 0; i < 6; i++) {
            final int t = i;
            view[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        adapterDetail.Update(help[t]);
                        drawYellow(t);
                        adapterDetail.notifyDataSetChanged();
                    }
                }
            });
        }
        adapterDetail.details = details;
        adapterDetail.Update("knld");
        Log.e("TAG", "details" + details.size());
        adapterDetail.notifyDataSetChanged();

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

    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
    }

    private void switchBlock() {
        if (animating)
            return;

        animating = true;
        if (currentBlockId == 0) {
            currentBlockId = 10;
        } else {
            currentBlockId--;
        }

        if (InfoMain != null) {
            ObjectAnimator animation =
                    ObjectAnimator.ofFloat(InfoMain, View.ROTATION_Y, 0f, 90f);

            animation.setDuration(500);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());

            animation.addListener(new AnimatorListener() {
                                      @Override
                                      public void onAnimationStart(Animator animation) {
                                      }

                                      @Override
                                      public void onAnimationEnd(Animator animation) {
                                          ObjectAnimator animation2 = ObjectAnimator.ofFloat(InfoMain, View.ROTATION_Y, 270f, 360f);
                                          animation2.setDuration(500);
                                          drawPolygon();
                                          animation2.setInterpolator(new AccelerateDecelerateInterpolator());
                                          animation2.addListener(new AnimatorListener() {
                                              @Override
                                              public void onAnimationStart(Animator animation) {
                                              }

                                              @Override
                                              public void onAnimationEnd(Animator animation) {
                                                  animating = false;
                                                  drawPolygon();
                                              }

                                              @Override
                                              public void onAnimationCancel(Animator animation) {
                                              }

                                              @Override
                                              public void onAnimationRepeat(Animator animation) {
                                              }
                                          });
                                          animation2.start();
                                      }

                                      @Override
                                      public void onAnimationCancel(Animator animation) {
                                      }

                                      @Override
                                      public void onAnimationRepeat(Animator animation) {
                                      }
                                  }

            );
            animation.start();
        }
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

    void drawPolygon() {
        if (InfoMain == null)
            return;

        int size = 1;
        if (emp_rating.getSize() > 1) {
            size = 3;
        }

        Drawable[] layers = new Drawable[size];
        layers[size - 1] = getResources().getDrawable(R.drawable.skills, null);
        if (size > 1) {
            logo1.setText(String.format("%s / ", emp_rating.getMonth(11)));
            logo2.setText(emp_rating.getMonth(currentBlockId));
            Polygon p = new Polygon(emp_rating.getCurrent());
            p.up = up;
            p.down = down;
            p.checkArrow(p, false);
            Polygon p2 = new Polygon(emp_rating.getByMonth(currentBlockId));
            p.checkArrow(p2, true);
            p2.up = up;
            p2.down = down;
            layers[1] = new BitmapDrawable(getResources(), p2.getBitmap(colors[1]));
            layers[0] = new BitmapDrawable(getResources(), p.getBitmap(colors[0]));
        }

        LayerDrawable layerDrawable = new LayerDrawable(layers);

        if (imageView != null)
            imageView.setImageDrawable(layerDrawable);
    }

    @Override
    public void onClick(View view) {
        switchBlock();
    }


    public void update(List<Detail> details) {
        this.details.put(details.get(0).id, details);

        if (adapterDetail != null) {
            adapterDetail.details = this.details;
            adapterDetail.Update("knld");
            adapterDetail.notifyDataSetChanged();
        }

    }
}