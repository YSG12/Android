package com.stav.zhbj.base.menu;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.stav.zhbj.R;
import com.stav.zhbj.base.BaseMenuDetailPager;
import com.stav.zhbj.domain.PhotoBean;
import com.stav.zhbj.global.GlobalContents;
import com.stav.zhbj.utils.CacheUtils;
import com.stav.zhbj.utils.MyBitmapUtils;

import java.util.ArrayList;


/**
 * Created by Administrator on 2017/7/22.
 */

/**
 * 菜单详情页-组图
 */
public class PhotosMenuDetailPager extends BaseMenuDetailPager implements View.OnClickListener {

    private ArrayList<PhotoBean.PhotoNews> mNewsList;

    public PhotosMenuDetailPager(Activity activity, ImageButton btnphoto) {
        super(activity);
        btnphoto.setOnClickListener(this);  //组图切换按钮切换
        this.btnPhoto = btnphoto;
    }

    @ViewInject(R.id.lv_photo)
    private ListView lv_photo;
    @ViewInject(R.id.gv_photo)
    private GridView gv_photo;
    private ImageButton btnPhoto;


    @Override
    public View initView() {

        View view = View.inflate(mActivity, R.layout.pager_photos_menu_detail, null);
        ViewUtils.inject(this,view);
        return view;
    }

    @Override
    public void initData() {
        String cache = CacheUtils.getCache(GlobalContents.PHOTO_RUL, mActivity);
        if (!TextUtils.isEmpty(cache)) {
            processData(cache);
        }

        getDataFromServer();
    }

    private void getDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpMethod.GET, GlobalContents.PHOTO_RUL,new RequestCallBack<String>(){

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processData(result);

                CacheUtils.setCache(GlobalContents.PHOTO_RUL,result,mActivity);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                //请求失败
                e.printStackTrace();
                Toast.makeText(mActivity, s, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void processData(String result) {
        Gson gson = new Gson();
        PhotoBean photoBean = gson.fromJson(result, PhotoBean.class);
        mNewsList = photoBean.data.news;
        lv_photo.setAdapter(new PhotoAdapter());
        gv_photo.setAdapter(new PhotoAdapter());    //gridview的布局和listView完全一致，所有可以共用一个adpater
    }

    private boolean isListView =  true; //标记当前是否为listView展示
    @Override
    public void onClick(View v) {
        if (isListView) {
            //切换成gridView
            lv_photo.setVisibility(View.GONE);
            gv_photo.setVisibility(View.VISIBLE);
            btnPhoto.setImageResource(R.mipmap.icon_pic_list_type);
            isListView = false;
        } else{
            //切换成listView
            lv_photo.setVisibility(View.VISIBLE);
            gv_photo.setVisibility(View.GONE);
            btnPhoto.setImageResource(R.mipmap.icon_pic_grid_type);
            isListView = true;
        }

    }

    class PhotoAdapter extends BaseAdapter {

        //xutils
//        private final BitmapUtils mBitmapUtils;

        private final MyBitmapUtils mBitmapUtils;

        public PhotoAdapter() {
//            mBitmapUtils = new BitmapUtils(mActivity);
//            mBitmapUtils.configDefaultLoadingImage(R.mipmap.pic_item_list_default);
            mBitmapUtils = new MyBitmapUtils();
        }

        @Override
        public int getCount() {
            return mNewsList.size();
        }

        @Override
        public PhotoBean.PhotoNews getItem(int position) {
            return mNewsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.list_item_photos, null);
                holder = new ViewHolder();
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            PhotoBean.PhotoNews item = getItem(position);
            holder.tv_title.setText(item.title);
            mBitmapUtils.display(holder.iv_pic,item.listimage);
            return convertView;
        }
    }
    public static class ViewHolder {
        public ImageView iv_pic;
        public TextView tv_title;
    }
}
