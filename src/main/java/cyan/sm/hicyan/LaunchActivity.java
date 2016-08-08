package cyan.sm.hicyan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                //WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_launch);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent();
                i.setClass(LaunchActivity.this,InfosActivity.class);
                startActivity(i);
                finish();
            }
        },2000);
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {


        }
    };
}
