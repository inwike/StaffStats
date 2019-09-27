package ru.gamingcore.staffstats.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

public class Polygon {
    private static final String TAG = "INWIKE";

    private static final int MAX = 220;
    private static final int CENTER_X = MAX / 2;
    private static final int CENTER_Y = MAX / 2;
    private static final double[] ANGLES = {245, 295, 0, 65, 115, 180};
    public Bitmap up;
    public Bitmap down;
    public double[] skills;
    public boolean[] arrows;
    private double[] MAX_LENS = {110, 110, 100, 110, 110, 100};
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Canvas tempCanvas;
    private Path wallpath = new Path();
    private Bitmap tempBitmap;
    private boolean needArrows = false;
    private int[][] xy = new int[6][2];

    public Polygon(double[] skills) {
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeWidth(3);
        paint.setStrokeWidth(3);

        tempBitmap = Bitmap.createBitmap(MAX, MAX, Bitmap.Config.ARGB_8888);
        tempCanvas = new Canvas(tempBitmap);
        this.skills = skills;
        arrows = new boolean[skills.length];
        for (int i = 0; i < skills.length; i++) {
            double radians = Math.toRadians(ANGLES[i]);
            double LEN = MAX_LENS[i] * skills[i] / 100;
            xy[i][0] = CENTER_X + (int) (LEN * Math.cos(radians));
            xy[i][1] = CENTER_Y + (int) (LEN * Math.sin(radians));
        }
    }

    public Bitmap getBitmap(int color) {
        paint.setColor(color - 0xF0000000);
        paint2.setColor(color);
        tempBitmap.eraseColor(Color.TRANSPARENT);
        wallpath.reset();
        wallpath.moveTo(xy[0][0], xy[0][1]);
        for (int i = xy.length - 1; i >= 0; i--) {
            wallpath.lineTo(xy[i][0], xy[i][1]);
            if (needArrows) {
                if (arrows[i])
                    tempCanvas.drawBitmap(up, xy[i][0], xy[i][1], null);
                else
                    tempCanvas.drawBitmap(down, xy[i][0], xy[i][1], null);
            }
        }

        tempCanvas.drawPath(wallpath, paint);
        tempCanvas.drawPath(wallpath, paint2);
        return tempBitmap;
    }

    public void checkArrow(Polygon p, boolean enabled) {
        needArrows = enabled;

        for (int i = 0; i < skills.length; i++) {
            arrows[i] = skills[i] >= p.skills[i];
        }
    }
}
