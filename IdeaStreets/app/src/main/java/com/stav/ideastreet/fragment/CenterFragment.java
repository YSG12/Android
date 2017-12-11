package com.stav.ideastreet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stav.ideastreet.EnshrineActivity;
import com.stav.ideastreet.R;
import com.stav.ideastreet.bean.MyUser;
import com.stav.ideastreet.ui.ContactActivity;
import com.stav.ideastreet.ui.SearchUserActivity;
import com.stav.ideastreet.ui.UpdateInfoActivity;
import com.stav.ideastreet.ui.LoginActivity;
import com.stav.ideastreet.ui.RegisterActivity;
import com.stav.ideastreet.ui.dialog.MyImageDialog;
import com.stav.ideastreet.utils.ConstantValue;
import com.stav.ideastreet.utils.PrefUtils;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.stav.ideastreet.R.id.ll_unlogin;
import static com.stav.ideastreet.base.BaseApplication.showToast;

public class CenterFragment extends Fragment {

	private Button btLogin,btRegister,btLogout;
	private ImageButton ib_qrcode;
	private ImageView ivIcon;
	private TextView tvMotto,tvUser;
	private View view;
	private LinearLayout llUnlogin,llCenter;
	private android.support.v4.app.FragmentManager mFm;
    private RelativeLayout rlList,rlInfomation,rlEnshrine,rlSettings;
	private CompositeSubscription mCompositeSubscription;
	private String mObjectId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		view = View.inflate(getContext(),R.layout.personal_center,null);
		llUnlogin = (LinearLayout) view.findViewById(ll_unlogin);
		llCenter = (LinearLayout) view.findViewById(R.id.ll_personal_center);
		//如果是第一次进入，显示为需要登录注册页面

		boolean isLogin = PrefUtils.getBoolean(getContext(), ConstantValue.IS_LOGIN, true);
		if (isLogin == false) {
			unLogin();
			//登录前页面
		} else if (isLogin == true){
			//登录后页面
			personalCenter();
		}
		return view;
	}

	private void personalCenter() {

		llCenter.setVisibility(View.VISIBLE);
		llUnlogin.setVisibility(View.GONE);
		mObjectId = PrefUtils.getString(getActivity(), ConstantValue.OBJECT_ID, "");


		ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
		tvUser = (TextView) view.findViewById(R.id.tv_user);
		tvMotto = (TextView) view.findViewById(R.id.tv_motto);



		ib_qrcode=(ImageButton) view.findViewById(R.id.ib_qrcode);
        btLogout = (Button) view.findViewById(R.id.bt_logout);
		rlList = (RelativeLayout) view.findViewById(R.id.rl_list);
        rlInfomation = (RelativeLayout) view.findViewById(R.id.rl_infomation);
        rlEnshrine = (RelativeLayout) view.findViewById(R.id.rl_enshrine);
        rlSettings = (RelativeLayout) view.findViewById(R.id.rl_settings);

		updateUser();
        //显示放大二维码
        ib_qrcode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v == ib_qrcode) {
					ib_qrcode.setDrawingCacheEnabled(true);
					MyImageDialog myImageDialog = new MyImageDialog(getActivity(), 0, 0, -300, ib_qrcode.getDrawingCache());
					myImageDialog.show();
				}
			}
		});

        //登出按钮
        btLogout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
				PrefUtils.setBoolean(getContext(), ConstantValue.IS_LOGIN, false);
				unLogin();
            }
        });

        //点击按钮修改信息
        rlInfomation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
				startActivity(new Intent(getActivity(), UpdateInfoActivity.class));
            }
        });

        //点击按钮添加好友
		rlList.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
				startActivity(new Intent(getContext(), ContactActivity.class));
            }
        });

		//点击按钮添加好友
		rlEnshrine.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
				startActivity(new Intent(getContext(), EnshrineActivity.class));
            }
        });

	}

	//初始化数据
	private void updateUser() {
		BmobQuery<MyUser> userInfo = new BmobQuery<>();
		addSubscription(userInfo.getObject(mObjectId, new QueryListener<MyUser>() {
					@Override
					public void done(final MyUser myUser, BmobException e) {
						if (e == null) {
							new Thread() {
								public void run() {
									//这儿是耗时操作，完成之后更新UI；
									getActivity().runOnUiThread(new Runnable(){

										@Override
										public void run() {
											//更新UI
											tvMotto.setText(myUser.getMotto());
											tvUser.setText(myUser.getUsername());
											ivIcon.setBackgroundResource(R.mipmap.head);
										}

									});
								}
							}.start();

						} else {
							Log.d("",e+"");
						}
					}
				})
		);
	}

	private void unLogin() {

		llUnlogin.setVisibility(View.VISIBLE);
		llCenter.setVisibility(View.GONE);
		btLogin=(Button) view.findViewById(R.id.bt_login);
		btRegister=(Button) view.findViewById(R.id.bt_register);
        //点击按钮，登录
		btLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getContext(), LoginActivity.class));
			}
		});
        //点击按钮，注册
		btRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getContext(), RegisterActivity.class));
			}
		});
	}

    /**
     * 清除本地用户
     */
    private void logOut() {
        BmobUser.logOut();
    }

	/**
	 * 解决Subscription内存泄露问题
	 * @param s
	 */
	public void addSubscription(Subscription s) {
		if (this.mCompositeSubscription == null) {
			this.mCompositeSubscription = new CompositeSubscription();
		}
		this.mCompositeSubscription.add(s);
	}

}