package im.quar.nightmode.changer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import im.quar.nightmode.ColorDrawableCompat;
import im.quar.nightmode.R;
import im.quar.nightmode.animation.FastOutSlowInInterpolator;
import im.quar.nightmode.animation.FixedArgbEvaluator;

/**
 * Created by DTHeaven on 16/1/28.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AnimatorChanger extends AbsChanger {

    private static final int DURATION = 360;
    private static final FixedArgbEvaluator EVALUATOR = new FixedArgbEvaluator();
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    @Override
    protected boolean supportAnimation() {
        return true;
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
    protected void animChangeBackground(final View view, final int fromColor, int toColor, final Drawable drawable) {
        ValueAnimator animator = (ValueAnimator) view.getTag(im.quar.nightmode.R.id.night_mode_background_animator);
        if (animator == null) {
            animator = new ValueAnimator();
            animator.setDuration(DURATION);
            animator.setInterpolator(INTERPOLATOR);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int color = (int) animation.getAnimatedValue();
                    Drawable background = view.getBackground();
                    if (background instanceof ColorDrawable && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        ColorDrawableCompat.setColor((ColorDrawable) background, color);
                    } else {
                        background.setColorFilter(color, PorterDuff.Mode.SRC_OVER);
                    }

                }
            });
            view.setTag(im.quar.nightmode.R.id.night_mode_background_animator, animator);
        } else if (animator.isRunning()) {
            animator.cancel();
            animator.removeAllListeners();
        }

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (view.getBackground() == null) {
                    view.setBackgroundColor(fromColor);
                }
            }
        });

        animator.setIntValues(fromColor, toColor);
        animator.setEvaluator(EVALUATOR);
        animator.start();
    }

    @Override
    protected void changeTintColor(ImageView imageView, int color) {
        imageView.setColorFilter(color, PorterDuff.Mode.SRC_OVER);
    }

    @Override
    protected void animChangeTintColor(final ImageView imageView, int fromColor, int toColor) {
        ValueAnimator animator = (ValueAnimator) imageView.getTag(im.quar.nightmode.R.id.night_mode_tint_animator);
        if (animator == null) {
            animator = new ValueAnimator();
            animator.setDuration(DURATION);
            animator.setInterpolator(INTERPOLATOR);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int color = (int) animation.getAnimatedValue();
                    imageView.setColorFilter(color, PorterDuff.Mode.SRC_OVER);
                }
            });
            imageView.setTag(im.quar.nightmode.R.id.night_mode_tint_animator, animator);
        } else if (animator.isRunning()) {
            animator.cancel();
            animator.removeAllListeners();
        }

        animator.setIntValues(fromColor, toColor);
        animator.setEvaluator(EVALUATOR);
        animator.start();
    }

    @Override
    protected void changeTextColor(TextView textView, ColorStateList colorStateList) {
        textView.setTextColor(colorStateList);
    }

    @Override
    protected void animChangeTextColor(final TextView textView, int fromColor, int toColor, final ColorStateList colorStateList) {
        ValueAnimator animator = (ValueAnimator) textView.getTag(R.id.night_mode_text_color_animator);
        if (animator == null) {
            animator = new ValueAnimator();
            animator.setDuration(DURATION);
            animator.setInterpolator(INTERPOLATOR);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int color = (int) animation.getAnimatedValue();
                    textView.setTextColor(color);

                }
            });

            textView.setTag(R.id.night_mode_text_color_animator, animator);
        } else if (animator.isRunning()) {
            animator.cancel();
            animator.removeAllListeners();
        }

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                textView.setTextColor(colorStateList);
            }
        });

        animator.setIntValues(fromColor, toColor);
        animator.setEvaluator(EVALUATOR);
        animator.start();
    }
}
