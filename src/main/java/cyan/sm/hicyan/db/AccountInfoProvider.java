package cyan.sm.hicyan.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class AccountInfoProvider extends ContentProvider {
    //定义一个UriMatcher类对象，用来匹配Uri的。
    public static final UriMatcher uriMatcher;
    //组时的ID
    public static final int all = 1;
    //单个时的ID
    public static final int one = 2;

    public static final String AUTHORTY = "com.wx.hicyan.AccountInfoProvider";


    public static final String BASE_URI = "content://" + AUTHORTY + "/AccountInfos";
    //CONTENT_URI为常量Uri; parse是将文本转换成Uri
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORTY + "/AccountInfos");
    //返回ContentProvider中表的数据类型
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.AccountInfoProvider.AccountInfo";
    //返回ContentProvider表中item的数据类型
    public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd.AccountInfoProvider.AccountInfo";

    static {

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);//UriMatcher.NO_MATCH表示不匹配任何路径的返回码
        uriMatcher.addURI(AccountInfoProvider.AUTHORTY, "AccountInfos", all);
        uriMatcher.addURI(AccountInfoProvider.AUTHORTY, "AccountInfos/#", one);//后面加了#表示为单个

    }

    private DB db = null;

    public AccountInfoProvider() {

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case all:
                count = db.getWritableDatabase().delete(
                        Accounts.tableName,
                        selection,
                        selectionArgs);
                break;
            case one:
                String id = uri.getPathSegments().get(1);
                count = db.getWritableDatabase().delete(
                        Accounts.tableName,
                        Accounts.c.id.name() + " = " + id +
                                (!TextUtils.isEmpty(selection) ? " AND (" +
                                        selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case all:
                return CONTENT_TYPE;
            case one:
                return CONTENT_TYPE_ITEM;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //---add a new book---
        long rowID = db.getWritableDatabase().insert(
                Accounts.tableName,
                "",
                values);
        //---if added successfully---
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public boolean onCreate() {

        db = DB.getInstance(this.getContext());

        return db == null ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder sqlBuider = new SQLiteQueryBuilder();
        sqlBuider.setTables(Accounts.tableName);

        if (uriMatcher.match(uri) == one) {
            sqlBuider.appendWhere(Accounts.c.id.name() + " = " + uri.getPathSegments().get(1));
        }

        if (sortOrder == null || sortOrder == "") {
            sortOrder = Accounts.c.id.name();
        }

        Cursor c = sqlBuider.query(db.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
        return c;

        // throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)) {
            case all:
                count = db.getWritableDatabase().update(Accounts.tableName, values, selection, selectionArgs);
                break;
            case one:
                count = db.getWritableDatabase().update(Accounts.tableName, values, Accounts.c.id.name() + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? " AND (" +
                                        selection + ')' : ""),
                        selectionArgs);
                break;

        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
