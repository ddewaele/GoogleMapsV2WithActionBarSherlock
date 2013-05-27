package com.ecs.google.maps.v2.actionbarsherlock;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class SimpleAnimation extends SherlockFragmentActivity {

	private SupportMapFragment findFragmentById;
	private GoogleMap googleMap;

	private List<Marker> markers = new ArrayList<Marker>();
	private Marker selectedMarker;

	private Polyline polyLine;
	private PolylineOptions rectOptions = new PolylineOptions();

	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.support_map_fragment);
		findFragmentById = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		googleMap = findFragmentById.getMap();
	
		  googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
				
				@Override
				public void onMapClick(LatLng latLng) {
					System.out.println("On Map click");
					addMarkerToMap(latLng);
				}

			});
		  
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

	
	 @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
		  getSupportMenuInflater().inflate(R.menu.main_menu, menu);
	      return true;
	  }    
	  
	  
	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
//		  if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
//	          return false;
//	      }
		  
		  if (item.getItemId() == R.id.action_bar_remove_location) {
			  //mapFragment.removeSelectedMarker();
		  } else if (item.getItemId() == R.id.action_bar_start) {
			  startAnimation();
		  } else if (item.getItemId() == R.id.action_bar_stop) {
			  //mapFragment.stopAnimation();
		  } else if (item.getItemId() == R.id.action_bar_clear_locations) {
		    	//mapFragment.clearMarkers();
		  } else if (item.getItemId() == R.id.action_bar_pan_camera) {
		    	//panCamera();
		  } else if (item.getItemId() == R.id.action_bar_toggle_style) {
			  //mapFragment.toggleStyle();
		  }
		  
	      Toast.makeText(this, "Menu id  \"" + item.getItemId() + "\" clicked.", Toast.LENGTH_SHORT).show();
	      return true;
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

	public void startAnimation() {
		googleMap.animateCamera(
		CameraUpdateFactory.zoomTo(googleMap.getCameraPosition().zoom + 0.5f), 
		3000,
		MyCancelableCallback);						

		currentPt = 0-1;

	}
	
	public void stopAnimation() {
		
	}
	
	int currentPt;
	
	CancelableCallback MyCancelableCallback = new CancelableCallback(){

				@Override
				public void onCancel() {
				}

				@Override
				public void onFinish() {
					
					if(++currentPt < markers.size()){
						
						float targetBearing = bearingBetweenLatLngs( googleMap.getCameraPosition().target, markers.get(currentPt).getPosition());
						LatLng targetLatLng = markers.get(currentPt).getPosition();

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
						
						markers.get(currentPt).showInfoWindow();
						
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
	
}
