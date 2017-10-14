package com.stav.ideastreet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.stav.ideastreet.Main2Activity;
import com.stav.ideastreet.R;

public class DFragment extends Fragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		View view = View.inflate(getActivity(), R.layout.login, null);


		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:
			startActivity(new Intent(getContext(), Main2Activity.class));
			break;
		case R.id.like:
			break;
		default:
			break;
		}
	}


}