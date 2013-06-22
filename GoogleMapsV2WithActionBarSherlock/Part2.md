## Markers
### Introduction

In this article, I'll show you some simple ways of dealing with Markers on your map. 
Markers have finally become first class citizens in the Google Maps for Android v2 API. Gone are the days where you need to work with low-level overlays and overlay items to get a simple marker on the Map. 
As of v2, we finally have `Marker` objects and an `addMarkerToMap` method on the map, so let's see what we can do with it...

But first we're going to take a little side-track. 

### Adding some tabs to our application.

The next 4 parts in this series will be included in a single Android demo application.
I'm going to use a wonderful library from [Andreas Stütz](https://plus.google.com/117122118961369445953/posts) called [PagerSlidingTabStrip](https://github.com/astuetz/PagerSlidingTabStrip) that adds a nice set of tabs to the application.
Setting up the project is really easy. Go checkout the Github page and download the library and the sample project if you're interested in finding out more.


### Adding markers to the map

Adding a marker to the map is very simple. The only thing you need to do is call the `addMarkerToMap` method on the googleMap.
Notice how we also store the returned marker from the addMarkerToMap method in an ArrayList here in order to do some Marker management later on.

	public void addMarkerToMap(LatLng latLng) {
		Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
				 .title("title")
				 .snippet("snippet"));
		markers.add(marker);

	}
	
A title and snippet can be provided to be displayed on the `InfoWindow` when selecting the marker.

###  Marker management

The googleMap doesn't expose a list of markers that have been added to the map, nor does it track the currently selected marker. 
In other words, you'll need to do your own `marker management`. Marker management is all about having a flexible way to keep 
track of the markers being used on your app, so that you can refer to them in an easy way.

It comes in handy when you want to interact with markers without going through the standard callbacks of the googleMap.

When you have a marker somewhere on the map and you didn't store the resulting marker in a list, the only way to access the marker again is by clicking on it, and using the marker argument in the callback.
	
	googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

				@Override
				public boolean onMarkerClick(Marker marker) {
					return false;
				}
			}
	);

If you wan to print all the markers on the map, programmatically highlight a specific marker, or jump from one marker to another you cannot do it unless you have some way of referencing these markers.
By keeping the track of the markers in an external list we can add some flexibility regarding the way we can interact with these markers.

### The selected marker

It's always interesting to know what marker is currently selected, and that's also part of Marker management. 
Suppose you have a use-case where a user can select a marker, and pull up a menu to delete that marker.
The action to delete the marker needs to know what marker should be deleted, so it needs to rely on an instance variable for that.

Our method to remove the selected marker would look like this:

	public void removeSelectedMarker() {
		this.markers.remove(this.selectedMarker);
		this.selectedMarker.remove();
	}

As we have a reference to the selected marker, it's simply a matter of removing it from our internal list and removing it from the map.
Keep in mind that it's your responsibility to make sure the internal list is kept in sync with whatever is shown on the map.
	
### Highlighting a marker

If you want to highlight a marker, you typically want to change it's appearance. One of the things you can change very easily now (since the latest Google Play Services update) is the icon on the map.

There are 2 ways to highlight a marker

- Clicking on a marker
- Programmatically highlight a marker.

### Clicking on a marker

If you want to intercept the user clicking on a marker, you simply need to implement an `OnMarkerClickListener`

	googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

				@Override
				public boolean onMarkerClick(Marker marker) {
					return false;
				}
			}
	);

- You return true if the listener has consumed the event and you don't want the default behavior to occur).
- You return false if  the default behavior should occur).

The default behavior is for the camera to move to the map and an info window to appear.
	
For example you could call the following method upon clicking the marker to change it's color.
	
	private void highLightMarker(Marker marker) {
		marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		marker.showInfoWindow();
		this.selectedMarker=marker;
	}	

TODO: showInfoWindow not needed ? depending on return true|false in onMarkerClick
			
If you want to programmatically highlight a marker, and you either have a reference to it, or you know the index of the marker, simply call the highlightMarker directly.
It will maintain the proper state (selectedMarker) and do the highlighting.


### Some useful methods to have to manage your markers

If you're working with maps a lot, and you have more than one app that uses maps, it's interesting to encapsulate all of this into your own custom map fragment.
The following generic methods can be placed on the MapFragment so that you can not only drop the fragment into your layout, but also re-use a lot of the business logic associated with marker management.

Adds a marker to the map.

	public void addMarkerToMap(LatLng latLng) {
		Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
				 .title("title")
				 .snippet("snippet"));
		markers.add(marker);

	}
	
Clears all markers from the map.

	public void clearMarkers() {
		googleMap.clear();
    	markers.clear();		
	}

Remove the currently selected marker.

	public void removeSelectedMarker() {
		this.markers.remove(this.selectedMarker);
		this.selectedMarker.remove();
	}	

Highlight the marker by index.

	private void highLightMarker(int index) {
		highLightMarker(markers.get(index));
	}

Highlight the marker by marker.

	private void highLightMarker(Marker marker) {
		marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
		marker.showInfoWindow();
		this.selectedMarker=marker;
	}	
	
### Re-usability

A lot of the stuff we've discussed here are generic / re-usable	that you want to have at your disposal when dealing with maps. 
It's important to position them on the proper level in your code. Placing them on the MapFragment is an ideal way to promote re-use as

- the methods are tightly coupled with the map
- the methods are eligable for re-used
- the MapFragment containing these methods can be easily embedded in another layout

### Custom InfoWindows
	
### Polylines
	
	
	private Polyline initializePolyLine() {
		//polyLinePoints = new ArrayList<LatLng>();
		rectOptions.add(this.markers.get(0).getPosition());
		return googleMap.addPolyline(rectOptions);
	}

As we add markers to the map we need to update the polyLine.
We do this by adding our the newly dropped marker onto the polyLine.
Notice how we need to call setPoints again. 
Simply doing polyLine.getPoints().add(latLng) doesn't work.
	
	/**
	 * Add the marker to the polyline.
	 */
	private void updatePolyLine(LatLng latLng) {
		List<LatLng> points = polyLine.getPoints();
		points.add(latLng);
		polyLine.setPoints(points);
	}

## References

http://www.youtube.com/watch?feature=player_embedded&v=nb2X9IjjZpM#!