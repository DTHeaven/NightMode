package im.quar.nightmode.changer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.TextView;

import im.quar.nightmode.R;

/**
 * Created by DTHeaven on 16/1/28.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AnimatorChanger extends AbsChanger {

    private static final int DURATION = 360;
    private static final ArgbEvaluator EVALUATOR = new ArgbEvaluator();
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    @Override
    protected boolean supportAnimation() {
        return true;
    }

    @Override
    protected void changeBackground(View view, Drawable drawable) {
        view.setBackgroundDrawable(drawable);
    }

    @Override
    protected void animChangeBackground(final View view, int fromColor, int toColor, final Drawable drawable) {
        ValueAnimator animator = (ValueAnimator) view.getTag(R.id.night_mode_background_animator);
        if (animator == null) {
            animator = new ValueAnimator();
            animator.setDuration(DURATION);
            animator.setInterpolator(INTERPOLATOR);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int color = (int) animation.getAnimatedValue();
                    Drawable drawable = view.getBackground();
                    drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                }
            });
            view.setTag(R.id.night_mode_background_animator, animator);
        } else if (animator.isRunning()) {
            animator.cancel();
            animator.removeAllListeners();
        }

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setBackgroundDrawable(drawable);
            }
        });

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
