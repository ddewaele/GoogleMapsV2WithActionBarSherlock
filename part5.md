---
layout: index
title: Using Google APIs on your map - Directions and Places
---

##Using Google APIs on your map : Directions and Places

In the following part I'll show you howto integrate with 2 popular Google APIs that you can use to enrich your Maps experience, the `Directions API` and the `Places API`.

The goal is to create a simple Directions application where the user can enter an origin and a destination.
As the user is typing the origin / destination, he'll receive hints from the Google Places API (autocompletion).
After the user has entered an origin and a destination, we'll show the directions on a map by drawing a polyline and animating along the path.

<iframe width="560" height="315" src="//www.youtube.com/embed/jen6zti4L3k?rel=0" frameborder="0"> </iframe>

We're going to be using AsyncTask to perform the HTTP processing in the background off the main thread. 
For the actual HTTP communication we're going to be using the [Google HTTP Client Library for Java][0].

###The Google Directions API
The Google Directions API is a service that calculates directions between locations using an HTTP request. We'll use it to draw a path (a polyline) between 2 points (origin and destination).

###The Google Places API
The Google Places API is a service that returns information about Places — defined within this API as establishments, geographic locations, or prominent points of interest — using HTTP requests. Place requests specify locations as latitude/longitude coordinates.
We're going to be using a small part of the Google Places API, namely the Places Autocomplete API.

###Places Autocomplete
The Google Places Autocomplete API is a web service that returns Place information based on text search terms, and, optionally, geographic bounds. The API can be used to provide autocomplete functionality for text-based geographic searches, by returning Places such as businesses, addresses, and points of interest as a user types.

###Directions input

We're going to use a GridLayout to build our directions input layout.
We add a couple of labels and 2 AutoCompleteTextView components to the layout.
The AutoCompleteTextView components will be used to pull in data from the Google Places API as the user is typing.

{% highlight xml %}
<android.support.v7.widget.GridLayout xmlns:android="http://schemas.android.com/apk/res/android"
	xmlns:app="http://schemas.android.com/apk/res-auto"
	android:layout_width="match_parent"
	android:layout_height="wrap_content"
	android:alignmentMode="alignBounds"
	android:orientation="horizontal"
	android:padding="16dp"
	android:useDefaultMargins="true"
	app:columnCount="1" >

	<TextView
		android:id="@+id/from_label"
		app:layout_gravity="fill_horizontal"
		android:text="@string/from"
		android:textColor="?android:textColorSecondary" />

	<AutoCompleteTextView
		android:id="@+id/from"
		android:layout_width="fill_parent"
		android:layout_height="wrap_content"
		app:layout_gravity="fill_horizontal"
		android:completionThreshold="3" />

	<TextView
		android:id="@+id/to_label"
		app:layout_gravity="fill_horizontal"
		android:text="@string/to"
		android:textColor="?android:textColorSecondary" />

	<AutoCompleteTextView
		android:id="@+id/to"
		android:layout_width="fill_parent"
		android:layout_height="wrap_content"
		app:layout_gravity="fill_horizontal"
		android:completionThreshold="3" />

	<Button
		android:id="@+id/load_directions"
		android:layout_columnSpan="@integer/create_account_pane_column_count"
		app:layout_gravity="fill_horizontal"
		android:text="@string/load_directions" />

</android.support.v7.widget.GridLayout>
{% endhighlight %}

In our Activity, we hook up our origin and destination AutoCompleteTextView components, set some default values, and attach a `PlacesAutoCompleteAdapter`
The `PlacesAutoCompleteAdapter` is responsible for delivering the places associated with the text that the user has keyed in.

{% highlight java %}
from = (AutoCompleteTextView) findViewById(R.id.from);
to = (AutoCompleteTextView) findViewById(R.id.to);

from.setText("Fisherman's Wharf, San Francisco, CA, United States");
to.setText("The Moscone Center, Howard Street, San Francisco, CA, United States");

from.setAdapter(new PlacesAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line));
to.setAdapter(new PlacesAutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line));
{% endhighlight %}

The `PlacesAutoCompleteAdapter` is an exact copy (rip-off) of the one that can be found in the excellent [Adding Autocomplete to your Android App][3] article.

It looks like this :

