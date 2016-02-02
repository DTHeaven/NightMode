package im.quar.nightmode;

import android.support.annotation.StyleRes;

import im.quar.nightmode.changer.Changer;
import im.quar.nightmode.changer.DefaultChanger;

/**
 * Created by DTHeaven on 16/1/31.
 */
public class Configuration {

    private MultiThemePolicy policy;
    private Changer changer;
    private int[] themes;

    private Configuration(MultiThemePolicy policy, int[] themes, Changer changer) {
        this.policy = policy;
        this.themes = themes;
        this.changer = changer;
    }

    public MultiThemePolicy getPolicy() {
        return policy;
    }

    public Changer getChanger() {
        return changer;
    }

    public int[] getThemes() {
        return themes;
    }

    public static class Builder {
        private MultiThemePolicy policy;
        private Changer changer;
        private int[] themes;

        public Builder setMultiThemePolicy(MultiThemePolicy policy) {
            this.policy = policy;
            return this;
        }

        public Builder setThemes(@StyleRes int... themes) {
            this.themes = themes;
            return this;
        }

        public Builder setChanger(Changer changer) {
            this.changer = changer;
            return this;
        }

        public Configuration build() {
            if (policy == null) {
                policy = MultiThemePolicy.UI_MODE;
            }

            if (changer == null) {
                changer = new DefaultChanger();
            }

            if (themes == null) {
                themes = new int[]{};
            }

            return new Configuration(policy, themes, changer);
        }
    }
}
