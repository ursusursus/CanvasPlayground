package sk.ursus.canvasplayground;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 * Created by brecka on 28.12.2016.
 */

public class ElasticView extends View {
    private static final int MESH_WIDTH = 30;
    private static final int MESH_HEIGHT = 30;
    private static final int MESH_SIZE = (MESH_WIDTH + 1) * (MESH_HEIGHT + 1);
    public static final float MAX_STRETCH = 300f;
    private Bitmap mBitmap;
    private Path mPath = new Path();
    private Paint mDebugPaint;
    private float[] mStaticVertices = new float[MESH_SIZE * 2];
    private float[] mDrawVertices = new float[MESH_SIZE * 2];
    private float mProgress;
    private float mDownY;

    public ElasticView(Context context) {
        super(context);
        init();
    }

    public ElasticView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ElasticView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ElasticView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.banana);

        mDebugPaint = new Paint();
        mDebugPaint.setStyle(Paint.Style.STROKE);
        mDebugPaint.setColor(0xFFFFAABB);

        createPath();
        createVerts();
    }

    private void createVerts() {
        float bitmapWidth = (float) mBitmap.getWidth();
        float bitmapHeight = (float) mBitmap.getHeight();

        int index = 0;
        for (int y = 0; y <= MESH_HEIGHT; y++) {
            float actualY = bitmapHeight * y / MESH_HEIGHT;
            for (int x = 0; x <= MESH_WIDTH; x++) {
                float actualX = bitmapWidth * x / MESH_WIDTH;
                setXY(mDrawVertices, index, actualX, actualY);
                setXY(mStaticVertices, index, actualX, actualY);
                index++;
            }
        }
    }

    public void setXY(float[] array, int index, float x, float y) {
        array[index * 2] = x;
        array[index * 2 + 1] = y;
    }

    private void refresh() {
        createPath();
        matchVerts();
        invalidate();
    }

    private void createPath() {
        int bitmapHeight = mBitmap.getHeight();
        float stretchSize = 0.25F * bitmapHeight * mProgress;

        mPath.reset();
        mPath.moveTo(0, 0);
        mPath.quadTo(
                stretchSize, bitmapHeight,
                0, bitmapHeight + stretchSize);
    }

    private void matchVerts() {
        PathMeasure pm = new PathMeasure(mPath, false);
        float[] coords = new float[2];
        float bitmapHeight = mBitmap.getHeight();

        for (int i = 0; i < mStaticVertices.length / 2; i++) {
            float x = mStaticVertices[i * 2];
            float y = mStaticVertices[i * 2 + 1];
            float yIndexFraction = y / bitmapHeight;

            pm.getPosTan(yIndexFraction * pm.getLength(), coords, null);
            setXY(mDrawVertices, i, x + coords[0], coords[1]);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = event.getY() - mDownY;
                mProgress = Math.max(Math.min(dy / MAX_STRETCH, 1f), -1f);
                refresh();
                break;
            case MotionEvent.ACTION_UP:
                if (mProgress != 0f) {
                    float startProgress = mProgress;
                    ValueAnimator animator = ValueAnimator.ofFloat(startProgress, 0f);
                    animator.setDuration(200);
                    animator.setInterpolator(new OvershootInterpolator());
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mProgress = (float) animation.getAnimatedValue();
                            refresh();
                        }
                    });
                    animator.start();

//                    SpringSystem springSystem = SpringSystem.create();
//                    Spring spring = springSystem.createSpring();
//                    spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(40, 3));
//                    spring.addListener(new SimpleSpringListener() {
//
//                        @Override
//                        public void onSpringUpdate(Spring spring) {
//                            mProgress = (float) spring.getCurrentValue();
//                            refresh();
//                        }
//                    });
//                    spring.setCurrentValue(startProgress);
//                    spring.setEndValue(0f);
                }
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmapMesh(mBitmap, MESH_WIDTH, MESH_HEIGHT, mDrawVertices, 0, null, 0, null);
        canvas.drawPath(mPath, mDebugPaint);
    }
}
