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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import im.quar.nightmode.changer.Changer;
import im.quar.nightmode.utils.SharePrefHelper;

/**
 * Created by DTHeaven on 16/1/22.
 */
public class NightModeManager {

    private static final String TAG = "NightModeManager";
    private static final boolean DEBUG = true;

    public static final int DAY_MODE = 0;
    public static final int NIGHT_MODE = 1;

    private static final String MODE_CHANGER_SUFFIX = "$$ModeChanger";

    private static final String SHARE_PREFERENCE_NAME = "night_mode_manager.pref";
    private static final String CURRENT_MODE = "current_mode";
    private static SharePrefHelper sSharePrefHelper;

    //以activity为单位添加set，set中包含activity下bind(object)对象，
    // unbind对象时清除相应对象，unbind activity时清空activity下所有元素
    //activity onCreate()中bind，添加set，destroy中unbind，移除set
    //set中object与CHANGERS中对象对应，当前activity下set与CURRENT对应
    //CHANGERS中key为object's class, value为注解生成的changers（每个bind生成对应changer，changer是否需要与instance对应，而不是class？）
    private static final Stack<Map<Integer, WeakReference<Object>>> STACK = new Stack<>();
    private static final Map<Integer, Set<Integer>> FRAGMENT_BINDER = new HashMap<>();//Fragment.hashCode(), set(target.hashCode())

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
        Log.i(TAG, "bind activity:" + target);

        //Bind activity时视为进入下一页
        Map<Integer, WeakReference<Object>> newPageMap = new LinkedHashMap<>();
        newPageMap.put(target.hashCode(), new WeakReference<Object>(target));
        STACK.push(newPageMap);
        bindChanger(target);

