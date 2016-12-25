package sk.ursus.canvasplayground;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by brecka on 25.12.2016.
 */

public class CurveMotionActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private CurveMotionView mCurveMotionView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curvemotion);

        mCurveMotionView = (CurveMotionView) findViewById(R.id.curveMotionView);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Curve motion");
        mToolbar.inflateMenu(R.menu.menu_curvemotion);

        MyAdapter adapter = new MyAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new ListItemMarginDecoration(false, (int) UiUtils.dpToPixels(getResources(), 16f), 0, 0));
        recyclerView.setAdapter(adapter);
    }

    private void onSendClick(final MyAdapter.MyViewHolder holder) {
        float statusBarHeight = UiUtils.dpToPixels(getResources(), 24f);

        Rect rectStart = new Rect();
        holder.mImageView.getGlobalVisibleRect(rectStart);
        Log.d("Default", "r=" + rectStart);

        Rect rectTarget = new Rect();
        mToolbar.findViewById(R.id.stack).getGlobalVisibleRect(rectTarget);
        Log.d("Default", "rt=" + rectTarget);

        mCurveMotionView.runAnimation(
                rectStart.centerX(), (int) (rectStart.centerY() - statusBarHeight),
                rectTarget.centerX(), (int) (rectTarget.centerY() - statusBarHeight),
                new Runnable() {
                    @Override
                    public void run() {
                        holder.mImageView.setVisibility(View.INVISIBLE);
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        holder.mImageView.setVisibility(View.VISIBLE);
                    }
                });
    }

    static class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private final CurveMotionActivity mCurveMotionActivity;
        private final LayoutInflater mInflater;

        MyAdapter(CurveMotionActivity curveMotionActivity) {
            mInflater = LayoutInflater.from(curveMotionActivity);
            mCurveMotionActivity = curveMotionActivity;
        }

        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(mInflater.inflate(R.layout.item, parent, false), mCurveMotionActivity);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 5;
        }

        static class MyViewHolder extends RecyclerView.ViewHolder {
            private final ImageView mImageView;

            MyViewHolder(View itemView, final CurveMotionActivity curveMotionActivity) {
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        curveMotionActivity.onSendClick(MyViewHolder.this);
                    }
                });
                mImageView = (ImageView) itemView.findViewById(R.id.imageView);
            }
        }

    }
}
