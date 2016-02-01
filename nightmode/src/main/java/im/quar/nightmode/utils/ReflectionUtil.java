package im.quar.nightmode.utils;

import java.lang.reflect.Field;

/**
 * Created by DTHeaven on 15/11/21.
 */
public class ReflectionUtil {

    private ReflectionUtil() {}

//    public static boolean getBoolean(Object o, String name, boolean defaultValue) {
//        Object value = getValue(o, name);
//        if (value == null || !(value instanceof Boolean)) {
//            return defaultValue;
//        }
//
//        return (boolean) value;
//    }
//
//    public static boolean getSuperClassBoolean(Object o, String name, boolean defaultValue) {
//        for (Class<?> clazz = o.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
//            try {
//                Field field = clazz.getDeclaredField(name);
//                field.setAccessible(true);
//                Object value = field.get(o);
//                if (value != null && value instanceof Boolean) {
//                    return (boolean) value;
//                }
//            } catch (Exception e) {
//            }
//        }
//        return defaultValue;
//    }
//
//    public static Object getValue(Object o, String name) {
//        return getValue(o, o.getClass(), name);
//    }
//
//    public static Object getSuperClassValue(Object o, String name) {
//        return getValue(o, o.getClass().getSuperclass(), name);
//    }

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
