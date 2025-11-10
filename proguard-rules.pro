# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep all public classes and methods in the emotion package
-keep class com.synheart.emotion.** { *; }

# Keep model classes for reflection
-keepclassmembers class com.synheart.emotion.LinearSvmModel {
    *;
}

# Keep result classes
-keep class com.synheart.emotion.EmotionResult { *; }
-keep class com.synheart.emotion.EmotionConfig { *; }
-keep class com.synheart.emotion.EmotionError { *; }

# Keep companion objects
-keepclassmembers class com.synheart.emotion.** {
    public static ** Companion;
}

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Preserve line numbers for debugging
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

