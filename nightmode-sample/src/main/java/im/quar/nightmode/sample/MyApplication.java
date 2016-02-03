package im.quar.nightmode.sample;

import android.app.Application;

import im.quar.nightmode.Configuration;
import im.quar.nightmode.MultiThemePolicy;
import im.quar.nightmode.NightModeManager;

/**
 * Created by DTHeaven on 16/1/26.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        NightModeManager.init(this, new Configuration.Builder()
                .setChanger(new NineOldAnimatorChanger())
                .setMultiThemePolicy(MultiThemePolicy.MULTI_THEMES)
                .setThemes(R.style.ThemeDefault, R.style.ThemeNight)
                .build());

        NightModeManager.correctMode(0);
    }
}
