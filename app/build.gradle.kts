plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.hilt)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.pepe.mycars"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pepe.mycars"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            pickFirsts += "META-INF/LICENSE.md"
            pickFirsts += "META-INF/LICENSE-notice.md"
            pickFirsts += "META-INF/COPYRIGHT"
        }
        jniLibs {
            useLegacyPackaging = false
        }
    }
}

dependencies {

    // Core AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Navigation
    implementation(libs.bundles.navigation)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.gson)

    // Firebase & Auth
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.firebase.ui.auth)
    implementation(libs.play.services.auth)

    // Reactive
    implementation(libs.rxjava)

    // Unit Testing
    testImplementation(libs.bundles.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.hilt.android.testing)

    // Debug & Instrumentation Testing
    debugImplementation(libs.mockito.junit.jupiter)
    debugImplementation(libs.mockito.inline)
    kaptTest(libs.hilt.compiler)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
