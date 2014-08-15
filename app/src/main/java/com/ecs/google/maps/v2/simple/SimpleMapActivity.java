package com.ecs.google.maps.v2.simple;

import android.app.Activity;
import android.os.Bundle;

import com.ecs.google.maps.v2.actionbarsherlock.R;
import com.ecs.google.maps.v2.actionbarsherlock.R.layout;
import com.google.android.gms.maps.GoogleMap;

/**
 * 
 * Here's one way of loading up a mapFragment on API level 11 or higher.
 * It uses 
 * 
 *  - an Activity 
 *  - a MapFragment component (com.google.android.gms.maps.MapFragment)
 *  - getFragmentManager
 *  
 * Because of its dependency with getFragmentManager and the use of the MapFragment (extending android.app.Fragment)
 * this code cannot run on lower API devices.
 * 
 * @author ddewaele
 *
 */
public class SimpleMapActivity extends Activity{
	
	private com.google.android.gms.maps.MapFragment mapFragment;
	private GoogleMap googleMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.support_map_fragment);

		// API level 11 is needed for this call to work.
		//mapFragment = (com.google.android.gms.maps.MapFragment) getFragmentManager().findFragmentById(R.id.map);
        //googleMap = mapFragment.getMap();
        //googleMap.setMyLocationEnabled(true);		
	}

}
