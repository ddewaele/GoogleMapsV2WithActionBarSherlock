---
layout: index
title: Animating the map
---
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

the CameraPosition allows us to define 

- the target (where does the camera needs to go)
- the bearing (where should the camera be pointing at)
- the tilt
- the zoom.

If you don't specify a values for either one of them, the Camera will keep the current bearing, tilt and zoom while doing its movement.

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

- onFinish is called after the animation has completely finished
- onCancel is called when the animation has been stopped for some reason (call to stopAnimation or another animation started).

Note that you cannot have 2 animations running at the same time. 
As soon as the second animation is scheduled (through an animateCamera call), the first animation will be stopped abrublty, and you'll get a notification through the onCancel callback.

### Setting the correct bearing

We're going to try and improve our animation by taking into account the bearing...

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
	
[TODO refer to the google maps library for a better implementation]

This should give us a much cleaner animation.

### Moving the marker.

Animating the map is great and moving between these markers provides a nice effect but
there's currently no notion of the "current position" in the animation.

We're going to add a tracker marker that represents this current position, and we'll let 
that marker move along the path as we are moving the camera around. 

Moving the marker requires a bit more plumbing than the camera movement. There's no public API to fluently move the marker from point A to point B, so we'll need to do this ourselves.
Moving a marker between 2 points means that we simply need to change the markers position from time to time to a location along the path between point A and point B.

We need to take into account

- Setting the position of the marker to a new LatLng point.
- The different LatLng points between beginning and end that we'll use to shift the marker.
- How much time is spent moving the marker from beginning to end and hwo many position updates do want.

Setting the position of the marker to a new LatLng point is easy as it only takes a call to setPosition with the appropriate LatLng. 


For every line drawn between 2 points, there's an infine number of markers we can put
along the path between these 2 points. 
In our case howver, we're going to limit ourselves :)

We're going to give outselves 1.5 seconds to move between the 2 points. 
Within that timeframe we're going to put a marker every 16ms.

We're going to calculate the elapsed time between each frame to ensure we don't 
cross over the 1.5seconds. 

We're also going to use a LinearInterpolator that will return as a number between 0 and 1 to indicate how far we our in our animation

We'll use that number to calculate the coordinates of the intermediate point.
Once we have those coordinates, we set our tracking marker to that new position.

	long elapsed = SystemClock.uptimeMillis() - start;
	double t = interpolator.getInterpolation((float)elapsed/ANIMATE_SPEEED);
	
	double lat = t * endLatLng.latitude + (1-t) * beginLatLng.latitude;
	double lng = t * endLatLng.longitude + (1-t) * beginLatLng.longitude;
				
	LatLng intermediatePosition = new LatLng(lat, lng);
			
	trackingMarker.setPosition(intermediatePosition);

We'll also update our polyline with the new marker position, creating a trailing effect on the polyline.

	private void updatePolyLine(LatLng latLng) {
		List<LatLng> points = polyLine.getPoints();
		points.add(latLng);
		polyLine.setPoints(points);
	}


As long as the interpolator returns a value below 1, we'll continue this process.

