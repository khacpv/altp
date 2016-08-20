package com.example.gcs.faster5.ui.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.os.Handler;

/**
 * Created by AnderWeb (Gustavo Claramunt) on 7/10/14.
 */
public class HexagonDrawable extends Drawable {

    public static final int SIDES = 6;
    private Path hexagon = new Path();
    private Path temporal = new Path();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int color1 = Color.parseColor("#003871");
    private int color2 = Color.parseColor("#2780D3");
    private int colorStroke = Color.WHITE;
    float angle = -90.0f;
    Rect bounds = new Rect();
    SweepGradient bgColor;
    boolean animating = false;
    Handler mHandler = new Handler();

    private Runnable mAnimatingRunnable = new Runnable() {
        @Override
        public void run() {
            if (bounds == null || bounds.width() <= 0 || !animating) {
                return;
            }
            angle = angle + 1f;
            if (angle > 360f) {
                angle = 0.0f;
            }

            rotateTo(angle);

            mHandler.postDelayed(mAnimatingRunnable,1);
        }
    };

    public HexagonDrawable() {
        init();
    }

    public HexagonDrawable(int color1, int color2) {
        this.color1 = color1;
        this.color2 = color2;
        init();
    }

    public HexagonDrawable(int color1, int color2, int colorStroke) {
        this.color1 = color1;
        this.color2 = color2;
        this.colorStroke = colorStroke;
        init();
    }

    private void init() {
        paintStroke.setColor(colorStroke);
        paintStroke.setStrokeWidth(20);
        paintStroke.setStyle(Paint.Style.FILL_AND_STROKE);

        paint.setStyle(Paint.Style.FILL);
        hexagon.setFillType(Path.FillType.WINDING);

    }

    public void start() {
        animating = true;
        mHandler.postDelayed(mAnimatingRunnable, 1);
    }

    public void stop() {
        animating = false;
    }

    public void reset() {
        animating = false;
        rotateTo(-90);
    }

    public void rotateTo(float angle) {
        this.angle = angle;

        int[] colors = {color1, color2};
        float[] positions = {0, 1f};
        bgColor = new SweepGradient(bounds.centerX(), bounds.centerY(), colors,
                positions);
        paint.setShader(bgColor);

        Matrix angleMatrix = new Matrix();
        bgColor.getLocalMatrix(angleMatrix);
        angleMatrix.postRotate(angle, bounds.centerX(), bounds.centerY());
        bgColor.setLocalMatrix(angleMatrix);

        invalidateSelf();
    }

    public boolean isAnimating() {
        return animating;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(hexagon, paintStroke);

        canvas.drawPath(hexagon, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
        paintStroke.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return paint.getAlpha();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        computeHex(bounds);
        reset();
    }

    public void computeHex(Rect bounds) {
        this.bounds = bounds;
        final int width = bounds.width();
        final int height = bounds.height();
        final int size = Math.min(width, height);
        final int centerX = bounds.left + (width / 2);
        final int centerY = bounds.top + (height / 2);

        hexagon.reset();
        hexagon.addPath(createHexagon((int) (size * .9f), centerX, centerY));
        hexagon.addPath(createHexagon((int) (size * .8f), centerX, centerY));

        // invalidate at first time
        start();
        stop();
    }

    private Path createHexagon(int size, int centerX, int centerY) {
        final float section = (float) (2.0 * Math.PI / SIDES);
        int radius = size / 2;
        Path hex = temporal;
        hex.reset();
        hex.moveTo(
                (centerX + radius * (float) Math.cos(0)),
                (centerY + radius * (float) Math.sin(0)));

        for (int i = 1; i < SIDES; i++) {
            hex.lineTo(
                    (centerX + radius * (float) Math.cos(section * i)),
                    (centerY + radius * (float) Math.sin(section * i)));
        }

        hex.close();
        return hex;
    }
}
