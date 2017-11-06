package com.stav.ideastreet.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.stav.ideastreet.R;

public class DiscoveryFragment extends Fragment  implements OnClickListener {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.discovery, null);
		return view;
	}

	@Override
	public void onClick(View v) {

	}
}
