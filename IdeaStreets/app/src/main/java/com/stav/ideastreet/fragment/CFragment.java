package com.stav.ideastreet.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.http.Header;

import com.stav.ideastreet.R;
import com.stav.ideastreet.bean.NewsList;
import com.stav.ideastreet.utils.XmlUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CFragment extends Fragment  implements OnClickListener {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.clipboard, null);
		Button clip = (Button) view.findViewById(R.id.clip);
		clip.setOnClickListener(this);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getDataFromServer();
	}

	private void getDataFromServer() {
		boolean b = true;
		FileInputStream openFileInput = null;
		try {
			openFileInput = getActivity().openFileInput("saveobj");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			b = false;
		} finally {
			try {
				if(openFileInput!=null){
					openFileInput.close();
				}
			} catch (IOException e) {
			}
		}
		if (b) {
			readObject();
			return;
		}
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		asyncHttpClient.get("http://10.0.2.2:8080/oschina/list/news/page0.xml",
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] header,
							byte[] bytes, Throwable arg3) {
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] bytes) {
						NewsList newsList = XmlUtils.toBean(NewsList.class,
								bytes);
						System.out.println(newsList.getList().get(0).getBody());

						saveObject(newsList);
					}


				});
	}

	private void readObject() {
		new AsyncTask<String, Void, NewsList>() {

			@Override
			protected NewsList doInBackground(String... params) {
				NewsList newsList = null;
				FileInputStream fis = null;
				ObjectInputStream ois = null;
				try {
					fis = getActivity().openFileInput(params[0]);
					ois = new ObjectInputStream(fis);
					newsList = (NewsList) ois.readObject();
				} catch (FileNotFoundException e) {
				} catch (Exception e) {
					e.printStackTrace();
					// 反序列化失败 - 删除缓存文件
					if (e instanceof InvalidClassException) {
						File data = getActivity().getFileStreamPath(params[0]);
						data.delete();
					}
				} finally {
					try {
						ois.close();
					} catch (Exception e) {
					}
					try {
						fis.close();
					} catch (Exception e) {
					}
				}
				return newsList;
			}

			protected void onPostExecute(NewsList result) {
				System.out.println("获取缓存:" + result.getList().get(0).getBody());
			};
		}.execute("saveobj");
	}

	public void saveObject(NewsList bean) {
		// 参数1：doInBackground里面的方法参数，参数2：onProgressUpdate里面的方法参数,参数3：doInBackground返回的结果，及onPostExecute接受的参数
		new AsyncTask<NewsList, Integer, String>() {
			// 运行在主线程中，更新界面,由publishProgress方法触发
			protected void onProgressUpdate(Integer... values) {
			};

			// 做耗时任务，运行在子线程
			@Override
			protected String doInBackground(NewsList... params) {
				String result = "保存失败";
				FileOutputStream fos = null;
				ObjectOutputStream oos = null;
				try {
					fos = getActivity().openFileOutput("saveobj",
							Context.MODE_PRIVATE);
					oos = new ObjectOutputStream(fos);
					oos.writeObject(params[0]);
					oos.flush();
					result = "保存成功";
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						oos.close();
					} catch (Exception e) {
					}
					try {
						fos.close();
					} catch (Exception e) {
					}
				}
				return result;
			}

			// 耗时操作完成后调用，运行在主线程
			protected void onPostExecute(String result) {
				System.out.println(result);
			};

			// 耗时操作执行前调用，做准备工作，运行在主线程
			protected void onPreExecute() {

			};

		}.execute(bean);
		
	}

	@Override
	public void onClick(View v) {
		ClipboardManager clip = (ClipboardManager) getActivity()
				.getSystemService(Context.CLIPBOARD_SERVICE);
			clip.setText("我是复制品");
	}
}
