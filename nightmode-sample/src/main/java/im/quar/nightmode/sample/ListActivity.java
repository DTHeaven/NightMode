package im.quar.nightmode.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import im.quar.nightmode.MultiBackground;
import im.quar.nightmode.MultiTextColor;
import im.quar.nightmode.NightModeManager;

/**
 * Created by DTHeaven on 16/1/26.
 */
public class ListActivity extends AppCompatActivity {

    @MultiTextColor(R.attr.textColor)
    @MultiBackground(R.attr.buttonBackgroundColor)
    Button mListBtn;

    @MultiTextColor(R.attr.textColor)
    @MultiBackground(R.attr.buttonBackgroundColor)
    Button mSimpleBtn;

    private Fragment mListFragment;
    private Fragment mSimpleFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NightModeManager.bind(this);
        setContentView(R.layout.activity_list);
        mListBtn = (Button) findViewById(R.id.btn_list);
        mSimpleBtn = (Button) findViewById(R.id.btn_simple);
        NightModeManager.updateCurrent(this);

        mListFragment = new ListFragment();
        mSimpleFragment = new SimpleFragment();

        switchToFragment(mListFragment);

        mListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFragment(mListFragment);
            }
        });

        mSimpleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFragment(mSimpleFragment);
            }
        });
    }

    private void switchToFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame, fragment);
        ft.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NightModeManager.unbind(this);
    }
}
