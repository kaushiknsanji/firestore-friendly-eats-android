# Friendly Eats - Cloud Firestore Android Codelab

**Friendly Eats** app built by following the instructions detailed in the Google Codelab **["Cloud Firestore Android Codelab"][Firestore_Friendly_Eats_Codelab]**. Original _starter_ code by Google for this codelab can be referred [here][Firestore_Friendly_Eats_Repository]. One may refer the completed _sample_ code by Google over [here](https://github.com/firebase/quickstart-android/tree/master/firestore) which has solution written in both Java and Kotlin using Jetpack Navigation, that which is not used in this codelab.

<img src="docs/home.png" width="300"/>

## What one will learn

* Use Firebase CLI and Firebase Emulator Suite to develop and test Android App locally.
* Read and write data to Firestore from an Android app, by making use of- 
	* Collections
	* Documents
	* Subcollections
	* Transactions
* Listen to realtime changes in Firestore database. 
* Sorting and filtering with complex Firestore queries.
* Use Firebase Authentication and security rules to secure Firestore data.
* Index complex Firestore queries for performance.

## Getting Started

* Android Studio 4.0 or higher with updated SDK and Gradle.
* Android device or emulator with Android 4.1+.
* Java version 8 or higher.
* Node.js version 10 or higher.

### Branches in this Repository

* **[starter-code](https://github.com/kaushiknsanji/firestore-friendly-eats-android/tree/starter-code)**
	* This is the Starter code for the [codelab][Firestore_Friendly_Eats_Codelab].
	* In comparison to the original [repository][Firestore_Friendly_Eats_Repository], this repository contains some modifications and corrections-
		* Moved all dependency versions to project gradle for centralized management and updated the same to their latest versions.
		* Applied Lint corrections in codes and layouts related to chosen sdk versions.
		* Applied compatible changes to deprecated code.
		* Used Android ViewBinding.
		* Targeted Java version 8 and used lambda expressions wherever possible.
		* Configured Glide to be used via [AppGlideModule](https://github.com/kaushiknsanji/firestore-friendly-eats-android/blob/starter-code/app/src/main/java/com/google/firebase/example/fireeats/util/MyAppGlideModule.java).
		* Used constants for Firestore Document data field names and Collection names, and other attributes used in a Firestore Query - [commit](https://github.com/kaushiknsanji/firestore-friendly-eats-android/commit/195c38e25e010250b627bb70a00334fe693d687a).
		* Added code comments for better understanding of pre-written code - [commit](https://github.com/kaushiknsanji/firestore-friendly-eats-android/commit/a7f931771c099c81a49f6fce57f35c1415d4c077).
		* Removed a line that says _"ite"_ in `strings.xml` file - [commit](https://github.com/kaushiknsanji/firestore-friendly-eats-android/commit/c820a53d9b0fee67e29ab4773d2af266e03a6af2).
		* Made eligible class members `final` - [commit](https://github.com/kaushiknsanji/firestore-friendly-eats-android/commit/6d888d0d7df6969f552772ed71f93e56b1695adf).
		* Fixed the issue ["Order by clause cannot contain a field with an equality filter price"](https://github.com/firebase/friendlyeats-android/issues/118) - [commit](https://github.com/kaushiknsanji/firestore-friendly-eats-android/commit/3c40fb9fbdc5d152af2c73e97ef1b50c04d4b08f).
* **[master](https://github.com/kaushiknsanji/firestore-friendly-eats-android/tree/master)**
	* This contains the Solution for the [codelab][Firestore_Friendly_Eats_Codelab].
	* Includes automated app signing with keystore in order to test the _release_ version of the App with Production Firestore database.

<!-- Reference Style Links are to be placed after this -->
[Firestore_Friendly_Eats_Codelab]: https://firebase.google.com/codelabs/firestore-android
[Firestore_Friendly_Eats_Repository]: https://github.com/firebase/friendlyeats-android

