package com.ecs.google.maps.v2.actionbarsherlock;

import java.util.List;

import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

public class Utils {

	public static void bounceMarker(GoogleMap googleMap,final Marker marker) {
	//Make the marker bounce
    final Handler handler = new Handler();
    
    final long startTime = SystemClock.uptimeMillis();
    final long duration = 2000;
    
    Projection proj = googleMap.getProjection();
    final LatLng markerLatLng = marker.getPosition();
    Point startPoint = proj.toScreenLocation(markerLatLng);
    startPoint.offset(0, -100);
    final LatLng startLatLng = proj.fromScreenLocation(startPoint);

    final Interpolator interpolator = new BounceInterpolator();

    handler.post(new Runnable() {
        @Override
        public void run() {
            long elapsed = SystemClock.uptimeMillis() - startTime;
            float t = interpolator.getInterpolation((float) elapsed / duration);
            double lng = t * markerLatLng.longitude + (1 - t) * startLatLng.longitude;
            double lat = t * markerLatLng.latitude + (1 - t) * startLatLng.latitude;
            marker.setPosition(new LatLng(lat, lng));

            if (t < 1.0) {
                // Post again 16ms later.
                handler.postDelayed(this, 16);
            }
        }
    });
	}
	
	public static void fixZoom(GoogleMap googleMap, List<Marker> markers) {
	    LatLngBounds.Builder bc = new LatLngBounds.Builder();

	    for (Marker marker : markers) {
	        bc.include(marker.getPosition());
	    }

	    googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50),4000,null);
	}
}
