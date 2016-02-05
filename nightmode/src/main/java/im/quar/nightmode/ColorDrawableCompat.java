package im.quar.nightmode;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import im.quar.nightmode.utils.ReflectionUtil;

/**
 * Created by DTHeaven on 16/1/28.
 */
public class ColorDrawableCompat {

    private ColorDrawableCompat() {
    }

    public static int getColor(ColorDrawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return drawable.getColor();
        } else {
            return getColorByReflection(drawable);
        }
    }

    public static void setColor(ColorDrawable drawable, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            drawable.setColor(color);
        } else {
            setColorByReflection(drawable, color);
        }
    }

    private static int getColorByReflection(ColorDrawable drawable) {
        Object mState = ReflectionUtil.getFieldValue(drawable, "mState");
        if (mState != null) {
            Object mUseColor = ReflectionUtil.getFieldValue(mState, "mUseColor");
            if (mUseColor != null) {
                return (int) mUseColor;
            }
        }

        return 0;
    }

    private static void setColorByReflection(ColorDrawable drawable, int color) {
        Object mState = ReflectionUtil.getFieldValue(drawable, "mState");
        if (mState != null) {
            ReflectionUtil.setFieldValue(mState, "mBaseColor", color);
            ReflectionUtil.setFieldValue(mState, "mUseColor", color);
        }
    }
}
