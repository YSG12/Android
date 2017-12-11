package com.stav.ideastreet.test;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.stav.ideastreet.R;
import com.stav.ideastreet.bean.Comment;
import com.stav.ideastreet.bean.MyUser;
import com.stav.ideastreet.bean.Post;
import com.stav.ideastreet.bean.PostOther;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author stav
 * @date 2017/11/21 11:16
 */
public class TestActivity extends AppCompatActivity {

    private Post mWeibo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mWeibo = new Post();
        mWeibo.setObjectId(getIntent().getStringExtra("objectId"));


    }

    public void test(View view) {

        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if(user == null){
            Toast.makeText(this, "发表评论前请先登陆", Toast.LENGTH_SHORT).show();
            return;
        }else if(TextUtils.isEmpty("123")){
            Toast.makeText(this, "发表评论不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        final PostOther comment = new PostOther();
//        comment.setPost(mWeibo);
//        comment.setUser(user);
        comment.setEnshrine(false);
        comment.save(new SaveListener<String>() {

            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Toast.makeText(getApplicationContext(), "评论成功", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d("",e+"");
                }
            }
        });
    }

}
