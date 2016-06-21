package com.runrunfast.homegym.home.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.runrunfast.homegym.R;

public class MeFragment extends Fragment {
	
	private View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.activity_btdevice, container, false);
		return rootView;
	}
}
