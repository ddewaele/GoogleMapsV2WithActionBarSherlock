---
layout: index
title: Document Center
---

##Introduction

This repository contains a number of projects that will help you get up to speed with Google Maps v2 on Android with ActionBarSherlock 4.3.1

It contains the following folders

- [GoogleMapsV2SkeletonV11](https://github.com/ddewaele/GoogleMapsV2WithActionBarSherlock/tree/master/GoogleMapsV2SkeletonV11) (Skeleton project targetted at API level 11 and higher)
- [GoogleMapsV2SkeletonV8](https://github.com/ddewaele/GoogleMapsV2WithActionBarSherlock/tree/master/GoogleMapsV2SkeletonV8) (Skeleton project using Support Library targetted at API level 8 and higher)
- [GoogleMapsV2WithActionBarSherlock](https://github.com/ddewaele/GoogleMapsV2WithActionBarSherlock/tree/master/GoogleMapsV2WithActionBarSherlock) (the complete sample application)
- actionbarsherlock (The ActionBar we all love, put here for your convenience.)
- google-play-services_lib (The Google Play Services lib, put here for your convenience.)

##Tutorial

The tutorial docs can be found in the docs folder of this project. It contains 6 parts

This sample application is part of a 3-part tutorial covering

####[Part 1 : Setting up the Maps component with ActionBarSherlock](./part1)
- Setup a skeleton project
- Add the ActionBarSherlock and GooglePlayServices libs
	
####[Part 2 : Drawing on the Map : Markers and polylines.](./part2)
- Adding markers
- Highlighting markers
- Removing markers

####[Part 3 : Animating the Map : Markers and polylines.](./part3)
- Animate through a set of markers on the map
	
####[Part 4 : Migrating from v1 maps to v2 maps.](./part4)
- Some tips and tricks for migrating your old v1 apps to v2.
- A rundown of all the changes.

####[Part 5 : Using the android-maps-utils.](./part5)
- Using the Android Map Utils library (to be completed).
	
####[Part 6 : Using Google APIs : Directions and Places API.](./part6)
- Use Google Places Autocomplete API
- Use Google Directions API
- Putting everything together.
	
##Beware of duplicate jars.... (verify this)

When creating a new project, the wizard will see 2 support JARs (one provided by ActionBarSherlock and one provided by your project).

It will spit out the message

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

The solution is to simply remove the GoogleMapsV2WithActionBarSherlock\libs\android-support-v4.jar file to fix the error.

# Android Libraries

This project depends on 2 Android libraries that have been included in this repo for your convenience.

- Google Play Services (downloaded through the Android SDK Manager and extracted in sdk/extras/google/google_play_services)
- [ActionBarSherlock](ActionBarSherlock)