{% highlight java %}
private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
	private ArrayList<String> resultList;
	
	public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}
	
	@Override
	public int getCount() {
		return resultList.size();
	}

	@Override
	public String getItem(int index) {
		return resultList.get(index);
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults filterResults = new FilterResults();
				if (constraint != null) {
					// Retrieve the autocomplete results.
					resultList = autocomplete(constraint.toString());
					
					// Assign the data to the FilterResults
					filterResults.values = resultList;
					filterResults.count = resultList.size();
				}
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				if (results != null && results.count > 0) {
					notifyDataSetChanged();
				}
				else {
					notifyDataSetInvalidated();
				}
			}};
		return filter;
	}
}
{% endhighlight %}

The actual activity looks like this:

![directions_input](https://dl.dropboxusercontent.com/u/13246619/Blog%20Articles/GoogleMapsV2/directions_input.png)

As the user is typing, he'll get the hints from the Google Places Autocompletion / AutoCompleteTextView:

![google-places-autocomplete](https://dl.dropboxusercontent.com/u/13246619/Blog%20Articles/GoogleMapsV2/google-places-autocomplete.png)
		
The grunt of the work happens in the `autocomplete` method. I've used a different approach for that one as I wanted to use the [Google HTTP Client Library for Java][0] to connect to the Google API and parse the results, as opposed to using a lower-level `HttpURLConnection` to make the HTTP connection, and parsing the JSON response using a `JSONObject`.

###Communicating the the Google Services
  
The [Google HTTP Client Library for Java][0] provides an easy and convenient way to interact with JSON based webservices like the ones we'll be discussing today

We start our code by creating an `HttpTransport` and a `JsonFactory`. The type of `HttpTransport` that is to be used depends on the version of the target Android environment.

Application targeted at Gingerbread or higher should use NetHttpTransport. NetHttpTransport is built into the Android SDK and is found in all Java SDKs. 
In earlier version of Android SDKs the implementation of HttpURLConnection/NetHttpTransport was buggy, and using the ApacheHttpTransport was preferred. 
If you are targeting multiple Android API levels, simply call AndroidHttp.newCompatibleTransport() and it will decide of these two to use based on the Android SDK level.

The JSON Factory is the low-level JSON library implementation based on Jackson 2, needed to parse the JSON responses.

{% highlight java %}	
static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
static final JsonFactory JSON_FACTORY = new JacksonFactory();
{% endhighlight %}

We'll use the [Google HTTP Client Library for Java][0] for both the Places Autocompletion API call as well as for the Directions API call.

###Places Autocompletion API

Using our HttpTransport, we create an `HttpRequestFactory` (responsible for setting the parser) that we'll use to build our actual HTTP requests.

{% highlight java %}
HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
		 @Override
		 public void initialize(HttpRequest request) {
			 request.setParser(new JsonObjectParser(JSON_FACTORY));
		 }
	 }
);
{% endhighlight %}

But before we can make a request, we need to build a URL. this is done using the `GenericUrl` object. We pass in an encoded URL and append whatever parameters we need.
In this case, we provide an input, API key and sensor value.

{% highlight java %}
GenericUrl url = new GenericUrl(PLACES_API_AUTOCOMPLETION);
url.put("input", input);
url.put("key", PLACES_API_KEY);
url.put("sensor",false);
{% endhighlight %}

As this is a simple REST call, you can enter the following URL in your browser to see what it returns

	https://maps.googleapis.com/maps/api/place/autocomplete/json?input=Fisher&key=[YOUR API KEY]&sensor=false

Now that we have create a URL, we can go ahead and build a GET request with it.	
	
{% highlight java %}
HttpRequest request = requestFactory.buildGetRequest(url);	
{% endhighlight %}

This will give us an `HttpRequest` that we can execute by calling its `execute()` method. This will give us the `HttpResponse`.

{% highlight java %}
HttpResponse httpResponse = request.execute();
{% endhighlight %}
	
The cool thing about the [Google HTTP Client Library for Java][0] is that it can parse the response properly and take care of the JSON response handling for us. 
This releaves us from the burden of writing our own JSON handling.

