package im.quar.nightmode.sample;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import im.quar.nightmode.Configuration;
import im.quar.nightmode.MultiThemePolicy;
import im.quar.nightmode.NightModeManager;
import im.quar.nightmode.changer.AnimatorChanger;

/**
 * Created by DTHeaven on 16/1/26.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);

        NightModeManager.init(this, new Configuration.Builder()
                .setChanger(new AnimatorChanger())
                .setMultiThemePolicy(MultiThemePolicy.MULTI_THEMES)
                .setThemes(R.style.ThemeDefault, R.style.ThemeNight)
                .build());

        NightModeManager.correctMode(0);
    }
}
