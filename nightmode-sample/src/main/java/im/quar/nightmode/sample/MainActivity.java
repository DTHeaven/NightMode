package im.quar.nightmode.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import im.quar.nightmode.MultiBackground;
import im.quar.nightmode.MultiImageTint;
import im.quar.nightmode.MultiModeListener;
import im.quar.nightmode.MultiTextColor;
import im.quar.nightmode.NightModeManager;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

//    @MultiTextColor(R.color.textColor)
//    @MultiBackground(R.color.backgroundColor)
//    @MultiTextColor({R.color.textColor, R.color.textColorNight})
//    @MultiBackground({R.color.backgroundColor, R.color.backgroundColorNight})
    @MultiTextColor(R.attr.textColor)
    @MultiBackground(R.attr.backgroundColor)
    TextView mText;

//    @MultiTextColor(R.color.textColor)
//    @MultiBackground(R.color.buttonBackgroundColor)
//    @MultiTextColor({R.color.textColor, R.color.textColorNight})
//    @MultiBackground({R.color.buttonBackgroundColor, R.color.buttonBackgroundColorNight})
    @MultiTextColor(R.attr.textColor)
    @MultiBackground(R.attr.buttonBackgroundColor)
    Button mBtn;

//    @MultiImageTint({R.color.buttonBackgroundColor, R.color.buttonBackgroundColorNight})
    @MultiImageTint(R.attr.buttonBackgroundColor)
    ImageView mImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NightModeManager.bind(this);

        setContentView(R.layout.activity_main);
        mText = (TextView) findViewById(R.id.txt);
        mBtn = (Button) findViewById(R.id.btn);
        mImg = (ImageView) findViewById(R.id.img);

        NightModeManager.updateCurrent(this);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                NightModeManager.toggleModeWithAnimation();
                startActivity(new Intent(MainActivity.this, ListActivity.class));
            }
        });
    }

    @MultiModeListener
    void onModeChanged() {
        Toast.makeText(this, "Mode changed.", Toast.LENGTH_SHORT).show();
    }

    @MultiModeListener
    void modeChanged(int targetMode) {
        Log.i(TAG, "Listener with parameter.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NightModeManager.unbind(this);
    }
}
