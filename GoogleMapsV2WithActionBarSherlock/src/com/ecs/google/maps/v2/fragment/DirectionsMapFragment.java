package com.ecs.google.maps.v2.fragment;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ecs.google.maps.v2.actionbarsherlock.R;
import com.ecs.google.maps.v2.component.SherlockMapFragment;
import com.ecs.google.maps.v2.util.GoogleMapUtis;
import com.ecs.google.maps.v2.util.ViewUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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
import com.google.maps.android.SphericalUtil;

public class DirectionsMapFragment extends SherlockMapFragment {

	private static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	
	private Handler handler = new Handler();
	private Random random = new Random();
	private Runnable runner = new Runnable() {
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
	
//	private void addMarkerToMap(LatLng latLng) {
//		Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng)
//				 .title("title")
//				 .snippet("snippet"));
//		markers.add(marker);
//		
//	}
//	
//	/**
//	 * Adds a list of markers to the map.
//	 */
//	public void addMarkersToMap(List<LatLng> latLngs) {
//		for (LatLng latLng : latLngs) {
//			addMarkerToMap(latLng);
//		}
//	}
	
	/**
	 * Adds a list of markers to the map.
	 */
	public void addPolylineToMap(List<LatLng> latLngs) {
		PolylineOptions options = new PolylineOptions();
		for (LatLng latLng : latLngs) {
			options.add(latLng);
		}
		googleMap.addPolyline(options);
	}	

	/**
	 * Clears all markers from the map.
	 */
	public void clearMarkers() {
		googleMap.clear();
    	markers.clear();		
	}
	
	private Menu menu;
	private boolean directionsFetched = false;
	
	 
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		this.menu = menu;
		//menu.clear();
		inflater.inflate(R.menu.directions_menu, menu);
		updateNavigationStopStart();
	}
	

    private void updateNavigationStopStart() {
        MenuItem startAnimation = this.menu.findItem(R.id.action_bar_start_animation);
        startAnimation.setVisible(!animator.isAnimating() && directionsFetched);
        MenuItem stopAnimation = this.menu.findItem(R.id.action_bar_stop_animation);
        stopAnimation.setVisible(animator.isAnimating());
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		  if (item.getItemId() == R.id.action_bar_directions) {
			  startActivityForResult(new Intent(getActivity(), DirectionsInputActivity.class), DirectionsInputActivity.RESULT_CODE);
		  } else if (item.getItemId() == R.id.action_bar_start_animation) {
			  animator.startAnimation(true,latLngs);
			  updateNavigationStopStart();
		  } else if (item.getItemId() == R.id.action_bar_stop_animation) {
			  animator.stopAnimation();
			  updateNavigationStopStart();
		  } else if (item.getItemId() == R.id.action_bar_toggle_style) {
			  GoogleMapUtis.toggleStyle(googleMap);
			  
		  }
		  
	      return true;
	}	
	
	private List<LatLng> latLngs = new ArrayList<LatLng>();
	
	 private class DirectionsFetcher extends AsyncTask<URL, Integer, Void> {
	     
		
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
	    	 
