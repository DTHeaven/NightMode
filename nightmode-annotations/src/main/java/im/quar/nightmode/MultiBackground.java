package im.quar.nightmode;

import android.support.annotation.AttrRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StyleRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by DTHeaven on 16/1/25.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface MultiBackground {
    @ColorRes @AttrRes @DrawableRes int[] value();
    @StyleRes int[] theme() default {};
}
