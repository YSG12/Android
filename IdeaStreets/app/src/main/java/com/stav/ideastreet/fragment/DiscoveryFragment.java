package com.stav.ideastreet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lidroid.xutils.ViewUtils;
import com.stav.ideastreet.R;
import com.stav.ideastreet.ui.MyIdeaActivity;
import com.stav.ideastreet.ui.SearchUserActivity;

public class DiscoveryFragment extends Fragment implements OnClickListener {
	LinearLayout ll_find_friend,rl_active;
    private View view;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.discovery, null);
		initUI();
		return view;
	}

	private void initUI() {
		ll_find_friend = (LinearLayout) view.findViewById(R.id.ll_find_friend);
		rl_active = (LinearLayout) view.findViewById(R.id.rl_active);
		ll_find_friend.setOnClickListener(this);
		rl_active.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.ll_find_friend:
				startActivity(new Intent(getActivity(), SearchUserActivity.class));
				break;
			case R.id.rl_active:
				startActivity(new Intent(getActivity(), MyIdeaActivity.class));
				break;
            default:
                break;
		}
	}
}
