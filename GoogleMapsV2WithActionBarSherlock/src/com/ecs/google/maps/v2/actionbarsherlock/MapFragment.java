package com.ecs.google.maps.v2.actionbarsherlock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;

public class MapFragment extends SherlockMapFragment {
	private GoogleMap mMap;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = super.onCreateView(inflater, container, savedInstanceState);
		mMap = getMap();
		return root;
	}
}