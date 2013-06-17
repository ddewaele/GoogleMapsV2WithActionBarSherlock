package com.ecs.google.maps.v2.fragment;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ecs.google.maps.v2.actionbarsherlock.R;
import com.ecs.google.maps.v2.component.SherlockMapFragment;
import com.ecs.google.maps.v2.util.FileUtils;
import com.ecs.google.maps.v2.util.GoogleMapUtis;
import com.ecs.google.maps.v2.util.ViewUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import com.google.maps.android.PolyUtil;

public class DirectionsMapFragment extends SherlockMapFragment {

	static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
	static final JsonFactory JSON_FACTORY = new JacksonFactory();

	Handler handler = new Handler();
	Random random = new Random();
	Runnable runner = new Runnable() {
        @Override
        public void run() {
            setHasOptionsMenu(true);
        }
    };
	private GoogleMap googleMap;
	private List<Marker> markers = new ArrayList<Marker>();
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		handler.postDelayed(runner, random.nextInt(2000));
		
		View view = super.onCreateView(inflater, container, savedInstanceState);

		googleMap = getMap();

		ViewUtils.initializeMargin(getActivity(), view);

		return view;
	}
	
	private void addMarkerToMap(LatLng latLng) {
		Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
				 .title("title")
				 .snippet("snippet"));
		markers.add(marker);
		
	}
	
	/**
	 * Adds a list of markers to the map.
	 */
	public void addMarkersToMap(List<LatLng> latLngs) {
		for (LatLng latLng : latLngs) {
			addMarkerToMap(latLng);
		}
	}
	
	/**
	 * Adds a list of markers to the map.
	 */
	public void addPolygonToMap(List<LatLng> latLngs) {
		PolygonOptions options = new PolygonOptions();
		for (LatLng latLng : latLngs) {
			options.add(latLng);
		}
		googleMap.addPolygon(options);
	}	

	/**
	 * Clears all markers from the map.
	 */
	public void clearMarkers() {
		googleMap.clear();
    	markers.clear();		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.directions_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		  if (item.getItemId() == R.id.action_bar_directions) {
			  startActivityForResult(new Intent(getActivity(), DirectionsInputActivity.class), DirectionsInputActivity.RESULT_CODE);
		  } else if (item.getItemId() == R.id.action_bar_zoom) {
			  GoogleMapUtis.fixZoomForMarkers(googleMap, markers);
		  } else if (item.getItemId() == R.id.action_bar_toggle_style) {
			  GoogleMapUtis.toggleStyle(googleMap);
			  
		  }
		  
	      return true;
	}	
	
	 private class DirectionsFetcher extends AsyncTask<URL, Integer, Void> {
	     
		 private List<LatLng> latLngs = new ArrayList<LatLng>();
		private String origin;
		private String destination;
		 
		 public DirectionsFetcher(String origin,String destination) {
			this.origin = origin;
			this.destination = destination;
		}
		 
		 @Override
		protected void onPreExecute() {
			super.onPreExecute();
			clearMarkers();
			getActivity().setProgressBarIndeterminateVisibility(Boolean.TRUE);
			
		}
		 
		 protected Void doInBackground(URL... urls) {
			 android.os.Debug.waitForDebugger();
	    	 try {
	    		 HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
	    			 @Override
	    		     public void initialize(HttpRequest request) {
	    				 request.setParser(new JsonObjectParser(JSON_FACTORY));
	    			 }
	    		     });
	    	 
		    	 GenericUrl url = new GenericUrl("http://maps.googleapis.com/maps/api/directions/json");
		    	 
		    	 
		    	 url.put("origin", origin);
		    	 url.put("destination", destination);
		    	 url.put("sensor",false);
	    	 
    		    HttpRequest request = requestFactory.buildGetRequest(url);
    		    HttpResponse httpResponse = request.execute();
    		    DirectionsResult directionsResult = httpResponse.parseAs(DirectionsResult.class);
//    		    String parseAsString = httpResponse.parseAsString();
//    		    FileUtils.writeToFile("directions.json", parseAsString, getActivity());
    		    
    		    String encodedPoints = directionsResult.routes.get(0).overviewPolyLine.points;
    		    latLngs = PolyUtil.decode(encodedPoints);
	    	 } catch (Exception ex) {
	    		 ex.printStackTrace();
	    	 }
	    	 return null;
	     
	     }

	     protected void onProgressUpdate(Integer... progress) {
	     }

	     protected void onPostExecute(Void result) {
	    	 //addMarkersToMap(latLngs);
	    	 addPolygonToMap(latLngs);
	    	 GoogleMapUtis.fixZoomForLatLngs(googleMap, latLngs);
	    	 getActivity().setProgressBarIndeterminateVisibility(Boolean.FALSE);
	     }
	 }	
	 
	 /** Feed of Google+ activities. */
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
	  
	  @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode==DirectionsInputActivity.RESULT_CODE) {
			String from = data.getExtras().getString("from");
			String to = data.getExtras().getString("to");
			System.out.println("from = " + from);
			System.out.println("to = " + to);
			new DirectionsFetcher(from,to).execute();
		}
	}
}
