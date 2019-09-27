package ru.gamingcore.staffstats.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import ru.gamingcore.staffstats.R;


public class LogoActivity extends AppCompatActivity implements ViewPropertyAnimatorListener {
    public static final int ANIM_ITEM_DURATION = 2000;
    public static final float ANIM_ITEM_SCALE_END = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        Window window  =  getWindow();
        if(window!= null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                WindowManager.LayoutParams attrib = window.getAttributes();
                if(attrib != null)
                    attrib.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            }
        }
        setContentView(R.layout.activity_splash);
        animate();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
            super.onWindowFocusChanged(hasFocus);
        }
    }

    private void animate() {
        View logo = findViewById(R.id.img_logo);

        ViewPropertyAnimatorCompat logoAnimator = ViewCompat.animate(logo);
        logoAnimator.scaleX(ANIM_ITEM_SCALE_END);
        logoAnimator.scaleY(ANIM_ITEM_SCALE_END);
        logoAnimator.translationY(-300).alpha(1);
        logoAnimator.setDuration(ANIM_ITEM_DURATION).setInterpolator(
                new FastOutSlowInInterpolator());
        logoAnimator.start();

        View text1 = findViewById(R.id.text1);
        ViewPropertyAnimatorCompat text1Animator = ViewCompat.animate(text1);
        text1Animator.scaleX(ANIM_ITEM_SCALE_END);
        text1Animator.scaleY(ANIM_ITEM_SCALE_END);
        text1Animator.translationY(200).alpha(1);
        text1Animator.setDuration(ANIM_ITEM_DURATION).setInterpolator(
                new FastOutSlowInInterpolator());
        text1Animator.start();

        View text2 = findViewById(R.id.text2);
        ViewPropertyAnimatorCompat text2Animator = ViewCompat.animate(text2);
        text2Animator.scaleX(ANIM_ITEM_SCALE_END);
        text2Animator.scaleY(ANIM_ITEM_SCALE_END);
        text2Animator.translationY(300).alpha(1);
        text2Animator.setDuration(ANIM_ITEM_DURATION).setInterpolator(
                new FastOutSlowInInterpolator());
        text2Animator.setListener(this);
        text2Animator.start();
    }

    @Override
    public void onAnimationStart(View view) {
    }

    @Override
    public void onAnimationEnd(View view) {
        Intent MainIntent = new Intent(this, MainActivity.class);
        startActivity(MainIntent);
        overridePendingTransition(R.anim.splash_out,R.anim.splash_in);
        finish();
    }

    @Override
    public void onAnimationCancel(View view) {

    }
}