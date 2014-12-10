wearlocation
============

android wear detect location

note: 
- when doing location, don't forget androidmanifest.xml and build.gradle changes.
- need to use google play services api, remember to connect()
- i was not able to compile wear with 

      compile 'com.google.android.gms:play-services-wearable:6.5.87'

, so i did 

      compile 'com.google.android.gms:play-services:6.5.87'
