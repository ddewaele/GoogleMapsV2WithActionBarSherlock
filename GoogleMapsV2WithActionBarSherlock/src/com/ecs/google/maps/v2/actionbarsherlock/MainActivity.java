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
      //Used to put dark icons on light action bar
      boolean isLight = MainActivity.THEME == R.style.Theme_Sherlock_Light;
      
      menu.add("Save")
          .setIcon(isLight ? R.drawable.ic_compose_inverse : R.drawable.ic_compose)
          .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

      menu.add("Search")
          .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

      menu.add("Refresh")
          .setIcon(isLight ? R.drawable.ic_refresh_inverse : R.drawable.ic_refresh)
          .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

      return true;
  }    
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
	  if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
          return false;
      }
      THEME = item.getItemId();
      
      Toast.makeText(this, "Theme changed to \"" + item.getTitle() + "\"", Toast.LENGTH_SHORT).show();
      return true;
  }
}