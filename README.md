##Introduction

This repository contains a number of projects that will help you get up to speed with Google Maps v2 on Android with ActionBarSherlock 4.3.1

It contains the following folders

- [GoogleMapsV2SkeletonV11](https://github.com/ddewaele/GoogleMapsV2WithActionBarSherlock/tree/master/GoogleMapsV2SkeletonV11) (Skeleton project targetted at API level 11 and higher)
- [GoogleMapsV2SkeletonV8](https://github.com/ddewaele/GoogleMapsV2WithActionBarSherlock/tree/master/GoogleMapsV2SkeletonV8) (Skeleton project using Support Library targetted at API level 8 and higher)
- [GoogleMapsV2WithActionBarSherlock](https://github.com/ddewaele/GoogleMapsV2WithActionBarSherlock/tree/master/GoogleMapsV2WithActionBarSherlock) (the complete sample application)
- [ActionBarSherlock](http://actionbarsherlock.com/) (The ActionBar we all love, available in this repository for your convenience.)
- google-play-services_lib (The Google Play Services lib, available in this repository for your convenience.)

##Tutorial

The tutorial guides can be found in the docs folder of this project. The sample application is part of a 6-part tutorial covering

###Part 1 : Setting up the Maps component with ActionBarSherlock

- Setup a skeleton project
- Add the ActionBarSherlock and GooglePlayServices libs
- [More](./GoogleMapsV2WithActionBarSherlock/docs/part1.md)
	
###Part 2 : Drawing on the Map - Markers and polylines

- Adding markers
- Highlighting markers
- Removing markers
- [More](./GoogleMapsV2WithActionBarSherlock/docs/part2.md)

###Part 3 : Animating the Map

- Animate through a set of markers on the map
- [More](./GoogleMapsV2WithActionBarSherlock/docs/part3.md)
	
###Part 4 : Migrating from v1 maps to v2 maps

- Some tips and tricks for migrating your old v1 apps to v2.
- A rundown of all the changes.
- [More](./GoogleMapsV2WithActionBarSherlock/docs/part4.md)

###Part 5 : Using Google APIs : Directions and Places API

- Use Google Places Autocomplete API
- Use Google Directions API
- Putting everything together.
- [More](./GoogleMapsV2WithActionBarSherlock/docs/part6.md)
	

**Important note:**


###API keys
In order to run this application on your phone you'll first need to get an API key.

Instructions on how to setup your API key can be found in the [Setting up an Android Maps V2 Project][0] article.

You'll need to paste the API key into your application manifest on the following line:

```xml
<meta-data android:name="com.google.android.maps.v2.API_KEY" android:value="INSERT_YOUR_API_KEY_HERE"/>
```

###Library issues
If you attempt to run an Android sample app with your own copy of ActionBarSherlock, you might run into the following issue:

	Found 2 versions of android-support-v4.jar in the dependency list,
	but not all the versions are identical (check is based on SHA-1 only at this time).
	All versions of the libraries must be the same at this time.
	Versions found are:
	Path: C:\PROJECTS\Android\GoogleMapsV2WithActionBarSherlock\libs\android-support-v4.jar
		Length: 484258
		SHA-1: bd6479f5dd592790607e0504e66e0f31c2b4d308
	Path: C:\PROJECTS\Android\ActionBarSherlock-4.3.1-0\actionbarsherlock\libs\android-support-v4.jar
		Length: 271754
		SHA-1: 53307dc2bd2b69fd5533458ee11885f55807de4b
	Jar mismatch! Fix your dependencies

The reason being that 2 different Android Support Library v4 JAR files are present in the projects getting built. (one provided by ActionBarSherlock and one provided by your project).
The solution is simple : Ensure that ActionBarSherlock is using the same version of the Android Support Library v4 JAR as the one your project is using.

In the enclosed ActionBarSherlock in this github repository the support JAR has already been replace. : https://github.com/ddewaele/GoogleMapsV2WithActionBarSherlock/tree/master/actionbarsherlock/libs
