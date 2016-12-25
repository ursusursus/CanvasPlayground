package sk.ursus.canvasplayground;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by brecka on 25.12.2016.
 */

public class ListItemMarginDecoration extends RecyclerView.ItemDecoration {

    private final int mSpaceSize;
    private int mHeaderHeight;
    private int mFooterHeight;
    private boolean mHorizontal;

    public ListItemMarginDecoration(boolean horizontal, int spaceSize, int headerHeight, int footerHeight) {
        mHorizontal = horizontal;
        mSpaceSize = spaceSize;
        mHeaderHeight = headerHeight;
        mFooterHeight = footerHeight;
    }

    public boolean setHeaderHeight(int headerHeight) {
        if (mHeaderHeight != headerHeight) {
            mHeaderHeight = headerHeight;
            return true;
        }
        return false;
    }

    public boolean setFooterHeight(int footerHeight) {
        if(mFooterHeight != footerHeight) {
            mFooterHeight = footerHeight;
            return true;
        }
        return false;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mHorizontal) {
            final int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                outRect.left = mSpaceSize + mHeaderHeight;
                outRect.right = (int) (mSpaceSize / 2F);

            } else if (position == state.getItemCount() - 1) {
                outRect.left = (int) (mSpaceSize / 2F);
                outRect.right = mSpaceSize + mFooterHeight;

            } else {
                outRect.left = (int) (mSpaceSize / 2F);
                outRect.right = (int) (mSpaceSize / 2F);

            }
            outRect.top = mSpaceSize;
            outRect.bottom = mSpaceSize;

        } else {
            final int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                outRect.top = mSpaceSize + mHeaderHeight;
                outRect.bottom = (int) (mSpaceSize / 2F);

            } else if (position == state.getItemCount() - 1) {
                outRect.top = (int) (mSpaceSize / 2F);
                outRect.bottom = mSpaceSize + mFooterHeight;

            } else {
                outRect.top = (int) (mSpaceSize / 2F);
                outRect.bottom = (int) (mSpaceSize / 2F);

            }
            outRect.left = mSpaceSize;
            outRect.right = mSpaceSize;
        }
    }
}
