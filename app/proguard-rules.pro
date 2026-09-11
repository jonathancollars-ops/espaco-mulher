# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Preserve Room and SQLite database models to avoid runtime reflection overhead/warnings
-keep class androidx.room.** { *; }
-keep class androidx.sqlite.** { *; }
-keep class com.example.data.local.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase { *; }

# General optimization mitigations for memory logging
-dontwarn android.app.ActivityManager
-dontwarn android.os.MemoryFile

# Keep line numbers for debugging
-keepattributes SourceFile,LineNumberTable
