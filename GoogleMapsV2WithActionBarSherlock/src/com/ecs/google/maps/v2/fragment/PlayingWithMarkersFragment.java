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
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ecs.google.maps.v2.actionbarsherlock.R;
import com.ecs.google.maps.v2.component.SherlockMapFragment;
import com.ecs.google.maps.v2.util.GoogleMapUtis;
import com.ecs.google.maps.v2.util.ViewUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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

/**
 * 
 * Some information regarding menu items in Fragments in ViewPagers combined
 * with screen orientation changes.
 * 
 * https://groups.google.com/forum/#!topic/actionbarsherlock/0cFiieEBrzo/discussion
 * http://pastebin.com/X5ixKEfq
 * 
 * The reason we are using SherlockMapFragment here is to have support
 * for the ABS menus.
 * 
 * @author ddewaele
 *
 */
public class PlayingWithMarkersFragment extends SherlockMapFragment {

	private GoogleMap googleMap;

	private List<Marker> markers = new ArrayList<Marker>();
	private Marker selectedMarker;

	private Polyline polyLine;
	private PolylineOptions rectOptions = new PolylineOptions();

	Handler handler = new Handler();
	Random random = new Random();
	Runnable runner = new Runnable() {
        @Override
        public void run() {
            setHasOptionsMenu(true);
        }
    };
	
    
    /**
     * 
     * Here we do some special handling for the menuItems.
     * We also 
     * 		retrieve a reference to our GoogleMap object
     * 		initialize the polyLine
     * 		setup an OnMapClickListener to add markers
     * 
     */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		handler.postDelayed(runner, random.nextInt(2000));
		
		if (savedInstanceState!=null) {
			System.out.println(" ((((((((((((( restoring state.....");
			boolean b = savedInstanceState.getBoolean("controlsvisible");
			controlsvisible = b;
		}

		View view = super.onCreateView(inflater, container, savedInstanceState);
		//View view = inflater.inflate(R.id.fragmentContainer, container,false);

		googleMap = getMap();
		polyLine = initializePolyLine();

		googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