When we call the `parseAs()` method on the HttpResponse object, we can provide a java class that the library will use to convert the JSON response.
The `parseAs()` method will return an object of that type that we can use the parse the response in a very straightforward way without having to worry about using JSON directly.

{% highlight java %}
PlacesResult directionsResult = httpResponse.parseAs(PlacesResult.class);

List<Prediction> predictions = directionsResult.predictions;
for (Prediction prediction : predictions) {
	resultList.add(prediction.description);
}
{% endhighlight %}

So how will the library to the mapping from JSON to our Java object ? Well, we can provide hints in our `PlacesResult` class (and child classes) using the `@Key` annotation.

Remember the Autocompletion API returned a JSON string like this (abbreviated) :

{% highlight json %}
{
   "predictions" : [
	  {
		 "description" : "Fishers, IN, United States",
		 "id" : "51f9b9731d3996a2e3919fbc0b8d7c52a215b918",
		 "matched_substrings" : [...],
		 "reference" : "CkQy...",
		 "terms" : [...],
		 "types" : [ "locality", "political", "geocode" ]
	  },
	  {
		 "description" : "Fishersville, VA, United States",
		 "id" : "25c391688a6f5479f94f53231beac994f79e80bc",
		 "matched_substrings" : [...],
		 "reference" : "CkQ3A...",
		 "terms" : [...],
		 "types" : [ "locality", "political", "geocode" ]
	  },
	  {
		 "description" : "Fisherman's Wharf, San Francisco, CA, United States",
		 "id" : "e390dabe9db9ed93c37572ced9a94400f88f1ad4",
		 "matched_substrings" : [...],
		 "reference" : "ClRL...",
		 "terms" : [...],
		 "types" : [ "neighborhood", "political", "geocode" ]
	  },
	  .....
   ],
   "status" : "OK"
}
{% endhighlight %}

In order to model this using java we create the following value objects:

{% highlight java %}
public static class PlacesResult {

	@Key("predictions")
	public List<Prediction> predictions;

  }

public static class Prediction {
  @Key("description")
  public String description;
  
  @Key("id")
  public String id;
  
}	  
{% endhighlight %}

This allows you to parse the response very easily

{% highlight java %}
List<Prediction> predictions = directionsResult.predictions;
for (Prediction prediction : predictions) {
	resultList.add(prediction.description);
}
{% endhighlight %}

The complete code is pretty condensed and simple:

{% highlight java %}
private ArrayList<String> autocomplete(String input) {
	ArrayList<String> resultList = new ArrayList<String>();
	
	try {
	
		HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
				 @Override
				 public void initialize(HttpRequest request) {
					 request.setParser(new JsonObjectParser(JSON_FACTORY));
				 }
			 }
		);
		
		GenericUrl url = new GenericUrl(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
		url.put("input", input);
		url.put("key", PLACES_API_KEY);
		url.put("sensor",false);
 
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse httpResponse = request.execute();
		PlacesResult directionsResult = httpResponse.parseAs(PlacesResult.class);

		List<Prediction> predictions = directionsResult.predictions;
		for (Prediction prediction : predictions) {
			resultList.add(prediction.description);
		}
	} catch (Exception ex) {
		ex.printStackTrace();
	}
	return resultList;
}	
{% endhighlight %}	

###Directions API call

The Directions API call use an AsyncTask to fetch the data in the background.	
	
{% highlight java %}
private class DirectionsFetcher extends AsyncTask<URL, Integer, String> {
{% endhighlight %}
We define a list of LatLng objects that we'll use to store our results	
	
{% highlight java %}
private List<LatLng> latLngs = new ArrayList<LatLng>();
{% endhighlight %}
And we'll do the heavy lifting in the doInBackground method.

As this is a simple REST call, you can enter the following URL in your browser to see what it returns

