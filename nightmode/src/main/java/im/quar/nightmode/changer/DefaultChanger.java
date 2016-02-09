package im.quar.nightmode.changer;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import im.quar.nightmode.ColorDrawableCompat;

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
        Drawable background = view.getBackground();
        if (background != null && drawable instanceof ColorDrawable) {
            int color = ColorDrawableCompat.getColor((ColorDrawable) drawable);
            if (background instanceof ColorDrawable && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                ColorDrawableCompat.setColor((ColorDrawable) background, color);
            } else {
                background.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    @Override
    protected void animChangeBackground(View view, int fromColor, int toColor, Drawable drawable) {
        view.setBackgroundDrawable(drawable);
    }

    @Override
    protected void changeTintColor(ImageView imageView, int color) {
        imageView.setColorFilter(color, PorterDuff.Mode.SRC_OVER);
    }

    @Override
    protected void animChangeTintColor(ImageView imageView, int fromColor, int toColor) {
        imageView.setColorFilter(toColor, PorterDuff.Mode.SRC_OVER);
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
