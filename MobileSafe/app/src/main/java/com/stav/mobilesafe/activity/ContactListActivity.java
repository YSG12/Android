package com.stav.mobilesafe.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.stav.mobilesafe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {

    private static final String tag = "ContactListActivity";
    private ListView lv_contact;
    private List<HashMap<String,String>> contactList = new ArrayList<HashMap<String,String>>();
    private MyAdapter mAdapter;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //8.填充数据适配器
            mAdapter = new MyAdapter();
            lv_contact.setAdapter(mAdapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        initUI();
        initData();
    }

    /**
     * 获取系统联系人数据
     */
    private void initData() {
        //读取系统联系人可能是一个耗时操作，放置到主线程中去处理
        new Thread(){
            @Override
            public void run() {
                //1.获取内容解析器对象
                ContentResolver contentResolver = getContentResolver();
                //2.做查询系统联系人数据表过程（读取联系人权限）
                Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/raw_contacts"),
                        new String[]{"contact_id"}, null, null, null);
                contactList.clear();
                //3.循环游标，直到没有数据位置
                while (cursor.moveToNext()){
                    String id = cursor.getString(0);
                    Log.i(tag,"id"+id);
                    //4.根据用户唯一性id值，查询data表和mimetype表生成的视图
                    Cursor indexCursor = contentResolver.query(Uri.parse("content://com.android.contacts/data"), new String[]{"data1", "mimetype"},
                            "raw_contact_id=?", new String[]{id
                    }, null);
                    //5.循环获取每一个联系人的电话号码以及姓名，数据类型
                    HashMap<String, String> hashMap = new HashMap<>();
                    while (indexCursor.moveToNext()) {
                        String data = indexCursor.getString(0);
                        String type = indexCursor.getString(1);

                        //区分类型去填充数据
                        if (type.equals("vnd.android.cursor.item/phone_v2")) {
                            //数据非空的判断
                            if (!TextUtils.isEmpty(data)) {
                                hashMap.put("phone", data);
                            }
                        } else if (type.equals("vnd.android.cursor.item/name")) {
                            if (!TextUtils.isEmpty(data)) {
                                hashMap.put("name", data);
                            }
                        }
                    }
                    indexCursor.close();
                    contactList.add(hashMap);
                }
                cursor.close();
                //7.消息机制 发送一个空的消息，告知主线程可以去使用子线程已经已经填充好的数据集合
                mHandler.sendEmptyMessage(0);

            }
        }.start();



    }

    /**
     * 初始化UI
     */
    private void initUI() {
        lv_contact = (ListView) findViewById(R.id.lv_contact);
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //1.获取点中条目的索引指向集合的对象
                if (mAdapter != null) {
                    //2.获取当前条目的电话号码
                    HashMap<String, String> hashMap = mAdapter.getItem(position);
                    //3.此电话号码需要给第三个导航界面使用
                    String phone = hashMap.get("phone");
                    //4.在借宿次洁面会道歉一个导航界面的时候，需要将数据返回过去
                    Intent intent = new Intent();
                    intent.putExtra("phone",phone);
                    setResult(0, intent);

                    finish();

                }
            }
        });
    }

    /**
     * 设置数据适配器
     */
    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return contactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.list_contact_item, null);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);

            tv_name.setText(getItem(position).get("name"));
            tv_phone.setText(getItem(position).get("phone"));

            return view;
        }
    }
}
