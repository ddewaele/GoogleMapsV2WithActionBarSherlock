package com.ecs.google.maps.v2.actionbarsherlock;

import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ecs.google.maps.v2.fragment.AnimatingMarkersFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends SherlockFragmentActivity {
    
	public static int THEME = R.style.Theme_Sherlock;
	private GoogleMap googleMap;
	private AnimatingMarkersFragment mapFragment;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("GoogleMapsDemo");
        setContentView(R.layout.main_activity);
        
        mapFragment = (AnimatingMarkersFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        
        googleMap = mapFragment.getMap();
        
        mapFragment.addMarkerToMap(new LatLng(50.961813797827055,3.5168474167585373));
        mapFragment.addMarkerToMap(new LatLng(50.96085423274633,3.517405651509762));
        mapFragment.addMarkerToMap(new LatLng(50.96020550146382,3.5177918896079063));
        mapFragment.addMarkerToMap(new LatLng(50.95936754348453,3.518972061574459));
        mapFragment.addMarkerToMap(new LatLng(50.95877285446026,3.5199161991477013));
        mapFragment.addMarkerToMap(new LatLng(50.958179213755905,3.520646095275879));
        mapFragment.addMarkerToMap(new LatLng(50.95901719316589,3.5222768783569336));
        mapFragment.addMarkerToMap(new LatLng(50.95954430150347,3.523542881011963));
        mapFragment.addMarkerToMap(new LatLng(50.95873336312275,3.5244011878967285));
        mapFragment.addMarkerToMap(new LatLng(50.95955781702322,3.525688648223877));
        mapFragment.addMarkerToMap(new LatLng(50.958855004782116,3.5269761085510254));
        
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng latLng) {
				System.out.println("On Map click");
				mapFragment.addMarkerToMap(latLng);
			}

		});
        
        
        if (googleMap.getMyLocation()!=null) {
        
	        double lat = googleMap.getMyLocation().getLatitude();
	        double lng = googleMap.getMyLocation().getLongitude();
	    
	        mapFragment.navigateToPoint(new LatLng(lat,lng), true);
        }
        
        
        
        
    }
    
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
	  getSupportMenuInflater().inflate(R.menu.main_menu, menu);
      return true;
  }    
  
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
//	  if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
//          return false;
//      }
	  
	  if (item.getItemId() == R.id.action_bar_remove_location) {
		  mapFragment.removeSelectedMarker();
	  } else if (item.getItemId() == R.id.action_bar_start) {
		  mapFragment.startAnimation(true);
	  } else if (item.getItemId() == R.id.action_bar_stop) {
		  mapFragment.stopAnimation();
	  } else if (item.getItemId() == R.id.action_bar_clear_locations) {
	    	mapFragment.clearMarkers();
	  } else if (item.getItemId() == R.id.action_bar_pan_camera) {
	    	panCamera();
	  } else if (item.getItemId() == R.id.action_bar_toggle_style) {
		  mapFragment.toggleStyle();
	  }
	  
      Toast.makeText(this, "Menu id  \"" + item.getItemId() + "\" clicked.", Toast.LENGTH_SHORT).show();
      return true;
  }

  private void panCamera() {
	
	LatLng begin = googleMap.getCameraPosition().target;

	CameraPosition cameraPosition =
			new CameraPosition.Builder()
					.target(begin)
                    .bearing(45)
                    .tilt(45)
                    .zoom(googleMap.getCameraPosition().zoom)
                    .build();

	googleMap.animateCamera(
			CameraUpdateFactory.newCameraPosition(cameraPosition), 
			3000,
			new CancelableCallback() {
				
				@Override
				public void onFinish() {
					System.out.println("finished camera");
				}
				
				@Override
				public void onCancel() {
					System.out.println("cancelling camera");									
				}
			}
	);
		
  }
}