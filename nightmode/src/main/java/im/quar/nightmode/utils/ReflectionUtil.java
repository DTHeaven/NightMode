package im.quar.nightmode.utils;

import java.lang.reflect.Field;

/**
 * Created by DTHeaven on 15/11/21.
 */
public class ReflectionUtil {

    private ReflectionUtil() {}

    public static Object getValue(Object o, String name) {

        try {
            Field field = o.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(o);
        } catch (Exception e) {
        }

        return null;
    }
}
