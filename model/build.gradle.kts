group = "org.secuso.pfa-core"

plugins {
    alias(libs.plugins.android.lib)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "org.secuso.pfacore"
    compileSdk = 34

    defaultConfig {
        minSdk = 17

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {

    implementation(libs.ax.core.ktx)
    implementation(libs.ax.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ax.test.junit)
    androidTestImplementation(libs.espresso)
    implementation(libs.ax.pref)
    implementation(libs.multidex)

    implementation(libs.ax.room)
    annotationProcessor(libs.ax.room.compiler)
    implementation(libs.ax.room.ktx)

    implementation(libs.ax.work.runtime)
    implementation(libs.ax.work.runtime.ktx)
    androidTestImplementation(libs.ax.work.testing)

    implementation(project(":backup-api"))

}