package cyan.sm.hicyan;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import cyan.sm.hicyan.db.EnDe;
import cyan.sm.hicyan.lockviews.gesturelock.GestureLockView;


public class LaunchActivity extends Activity {
    private GestureLockView gv;
    int step = 0;//0为设置密码 1为重复一次密码 2为登录直接输入密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//            getWindow().setExitTransition(new Explode());
//            getWindow().setEnterTransition(new Explode());
//        }

        setContentView(R.layout.activity_launch);

        gv = (GestureLockView) findViewById(R.id.gestureLockview);

        SharedPreferences sp = getSharedPreferences("lock", MODE_PRIVATE);
        String key = sp.getString("lock", "");
        if (key.equals("")) {
            step = 0;
        } else {
            step = 2;
            gv.setKey(EnDe.de(key));
        }

        HiCyan app = (HiCyan) getApplication();
        app.finishDa();

        gv.setOnGestureFinishListener(gfl);
    }

    GestureLockView.OnGestureFinishListener gfl = new GestureLockView.OnGestureFinishListener() {
        @Override
        public void OnGestureFinish(boolean success, String key) {
            if (step == 0) {
                gv.setKey(key);
                step = 1;
                Toast.makeText(LaunchActivity.this, "请再输入一次!", Toast.LENGTH_SHORT).show();
                gv.invalidate();
            } else if (step == 1) {
                if (!success) {
                    Toast.makeText(LaunchActivity.this, "两次密码不一致!", Toast.LENGTH_SHORT).show();
                    step = 0;
                } else {
                    SharedPreferences sp = getSharedPreferences("lock", MODE_PRIVATE);
                    SharedPreferences.Editor ed = sp.edit();
                    ed.putString("lock", EnDe.en(key));
                    ed.commit();
                    Toast.makeText(LaunchActivity.this, "成功!", Toast.LENGTH_SHORT).show();

                    enter();
                }
            } else {
                if (!success) {
                    Toast.makeText(LaunchActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LaunchActivity.this, "成功!", Toast.LENGTH_SHORT).show();
                    enter();
                }
            }
        }
    };

    private void enter() {
        SharedPreferences sp = getSharedPreferences("lock", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putLong("time", System.currentTimeMillis());
        ed.commit();

        Intent intt = new Intent(LaunchActivity.this, InfosActivity.class);
        startActivity(intt);
        finish();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {


        }
    };
}
