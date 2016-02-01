package im.quar.nightmode;

import im.quar.nightmode.changer.Changer;

/**
 * Created by DTHeaven on 16/1/22.
 */
public interface ModeChanger<T> {
    void change(Changer changer, T target, int targetMode, boolean withAnimation);
}
