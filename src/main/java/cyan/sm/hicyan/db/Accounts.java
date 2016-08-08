package cyan.sm.hicyan.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wx on 16/8/1.
 */
public class Accounts {
    public static final String tableName = "accounts";

    public static enum c {
        id, name, loginname, pwd
    }

    public static void createTable(SQLiteDatabase db) {
        StringBuffer sb = new StringBuffer();
        sb.append("CREATE TABLE ").append(tableName).append("(");

        sb.append(c.id.name()).append(" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");// AUTOINCREMENT
        sb.append(c.name.name()).append(" varchar(50) NOT NULL , ");
        sb.append(c.loginname.name()).append(" varchar(100) NOT NULL , ");
        sb.append(c.pwd.name()).append(" varchar(32) NOT NULL ");

        sb.append(")");

        db.execSQL(sb.toString());
    }

    public static void insert(String name, String loginname, String pwd, Context ctx) {
        ContentValues cv = new ContentValues();
        cv.put(c.name.name(), name);
        cv.put(c.loginname.name(), loginname);
        cv.put(c.pwd.name(), pwd);

        SQLiteDatabase db = DB.getInstance(ctx).getWritableDatabase();
        db.insert(Accounts.tableName, null, cv);
        db.close();

    }

    public static List<Map<String, String>> all(Context ctx) {
        Cursor cs = null;
        SQLiteDatabase db = null;
        List<Map<String, String>> retList = null;
        try {
            db = DB.getInstance(ctx).getReadableDatabase();
            cs = db.query(Accounts.tableName, null, null, null, null, null, null);
            while (cs.moveToNext()) {
                if(retList==null)
                    retList=new ArrayList<>(cs.getCount());
                Map<String, String> m = new HashMap<>();
                m.put(c.name.name(), cs.getString(cs.getColumnIndex(c.name.name())));
                m.put(c.loginname.name(), cs.getString(cs.getColumnIndex(c.loginname.name())));
                m.put(c.pwd.name(), cs.getString(cs.getColumnIndex(c.pwd.name())));
                retList.add(m);
            }
        } catch (Exception e) {

        } finally {
            if (cs != null && !cs.isClosed())
                cs.close();
            if (db != null && db.isOpen())
                db.close();
        }
        return retList==null?new ArrayList<Map<String, String>>(0):retList;
    }
}
