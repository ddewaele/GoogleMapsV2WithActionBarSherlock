package com.ecs.google.maps.v2.actionbarsherlock;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class PlayingWithMarkers extends SherlockFragmentActivity {

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
		 polyLine = initializePolyLine();
	
		  googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
				
				@Override
				public void onMapClick(LatLng latLng) {
					System.out.println("On Map click");
					addMarkerToMap(latLng);
					updatePolyLine(latLng);
				}

			});
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main_menu, menu);
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
	
	private Polyline initializePolyLine() {
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
