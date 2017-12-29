package com.stav.ideastreet.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.stav.ideastreet.R;
import com.stav.ideastreet.bean.MyUser;
import com.stav.ideastreet.bean.Post;
import com.stav.ideastreet.db.DatabaseUtil;
import com.stav.ideastreet.fragment.MainFragment;
import com.stav.ideastreet.sns.TencentShare;
import com.stav.ideastreet.sns.TencentShareConstants;
import com.stav.ideastreet.sns.TencentShareEntity;
import com.stav.ideastreet.ui.CommentListActivity;
import com.stav.ideastreet.ui.UserInfoActivity;
import com.stav.ideastreet.util.StringUtils;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

import static com.stav.ideastreet.base.BaseApplication.showToast;

public class MAdapter extends BaseAdapter {


    private LayoutInflater mInflater;

    static List<Post> weibos = new ArrayList<>();
    private Context mContext;

        public MAdapter(Context context, List<Post> list) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
            weibos = list;

        }

        class ViewHolder {
            TextView tv_content;
            TextView tv_author;
            TextView tv_selector;
            TextView tv_createAt;
            TextView tv_comment_num;
            TextView tv_like_num;
            ImageButton ib_enshrine;
            ImageButton ib_author;
            ImageButton ib_commment;
            ImageButton ib_like;
            ImageButton ib_share;
            ImageView iv_img;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext,R.layout.list_item_weibo, null);

            holder = new ViewHolder();
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_author = (TextView) convertView.findViewById(R.id.tv_author);
            holder.tv_selector = (TextView) convertView.findViewById(R.id.tv_selector);
            holder.tv_createAt = (TextView) convertView.findViewById(R.id.tv_createAt);
            holder.tv_comment_num = (TextView) convertView.findViewById(R.id.tv_comment_num);
            holder.tv_like_num = (TextView) convertView.findViewById(R.id.tv_like_num);
            holder.ib_enshrine = (ImageButton) convertView.findViewById(R.id.ib_enshrine);
            holder.ib_author = (ImageButton) convertView.findViewById(R.id.ib_author);
            holder.ib_commment = (ImageButton) convertView.findViewById(R.id.ib_commment);
            holder.ib_like = (ImageButton) convertView.findViewById(R.id.ib_like);
            holder.iv_img = (ImageView) convertView.findViewById(R.id.iv_img);
            holder.ib_share = (ImageButton) convertView.findViewById(R.id.ib_share);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Bind the data efficiently with the holder.
        final Post weibo = weibos.get(position);
        final MyUser user = weibo.getAuthor();
        holder.tv_author.setText(user == null ? "" : user.getUsername()); //发布人
        holder.tv_createAt.setText(weibo.getCreatedAt());   //创意发布时间
        holder.tv_selector.setText(weibo.getSelector());    //创意分类
        holder.tv_content.setTextColor(0xff666666);
        holder.tv_author.setTextColor(0xff666666);
        holder.tv_selector.setTextColor(Color.RED);
        holder.tv_createAt.setTextColor(0xff666666);
        holder.tv_comment_num.setTextColor(0xff666666);
        holder.tv_like_num.setTextColor(0xff666666);
        ImageOptions options =new ImageOptions.Builder()
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

        x.image().bind(holder.ib_author, user.getAvatar(), options);


        if (weibo.getUpdownImg() != null) {
            holder.iv_img.setVisibility(View.VISIBLE);

            ImageOptions option_avatar =new ImageOptions.Builder()
//                        //设置加载过程中的图片
//                        .setLoadingDrawableId(R.mipmap.default_head)
//                        //设置加载失败后的图片
//                        .setFailureDrawableId(R.mipmap.default_head)
                    //设置使用缓存
                    .setUseMemCache(true)
                    //设置显示圆形图片
                    .setCircular(false)
                    //设置支持gif
                    .setIgnoreGif(false)
                    .build();

            x.image().bind(holder.iv_img, weibo.getUpdownImg(), option_avatar);
        }

        holder.ib_share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showToast("分享给好友看哦~");
            }
        });

        //当点赞数为0时不显示
        if (holder.tv_like_num.getText() == 0 + "") {
            holder.tv_like_num.setVisibility(View.INVISIBLE);
        }
        holder.tv_like_num.setText(weibo.getLove() + "");  //点赞数

        if (DatabaseUtil.getInstance(mContext).isLoved(weibo)) {
            holder.tv_like_num.setTextColor(Color.parseColor("#D95555"));
            holder.ib_like.setBackgroundResource(R.drawable.ic_likeed);
        } else {
            holder.tv_like_num.setTextColor(Color.parseColor("#000000"));
            holder.ib_like.setBackgroundResource(R.drawable.ic_unlike);
        }
        //点击点赞按钮
        holder.ib_like.setOnClickListener(new View.OnClickListener() {
            boolean oldFav = weibo.isMyFav();

            @Override
            public void onClick(View v) {
                if (user == null) {
                    showToast("请先登录。");
                    return;
                }
                if (weibo.isMyLove()) {
                    showToast("您已赞过啦");
                    return;
                }

                if (DatabaseUtil.getInstance(mContext).isLoved(weibo)) {
                    showToast("您已赞过啦");
                    weibo.setMyLove(true);
//                        weibo.setLove(weibo.getLove() + 1);
//                        holder.tv_like_num.setTextColor(Color.parseColor("#D95555"));
//                        holder.tv_like_num.setText(weibo.getLove() + "");
                    return;
                }

                weibo.setLove(weibo.getLove() + 1);
                holder.tv_like_num.setTextColor(Color.parseColor("#D95555"));
                holder.ib_like.setBackgroundResource(R.drawable.ic_likeed);
                holder.tv_like_num.setText(weibo.getLove() + "");

                weibo.increment("love", 1);
                if (weibo.isMyFav()) {
                    weibo.setMyFav(false);
                }
                weibo.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {

                        if (e == null) {
                            weibo.setMyLove(true);
                            weibo.setMyFav(oldFav);
                            DatabaseUtil.getInstance(mContext).insertFav(weibo);
                            Log.i("stav", "点赞成功~");
                        } else {
                            weibo.setMyLove(true);
                            weibo.setMyFav(oldFav);
                        }
                    }
                });
            }
        });