	http://maps.googleapis.com/maps/api/directions/json?origin=Chicago,IL&destination=Los%20Angeles,CA&sensor=false

Again, the GenericUrl object takes care of adding and properly encoding the query parameters. 

{% highlight java %}	
 GenericUrl url = new GenericUrl("http://maps.googleapis.com/maps/api/directions/json");
 url.put("origin", origin);
 url.put("destination", destination);
 url.put("sensor",false);
{% endhighlight %}

We've again built our own java object model based on the Directions API REST response.

{% highlight java %}
DirectionsResult directionsResult = httpResponse.parseAs(DirectionsResult.class);
{% endhighlight %}

The Directions API returned a JSON string like this (removed the legs info for abbrevity.)

{% highlight json %}
{
   "routes" : [
	  {
		 "bounds" : {
			"northeast" : {
			   "lat" : 41.9054370,
			   "lng" : -87.62978720
			},
			"southwest" : {
			   "lat" : 34.05236330,
			   "lng" : -118.24356010
			}
		 },
		 "copyrights" : "Map data ©2013 Google",
		 "legs" : [
			.. legs info
		 ],
		 "overview_polyline" : {
			"points" : "eir~Fd..."
		 },
		 "summary" : "I-80 W",
		 "warnings" : [],
		 "waypoint_order" : []
	  }
   ],
   "status" : "OK"	
}
{% endhighlight %}

The Directions API always returns a single route if no alternatives are requested, so we know we need to search in the first route that is returned.
Within that route, we are interested in the overview_polyline field that in turn contains a points field. 

This points field contains an object holding an array of encoded points that represent an approximate (smoothed) path of the resulting directions.

{% highlight java %}
String encodedPoints = directionsResult.routes.get(0).overviewPolyLine.points;
{% endhighlight %}

The following value object structure can be used to traverse the JSON path above. This is again implemented using a simple value object model.

{% highlight java %}
public static class DirectionsResult {
	@Key("routes")
	public List<Route> routes;
}

public static class Route {
	@Key("overview_polyline")
	public OverviewPolyLine overviewPolyLine;
}

public static class OverviewPolyLine {
	@Key("points")
	public String points;
}
{% endhighlight %}
		
You can see how it maps to the following JSON structure

{% highlight json %}
{ "routes" : [
	  {
		 "overview_polyline" : {
			"points" : "eir~Fdezu..."
		 }
	  }
   ]
}
{% endhighlight %}
	
The full code can be found here.	
	
{% highlight java %}	
protected String doInBackground(URL... urls) {
	try {
		HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
		@Override
		public void initialize(HttpRequest request) {
		request.setParser(new JsonObjectParser(JSON_FACTORY));
		}
		});

		GenericUrl url = new GenericUrl("http://maps.googleapis.com/maps/api/directions/json");
		url.put("origin", "Chicago,IL");
		url.put("destination", "Los Angeles,CA");
		url.put("sensor",false);

		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse httpResponse = request.execute();
		DirectionsResult directionsResult = httpResponse.parseAs(DirectionsResult.class);
		String encodedPoints = directionsResult.routes.get(0).overviewPolyLine.points;
		latLngs = PolyUtil.decode(encodedPoints);
	} catch (Exception ex) {
		ex.printStackTrace();
	}
	return null;
	}

	protected void onProgressUpdate(Integer... progress) {
	}

	protected void onPostExecute(String result) {
		clearMarkers();
		addMarkersToMap(latLngs);
	}
}	
{% endhighlight %}

###References

- [Google HTTP Client Library for Java][0]
- [The Google Directions API][1]
- [The Google Places API][2]
- [Places Autocomplete][5]
- [Adding Autocomplete to your Android App][3]
- [Google Maps Android API v2][4]
- [Online JSON Editor][6]


[0]: http://code.google.com/p/google-http-java-client/ "Google HTTP Client Library for Java"
[1]: https://developers.google.com/maps/documentation/directions/ "The Google Directions API"
[2]: https://developers.google.com/places/ "The Google Places API"
[3]: https://developers.google.com/places/training/autocomplete-android "Adding Autocomplete to your Android App"
[4]: https://developers.google.com/maps/documentation/android/ "Google Maps Android API v2"
[5]: https://developers.google.com/places/documentation/autocomplete "Places Autocomplete"
[6]: http://www.jsoneditoronline.org "Online JSON Editor"

[7]: http://www.youtube.com/watch?feature=player_embedded&v=nb2X9IjjZpM#!
[8]: https://github.com/googlemaps/android-maps-utils
[9]: https://developers.google.com/live/shows/585386324 "Fireside Chat with the Google Maps Team"
[10]: https://code.google.com/p/android-maps-extensions/
[11]: https://github.com/cyrilmottier/Polaris2