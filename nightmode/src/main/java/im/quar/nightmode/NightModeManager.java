package im.quar.nightmode;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import im.quar.nightmode.changer.Changer;
import im.quar.nightmode.utils.SharePrefHelper;

/**
 * Created by DTHeaven on 16/1/22.
 */
public class NightModeManager {

    private static final String TAG = "NightModeManager";
    private static final boolean DEBUG = false;

    public static final int DAY_MODE = 0;
    public static final int NIGHT_MODE = 1;

    private static final String MODE_CHANGER_SUFFIX = "$$ModeChanger";

    private static final String SHARE_PREFERENCE_NAME = "night_mode_manager.pref";
    private static final String CURRENT_MODE = "current_mode";
    private static SharePrefHelper sSharePrefHelper;

    //Value object may be WeakReference<Object> or LinkedHashSet<Integer, WeakReference<Object>> for fragment.
    private static final Stack<Map<Integer, Object>> STACK = new Stack<>();
    private static final Map<Class<?>, ModeChanger<Object>> MODE_CHANGERS = new LinkedHashMap<>();

    private static Context sContext;
    private static MultiThemePolicy sMultiThemePolicy;
    private static Changer sChanger;
    private static int[] sThemes;
    private static int sCurMode = DAY_MODE;

    public static void init(Context context) {
        init(context, new im.quar.nightmode.Configuration.Builder().build());
    }

    public static void init(Context context, im.quar.nightmode.Configuration config) {
        sContext = context.getApplicationContext();
        sMultiThemePolicy = config.getPolicy();
        sChanger = config.getChanger();
        sThemes = config.getThemes();

        sSharePrefHelper = SharePrefHelper.getInstance(sContext, SHARE_PREFERENCE_NAME);
        sCurMode = sSharePrefHelper.getPref(CURRENT_MODE, DAY_MODE);
    }

    public static MultiThemePolicy getMultiThemePolicy() {
        return sMultiThemePolicy;
    }

    /**
     * Correct the current mode only when yourself mode is not matching {@link #getCurrentMode()}.
     * @param mode
     */
    public static void correctMode(int mode) {
        checkInitial();
        sCurMode = mode;
        sSharePrefHelper.setPref(CURRENT_MODE, sCurMode);
    }

    public static int getCurrentMode() {
        return sCurMode;
    }

    /**
     * Every activity should call this method, because NightModeManager distinguish current page by this.
     * @param target
     */
    public static void bind(@NonNull Activity target) {
        if (DEBUG) {
            Log.i(TAG, "bind activity:" + target);
        }

        //Bind activity, considered as enter next page.
        Map<Integer, Object> newPageMap = new LinkedHashMap<>();
        newPageMap.put(target.hashCode(), new WeakReference<Object>(target));
        STACK.push(newPageMap);
        bindChanger(target);

        if (DEBUG) {
            dump();
        }
    }

