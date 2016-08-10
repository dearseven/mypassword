package cyan.sm.hicyan.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by apple on 16/7/25.
 */
public class DB extends SQLiteOpenHelper {

    /** 单例 */
    private static DB sInstance;
    private static Context _context;

    /**
     * 获取单例
     *
     * @param context
     *            Context
     * @return 单例
     */
    public static DB getInstance(Context context) {
        if (sInstance == null) {
            synchronized (DB.class) {
                if (sInstance == null) {
                    _context = context;
                    sInstance = new DB(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    /**
     * just为了方便
     *
     * @param context
     */
    private DB(Context context) {
        super(context, "db_name_HI_CYAN", null, 1);
    }

    public DB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Accounts.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
