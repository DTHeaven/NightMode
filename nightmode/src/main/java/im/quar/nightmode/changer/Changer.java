package im.quar.nightmode.changer;

import android.view.View;
import android.widget.TextView;

/**
 * Created by DTHeaven on 16/1/22.
 */
public interface Changer {
    void changeBackground(View view, int[] values, int targetMode, boolean withAnimation);
    void changeTextColor(TextView view, int[] values, int targetMode, boolean withAnimation);
}