Once it hits 1 or above, we know we've reached the end-marker for this animation, and we'll start with the next animation.

	06-19 07:36:57.728: I/System.out(4153): Move to next marker.... current = 7 and size = 55
	06-19 07:36:57.728: I/System.out(4153): Found elapsed = 1 t = 6.666666595265269E-4
	06-19 07:36:57.830: I/System.out(4153): Found elapsed = 103 t = 0.06866666674613953
	06-19 07:36:57.916: I/System.out(4153): Found elapsed = 187 t = 0.12466666847467422
	06-19 07:36:58.002: I/System.out(4153): Found elapsed = 271 t = 0.18066667020320892
	06-19 07:36:58.088: I/System.out(4153): Found elapsed = 360 t = 0.23999999463558197
	06-19 07:36:58.174: I/System.out(4153): Found elapsed = 441 t = 0.2939999997615814
	06-19 07:36:58.252: I/System.out(4153): Found elapsed = 523 t = 0.3486666679382324
	06-19 07:36:58.338: I/System.out(4153): Found elapsed = 608 t = 0.40533334016799927
	06-19 07:36:58.416: I/System.out(4153): Found elapsed = 689 t = 0.4593333303928375
	06-19 07:36:58.502: I/System.out(4153): Found elapsed = 777 t = 0.5180000066757202
	06-19 07:36:58.549: I/System.out(4153): Found elapsed = 825 t = 0.550000011920929
	06-19 07:36:58.572: I/System.out(4153): Found elapsed = 846 t = 0.5640000104904175
	06-19 07:36:58.595: I/System.out(4153): Found elapsed = 868 t = 0.5786666870117188
	06-19 07:36:58.627: I/System.out(4153): Found elapsed = 895 t = 0.596666693687439
	06-19 07:36:58.674: I/System.out(4153): Found elapsed = 949 t = 0.6326666474342346
	06-19 07:36:58.697: I/System.out(4153): Found elapsed = 973 t = 0.6486666798591614
	06-19 07:36:58.720: I/System.out(4153): Found elapsed = 997 t = 0.6646666526794434
	06-19 07:36:58.744: I/System.out(4153): Found elapsed = 1020 t = 0.6800000071525574
	06-19 07:36:58.775: I/System.out(4153): Found elapsed = 1046 t = 0.6973333358764648
	06-19 07:36:58.799: I/System.out(4153): Found elapsed = 1072 t = 0.7146666646003723
	06-19 07:36:58.822: I/System.out(4153): Found elapsed = 1096 t = 0.7306666374206543
	06-19 07:36:58.845: I/System.out(4153): Found elapsed = 1120 t = 0.746666669845581
	06-19 07:36:58.869: I/System.out(4153): Found elapsed = 1141 t = 0.7606666684150696
	06-19 07:36:58.986: I/System.out(4153): Found elapsed = 1262 t = 0.8413333296775818
	06-19 07:36:59.025: I/System.out(4153): Found elapsed = 1298 t = 0.8653333187103271
	06-19 07:36:59.064: I/System.out(4153): Found elapsed = 1336 t = 0.890666663646698
	06-19 07:36:59.088: I/System.out(4153): Found elapsed = 1361 t = 0.9073333144187927
	06-19 07:36:59.111: I/System.out(4153): Found elapsed = 1384 t = 0.9226666688919067
	06-19 07:36:59.142: I/System.out(4153): Found elapsed = 1417 t = 0.9446666836738586
	06-19 07:36:59.174: I/System.out(4153): Found elapsed = 1443 t = 0.9620000123977661
	06-19 07:36:59.197: I/System.out(4153): Found elapsed = 1466 t = 0.9773333072662354
	06-19 07:36:59.213: I/System.out(4153): Found elapsed = 1488 t = 0.9919999837875366
	06-19 07:36:59.236: I/System.out(4153): Found elapsed = 1511 t = 1.0073332786560059
	06-19 07:36:59.260: I/System.out(4153): Move to next marker.... current = 8 and size = 55

Animating between the subsequent markers involves the following code:


	currentIndex++;
	highLightMarker(currentIndex);
						
	beginLatLng = getBeginLatLng();
	endLatLng = getEndLatLng();

	start = SystemClock.uptimeMillis();

	Double heading = SphericalUtil.computeHeading(beginLatLng, endLatLng);

As you can see we update our currentIndex, highlight the marker, specify a new start time, and calculate a new heading for the animation.

	CameraPosition cameraPosition =
		new CameraPosition.Builder()
		.target(endLatLng)
		.bearing(heading.floatValue()) 
		.tilt(tilt)
		.zoom(googleMap.getCameraPosition().zoom)
		.build();

We transition to the next animation by first pointing our camera in the right direction.

	googleMap.animateCamera(
			CameraUpdateFactory.newCameraPosition(cameraPosition), 
			ANIMATE_SPEEED_TURN,
			null
	);
	
And finally start the animation again.

	mHandler.postDelayed(animator, 16);					

						
	


###References

[1]: http://www.geomidpoint.com/destination/
[2]: http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/2.2_r1.1/android/location/Location.java#Location.computeDistanceAndBearing%28double%2Cdouble%2Cdouble%2Cdouble%2Cfloat%5B%5D%29
[3]: http://williams.best.vwh.net/avform.htm#Intro "Aviation Formulary V1.46"



