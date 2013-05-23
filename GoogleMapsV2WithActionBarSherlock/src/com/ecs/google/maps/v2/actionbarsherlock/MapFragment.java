package com.ecs.google.maps.v2.actionbarsherlock;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends SherlockMapFragment {
	private GoogleMap googleMap;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = super.onCreateView(inflater, container, savedInstanceState);
		googleMap = getMap();
		
		final LatLng latLng = new LatLng(0, 0);
		
		final Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
																 .title("title")
																 .snippet("snippet"));

		final LatLng endLatLng = new LatLng(50, 4);
		
		final int duration = 60000;
		
		//navigateToPoint(latLng);
		
		Handler handler = new Handler();
		final Handler mHandler = new Handler();
		final long start = SystemClock.uptimeMillis();
		final Interpolator interpolator = new LinearInterpolator();
		Runnable animator = new Runnable() {
			
			float tilt = 0;
			float zoom = 5.5f;
			boolean upward=true;
			
			@Override
			public void run() {
				//System.out.println("tilt = " + tilt);
				//System.out.println("upward = " + upward);
				//System.out.println("zoom = " + zoom);
				if (upward) {
					
					if (tilt<90) {
						tilt ++;
						zoom-=0.05f;
					} else {
						upward=false;
					}
					
				} else {
					if (tilt>0) {
						tilt --;
						zoom+=0.05f;
					} else {
						upward=true;
					}
				}
				
				long elapsed = SystemClock.uptimeMillis() - start;
				double t = interpolator.getInterpolation((float)elapsed/duration);
				double lat = t*endLatLng.latitude + (1-t) * latLng.latitude;
				double lng = t*endLatLng.longitude + (1-t) * latLng.longitude;
				LatLng latLng2 = new LatLng(lat, lng);
				marker.setPosition(latLng2);
				
				//var heading = google.maps.geometry.spherical.computeHeading(point1,point2);
				navigateToPoint(latLng2,tilt,tilt,zoom);
				if (t< 1) {
					mHandler.postDelayed(this, 16);
				}
			}
		};
		
		//handler.post(animator);
		
		return root;
	}
	
	 private void changeCamera(CameraUpdate update, CancelableCallback callback) {
	     //googleMap.animateCamera(update, callback);
	     googleMap.moveCamera(update);
	 }

	 private void changeCamera(CameraUpdate update) {
		 changeCamera(update, null);
	 }

	    
	 private void navigateToPoint(LatLng latLng,float tilt, float bearing, float zoom) {
		 CameraPosition position =
				 new CameraPosition.Builder().target(latLng)
	                        .zoom(zoom)
	                        .bearing(bearing)
	                        .tilt(tilt)
	                        .build();
	    	
	    	changeCamera(CameraUpdateFactory.newCameraPosition(position)); 
	 }	
}