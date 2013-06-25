package com.ecs.google.maps.v2.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ecs.google.maps.v2.actionbarsherlock.R;
import com.ecs.google.maps.v2.component.SherlockMapFragment;
import com.ecs.google.maps.v2.tooltip.IconizedWindowAdapter;
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

/**
 * 
 * Re-usable component.
 * 
 * @author ddewaele
 *
 */
public class AnimatingMarkersFragment extends SherlockMapFragment {
	
	
	// Keep track of our markers
	private List<Marker> markers = new ArrayList<Marker>();

	private GoogleMap googleMap;
	private final Handler mHandler = new Handler();
	
	private Marker selectedMarker;

	Handler handler = new Handler();
	Random random = new Random();
	Runnable runner = new Runnable() {
        @Override
        public void run() {
            setHasOptionsMenu(true);
        }
    };

    public static AnimatingMarkersFragment newInstance(int position,String title) {
    	AnimatingMarkersFragment fragment = new AnimatingMarkersFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putString("title", title);
        fragment.setArguments(bundle);
        return fragment;
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		handler.postDelayed(runner, random.nextInt(2000));
		
		View root = super.onCreateView(inflater, container, savedInstanceState);
		googleMap = getMap();
		googleMap.setMyLocationEnabled(true);
		//googleMap.getUiSettings().setMyLocationButtonEnabled(true);
		
		googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng latLng) {
				addMarkerToMap(latLng);
			}

		});
		
		googleMap.setInfoWindowAdapter(new IconizedWindowAdapter(getActivity().getLayoutInflater()));
		
		initializeMargin(root);
		   		

		return root;
	}

	private void initializeMargin(View root) {
		final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

		root.setPadding(margin, margin, margin, margin);
		
		 FrameLayout frameLayout = new FrameLayout(getActivity());
		    frameLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		    ((ViewGroup) root).addView(frameLayout,
		        new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.animating_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		  if (item.getItemId() == R.id.action_bar_remove_location) {
			  removeSelectedMarker();
		  } else if (item.getItemId() == R.id.action_bar_add_default_locations) {
			  addDefaultLocations();
		  } else if (item.getItemId() == R.id.action_bar_start_animation) {
			  animator.startAnimation(true);
		  } else if (item.getItemId() == R.id.action_bar_stop_animation) {
			  animator.stopAnimation();
		  } else if (item.getItemId() == R.id.action_bar_clear_locations) {
			  clearMarkers();
		  } else if (item.getItemId() == R.id.action_bar_toggle_style) {
			  toggleStyle();
		  }
		  return true;
	}	
	
	private void addDefaultLocations() {
        addMarkerToMap(new LatLng(50.961813797827055,3.5168474167585373));
        addMarkerToMap(new LatLng(50.96085423274633,3.517405651509762));
        addMarkerToMap(new LatLng(50.96020550146382,3.5177918896079063));
        addMarkerToMap(new LatLng(50.95936754348453,3.518972061574459));
        addMarkerToMap(new LatLng(50.95877285446026,3.5199161991477013));
        addMarkerToMap(new LatLng(50.958179213755905,3.520646095275879));
        addMarkerToMap(new LatLng(50.95901719316589,3.5222768783569336));
        addMarkerToMap(new LatLng(50.95954430150347,3.523542881011963));
        addMarkerToMap(new LatLng(50.95873336312275,3.5244011878967285));
        addMarkerToMap(new LatLng(50.95955781702322,3.525688648223877));
        addMarkerToMap(new LatLng(50.958855004782116,3.5269761085510254));
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
	
	public class Animator implements Runnable {
		
		private static final int ANIMATE_SPEEED = 1500;
		private static final int ANIMATE_SPEEED_TURN = 1000;
		private static final int BEARING_OFFSET = 20;

		private final Interpolator interpolator = new LinearInterpolator();
		
		int currentIndex = 0;
		
		float tilt = 90;
		float zoom = 15.5f;
		boolean upward=true;
		
		long start = SystemClock.uptimeMillis();
		
		LatLng endLatLng = null; 
		LatLng beginLatLng = null;
		
		boolean showPolyline = false;
		
		private Marker trackingMarker;
		
		public void reset() {
			resetMarkers();
			start = SystemClock.uptimeMillis();
			currentIndex = 0;
			endLatLng = getEndLatLng();
			beginLatLng = getBeginLatLng();
			
		}
		
		public void stop() {
			trackingMarker.remove();
			mHandler.removeCallbacks(animator);
			
		}

		public void initialize(boolean showPolyLine) {
			reset();
			this.showPolyline = showPolyLine;
			
			highLightMarker(0);
			
			if (showPolyLine) {
				polyLine = initializePolyLine();
			}
			
			// We first need to put the camera in the correct position for the first run (we need 2 markers for this).....
			LatLng markerPos = markers.get(0).getPosition();
			LatLng secondPos = markers.get(1).getPosition();
			
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
		
		private Polyline polyLine;
		private PolylineOptions rectOptions = new PolylineOptions();

		
		private Polyline initializePolyLine() {
			//polyLinePoints = new ArrayList<LatLng>();
			rectOptions.add(markers.get(0).getPosition());
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
		

		public void stopAnimation() {
			animator.stop();
		}
		
		public void startAnimation(boolean showPolyLine) {
			if (markers.size()>2) {
				animator.initialize(showPolyLine);
			}
		}		


		@Override
		public void run() {
			
			long elapsed = SystemClock.uptimeMillis() - start;
			double t = interpolator.getInterpolation((float)elapsed/ANIMATE_SPEEED);
			
//			LatLng endLatLng = getEndLatLng();
//			LatLng beginLatLng = getBeginLatLng();
			
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
					
					endLatLng = getEndLatLng();
					beginLatLng = getBeginLatLng();

					
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

		//Utils.bounceMarker(googleMap, marker);
		
		this.selectedMarker=marker;
	}	

	private void resetMarkers() {
		for (Marker marker : this.markers) {
			marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		}
	}
		
}