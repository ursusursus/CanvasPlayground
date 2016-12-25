package sk.ursus.canvasplayground;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by brecka on 24.12.2016.
 */

public class CurveMotionView extends View {
    private Path mPath;
    private Bitmap mBitmap;
    private Paint mPaint;
    private Matrix mMatrix;

    public CurveMotionView(Context context) {
        super(context);
        init();
    }

    public CurveMotionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CurveMotionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CurveMotionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        Drawable drawable = getResources().getDrawable(R.drawable.ic_send);
        mBitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0xFFFFEE99);

        mMatrix = new Matrix();
    }

    public void runAnimation(int startX, int startY, int endX, int endY, final Runnable start, final Runnable end) {
        mPath = new Path();
        mPath.moveTo(startX, startY);
        mPath.quadTo(endX, startY + 100, endX, endY);

        final PathMeasure pm = new PathMeasure(mPath, false);
        final float length = pm.getLength();
        final float[] pos = new float[2];
        final float[] tan = new float[2];
        final float halfWidth = mBitmap.getWidth() / 2f;
        final float halfHeight = mBitmap.getHeight() / 2f;

        ValueAnimator anim = ValueAnimator.ofFloat(0f, length);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animation.getAnimatedValue();
                mMatrix.reset();
                pm.getPosTan((Float) animation.getAnimatedValue(), pos, tan);

                float tangens = tan[1] / tan[0];
                float arcusTangens = (float) Math.atan(tangens);
                mMatrix.postRotate((float) Math.toDegrees(arcusTangens), halfWidth, halfHeight);

                mMatrix.postTranslate(pos[0] - halfWidth, pos[1] - halfHeight);
                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                start.run();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                end.run();
            }
        });
        anim.setDuration(750);
        anim.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPath != null) {
            canvas.drawPath(mPath, mPaint);
            canvas.drawBitmap(mBitmap, mMatrix, null);
        }
    }
}
