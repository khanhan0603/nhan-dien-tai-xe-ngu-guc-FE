plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "vn.edu.stu.projectnhandientaixenguguc"
    compileSdk = 36

    defaultConfig {
        applicationId = "vn.edu.stu.projectnhandientaixenguguc"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //RxJava
    implementation("io.reactivex.rxjava2:rxjava:2.1.16")
    implementation("io.reactivex.rxjava2:rxandroid:2.0.1")
    implementation("com.jakewharton.retrofit:retrofit2-rxjava2-adapter:1.0.0")
    //CameraX
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")
    //ML Kit
    implementation("com.google.mlkit:face-detection:16.1.5")
    //TensorFlow
    implementation("org.tensorflow:tensorflow-lite:2.14.0")
    //LifeCycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}