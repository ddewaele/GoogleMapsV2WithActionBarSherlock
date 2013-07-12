package com.ecs.google.maps.v2.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ecs.google.maps.v2.actionbarsherlock.R;
import com.ecs.google.maps.v2.component.SherlockMapFragment;
import com.ecs.google.maps.v2.util.ViewUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SimpleAnimatingMarkersFragment  extends SherlockMapFragment {
	
	
	// Keep track of our markers
	private List<Marker> markers = new ArrayList<Marker>();
	
	private GoogleMap googleMap;

	private Marker selectedMarker;
	
	Handler handler = new Handler();
	Random random = new Random();
	Runnable runner = new Runnable() {
        @Override
        public void run() {
            setHasOptionsMenu(true);
        }
    };
    
    public static SimpleAnimatingMarkersFragment newInstance(int position,String title) {
    	SimpleAnimatingMarkersFragment fragment = new SimpleAnimatingMarkersFragment();
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
		
		googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng latLng) {
				addMarkerToMap(latLng);
			}

		});
		
		addDefaultLocations();
		
		ViewUtils.initializeMargin(getActivity(),root,16);
		return root;
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
			  startAnimation();
		  } else if (item.getItemId() == R.id.action_bar_stop_animation) {
			  stopAnimation();
		  } else if (item.getItemId() == R.id.action_bar_clear_locations) {
			  clearMarkers();
		  } else if (item.getItemId() == R.id.action_bar_toggle_style) {
			  toggleStyle();
		  }
		  return true;
	}	
	
	private void stopAnimation() {
		// TODO Auto-generated method stub
		
	}

	private void startAnimation() {
		resetMarkers();
		googleMap.animateCamera(
		        CameraUpdateFactory.newLatLngZoom(markers.get(0).getPosition(), 16), 
		        5000,
		        simpleAnimationCancelableCallback);      
		      
		      currentPt = 0-1;	
	}

	private void addDefaultLocations() {
		clearMarkers();
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

	

	int currentPt;
	
	/**
	 * 
	 * Callback that highlights the current marker and keeps animating to the next marker, providing a "next marker" is still available.
	 * If we've reached the end-marker the animation stops.
	 * 
	 */
	CancelableCallback simpleAnimationCancelableCallback =
		new CancelableCallback(){

			@Override
			public void onCancel() {
			}

			@Override
			public void onFinish() {

				if(++currentPt < markers.size()){
					
//					double heading = SphericalUtil.computeHeading(googleMap.getCameraPosition().target, markers.get(currentPt).getPosition());
//					System.out.println("Heading  = " + (float)heading);
//					float targetBearing = bearingBetweenLatLngs(googleMap.getCameraPosition().target, markers.get(currentPt).getPosition());
//					System.out.println("Bearing  = " + targetBearing);
//					
					LatLng targetLatLng = markers.get(currentPt).getPosition();

					CameraPosition cameraPosition =
							new CameraPosition.Builder()
									.target(targetLatLng)
									.tilt(currentPt<markers.size()-1 ? 90 : 0)
				                    //.bearing((float)heading)
				                    .zoom(googleMap.getCameraPosition().zoom)
				                    .build();

					
					googleMap.animateCamera(
							CameraUpdateFactory.newCameraPosition(cameraPosition), 
							3000,
							simpleAnimationCancelableCallback);
					
					highLightMarker(currentPt);

				}
			}
	};
	    
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
		marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		marker.showInfoWindow();
		//Utils.bounceMarker(googleMap, marker);
		this.selectedMarker=marker;
	}	

	/**
	 * Ensure that all markers are using the default red colored icon. (removes any highlighted icons).
	 */
	private void resetMarkers() {
		for (Marker marker : this.markers) {
			marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		}
	}
		
}