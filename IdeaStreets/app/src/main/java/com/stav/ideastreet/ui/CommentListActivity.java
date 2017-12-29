package com.stav.ideastreet.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.stav.ideastreet.R;
import com.stav.ideastreet.base.BaseActivity;
import com.stav.ideastreet.base.BaseApplication;
import com.stav.ideastreet.bean.Comment;
import com.stav.ideastreet.bean.MyUser;
import com.stav.ideastreet.bean.Post;
import com.stav.ideastreet.test.TestActivity;
import com.stav.ideastreet.util.StringUtils;
import com.stav.ideastreet.utils.ActivityUtil;
import com.stav.ideastreet.widget.drop.DropCover;
import com.stav.ideastreet.widget.drop.WaterDrop;


import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobCallback;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 评论界面
 * @author stav
 * @date 2017/11/1 11:11
 */
public class CommentListActivity extends BaseActivity {

    ListView listView;
    EditText et_content;
    Button btn_publish;
    private final static String COMMENT_ID = "comment_id_";
    boolean isFav = false;
    private ListView commentList;
    private TextView footer;
    private EditText commentContent;
    private Button commentCommit;
    private TextView userName;
    private TextView commentItemContent;
    private ImageView commentItemImage;
    private ImageView userLogo;
    private ImageView myFav;
    private TextView hate;
    private WaterDrop mWaterDrop;
    private String commentEdit = "";
    private int pageNum;

    static List<Comment> comments = new ArrayList<Comment>();
    MyAdapter adapter;
    Post weibo = new Post();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initUI();
        weibo.setObjectId(getIntent().getStringExtra("objectId"));
        initData();
        adapter = new MyAdapter(this);
        et_content = (EditText) findViewById(R.id.comment_content);
        btn_publish = (Button) findViewById(R.id.comment_commit);
        commentList = (ListView) findViewById(R.id.comment_list);
        commentList.setAdapter(adapter);
        setListViewHeightBasedOnChildren(commentList);
        commentList.setCacheColorHint(0);
        commentList.setScrollingCacheEnabled(false);
        commentList.setScrollContainer(false);
        commentList.setFastScrollEnabled(true);
        commentList.setSmoothScrollbarEnabled(true);

        btn_publish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                publishComment(et_content.getText().toString());
                adapter.notifyDataSetChanged();
            }
        });
        findComments();
    }

    /**
     * 动态设置listview的高度
     * item 总布局必须是linearLayout
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1))
                + 15;
        listView.setLayoutParams(params);
    }

    private void initData() {
        BmobQuery<Post> post = new BmobQuery<Post>();
        post.addWhereEqualTo("objectId",weibo.getObjectId());
        post.findObjects(new FindListener<Post>() {
            @Override
            public void done(final List<Post> list, BmobException e) {
                userName.setText(list.get(0).getAuthorName());
                commentItemContent.setText(StringUtils.getEmotionContent(getApplicationContext(), commentItemContent, list.get(0).getContent()));
                if (list.get(0).getUpdownImg().isEmpty()) {
                    commentItemImage.setVisibility(View.GONE);
                } else {
                    commentItemImage.setVisibility(View.VISIBLE);
                    ImageOptions options=new ImageOptions.Builder()
                            //设置加载过程中的图片
                            .setLoadingDrawableId(R.mipmap.default_head)
                            //设置加载失败后的图片
                            .setFailureDrawableId(R.mipmap.default_head)
                            //设置使用缓存
                            .setUseMemCache(true)
                            //设置显示圆形图片
                            .setCircular(false)
                            //设置支持gif
                            .setIgnoreGif(false)
                            .build();

                    x.image().bind(commentItemImage, list.get(0).getUpdownImg(), options);

                    commentItemImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                            ArrayList<String> photos = new ArrayList<String>();
                            photos.add(list.get(0).getUpdownImg());
                            intent.putStringArrayListExtra("photos", photos);
                            intent.putExtra("position", 0);
                            startActivity(intent);
                        }
                    });
                }
                MyUser user = BmobUser.getCurrentUser(MyUser.class);
                ImageOptions options=new ImageOptions.Builder()
                        //设置加载过程中的图片
                        .setLoadingDrawableId(R.mipmap.default_head)
                        //设置加载失败后的图片
                        .setFailureDrawableId(R.mipmap.default_head)
                        //设置使用缓存
                        .setUseMemCache(true)
                        //设置显示圆形图片
                        .setCircular(false)
                        //设置支持gif
                        .setIgnoreGif(false)
                        .build();

                x.image().bind(userLogo, user.getAvatar(), options);
            }
        });
    }

    private void initUI() {
        commentContent = (EditText) findViewById(R.id.comment_content);
        commentCommit = (Button) findViewById(R.id.comment_commit);

        userName = (TextView) findViewById(R.id.user_name);
        commentItemContent = (TextView) findViewById(R.id.content_text);
        commentItemImage = (ImageView) findViewById(R.id.content_image);

        userLogo = (ImageView) findViewById(R.id.user_logo);
        myFav = (ImageView) findViewById(R.id.item_action_fav);
        mWaterDrop = (WaterDrop) findViewById(R.id.item_drop);
    }

    private void findComments(){
        BmobQuery<Comment> query = new BmobQuery<Comment>();
        query.addWhereEqualTo("post",new BmobPointer(weibo));
        query.include("user,post.author");
        query.findObjects(new FindListener<Comment>() {

            @Override
            public void done(final List<Comment> object, BmobException e) {
                if(e==null){
                    comments = object;
                    weibo.setComment(object.size());
                    weibo.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            Log.d("", "评论个数为"+object.size());
                        }
                    });
                    adapter.notifyDataSetChanged();
                    et_content.setText("");
                }else{
                    Log.d("",e+"");
                }
            }

        });

    }

    private void publishComment(String content){
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if(user == null){
            toast("发表评论前请先登陆");
            return;
        }else if(TextUtils.isEmpty(content)){
            toast("发表评论不能为空");
            return;
        }

        final Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(weibo);
        comment.setUser(user);
        comment.save(new SaveListener<String>() {

            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    findComments();
                    et_content.setText("");
                    adapter.notifyDataSetChanged();
                    toast("评论成功");
                }else{
                    Log.d("",e+"");
                }
            }
        });
    }

    private static class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        static class ViewHolder {
            public TextView userName;
            public TextView commentContent;
            public TextView index;
        }

        @Override
        public int getCount() {
            return comments.size();
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
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.comment_item, null);
                viewHolder.userName = (TextView) convertView.findViewById(R.id.userName_comment);
                viewHolder.commentContent = (TextView) convertView.findViewById(R.id.content_comment);
                viewHolder.index = (TextView) convertView.findViewById(R.id.index_comment);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final Comment comment = comments.get(position);
            if (comment.getUser() != null) {
                viewHolder.userName.setText(comment.getUser().getUsername());
                Log.i("CommentListActivity", "NAME:" + comment.getUser().getUsername());
            } else {
                viewHolder.userName.setText("墙友");
            }
            viewHolder.index.setText((position + 1) + "楼");
            viewHolder.commentContent.setText(comment.getContent());
            return convertView;
        }
    }

}
