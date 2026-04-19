# Keep Firebase/Firestore models (required for serialization)
-keepclassmembers class com.nedeme.data.model.** { *; }
-keep class com.nedeme.data.model.** { *; }

# Keep enums used by Firestore
-keepclassmembers enum com.nedeme.data.model.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Google Maps
-keep class com.google.android.gms.maps.** { *; }
-dontwarn com.google.android.gms.**
