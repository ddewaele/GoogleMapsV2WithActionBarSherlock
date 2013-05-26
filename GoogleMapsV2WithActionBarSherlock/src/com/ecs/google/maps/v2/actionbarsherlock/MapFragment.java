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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapFragment extends SherlockMapFragment {
	
	// Keep track of our markers
	private List<Marker> markers = new ArrayList<Marker>();
	
	private GoogleMap googleMap;

	private final Handler mHandler = new Handler();
	
	
	private final Interpolator interpolator = new LinearInterpolator();

	private Marker trackingMarker;
	
	private Polyline polyLine = null;
	private List<LatLng> polyLinePoints = new ArrayList<LatLng>();
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = super.onCreateView(inflater, container, savedInstanceState);
		googleMap = getMap();
		googleMap.setMyLocationEnabled(true);
		//googleMap.getUiSettings().setMyLocationButtonEnabled(true);
		return root;
	}
	
	public void startAnimation() {
		//navigateToPoint(latLng);
		
		polyLine = initializePolyLine();
		
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
		
		
		// We first need to put the camera in the correct position for the first run (we need 2 markers for this).....
		LatLng markerPos = this.markers.get(0).getPosition();
		LatLng secondPos = this.markers.get(1).getPosition();
		
		float bearing = bearingBetweenLatLngs(markerPos,secondPos);
		
		trackingMarker = googleMap.addMarker(new MarkerOptions().position(markerPos)
				 .title("title")
				 .snippet("snippet"));

		CameraPosition cameraPosition =
				new CameraPosition.Builder()
						.target(markerPos)
						.bearing(bearing)
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
						animator.reset();
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
	
	private PolylineOptions rectOptions = new PolylineOptions();
	
	
	private Polyline initializePolyLine() {
		polyLinePoints = new ArrayList<LatLng>();
		rectOptions.add(this.markers.get(0).getPosition());
		return googleMap.addPolyline(rectOptions);
	}
	
	private void updatePolyLine(LatLng latLng) {
		List<LatLng> points = polyLine.getPoints();
		points.add(latLng);
		polyLine.setPoints(points);
	}
	
	public class Animator implements Runnable {
		
		private static final int ANIMATE_SPEEED = 1500;

		private static final int ANIMATE_SPEEED_TURN = 1000;

		int currentIndex = 0;
		
		//final LatLng endLatLng = new LatLng(50, 4);
		final int duration = ANIMATE_SPEEED;

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
			
			long elapsed = SystemClock.uptimeMillis() - start;
			double t = interpolator.getInterpolation((float)elapsed/duration);
			
			LatLng endLatLng = getEndLatLng();
			LatLng beginLatLng = getBeginLatLng();
			
			double lat = t * endLatLng.latitude + (1-t) * beginLatLng.latitude;
			double lng = t * endLatLng.longitude + (1-t) * beginLatLng.longitude;
			LatLng newPosition = new LatLng(lat, lng);
			
			trackingMarker.setPosition(newPosition);
			updatePolyLine(newPosition);
			
			// It's not possible to move the marker + center it through a cameraposition update while another camerapostioning was already happening.
			//navigateToPoint(newPosition,tilt,bearing,currentZoom,false);
			//navigateToPoint(newPosition,false);

			if (t< 1) {
				mHandler.postDelayed(this, 16);
			} else {
				
				System.out.println("Move to next marker.... current = " + currentIndex + " and size = " + markers.size());
				// imagine 5 elements -  0|1|2|3|4 currentindex must be smaller than 4
				if (currentIndex<markers.size()-2) {
				
					currentIndex++;
					
					start = SystemClock.uptimeMillis();

					LatLng begin = getBeginLatLng();
					LatLng end = getEndLatLng();
					
					float bearingL = bearingBetweenLatLngs(begin, end);
					
					//System.out.println("Calculating baring between " + begin + " and " + end);
					
					markers.get(currentIndex).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
					
					//Create a new CameraPosition
					CameraPosition cameraPosition =
							new CameraPosition.Builder()
									.target(end) // changed this...
				                    .bearing(bearingL)
				                    .tilt(tilt)
				                    .zoom(googleMap.getCameraPosition().zoom)
				                    .build();

					
					googleMap.animateCamera(
							CameraUpdateFactory.newCameraPosition(cameraPosition), 
							ANIMATE_SPEEED_TURN,
							new CancelableCallback() {
								
								@Override
								public void onFinish() {
									System.out.println("camera pointing in the right direction....");
									
//									start = SystemClock.uptimeMillis();
//									mHandler.postDelayed(animator, 16);									
								}
								
								@Override
								public void onCancel() {
									System.out.println("cancelling camera....");									
								}
							}
					);
					
					start = SystemClock.uptimeMillis();
					mHandler.postDelayed(animator, 16);					
					
					// here shift to the next marker, reset the time
					// and do the animations all over again
					// resulting in a brusk movement.
					//currentIndex++;
					
					
					
					
				} else {
					stopAnimation();
				}
				
			}
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
	
//	 private void changeCamera(CameraUpdate update, CancelableCallback callback) {
//	     //googleMap.animateCamera(update, callback);
//	     googleMap.moveCamera(update);
//	 }
//
//	 private void changeCamera(CameraUpdate update) {
//		 changeCamera(update, null);
//	 }

	    
	 /**
	  * 
	  * Allows us to navigate to a certain point.
	  * 
	  * @param latLng
	  * @param tilt
	  * @param bearing
	  * @param zoom
	  */
	 public void navigateToPoint(LatLng latLng,float tilt, float bearing, float zoom,boolean animate) {
		 CameraPosition position =
				 new CameraPosition.Builder().target(latLng)
	                        .zoom(zoom)
	                        .bearing(bearing)
	                        .tilt(tilt)
	                        .build();
	    	
	    	changeCameraPosition(position, animate);
	    	
	 }

	 public void navigateToPoint(LatLng latLng, boolean animate) {
		 CameraPosition position =
				 new CameraPosition.Builder().target(latLng)
//	                        .zoom(zoom)
//	                        .bearing(bearing)
//	                        .tilt(tilt)
	                        .build();
	    	
	    	changeCameraPosition(position, animate);
	 }
	 
	 private void changeCameraPosition(CameraPosition cameraPosition, boolean animate) {
		 CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
		 
	    	if (animate) {
	    		googleMap.animateCamera(cameraUpdate);
	    	} else {
	    	  	googleMap.moveCamera(cameraUpdate);
	    	}

	 }
	 
	/**
	 * 
	 * Adds a marker to the map.
	 * 
	 * @param latLng
	 */
	public void addMarkerToMap(LatLng latLng) {
		Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
				 .title("title")
				 .snippet("snippet"));
		markers.add(marker);
		
	}

	/**
	 * 
	 * Clears all markers from the map.
	 * 
	 */
	public void clearMarkers() {
		googleMap.clear();
    	markers.clear();		
	}
	
	private Location convertLatLngToLocation(LatLng latLng) {
		Location loc = new Location("someLoc");
		loc.setLatitude(latLng.latitude);
		loc.setLongitude(latLng.longitude);
		return loc;
	}
	
	private float bearingBetweenLatLngs(LatLng begin,LatLng end) {
		Location beginL= convertLatLngToLocation(begin);
		Location endL= convertLatLngToLocation(end);
		return beginL.bearingTo(endL);
	}

	public void toggleStyle() {
		if (GoogleMap.MAP_TYPE_NORMAL == googleMap.getMapType()) {
			googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);		
		} else {
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		}
	}	
	
}