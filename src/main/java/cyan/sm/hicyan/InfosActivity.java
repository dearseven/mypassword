package cyan.sm.hicyan;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;

import cyan.sm.hicyan.db.AccountInfoProvider;
import cyan.sm.hicyan.db.Accounts;
import cyan.sm.hicyan.db.EnDe;


/**
 * 用户主要列表界面
 */
public class InfosActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private Toolbar toolbar;
    private Dialog dialog;

    private EditText etName;
    private EditText etAcc;
    private EditText etPwd;

    private ContentResolver cr = null;

    private List<Map<String, String>> mDatas = new ArrayList<Map<String, String>>();
    TodayRecyclerViewAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_order_list);

        initActionBar();

        cr = InfosActivity.this.getContentResolver();

        initRecyclerView();
    }

    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.main_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TodayRecyclerViewAdapter();
        mRecyclerView.setAdapter(adapter);
        new MyTask().execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        cr.registerContentObserver(AccountInfoProvider.CONTENT_URI, true, co);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cr.unregisterContentObserver(co);
    }

    private void initActionBar() {
        toolbar = (Toolbar) findViewById(R.id.info_toobar);
        //toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle("hi,cyan!");
        //toolbar.setSubtitle("remember your any passworld!");
        //这个位置不能错
        setSupportActionBar(toolbar);
        //设置返回按钮
        toolbar.setNavigationIcon(android.R.drawable.arrow_down_float);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                //创建输入用户名和密码的窗口
                View view = LayoutInflater.from(this).inflate(
                        R.layout.input_dialog, null);
                dialog = new Dialog(this, R.style.myTransparentDialogTheme);
                dialog.setContentView(view);
                dialog.show();

                // 注册按钮的点击事件
                Button confirm = (Button) view
                        .findViewById(R.id.input_dialog_confirm);
                Button cancel = (Button) view
                        .findViewById(R.id.input_dialog_cancel);

                confirm.setOnClickListener(this);
                cancel.setOnClickListener(this);

                etName = (EditText) view.findViewById(R.id.input_web_name);
                etAcc = (EditText) view.findViewById(R.id.input_acc);
                etPwd = (EditText) view.findViewById(R.id.input_pwd);
                break;
            case R.id.menu_finish:
                finish();
                break;
            default:
                break;
        }
//         Toast.makeText(MainActivity.this, ""+item.getItemId(), Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);
    }

    private final Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private final ContentObserver co = new ContentObserver(h) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Log.i("hicyan", "ContentObserver co selfChange:" + selfChange + ",uri:" + uri);
            new MyTask().execute();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.input_dialog_confirm:
                String name = etName.getText().toString();
                String acc = etAcc.getText().toString();
                String pwd = EnDe.en(etPwd.getText().toString());
                //插入数据
                Uri uri = AccountInfoProvider.CONTENT_URI;
                ContentValues values = new ContentValues();
                values.put(Accounts.c.name.name(), name);
                values.put(Accounts.c.loginname.name(), acc);
                values.put(Accounts.c.pwd.name(), pwd);
                cr.insert(uri, values);
                //不管怎么样都要dismiss 所以这里不break;
            case R.id.input_dialog_cancel:
                dialog.dismiss();
                dialog = null;
                break;
        }
    }

    class TodayRecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View holdView = LayoutInflater.from(InfosActivity.this).inflate(R.layout.accounts_item_layout, parent, false);
            MyViewHolder holder = new MyViewHolder(holdView);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            if (position < mDatas.size()) {
                holder.one.setText(mDatas.get(position).get(Accounts.c.name.name()).charAt(0) + "");
                holder.two.setText(mDatas.get(position).get(Accounts.c.loginname.name()));
            }
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView one;
        public TextView two;

        public MyViewHolder(View view) {
            super(view);
            one = (TextView) view.findViewById(R.id.accounts_item_layout_icon_txt);
            two = (TextView) view.findViewById(R.id.accounts_item_layout_name_txt);
        }
    }

    public class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Cursor cursor = cr.query(Uri.parse(AccountInfoProvider.BASE_URI), null,
                    null, null, null);
            mDatas.clear();
            while (cursor.moveToNext()) {
                Map<String, String> m = new HashMap<String, String>(4);
                m.put(Accounts.c.id.name(), cursor.getInt(cursor.getColumnIndex(Accounts.c.id.name())) + "");
                m.put(Accounts.c.name.name(), cursor.getString(cursor.getColumnIndex(Accounts.c.name.name())));
                m.put(Accounts.c.loginname.name(), cursor.getString(cursor.getColumnIndex(Accounts.c.loginname.name())));
                m.put(Accounts.c.pwd.name(), cursor.getString(cursor.getColumnIndex(Accounts.c.pwd.name())));
                mDatas.add(m);
                Log.i("hicyan", "doInBackground:" + cursor.getCount());
            }
            cursor.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //刷新数据
            adapter.notifyDataSetChanged();
            //super.onPostExecute(aVoid);
        }
    }
}
