plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.jetbrainshilt)
    alias(libs.plugins.kotlin.compose)
   // alias(libs.plugins.ksp)
    kotlin("kapt")
}

android {
    namespace = "com.example.omoperation"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.omoperation"
        minSdk = 24
        targetSdk = 35
        versionCode = 40
        versionName = "5.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
      //  vectorDrawables {
        //    useSupportLibrary = true
        //}
      //  vectorDrawables.useSupportLibrary = true


    }

    buildTypes {
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "2.0.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
}
kapt {
    correctErrorTypes = true
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.retrofit)
    implementation(libs.retrofitConverterGson1)
    implementation(libs.okhttpLoggingInterceptor)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.sdpAndroid)
    implementation(libs.sspAndroid)
    implementation(libs.coroutinescore)
    implementation(libs.coroutinesandroid)
    implementation(libs.lifecycleviewmodel)
    implementation(libs.lifecyclelivedata)
    implementation(libs.roomlibrary)
    implementation(libs.roomrun)
    implementation(libs.hilt)
    implementation(libs.glide)
    implementation(libs.zxing)
    implementation (libs.zxingandroidembedded)
    implementation(libs.multidex)

    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.play.services.location)
    kapt(libs.roomCompiler)
    kapt(libs.hiltcompiler)
    kapt(libs.glideCompiler)

    annotationProcessor(libs.glideCompiler)





    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}