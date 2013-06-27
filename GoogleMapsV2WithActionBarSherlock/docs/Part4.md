---
layout: index
title: Migrating from v1 maps to v2 maps.
---

### Part 4 : Migrating from v1 maps to v2 maps.

When moving to v2 of the Maps API for Android, you'll immediately notice that this is a completely new API with very little regard to backwards compatibility ( = a good thing in this case). 
You'll see a lot of compile errors as many of the existing classes will now be missing like MapActivity, GeoPoint, RecticleDrawMode and Overlay.
The new objects are all included in the com.google.android.gms.maps.* package.

In this section I would like to go over some of the differences between the v1 and v2 maps library.

- Users location
- Marker support
- HighLighting markers
- Animation

#### Users location

##### Android Maps v1

In Maps v1, the MyLocationOverlay was used to detect the users location

https://developers.google.com/maps/documentation/android/v1/reference/com/google/android/maps/MyLocationOverlay

	List overlaysoverlays = mapView.getOverlays();
	MyLocationOverlay myLocationOverlay = new MyLocationOverlay(this, mapView);
	myLocationOverlay.enableMyLocation();
	overlays.add(myLocationOverlay);
	
##### Android Maps v2

You can activate show a `My Location` button on the map very easily like this:

	googleMap.setMyLocationEnabled(true);
	
The `My Location` button appears in the top right corner of the screen only when the My Location layer is enabled.
When a user clicks the button, the camera animates to focus on the user's current location if the user's location is currently known.

		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		Criteria criteria = new Criteria();

		String provider = locationManager.getBestProvider(criteria, true);

		Location location = locationManager.getLastKnownLocation(provider);

		if(location!=null){
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();

			LatLng latLng = new LatLng(latitude, longitude);

			googleMap.addMarker(new MarkerOptions().position(myPosition).title("Start"));
		}

#### Highlighting markers

Highlighting markers has become very simple. Changing the marker icon has become a one-liner.

##### Android Maps v1

	private void highlightMarker(int index) {
		Drawable d = getResources().getDrawable(R.drawable.pin_green);
		Rect copyBounds = overlay.getMarker(0).copyBounds();
		d.setBounds(copyBounds);
		overlay.setMarker(d);
		SitesOverlay sitesOverlay =  (SitesOverlay) map.getOverlays().get(0);
		sitesOverlay.refresh();
	}

##### Android Maps v2	

	private void highLightMarker(int index) {
		Marker marker = markers.get(index))
		marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
	}
			

#### Animation

The ability to animate the map using the CameraPositions is a vast improvement over the v1 api. \
Not only is it a lot fancier from a UI perspective, but it also requires far less code than before.


##### Android Maps v1	

The v1 API was very limited in turns of animations. Not only did the map lack many of the gestures (bearing / tilt) we have today, the only way to have some form of animation was to animate the map to a certain GeoPoint location, giving the user a sense of motion.
We also didn't have marker objects, so we needed to model our own locationResources acting as markers.
We needed to convert those objects to GeoPoints, as that is the only object that the MapView could animate to.
We needed to manage a lot of state (is the animation still in progress, did the user press the stop button, ....)

this resulted in a lot of code like this :


		MapView map = (MapView) findViewById(R.id.map);
		MapController mc = map.getController();			
		
		ReplayRouteOnMap replayRouteOnMap; 
		replayRouteOnMap.execute();		
		
		private class ReplayRouteOnMap extends AsyncTask<Uri, String, Void> {
		
			int replayIndex=0;

			@Override
			protected Void doInBackground(Uri...params) {
			
				for (LocationResource locationResource : allLocationResources) {
					if (!playbackRequestedToStop) {
						highlightMarker(replayIndex-1);
						GeoPoint p = new GeoPoint((int)(locationResource.getLatitude() * 1E6),  (int)(locationResource.getLongitude()* 1E6));
						
						//TODO: should fix the Caused by: java.lang.NullPointerException at com.google.android.maps.MapController.animateTo(MapController.java:244) error
						//unsure what cause it.
						try {
							mc.animateTo(p);
						} catch (Exception ex) {
							Utils.addErrorMsg(this, "Error during animateTo in history map", ex);
						}

						try {
							publishProgress(allLocationResourcesAsString.get(replayIndex-1) + " at " + locationResource.getDateFormatted());
						} catch (Exception ex) {
							publishProgress("Location " + replayIndex + "/" + allLocationResources.size() + " at " + locationResource.getDateFormatted());	
						}
						SystemClock.sleep(replaySpeed);			
					} else {
						Utils.logDebugMsg(this,"request to stop route playback confirmed");
						playbackInProgress=false;
						return null;
					}
				}
			}
			
			@Override
			protected void onProgressUpdate(String... values) {
				// Update UI
				pinInfoText.setText(values[0]);
			}			
		}
		
##### Android Maps v2

Changing the view on the map in order to create beautiful animations can now be done in a much cleaner way. 
Not only do we have a nice set Camera objects to work with (allowing is to define the tilt / bearing / target / zoom of the camera), 
but proper animation support is built into the API, allowing us to specify durations and a CancelableCallback, giving us full control over the animation process.
 

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

		
		
##References

- [Google Maps Android API v2] (https://developers.google.com/maps/documentation/android/)
- [Android Maps Extensions](https://github.com/mg6maciej/android-maps-extensions)
- [Google Maps API bug reports and feature requests](http://code.google.com/p/gmaps-api-issues/issues/list)


## Code snippets

	private void fixZoom() {
		List<LatLng> points = route.getPoints(); // route is instance of PolylineOptions 

		LatLngBounds.Builder bc = new LatLngBounds.Builder();

		for (LatLng item : points) {
			bc.include(item);
		}

		map.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 50));
	}


TODO : add the drop-effect
TODO : add zoom at the end.
TODO : use tabs in the app to show the different tutorial sections
TODO : incorporate programmatic VS user moves.
TODO : write something on the map
TODO : add something about mock location testing (https://github.com/paulhoux/Android-MockProviderGPS/blob/master/src/nl/cowlumbus/android/mockgps/MockGpsProviderActivity.java)
