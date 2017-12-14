package com.stav.ideastreet.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.stav.ideastreet.R;
import com.stav.ideastreet.adapter.base.BaseViewHolder;
import com.stav.ideastreet.base.ImageLoaderFactory;
import com.stav.ideastreet.bean.MyUser;
import com.stav.ideastreet.ui.UserInfoActivity;

import org.xutils.image.ImageOptions;
import org.xutils.x;

public class SearchUserHolder extends BaseViewHolder {

  @ViewInject(R.id.avatar)
  public ImageView avatar;
  @ViewInject(R.id.name)
  public TextView name;
  @ViewInject(R.id.btn_add)
  public Button btn_add;

  public SearchUserHolder(Context context, ViewGroup root,OnRecyclerViewListener onRecyclerViewListener) {
    super(context, root, R.layout.item_search_user,onRecyclerViewListener);
  }

  @Override
  public void bindData(Object o) {
    final MyUser user =(MyUser)o;

//    ImageLoaderFactory.getLoader().loadAvator(avatar,user.getAvatar(), R.mipmap.head);

    ImageOptions options=new ImageOptions.Builder()
            //设置加载过程中的图片
            .setLoadingDrawableId(R.drawable.ic_launcher)
            //设置加载失败后的图片
            .setFailureDrawableId(R.drawable.ic_launcher)
            //设置使用缓存
            .setUseMemCache(true)
            //设置显示圆形图片
            .setCircular(false)
            //设置支持gif
            .setIgnoreGif(false)
            .build();
    x.image().bind(avatar, user.getAvatar(), options);

    name.setText(user.getUsername());
    btn_add.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {//查看个人详情
          Bundle bundle = new Bundle();
          bundle.putSerializable("u", user);
          startActivity(UserInfoActivity.class,bundle);
        }
    });
  }
}