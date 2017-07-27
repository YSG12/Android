package com.stav.mobilesafe.activity;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.stav.mobilesafe.R;
import com.stav.mobilesafe.db.dao.BlackNumberDao;
import com.stav.mobilesafe.db.domain.BlackNumberInfo;
import com.stav.mobilesafe.utils.ToastUtil;


import java.util.List;

import static com.stav.mobilesafe.R.id.iv_delete;
import static com.stav.mobilesafe.R.id.lv_contact;

//1.复用covertView
//2.对findViewById次数的优化，使用ViewHolder
//3.将ViewHolder定义成静态，不会去创建多个对象
//4.listView有多个条目的时候，可以进行分页处理，每一次加载20次条，逆序返回
public class BlackNumberActivity extends AppCompatActivity {

    private Button bt_add;
    private ListView lv_blacknumber;
    private BlackNumberDao mDao;
    private List<BlackNumberInfo> mBlackNumberList;
    private MyAdapter mAdapter;
    private int mode = 1;
    private int mCount;
    private boolean mIsLoad = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            //4.告知listView可以去设置数据适配器
            if (mAdapter == null){
                mAdapter = new MyAdapter();
                lv_blacknumber.setAdapter(mAdapter);
            } else{
                mAdapter.notifyDataSetChanged();
            }



        }
    };

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBlackNumberList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBlackNumberList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
//            View view = null;
//            if (convertView == null){
//                view = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);
//            } else {
//                view = convertView;
//            }
            //1.复用ViewHolder步骤一
            ViewHolder holder = null;
            //
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);
                //将findViewById的过程封装到convertView == null的情境下去做执行
                //2.减少findViewById的使用次数
                //复用ViewHolder步骤三
                holder = new ViewHolder();
                //复用ViewHolder步骤四
                holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
                holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
                holder.iv_delete = (ImageView) convertView.findViewById(iv_delete);
                //复用ViewHolder步骤五
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //1.数据库的删除
                    mDao.delete(mBlackNumberList.get(position).phone);
                    //2.集合中删除
                    mBlackNumberList.remove(position);
                    //3.,通知数据适配器刷新
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }

                }
            });
            holder.tv_phone.setText(mBlackNumberList.get(position).phone);
            mode = Integer.parseInt(mBlackNumberList.get(position).mode);
            switch (mode) {
                case 1:
                    holder.tv_mode.setText("拦截短信");
                    break;
                case 2:
                    holder.tv_mode.setText("拦截电话");
                    break;
                case 3:
                    holder.tv_mode.setText("拦截所有");
                    break;
                default:
                    break;
            }
            return convertView;
        }
    }

    //复用viewHolder步骤二
    public static class ViewHolder {
        TextView tv_phone;
        TextView tv_mode;
        ImageView iv_delete;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_number);
        initUI();
        initData();
    }

    /**
     * 用于获取数据库中所有电话号码
     */
    private void initData() {
        new Thread(){
            @Override
            public void run() {

                //1.获取操作数据库的对象
                mDao = BlackNumberDao.getInstance(getApplication());
                //查询部分数据
                mBlackNumberList = mDao.find(0);
                mCount = mDao.getCount();
                //通过消息机制告知主线程可以去试用班含数据的集合
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }



    private void initUI() {
        bt_add = (Button) findViewById(R.id.bt_add);
        lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        //监听其滚动状态
        lv_blacknumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            //滚动过程中，状态发生改变调用的方法
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mBlackNumberList != null) {
                    //条件1：滚动到停滞状态，条件2：最后一个条目可见
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE &&
                            lv_blacknumber.getLastVisiblePosition() >= mBlackNumberList.size() - 1
                            && !mIsLoad) {
                        /*mIsLoad 防止重复加载的变量
                        如果当前正在加载mIsload就会为true，而本次加载完成后，而将mIsLoad就会改成false
                        如果下一次加载需要做执行的时候，会判断上述mIsLoad变量是否为false 如果为true
                        就需要等待上一次加载完成，将其值改为false后再做加载*/
                        //加载下一页数据
                        //如果条目的总数大于集合的大小的时候，才可以继续加载更多
                        if (mCount > mBlackNumberList.size()) {
                            new Thread() {
                                @Override
                                public void run() {
                                    //1.获取操作数据库的对象
                                    mDao = BlackNumberDao.getInstance(getApplication());
                                    //2.查询部分数据(10)
                                    List<BlackNumberInfo> moreData = mDao.find(mBlackNumberList.size());
                                    //3.添加下一页数据的过程
                                    mBlackNumberList.addAll(moreData);
                                    //4.通知数据适配器刷新
                                    mHandler.sendEmptyMessage(0);
                                }
                            }.start();
                        } else {
                            ToastUtil.show(getApplicationContext(),"没有更多信息了");
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    protected void showDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(getApplicationContext(), R.layout.dialog_add_blacknumber, null);
        dialog.setView(view,0,0,0,0);

        final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
        final RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        Button bt_cacel = (Button) view.findViewById(R.id.bt_cancel);

        //监听其选中条目的切换过程
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                switch (checkedId) {
                    case R.id.rb_sms:
                        //拦截短信
                        mode = 1;
                        break;
                    case R.id.rb_phone:
                        //拦截电话
                        mode = 2;
                        break;
                    case R.id.rb_all:
                        //拦截所有
                        mode = 3;
                        break;
                    default:
                        break;
                }
            }
        });

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.获取输入框中的电话号码
                String phone = et_phone.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)){
                    //2.数据库插入当前输入的拦截号码
                    mDao.insert(phone,mode+"");
                    //3.让数据库和集合保持同步（1.数据库中数据重新读一遍，2.手动向集合添加对象）
                    BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                    blackNumberInfo.phone = phone;
                    blackNumberInfo.mode = mode +"";
                    //4.将对象插入到集合的最顶部
                    mBlackNumberList.add(0,blackNumberInfo);
                    //5.通知数据适配器刷新（数据适配器器中数据改变）
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    //6.隐藏对话框
                    dialog.dismiss();
                } else {
                    ToastUtil.show(getApplicationContext(),"请输入拦截号码");
                }
            }
        });
        bt_cacel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
