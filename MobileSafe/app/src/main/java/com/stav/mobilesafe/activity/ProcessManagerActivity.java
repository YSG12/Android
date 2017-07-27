package com.stav.mobilesafe.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.stav.mobilesafe.R;
import com.stav.mobilesafe.db.domain.ProcessInfo;
import com.stav.mobilesafe.engine.ProcessInfoProvider;
import com.stav.mobilesafe.utils.ConstantValue;
import com.stav.mobilesafe.utils.SpUtil;
import com.stav.mobilesafe.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class ProcessManagerActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_process_count,tv_memory_info,tv_des;
    private long mAvailSpace;
    private String mStrTotalSpace;
    private ListView lv_process_list;
    private Button bt_all,bt_reverse,bt_clear,bt_setting;
    private int mProcessCount;
    private MyAdapter mAdapter;
    private ProcessInfo mProcessInfo;
    private List<ProcessInfo> mProcessInfoList;
    private ArrayList<ProcessInfo> mSystemList;
    private ArrayList<ProcessInfo> mCustomerList;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mAdapter = new MyAdapter();
            lv_process_list.setAdapter(mAdapter);
            if (tv_des != null && mCustomerList != null) {
                tv_des.setText("用户应用(" + mCustomerList.size() + ")");
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);
        initUI();
        initTitleData();
        initListData();
    }

    /**
     * 获取进程列表
     */
    private void initListData() {
        getData();

    }

    class MyAdapter extends BaseAdapter {
        //获取数据适配器中条目类型的总数，修改成两种（纯文本，图片+文字）
        @Override
        public int getViewTypeCount() {

            return super.getViewTypeCount()+1;
        }

        //指定索引指向的项目类型，条目类型状态码指定（0（复用系统）1）
        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCustomerList.size()+1) {
                //返回0，表示纯文本的状态码
                return 0;
            } else {
                //返回1，代表图片+文本条目状态码
                return 1;
            }
        }

        @Override
        //listView中添加两个描述条目
        public int getCount() {
            if (SpUtil.getBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM,false)) {
                return mCustomerList.size() + mSystemList.size() + 2;
            } else {
                return mCustomerList.size()+1;
            }
        }

        @Override
        public ProcessInfo getItem(int position) {
            if (position == 0 || position == mCustomerList.size()+1) {
                return null;
            } else {
                if (position < mCustomerList.size()+1) {
                    return mCustomerList.get(position-1);
                } else {
                    //返回系统应用对应条目的对象
                    return mSystemList.get(position - mCustomerList.size()-2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == 0){
                //展示灰色纯文本条目
                //展示图片+文字条目
                AppManagerActivity.ViewTitleHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(),R.layout.listview_app_item_title,null);
                    holder = new AppManagerActivity.ViewTitleHolder();
                    holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                    convertView.setTag(holder);
                } else {
                    holder = (AppManagerActivity.ViewTitleHolder) convertView.getTag();
                }
                if (position == 0) {
                    holder.tv_title.setText("用户进程("+mCustomerList.size()+")");
                } else {
                    holder.tv_title.setText("系统进程("+mSystemList.size()+")");

                }
                return convertView;

            }else {
                //展示图片+文字条目
                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(),R.layout.listview_process_item,null);
                    holder = new ViewHolder();
                    holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                    holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                    holder.tv_memory_info = (TextView) convertView.findViewById(R.id.tv_memory_info);
                    holder.cb_box = (CheckBox) convertView.findViewById(R.id.cb_box);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.iv_icon.setBackgroundDrawable(getItem(position).icon);
                holder.tv_name.setText(getItem(position).name);
                String strSize = Formatter.formatFileSize(getApplicationContext(), getItem(position).getMemSize());
                holder.tv_memory_info.setText(strSize);
                //本进程不能被选中，所以应先将checkbox隐藏掉
                if (getItem(position).packageName.equals(getPackageName())) {
                    holder.cb_box.setVisibility(View.GONE);
                } else {
                    holder.cb_box.setVisibility(View.VISIBLE);
                }
                holder.cb_box.setChecked(getItem(position).isCheck);
                return convertView;
            }
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_memory_info;
        CheckBox cb_box;
    }

    static class ViewTitleHolder {
        TextView tv_title;
    }


    //获取数据
    private void getData() {
        new Thread(){
            @Override
            public void run() {
                mProcessInfoList = ProcessInfoProvider.getProcessInfo(getApplicationContext());
                mSystemList = new ArrayList<>();
                mCustomerList = new ArrayList<>();
                for (ProcessInfo info : mProcessInfoList) {
                    if (info.isSystem) {
                        //系统应用
                        mSystemList.add(info);
                    } else{
                        //用户应用
                        mCustomerList.add(info);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initTitleData() {
        mProcessCount = ProcessInfoProvider.getProcessCount(this);
        tv_process_count.setText("进程总数:"+mProcessCount);
        //获取可用内存大小，并且格式化
        mAvailSpace = ProcessInfoProvider.getAvailSpace(this);
        mStrTotalSpace = Formatter.formatFileSize(this, mAvailSpace);
        //总运行内存大小，并且格式化
        long totalSpace = ProcessInfoProvider.getTotalSpace(this);
        String strTotalSpace = Formatter.formatFileSize(this, totalSpace);
        tv_memory_info.setText("剩余/总共:"+mStrTotalSpace+"/"+strTotalSpace);
    }

    private void initUI() {
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        tv_memory_info = (TextView) findViewById(R.id.tv_memory_info);
        tv_des = (TextView) findViewById(R.id.tv_des);
        lv_process_list = (ListView) findViewById(R.id.lv_process_list);
        bt_all = (Button) findViewById(R.id.bt_all);
        bt_reverse = (Button) findViewById(R.id.bt_reverse);
        bt_clear = (Button) findViewById(R.id.bt_clear);
        bt_setting = (Button) findViewById(R.id.bt_setting);

        bt_all.setOnClickListener(this);
        bt_reverse.setOnClickListener(this);
        bt_clear.setOnClickListener(this);
        bt_setting.setOnClickListener(this);

        lv_process_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //滚动过程中调用的方法
                //AbsListView中View就是listView对象
                //firstVisibleItem 第一个可见的条目
                //visibleItemCount 当前一个屏幕可见数目
                //总共条目总数
                if (mCustomerList != null && mSystemList != null){
                    if (firstVisibleItem >= mCustomerList.size()+1){
                        //滚动到了系统应用条目
                        tv_des.setText("系统进程("+mSystemList.size()+")");
                    } else{
                        //滚动到了用户应用条目
                        tv_des.setText("用户进程("+mCustomerList.size()+")");
                    }
                }
            }
        });

        lv_process_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //view选中条目指向的view对象
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //view店中条目指向的view对象
                if (position == 0 || position == mCustomerList.size()+1) {
                    return;
                } else {
                    if (position < mCustomerList.size()+1) {
                        mProcessInfo = mCustomerList.get(position - 1);
                    } else {
                        //返回系统应用对应条目的对象
                        mProcessInfo = mSystemList.get(position - mCustomerList.size()-2);
                    }
                    if (mProcessInfo != null) {
                        if (!mProcessInfo.packageName.equals(getPackageName())){
                            //选中条目指向的对象和本应用的包名不一致，才需要状态取反和设置单选框状态
                            mProcessInfo.isCheck = !mProcessInfo.isCheck;
                            //checkbox显示状态切换
                            //通过选中的条目的view对象，findViewById找到此条目指向的cb_box，然后切换此状态
                            CheckBox cb_box = (CheckBox) view.findViewById(R.id.cb_box);
                            cb_box.setChecked(mProcessInfo.isCheck);
                        }
                    }
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_all:
                selectAll();
                break;
            case R.id.bt_reverse:
                selectReverse();
                break;
            case R.id.bt_clear:
                clearAll();
                break;
            case R.id.bt_setting:
                setting();
                break;

        }
    }

    /**
     * 跳转到设置页面
     */
    private void setting() {
        Intent intent = new Intent(this, ProcessSettingActivity.class);
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //通知数据适配器刷新
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 一键清理选中进程
     */
    private void clearAll() {
        //1.获取选中进程
        //2.创建一个记录需要杀死的进程集合
        ArrayList<ProcessInfo> killProcessList = new ArrayList<>();
        for (ProcessInfo processInfo: mCustomerList) {
            if (processInfo.getPackageName().equals(getPackageName())) {
                continue;
            }
            if (processInfo.isCheck){
                //不能再集合循环过程中去移除集合中的对象
                //mProcessInfoList.remove(processInfo);
                //3.记录需要杀死的用户进程
                killProcessList.add(processInfo);
            }
        }
        for (ProcessInfo processInfo: mSystemList) {
            //4.记录需要杀死的系统进程
            if (processInfo.isCheck) {
                killProcessList.add(processInfo);
            }
        }
        //5.循环遍历killProcessList，然后去移除mCustomerList和mSystemList对象
        long totalReleaseSpace = 0;
        for (ProcessInfo processInfo: killProcessList) {
            //6.判断当前进程在那个集合中，从所在集合中移除
            if (mCustomerList.contains(processInfo)) {
                mCustomerList.remove(processInfo);
            }
            if (mSystemList.contains(processInfo)) {
                mSystemList.remove(processInfo);
            }
            //7.杀死记录在killProcessList中的进程
            ProcessInfoProvider.killProcess(getApplicationContext(),processInfo);
            //记录释放空间的总大小
            totalReleaseSpace += processInfo.memSize;
        }
        //8.在集合改变后，需要通知数据适配器刷新
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        //9.进程总数的更新
        mProcessCount -= killProcessList.size();
        //10.更新可用剩余空间(释放控件+原有剩余空间==当前剩余空间)
        mAvailSpace += totalReleaseSpace;
        //11.根据进程总数和剩余空间
        tv_process_count.setText("进程总数:"+mProcessCount);
        long totalSpace = ProcessInfoProvider.getTotalSpace(this);
        String strTotalSpace = Formatter.formatFileSize(this, totalSpace);
        tv_memory_info.setText("剩余/总共:"+Formatter.formatFileSize(this,mAvailSpace)+"/"+strTotalSpace);
        //12.通过吐司告知用户，释放了多少空间，杀死了几个进程，占位符
        String totalRelease = Formatter.formatFileSize(this, totalReleaseSpace);
        ToastUtil.show(getApplicationContext(),"杀死了"+killProcessList.size()+"个进程，释放了"+totalRelease+"空间");
//        ToastUtil.show(getApplicationContext(),String.format("杀死了d%个进程，释放了s%空间",killProcessList.size(),totalRelease));
    }

    /**
     * 反选
     */
    private void selectReverse() {
        //1.将所有的集合中的对象上isCheck字段取反，代表全选，排除当前应用
        for (ProcessInfo processInfo: mCustomerList) {
            if (processInfo.getPackageName().equals(getPackageName())) {
                continue;
            }
            processInfo.isCheck = !processInfo.isCheck;
        }
        for (ProcessInfo processInfo: mSystemList) {
            processInfo.isCheck = !processInfo.isCheck;
        }
        //2.通知数据适配器刷新
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 全选
     */
    private void selectAll() {
        //1.将所有的集合中的对象上isCheck字段设置为true，代表全选，排除当前应用
        for (ProcessInfo processInfo: mCustomerList) {
            if (processInfo.getPackageName().equals(getPackageName())) {
                continue;
            }
            processInfo.isCheck = true;
        }
        for (ProcessInfo processInfo: mSystemList) {
            processInfo.isCheck = true;
        }
        //2.通知数据适配器刷新
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
