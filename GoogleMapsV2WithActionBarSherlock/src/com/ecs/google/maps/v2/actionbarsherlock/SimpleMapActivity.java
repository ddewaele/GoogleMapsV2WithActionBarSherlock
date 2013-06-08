package com.ecs.google.maps.v2.actionbarsherlock;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;

public class SimpleMapActivity extends Activity{
	
	private com.google.android.gms.maps.MapFragment mapFragment;
	private GoogleMap googleMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_fragment);

		// API level 11 is needed for this call to work.
		//mapFragment = (com.google.android.gms.maps.MapFragment) getFragmentManager().findFragmentById(R.id.map);
        //googleMap = mapFragment.getMap();
        //googleMap.setMyLocationEnabled(true);		
	}

}
