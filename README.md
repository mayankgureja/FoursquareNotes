FoursquareNotes
===============

Android app that allows Foursquare users to add notes to their check-ins

How To Run
----------

The FoursquareNotes.apk is located inside the /bin folder. Simply sideload it onto your Android phone or emulator and you're good to go.

Coding Environment
------------------

Written, built and tested on [Eclipse-ADT (Android Development Tools)](http://developer.android.com/tools/sdk/eclipse-adt.html)

Description
-----------

FoursquareNotes is an Android 4.2 To-Do List app that uses the [Cloudmine](http://cloudmine.me) service as a cloud storage solution. It allows users to create notes about their Foursquare check-ins. The user can view their check-ins and add/edit/delete notes (price, rating, pictures) about them.

Foursquare authentication is done using a WebView and allows the user to enter their credentials securely.

Since the time taken to GET information from a web resource can vary wildly, a ProgressDialog is displayed to give the user some feedback as to what is happening. Instead of waiting on a blank page for random periods of time, the user is shown a "Loading..." message until the application is ready.

Known Issues
------------

This is very much a beta product and there are various issues with this release.

The UI leaves much to be desired. The app was cobbled together in a very short amount of time, so the look and feel is neither constant nor appealing. However, the functionality is mostly sound.

Screenshots
-----------

Screenshots of the application in action are available in the /screenshots folder.

Dependencies/Requirements
-------------------------

[CloudMine Android Library](https://cloudmine.me/docs/java)
* cloudmine-android-v0.5.jar

Everything else is part of the standard libraries.

Collaborators
-------------

Built in collaboration with [Niteesh Prasad](https://github.com/dawnoflife)