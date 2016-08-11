package cyan.sm.hicyan;

import android.app.Application;

import cyan.sm.hicyan.utils.CrashHandler;

/**
 * Created by Administrator on 2016/8/9.
 */
public class HiCyan extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler ch=new CrashHandler(this);
        Thread.setDefaultUncaughtExceptionHandler(ch);
    }

    public DetailActivity da=null;

    public void finishDa(){
        if(da!=null){
            da.finish();
        }
    }
}
