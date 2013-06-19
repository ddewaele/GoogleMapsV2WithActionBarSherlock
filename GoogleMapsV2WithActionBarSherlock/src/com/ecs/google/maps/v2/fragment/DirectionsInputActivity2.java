package com.ecs.google.maps.v2.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.ecs.google.maps.v2.actionbarsherlock.R;
import com.google.android.gms.maps.model.LatLng;
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

public class DirectionsInputActivity2 extends FragmentActivity {

	private static final String PLACES_API_KEY = "AIzaSyB1k1X5cGfk1Wma3ewD2Xg-FmFSOOnK_J4";
	
	
	private static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();

	protected static final int RESULT_CODE = 123;
	private AutoCompleteTextView from;
	private EditText to;


	private AutoCompleteTextView myAutoComplete;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.directions_input2);
		
//		//from = (EditText) findViewById(R.id.from);
//		to = (EditText) findViewById(R.id.to);
//		Button btnLoadDirections = (Button) findViewById(R.id.load_directions);
//		
//		//from.setText("Brussels, Belgium");
//		to.setText("Antwerp, Belgium");
//		btnLoadDirections.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Intent data = new Intent();
//				data.putExtra("from", from.getText().toString());
//				data.putExtra("to", to.getText().toString());
//				DirectionsInputActivity.this.setResult(RESULT_CODE, data);
//				DirectionsInputActivity.this.finish();
//			}
//		});
		
		String item[]={
				  "January", "February", "March", "April",
				  "May", "June", "July", "August",
				  "September", "October", "November", "December"
				};
		
	    myAutoComplete = (AutoCompleteTextView)findViewById(R.id.myautocomplete);
	    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
		myAutoComplete.setAdapter(adapter);
		adapter.setNotifyOnChange(true);
	    
	       myAutoComplete.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				System.out.println("onTextChanged");
				new PlacesFetcher(adapter,s.toString()).execute();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				System.out.println("beforeTextChanged");
			}
			
			@Override
			public void afterTextChanged(Editable s) {
//				System.out.println("afterTextChanged");
//				for (int i=0 ; i<10 ; i++ ) {
//					adapter.add("Abcde" + i);
//				}
//				adapter.notifyDataSetChanged();
				//adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);
				//myAutoComplete.setAdapter(adapter);
				//notifydatasetchanged forces the dropdown to be shown.
				//adapter.notifyDataSetChanged();
			}
		});
	       
	       
//	   final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,item);
//	   from = (AutoCompleteTextView) findViewById(R.id.from);
//	   from.setThreshold(3);
//	   //adapter.setNotifyOnChange(true);
//	   from.setAdapter(adapter);
//	   from.addTextChangedListener(new TextWatcher() {
//		   
//		   public void onTextChanged(CharSequence s, int start, int before, int count) {
//			   if (count%3 == 1) {
//				   //adapter.clear();
//				   //new PlacesFetcher(adapter,s.toString()).execute();
//			   }
//		   }
//	   
//
//		public void beforeTextChanged(CharSequence s, int start, int count,int after) {
//		}
//
//		public void afterTextChanged(Editable s) {
//		}
//	   });		
	}
	
	private class PlacesFetcher extends AsyncTask<URL, Integer, Void> {
	     
		private String input;
		List<Prediction> predictions  = new ArrayList<Prediction>();
		private ArrayAdapter<String> adapter;
		 
		 public PlacesFetcher(ArrayAdapter<String> adapter, String input) {
			this.adapter = adapter;
			this.input = input;
		}
		 
		 @Override
		protected void onPreExecute() {
			super.onPreExecute();
			DirectionsInputActivity2.this.setProgressBarIndeterminateVisibility(Boolean.TRUE);
			
		}
		 
		 protected Void doInBackground(URL... urls) {
			// android.os.Debug.waitForDebugger();
	    	 try {
	    		 HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
	    			 @Override
	    		     public void initialize(HttpRequest request) {
	    				 request.setParser(new JsonObjectParser(JSON_FACTORY));
	    			 }
	    		     });
	    	 
		    	 GenericUrl url = new GenericUrl("https://maps.googleapis.com/maps/api/place/autocomplete/json");
		    	 
		    	 url.put("input", input);
		    	 url.put("key", PLACES_API_KEY);
		    	 url.put("sensor",false);
	    	 
    		    HttpRequest request = requestFactory.buildGetRequest(url);
    		    HttpResponse httpResponse = request.execute();
    		    PlacesResult directionsResult = httpResponse.parseAs(PlacesResult.class);
    		    //String parseAsString = httpResponse.parseAsString();
    		    //System.out.println(parseAsString);
//    		    FileUtils.writeToFile("directions.json", parseAsString, getActivity());
    		    
    		   this.predictions = directionsResult.predictions;
	    	 } catch (Exception ex) {
	    		 ex.printStackTrace();
	    	 }
	    	 return null;
	     
	     }

	     protected void onProgressUpdate(Integer... progress) {
	     }

	     protected void onPostExecute(Void result) {
 		    for (Prediction prediction : predictions) {
				System.out.println(prediction);
				adapter.add(prediction.description);
			}
 		    adapter.notifyDataSetChanged();
	    	 DirectionsInputActivity2.this.setProgressBarIndeterminateVisibility(Boolean.FALSE);
	     }
	 }		
	
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
	
}
