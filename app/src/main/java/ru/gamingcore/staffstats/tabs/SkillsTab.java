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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

import ru.gamingcore.staffstats.R;
import ru.gamingcore.staffstats.utils.Polygon;

public class SkillsTab extends DialogFragment implements View.OnTouchListener {

    private String[] header = {"Мои показатели", "Рост показателей", "Сравнение с общими"};
    private int[] colors = new int[2];//blue, red
    private Bitmap up;
    private Bitmap down;
    private RelativeLayout InfoMain;
    public List<Polygon> graphs = new ArrayList<>();

    private ImageView imageView;
    private TextView textView;

    private int currentBlockId = 0;

    private boolean animating = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.about_page, container, false);

        InfoMain = v.findViewById(R.id.about);
        InfoMain.setOnTouchListener(this);
        imageView = v.findViewById(R.id.android2);
        textView = v.findViewById(R.id.logo);
        colors[0] = getResources().getColor(R.color.skill_1);
        colors[1] = getResources().getColor(R.color.skill_2);
        drawPolygon();
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

    private void switchBlock() {
        if (animating)
            return;

        animating = true;

        if (currentBlockId < graphs.size() - 1) {
            currentBlockId++;
        } else {
            currentBlockId = 0;
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

    public void drawPolygon() {
        int size = graphs.size() + 1;
        textView.setText(header[currentBlockId]);
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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        // if (!graphs.get(currentBlockId).onTouch(view,motionEvent)) {
        //   switchBlock();
        // }
        return false;
    }
}