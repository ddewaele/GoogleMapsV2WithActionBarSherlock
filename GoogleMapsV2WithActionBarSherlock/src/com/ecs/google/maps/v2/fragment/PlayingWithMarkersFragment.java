package com.ecs.google.maps.v2.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ecs.google.maps.v2.actionbarsherlock.R;
import com.ecs.google.maps.v2.actionbarsherlock.R.id;
import com.ecs.google.maps.v2.actionbarsherlock.R.menu;
import com.ecs.google.maps.v2.component.SherlockMapFragment;
import com.ecs.google.maps.v2.util.GoogleMapUtis;
import com.ecs.google.maps.v2.util.ViewUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * 
 * Some information regarding menu items in Fragments in ViewPagers combined
 * with screen orientation changes.
 * 
 * https://groups.google.com/forum/#!topic/actionbarsherlock/0cFiieEBrzo/discussion
 * http://pastebin.com/X5ixKEfq
 * 
 * The reason we are using SherlockMapFragment here is to have support
 * for the ABS menus.
 * 
 * @author ddewaele
 *
 */
public class PlayingWithMarkersFragment extends SherlockMapFragment {

	private GoogleMap googleMap;

	private List<Marker> markers = new ArrayList<Marker>();
	private Marker selectedMarker;

	private Polyline polyLine;
	private PolylineOptions rectOptions = new PolylineOptions();

	Handler handler = new Handler();
	Random random = new Random();
	Runnable runner = new Runnable() {
        @Override
        public void run() {
            setHasOptionsMenu(true);
        }
    };
	
    
    /**
     * 
     * Here we do some special handling for the menuItems.
     * We also 
     * 		retrieve a reference to our GoogleMap object
     * 		initialize the polyLine
     * 		setup an OnMapClickListener to add markers
     * 
     */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		handler.postDelayed(runner, random.nextInt(2000));

		View view = super.onCreateView(inflater, container, savedInstanceState);

		googleMap = getMap();
		polyLine = initializePolyLine();

		googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

			@Override
			public void onMapClick(LatLng latLng) {
				addMarkerToMap(latLng);
				updatePolyLine(latLng);
			}

		});

		ViewUtils.initializeMargin(getActivity(), view);

		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.playing_with_markers_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		  if (item.getItemId() == R.id.action_bar_remove_location) {
			  //mapFragment.removeSelectedMarker();
		  } else if (item.getItemId() == R.id.action_bar_clear_locations) {
			  clearMarkers();
		  } else if (item.getItemId() == R.id.action_bar_add_default_locations) {
		    	addDefaultLocations();
		  } else if (item.getItemId() == R.id.action_bar_zoom) {
			  //mapFragment.toggleStyle();
		  } else if (item.getItemId() == R.id.action_bar_toggle_style) {
			  GoogleMapUtis.toggleStyle(googleMap);
		  }
		  
	      Toast.makeText(getActivity(), "Menu id  \"" + item.getItemId() + "\" clicked.", Toast.LENGTH_SHORT).show();
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

	private void addMarkerToMap(LatLng latLng) {
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
    	polyLine = initializePolyLine();
	}

	/**
	 * Remove the currently selected marker.
	 */
	public void removeSelectedMarker() {
		this.markers.remove(this.selectedMarker);
		this.selectedMarker.remove();
	}	
	
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
	
	private Polyline initializePolyLine() {
		rectOptions = new PolylineOptions();
		//polyLinePoints = new ArrayList<LatLng>();
		//rectOptions.add(this.markers.get(0).getPosition());
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

}
