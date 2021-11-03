# ESRC Face for Android

[![Platform](https://img.shields.io/badge/platform-android-orange.svg)](https://github.com/esrc-official/ESRC-Face-Android)
[![Languages](https://img.shields.io/badge/language-java-orange.svg)](https://github.com/esrc-official/ESRC-Face-Android)
[![Commercial License](https://img.shields.io/badge/license-Commercial-brightgreen.svg)](https://github.com/esrc-Face-official/ESRC-Android/blob/master/LICENSE.md)

<br />

## Introduction

[ESRC](http://esrc.co.kr) provides the vision API and SDK for your mobile app, enabling real-time analyzing facial action unit and recognizing facial expression using a camera.

<br />

## Installation

To use our Android samples, you should first install [ESRC Face SDK for Android](https://github.com/esrc-official/ESRC-Face-SDK-Android) 2.4.0 or higher on your system and should be received License Key by requesting by our email: **esrc@esrc.co.kr** <br /> 

<br />

## Before getting started

### Requirements

The minimum requirements for ESRC Face SDK for Android are:

- Android 4.2.0 (API level 17) or later
- Java 8 or later
- Gradle 4.0.0 or later

```groovy
// build.gradle(app)
android {
    defaultConfig {
        minSdkVersion 17
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

<br />

## Getting started

if you would like to try the sample app specifically fit to your usage, you can do so by following the steps below.

### Step 1: Initialize the ESRC Face SDK

Initialization binds the ESRC Face SDK to Android’s context, thereby allowing it to use a camera in your mobile. To the `init()` method, pass the **App ID** of your ESRC application to initialize the ESRC Face SDK and the **ESRCLicenseHandler** to received callback for validation of the App ID.

```java
ESRC.init(APP_ID, getApplicationContext(), new ESRCLicense.ESRCLicenseHandler() {
    @Override
    public void onValidatedLicense() {
        …
    }
    
    @Override
    public void onInvalidatedLicense() {
        …
    }
});
```

> Note: The `ESRC.init()` method must be called once across your Android app. It is recommended to initialize the ESRC Face SDK in the `onCreate()` method of the Application instance.

### (Optional) Step 2: Bind the ESRC Fragment

If you don't want to develop a layout that uses the camera, you can ues the ESRC Fragment provided from the ESRC Face SDK. Include the **container** to bind the ESRC Fragment in your layout `.xml` file. Please skip the Step 4: Feed the ESRC Face SDK. The ESRC Fragment will feed the image to our SDK itself.

```xml
<FrameLayout
    android:id=”@+id/container”
    android:layout_width=”match_parent”
    android:layout_height=”match_parent” />
```

> Note: FrameLayout is just one of examples. You can change to other layout type to purpose your app.

Bind the ESRC Fragment to display the image taken with the camera on the screen. ESRC Fragment send the image to the ESRC Face SDK in real-time to be able to analyze facial action unit and to recognize facial expression. ESRC Fragment automatically display the image to fit the size of your custom layout.

```java
// Bind LAYOUT.xml on your Activity.
setContentView(R.layout.LAYOUT);

// Replace the container in LAYOUT as the ESRC Fragment .
getSupportFragmentManager().beginTransaction()
    .replace(R.id.container, ESRCFragment.newInstance())
    .commit();
```

### Step 3: Start the ESRC Face SDK

Start the ESRC Face SDK to recognize your facial expression. To the `start()` method, pass the `ESRCType.Property` to select analysis modules and the `ESRC.ESRCHandler` to handle the results. You should implement the callback method of `ESRC.ESRCHandler` interface. So, you can receive the results of face, facial landmark, facial action unit, and facial expression. Please refer to **[sample app](https://github.com/esrc-official/ESRC-Face-Android)**.

```java
ESRC.start(
    new ESRCType.Property(
        true,  // Whether visualize result or not. It is only valid If you bind the ESRC Fragment (i.e., Step 2).
        true,  // Whether analyze measurement environment or not.
        true,  // Whether detect face or not.
        true,  // Whether detect facial landmark or not. If enableFace is false, it is also automatically set to false.
        true,  // Whether analyze facial action unit or not. If enableFace or enableFacialLandmark is false, it is also automatically set to false.
        true),  // Whether recognize facial expression or not. If enableFace is false, it is also automatically set to false.
    new ESRC.ESRCHandler() {
        @Override
        public void onDetectedFace(ESRCTYPE.Face face, ESRCException e) {
            if(e != null) {
                // Handle error.
            }
            
            // The face is detected.
            // Through the “face” parameter of the onDetectedFace() callback method,
            // you can get the location of the face from the result object
            // that ESRC Face SDK has passed to the onDetectedFace().
            …
        }
        
        // Please implement other callback method of ESRC.ESRCHandler interface.
        @Override public void onNotDetectedFace( … ) { … }
        @Override public void onAnalyzedMeasureEnv( … ) { … }
        @Override public void onDetectedFacialLandmark( … ) { … }
        @Override public void onAnalyzedFacialActionUnit( … ) { … }
        @Override public void onRecognizedFacialExpression( … ) { … }        
    });
```

### (Optional) Step 4: Feed the ESRC Face SDK

Feed `OpenCV Mat` on the ESRC Face SDK. To the `feed()` method, pass the `Mat` image received using a camera in real-time. You can skip this step if you follow Step 2: Bind the ESRC Fragment.

```java
ESRC.feed(Mat);
```

### Step 5: Stop the ESRC Face SDK

When your app is not use the camera or destroyed, stop the ESRC Face SDK.

```java
ESRC.stop();
```

## Android sample

You can **clone** the project from the [Sample repository](https://github.com/esrc-official/ESRC-Face-Android).

```
// Clone this repository
git clone git@github.com:esrc-official/ESRC-Face-Android.git

// Move to the sample
cd ESRC-Face-Android
```

<br />

## Reference

For further detail on ESRC Face SDK for Android, reter to [ESRC Face SDK for Android README](https://github.com/esrc-official/ESRC-Face-SDK-Android/blob/master/README.md).
