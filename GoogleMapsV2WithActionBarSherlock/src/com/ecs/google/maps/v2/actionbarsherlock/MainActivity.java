package com.ecs.google.maps.v2.actionbarsherlock;

import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends SherlockFragmentActivity {
    
	public static int THEME = R.style.Theme_Sherlock;
	private GoogleMap googleMap;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("GoogleMapsDemo");
        setContentView(R.layout.main_activity);
        
        
        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map)).getMap();
        
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng latLng) {
				final Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
						 .title("title")
						 .snippet("snippet"));
				
    			
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
      Toast.makeText(this, "Menu id  \"" + item.getItemId() + "\" clicked.", Toast.LENGTH_SHORT).show();
      return true;
  }
}