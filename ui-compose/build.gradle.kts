import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

plugins {
    alias(libs.plugins.kotlin.compose)
}

group = "org.secuso.pfa-core"
android {
    namespace = "org.secuso.ui.compose"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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

    implementation(libs.ax.work.runtime)
    implementation(libs.ax.work.runtime.ktx)
    androidTestImplementation(libs.ax.work.testing)

    // compose
    implementation(platform(libs.ax.compose.bom))

    implementation(project(":model"))

    val composeVersion = "2024.04.01"
    implementation(platform("androidx.compose:compose-bom:$composeVersion"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.3")
    implementation("androidx.compose.ui:ui-tooling:1.7.3")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.foundation:foundation:1.7.3")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    androidTestImplementation(platform("androidx.compose:compose-bom:$composeVersion"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

}