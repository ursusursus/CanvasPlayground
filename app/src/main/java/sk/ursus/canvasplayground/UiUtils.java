package sk.ursus.canvasplayground;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by brecka on 25.12.2016.
 */

public class UiUtils {

    public static int getToolbarPixelSize(Context context) {
        final TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.actionBarSize, typedValue, true);
        return TypedValue.complexToDimensionPixelSize(typedValue.data, context.getResources().getDisplayMetrics());
    }

    public static float dpToPixels(Resources res, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }
}
