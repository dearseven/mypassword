package cyan.sm.hicyan;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cyan.sm.hicyan.db.AccountInfoProvider;
import cyan.sm.hicyan.db.Accounts;
import cyan.sm.hicyan.db.EnDe;
import cyan.sm.hicyan.utils.DividerItemDecoration;


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

    /**
     * adapter
     */
    public TodayRecyclerViewAdapter adapter = null;

    /**
     * 实现ItemTouchHelper.Callback
     */
    RecyclerViewItemTouchCallBck recyclerViewItemTouchCallBck = null;
    /**
     * ItemTouchHelper处理RecyclerView的拖动删除和拖动交换item位置
     */
    ItemTouchHelper itemTouchHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_today_order_list);

        initActionBar();

        cr = InfosActivity.this.getContentResolver();

        initRecyclerView();
    }

    /**
     * 对recyclerview做一些基本的初始化，以及获取数据
     */
    private void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.main_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new TodayRecyclerViewAdapter();
        mRecyclerView.setAdapter(adapter);

        recyclerViewItemTouchCallBck = new RecyclerViewItemTouchCallBck();
        itemTouchHelper = new ItemTouchHelper(recyclerViewItemTouchCallBck);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        new MyTask().execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        cr.registerContentObserver(AccountInfoProvider.CONTENT_URI, true, co);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences sp = getSharedPreferences("lock", MODE_PRIVATE);
        if (System.currentTimeMillis() - sp.getLong("time", 0) > 5000) {
            Intent it = new Intent(this, LaunchActivity.class);
            startActivity(it);
            finish();
        }
        // Toast.makeText(this,sp.getLong("time",0)+"",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cr.unregisterContentObserver(co);
    }

    /**
     * 初始化toolbar
     */
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
        return super.onOptionsItemSelected(item);
    }

    private final Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    /**
     * 数据库采用ContentProvide 这里注册一个数据是为了获取服务器更新
     */
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
                if (name.trim().equals("")) {
                    name = "匿名";
                }

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

    /**
     * recyclerview的适配器
     */
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
                final MyViewHolder f_Holder = holder;
                final int f_Position = position;

                holder.one.setText(mDatas.get(position).get(Accounts.c.name.name()).charAt(0) + "");
                holder.two.setText(mDatas.get(position).get(Accounts.c.loginname.name()));

                holder.one.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        //如果按下
                        if (/*MotionEventCompat.getActionMasked(event)*/event.getAction() == MotionEvent.ACTION_DOWN) {
                            itemTouchHelper.startDrag(f_Holder);
                        }
                        return false;
                    }
                });


                holder.two.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = mDatas.get(f_Position).get(Accounts.c.id.name());
                        int top = v.getTop();
                        int height = v.getHeight();
                        int width = v.getWidth();
                        Intent intent = new Intent(InfosActivity.this, DetailActivity.class);

                        //获取高度位置
                        int[] location = new int[2];
                        v.getLocationOnScreen(location);
                        int x = location[0];
                        int y = location[1];
                        int result = 0;
                        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                        if (resourceId > 0) {
                            result = getResources().getDimensionPixelSize(resourceId);
                        }
                        y = y - result;//减去状态栏

                        intent.putExtra("id", id);
                        intent.putExtra("top", y);
                        intent.putExtra("width", width);
                        intent.putExtra("height", height);
                        //Toast.makeText(InfosActivity.this,top+" "+width+" "+height,Toast.LENGTH_LONG).show();
                        //Toast.makeText(InfosActivity.this,x+" "+y,Toast.LENGTH_LONG).show();

                        //获取数据然后传过去
                        Map<String, String> m = mDatas.get(f_Position);
                        intent.putExtra("name", m.get(Accounts.c.name.name()));
                        intent.putExtra("loginname", m.get(Accounts.c.loginname.name()));
                        intent.putExtra("pwd", m.get(Accounts.c.pwd.name()));

                        startActivity(intent);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        /**
         * 当触发删除的时候调用这个方法
         *
         * @param fromPosition
         * @param toPosition
         * @return
         */
        public boolean onItemMove(int fromPosition, int toPosition) {
            //交换mItems数据的位置
            Collections.swap(mDatas, fromPosition, toPosition);
            //交换RecyclerView列表中item的位置
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        /**
         * 当触发交换数据（调整位置的时候触发这个方法）
         *
         * @param position
         */
        public void onItemDismiss(int position) {
            //删除mItems数据
            mDatas.remove(position);
            //删除RecyclerView列表对应item
            notifyItemRemoved(position);
        }
    }

    /**
     * recyclerview的viewholder
     */
    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView one;
        public Button two;

        public MyViewHolder(View view) {
            super(view);
            one = (TextView) view.findViewById(R.id.accounts_item_layout_icon_txt);
            two = (Button) view.findViewById(R.id.accounts_item_layout_name_txt);
        }
    }

    /**
     * 异步任务，获取数据
     */
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

    /**
     * RecyclerView的item的拖拽功能，包括删除和调整位置!
     */
    public class RecyclerViewItemTouchCallBck extends ItemTouchHelper.Callback {
        /**
         * 这个方法是用来设置我们拖动的方向以及侧滑的方向的
         */
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            //如果是ListView样式的RecyclerView
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                //设置拖拽方向为上下
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                //设置侧滑方向为从左到右和从右到左都可以
                final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                //将方向参数设置进去
                return makeMovementFlags(dragFlags, swipeFlags);
            } else {//如果是GridView样式的RecyclerView
                //设置拖拽方向为上下左右
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                //不支持侧滑
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }
        }

        /**
         * 当我们拖动item时会回调此方法
         */
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //如果两个item不是一个类型的，我们让他不可以拖拽
            if (viewHolder.getItemViewType() != target.getItemViewType()) {
                return false;
            }
            //回调adapter中的onItemMove方法
            InfosActivity.this.adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        /**
         * 当我们侧滑item时会回调此方法
         */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            InfosActivity.this.adapter.onItemDismiss(viewHolder.getAdapterPosition());
        }
    }


}
