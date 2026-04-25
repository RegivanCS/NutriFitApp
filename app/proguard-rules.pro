# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android SDK tools proguard files.

# Keep Chaquopy Python files
-keep class com.chaquo.python.** { *; }

# Keep Room entities
-keep class com.nutrifit.app.data.local.entities.** { *; }

# Keep serialization
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
