##Part 3 : Animating the map

In the previous sections we've seen how to setup your map applications, how to move around the map with the camera, and how to add markers and lines to the map.
In this last part we're going to create an animation that resembles a fly-over.

Given a series of LatLng points on the map, we would like to see the following happen when animating the map.

- Animate the camera on the map in the direction of the path represented by the first 2 points.
- Start animating a marker moving along these 2 points (first and second).
- When it reaches the second point, animate the camera so that it points to the third point.
- Start animating a marker moving along these 2 points (second and third).

The final animation looks like this:

[INSERT YOUTUBE VID]

Animating the camera on the map is one of the many cool new features of the Google Maps Android API v2.

We'll start our animation by simply animating to the different markers on the map. For the moment we won't even take into account the bearing of the camera.

We've already placed a couple of markers on the map to get us started.

In order to animate the camera, we first need to define where it should be pointed at. We do this by building a new CameraPosition object.

	CameraPosition cameraPosition =
					new CameraPosition.Builder()
							.target(new LatLng(0,0))
							.bearing(45)
							.tilt(90)
							.zoom(googleMap.getCameraPosition().zoom)
							.build();

the CameraPosition allos us to define the target (where does the camera needs to go), the bearing (where should the camera be pointing at), the tilt and the zoom.
We'll just hardcode these values for now...

Once we have the CameraPosition, we can pass it on the Google Map animateCamera method:

	googleMap.animateCamera(
					CameraUpdateFactory.newCameraPosition(cameraPosition), 
					ANIMATE_SPEEED_TURN,
					new CancelableCallback() {

						@Override
						public void onFinish() {
						}

						@Override
						public void onCancel() {
						}
					}
			);						

The callback provides us hooks when the animation finishes. 

onFinish is called after the animation has completely finished
onCancel is called when the animation has been stopped for some reason (call to stopAnimation or another animation started).

Note that you cannot have 2 animations running at the same time. When the second animation starts, the first animation will be stopped abrublty, and you'll get a notification through the onCancel callback.

### Setting the correct bearing

The resulting animation will look like this. As you can see, we're "bouncing" from one marker to another. The first thing we'll fix is the bearing of the camera.

A bearing can be calculated between 2 android.location.Location objects. As we're working with com.google.android.gms.maps.model.LatLng objects here, we first need to convert them into Location objects.

	private Location convertLatLngToLocation(LatLng latLng) {
		Location location = new Location("someLoc");
		location.setLatitude(latLng.latitude);
		location.setLongitude(latLng.longitude);
		return location;
	}

Once we have 2 Location objects, we can calculate the bearing between the 2. This is what we need to put on the Camera when transitioning to the target (the endLocation).

	private float bearingBetweenLatLngs(LatLng beginLatLng,LatLng endLatLng) {
		Location beginLocation = convertLatLngToLocation(beginLatLng);
		Location endLocation = convertLatLngToLocation(endLatLng);
		return beginLocation.bearingTo(endLocation);
	}

This should give us a much cleaner animation.

### Moving the marker.

Moving the marker requires a bit more plumbing than the camera movement. There's no public API to fluently move the marker from point A to point B, so we'll need to do this ourselves.
Moving a marker between 2 points means that we simply need to change the markers position from time to time to a location along the path between point A and point B.

We need to take into account

- Setting the position of the marker to a new LatLng point.
- The different LatLng points between beginning and end that we'll use to shift the marker.
- How much time is spent moving the marker from beginning to end and hwo many position updates do want.

Setting the position of the marker to a new LatLng point is easy as it only takes a call to setPosition with the appropriate LatLng. 
