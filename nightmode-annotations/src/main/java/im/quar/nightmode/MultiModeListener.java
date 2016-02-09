package im.quar.nightmode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import im.quar.nightmode.internal.ListenerMethod;


/**
 * Created by DTHeaven on 16/2/9.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@ListenerMethod(parameters = "int")
public @interface MultiModeListener {
}
