package im.quar.nightmode.changer;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import im.quar.nightmode.NightModeManager;
import im.quar.nightmode.R;
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
                    if (TypeUtil.isColor(sTypedValue)) {
                        if (withAnimation && supportAnimation()) {
                            int currentColor = getBackgroundColorFromTag(view);
                            animChangeBackground(view, currentColor, sTypedValue.data, new ColorDrawable(sTypedValue.data));
                        } else {
                            changeBackground(view, new ColorDrawable(sTypedValue.data));
                        }

                        saveBackgroundColorToTag(view, sTypedValue.data);//Save current color for next change.
                    } else {
                        setBackgroundByResId(view, sTypedValue.resourceId, withAnimation);
                    }
                }
                break;
        }
    }

    private void setBackgroundByResId(View view, int resId, boolean withAnimation) {
        if (TypeUtil.isColor(resId)) {
            int targetColor = view.getResources().getColor(resId);

            if (withAnimation && supportAnimation()) {
                int currentColor = getBackgroundColorFromTag(view);
                animChangeBackground(view, currentColor, targetColor, view.getResources().getDrawable(resId));
            } else {
                changeBackground(view, view.getResources().getDrawable(resId));
            }
            saveBackgroundColorToTag(view, targetColor);//Save current color for next change.
        } else if (TypeUtil.isDrawable(resId)) {
            changeBackground(view, view.getResources().getDrawable(resId));
            saveBackgroundColorToTag(view, Color.TRANSPARENT);//Save current color for next change.
        }
    }

    private void saveBackgroundColorToTag(View view, int color) {
        view.setTag(R.id.night_mode_current_background_color, color);
    }

    private int getBackgroundColorFromTag(View view) {
        Object tag = view.getTag(R.id.night_mode_current_background_color);
        if (tag instanceof Integer) {
            return (int) tag;
        }

        return Color.TRANSPARENT;
    }

    private void saveTintColorToTag(View view, int color) {
        view.setTag(R.id.night_mode_current_tint_color, color);
    }

    private int getTintColorFromTag(View view) {
        Object tag = view.getTag(R.id.night_mode_current_tint_color);
        if (tag instanceof Integer) {
            return (int) tag;
        }

        return Color.TRANSPARENT;
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

    @Override
    public void changeImageTint(ImageView imageView, int[] values, int targetMode, boolean withAnimation) {
        switch (NightModeManager.getMultiThemePolicy()) {
            case UI_MODE:
                int resId = values[0];
                if (TypeUtil.isColor(resId)) {
                    changeImageTintByResId(imageView, resId, withAnimation);
                }
                break;

            case MULTI_VALUES:
                if (targetMode <= values.length) {
                    resId = values[targetMode];
                    if (TypeUtil.isColor(resId)) {
                        changeImageTintByResId(imageView, resId, withAnimation);
                    }
                }
                break;

            case MULTI_THEMES:
                int attr = values[0];
                if (TypeUtil.isAttr(attr)) {
                    Resources.Theme theme = imageView.getContext().getTheme();
                    theme.resolveAttribute(attr, sTypedValue, true);
                    if (TypeUtil.isColor(sTypedValue)) {
                        if (withAnimation && supportAnimation()) {
                            int fromColor = getTintColorFromTag(imageView);
                            animChangeTintColor(imageView, fromColor, sTypedValue.data);
                        } else {
                            changeTintColor(imageView, sTypedValue.data);
                        }
                    } else if (TypeUtil.isColor(sTypedValue.resourceId)) {
                        changeImageTintByResId(imageView, sTypedValue.resourceId, withAnimation);
                    }
                }
                break;
        }
    }


    private void changeImageTintByResId(ImageView view, int resId, boolean withAnimation) {
        if (TypeUtil.isColor(resId)) {
            int targetColor = view.getResources().getColor(resId);

            if (withAnimation && supportAnimation()) {
                int currentColor = getBackgroundColorFromTag(view);
                animChangeTintColor(view, currentColor, targetColor);
            } else {
                changeTintColor(view, view.getResources().getColor(resId));
            }
            saveTintColorToTag(view, targetColor);//Save current tint color for next change.
        }
    }

    protected abstract boolean supportAnimation();

    protected abstract void changeBackground(View view, Drawable drawable);
    protected abstract void animChangeBackground(View view, int fromColor, int toColor, Drawable drawable);

    protected abstract void changeTintColor(ImageView imageView, int color);
    protected abstract void animChangeTintColor(ImageView imageView, int fromColor, int toColor);

    protected abstract void changeTextColor(TextView textView, ColorStateList colorStateList);
    protected abstract void animChangeTextColor(TextView textView, int fromColor, int toColor, ColorStateList colorStateList);
}
