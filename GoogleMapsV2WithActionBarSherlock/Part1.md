#Part 1 : Setting up the project.

##Introduction

In this tutorial I will show you can start using Google Naps Android API v2 in your Android application. 
The new API is a vast improvement over the original one and comes with many new features. 
Not only on a pure UI but also on the API level is the new Maps v2 library a "delight" for both end-users and developers.

A quick overview of these features

- Distribution via Google Play Services
- Full fragment support (no more MapView, no more single map / app limitation)
- New Map look and feel (new tiles, new gestures, 3D support ....)


In this guide I will show you how to

- setup the skeleton project
- install the library projecs
- setup your map key
- add the map to your application.

### Setting up the skeleton project.

I'm going to be using Eclipse ADT 22 to build the project. We'll start by creating e a standard Android project using the project wizard.

As our project is going to use

- the new Google Maps V2 library for Android, we're going to need the Google Play Services library project.
- ActionBarSherlock, we're going to need the ActionBarSherlock library 

Please checkout the resources at the end of the document on how to obtain these projects.

### Installing the library projects.

The first thing we need to do is download the library projects.

- Google Play Services (downloaded through the Android SDK Manager and extracted in sdk/extras/google/google_play_services)
- [ActionBarSherlock](http://actionbarsherlock.com/) can be downloaded from its homepage.

When importing Library projects into your Eclipse workspace, it's safer to have them somewhere outside of the current workspace before importing them.
Otherwise, you might run into the following error :

[TODO: add screenshot here] 

We are going to add the library projects into our Eclipse workspace by importing them. Browse to the Google Play Services path in the SDK and import the project. Do the same for ActionBarSherlock.
Once both projects compile in your workspace, you can start adding these library projects as dependencies to our project.

### Adding the necessary permissions

You need to make sure that the following properties are added to your manifest. (put them before the starting application tag) :

	<!-- Specify the correct minSDK and targetSDK versions. -->
	<uses-sdk android:minSdkVersion="8" android:targetSdkVersion="17"/>
	
	<!-- Google Maps related permissions -->
	<permission android:name="com.ecs.google.maps.v2.actionbarsherlock.permission.MAPS_RECEIVE" android:protectionLevel="signature"/>
	<uses-permission android:name="com.example.mapdemo.permission.MAPS_RECEIVE"/>
	 
	<!-- Network connectivity permissions -->
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
	<uses-permission android:name="android.permission.INTERNET"/>
	 
	<!-- Access Google based webservices -->
	<uses-permission android:name="com.google.android.providers.gsf.permission.READ_GSERVICES"/>
	 
	<!-- External storage for caching. -->
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	
	<!-- My Location -->
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
	
	<!-- Maps API needs OpenGL ES 2.0. -->
	<uses-feature android:glEsVersion="0x00020000" android:required="true"/>   


### Setting up the map key  

Inside the application tag we need to specify our map key. This needs to be done by adding a meta-data element inside the application element:

	<meta-data android:name="com.google.android.maps.v2.API_KEY" android:value="AIzaSyA_u1faIXRhx_Q7NafFbrhQJEerl6UUbPY"/>

The whole proceess of [getting a key is described in great length](https://developers.google.com/maps/documentation/android/start#the_google_maps_api_key) on the Google Android Maps v2 page so I'm not going to discuss it here.

![Overview picture](https://dl.dropboxusercontent.com/u/13246619/Blog%20Articles/GoogleMapsV2/google_maps_key.002.jpg)

### Adding the map fragment

We'll start with something very simple. We'll put a full-screen map into our application without worrying about ActionBarSherlock.

In order to add the map to our application we need to do 2 things.

- Define and add the fragment to the layout
- Implement the fragment

Adding the fragment to a layout is very simple. 

The Google Play Services library provides a simple MapFragment (com.google.android.gms.maps.MapFragment) that can be used out of the box.
Note that the MapFragment requires the native API Level 11 fragment implementation, so it's only available on devices with API level 11 and higher.
There is a support library equivalent called SupportMapFragment that is for use with the Android Support package's backport of fragments. 
In other words, the SupportMamFragment can be used on Android devices running API 10 and lower, as well as Android devices running 11 and higher. 


	<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
	    android:id="@+id/root"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    android:orientation="horizontal" >
	
	<fragment android:id="@+id/map"
	          android:layout_width="match_parent"
	          android:layout_height="match_parent"
	          android:name="com.google.android.gms.maps.MapFragment"/>
	</FrameLayout> 

Create the activity that will load up the layout above:

	package com.ecs.google.maps.v2.actionbarsherlock;
	
	import android.app.Activity;
	import android.os.Bundle;
	
	public class SimpleMapActivity extends Activity{
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.map);
		}
	
	}

And you should see your map.

Keep in mind that although the MapFragment can run in an Activity, the SupportMapFragment cannot, and needs to run in a FragmentActivity. 
Simply have your activity extend SupportFragment instead of Activity in order to resolve this. 

If not, you'll get the following error:

	Caused by: java.lang.ClassCastException: com.google.android.gms.maps.SupportMapFragment cannot be cast to android.app.Fragment

### Adding the actionBar.

The point of this article was to show you how you can include the actionBar in your app.

Typically if you want to embed a fragment in an application using ActionBarSherlock, you simply have your activity extend SherlockFragmentActivity.
However, this doesn't work with the MapFragment as you'll see the following error

	Caused by: java.lang.ClassCastException: com.google.android.gms.maps.MapFragment cannot be cast to android.support.v4.app.Fragment

There's no real built-in support for MapFragments in ActionBarSherlock, so you can't really use it out of the box, but it is very simple to add support.
You simply need to "bridge" the SupportMapFragment and the SherlockFragmentActivity. As this is a common requirement, lots of people already did it and you can find lots of examples on the web..




## Resources


- [Google Maps Android API v2](https://developers.google.com/maps/documentation/android/)
= Google Play Services Map Samples (included in the SDK)
 