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
#-ignorewarnings
#-keep class * {
#    public private *;
#}


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

# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/chris/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascriptToInject.interface.for.webview {
#   public *;
#}

-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn retrofit2.**




## keep classes and class members that implement java.io.Serializable from being removed or renamed
## Fixes "Class class com.twinpeek.android.model.User does not have an id field" execption
-keep class * implements java.io.Serializable {
    *;
}

## Rules for Retrofit2
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
#-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
#-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions


## Rules for Gson
# For using GSON @Expose annotation
-keepattributes *Annotation*
# Gson specific classes
#-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer




# Keep classes used by Gson (if using GsonConverterFactory)
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**




# Rules for Javamail
-keep class javax.** {*;}
-dontwarn java.awt.**
#-dontwarn java.beans.Beans
-dontwarn javax.security.**

# Otto Library

# Remove logs, don't forget to use 'proguard-android-optimize.txt' file in build.gradle
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
    public static int wtf(...);
}



-keep class retrofit2.Retrofit { *; }
-keep class retrofit2.converter.** { *; }
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Keep service method annotations and parameters
-keepclassmembers class * {
    @retrofit2.http.* <methods>;
}


# Keep Kotlin coroutines and related classes
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
-keepclassmembers class kotlinx.coroutines.** {
    *;
}

# Keep any classes or interfaces that you use in your API calls
-keep class com.example.omoperation.model.** { *; }
-keep class java.lang.invoke.MethodHandles$Lookup { *; }
-dontwarn okhttp3.logging.**
-keep class okhttp3.logging.** { *; }
-keepattributes *Annotation*, Signature
# Retrofit and OkHttp
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**



# Keep Retrofit service interfaces and methods
-keep interface * {
    @retrofit2.http.* <methods>;
}

# Preserve Retrofit method annotations
-keepclassmembers class * {
    @retrofit2.http.* <methods>;
}

# Keep method parameters and annotations
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault

# Gson and GsonConverterFactory
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# Keep GsonConverterFactory (used with Retrofit)
-keep class retrofit2.converter.gson.GsonConverterFactory { *; }

# Logging Interceptor (if using OkHttp logging interceptor)
-keep class okhttp3.logging.** { *; }
-dontwarn okhttp3.logging.**



# ViewModel (Android Architecture Components)
-keepclassmembers,allowobfuscation class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep ViewModel and LiveData related classes
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

# Kotlin Coroutines
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Keep coroutine-related methods and classes
-keepclassmembers class kotlinx.coroutines.** {
    *;
}

# If you use reflection or dynamic method calls
-keepclassmembers class * {
    *;
}

# Prevent obfuscation of methods used by Retrofit
-keepattributes *Annotation*, Signature

# Model classes used in API responses/requests
-keep class com.example.omoperation.model.** { *; }
# Keep Room entities (database tables)
-keep class androidx.room.Entity { *; }
-keep @androidx.room.Entity class * { *; }

# Keep Room DAOs (Data Access Objects)
-keep @androidx.room.Dao class * { *; }
-keep interface androidx.room.Dao { *; }

# Keep Room Database
-keep @androidx.room.Database class * { *; }
-keep class androidx.room.RoomDatabase { *; }

# Preserve the names of fields in entities (this is important for Room to map the fields to database columns correctly)
-keepclassmembers class * {
    @androidx.room.ColumnInfo <fields>;
    @androidx.room.PrimaryKey <fields>;
    @androidx.room.Embedded <fields>;
    @androidx.room.Relation <fields>;
}

# If you are using Kotlin and Room
-keepclassmembers class * extends kotlin.coroutines.Continuation {
    public *;
}

# Preserve Kotlin data classes used as Room entities
-keepclassmembers class * {
    @androidx.room.Entity <fields>;
    @androidx.room.PrimaryKey <fields>;
}

-dontwarn javax.lang.model.element.Modifier

# If you are using Room with RxJava or LiveData
-dontwarn io.reactivex.**
-dontwarn androidx.lifecycle.LiveData



# Keep generated code for Hilt (including Dagger)
-keep class dagger.hilt.** { *; }
-keep class dagger.hilt.internal.** { *; }
-keep class hilt_aggregated_deps.** { *; }
-keep class **_HiltModules.** { *; }
-keep class **_MembersInjector { *; }
-keep class **_Factory { *; }
-keep class **_ComponentTreeDeps { *; }
-keep class **_HiltComponents { *; }
-keep class **_HiltComponentTree { *; }

# Keep Dagger-annotated classes
-keep class * {
    @dagger.** *;
}

# Keep classes that use assisted injection
-keep class dagger.hilt.android.internal.lifecycle.** { *; }

# Hilt-generated code with generic types
-keep class **_HiltModules_** { *; }
-keepclassmembers class dagger.hilt.android.internal.lifecycle.HiltViewModelFactory** { *; }
-keepclassmembers class * {
    dagger.hilt.android.scopes.ViewModelScoped <fields>;
}

# Keep classes generated by the Hilt Gradle plugin
-keep class **_HiltModules_BindsModule { *; }




