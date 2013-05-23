#Introduction

This is a skeleton project for using Google Maps v2 on Android with ActionBarSherlock 4.3.1


#Beware of duplicate jars....

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





