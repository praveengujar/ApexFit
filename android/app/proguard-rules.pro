# Zyva ProGuard Rules

# --- Room ---
-keep class com.zyva.core.data.entity.** { *; }
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# --- kotlinx.serialization ---
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.zyva.**$$serializer { *; }
-keepclassmembers class com.zyva.** {
    *** Companion;
}
-keepclasseswithmembers class com.zyva.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# --- Health Connect ---
-keep class androidx.health.connect.client.** { *; }
-dontwarn androidx.health.connect.client.**

# --- ScoringConfig model classes ---
-keep class com.zyva.core.model.config.** { *; }

# --- Hilt ---
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# --- Compose ---
-dontwarn androidx.compose.**

# --- Coroutines ---
-dontwarn kotlinx.coroutines.**

# --- General ---
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
