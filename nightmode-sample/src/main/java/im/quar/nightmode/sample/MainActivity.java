package im.quar.nightmode.sample;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import im.quar.nightmode.MultiBackground;
import im.quar.nightmode.MultiImageTint;
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

    @MultiTextColor(R.attr.textColor)
    @MultiBackground(R.attr.buttonBackgroundColor)
    Button mBtn;

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

//        mBtn.setBackgroundResource(R.drawable.button_green_selector);
//        mBtn.setBackgroundResource(R.drawable.circle);
        Log.i("tag", "bg:" + mBtn.getBackground());
        Drawable orbg = mBtn.getBackground();
//        Drawable drawable = ((InsetDrawable) mBtn.getBackground()).getDrawable();
//        mBtn.setBackgroundDrawable(drawable);
//        if (drawable instanceof GradientDrawable) {
//            ((GradientDrawable) drawable).setColor(Color.RED);
//        }
//        Log.i("tag", "drawable:" + drawable);
//        mBtn.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
//        drawable.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
//        mBtn.setBackgroundDrawable(orbg);
        orbg.invalidateSelf();
//        mImg.getDrawable().setColorFilter(Color.parseColor("#88000000"), PorterDuff.Mode.SRC_OVER);
        mBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("tag", "bg:" + mBtn.getBackground());
//                        Log.i("tag", "dbg:" + ((InsetDrawable) mBtn.getBackground()).getDrawable());
//                        ((InsetDrawable) mBtn.getBackground()).getDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                        break;

                    case MotionEvent.ACTION_UP:
                        Log.i("tag", "bg:" + mBtn.getBackground());
//                        Log.i("tag", "dbg:" + ((InsetDrawable) mBtn.getBackground()).getDrawable());
//                        ((InsetDrawable) mBtn.getBackground()).getDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                        break;
                }
                return false;
            }
        });
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("tag", "drawable:" + mBtn.getBackground());
                NightModeManager.toggleModeWithAnimation();
//                startActivity(new Intent(MainActivity.this, ListActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NightModeManager.unbind(this);
    }
}
