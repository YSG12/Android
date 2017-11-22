package com.stav.ideastreet.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.stav.ideastreet.R;
import com.stav.ideastreet.base.ParentWithNaviActivity;
import com.stav.ideastreet.bean.MyUser;
import com.stav.ideastreet.bean.Post;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import static com.stav.ideastreet.base.BaseApplication.showToast;

/**新朋友
 * @author :smile
 * @project:NewFriendActivity
 * @date :2016-01-25-18:23
 */
public class PublishActivity extends ParentWithNaviActivity {


    ListView listView;
    EditText et_content;
    ImageButton btn_publish;

    static List<Post> weibos = new ArrayList<Post>();
     MyAdapter adapter;

    @Override
    protected String title() {
        return "新朋友";
    }

    @Override
    public Object right() {
        return R.drawable.base_action_bar_publish_bg_selector;
    }

    @Override
    public ParentWithNaviActivity.ToolBarListener setToolBarListener() {
        return new ParentWithNaviActivity.ToolBarListener() {
            @Override
            public void clickLeft() {
                finish();
            }

            @Override
            public void clickRight() {
                startActivity(SearchUserActivity.class,null);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        initNaviView();
        initUI();
        listView.setAdapter(adapter);
        btn_publish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                publishWeibo(et_content.getText().toString());
            }
        });

        findWeibos();
    }

    private void initUI() {
        adapter = new MyAdapter(this);
        et_content = (EditText) findViewById(R.id.et_content);
        btn_publish = (ImageButton) findViewById(R.id.btn_publish);
        listView = (ListView) findViewById(R.id.listview);
    }


    /**
     * 查询微博
     */
    private void findWeibos(){
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        BmobQuery<Post> query = new BmobQuery<Post>();
        query.addWhereEqualTo("author", user);	// 查询当前用户的所有微博
        query.order("-updatedAt");
        query.include("author");// 希望在查询微博信息的同时也把发布人的信息查询出来，可以使用include方法
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> object, BmobException e) {
                if(e==null){
                    weibos = object;
                    adapter.notifyDataSetChanged();
                    et_content.setText("");
                }else{
                    Log.e("tag", "done: ",e);
                }
            }

        });

    }

    /**
     * 发布微博，发表微博时关联了用户类型，是一对一的体现
     */
    private void publishWeibo(String content){
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if(user == null){
            showToast("发布微博前请先登陆");
            return;
        }else if(TextUtils.isEmpty(content)){
            showToast("发布内容不能为空");
            return;
        }
        // 创建微博信息
        Post weibo = new Post();
        weibo.setContent(content);
        weibo.setAuthor(user);
        weibo.save(new SaveListener<String>() {

            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    showToast("发布成功");
                    findWeibos();
                }else{
                    Log.e("tag", "done: "+(e));
                }
            }
        });
    }


    private static class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        private Context mContext;

        public MyAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        static class ViewHolder {
            TextView tv_content;
            TextView tv_author;
        }

        @Override
        public int getCount() {
            return weibos.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final MyAdapter.ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_weibo, null);

                holder = new MyAdapter.ViewHolder();
                holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
                holder.tv_author = (TextView) convertView.findViewById(R.id.tv_author);

                convertView.setTag(holder);
            } else {
                holder = (MyAdapter.ViewHolder) convertView.getTag();
            }

            // Bind the data efficiently with the holder.
            final Post weibo = weibos.get(position);
            MyUser user = weibo.getAuthor();
            holder.tv_author.setText("发布人："+(user==null?"":user.getUsername()));

            final String str = weibo.getContent();

            holder.tv_content.setText(str);

            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CommentListActivity.class);
                    intent.putExtra("objectId", weibo.getObjectId());
                    mContext.startActivity(intent);
                }
            });

            return convertView;
        }
    }

}
