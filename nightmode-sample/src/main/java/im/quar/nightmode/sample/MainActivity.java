package im.quar.nightmode.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import im.quar.nightmode.MultiBackground;
import im.quar.nightmode.MultiTextColor;
import im.quar.nightmode.NightModeManager;

public class MainActivity extends AppCompatActivity {


//    @MultiTextColor(R.color.textColor)
//    @MultiBackground(R.color.backgroundColor)
//    @MultiTextColor({R.color.textColor, R.color.colorAccent})
//    @MultiBackground({R.color.backgroundColor, R.color.backgroundColorNight})
    @MultiTextColor(R.attr.textColor)
    @MultiBackground(R.attr.backgroundColor)
    TextView mText;

    Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NightModeManager.bind(this);

        setContentView(R.layout.activity_main);
        mText = (TextView) findViewById(R.id.txt);
        mBtn = (Button) findViewById(R.id.btn);

        NightModeManager.updateCurrent(this);


        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListActivity.class));

//                NightModeManager.toggleMode(MainActivity.this);
//
//                mBtn.setText("CurMode:" + NightModeManager.getCurrentMode());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NightModeManager.unbind(this);
    }
}
