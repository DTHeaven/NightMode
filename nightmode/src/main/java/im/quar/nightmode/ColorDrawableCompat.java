package im.quar.nightmode;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import im.quar.nightmode.utils.ReflectionUtil;

/**
 * Created by DTHeaven on 16/1/28.
 */
public class ColorDrawableCompat {

    private ColorDrawableCompat() {}

    public static int getColor(ColorDrawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return drawable.getColor();
        } else {
            return getColorByReflection(drawable);
        }
    }

    private static int getColorByReflection(ColorDrawable drawable) {
        Object mState = ReflectionUtil.getValue(drawable, "mState");
        if (mState != null) {
            Object mUseColor = ReflectionUtil.getValue(mState, "mUseColor");
            if (mUseColor != null) {
                return (int) mUseColor;
            }
        }

        return 0;
    }
}
