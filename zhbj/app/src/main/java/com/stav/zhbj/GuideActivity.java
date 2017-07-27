package com.stav.zhbj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.stav.zhbj.utils.DensityUtils;
import com.stav.zhbj.utils.PrefUtils;
import com.umeng.analytics.MobclickAgent;

import org.xutils.common.util.DensityUtil;

import java.util.ArrayList;

/**
 * 新手引导页面
 */
public class GuideActivity extends Activity {

    private ViewPager vp_guide;
    //引导页面图片数组
    private int[] mImageIds = new int[] {
            R.mipmap.guide_1,R.mipmap.guide_2,R.mipmap.guide_3
    };
    private ArrayList<ImageView> mImageViewList;    //ImageView 集合
    private GuideAdapter mGuideAdapter;
    private LinearLayout ll_container;
    private ImageView iv_red_point;
    private int mPointDis;  //小红点移动距离
    private Button btn_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        //初始化UI
        initUI();
        //初始化数据
        initData();
        mGuideAdapter = new GuideAdapter();
        vp_guide.setAdapter(mGuideAdapter);

        vp_guide.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //当某个页面滑动过程中的回调

                //更新小红点移动的距离
                int leftMargin = (int) (mPointDis * positionOffset) + position*mPointDis;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_red_point.getLayoutParams();
                params.leftMargin = leftMargin; //修改左边距
                //重新设置布局参数
                iv_red_point.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                //当某个页面被选中
                //最后一个页面显示开始体验按钮
                if (position == mImageViewList.size()-1) {
                    btn_start.setVisibility(View.VISIBLE);
                } else {
                    btn_start.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //当某个页面状态发生变化

            }
        });

        //监听layout方法结束的监听事件，结束之后位置确定之后在获取距离
        //视图树
        iv_red_point.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //移除监听，避免重复获取
                iv_red_point.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //layout方法执行结束后回调
                //计算两个圆点的距离（第二个圆点left-第一个圆点left）
                //measure->layout-draw 在onCreate执行完之后执行
                mPointDis = ll_container.getChildAt(1).getLeft() - ll_container.getChildAt(0).getLeft();
            }
        });

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //更新sp，已经不是第一次进入引导页面了
                PrefUtils.setBoolean(getApplicationContext(),"isFirstEnter",false);
                //跳转到主界面
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
    }

    private void initUI() {
        btn_start = (Button) findViewById(R.id.btn_start);
        iv_red_point = (ImageView) findViewById(R.id.iv_red_point);
        vp_guide = (ViewPager) findViewById(R.id.vp_guide);
        ll_container = (LinearLayout) findViewById(R.id.ll_container);

    }

    public void initData() {
        mImageViewList = new ArrayList<>();
        for (int i=0;i<mImageIds.length;i++){
            ImageView view = new ImageView(this);
            view.setBackgroundResource(mImageIds[i]);   //通过设置背景填充满父窗体
            //view.setImageResource(ResId) 不一定能填充父窗体
            mImageViewList.add(view);


            // 初始化小圆点
            ImageView point = new ImageView(this);
            point.setImageResource(R.drawable.shape_point_gray);// 设置图片(shape形状)

            // 初始化布局参数, 宽高包裹内容,父控件是谁,就是谁声明的布局参数
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            if (i > 0) {
                // 从第二个点开始设置左边距
                params.leftMargin = DensityUtils.dis2px(10,this);
            }
            ll_container.addView(point,params);
        }

    }

    class GuideAdapter extends PagerAdapter{

        //item个数
        @Override
        public int getCount() {
            return mImageViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {

            return view == object;
        }

        //初始化item布局
        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView view = mImageViewList.get(position);
            container.addView(view);
            return view;
        }

        //销毁item布局
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
