package im.quar.nightmode.utils;

import java.lang.reflect.Field;

/**
 * Created by DTHeaven on 15/11/21.
 */
public class ReflectionUtil {

    private ReflectionUtil() {}

    public static Field getField(Object o, String name) {
        try {
            Field field = o.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
        }

        return null;
    }

    public static Object getFieldValue(Object o, String name) {

        try {
            Field field = getField(o, name);
            return field.get(o);
        } catch (Exception e) {
        }

        return null;
    }

    public static void setFieldValue(Object o, String name, Object value) {
        Field field = getField(o, name);
        try {
            field.set(o, value);
        } catch (Exception e) {
        }
    }
}
