# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# ========================
# Room Database (CRITICAL)
# ========================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Keep ALL DAO interfaces and methods
-keep interface com.flownote.data.local.dao.** { *; }
-keep class com.flownote.data.local.dao.** { *; }

# Keep data classes used with Room
-keep class com.flownote.data.local.entity.** { *; }
-keep class com.flownote.data.model.** { *; }
-keep class com.flownote.data.local.database.** { *; }

# ========================
# Gson (for Backup feature)
# ========================
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }

# Keep BackupRepository data classes
-keep class com.flownote.data.repository.BackupRepository$** { *; }
-keep class com.flownote.data.repository.DateTypeAdapter { *; }

# ========================
# Hilt / Dagger
# ========================
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }
-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**

# ========================
# WorkManager (for Auto-Pruning)
# ========================
-keep class * extends androidx.work.Worker
-keep class * extends androidx.work.CoroutineWorker  
-keep class com.flownote.workers.** { *; }
-keep class androidx.work.** { *; }

# ========================
# Kotlin Coroutines
# ========================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# ========================
# Jetpack Compose
# ========================
-dontwarn androidx.compose.runtime.**

# ========================
# Rich Text Editor Library
# ========================
-keep class com.mohamedrejeb.richeditor.** { *; }

# ========================
# Enum Classes (Category, NoteColor)
# ========================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    **[] $VALUES;
    public *;
}

# ========================
# Debug Stack Traces
# ========================
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ========================
# Remove Debug Logging Only
# Keep error and warning logs for production debugging
# ========================
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

