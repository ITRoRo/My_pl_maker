
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") version "1.9.10-1.0.13"

}

android {
    namespace = "com.example.myplmaker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myplmaker"
        minSdk = 29
        targetSdk = 34
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
}
dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.ui.text.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.google.material.v161)
    implementation (libs.glide)
    annotationProcessor (libs.compiler)
    implementation (libs.gson)
    implementation (libs.retrofit)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.material.v180)
    //noinspection GradleDependency
    implementation(libs.androidx.viewpager2)
    implementation(libs.koin.android.v340)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    //noinspection GradleDependency
    implementation(libs.androidx.fragment.ktx)
    // Зависимость на RxJava
    implementation(libs.rxjava)
// Зависимость на RxAndroid
    implementation(libs.rxandroid)
    // Coroutines
    implementation(libs.kotlinx.coroutines.android.v139)

    val room_version = "2.5.1"
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
}


