// build.gradle.kts (Project Level)
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}
configurations.all {
    resolutionStrategy {
        exclude("com.mapbox.common", "common")
    }
}
