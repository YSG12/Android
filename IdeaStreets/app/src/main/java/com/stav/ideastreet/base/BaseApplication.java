package com.stav.ideastreet.base;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.stav.ideastreet.R;
import com.stav.ideastreet.bean.MyUser;
import com.stav.ideastreet.bean.Post;
import com.stav.ideastreet.utils.ACache;
import com.stav.ideastreet.utils.ActivityManagerUtils;
import com.stav.ideastreet.utils.ConstantValue;
import com.stav.ideastreet.utils.PrefUtils;
import com.stav.ideastreet.utils.StringUtils;


import org.xutils.x;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.jpush.android.api.JPushInterface;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

@SuppressLint("InflateParams")
public class BaseApplication extends Application {
	private static String PREF_NAME = "creativelocker.pref";
	private static String LAST_REFRESH_TIME = "last_refresh_time.pref";
	static Context _context;
	static Resources _resource;
	private static String lastToast = "";

	public Post getCurrentPost() {
		return currentPost;
	}

	public void setCurrentPost(Post currentPost) {
		this.currentPost = currentPost;
	}

	private Post currentPost;
	private static long lastToastTime;
	private static BaseApplication myApplication = null;

	private static boolean sIsAtLeastGB;
	public static String APPID = "73be482259546dce10268f6f48349f09";

