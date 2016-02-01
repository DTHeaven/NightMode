package im.quar.nightmode.utils;

import android.util.TypedValue;

/**
 * Created by DTHeaven on 16/1/26.
 */
public class TypeUtil {
    public static final TypedValue sTypedValue = new TypedValue();

    private static final int RESOURCE_TYPE_MASK = 0x00ff0000;
    private static final int DRAWABLE_TYPE = 0x00020000;
    private static final int COLOR_TYPE = 0x000b0000;
    private static final int ATTR_TYPE = 0x00010000;

    public static boolean isColor(TypedValue typedValue) {
        return typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT;
    }

    public static boolean isColor(int resId) {
        return (resId & RESOURCE_TYPE_MASK) == COLOR_TYPE;
    }

    public static boolean isDrawable(int resId) {
        return (resId & RESOURCE_TYPE_MASK) == DRAWABLE_TYPE;
    }

    public static boolean isAttr(int resId) {
        return (resId & RESOURCE_TYPE_MASK) == ATTR_TYPE;
    }
}
