package cyan.sm.hicyan;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.transition.Explode;
import android.view.Window;


public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                //WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setExitTransition(new Explode());
            getWindow().setEnterTransition(new Explode());
        }

        setContentView(R.layout.activity_launch);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent();
                i.setClass(LaunchActivity.this, InfosActivity.class);
                startActivity(i);
                finish();
            }
        }, 2000);
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {


        }
    };
}