	static {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			sIsAtLeastGB = true;
		}
	}


	private static BaseApplication INSTANCE;

	public static BaseApplication getInstance(){
		return myApplication;
	}

	public MyUser getCurrentUser() {
		MyUser user = BmobUser.getCurrentUser(MyUser.class);
		if(user!=null){
			return user;
		}
		return null;
	}

	public static BaseApplication INSTANCE() {
		return INSTANCE;
	}

	private void setInstance(BaseApplication app) {
		setBaseApplication(app);
	}


	private static void setBaseApplication(BaseApplication a) {
		BaseApplication.INSTANCE = a;
	}
	public Activity getTopActivity() {
		return ActivityManagerUtils.getInstance().getTopActivity();
	}


	@Override
	public void onCreate() {

		super.onCreate();
		setInstance(this);
		x.Ext.init(this);// xutils init ...
		//bmob默认初始化
		Bmob.initialize(this, APPID);
		//bmobIM初始化
		if (getApplicationInfo().packageName.equals(getMyProcessName())){
			BmobIM.init(this);
			BmobIM.registerDefaultMessageHandler(new DemoMessageHandler(this));
		}

		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);

		_context = getApplicationContext();
		_resource = _context.getResources();
	}

	private ACache mACache;
	public ACache getCache() {
		if (mACache == null) {
			return ACache.get(getApplicationContext());
		} else {
			return mACache;
		}
	}

	/**
	 * 获取当前运行的进程名
	 * @return
	 */
	public static String getMyProcessName() {
		try {
			File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
			BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
			String processName = mBufferedReader.readLine().trim();
			mBufferedReader.close();
			return processName;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static synchronized BaseApplication context() {
		return (BaseApplication) _context;
	}

	public static Resources resources() {
		return _resource;
	}

	/**
	 * 放入已读文章列表中
	 * 
	 * @param
	 */
	public static void putReadedPostList(String prefFileName, String key,
			String value) {
		SharedPreferences preferences = getPreferences(prefFileName);
		int size = preferences.getAll().size();
		Editor editor = preferences.edit();
		if (size >= 100) {
			editor.clear();
		}
		editor.putString(key, value);
		apply(editor);
	}



	/**
	 * 读取是否是已读的文章列表
	 * 
	 * @param
	 * @return
	 */
	public static boolean isOnReadedPostList(String prefFileName, String key) {
		return getPreferences(prefFileName).contains(key);
	}

	/***
	 * 记录列表上次刷新时间
	 * 
	 * @author 火蚁 2015-2-9 下午2:21:37
	 * 
	 * @return void
	 * @param key
	 * @param value
	 */
	public static void putToLastRefreshTime(String key, String value) {
		SharedPreferences preferences = getPreferences(LAST_REFRESH_TIME);
		Editor editor = preferences.edit();
		editor.putString(key, value);
		apply(editor);
	}

	/***
	 * 获取列表的上次刷新时间
	 * 
	 * @author 火蚁 2015-2-9 下午2:22:04
	 * 
	 * @return String
	 * @param key
	 * @return
	 */
	public static String getLastRefreshTime(String key) {
		return getPreferences(LAST_REFRESH_TIME).getString(key,
				StringUtils.getCurTimeStr());
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static void apply(Editor editor) {
		if (sIsAtLeastGB) {
			editor.apply();
		} else {
			editor.commit();
		}
	}

	public static void set(String key, boolean value) {
		Editor editor = getPreferences().edit();
		editor.putBoolean(key, value);
		apply(editor);
	}

	public static void set(String key, String value) {
		Editor editor = getPreferences().edit();
		editor.putString(key, value);
		apply(editor);
	}

	public static boolean get(String key, boolean defValue) {
		return getPreferences().getBoolean(key, defValue);
	}

	public static String get(String key, String defValue) {
		return getPreferences().getString(key, defValue);
	}

	public static int get(String key, int defValue) {
		return getPreferences().getInt(key, defValue);
	}

	public static long get(String key, long defValue) {
		return getPreferences().getLong(key, defValue);
	}

	public static float get(String key, float defValue) {
		return getPreferences().getFloat(key, defValue);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static SharedPreferences getPreferences() {
		SharedPreferences pre = context().getSharedPreferences(PREF_NAME,
				Context.MODE_MULTI_PROCESS);
		return pre;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static SharedPreferences getPreferences(String prefName) {
		return context().getSharedPreferences(prefName,
				Context.MODE_MULTI_PROCESS);
	}

	public static int[] getDisplaySize() {
		return new int[] { getPreferences().getInt("screen_width", 480),
				getPreferences().getInt("screen_height", 854) };
	}

	public static void saveDisplaySize(Activity activity) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		Editor editor = getPreferences().edit();
		editor.putInt("screen_width", displaymetrics.widthPixels);
		editor.putInt("screen_height", displaymetrics.heightPixels);
		editor.putFloat("density", displaymetrics.density);
		editor.commit();
	}

	public static String string(int id) {
		return _resource.getString(id);
	}

	public static String string(int id, Object... args) {
		return _resource.getString(id, args);
	}

	public static void showToast(int message) {
		showToast(message, Toast.LENGTH_LONG, 0);
	}

	public static void showToast(String message) {
		showToast(message, Toast.LENGTH_LONG, 0, Gravity.BOTTOM);
	}

	public static void showToast(int message, int icon) {
		showToast(message, Toast.LENGTH_LONG, icon);
	}

	public static void showToast(String message, int icon) {
		showToast(message, Toast.LENGTH_LONG, icon, Gravity.BOTTOM);
	}

	public static void showToastShort(int message) {
		showToast(message, Toast.LENGTH_SHORT, 0);
	}

	public static void showToastShort(String message) {
		showToast(message, Toast.LENGTH_SHORT, 0, Gravity.BOTTOM);
	}

	public static void showToastShort(int message, Object... args) {
		showToast(message, Toast.LENGTH_SHORT, 0, Gravity.BOTTOM, args);
	}

	public static void showToast(int message, int duration, int icon) {
		showToast(message, duration, icon, Gravity.BOTTOM);
	}

	public static void showToast(int message, int duration, int icon,
			int gravity) {
		showToast(context().getString(message), duration, icon, gravity);
	}

	public static void showToast(int message, int duration, int icon,
			int gravity, Object... args) {
		showToast(context().getString(message, args), duration, icon, gravity);
	}

	public static void showToast(String message, int duration, int icon,
			int gravity) {
		if (message != null && !message.equalsIgnoreCase("")) {
			long time = System.currentTimeMillis();
			if (!message.equalsIgnoreCase(lastToast)
					|| Math.abs(time - lastToastTime) > 2000) {
				View view = LayoutInflater.from(context()).inflate(
						R.layout.view_toast, null);
				((TextView) view.findViewById(R.id.title_tv)).setText(message);
				if (icon != 0) {
					((ImageView) view.findViewById(R.id.icon_iv))
							.setImageResource(icon);
					((ImageView) view.findViewById(R.id.icon_iv))
							.setVisibility(View.VISIBLE);
				}
				Toast toast = new Toast(context());
				toast.setView(view);
				if (gravity == Gravity.CENTER) {
					toast.setGravity(gravity, 0, 0);
				} else {
					toast.setGravity(gravity, 0, 35);
				}

				toast.setDuration(duration);
				toast.show();
				lastToast = message;
				lastToastTime = System.currentTimeMillis();
			}
		}
	}
}
