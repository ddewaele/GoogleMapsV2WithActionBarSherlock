package com.ecs.google.maps.v2.actionbarsherlock;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;
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
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends SherlockMapFragment {
	
	// Keep track of our markers
	private List<Marker> markers = new ArrayList<Marker>();
	
	private GoogleMap googleMap;

	private final Handler mHandler = new Handler();
	
	
	private final Interpolator interpolator = new LinearInterpolator();

	private Marker trackingMarker;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = super.onCreateView(inflater, container, savedInstanceState);
		googleMap = getMap();
		googleMap.setMyLocationEnabled(true);
		googleMap.getUiSettings().setMyLocationButtonEnabled(true);
		//addMarkerToMap(new LatLng(0, 0));
		return root;
	}
	
	public void startAnimation() {
		//navigateToPoint(latLng);
		
		System.out.println("Markers (" + this.markers.size() + ")");
		System.out.println("-------");
		for (Marker marker : this.markers) {
			System.out.println(marker.getPosition());
		}
		
		
//		googleMap.animateCamera(
//				CameraUpdateFactory.zoomTo(googleMap.getCameraPosition().zoom + 0.5f), 
//				3000,
//				MyCancelableCallback);						
//		
//		currentPt = 0-1;
		
		animator.reset();
		
		LatLng markerPos = this.markers.get(0).getPosition();
		trackingMarker = googleMap.addMarker(new MarkerOptions().position(markerPos)
				 .title("title")
				 .snippet("snippet"));

		CameraPosition cameraPosition =
				new CameraPosition.Builder()
						.target(markerPos)
	                    .tilt(90)
	                    .zoom(googleMap.getCameraPosition().zoom)
	                    .build();


		
		googleMap.animateCamera(
				CameraUpdateFactory.newCameraPosition(cameraPosition), 
				2000,
				new CancelableCallback() {
					
					@Override
					public void onFinish() {
						System.out.println("finished camera");
						Handler handler = new Handler();
						handler.post(animator);	
					}
					
					@Override
					public void onCancel() {
						System.out.println("cancelling camera");									
					}
				}
		);
		
		
		
		
	}
	
	public void stopAnimation() {
		animator.reset();
		mHandler.removeCallbacks(animator);
	}
	

	private Animator animator = new Animator();
	
	
	int currentPt;
	
	CancelableCallback MyCancelableCallback =
			new CancelableCallback(){

				@Override
				public void onCancel() {
					//info.setText("onCancel()");
				}

				@Override
				public void onFinish() {

					
					if(++currentPt < markers.size()){
						
						//Get the current location
						Location startingLocation = new Location("starting point");
						startingLocation.setLatitude(googleMap.getCameraPosition().target.latitude);
						startingLocation.setLongitude(googleMap.getCameraPosition().target.longitude);
						
						//Get the target location
						Location endingLocation = new Location("ending point");
						endingLocation.setLatitude(markers.get(currentPt).getPosition().latitude);
						endingLocation.setLongitude(markers.get(currentPt).getPosition().longitude);
						
						//Find the Bearing from current location to next location
						float targetBearing = startingLocation.bearingTo(endingLocation);
						
						LatLng targetLatLng = markers.get(currentPt).getPosition();
						//float targetZoom = zoomBar.getProgress();
						
						
						System.out.println("currentPt  = " + currentPt  );
						System.out.println("size  = " + markers.size());
						//Create a new CameraPosition
						CameraPosition cameraPosition =
								new CameraPosition.Builder()
										.target(targetLatLng)
										.tilt(currentPt<markers.size()-1 ? 90 : 0)
					                    .bearing(targetBearing)
					                    .zoom(googleMap.getCameraPosition().zoom)
					                    .build();
	
						
						googleMap.animateCamera(
								CameraUpdateFactory.newCameraPosition(cameraPosition), 
								3000,
								MyCancelableCallback);
						System.out.println("Animate to: " + markers.get(currentPt).getPosition() + "\n" +
								"Bearing: " + targetBearing);
						
						markers.get(currentPt).showInfoWindow();
						
					}else{
						//info.setText("onFinish()");
					}
					
				}
		
	};
	
	public class Animator implements Runnable {
		
		int currentIndex = 0;
		
		//final LatLng endLatLng = new LatLng(50, 4);
		final int duration = 5000;

		float tilt = 90;
		float zoom = 15.5f;
		boolean upward=true;
		
		long start = SystemClock.uptimeMillis();
		
		public void reset() {
			start = SystemClock.uptimeMillis();
			currentIndex = 0;
			
		}
		
		@Override
		public void run() {
			
			//System.out.println("run with " + currentIndex);
			
			//adjustCameraPosition();
			
			long elapsed = SystemClock.uptimeMillis() - start;
			double t = interpolator.getInterpolation((float)elapsed/duration);
			
//			LatLng endLatLng = new LatLng(50, 4); // getEndLatLng();
//			LatLng beginLatLng = new LatLng(0, 0); // getBeginLatLng();
			
			LatLng endLatLng = getEndLatLng();
			LatLng beginLatLng = getBeginLatLng();
			
			System.out.println("Calculating " + currentIndex + " with t=" + t + " between " + beginLatLng + " and " + endLatLng);
			
			Location beginLocation = convertLatLngToLocation(beginLatLng);
			Location endLocation = convertLatLngToLocation(endLatLng);
			
			float bearing = beginLocation.bearingTo(endLocation);
			
			//System.out.println("end latlng " + endLatLng);
			//System.out.println("begin latlng " + beginLatLng);
			
			double lat = t * endLatLng.latitude + (1-t) * beginLatLng.latitude;
			double lng = t * endLatLng.longitude + (1-t) * beginLatLng.longitude;
			LatLng newPosition = new LatLng(lat, lng);
			//System.out.println("t= " + t + " - newposition = " + newPosition);
			
			trackingMarker.setPosition(newPosition);
			
			float bearingC = googleMap.getCameraPosition().bearing;
			//System.out.println("Camera bearing : " + bearingC);
			
			navigateToPoint(newPosition,tilt,bearing,zoom);
			if (t< 1) {
				//System.out.println("Continue....");
				mHandler.postDelayed(this, 16);
			} else {
				
				System.out.println("Move to next marker.... current = " + currentIndex + " and size = " + markers.size());
				// imagine 5 elements -  0|1|2|3|4 currentindex must be smaller than 4
				if (currentIndex<markers.size()-2) {
				
					currentIndex++;
					
					start = SystemClock.uptimeMillis();
					
					LatLng begin = getBeginLatLng(); // googleMap.getCameraPosition().target;
					LatLng end = getEndLatLng();
					
					Location beginL= convertLatLngToLocation(begin);
					Location endL= convertLatLngToLocation(end);
					
					System.out.println("Calculating baring between " + begin + " and " + end);
					float bearingL = beginL.bearingTo(endL);
					
					//Create a new CameraPosition
					CameraPosition cameraPosition =
							new CameraPosition.Builder()
									.target(begin)
				                    .bearing(bearingL)
				                    .tilt(tilt)
				                    .zoom(googleMap.getCameraPosition().zoom)
				                    .build();

					
					System.out.println("New bearing : " + bearingL);
					System.out.println("Animating camera....");
					googleMap.animateCamera(
							CameraUpdateFactory.newCameraPosition(cameraPosition), 
							2000,
							new CancelableCallback() {
								
								@Override
								public void onFinish() {
									System.out.println("finished camera");
									start = SystemClock.uptimeMillis();
									mHandler.postDelayed(animator, 16);									
								}
								
								@Override
								public void onCancel() {
									System.out.println("cancelling camera");									
								}
							}
					);
					
					// here shift to the next marker, reset the time
					// and do the animations all over again
					// resulting in a brusk movement.
					//currentIndex++;
					
					
					
					
				} else {
					stopAnimation();
				}
				
			}
		}
		
		private Location convertLatLngToLocation(LatLng latLng) {
			Location loc = new Location("someLoc");
			loc.setLatitude(latLng.latitude);
			loc.setLongitude(latLng.longitude);
			return loc;
		}
		
		private LatLng getEndLatLng() {
			return markers.get(currentIndex+1).getPosition();
		}
		
		private LatLng getBeginLatLng() {
			return markers.get(currentIndex).getPosition();
		}
		
		private void adjustCameraPosition() {
			//System.out.println("tilt = " + tilt);
			//System.out.println("upward = " + upward);
			//System.out.println("zoom = " + zoom);
			if (upward) {
				
				if (tilt<90) {
					tilt ++;
					zoom-=0.01f;
				} else {
					upward=false;
				}
				
			} else {
				if (tilt>0) {
					tilt --;
					zoom+=0.01f;
				} else {
					upward=true;
				}
			}			
		}
	};	
	
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

	public void addMarkerToMap(LatLng latLng) {
		Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
				 .title("title")
				 .snippet("snippet"));
		markers.add(marker);
		
	}

	public void clearMarkers() {
		googleMap.clear();
    	markers.clear();		
	}	
}