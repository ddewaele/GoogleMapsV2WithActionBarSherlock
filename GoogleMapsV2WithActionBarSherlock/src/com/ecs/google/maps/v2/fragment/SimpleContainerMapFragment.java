package com.ecs.google.maps.v2.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecs.google.maps.v2.actionbarsherlock.R;
import com.ecs.google.maps.v2.util.ViewUtils;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * 
 * Simple custom SupportMapFragment that adds a margin.
 * 
 * @author ddewaele
 *
 */
public class SimpleContainerMapFragment extends SupportMapFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.support_map_fragment, container,false);
		ViewUtils.initializeMargin(getActivity(), view);
		return view;
		
	}
	
}
