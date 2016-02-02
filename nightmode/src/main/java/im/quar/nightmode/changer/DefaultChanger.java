package im.quar.nightmode.changer;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

/**
 * Created by DTHeaven on 16/1/25.
 */
public class DefaultChanger extends AbsChanger {

    @Override
    protected boolean supportAnimation() {
        return false;
    }

    @Override
    protected void changeBackground(View view, Drawable drawable) {
        view.setBackgroundDrawable(drawable);
    }

    @Override
    protected void animChangeBackground(View view, int fromColor, int toColor, Drawable drawable) {
        view.setBackgroundDrawable(drawable);
    }

    @Override
    protected void changeTextColor(TextView textView, ColorStateList colorStateList) {
        textView.setTextColor(colorStateList);
    }

    @Override
    protected void animChangeTextColor(TextView textView, int fromColor, int toColor, ColorStateList colorStateList) {
        textView.setTextColor(colorStateList);
    }
}
