package com.ecs.google.maps.v2.actionbarsherlock;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends SherlockFragmentActivity {
    
	public static int THEME = R.style.Theme_Sherlock;
	private GoogleMap googleMap;
	private MapFragment mapFragment;
	
	
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("GoogleMapsDemo");
        setContentView(R.layout.main_activity);
        
        mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        
        googleMap = mapFragment.getMap();
        
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng latLng) {
				mapFragment.addMarkerToMap(latLng);
			}

		});
        
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
	  
	  if (item.getItemId() == R.id.action_bar_add_manual_location) {
		  
	  } else if (item.getItemId() == R.id.action_bar_start) {
		  mapFragment.startAnimation();
	  } else if (item.getItemId() == R.id.action_bar_stop) {
		  mapFragment.stopAnimation();
	  } else if (item.getItemId() == R.id.action_bar_clear_locations) {
	    	mapFragment.clearMarkers();
	  } else if (item.getItemId() == R.id.action_bar_pan_camera) {
	    	panCamera();
	  }
	  
      Toast.makeText(this, "Menu id  \"" + item.getItemId() + "\" clicked.", Toast.LENGTH_SHORT).show();
      return true;
  }

private void panCamera() {
	LatLng begin = googleMap.getCameraPosition().target;
	//Create a new CameraPosition
	CameraPosition cameraPosition =
			new CameraPosition.Builder()
					.target(begin)
                    .bearing(0)
                    .zoom(googleMap.getCameraPosition().zoom)
                    .build();

	System.out.println("Animating camera....");
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
			});
		
}
}