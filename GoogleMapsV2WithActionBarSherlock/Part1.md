#Part 1 : Setting up the project.

##Introduction

In this tutorial I will show you can start using Google Naps Android API v2 in your Android application. 
The new API is a vast improvement over the original one and comes with many new features.

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



## Resources


[Google Maps Android API v2](https://developers.google.com/maps/documentation/android/)