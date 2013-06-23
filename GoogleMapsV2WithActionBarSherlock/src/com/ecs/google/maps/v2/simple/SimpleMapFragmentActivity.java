package com.ecs.google.maps.v2.simple;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

import com.ecs.google.maps.v2.actionbarsherlock.R;
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
		setContentView(R.layout.support_map_fragment);
		
		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        googleMap = mapFragment.getMap();
        googleMap.setMyLocationEnabled(true);
	}
	
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
		  getMenuInflater().inflate(R.menu.main_menu, menu);
	      return true;
	  } 
}