			@Override
			public void onMapClick(LatLng latLng) {
				addMarkerToMap(latLng);
				updatePolyLine(latLng);
			}

		});

		ViewUtils.initializeMargin(getActivity(), view);

		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.playing_with_markers_menu, menu);
	}

	private boolean controlsvisible = false;
	
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  super.onSaveInstanceState(savedInstanceState);
	  savedInstanceState.putBoolean("controlsvisible", controlsvisible);
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		  if (item.getItemId() == R.id.action_bar_clear_locations) {
			  clearMarkers();
		  } else if (item.getItemId() == R.id.action_bar_add_default_locations) {
			  addDefaultLocations();
		  } else if (item.getItemId() == R.id.action_bar_zoom) {
//			  GoogleMapUtis.fixZoom(googleMap, markers);
			  //new DirectionsFetcher().execute();

			  startActivityForResult(new Intent(getActivity(), DirectionsInputFragment.class), DirectionsInputFragment.RESULT_CODE);
			  /*
			  if (!controlsvisible) {
				  DirectionsInputFragment directionsInputFragment = new DirectionsInputFragment();
				  FragmentTransaction transaction = getFragmentManager().beginTransaction();

				transaction.replace(R.id.fragmentContainer, directionsInputFragment);
				transaction.addToBackStack(null);
				controlsvisible=true;
				// Commit the transaction
				transaction.commit();
			  } else {
				  FragmentTransaction transaction = getFragmentManager().beginTransaction();
				  Fragment tobeRemoved = getFragmentManager().findFragmentById(R.id.fragmentContainer);
				  transaction.remove(tobeRemoved);
				  controlsvisible = false;
				  transaction.commit();
			  }
			  */
			
		  } else if (item.getItemId() == R.id.action_bar_toggle_style) {
			  GoogleMapUtis.toggleStyle(googleMap);
		  }
		  
	      Toast.makeText(getActivity(), "Menu id  \"" + item.getItemId() + "\" clicked.", Toast.LENGTH_SHORT).show();
	      return true;
	}
	
	private void addDefaultLocations() {
        addMarkerToMap(new LatLng(50.961813797827055,3.5168474167585373));
        addMarkerToMap(new LatLng(50.96085423274633,3.517405651509762));
        addMarkerToMap(new LatLng(50.96020550146382,3.5177918896079063));
        addMarkerToMap(new LatLng(50.95936754348453,3.518972061574459));
        addMarkerToMap(new LatLng(50.95877285446026,3.5199161991477013));
        addMarkerToMap(new LatLng(50.958179213755905,3.520646095275879));
        addMarkerToMap(new LatLng(50.95901719316589,3.5222768783569336));
        addMarkerToMap(new LatLng(50.95954430150347,3.523542881011963));
        addMarkerToMap(new LatLng(50.95873336312275,3.5244011878967285));
        addMarkerToMap(new LatLng(50.95955781702322,3.525688648223877));
        addMarkerToMap(new LatLng(50.958855004782116,3.5269761085510254));
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
			Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
					 .title("title")
					 .snippet("snippet"));
			markers.add(marker);
			
		}
	}

	/**
	 * Clears all markers from the map.
	 */
	public void clearMarkers() {
		googleMap.clear();
    	markers.clear();		
    	polyLine = initializePolyLine();
	}

	/**
	 * Remove the currently selected marker.
	 */
	public void removeSelectedMarker() {
		this.markers.remove(this.selectedMarker);
		this.selectedMarker.remove();
	}	
	
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
		rectOptions = new PolylineOptions();
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
	
	static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
	static final JsonFactory JSON_FACTORY = new JacksonFactory();

	 private class DirectionsFetcher extends AsyncTask<URL, Integer, String> {
	     
		 private List<LatLng> latLngs = new ArrayList<LatLng>();
		 private String origin = null; 
		 private String destination= null;
		private String to;
		private String from;
		 
		 public DirectionsFetcher(String from,String to) {
			this.from = from;
			this.to = to;
		}
		 
		 @Override
		protected void onPreExecute() {
			super.onPreExecute();
			getActivity().setProgressBarIndeterminateVisibility(Boolean.TRUE); 
//	    	 System.out.println(" ------------ " + markers.get(0).getPosition());
//	    	 System.out.println(" ------------ " + markers.get(0).getPosition().toString());
//	    	 System.out.println(" ------------ " + markers.get(0).getPosition().latitude + "," + markers.get(0).getPosition().longitude);
	    	 
	    	 
	    	 //origin = markers.get(0).getPosition().latitude + "," + markers.get(0).getPosition().longitude;
	    	 //destination= markers.get(1).getPosition().latitude + "," + markers.get(1).getPosition().longitude;
	    	 
	    	 origin = from;
	    	 destination = to;
//	    	 origin = "Chicago,IL";
//	    	 destination = "Los Angeles,CA";
			
		}
		 
		 protected String doInBackground(URL... urls) {
			 android.os.Debug.waitForDebugger();
	    	 try {
	    	 HttpRequestFactory requestFactory =
	    		        HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
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
	    		    String encodedPoints = directionsResult.routes.get(0).overviewPolyLine.points;
					System.out.println(encodedPoints);
	    		    //String responseAsString = httpResponse.parseAsString();
					//System.out.println("Response = " + responseAsString);
	    		    //FileUtils.writeToFile("directions-dump.txt", responseAsString, getActivity());
	    		    
	    		    latLngs = PolyUtil.decode(encodedPoints);
	    	 } catch (Exception ex) {
	    		 ex.printStackTrace();
	    	 }
	    	 return null;
	     
	     }

	     protected void onProgressUpdate(Integer... progress) {
	         //setProgressPercent(progress[0]);
	     }

	     protected void onPostExecute(String result) {
	         //showDialog("Downloaded " + result + " bytes");
	    	 clearMarkers();
	    	 addMarkersToMap(latLngs);
	    	 GoogleMapUtis.fixZoom(googleMap, markers);
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
		if (resultCode==DirectionsInputFragment.RESULT_CODE) {
			String from = data.getExtras().getString("from");
			String to = data.getExtras().getString("to");
			System.out.println("from = " + from);
			System.out.println("to = " + to);
			new DirectionsFetcher(from,to).execute();
		}
	}
	  
}
