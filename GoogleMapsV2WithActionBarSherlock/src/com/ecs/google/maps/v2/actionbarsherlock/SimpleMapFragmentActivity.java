package com.ecs.google.maps.v2.actionbarsherlock;

import com.google.android.gms.maps.GoogleMap;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class SimpleMapFragmentActivity extends FragmentActivity{

	private MapFragment mapFragment;
	private GoogleMap googleMap;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.support_map_fragment);
// TODO: this doesn't work. Expplain why.
		//		mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
//        googleMap = mapFragment.getMap();
//        googleMap.setMyLocationEnabled(true);
        
	}
}