    public static void unbind(@NonNull Activity target) {
        if (DEBUG) {
            Log.i(TAG, "unbind activity:" + target);
        }

        if (STACK.isEmpty()) {
            Log.w(TAG, "STACK is empty...");

            if (DEBUG) {
                dump();
            }
            return;
        }

        //back to last page.
        Map<Integer, Object> topMap = STACK.pop();
        if (!topMap.containsKey(target.hashCode())) {//Find target and remove.
//            Log.w(TAG, "Some thing was wrong when unbind, did you forget to call unbind when finish last activity?");
            int key = target.hashCode();
            int size = STACK.size();
            for (int i = size - 1; i >= 0; i--) {
                if (STACK.elementAt(i).containsKey(key)) {//Remove target.
                    STACK.removeElementAt(i);
                    break;
                }
            }

            //Re-add topMap.
            STACK.push(topMap);
        }

        if (DEBUG) {
            dump();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void bind(@NonNull Fragment target) {
        if (DEBUG) {
            Log.i(TAG, "bind fragment:" + target);
        }

        if (STACK.isEmpty()) {
            Log.w(TAG, "Must bind activity at first.");
            return;
        }

        Map<Integer, Object> topMap = STACK.peek();
        LinkedHashMap<Integer, WeakReference<Object>> map = new LinkedHashMap<>();
        map.put(target.hashCode(), new WeakReference<Object>(target));
        topMap.put(target.hashCode(), map);
        bindChanger(target);

        if (DEBUG) {
            dump();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void unbind(@NonNull Fragment target) {
        if (DEBUG) {
            Log.i(TAG, "unbind fragment:" + target);
        }

        if (STACK.isEmpty()) {
            Log.w(TAG, "Must bind activity at first.");
            return;
        }

        Map<Integer, Object> topMap = STACK.peek();
        topMap.remove(target.hashCode());

        if (DEBUG) {
            dump();
        }
    }

    public static void bind(@NonNull android.support.v4.app.Fragment target) {
        if (DEBUG) {
            Log.i(TAG, "bind fragment:" + target);
        }

        if (STACK.isEmpty()) {
            Log.w(TAG, "Must bind activity at first.");
            return;
        }

        Map<Integer, Object> topMap = STACK.peek();
        LinkedHashMap<Integer, WeakReference<Object>> map = new LinkedHashMap<>();
        map.put(target.hashCode(), new WeakReference<Object>(target));
        topMap.put(target.hashCode(), map);
        bindChanger(target);

        if (DEBUG) {
            dump();
        }
    }

    public static void unbind(@NonNull android.support.v4.app.Fragment target) {
        if (DEBUG) {
            Log.i(TAG, "unbind fragment:" + target);
        }

        if (STACK.isEmpty()) {
            Log.w(TAG, "Must bind activity at first.");
            return;
        }

        Map<Integer, Object> topMap = STACK.peek();
        topMap.remove(target.hashCode());

        if (DEBUG) {
            dump();
        }
    }

    public static void bind(@NonNull Object target) {
        if (DEBUG) {
            Log.i(TAG, "bind:" + target);
        }

        if (STACK.isEmpty()) {
            Log.w(TAG, "Must bind activity at first.");
            return;
        }

        Map<Integer, Object> topMap = STACK.peek();
        topMap.put(target.hashCode(), new WeakReference<Object>(target));
        bindChanger(target);

        if (DEBUG) {
            dump();
        }
    }

    public static void unbind(@NonNull Object target) {
        if (DEBUG) {
            Log.i(TAG, "unbind:" + target);
        }

        if (STACK.isEmpty()) {
            Log.w(TAG, "Must bind activity at first.");
            return;
        }

        Map<Integer, Object> topMap = STACK.peek();
        topMap.remove(target.hashCode());

        if (DEBUG) {
            dump();
        }
    }

    /**
     * This method only works when target api is above {@link android.os.Build.VERSION_CODES#HONEYCOMB_MR1}.
     * @param target
     * @param source
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static void bind(@NonNull final Object target, @NonNull final View source) {
        source.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                bind(target);
                updateCurrent(target);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                unbind(target);
            }
        });
    }

    /**
     * This method only works when target api is above {@link android.os.Build.VERSION_CODES#HONEYCOMB_MR1}.
     * @param target
     * @param source
     */
    public static void unbind(@NonNull Object target, @NonNull View source) {
        unbind(target);
    }

    public static void bind(@NonNull Object target, @NonNull Fragment parentFragment) {
        bindToFragment(target, parentFragment);
    }

    public static void unbind(@NonNull Object target, @Nullable Fragment parentFragment) {
        unbindFromFragment(target, parentFragment);
    }

    public static void bind(@NonNull Object target, @Nullable android.support.v4.app.Fragment parentFragment) {
        bindToFragment(target, parentFragment);
    }

    public static void unbind(@NonNull Object target, @Nullable android.support.v4.app.Fragment parentFragment) {
        unbindFromFragment(target, parentFragment);
    }

    private static void bindToFragment(@NonNull Object target, @Nullable Object parentFragment) {
        if (parentFragment == null) {
            bind(target);
        } else {
            if (DEBUG) {
                Log.i(TAG, "bind to fragment:" + target);
            }

            if (STACK.isEmpty()) {
                Log.w(TAG, "Must bind activity at first.");
                return;
            }

            Map<Integer, Object> topMap = STACK.peek();
            Object map = topMap.get(parentFragment.hashCode());
            if (map instanceof Map) {
                ((Map) map).put(target.hashCode(), new WeakReference<Object>(target));
            }
            bindChanger(target);

            if (DEBUG) {
                dump();
            }
        }
    }

    private static void unbindFromFragment(@NonNull Object target, @Nullable Object parentFragment) {
        if (parentFragment == null) {
            unbind(target);
        } else {
            if (DEBUG) {
                Log.i(TAG, "unbind from fragment:" + target);
            }

            if (STACK.isEmpty()) {
                Log.w(TAG, "Must bind activity at first.");
                return;
            }

            Map<Integer, Object> topMap = STACK.peek();
            Object map = topMap.get(parentFragment.hashCode());
            if (map instanceof Map) {
                ((Map) map).remove(target.hashCode());
            }

            if (DEBUG) {
                dump();
            }
        }
    }

    private static void bindChanger(Object target) {
        if (MODE_CHANGERS.get(target.getClass()) == null) {
            String name = target.getClass().getName() + MODE_CHANGER_SUFFIX;
            try {
                Class<?> clazz = Class.forName(name);
                MODE_CHANGERS.put(target.getClass(), (ModeChanger<Object>) clazz.newInstance());
            } catch (ClassNotFoundException e) {
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }
        }
    }

    /**
     * Update current page ui with current mode.
     *
     * If using {@link MultiThemePolicy#MULTI_VALUES} or {@link MultiThemePolicy#MULTI_THEMES}, you may need invoke this method after initialed your widgets.
     * @param target Same target with bind().
     */
    public static void updateCurrent(Object target) {
        updateTargetMode(target, sCurMode, false);
    }

    /**
     * Toggle day/night mode.
     */
    public static void toggleMode() {
        int targetMode = sCurMode == DAY_MODE ? NIGHT_MODE : DAY_MODE;
        changeMode(targetMode, false);
    }

    /**
     * Toggle day/night mode with animation.
     */
    public static void toggleModeWithAnimation() {
        int targetMode = sCurMode == DAY_MODE ? NIGHT_MODE : DAY_MODE;
        changeMode(targetMode, true);
    }

    public static void changeMode(int targetMode) {
        changeMode(targetMode, false);
    }

    public static void changeMode(int targetMode, boolean withAnimation) {
        checkInitial();
        if (sMultiThemePolicy == MultiThemePolicy.UI_MODE) {
            //修改ui mode
            updateUiMode(sContext, targetMode == NIGHT_MODE);
        }

        for (int i = STACK.size() - 1; i >= 0; i--) {//From top to bottom.
            Collection<Object> set = STACK.elementAt(i).values();
            changeElementsMode(set, targetMode, withAnimation);
            if (withAnimation) {//仅需在当前activity中使用动画，即stack中栈顶元素
                withAnimation = false;
            }
        }

        sCurMode = targetMode;
        sSharePrefHelper.setPref(CURRENT_MODE, sCurMode);
    }

    private static void changeElementsMode(Collection<Object> set, int mode, boolean withAnimation) {
        Iterator<Object> iterator = set.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof WeakReference) {
                WeakReference<Object> elementRef = (WeakReference<Object>) obj;
                Object element = elementRef.get();
                if (element != null) {
                    updateTargetMode(element, mode, withAnimation);
                } else {
                    //TODO Remove null reference
                    Log.w(TAG, "Should remove null reference...");
                }
            } else if (obj instanceof Map) {
                Map<Integer, Object> map = (Map<Integer, Object>) obj;
                changeElementsMode(map.values(), mode, withAnimation);
            }
        }
    }

    private static void updateTargetMode(Object target, int targetMode, boolean withAnimation) {
        if (sMultiThemePolicy == MultiThemePolicy.MULTI_THEMES) {
            if (targetMode < sThemes.length && target instanceof Context) {
                ((Context) target).setTheme(sThemes[targetMode]);//Update every activity's theme
            }
        }
        ModeChanger<Object> modeChanger = MODE_CHANGERS.get(target.getClass());
        if (modeChanger != null) {
            modeChanger.change(sChanger, target, targetMode, withAnimation);
        } else {
            Log.w(TAG, "Did you forget to call bind() before changing mode?");
        }
    }

    private static void updateUiMode(Context context, boolean on) {
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        config.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK;
        config.uiMode |= on ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO;
        res.updateConfiguration(config, dm);
    }

    private static void checkInitial() {
        if (sContext == null) {
            throw new IllegalStateException("Must initial NightModeManager before changing mode.");
        }
    }

    private static void dump() {
        Iterator<Map<Integer, Object>> iterator = STACK.iterator();
        while (iterator.hasNext()) {
            Log.i(TAG, "NewPage---------------------------");
            Collection<Object>  set = iterator.next().values();
            dumpElementsMode(set);
        }
    }

    private static void dumpElementsMode(Collection<Object> set) {
        Iterator<Object> iterator = set.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof WeakReference) {
                WeakReference<Object> elementRef = (WeakReference<Object>) obj;
                Log.i(TAG, "target:" + elementRef.get());
            } else if (obj instanceof Map) {
                Map<Integer, Object> map = (Map<Integer, Object>) obj;
                dumpElementsMode(map.values());
            }
        }
    }
}
