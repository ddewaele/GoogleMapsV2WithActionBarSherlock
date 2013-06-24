##Using Google APIs on your map : Directions and Places

In the following part I'll show you howto integrate with 2 popular Google APIs that you can use to enrich your Maps experience, the `Directions API` and the `Places API`.
We're going to be using AsyncTask to perform the HTTP processing in the background off the main thread. For the actual HTTP communication we're going to be using the [0][Google HTTP Client Library for Java].

###The Google Directions API
The Google Directions API is a service that calculates directions between locations using an HTTP request. We'll use it to draw a path (a polyline) between 2 points (origin and destination).

###The Google Places API
The Google Places API is a service that returns information about Places — defined within this API as establishments, geographic locations, or prominent points of interest — using HTTP requests. Place requests specify locations as latitude/longitude coordinates.
We're going to be using a small part of the Google Places API, namely the Places Autocomplete API.

###[Places Autocomplete][5]
The Google Places Autocomplete API is a web service that returns Place information based on text search terms, and, optionally, geographic bounds. The API can be used to provide autocomplete functionality for text-based geographic searches, by returning Places such as businesses, addresses, and points of interest as a user types.

###Communicating the the Google Services
  
The [Google HTTP Client Library for Java][0] provides an easy and convenient way to interact with JSON based webservices like the ones we'll be discussing today

We start our code by creating an `HttpTransport` and a` JsonFactory`. The type of `HttpTransport` that is to be used depends on the version of the target Android environment.

Application targeted at Gingerbread or higher should use NetHttpTransport. NetHttpTransport is built into the Android SDK and is found in all Java SDKs. 
In ealier version of Android SDKs the implementation of HttpURLConnection (the basis NetHttpTransport) for was buggy, and the ApacheHttpTransport was preferred. 
If you are targetting all these Android SDKs, simply call AndroidHttp.newCompatibleTransport() and it will decide of these two to use based on the Android SDK level.

The JSON Factory is the low-level JSON library implementation based on Jackson 2, needed to parse the JSON responses.
	
	static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
	static final JsonFactory JSON_FACTORY = new JacksonFactory();

We'll use an AsyncTask to fetch the data in the background.	
	
	private class DirectionsFetcher extends AsyncTask<URL, Integer, String> {

We define a list of LatLng objects that we'll use to store our results	
	
	private List<LatLng> latLngs = new ArrayList<LatLng>();

And we'll do the heavy lifting in the doInBackground method.

Using our HttpTransport, we create an `HttpRequestFactory` (responsible for setting the parser) that we'll use to build our actual HTTP requests.
But before we can make a request, we need to build a URL. this is done using the `GenericUrl` object. We pass in an encoded URL and append whatever parameters we need.
In this case, we provide an origin, destination and sensor value.

As this is a simple REST call, you can enter the following URL in your browser to see what it returns

	http://maps.googleapis.com/maps/api/directions/json?origin=Chicago,IL&destination=Los%20Angeles,CA&sensor=false

The GenericUrl object takes care of adding and properly encoding the query parameters. 
	
Now that we have create a URL, we can go ahead and build a GET request with it.	
	
	HttpRequest request = requestFactory.buildGetRequest(url);	
	
This will give us an `HttpRequest` that we can execute by calling its `execute()` method. This will give us the `HttpResponse`.

	HttpResponse httpResponse = request.execute();
	
The cool thing about the [Google HTTP Client Library for Java][0] is that it can parse the response properly and take care of the JSON response handling for us. 
This releaves us from the burden of writing our own JSON handling.

When we call the `parseAs()` method on the HttpResponse object, we can provide a java class that the library will use to convert the JSON response.


	DirectionsResult directionsResult = httpResponse.parseAs(DirectionsResult.class);

So how will the library to the mapping from JSON to our Java object ? Well, we can provide hints using the `@Key` annotation.

Remember the Directions API returned a JSON string like this (removed the legs info for abbrevity.

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

The Directions API always returns a single route if no alternatives are requested, so we know we need to search in the first route that is returned.
Within that route, we are interested in the overview_polyline field that in turn contains a points field. 

This points field contains an object holding an array of encoded points that represent an approximate (smoothed) path of the resulting directions.

	String encodedPoints = directionsResult.routes.get(0).overviewPolyLine.points;

You'll notice how we have create a value object structure to traverse the JSON path. This is implemented using a simple value object model.

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
	
		

	{ "routes" : [
	      {
	         "overview_polyline" : {
	            "points" : "eir~Fdezu..."
	         }
	      }
	   ]
	}

	
The full code can be found here.	
	
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


###References

- [Google HTTP Client Library for Java][0]
- [The Google Directions API][1]
- [The Google Places API][2]
- [Places Autocomplete][5]
- [Adding Autocomplete to your Android App][3]
- [Google Maps Android API v2][4]

[0]: http://code.google.com/p/google-http-java-client/ "Google HTTP Client Library for Java"
[1]: https://developers.google.com/maps/documentation/directions/ "The Google Directions API"
[2]: https://developers.google.com/places/ "The Google Places API"
[3]: https://developers.google.com/places/training/autocomplete-android "Adding Autocomplete to your Android App"
[4]: https://developers.google.com/maps/documentation/android/ "Google Maps Android API v2"
[5]: https://developers.google.com/places/documentation/autocomplete "Places Autocomplete"