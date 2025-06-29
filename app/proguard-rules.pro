# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# General rules
# Keep all parcelable implementations
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep res line number for debugging release crash
-keepattributes SourceFile, LineNumberTable
-keepattributes Signature

# Suppress warnings for Java time classes
-dontwarn java.time.**
-dontwarn java.util.**

# Suppress warnings for specific classes
-dontwarn android.util.ArrayMap
-dontwarn java.nio.file.Path

# Keep semua model data lokal kamu
-keep class com.example.sasindai.model.** { *; }

# Keep all activities, fragments, services (best practice if using custom ProGuard config)
-keep class * extends android.app.Activity
-keep class * extends android.app.Service
-keep class * extends android.app.Fragment
-keep class * extends androidx.fragment.app.Fragment

# Glide dep
-keep class com.bumptech.glide.** { *; }
-keep interface com.bumptech.glide.** { *; }
-dontwarn com.bumptech.glide.**

# Retrofit & Okhttp
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

# Gson
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# FirebaseMessagingService
-keep class * extends com.google.firebase.messaging.FirebaseMessagingService { *; }

# Midtrans
-keep class com.midtrans.sdk.** { *; }
-dontwarn com.midtrans.sdk.**

# Scenform + ARCore
-keep class com.google.ar.** { *; }
-dontwarn com.google.ar.**
-keep class com.gorisse.thomas.sceneform.** { *; }
-dontwarn com.gorisse.thomas.sceneform.**
-keep class com.google.ar.sceneform.** { *; }
-dontwarn com.google.ar.sceneform.**

# Lottie animt
-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**

# Facebook shimmer
-keep class com.facebook.shimmer.** { *; }
-dontwarn com.facebook.shimmer.**

# Chip navbar
-keep class com.ismaeldivita.chipnavigation.** { *; }
-dontwarn com.ismaeldivita.chipnavigation.**

# Google Play serv
-keep class com.google.android.gms.auth.** { *; }
-dontwarn com.google.android.gms.auth.**