        if (DEBUG) {
            dump();
        }
    }

    public static void unbind(@NonNull Activity target) {
        Log.i(TAG, "unbind activity:" + target);

        if (STACK.isEmpty()) {
            Log.w(TAG, "STACK isEmpty...");
            dump();
            return;
        }

        //unbind activity时视为返回上一页面
        Map<Integer, WeakReference<Object>> topMap = STACK.pop();
        if (topMap.containsKey(target.hashCode())) {
            removeFragmentBinder(topMap);
        } else {
            Log.w(TAG, "Some thing was wrong when unbind, did you forget to call unbind when finish last activity?");
            int key = target.hashCode();
            int size = STACK.size();
            for (int i = size - 1; i >= 0; i--) {
                if (STACK.elementAt(i).containsKey(key)) {//Remove every object above target.
                    for (int j = size - 1; j >= i; j--) {
                        Map<Integer, WeakReference<Object>> map = STACK.pop();
                        removeFragmentBinder(map);
                    }

                    if (DEBUG) {
                        dump();
                    }
                    return;
                }
            }

            //Not found, re-add.
            STACK.push(topMap);
        }

        if (DEBUG) {
            dump();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void bind(@NonNull Fragment target) {
        Log.i(TAG, "bind fragment:" + target);
        if (STACK.isEmpty()) {
            Log.w(TAG, "Must bind activity at first.");
            return;
        }

        Map<Integer, WeakReference<Object>> topMap = STACK.peek();
        topMap.put(target.hashCode(), new WeakReference<Object>(target));
        bindChanger(target);

        if (DEBUG) {
            dump();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void unbind(@NonNull Fragment target) {
        Log.i(TAG, "unbind fragment:" + target);

        if (STACK.isEmpty()) {
            Log.w(TAG, "Must bind activity at first.");
            return;
        }

        Map<Integer, WeakReference<Object>> topMap = STACK.peek();
        topMap.remove(target.hashCode());
        removeTargetsByFragment(target.hashCode(), topMap);

        if (DEBUG) {
            dump();
        }
    }

    public static void bind(@NonNull android.support.v4.app.Fragment target) {
        Log.i(TAG, "bind fragment:" + target);
        if (STACK.isEmpty()) {
            Log.w(TAG, "Must bind activity at first.");
            return;
        }

        Map<Integer, WeakReference<Object>> topMap = STACK.peek();
        topMap.put(target.hashCode(), new WeakReference<Object>(target));
        bindChanger(target);

        if (DEBUG) {
            dump();
        }
    }

    public static void unbind(@NonNull android.support.v4.app.Fragment target) {
        Log.i(TAG, "unbind fragment:" + target);

        if (STACK.isEmpty()) {
            Log.w(TAG, "Must bind activity at first.");
            return;
        }

        Map<Integer, WeakReference<Object>> topMap = STACK.peek();
        topMap.remove(target.hashCode());
        removeTargetsByFragment(target.hashCode(), topMap);

        if (DEBUG) {
            dump();
        }
    }

    public static void bind(@NonNull Object target) {
        Log.i(TAG, "bind:" + target);
        if (STACK.isEmpty()) {
            Log.w(TAG, "Must bind activity at first.");
            return;
        }

        Map<Integer, WeakReference<Object>> topMap = STACK.peek();
        topMap.put(target.hashCode(), new WeakReference<Object>(target));
        bindChanger(target);

        if (DEBUG) {
            dump();
        }
    }

    public static void unbind(@NonNull Object target) {
        Log.i(TAG, "unbind:" + target);

        if (STACK.isEmpty()) {
            Log.w(TAG, "Must bind activity at first.");
            return;
        }

        Map<Integer, WeakReference<Object>> topMap = STACK.peek();
        topMap.remove(target.hashCode());

        if (DEBUG) {
            dump();
        }
    }

//    public static void  bind(@NonNull View target) {
//
//    }
//
//    public static void unbind(@NonNull View target) {
//
//    }

    /**
     * This method only works when target api is above 11(HONEYCOMB_MR1).
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
     * This method only works when target api is above 11(HONEYCOMB_MR1).
     * @param target
     * @param source
     */
    public static void unbind(@NonNull Object target, @NonNull View source) {
        unbind(target);
    }

    public static void bind(@NonNull Object target, @NonNull Fragment parentFragment) {
        bindInternal(target, parentFragment);
    }

    public static void unbind(@NonNull Object target, @Nullable Fragment parentFragment) {
        unbindInternal(target, parentFragment);
    }

    public static void bind(@NonNull Object target, @Nullable android.support.v4.app.Fragment parentFragment) {
        bindInternal(target, parentFragment);
    }

    public static void unbind(@NonNull Object target, @Nullable android.support.v4.app.Fragment parentFragment) {
        unbindInternal(target, parentFragment);
    }

    private static void bindInternal(@NonNull Object target, @Nullable Object parentFragment) {
        bind(target);
        if (parentFragment != null) {
            attachToFragment(target.hashCode(), parentFragment.hashCode());
        }
    }

    private static void unbindInternal(@NonNull Object target, @Nullable Object parentFragment) {
        unbind(target);
        if (parentFragment != null) {
            detachFromFragment(target.hashCode(), parentFragment.hashCode());
        }
    }

    private static void attachToFragment(int targetHashCode, int fragmentHashCode) {
        Set<Integer> set = FRAGMENT_BINDER.get(fragmentHashCode);
        if (set == null) {
            set = new HashSet<>();
            FRAGMENT_BINDER.put(fragmentHashCode, set);
        }
        set.add(targetHashCode);
    }

    private static void detachFromFragment(int targetHashCode, int fragmentHashCode) {
        Set<Integer> set = FRAGMENT_BINDER.get(fragmentHashCode);
        if (set != null) {
            set.remove(targetHashCode);
        }
    }

    private static void removeTargetsByFragment(int fragmentHashCode, Map<Integer, WeakReference<Object>> topMap) {
        Set<Integer> set = FRAGMENT_BINDER.get(fragmentHashCode);
        if (set != null) {
            Iterator<Integer> iterator =  set.iterator();
            while (iterator.hasNext()) {
                topMap.remove(iterator.next());
            }
            FRAGMENT_BINDER.remove(fragmentHashCode);
        }
    }

    private static void removeFragmentBinder(Map<Integer, WeakReference<Object>> topMap) {
        Iterator<WeakReference<Object>> iterator = topMap.values().iterator();
        while (iterator.hasNext()) {
            Object target = iterator.next().get();
            if (target != null) {
                unbind(target);
//                FRAGMENT_BINDER.remove(target.hashCode());
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

    private static void dump() {
        Iterator<Map<Integer, WeakReference<Object>>> iterator = STACK.iterator();
        while (iterator.hasNext()) {
            Log.i(TAG, "NewPage---------------------------");
            Map<Integer, WeakReference<Object>> map = iterator.next();
            Iterator<WeakReference<Object>> valuesIterator =  map.values().iterator();
            while (valuesIterator.hasNext()) {
                WeakReference<Object> target = valuesIterator.next();
                Log.i(TAG, "target:" + target.get());
            }
        }

        Log.i(TAG, "FragmentBinders:" + FRAGMENT_BINDER.size());
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

    public static void changeMode(int mode) {
        changeMode(mode, false);
    }

    public static void changeMode(int mode, boolean withAnimation) {
        checkInitial();
        sCurMode = mode;
        if (sMultiThemePolicy == MultiThemePolicy.UI_MODE) {
            //修改ui mode
            updateUiMode(sContext, sCurMode == NIGHT_MODE);
        }

        for (int i = STACK.size() - 1; i >= 0; i--) {//From top to bottom.
            Collection<WeakReference<Object>> set = STACK.get(i).values();
            changeElementsMode(set, mode, withAnimation);
            if (withAnimation) {//仅需在当前activity中使用动画，即stack中栈顶元素
                withAnimation = false;
            }
        }

        sSharePrefHelper.setPref(CURRENT_MODE, sCurMode);
    }

    private static void changeElementsMode(Collection<WeakReference<Object>> set, int mode, boolean withAnimation) {
        Iterator<WeakReference<Object>> iterator = set.iterator();
        while (iterator.hasNext()) {
            WeakReference<Object> elementRef = iterator.next();
            Object element = elementRef.get();
            if (element != null) {
                updateTargetMode(element, mode, withAnimation);
            } else {
                //TODO Remove null reference
                Log.w(TAG, "Should remove null reference...");
            }
        }
    }

    private static void updateTargetMode(Object target, int mode, boolean withAnimation) {
        if (sMultiThemePolicy == MultiThemePolicy.MULTI_THEMES) {
            if (mode < sThemes.length && target instanceof Context) {
                ((Context) target).setTheme(sThemes[sCurMode]);//Update every activity's theme
            }
        }
        ModeChanger<Object> modeChanger = MODE_CHANGERS.get(target.getClass());
        if (modeChanger != null) {
            modeChanger.change(sChanger, target, mode, withAnimation);
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
}
