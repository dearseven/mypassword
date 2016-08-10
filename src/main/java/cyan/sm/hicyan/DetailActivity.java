package cyan.sm.hicyan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        View view = findViewById(R.id.detail_container);
        int top = getIntent().getIntExtra("top", 0);
        int width = getIntent().getIntExtra("width", 0);
        int height = getIntent().getIntExtra("height", 0);
        //Toast.makeText(DetailActivity.this, "why:" + top + " " + width + " " + height, Toast.LENGTH_LONG).show();

        anim(view, top, height, width);
    }

    void anim(final View view, int top, int height, int width) {
        //记住括号哦，我这里调试了一小时
//        float delta = ((float) width) / ((float) height);
//        Log.i("hicyan anim", "delta:" + delta);
//        float fromDelta, toDelta, fromY, toY;
//        if (!isEnter) {
//            fromDelta = 1f;
//            toDelta = delta;
//            fromY = top;
//            toY = 0;
//        } else {
//            fromDelta = delta;
//            toDelta = 1f;
//            fromY = 0;
//            toY = top;
//        }

//        Animation anim = new ScaleAnimation(fromDelta, toDelta,
//                // Start and end values for the X axis scaling
//                fromDelta, toDelta, // Start and end values for the Y axis scaling
//                Animation.RELATIVE_TO_SELF, 0.5f, // scale from mid of x
//                Animation.RELATIVE_TO_SELF, 0f); // scale from start of y

        final float fY = ((float) height) / getResources().getDisplayMetrics().heightPixels;
        Animation anim = new ScaleAnimation(1, 1, fY, fY);

        Animation trans = new TranslateAnimation(0, 0f, top, 0);
        AnimationSet set = new AnimationSet(true);
        //添加并行动画
        set.addAnimation(anim);
        set.addAnimation(trans);
        //动画结束后保持原样
        set.setFillEnabled(true);
        set.setFillAfter(true);
        //监听器
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation anim = new ScaleAnimation(1, 1, fY, 1);
                anim.setDuration(500);
                view.startAnimation(anim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        set.setDuration(500);
        view.startAnimation(set);

    }


}