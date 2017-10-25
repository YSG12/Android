package com.stav.ideastreet.ui.dialog;

import com.stav.ideastreet.R;
import com.stav.ideastreet.test.Main2Activity;
import com.stav.ideastreet.ui.NewsDetailActivity;
import com.stav.ideastreet.utils.UIHelper;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class QuickOptionDialog extends Dialog implements
        View.OnClickListener {

    private ImageView mClose;

    public interface OnQuickOptionformClick {
        void onQuickOptionClick(int id);
    }

    private OnQuickOptionformClick mListener;
	private LinearLayout ly_quick_option_text, ly_quick_option_album, ly_quick_option_photo;

    @SuppressLint("InflateParams")
    private QuickOptionDialog(Context context, int defStyle) {
        super(context, defStyle);
        View contentView = getLayoutInflater().inflate(
                R.layout.dialog_quick_option, null);
        mClose = (ImageView) contentView.findViewById(R.id.iv_close);
        ly_quick_option_text = (LinearLayout) contentView.findViewById(R.id.ly_quick_option_text);
        ly_quick_option_album = (LinearLayout) contentView.findViewById(R.id.ly_quick_option_album);
        ly_quick_option_photo = (LinearLayout) contentView.findViewById(R.id.ly_quick_option_photo);
        ly_quick_option_text.setOnClickListener(this);
        ly_quick_option_album.setOnClickListener(this);
        ly_quick_option_photo.setOnClickListener(this);

        Animation operatingAnim = AnimationUtils.loadAnimation(getContext(),
                R.anim.quick_option_close);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        mClose.startAnimation(operatingAnim);

        mClose.setOnClickListener(this);
        // 去掉标题，必须在setContentView之前调用
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                QuickOptionDialog.this.dismiss();
                return true;
            }
        });

        setContentView(contentView);
        // 设置Dialog的宽度等于屏幕的宽度，必须在setContentView后调用
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(p);

    }

    public QuickOptionDialog(Context context) {
        this(context, R.style.quick_option_dialog);
        // 让Dialog显示在底部
        getWindow().setGravity(Gravity.BOTTOM);

    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

    }

    public void setOnQuickOptionformClickListener(OnQuickOptionformClick lis) {
        mListener = lis;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
        case R.id.iv_close:
            dismiss();
            break;
        case R.id.ly_quick_option_text:
        	getContext().startActivity(new Intent(getContext(),NewsDetailActivity.class));
        	dismiss();
            break;
        case R.id.ly_quick_option_album:
            getContext().startActivity(new Intent(getContext(),Main2Activity.class));
            dismiss();
            break;
        case R.id.ly_quick_option_photo:
            getContext().startActivity(new Intent(getContext(),Main2Activity.class));
            dismiss();
            break;
        default:
            break;
        }
    }

}
