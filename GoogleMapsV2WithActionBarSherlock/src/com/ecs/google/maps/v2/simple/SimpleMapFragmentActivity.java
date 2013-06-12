package com.ecs.google.maps.v2.simple;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.ecs.google.maps.v2.actionbarsherlock.R;
import com.ecs.google.maps.v2.actionbarsherlock.R.id;
import com.ecs.google.maps.v2.actionbarsherlock.R.layout;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * 
 * Here's an alternative way of loading up a MapFragment, this time using the support library.
 * As you can see it uses 
 * 
 *  - FragmentActivity instead of Activity
 *  - com.google.android.gms.maps.SupportMapFragment instead of com.google.android.gms.maps.MapFragment
 *  - getSupportFragmentManager instead of getFragmentManager
 *  
 * That way this code can run on lower API level devices.
 * 
 * @author ddewaele
 *
 */
public class SimpleMapFragmentActivity extends FragmentActivity{

	private SupportMapFragment mapFragment;
	private GoogleMap googleMap;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.support_map_fragment_container);
		
		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_container);
        googleMap = mapFragment.getMap();
        googleMap.setMyLocationEnabled(true);
	}
}
