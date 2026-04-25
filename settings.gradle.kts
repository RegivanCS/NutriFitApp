cd C:\NutriFitApp\NutriFitApp
git commit -m "Initial commit - NutriFitApp"pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://chaquo.com/maven") }
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://chaquo.com/maven") }
    }
}

rootProject.name = "NutriFitApp"
include(":app")

