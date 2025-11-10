# Consumer ProGuard rules
# These rules are applied to consumers of this library when they enable ProGuard/R8

# Keep all public API classes and methods
-keep class com.synheart.emotion.** { *; }

# Keep public constructors
-keepclassmembers class com.synheart.emotion.** {
    public <init>(...);
}

# Keep companion objects and their methods
-keepclassmembers class com.synheart.emotion.** {
    public static ** Companion;
    public static ** Companion.*;
}

# Keep data class components
-keepclassmembers class com.synheart.emotion.** {
    public <methods>;
}

# Keep model classes for runtime use
-keep class com.synheart.emotion.LinearSvmModel { *; }
-keep class com.synheart.emotion.EmotionEngine { *; }
-keep class com.synheart.emotion.EmotionResult { *; }
-keep class com.synheart.emotion.EmotionConfig { *; }
-keep class com.synheart.emotion.EmotionError { *; }
-keep class com.synheart.emotion.FeatureExtractor { *; }

