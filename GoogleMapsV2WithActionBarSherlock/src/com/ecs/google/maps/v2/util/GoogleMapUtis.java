package com.ecs.google.maps.v2.util;

import com.google.android.gms.maps.GoogleMap;

public class GoogleMapUtis {

	public static void toggleStyle(GoogleMap googleMap) {
		if (GoogleMap.MAP_TYPE_NORMAL == googleMap.getMapType()) {
			googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);		
		} else {
			googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		}
	}
}
