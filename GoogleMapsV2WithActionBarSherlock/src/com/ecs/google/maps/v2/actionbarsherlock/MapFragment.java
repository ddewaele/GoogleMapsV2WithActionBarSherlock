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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapFragment extends SupportMapFragment /*SherlockMapFragment */{
	
	private static final int ANIMATE_SPEEED = 1500;
	private static final int ANIMATE_SPEEED_TURN = 1000;
	private static final int BEARING_OFFSET = 20;
	
	// Keep track of our markers
	private List<Marker> markers = new ArrayList<Marker>();
	
	private GoogleMap googleMap;

	private final Handler mHandler = new Handler();
	
	private final Interpolator interpolator = new LinearInterpolator();

	private Marker trackingMarker;
	private Marker selectedMarker;
	
	private boolean showPolyline = false;
	private Polyline polyLine;
	private PolylineOptions rectOptions = new PolylineOptions();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = super.onCreateView(inflater, container, savedInstanceState);
		googleMap = getMap();
		googleMap.setMyLocationEnabled(true);
		//googleMap.getUiSettings().setMyLocationButtonEnabled(true);
		
		googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				System.out.println("Clicked on map... ");
				marker.hideInfoWindow();
				selectedMarker = marker;
				return false;
			}
		});
		
		return root;
	}
	
	public void startAnimation(boolean showPolyLine) {
		animator.reset();
		
		this.showPolyline = showPolyLine;
		
		highLightMarker(0);
		
		if (this.showPolyline) {
			polyLine = initializePolyLine();
		}
		
//		System.out.println("Markers (" + this.markers.size() + ")");
//		System.out.println("-------");
//		for (Marker marker : this.markers) {
//			System.out.println(marker.getPosition());
//		}
//		
		
//		googleMap.animateCamera(
//				CameraUpdateFactory.zoomTo(googleMap.getCameraPosition().zoom + 0.5f), 
//				3000,
//				MyCancelableCallback);						
//		
//		currentPt = 0-1;
		
		// We first need to put the camera in the correct position for the first run (we need 2 markers for this).....
		LatLng markerPos = this.markers.get(0).getPosition();
		LatLng secondPos = this.markers.get(1).getPosition();
		
		setupCameraPositionForMovement(markerPos, secondPos);
	}

	private void setupCameraPositionForMovement(LatLng markerPos,
			LatLng secondPos) {
		
		float bearing = bearingBetweenLatLngs(markerPos,secondPos);
		
		trackingMarker = googleMap.addMarker(new MarkerOptions().position(markerPos)
				 .title("title")
				 .snippet("snippet"));

		CameraPosition cameraPosition =
				new CameraPosition.Builder()
						.target(markerPos)
						.bearing(bearing + BEARING_OFFSET)
	                    .tilt(90)
	                    .zoom(googleMap.getCameraPosition().zoom >=16 ? googleMap.getCameraPosition().zoom : 16)
	                    .build();
		
		googleMap.animateCamera(
				CameraUpdateFactory.newCameraPosition(cameraPosition), 
				ANIMATE_SPEEED_TURN,
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
		trackingMarker.remove();
		//animator.reset();
		mHandler.removeCallbacks(animator);
	}
	

	private Animator animator = new Animator();
	
	
	int currentPt;
	
	CancelableCallback MyCancelableCallback =
			new CancelableCallback(){

				@Override
				public void onCancel() {
					System.out.println("onCancelled called");
				}

				@Override
				public void onFinish() {

					
					if(++currentPt < markers.size()){
						
//						//Get the current location
//						Location startingLocation = new Location("starting point");
//						startingLocation.setLatitude(googleMap.getCameraPosition().target.latitude);
//						startingLocation.setLongitude(googleMap.getCameraPosition().target.longitude);
//						
//						//Get the target location
//						Location endingLocation = new Location("ending point");
//						endingLocation.setLatitude(markers.get(currentPt).getPosition().latitude);
//						endingLocation.setLongitude(markers.get(currentPt).getPosition().longitude);
//						
//						//Find the Bearing from current location to next location
//						float targetBearing = startingLocation.bearingTo(endingLocation);
					
						float targetBearing = bearingBetweenLatLngs( googleMap.getCameraPosition().target, markers.get(currentPt).getPosition());
						
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
	
	
	private Polyline initializePolyLine() {
		//polyLinePoints = new ArrayList<LatLng>();
		rectOptions.add(this.markers.get(0).getPosition());
		return googleMap.addPolyline(rectOptions);
	}
	
	/**
	 * Add the marker to the polyline.
	 */
	private void updatePolyLine(LatLng latLng) {
		List<LatLng> points = polyLine.getPoints();
		points.add(latLng);
		polyLine.setPoints(points);
	}
	
	public class Animator implements Runnable {
		
		int currentIndex = 0;
		
		float tilt = 90;
		float zoom = 15.5f;
		boolean upward=true;
		
		long start = SystemClock.uptimeMillis();
		
		public void reset() {
			resetMarkers();
			start = SystemClock.uptimeMillis();
			currentIndex = 0;
		}
		
		@Override
		public void run() {
			
			long elapsed = SystemClock.uptimeMillis() - start;
			double t = interpolator.getInterpolation((float)elapsed/ANIMATE_SPEEED);
			
			LatLng endLatLng = getEndLatLng();
			LatLng beginLatLng = getBeginLatLng();
			
			double lat = t * endLatLng.latitude + (1-t) * beginLatLng.latitude;
			double lng = t * endLatLng.longitude + (1-t) * beginLatLng.longitude;
			LatLng newPosition = new LatLng(lat, lng);
			
			trackingMarker.setPosition(newPosition);
			
			if (showPolyline) {
				updatePolyLine(newPosition);
			}
			
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
					
					highLightMarker(currentIndex);
					
					CameraPosition cameraPosition =
							new CameraPosition.Builder()
									.target(end) // changed this...
				                    .bearing(bearingL  + BEARING_OFFSET)
				                    .tilt(tilt)
				                    .zoom(googleMap.getCameraPosition().zoom)
				                    .build();

					
					googleMap.animateCamera(
							CameraUpdateFactory.newCameraPosition(cameraPosition), 
							ANIMATE_SPEEED_TURN,
							null
					);
					
					start = SystemClock.uptimeMillis();
					mHandler.postDelayed(animator, 16);					
					
				} else {
					currentIndex++;
					highLightMarker(currentIndex);
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
	    
	 /**
	  * Allows us to navigate to a certain point.
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
		 CameraPosition position = new CameraPosition.Builder().target(latLng).build();
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

	
	/**
	 * Adds a marker to the map.
	 */
	public void addMarkerToMap(LatLng latLng) {
		Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
				 .title("title")
				 .snippet("snippet"));
		markers.add(marker);
		
	}

	/**
	 * Clears all markers from the map.
	 */
	public void clearMarkers() {
		googleMap.clear();
    	markers.clear();		
	}

	/**
	 * Remove the currently selected marker.
	 */
	public void removeSelectedMarker() {
		this.markers.remove(this.selectedMarker);
		this.selectedMarker.remove();
	}	
	
	/**
	 * Highlight the marker by index.
	 */
	private void highLightMarker(int index) {
		highLightMarker(markers.get(index));
	}

	/**
	 * Highlight the marker by marker.
	 */
	private void highLightMarker(Marker marker) {
		
		/*
		for (Marker foundMarker : this.markers) {
			if (!foundMarker.equals(marker)) {
				foundMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			} else {
				foundMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
				foundMarker.showInfoWindow();
			}
		}
		*/
		marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		marker.showInfoWindow();

		this.selectedMarker=marker;
	}	

	private void resetMarkers() {
		for (Marker marker : this.markers) {
			marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		}
	}
		
}