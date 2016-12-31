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
    /**
     * Tu je ten trik, kedze mam grid cell je na sirku len 1,
     * tak potom mozem pouzit dve krivky oproti sebe v rovnakom smere (obe vertikalne),
     * a pixely medzi budu dopocitane normalne linearne,
     * ak by bolo viacej na sirku, tak by som musel hybat vrcholmi v strede nejako
     * a to by uz bolo zlozitejsie.
     *
     * Z toho vyplyva ze takto nepojde moc robit taky strecovaci efekt ktory bude
     * mat krivky jednu vertikalne druhu zvislo. Resp mozem ale musim manualne posuvat
     * vsetky krivky ktore mi vzniknu ako ciary v tom gride, resp ich kontrolne body
     * a to uz je komplikejtid
     *
     */
    private static final int MESH_WIDTH = 1;
    private static final int MESH_HEIGHT = 30;
    private static final int MESH_SIZE = (MESH_WIDTH + 1) * (MESH_HEIGHT + 1);
    public static final float MAX_STRETCH = 300f;
    private Bitmap mBitmap;
    private Path mLeftPath = new Path();
    private Path mRightPath = new Path();
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
        int bitmapWidth = mBitmap.getWidth();
        float stretchSize = 0.25F * bitmapHeight * mProgress;

        mLeftPath.reset();
        mLeftPath.moveTo(0, 0);
        mLeftPath.quadTo(
                stretchSize, bitmapHeight,
                0, bitmapHeight + stretchSize);

        mRightPath.reset();
        mRightPath.moveTo(bitmapWidth, 0);
        mRightPath.quadTo(
                bitmapWidth - stretchSize, bitmapHeight,
                bitmapWidth, bitmapHeight + stretchSize);
    }

    private void matchVerts() {
        PathMeasure pmLeft = new PathMeasure(mLeftPath, false);
        PathMeasure pmRight = new PathMeasure(mRightPath, false);

        float[] coords = new float[2];
        float bitmapHeight = mBitmap.getHeight();

        for (int i = 0; i < mStaticVertices.length / 2; i++) {
            float x = mStaticVertices[i * 2];
            float y = mStaticVertices[i * 2 + 1];
            float yIndexFraction = y / bitmapHeight;

            // Viem ze mam len jeden slice na sirku,
            // teda pomozem si ze ak vrchol ma x = 0
            // tak viem ze sa ma posuvat podla lavej krivky
            // else pravej
            PathMeasure pm = (x == 0f) ? pmLeft : pmRight;
            pm.getPosTan(yIndexFraction * pm.getLength(), coords, null);

            // setXY(mDrawVertices, i, x + coords[0], coords[1]);
            setXY(mDrawVertices, i, coords[0], coords[1]);
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
        canvas.drawPath(mLeftPath, mDebugPaint);
        canvas.drawPath(mRightPath, mDebugPaint);
    }
}