		    	 System.out.println("Building get request");
    		    HttpRequest request = requestFactory.buildGetRequest(url);
    		    System.out.println("Executing get request");
    		    HttpResponse httpResponse = request.execute();
    		    System.out.println("Got response");
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
	    	 directionsFetched=true;
	    	 System.out.println("Adding polyline");
	    	 addPolylineToMap(latLngs);
	    	 System.out.println("Fix Zoom");
	    	 GoogleMapUtis.fixZoomForLatLngs(googleMap, latLngs);
	    	 System.out.println("Start anim");
	    	 animator.startAnimation(false, latLngs);
	    	 updateNavigationStopStart();
	    	 getActivity().setProgressBarIndeterminateVisibility(Boolean.FALSE);
	     }
	 }	
	 
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
	  
	  private Animator animator = new Animator();
	  private final Handler mHandler = new Handler();
	  
	  public class Animator implements Runnable {
			
			private static final int ANIMATE_SPEEED = 1500;
			private static final int ANIMATE_SPEEED_TURN = 1000;
			private static final int BEARING_OFFSET = 20;

			private final Interpolator interpolator = new LinearInterpolator();
			//private final Interpolator interpolator = new AccelerateDecelerateInterpolator();
			
			private boolean animating = false;
			
			private List<LatLng> latLngs = new ArrayList<LatLng>();
			
			int currentIndex = 0;
			
			float tilt = 90;
			float zoom = 15.5f;
			boolean upward=true;
			
			long start = SystemClock.uptimeMillis();
			
			LatLng endLatLng = null; 
			LatLng beginLatLng = null;
			
			boolean showPolyline = false;
			
			private Marker trackingMarker;
			
			public void reset() {
				resetMarkers();
				start = SystemClock.uptimeMillis();
				currentIndex = 0;
				endLatLng = getEndLatLng();
				beginLatLng = getBeginLatLng();
				
			}
			
			public void stopAnimation() {
				animating=false;
				mHandler.removeCallbacks(animator);
				
			}

			public void initialize(boolean showPolyLine) {
				reset();
				this.showPolyline = showPolyLine;
				
				highLightMarker(0);
				
				if (showPolyLine) {
					polyLine = initializePolyLine();
				}
				
				// We first need to put the camera in the correct position for the first run (we need 2 markers for this).....
				LatLng markerPos = latLngs.get(0);
				LatLng secondPos = latLngs.get(1);
				
				setupCameraPositionForMovement(markerPos, secondPos);
				
			}
			
			private void setupCameraPositionForMovement(LatLng markerPos,
					LatLng secondPos) {
				
				float bearing = GoogleMapUtis.bearingBetweenLatLngs(markerPos,secondPos);
				
				trackingMarker = googleMap.addMarker(new MarkerOptions().position(markerPos)
						 .title("title")
						 .snippet("snippet"));

				CameraPosition cameraPosition =
						new CameraPosition.Builder()
								.target(markerPos)
								.bearing(bearing + BEARING_OFFSET)
			                    .tilt(90)
			                    .zoom(googleMap.getCameraPosition().zoom >=16 ? googleMap.getCameraPosition().zoom : 16)
			                    .build();
				
				googleMap.animateCamera(
						CameraUpdateFactory.newCameraPosition(cameraPosition), 
						ANIMATE_SPEEED_TURN,
						new CancelableCallback() {
							
							@Override
							public void onFinish() {
								System.out.println("finished camera");
								animator.reset();
								Handler handler = new Handler();
								handler.post(animator);	
							}
							
							@Override
							public void onCancel() {
								System.out.println("cancelling camera");									
							}
						}
				);
			}		
			
			private Polyline polyLine;
			private PolylineOptions rectOptions = new PolylineOptions();

			
			private Polyline initializePolyLine() {
				//polyLinePoints = new ArrayList<LatLng>();
				rectOptions.add(latLngs.get(0));
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
			
//			public void stopAnimation() {
//				animator.stop();
//			}
			
			public void startAnimation(boolean showPolyLine,List<LatLng> latLngs) {
				if (trackingMarker!=null) {
					trackingMarker.remove();
				}
				this.animating = true;
				this.latLngs=latLngs;
				if (latLngs.size()>2) {
					initialize(showPolyLine);
				}
				
			}				
			
			public boolean isAnimating() {
				return this.animating;
			}


			@Override
			public void run() {
				
				long elapsed = SystemClock.uptimeMillis() - start;
				double t = interpolator.getInterpolation((float)elapsed/ANIMATE_SPEEED);
				LatLng intermediatePosition = SphericalUtil.interpolate(beginLatLng, endLatLng, t);
				
				trackingMarker.setPosition(intermediatePosition);
				
				if (showPolyline) {
					updatePolyLine(intermediatePosition);
				}
				
				if (t< 1) {
					mHandler.postDelayed(this, 16);
				} else {
					
					System.out.println("Move to next marker.... current = " + currentIndex + " and size = " + latLngs.size());
					// imagine 5 elements -  0|1|2|3|4 currentindex must be smaller than 4
					if (currentIndex<latLngs.size()-2) {
					
						currentIndex++;
						
						endLatLng = getEndLatLng();
						beginLatLng = getBeginLatLng();

						
						start = SystemClock.uptimeMillis();

						Double heading = SphericalUtil.computeHeading(beginLatLng, endLatLng);
						
						highLightMarker(currentIndex);
						
						CameraPosition cameraPosition =
								new CameraPosition.Builder()
										.target(endLatLng) // changed this...
					                    .bearing(heading.floatValue() + BEARING_OFFSET) // .bearing(bearingL  + BEARING_OFFSET)
					                    .tilt(tilt)
					                    .zoom(googleMap.getCameraPosition().zoom)
					                    .build();

						
						googleMap.animateCamera(
								CameraUpdateFactory.newCameraPosition(cameraPosition), 
								ANIMATE_SPEEED_TURN,
								null
						);
						
						start = SystemClock.uptimeMillis();
						mHandler.postDelayed(animator, 16);					
						
					} else {
						currentIndex++;
						highLightMarker(currentIndex);
						stopAnimation();
					}
					
				}
			}
			

			
			private LatLng getEndLatLng() {
				return latLngs.get(currentIndex+1);
			}
			
			private LatLng getBeginLatLng() {
				return latLngs.get(currentIndex);
			}

		};		  
	  
		/**
		 * Highlight the marker by index.
		 */
		private void highLightMarker(int index) {
			if (markers.size()>=index+1) {
				highLightMarker(markers.get(index));
			}
		}

		/**
		 * Highlight the marker by marker.
		 */
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
			if (marker!=null) {
				marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
				marker.showInfoWindow();
			}

			//Utils.bounceMarker(googleMap, marker);
			
			//TODO: DDW FIX THIS
			//this.selectedMarker=marker;
		}	

		private void resetMarkers() {
			for (Marker marker : this.markers) {
				marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			}
		}		
	  
}