// && DatabaseUtil.getInstance(getActivity()).isLoved(weibo)
        if (weibo.isMyFav()) {
            holder.ib_enshrine.setBackgroundResource(R.mipmap.base_action_bar_enshrine_bg_p);
        } else {
            holder.ib_enshrine.setBackgroundResource(R.mipmap.base_action_bar_enshrine_bg_n);
        }
        //点击收藏按钮
        holder.ib_enshrine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFav(v, weibo, holder);
            }
        });


        final String str = weibo.getContent();
        // 特殊文字处理,将表情等转换一下
        holder.tv_content.setText(StringUtils.getEmotionContent(mContext, holder.tv_content, str)); //发布创意内容


        holder.tv_comment_num.setText(weibo.getComment()+"");
        //点击进入创意评论页
        holder.ib_commment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentListActivity.class);
                intent.putExtra("objectId", weibo.getObjectId());
                mContext.startActivity(intent);
            }
        });

        //点击进入添加好友页
        holder.ib_author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                MyUser user = weibo.getAuthor();
                bundle.putSerializable("u", user);
                startActivity(UserInfoActivity.class, bundle, false);
            }
        });


        return convertView;
    }

        private void onClickFav(View v, final Post weibo, ViewHolder holder) {
            // TODO Auto-generated method stub
            MyUser user = BmobUser.getCurrentUser(MyUser.class);
            if (user != null && user.getSessionToken() != null) {
                BmobRelation favRelaton = new BmobRelation();

                weibo.setMyFav(!weibo.isMyFav());
                if (weibo.isMyFav()) {
                    holder.ib_enshrine.setBackgroundResource(R.mipmap.base_action_bar_enshrine_bg_p);

//                    ((ImageView) v).setImageResource(R.mipmap.base_action_bar_enshrine_bg_p);
                    favRelaton.add(weibo);
                    user.setFavorite(favRelaton);
                    user.update(new UpdateListener() {

                        @Override
                        public void done(BmobException e) {
                            if (e == null){
                                DatabaseUtil.getInstance(mContext).insertFav(weibo);
                                Log.i("stav", "收藏成功。");
                                weibo.setMyFav(true);
                                weibo.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {

                                    }
                                });
                            } else {
                                // TODO Auto-generated method stub
                                Log.i("stav", "收藏失败。请检查网络~");
                                showToast("收藏失败。请检查网络~" + e);
                            }
                        }
                    });

                } else {
                    holder.ib_enshrine.setBackgroundResource(R.mipmap.base_action_bar_enshrine_bg_n);

//                    ((ImageView) v).setImageResource(R.mipmap.base_action_bar_enshrine_bg_n);
                    favRelaton.remove(weibo);
                    user.setFavorite(favRelaton);
                    user.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                DatabaseUtil.getInstance(mContext).deleteFav(weibo);
                                Log.i("stav", "取消收藏。");
                                weibo.setMyFav(false);
                                weibo.update(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {

                                    }
                                });
                            } else {
                                showToast("取消收藏失败。请检查网络~" + e);
                            }
                        }
                    });
                }

            } else {
                // 前往登录注册界面
                showToast("收藏前请先登录。");
            }
        }

    public void startActivity(Class<? extends Activity> target, Bundle bundle, boolean finish) {
        Intent intent = new Intent();
        intent.setClass(mContext, target);
        if (bundle != null)
            intent.putExtra(mContext.getPackageName(), bundle);
        mContext.startActivity(intent);
    }

}