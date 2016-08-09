package cyan.sm.hicyan;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 用户主要列表界面
 */
public class InfosActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;

    private Toolbar toolbar;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_order_list);

        initActionBar();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.input_dialog_confirm:

                //不管怎么样都要dismiss 所以这里不break;
            case R.id.input_dialog_cancel:
                dialog.dismiss();
                dialog=null;
                break;
        }
    }

    class TodayRecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public MyViewHolder(View view) {
            super(view);
        }
    }

    public class MyTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
