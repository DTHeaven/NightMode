package im.quar.nightmode.changer;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import im.quar.nightmode.ColorDrawableCompat;
import im.quar.nightmode.NightModeManager;
import im.quar.nightmode.utils.TypeUtil;

/**
 * Created by DTHeaven on 16/1/28.
 */
public abstract class AbsChanger implements Changer {

    private static final TypedValue sTypedValue = new TypedValue();

    @Override
    public void changeBackground(View view, int[] values, int targetMode, boolean withAnimation) {
        switch (NightModeManager.getMultiThemePolicy()) {
            case UI_MODE:
                int resId = values[0];
                setBackgroundByResId(view, resId, withAnimation);
                break;

            case MULTI_VALUES:
                if (targetMode <= values.length) {
                    resId = values[targetMode];
                    setBackgroundByResId(view, resId, withAnimation);
                }
                break;

            case MULTI_THEMES:
                int attr = values[0];
                if (TypeUtil.isAttr(attr)) {
                    Resources.Theme theme = view.getContext().getTheme();
                    theme.resolveAttribute(attr, sTypedValue, true);
//                    Log.i("tag", "type:" + sTypedValue.type + " data:" + sTypedValue.data + " resourceId:" + sTypedValue.resourceId);
                    if (TypeUtil.isColor(sTypedValue)) {
                        if (withAnimation && supportAnimation()) {
                            Drawable curBg = view.getBackground();
                            if (curBg instanceof ColorDrawable) {
                                int fromColor = ColorDrawableCompat.getColor((ColorDrawable) curBg);
                                int toColor = sTypedValue.data;
                                animChangeBackground(view, fromColor, toColor, new ColorDrawable(toColor));
                            } else {
                                changeBackground(view, new ColorDrawable(sTypedValue.data));
                            }
                        } else {
                            changeBackground(view, new ColorDrawable(sTypedValue.data));
                        }

                    } else {
                        setBackgroundByResId(view, sTypedValue.resourceId, withAnimation);
                    }
                }
                break;
        }
    }

    private void setBackgroundByResId(View view, int resId, boolean withAnimation) {
        if (TypeUtil.isColor(resId)) {
            if (withAnimation && supportAnimation()) {
                Drawable curBg = view.getBackground();
                if (curBg instanceof ColorDrawable) {
                    int fromColor = ColorDrawableCompat.getColor((ColorDrawable) curBg);
                    int toColor = view.getResources().getColor(resId);
                    animChangeBackground(view, fromColor, toColor, view.getResources().getDrawable(resId));
                } else {
                    changeBackground(view, view.getResources().getDrawable(resId));
                }
            } else {
                changeBackground(view, view.getResources().getDrawable(resId));
            }

        } else if (TypeUtil.isDrawable(resId)) {
            changeBackground(view, view.getResources().getDrawable(resId));
        }
    }

    @Override
    public void changeTextColor(TextView textView, int[] values, int targetMode, boolean withAnimation) {
        switch (NightModeManager.getMultiThemePolicy()) {
            case UI_MODE:
                int resId = values[0];
                if (TypeUtil.isColor(resId)) {
                    changeTextColorByResId(textView, resId, withAnimation);
                }
                break;

            case MULTI_VALUES:
                if (targetMode <= values.length) {
                    resId = values[targetMode];
                    if (TypeUtil.isColor(resId)) {
                        changeTextColorByResId(textView, resId, withAnimation);
                    }
                }
                break;

            case MULTI_THEMES:
                int attr = values[0];
                if (TypeUtil.isAttr(attr)) {
                    Resources.Theme theme = textView.getContext().getTheme();
                    theme.resolveAttribute(attr, sTypedValue, true);
//                    Log.i("tag", "type:" + sTypedValue.type + " data:" + sTypedValue.data + " resourceId:" + sTypedValue.resourceId);
                    if (TypeUtil.isColor(sTypedValue)) {
                        ColorStateList colorStateList = ColorStateList.valueOf(sTypedValue.data);
                        if (withAnimation && supportAnimation()) {
                            int fromColor = textView.getTextColors().getDefaultColor();
                            int toColor = colorStateList.getDefaultColor();
                            animChangeTextColor(textView, fromColor, toColor, colorStateList);
                        } else {
                            changeTextColor(textView, colorStateList);
                        }
                    } else if (TypeUtil.isColor(sTypedValue.resourceId)) {
                        changeTextColorByResId(textView, sTypedValue.resourceId, withAnimation);
                    }
                }
                break;
        }

    }

    private void changeTextColorByResId(TextView view, int resId, boolean withAnimation) {
        ColorStateList colorStateList = view.getResources().getColorStateList(resId);
        if (colorStateList != null) {
            if (withAnimation && supportAnimation()) {
                int fromColor = view.getTextColors().getDefaultColor();
                int toColor = colorStateList.getDefaultColor();
                animChangeTextColor(view, fromColor, toColor, colorStateList);
            } else {
                changeTextColor(view, colorStateList);
            }
        }
    }

    protected abstract boolean supportAnimation();

    protected abstract void changeBackground(View view, Drawable drawable);
    protected abstract void animChangeBackground(View view, int fromColor, int toColor, Drawable drawable);

    protected abstract void changeTextColor(TextView textView, ColorStateList colorStateList);
    protected abstract void animChangeTextColor(TextView textView, int fromColor, int toColor, ColorStateList colorStateList);
}
