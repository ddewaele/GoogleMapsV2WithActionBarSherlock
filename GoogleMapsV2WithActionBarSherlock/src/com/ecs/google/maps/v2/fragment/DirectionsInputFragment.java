package com.ecs.google.maps.v2.fragment;

import com.ecs.google.maps.v2.actionbarsherlock.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DirectionsInputFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.directions_input, container,false);
		//return super.onCreateView(inflater, container, savedInstanceState);
	}
}
